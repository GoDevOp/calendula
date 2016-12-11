package es.usc.citius.servando.calendula.pharmacies.fragments;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

    LinearLayout layoutPhone;
    RelativeLayout weekleyHours;

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
    TextView txtHoursMonday;
    TextView txtHoursTuesday;
    TextView txtHoursWensday;
    TextView txtHoursThursday;
    TextView txtHoursFriday;
    TextView txtHoursSaturday;
    TextView txtHoursSunday;

    Toolbar toolbar;

    IconicsDrawable iconDirections;
    IconicsDrawable iconHours;

    FloatingActionButton btnDirections;
    FloatingActionButton btnHours;

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

        txtHoursMonday = (TextView) layout.findViewById(R.id.pharmacy_hour_monday);
        txtHoursTuesday = (TextView) layout.findViewById(R.id.pharmacy_hour_tuesday);
        txtHoursWensday = (TextView) layout.findViewById(R.id.pharmacy_hour_wensday);
        txtHoursThursday = (TextView) layout.findViewById(R.id.pharmacy_hour_thursday);
        txtHoursFriday = (TextView) layout.findViewById(R.id.pharmacy_hour_friday);
        txtHoursSaturday = (TextView) layout.findViewById(R.id.pharmacy_hour_saturday);
        txtHoursSunday = (TextView) layout.findViewById(R.id.pharmacy_hour_sunday);

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
                String destination = pharmacy.getGps()[1]+","+pharmacy.getGps()[0];
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+destination);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        weekleyHours = (RelativeLayout) layout.findViewById(R.id.pharmacy_weekley_hours);
        iconHours = new IconicsDrawable(getActivity(), GoogleMaterial.Icon.gmd_time)
                .sizeDp(24)
                .color(Color.BLACK);
        btnHours = (FloatingActionButton) layout.findViewById(R.id.view_week_hours_btn);
        btnHours.setImageDrawable(iconHours);
        btnHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pharmacy.getWeekHours() == null){
                    pharmacy.calculateWeekHours();
                }
                txtHoursMonday.setText(pharmacy.getWeekHours().get(1) == null ? getString(R.string.pharmacy_not_hours_today) : pharmacy.getWeekHours().get(1));
                txtHoursTuesday.setText(pharmacy.getWeekHours().get(2) == null ? getString(R.string.pharmacy_not_hours_today) : pharmacy.getWeekHours().get(2));
                txtHoursWensday.setText(pharmacy.getWeekHours().get(3) == null ? getString(R.string.pharmacy_not_hours_today) : pharmacy.getWeekHours().get(3));
                txtHoursThursday.setText(pharmacy.getWeekHours().get(4) == null ? getString(R.string.pharmacy_not_hours_today) : pharmacy.getWeekHours().get(4));
                txtHoursFriday.setText(pharmacy.getWeekHours().get(5) == null ? getString(R.string.pharmacy_not_hours_today) : pharmacy.getWeekHours().get(5));
                txtHoursSaturday.setText(pharmacy.getWeekHours().get(6) == null ? getString(R.string.pharmacy_not_hours_today) : pharmacy.getWeekHours().get(6));
                txtHoursSunday.setText(pharmacy.getWeekHours().get(7) == null ? getString(R.string.pharmacy_not_hours_today) : pharmacy.getWeekHours().get(7));

                weekleyHours.setVisibility(View.VISIBLE);
                btnHours.hide();
            }
        });

        TextView hoursClose = (TextView) layout.findViewById(R.id.pharmacy_hours_close);
        hoursClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekleyHours.setVisibility(View.GONE);
                btnHours.show();
            }
        });

        layoutPhone = (LinearLayout) layout.findViewById(R.id.pharmacy_detail_layout_tel);
        layoutPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tel = (TextView) layout.findViewById(R.id.pharmacy_detail_tel);
                String uri = "tel:" + tel.getText().toString().trim() ;
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
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
        String hours = pharmacy.getHours();
        if (hours.isEmpty()){
            hours = getString(R.string.pharmacy_not_hours_today);
        }
        txtHours.setText(hours);
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

        txtCarTime.setText(Utils.secondsToFormatString(pharmacy.getTimeTravelCarSec(), true));
        txtBikeTime.setText(Utils.secondsToFormatString(pharmacy.getTimeTravelBicycleSec(), true));
        txtPublicTime.setText(Utils.secondsToFormatString(pharmacy.getTimeTravelTransitSec(), true));
        txtWalkTime.setText(Utils.secondsToFormatString(pharmacy.getTimeTravelWalkingSec(), true));

        try {
            if (pharmacy.getTimeTravelCarSec() != "" && Long.parseLong(pharmacy.getTimeTravelCarSec()) > pharmacy.getSecondsUntilNextClose()) {
                txtCarTime.setTextColor(Color.RED);
            } else {
                txtCarTime.setTextColor(Color.parseColor("#0099CC"));
            }

            if (pharmacy.getTimeTravelBicycleSec() != "" && Long.parseLong(pharmacy.getTimeTravelBicycleSec()) > pharmacy.getSecondsUntilNextClose()) {
                txtBikeTime.setTextColor(Color.RED);
            } else {
                txtBikeTime.setTextColor(Color.parseColor("#0099CC"));
            }

            if (pharmacy.getTimeTravelTransitSec() != "" && Long.parseLong(pharmacy.getTimeTravelTransitSec()) > pharmacy.getSecondsUntilNextClose()) {
                txtPublicTime.setTextColor(Color.RED);
            } else {
                txtPublicTime.setTextColor(Color.parseColor("#0099CC"));
            }

            if (pharmacy.getTimeTravelWalkingSec() != "" && Long.parseLong(pharmacy.getTimeTravelWalkingSec()) > pharmacy.getSecondsUntilNextClose()) {
                txtWalkTime.setTextColor(Color.RED);
            } else {
                txtWalkTime.setTextColor(Color.parseColor("#0099CC"));
            }
        } catch (NumberFormatException e){
            Log.e("PHARMADETAIL", "PharmacyFragment.updateData "+e.getLocalizedMessage());
        }
    }

}
