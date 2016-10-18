package es.usc.citius.servando.calendula.pharmacies.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsLayoutInflater;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.usc.citius.servando.calendula.CalendulaActivity;
import es.usc.citius.servando.calendula.R;
import es.usc.citius.servando.calendula.pharmacies.persistance.Pharmacy;
import es.usc.citius.servando.calendula.pharmacies.remote.PharmaciesService;
import es.usc.citius.servando.calendula.pharmacies.remote.RemoteServiceCreator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by isaac@isaaccastro.eu on 10/10/16.
 */
public class PharmaciesMapActivity extends CalendulaActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        Callback<List<Pharmacy>> {

    //Updates will never be more frequent than this value.
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    private static final int PERMISSION_ACCESS_FINE_LOCATION = 1;

    private boolean firstTime;

    private List<Pharmacy> pharmacies = null;
    private HashMap<Integer, Pharmacy> pharmaciesHashMap = null;
    private HashMap<String, Integer> markers = null;

    private PharmaciesService service;

    private SupportMapFragment mapFragment;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private GoogleMap map;

    private boolean mapLoaded = false;

    //UI Controls
    ImageButton btnMyPostion;
    ImageButton btnList;
    Button btnClear;

    IconicsDrawable iconMyLocation;
    IconicsDrawable iconList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacies_map);

        firstTime = true;

        iconMyLocation = new IconicsDrawable(this, GoogleMaterial.Icon.gmd_my_location)
                .sizeDp(24)
                .color(Color.parseColor("#304FFE"));
        iconList = new IconicsDrawable(this, GoogleMaterial.Icon.gmd_view_list)
                .sizeDp(24)
                .color(Color.GRAY);


        // UI events
        btnMyPostion = (ImageButton) findViewById(R.id.center_map_pharmacies);
        btnMyPostion.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                centerMap();
            }
        });
        btnMyPostion.setImageDrawable(iconMyLocation);

        btnList = (ImageButton) findViewById(R.id.pharmacies_list);
        btnList.setImageDrawable(iconList);

        btnClear = (Button) findViewById(R.id.clear_search_pharmacies);
        btnClear.setVisibility(View.GONE);


        // Check permissions and create GoogleApiClient.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_ACCESS_FINE_LOCATION);
        }
        buildGoogleApiClient();

        service = RemoteServiceCreator.createService(PharmaciesService.class, "http://test.isaaccastro.eu/api/");

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    /**
     * API call finished
     * @param call
     * @param response
     */
    @Override
    public void onResponse(Call<List<Pharmacy>> call, Response<List<Pharmacy>> response) {
        pharmacies = response.body();
        pharmaciesHashMap = new HashMap<>();
        for (Pharmacy pharmacy : pharmacies){
            pharmaciesHashMap.put(pharmacy.getCodPharmacy(), pharmacy);
        }
        Date d= new Date();
        Log.d("DEBUG", d.getTime()+" API di que envía "+pharmaciesHashMap.size());
        Toast.makeText(PharmaciesMapActivity.this, "There are "+pharmacies.size()+" pharmacies in the map", Toast.LENGTH_SHORT).show();
        updateUI(false);
    }

    @Override
    public void onFailure(Call<List<Pharmacy>> call, Throwable t) {
        Log.e(PharmaciesMapActivity.class.getSimpleName(), t.getLocalizedMessage());
        Toast.makeText(PharmaciesMapActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mapLoaded = true;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map = googleMap;

            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            googleMap.getUiSettings().setMapToolbarEnabled(false);

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                updateLocationDatafromAPI(getMapRadio(), getMapCenter());
                updateUI(false);
            }
        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(PharmaciesMapActivity.class.getSimpleName(), "Connected to Google Play Services!");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
                else{
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(PharmaciesMapActivity.class.getSimpleName(), "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(PharmaciesMapActivity.class.getSimpleName(), "Can't connect to Google Play Services!");
        Toast.makeText(this, "We can't connect to Google Play Services :(", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {

            mLastLocation = location;

            mLastLocation.setLatitude(location.getLatitude());
            mLastLocation.setLongitude(location.getLongitude());

        if (firstTime) {
            firstTime = false;
            centerMap();
        }
    }

    private Integer getMapRadio(){

        VisibleRegion vr = map.getProjection().getVisibleRegion();
        double left = vr.latLngBounds.southwest.longitude;

        Location center = new Location("center");
        center.setLatitude(vr.latLngBounds.getCenter().latitude);
        center.setLongitude(vr.latLngBounds.getCenter().longitude);

        Location middleLeftCornerLocation = new Location("center");
        middleLeftCornerLocation.setLatitude(center.getLatitude());
        middleLeftCornerLocation.setLongitude(left);

        Float dis = center.distanceTo(middleLeftCornerLocation);

        return dis.intValue();

    }

    private Location getMapCenter(){
        LatLng centerLatLng = null;
        Location center = null;

        centerLatLng = map.getCameraPosition().target;

        if (centerLatLng != null) {
            center = new Location("");
            center.setLatitude(centerLatLng.latitude);
            center.setLongitude(centerLatLng.longitude);
        }

        return center;
    }

    private void updateLocationDatafromAPI(Integer radio, Location location) {
        if (location != null && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Call<List<Pharmacy>> call = service.listByLocation(location.getLatitude(), location.getLongitude(), radio, "");
            call.enqueue(this);
            Date d = new Date();
            Log.d("DEBUG", d.getTime() + " Solicitadas farmacias");
        }
    }

    private void updateUI(boolean centerMap){
        if (mapLoaded) {

            if (mLastLocation != null && centerMap) {
                centerMap();
            }

            if (pharmaciesHashMap != null) {
                markers = new HashMap<>();
                map.clear();

                for (Map.Entry<Integer, Pharmacy> entry : pharmaciesHashMap.entrySet()){
                    Pharmacy pharmacy = entry.getValue();
                    LatLng pharmacyLocation = new LatLng(pharmacy.getGps()[1], pharmacy.getGps()[0]);
                    MarkerOptions pharmaMarker = new MarkerOptions();
                    pharmaMarker.position(pharmacyLocation);
                    Marker marker = map.addMarker(pharmaMarker);
                    markers.put(marker.getId(), pharmacy.getCodPharmacy());
                }
                Date d= new Date();
                Log.d("DEBUG", d.getTime()+" Mapa actualizado con farmacias= "+pharmaciesHashMap.size());
            }

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Pharmacy pharma = pharmaciesHashMap.get(markers.get(marker.getId()));
                    Toast.makeText(getBaseContext(), "Clicked marker which corresponds to pharmacy number "+pharma.getCodPharmacy()+", with name "+pharma.getName(), Toast.LENGTH_LONG).show();
                    return true;
                }
            });

        }

    }

    private void centerMap(){
        LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(16));
        iconMyLocation.color(Color.parseColor("#304FFE"));
    }

    /**
     * Builds a GoogleApiClient
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(PharmaciesMapActivity.class.getSimpleName(), "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    /**
     * Sets up the location request.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Requests location updates.
     */
    protected void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    /**
     * Removes location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }
}
