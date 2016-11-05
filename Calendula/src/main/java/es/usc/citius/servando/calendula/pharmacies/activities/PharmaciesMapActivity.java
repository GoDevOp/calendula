package es.usc.citius.servando.calendula.pharmacies.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.LayoutInflaterCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import es.usc.citius.servando.calendula.pharmacies.fragments.PharmacyMarkerDetailsFragment;
import es.usc.citius.servando.calendula.pharmacies.persistance.Pharmacy;
import es.usc.citius.servando.calendula.pharmacies.persistance.Query;
import es.usc.citius.servando.calendula.pharmacies.remote.PharmaciesService;
import es.usc.citius.servando.calendula.pharmacies.remote.RemoteServiceCreator;
import es.usc.citius.servando.calendula.pharmacies.util.PharmaciesFont;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by isaac@isaaccastro.eu on 10/10/16.
 */
public class PharmaciesMapActivity extends CalendulaActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    //Updates will never be more frequent than this value.
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    private static final int PERMISSION_ACCESS_FINE_LOCATION = 1;

    private boolean firstTime;

    private List<Pharmacy> pharmacies = null;
    private HashMap<Integer, Pharmacy> pharmaciesHashMap = null;
    private HashMap<Marker, Integer> markersHashMap = null;

    private Marker previousMarker;
    private Pharmacy previousPharmacy;

    private PharmaciesService service;

    private FragmentTransaction transaction;
    private PharmacyMarkerDetailsFragment fragmentMarker;

    private Bundle argsToFragment;

    private SupportMapFragment mapFragment;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private GoogleMap map;

    private ClusterManager<PharmacyMarker> mClusterManager;

    private boolean mapLoaded = false;

    //UI Controls
    ImageButton btnMyPostion;
    ImageButton btnList;
    Button btnClear;

    IconicsDrawable iconMyLocation;
    IconicsDrawable iconList;
    IconicsDrawable iconMarker;
    IconicsDrawable iconSelectedMarker;
    IconicsDrawable iconClosedMarker;
    IconicsDrawable iconClosedSelectedMarker;

    GetApiDataTask apiTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));

        // Check permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_ACCESS_FINE_LOCATION);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacies_map);

        buildGoogleApiClient();

        // Add marker info fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        fragmentMarker = new PharmacyMarkerDetailsFragment();
        transaction.add(R.id.fragment_contenedor, fragmentMarker);
        transaction.hide(fragmentMarker);
        transaction.commit();

        argsToFragment = new Bundle();

        firstTime = true;

        iconMyLocation = new IconicsDrawable(this, GoogleMaterial.Icon.gmd_my_location)
                .sizeDp(24)
                .color(Color.parseColor("#304FFE"));
        iconList = new IconicsDrawable(this, GoogleMaterial.Icon.gmd_view_list)
                .sizeDp(24)
                .color(Color.GRAY);
        iconMarker = new IconicsDrawable(this, PharmaciesFont.Icon.ic_marker)
                .sizeDp(34)
                .color(Color.parseColor("#82C77B"));
        iconSelectedMarker = new IconicsDrawable(this, PharmaciesFont.Icon.ic_marker)
                .sizeDp(34)
                .color(Color.parseColor("#169E58"));
        iconClosedMarker = new IconicsDrawable(this, PharmaciesFont.Icon.ic_marker)
                .sizeDp(34)
                .color(Color.parseColor("#BCBCBC"));
        iconClosedSelectedMarker = new IconicsDrawable(this, PharmaciesFont.Icon.ic_marker)
                .sizeDp(34)
                .color(Color.parseColor("#8C8C8C"));

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

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mapLoaded = true;

        map = googleMap;


        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);
        //map.getUiSettings().setCompassEnabled(false);
        try {
            assert mapFragment.getView() != null;
            final ViewGroup parent = (ViewGroup) mapFragment.getView().findViewWithTag("GoogleMapMyLocationButton").getParent();
            parent.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int i = 0, n = parent.getChildCount(); i < n; i++) {
                            View view = parent.getChildAt(i);
                            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) view.getLayoutParams();
                            // position on left bottom
                            rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP,0);
                            rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                            rlp.rightMargin = rlp.leftMargin;
                            rlp.bottomMargin = 50;
                            view.requestLayout();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (!firstTime) {
                    Location loc = getMapCenter();
                    Query query = new Query();
                    query.setLatitude(loc.getLatitude());
                    query.setLongitude(loc.getLongitude());
                    query.setRadio(getMapRadio());
                    if (apiTask != null && apiTask.getStatus() != AsyncTask.Status.FINISHED){
                        apiTask.cancel(true);
                    }
                    apiTask = new GetApiDataTask(query);
                    Date d = new Date();
                    Log.d("DEBUG", d.getTime() + " New task " + apiTask.toString());
                    apiTask.execute();
                }
            }
        });

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                hideFragment(fragmentMarker);

                // Change color last marker clicked
                if (previousMarker != null) {
                    if (!previousPharmacy.isOpen()){
                        previousMarker.setIcon(BitmapDescriptorFactory.fromBitmap(iconClosedMarker.toBitmap()));
                    }
                    else {
                        previousMarker.setIcon(BitmapDescriptorFactory.fromBitmap(iconMarker.toBitmap()));
                    }
                    //previousMarker.setIcon(BitmapDescriptorFactory.fromBitmap(iconMarker.toBitmap()));
                    previousMarker = null;
                }
            }
         });


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(PharmaciesMapActivity.class.getSimpleName(), "Connected to Google Play Services!");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
        double top = vr.latLngBounds.northeast.latitude;

        Location center = new Location("center");
        center.setLatitude(vr.latLngBounds.getCenter().latitude);
        center.setLongitude(vr.latLngBounds.getCenter().longitude);

        Location middleLeftCornerLocation = new Location("center");
        middleLeftCornerLocation.setLatitude(center.getLatitude());

        middleLeftCornerLocation.setLatitude(center.getLatitude());
        middleLeftCornerLocation.setLongitude(left);
        Float disToLeft = center.distanceTo(middleLeftCornerLocation);

        middleLeftCornerLocation.setLongitude(center.getLongitude());
        middleLeftCornerLocation.setLatitude(top);
        Float disToTop = center.distanceTo(middleLeftCornerLocation);

        if (disToLeft > disToTop){
            return disToLeft.intValue();
        }
        else{
            return disToTop.intValue();
        }

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

    public void showFragment(final Fragment fragment){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_up_from_bottom, R.anim.slide_out_from_bottom);
        ft.addToBackStack(null);
        ft.replace(R.id.fragment_contenedor, fragment);

        if (fragment.isHidden()) {
            ft.show(fragment);
        }

        ft.commit();
    }

    public void hideFragment(final Fragment fragment){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_contenedor, fragment);

        if (!fragment.isHidden()) {
            ft.hide(fragment);
        }

        ft.commit();
    }

    private void centerMap(){
        if (mLastLocation != null) {
            LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            map.animateCamera(CameraUpdateFactory.zoomTo(16));
            iconMyLocation.color(Color.parseColor("#304FFE"));
        }
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    /**
     * Removes location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    private void setMarkerColor(Marker marker, Pharmacy pharmacy){
        if (previousPharmacy != null && previousPharmacy.getCodPharmacy().intValue() == pharmacy.getCodPharmacy().intValue() &&
                fragmentMarker.isVisible()){
            if (!pharmacy.isOpen()){
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconClosedSelectedMarker.toBitmap()));
            }
            else {
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconSelectedMarker.toBitmap()));
            }
            previousMarker = marker;
        }
        else {
            if (!pharmacy.isOpen()){
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconClosedMarker.toBitmap()));
            }
            else {
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconMarker.toBitmap()));
            }
        }
    }

    private class GetApiDataTask extends AsyncTask<Void, Void, Void> implements Callback<List<Pharmacy>> {

        private Query query;
        private Call<List<Pharmacy>> call;

        private boolean finished = false;

        GetApiDataTask(Query query){
            this.query = query;
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (ActivityCompat.checkSelfPermission(PharmaciesMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PharmaciesMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                call = service.listByLocation(query.getLatitude(), query.getLongitude(), query.getRadio(), "");
                call.enqueue(this);
                Date d = new Date();
                Log.d("DEBUG", d.getTime() + " Pharmacies request");
            }
            while(!finished){
                 //wait for API
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            Date d= new Date();
            if (call != null) {
                Log.d("DEBUG", d.getTime()+" Cancelled call at task "+apiTask.toString());
                call.cancel();
            }
            super.onCancelled();
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            Date d= new Date();
            Log.d("DEBUG", d.getTime()+" Called onPostExecute on task "+apiTask.toString());
            super.onPostExecute(aVoid);
        }

        @Override
        public void onResponse(Call<List<Pharmacy>> call, Response<List<Pharmacy>> response) {
            pharmacies = response.body();
            pharmaciesHashMap = new HashMap<>();
            for (Pharmacy pharmacy : pharmacies){
                pharmaciesHashMap.put(pharmacy.getCodPharmacy(), pharmacy);
            }
            Date d= new Date();
            Log.d("DEBUG", d.getTime()+" API sends "+pharmaciesHashMap.size() + " pharmacies");
            updateUI(false);
            finished = true;
        }

        @Override
        public void onFailure(Call<List<Pharmacy>> call, Throwable t) {
            Log.e(PharmaciesMapActivity.class.getSimpleName(), t.getLocalizedMessage());
            Toast.makeText(PharmaciesMapActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }

        private void updateUI(boolean centerMap){
            if (mapLoaded) {

                if (mLastLocation != null && centerMap) {
                    centerMap();
                }

                if (pharmaciesHashMap != null) {
                    markersHashMap = new HashMap<>();
                    map.clear();

                    for (Map.Entry<Integer, Pharmacy> entry : pharmaciesHashMap.entrySet()){
                        Pharmacy pharmacy = entry.getValue();
                        LatLng pharmacyLocation = new LatLng(pharmacy.getGps()[1], pharmacy.getGps()[0]);
                        MarkerOptions pharmaMarker = new MarkerOptions();
                        pharmaMarker.position(pharmacyLocation);
                        Marker marker = map.addMarker(pharmaMarker);
                        if (previousPharmacy != null && previousPharmacy.getCodPharmacy().intValue() == pharmacy.getCodPharmacy().intValue() &&
                                fragmentMarker.isVisible()){
                            if (!pharmacy.isOpen()){
                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconClosedSelectedMarker.toBitmap()));
                            }
                            else {
                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconSelectedMarker.toBitmap()));
                            }
                            previousMarker = marker;
                        }
                        else {
                            if (!pharmacy.isOpen()){
                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconClosedMarker.toBitmap()));
                            }
                            else {
                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconMarker.toBitmap()));
                            }
                        }
                        markersHashMap.put(marker, pharmacy.getCodPharmacy());
                    }
                    if (!pharmaciesHashMap.containsValue(previousPharmacy)){
                        previousMarker = null;
                        previousPharmacy = null;
                    }

                    Date d= new Date();
                    Log.d("DEBUG", d.getTime()+" Updated map with "+pharmaciesHashMap.size() + " pharmacies");
                }

                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        Pharmacy pharma = pharmaciesHashMap.get(markersHashMap.get(marker));
                        // Change color marker
                        if(previousPharmacy != null && previousMarker != null && fragmentMarker.isVisible()){
                            if (!previousPharmacy.isOpen()){
                                previousMarker.setIcon(BitmapDescriptorFactory.fromBitmap(iconClosedMarker.toBitmap()));
                            }
                            else {
                                previousMarker.setIcon(BitmapDescriptorFactory.fromBitmap(iconMarker.toBitmap()));
                            }
                            //previousMarker.setIcon(BitmapDescriptorFactory.fromBitmap(iconMarker.toBitmap()));
                        }
                        if (!pharma.isOpen()){
                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconClosedSelectedMarker.toBitmap()));
                        }
                        else {
                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconSelectedMarker.toBitmap()));
                        }
                        //marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconSelectedMarker.toBitmap()));
                        previousMarker=marker;
                        previousPharmacy = pharma;

                        argsToFragment.putParcelable("pharmacy", pharma);
                        fragmentMarker.getData(pharma);
                        showFragment(fragmentMarker);
                        fragmentMarker.updateData();
                        return true;
                    }
                });

            }

        }
    }

    public class PharmacyMarker implements ClusterItem {
        private final LatLng mPosition;

        public PharmacyMarker(double lat, double lng) {
            mPosition = new LatLng(lat, lng);
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }
    }
}
