package es.usc.citius.servando.calendula.pharmacies.fragments;


import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.Iconics;
import com.mikepenz.iconics.IconicsDrawable;

import es.usc.citius.servando.calendula.R;
import es.usc.citius.servando.calendula.database.DB;
import es.usc.citius.servando.calendula.pharmacies.persistance.Pharmacy;
import es.usc.citius.servando.calendula.pharmacies.util.TravelTypes;
import es.usc.citius.servando.calendula.pharmacies.util.Utils;
import es.usc.citius.servando.calendula.util.ScreenUtils;


public class PharmacyFragment extends Fragment {

    View layout;

    Pharmacy pharmacy;
    TextView txtName;
    TextView txtHours;
    TextView txtState;
    TextView txtTel;
    TextView txtDirection;
    TextView txtCarTime;
    TextView txtWalkTime;
    TextView txtBikeTime;
    TextView txtPublicTime;

    Toolbar toolbar;

    IconicsDrawable iconDirections;

    FloatingActionButton btnDirections;

    public PharmacyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        layout =  inflater.inflate(R.layout.fragment_pharmacy, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            pharmacy = bundle.getParcelable("pharmacy");
        }

        ScreenUtils.setStatusBarColor(getActivity(), Color.parseColor("#148577"));

        Drawable icon = new IconicsDrawable(layout.getContext(), GoogleMaterial.Icon.gmd_arrow_back)
                .sizeDp(24)
                .paddingDp(2)
                .color(Color.WHITE);

        AppCompatActivity activity = (AppCompatActivity) getActivity();

        toolbar = (android.support.v7.widget.Toolbar) layout.findViewById(R.id.toolbar_pharmacy);
        toolbar.setNavigationIcon(icon);
        activity.getDelegate().setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        //set the back arrow in the toolbar
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        activity.getSupportActionBar().setHomeButtonEnabled(true);

        iconDirections = new IconicsDrawable(getActivity(), GoogleMaterial.Icon.gmd_directions)
                .sizeDp(24)
                .color(Color.WHITE);
        btnDirections = (FloatingActionButton) layout.findViewById(R.id.get_pharmacy_route_detail);
        btnDirections.setImageDrawable(iconDirections);
        btnDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Get directions", Toast.LENGTH_SHORT).show();
            }
        });

        updateData();

        return layout;
    }

    public void getData(Pharmacy pharmacy){
        this.pharmacy = pharmacy;
    }

    public void updateData(){
        txtName = (TextView) layout.findViewById(R.id.detail_pharmacy_name);
        txtHours = (TextView) layout.findViewById(R.id.pharmacy_detail_hour);
        txtState = (TextView) layout.findViewById(R.id.pharmacy_detail_state);
        txtTel = (TextView) layout.findViewById(R.id.pharmacy_detail_tel);
        txtDirection = (TextView) layout.findViewById(R.id.pharmacy_detail_dir);
        txtCarTime = (TextView) layout.findViewById(R.id.detail_time_car);
        txtWalkTime = (TextView) layout.findViewById(R.id.detail_time_walking);
        txtBikeTime = (TextView) layout.findViewById(R.id.detail_time_bike);
        txtPublicTime = (TextView) layout.findViewById(R.id.detail_time_public);

        txtName.setText(Utils.capitalizeNames(pharmacy.getName()));
        txtHours.setText(pharmacy.getHours());
        if (pharmacy.isOpen()){
            txtState.setText(getString(R.string.pharmacy_open));
            txtState.setTextColor(Color.parseColor("#669900"));
        }
        else{
            txtState.setText(getString(R.string.pharmacy_closed));
            txtState.setTextColor(Color.parseColor("#BCBCBC"));
        }
        txtTel.setText(pharmacy.getPhone());

        String dir = pharmacy.getAddress()+"\n";
        dir += pharmacy.getPostCode() + " " + pharmacy.getTown();
        txtDirection.setText(dir);

        txtCarTime.setText(pharmacy.getTimeTravelCar());
        txtBikeTime.setText(pharmacy.getTimeTravelBicycle());
        txtPublicTime.setText(pharmacy.getTimeTravelTransit());
        txtWalkTime.setText(pharmacy.getTimeTravelWalking());
    }

}
