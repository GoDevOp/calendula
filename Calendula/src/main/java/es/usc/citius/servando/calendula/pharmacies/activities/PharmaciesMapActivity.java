package es.usc.citius.servando.calendula.pharmacies.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.LayoutInflaterCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.usc.citius.servando.calendula.CalendulaActivity;
import es.usc.citius.servando.calendula.R;
import es.usc.citius.servando.calendula.pharmacies.adapters.PharmacyListItem;
import es.usc.citius.servando.calendula.pharmacies.fragments.PharmacyFragment;
import es.usc.citius.servando.calendula.pharmacies.fragments.PharmacyListFragment;
import es.usc.citius.servando.calendula.pharmacies.fragments.PharmacyMarkerDetailsFragment;
import es.usc.citius.servando.calendula.pharmacies.persistance.Pharmacy;
import es.usc.citius.servando.calendula.pharmacies.persistance.Query;
import es.usc.citius.servando.calendula.pharmacies.remote.PharmaciesService;
import es.usc.citius.servando.calendula.pharmacies.remote.RemoteServiceCreator;
import es.usc.citius.servando.calendula.pharmacies.util.MatrixDirectionsAPI;
import es.usc.citius.servando.calendula.pharmacies.util.PharmaciesFont;
import es.usc.citius.servando.calendula.pharmacies.util.TimeTravel;
import es.usc.citius.servando.calendula.pharmacies.util.TravelTypes;
import es.usc.citius.servando.calendula.pharmacies.util.Utils;
import es.usc.citius.servando.calendula.util.ScreenUtils;
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
        ClusterManager.OnClusterItemClickListener<PharmaciesMapActivity.PharmacyMarker> {


    //Updates will never be more frequent than this value.
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    public static final int GET_LOCATION_PHARMACIES = 1;
    public static final int GET_NEAREST_PHARMACIES = 2;
    public static final int GET_TEXT_QUERY_PHARMACIES = 3;

    private static final int PERMISSION_ACCESS_FINE_LOCATION = 1;

    private boolean firstTime = true;
    private boolean showNearest = true;

    private List<Pharmacy> pharmacies = null;
    private HashMap<Integer, Pharmacy> pharmaciesHashMap = null;

    private Marker previousMarker;
    private Pharmacy previousPharmacy;
    private PharmacyMarker previousPharmacyMarker;

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
    private MarkerRenderer mMarkerRenderer;

    private boolean mapLoaded = false;

    //UI Controls
    FloatingActionButton btnMyPostion;
    FloatingActionButton btnList;
    FloatingActionButton btnDirections;
    Button btnClear;
    EditText searchTxt;
    ImageView searchImg;
    ProgressBar progressBarMap;

    IconicsDrawable iconMyLocation;
    IconicsDrawable iconList;
    IconicsDrawable iconMarker;
    IconicsDrawable iconSelectedMarker;
    IconicsDrawable iconClosedMarker;
    IconicsDrawable iconClosedSelectedMarker;
    IconicsDrawable iconDirections;
    IconicsDrawable iconSearch;

    private SlidingUpPanelLayout slidingLayout;
    RelativeLayout fragmentContainer;
    RelativeLayout listLayout;

    PharmacyFragment fragmentPharmacyFull;

    PharmacyListFragment pharmacyListFragment;

    Integer slidingLayoutHeight;

    GetApiDataTask apiTask = null;
    GetApiDataTask apiTaskNearest = null;
    GetApiDataTask apiTaskSearch = null;

    HashMap<String, Pharmacy> markerMap;

    List<PharmacyListItem> pharmaciesListItems;

    HashMap<Integer, String> travelTimesCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));

        ScreenUtils.setStatusBarColor(PharmaciesMapActivity.this, Color.argb(50, 61, 63, 64));

        // Check permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_ACCESS_FINE_LOCATION);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacies_map);

        buildGoogleApiClient();

        markerMap = new HashMap<>();

        // Add marker info fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        fragmentMarker = new PharmacyMarkerDetailsFragment();
        transaction.add(R.id.fragment_contenedor, fragmentMarker);
        transaction.hide(fragmentMarker);
        transaction.commit();

        argsToFragment = new Bundle();

        firstTime = true;

        listLayout = (RelativeLayout) findViewById(R.id.pharmacies_list);
        pharmacyListFragment = new PharmacyListFragment();

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
        iconDirections = new IconicsDrawable(this, GoogleMaterial.Icon.gmd_directions)
                .sizeDp(24)
                .color(Color.WHITE);
        iconSearch = new IconicsDrawable(this, GoogleMaterial.Icon.gmd_search)
                .sizeDp(24)
                .color(Color.GRAY);

        btnDirections = (FloatingActionButton) findViewById(R.id.get_pharmacy_route);
        btnDirections.setImageDrawable(iconDirections);
        btnDirections.setVisibility(View.INVISIBLE);
        btnDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (previousPharmacy != null) {
                    String destination = previousPharmacy.getGps()[1] + "," + previousPharmacy.getGps()[0];
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + destination);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            }
        });

        // UI events
        btnMyPostion = (FloatingActionButton) findViewById(R.id.center_map_pharmacies);
        btnMyPostion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                centerMap();
            }
        });
        btnMyPostion.setImageDrawable(iconMyLocation);

        btnList = (FloatingActionButton) findViewById(R.id.pharmacies_list_button);
        btnList.setImageDrawable(iconList);
        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listLayout = (RelativeLayout) findViewById(R.id.pharmacies_list);
                listLayout.setVisibility(View.VISIBLE);

                pharmacyListFragment.setData(pharmaciesListItems);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                ft.replace(R.id.pharmacies_list, pharmacyListFragment);
                ft.addToBackStack(null);
                ft.commit();
                pharmacyListFragment.updateData();
            }
        });

        searchImg = (ImageView) findViewById(R.id.search_pharmacies_image);
        searchImg.setImageDrawable(iconSearch);

        progressBarMap = (ProgressBar) findViewById(R.id.search_pharmacies_loading);

        searchTxt = (EditText) findViewById(R.id.search_pharmacies_text);
        searchTxt.setCursorVisible(false);
        searchTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (v.getText().toString().isEmpty()){
                        Toast.makeText(getBaseContext(), getString(R.string.pharmacy_search_without_text), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    hideKeyboard();
                    searchTxt.setCursorVisible(false);
                    if (apiTaskNearest != null && apiTaskNearest.getStatus() != AsyncTask.Status.FINISHED){
                        apiTaskNearest.cancel(true);
                    }
                    if (apiTask != null && apiTask.getStatus() != AsyncTask.Status.FINISHED){
                        apiTask.cancel(true);
                    }
                    Query searchQuery = new Query();
                    searchQuery.setQueryType(GET_TEXT_QUERY_PHARMACIES);
                    searchQuery.setSearch(v.getText().toString());
                    apiTaskSearch = new GetApiDataTask(searchQuery);
                    apiTaskSearch.execute();

                    return true;
                }
                return false;
            }
        });
        searchTxt.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0){
                    btnClear.setVisibility(View.VISIBLE);
                }
                else{
                    btnClear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        searchTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchTxt.setCursorVisible(true);
            }
        });

        btnClear = (Button) findViewById(R.id.clear_search_pharmacies);
        btnClear.setVisibility(View.GONE);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiTaskSearch = null;
                centerMap();
                searchTxt.setText("");
                throwLocationQuery();
                btnClear.setVisibility(View.GONE);
            }
        });

        fragmentPharmacyFull = new PharmacyFragment();

        fragmentContainer = (RelativeLayout) findViewById(R.id.fragment_contenedor);

        slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingLayoutHeight = slidingLayout.getPanelHeight();
        slidingLayout.setPanelHeight(0);
        slidingLayout.setOverlayed(true);
        slidingLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            Date d = new Date();

            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if (slideOffset > 0.01) {
                    fragmentPharmacyFull.getData(fragmentMarker.getPharmacy());
                    btnDirections.hide();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    ft.replace(R.id.fragment_contenedor, fragmentPharmacyFull);
                    fragmentContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    ft.commit();
                }
                else{
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    ft.replace(R.id.fragment_contenedor, fragmentMarker);
                    fragmentContainer.getLayoutParams().height = slidingLayoutHeight;
                    ft.commit();
                    fragmentMarker.updateData(false);
                    if (fragmentMarker.isVisible()) {
                        btnDirections.show();
                    }
                }
            }

            @Override
            public void onPanelCollapsed(View panel) {
                ScreenUtils.setStatusBarColor(PharmaciesMapActivity.this, Color.argb(50, 61, 63, 64));
            }

            @Override
            public void onPanelExpanded(View panel) {
            }

            @Override
            public void onPanelAnchored(View panel) {
            }

            @Override
            public void onPanelHidden(View panel) {
            }
        });

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
        try {
            assert mapFragment.getView() != null;
            final ViewGroup parent = (ViewGroup) mapFragment.getView().findViewWithTag("GoogleMapMyLocationButton").getParent();
            parent.post(new Runnable() {
                @Override
                public void run() {
                    // Place compass at bottom left
                    try {
                        for (int i = 0, n = parent.getChildCount(); i < n; i++) {
                            View view = parent.getChildAt(i);
                            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) view.getLayoutParams();
                            // position on left bottom
                            rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
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

        mClusterManager = new ClusterManager<>(this, map);
        mMarkerRenderer = new MarkerRenderer();
        mClusterManager.setRenderer(mMarkerRenderer);

        map.setOnMarkerClickListener(mClusterManager);
        map.setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.setOnClusterItemClickListener(this);

        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                mClusterManager.onCameraIdle();
                if (!firstTime && searchTxt.getText().length() == 0) {
                    throwLocationQuery();
                }
            }
        });

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                hideKeyboard();
                hideFragment(fragmentMarker);

                // Change color last marker clicked
                if (previousMarker != null && previousPharmacy != null && pharmaciesHashMap.containsValue(previousPharmacy)) {
                    if (!previousPharmacy.isOpen()) {
                        previousMarker.setIcon(BitmapDescriptorFactory.fromBitmap(iconClosedMarker.toBitmap()));
                    } else {
                        previousMarker.setIcon(BitmapDescriptorFactory.fromBitmap(iconMarker.toBitmap()));
                    }
                    previousMarker = null;
                    previousPharmacy = null;
                    previousPharmacyMarker = null;
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
                } else {
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

    @Override
    public boolean onClusterItemClick(PharmacyMarker pharmacyMarker) {
        hideKeyboard();
        Pharmacy pharma = pharmacyMarker.getPharmacy();
        // Change color marker
        if (previousPharmacy != null && previousMarker != null && markerMap.containsKey(previousMarker.getId()) &&
                fragmentMarker.isVisible() && pharmaciesHashMap.containsKey(previousPharmacy.getCodPharmacy())) {
            if (!previousPharmacy.isOpen()) {
                previousMarker.setIcon(BitmapDescriptorFactory.fromBitmap(iconClosedMarker.toBitmap()));
            } else {
                previousMarker.setIcon(BitmapDescriptorFactory.fromBitmap(iconMarker.toBitmap()));
            }
        }
        Marker marker = mMarkerRenderer.getMarker(pharmacyMarker);
        if (!pharma.isOpen()) {
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconClosedSelectedMarker.toBitmap()));
        } else {
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconSelectedMarker.toBitmap()));
        }
        previousMarker = marker;
        previousPharmacy = pharma;
        previousPharmacyMarker = pharmacyMarker;

        argsToFragment.putParcelable("pharmacy", pharma);
        argsToFragment.putParcelable("lastLocation", mLastLocation);
        fragmentMarker.setData(pharma, mLastLocation);
        showFragment(fragmentMarker);
        slidingLayout.setVisibility(View.VISIBLE);
        fragmentMarker.updateData(true);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (fragmentMarker.isVisible()) {
            if (previousMarker != null) {
                if (!previousPharmacy.isOpen()) {
                    previousMarker.setIcon(BitmapDescriptorFactory.fromBitmap(iconClosedMarker.toBitmap()));
                } else {
                    previousMarker.setIcon(BitmapDescriptorFactory.fromBitmap(iconMarker.toBitmap()));
                }
            }
            previousMarker = null;
            previousPharmacy = null;
            previousPharmacyMarker = null;
            hideFragment(fragmentMarker);
        }
        else if (slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED){
            slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        else{
            finish();
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

        if (disToLeft > disToTop) {
            return disToLeft.intValue();
        } else {
            return disToTop.intValue();
        }

    }

    private Location getMapCenter() {
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

    public void showFragment(final Fragment fragment) {

        btnDirections.show();
        btnList.hide();
        btnMyPostion.hide();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.replace(R.id.fragment_contenedor, fragment);
        slidingLayout.setPanelHeight(slidingLayoutHeight);
        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

        if (fragment.isHidden()) {
            ft.show(fragment);
        }

        ft.commit();
    }

    public void hideFragment(final Fragment fragment) {
        btnDirections.hide();
        btnList.show();
        btnMyPostion.show();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_contenedor, fragment);

        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        if (!fragment.isHidden()) {
            ft.hide(fragment);
        }

        ft.commit();

    }

    private void centerMap() {
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

    private void hideKeyboard(){
        View view = PharmaciesMapActivity.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void throwLocationQuery(){
        Location loc = getMapCenter();
        Query query = new Query();
        query.setLatitude(loc.getLatitude());
        query.setLongitude(loc.getLongitude());
        query.setRadio(getMapRadio());
        query.setQueryType(GET_LOCATION_PHARMACIES);
        if (apiTask != null && apiTask.getStatus() != AsyncTask.Status.FINISHED) {
            apiTask.cancel(true);
        }
        if (apiTaskNearest != null && apiTaskNearest.getStatus() != AsyncTask.Status.FINISHED) {
            apiTaskNearest.cancel(true);
        }
        if (apiTaskSearch != null && apiTaskSearch.getStatus() != AsyncTask.Status.FINISHED) {
            apiTaskSearch.cancel(true);
        }
        apiTask = new GetApiDataTask(query);
        Date d = new Date();
        Log.d("DEBUG", Utils.getDate(d) + " New task " + apiTask.toString());
        apiTask.execute();
    }

    private class GetApiDataTask extends AsyncTask<Void, Void, Void> implements Callback<List<Pharmacy>> {

        private Query query;
        private Call<List<Pharmacy>> call;

        private boolean finished = false;

        GetApiDataTask(Query query) {
            this.query = query;
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (ActivityCompat.checkSelfPermission(PharmaciesMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PharmaciesMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (query.getQueryType() == GET_LOCATION_PHARMACIES) {
                    call = service.listByLocation(query.getLatitude(), query.getLongitude(), query.getRadio(), "");
                    Date d = new Date();
                    Log.d("DEBUG", Utils.getDate(d) + " Pharmacies location request");
                }
                else if (query.getQueryType() == GET_NEAREST_PHARMACIES){
                    call = service.getNearest(query.getLatitude(), query.getLongitude(), 1000000, "open");
                    Date d = new Date();
                    Log.d("DEBUG", Utils.getDate(d) + " Pharmacies nearest request");
                }
                else if (query.getQueryType() == GET_TEXT_QUERY_PHARMACIES){
                    call = service.listByUserSearch(query.getSearch());
                    Date d = new Date();
                    Log.d("DEBUG", Utils.getDate(d) + " Pharmacies nearest request");
                }
                call.enqueue(this);
            }
            while (!finished) {
                if (isCancelled()) {
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            Date d = new Date();
            if (call != null) {
                Log.d("DEBUG", Utils.getDate(d) + " Cancelled call at task " + apiTask.toString());
                call.cancel();
            }
            super.onCancelled();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarMap.setVisibility(View.VISIBLE);
            //searchImg.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Date d = new Date();
            Log.d("DEBUG", Utils.getDate(d) + " Called onPostExecute on task " + apiTask.toString());
            super.onPostExecute(aVoid);
        }

        @Override
        public void onResponse(Call<List<Pharmacy>> call, Response<List<Pharmacy>> response) {
            pharmacies = response.body();
            pharmaciesHashMap = new HashMap<>();
            pharmaciesListItems = new ArrayList<>();
            markerMap = new HashMap<>();

            if (pharmacies != null && pharmacies.size() == 0 && apiTaskSearch == null) {
                Location loc = getMapCenter();
                Query query = new Query();
                query.setLatitude(loc.getLatitude());
                query.setLongitude(loc.getLongitude());
                query.setRadio(getMapRadio());
                query.setQueryType(GET_NEAREST_PHARMACIES);
                apiTaskNearest = new GetApiDataTask(query);
                Date d = new Date();
                Log.d("DEBUG", Utils.getDate(d) + " New task " + apiTask.toString());
                apiTaskNearest.execute();
                if (apiTask != null && apiTask.getStatus() != Status.FINISHED) {
                    apiTask.cancel(true);
                }
                if (apiTaskSearch != null && apiTaskSearch.getStatus() != Status.FINISHED) {
                    apiTaskSearch.cancel(true);
                }
            }
            else if (this.query.getQueryType() == GET_TEXT_QUERY_PHARMACIES){
                Toast.makeText(getBaseContext(), getString(R.string.pharmacy_search_no_results), Toast.LENGTH_SHORT).show();
            }

            if (pharmacies != null && !isCancelled()) {
                for (Pharmacy pharmacy : pharmacies) {
                    pharmaciesHashMap.put(pharmacy.getCodPharmacy(), pharmacy);
                    PharmacyListItem listItem = new PharmacyListItem();
                    listItem.setName(Utils.capitalizeNames(pharmacy.getName()));
                    listItem.setAddress(pharmacy.getAddress());
                    listItem.setOpen(pharmacy.isOpen());
                    pharmaciesListItems.add(listItem);
                }
                GetTravelTimeTask getTimesTask = new GetTravelTimeTask(TravelTypes.CAR);
                getTimesTask.execute();
                pharmacyListFragment.setData(pharmaciesListItems);
                Date d = new Date();
                Log.d("DEBUG", Utils.getDate(d) + " API sends " + pharmaciesHashMap.size() + " pharmacies");
                updateUI(false);
                finished = true;
            }
            // searchImg.setVisibility(View.VISIBLE);
            progressBarMap.setVisibility(View.GONE);
        }

        @Override
        public void onFailure(Call<List<Pharmacy>> call, Throwable t) {
            Log.e(PharmaciesMapActivity.class.getSimpleName(), t.getMessage());
        }

        private void updateUI(boolean centerMap) {
            if (mapLoaded) {

                if (mLastLocation != null && centerMap) {
                    centerMap();
                }

                if (pharmaciesHashMap != null) {
                    mClusterManager.clearItems();

                    if (showNearest && pharmacies.size() == 1 && apiTaskNearest != null && apiTaskNearest.getStatus() == Status.RUNNING) {
                        showNearest = false;
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        LatLng latlng = new LatLng(pharmacies.get(0).getGps()[1], pharmacies.get(0).getGps()[0]);
                        LatLng latlngLastLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                        builder.include(latlng);
                        builder.include(latlngLastLocation);
                        LatLngBounds bounds = builder.build();

                        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300));
                    }
                    else if (pharmacies.size() >= 1 && apiTaskSearch != null){
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        LatLng latlng = new LatLng(pharmacies.get(0).getGps()[1], pharmacies.get(0).getGps()[0]);
                        LatLng latlngLastLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                        builder.include(latlng);
                        builder.include(latlngLastLocation);
                        LatLngBounds bounds = builder.build();
                        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300));
                    }

                    for (Map.Entry<Integer, Pharmacy> entry : pharmaciesHashMap.entrySet()) {
                        Pharmacy pharmacy = entry.getValue();
                        LatLng pharmacyLocation = new LatLng(pharmacy.getGps()[1], pharmacy.getGps()[0]);
                        MarkerOptions pharmaMarker = new MarkerOptions();
                        pharmaMarker.position(pharmacyLocation);
                        PharmacyMarker pharmacyMarker = new PharmacyMarker(pharmacy.getGps()[1], pharmacy.getGps()[0], pharmacy);
                        mClusterManager.addItem(pharmacyMarker);
                    }
                    mClusterManager.cluster();

                    Date d = new Date();
                    Log.d("DEBUG", Utils.getDate(d) + " Updated map with " + pharmaciesHashMap.size() + " pharmacies");
                }

            }

        }
    }

    private class GetTravelTimeTask extends AsyncTask<Void, Void, HashMap<Integer, String>> {

        private TravelTypes method;

        GetTravelTimeTask(TravelTypes method){
            this.method =  method;
        }

        @Override
        protected HashMap<Integer, String> doInBackground(Void... params) {

            HashMap<Integer, String> response = new HashMap<Integer, String>();

            response = MatrixDirectionsAPI.getTime(mLastLocation, pharmacies, method.getValue());

            return response;
        }

        @Override
        protected void onPostExecute(HashMap<Integer, String> result) {
            if (method.equals(TravelTypes.CAR)) {
                travelTimesCar = result;
            }
        }
    }

    public class PharmacyMarker implements ClusterItem {
        private final LatLng mPosition;
        private final Pharmacy pharmacy;

        public PharmacyMarker(double lat, double lng, Pharmacy pharmacy) {
            mPosition = new LatLng(lat, lng);
            this.pharmacy = pharmacy;
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }

        public Pharmacy getPharmacy() {
            return pharmacy;
        }
    }

    private class MarkerRenderer extends DefaultClusterRenderer<PharmacyMarker> {

        public MarkerRenderer() {
            super(getApplicationContext(), map, mClusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(PharmacyMarker marker, MarkerOptions markerOptions) {
            Pharmacy pharmacy = marker.getPharmacy();
            if (previousPharmacy != null && previousPharmacy.getCodPharmacy().intValue() == pharmacy.getCodPharmacy().intValue() &&
                    fragmentMarker.isVisible()) {
                if (!pharmacy.isOpen()) {
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(iconClosedSelectedMarker.toBitmap()));
                } else {
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(iconSelectedMarker.toBitmap()));
                }
            } else {
                if (!pharmacy.isOpen()) {
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(iconClosedMarker.toBitmap()));
                } else {
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(iconMarker.toBitmap()));
                }
            }
        }

        @Override
        protected void onClusterItemRendered(PharmacyMarker pharmacyMarker, Marker marker) {
            super.onClusterItemRendered(pharmacyMarker, marker);
            markerMap.put(marker.getId(), pharmacyMarker.getPharmacy());
            if (previousPharmacy != null && pharmaciesHashMap.containsKey(previousPharmacy.getCodPharmacy()) && previousPharmacy.getCodPharmacy().intValue() == pharmacyMarker.getPharmacy().getCodPharmacy().intValue()) {
                previousMarker = marker;
                previousPharmacyMarker = pharmacyMarker;
                previousPharmacy = pharmacyMarker.getPharmacy();
            }
        }

        @Override
        protected void onClusterRendered(Cluster<PharmacyMarker> cluster, Marker marker) {
            super.onClusterRendered(cluster, marker);

            if (cluster.getItems().contains(previousPharmacyMarker)) {
                previousPharmacyMarker = null;
                previousMarker = null;
                previousPharmacy = null;
            }

        }
    }
}
