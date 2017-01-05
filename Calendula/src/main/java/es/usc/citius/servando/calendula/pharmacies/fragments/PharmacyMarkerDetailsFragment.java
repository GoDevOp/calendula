package es.usc.citius.servando.calendula.pharmacies.fragments;

import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.iconics.IconicsDrawable;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import es.usc.citius.servando.calendula.R;
import es.usc.citius.servando.calendula.pharmacies.activities.PharmaciesMapActivity;
import es.usc.citius.servando.calendula.pharmacies.persistance.Pharmacy;
import es.usc.citius.servando.calendula.pharmacies.util.PharmaciesFont;
import es.usc.citius.servando.calendula.pharmacies.util.TimeTravel;
import es.usc.citius.servando.calendula.pharmacies.util.TravelTypes;
import es.usc.citius.servando.calendula.pharmacies.util.Utils;

public class PharmacyMarkerDetailsFragment extends Fragment {

    Pharmacy pharmacy;
    TextView txtName;
    TextView txtHours;
    TextView txtState;
    TextView txtCarTime;
    TextView txtWalkTime;
    TextView txtBikeTime;
    TextView txtPublicTime;
    Location lastLocation;

    View layout;

    public PharmacyMarkerDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_pharmacy_marker_details, container, false);

        ImageView cross = (ImageView) layout.findViewById(R.id.pharmacy_cross_icon);
        IconicsDrawable iconCross = new IconicsDrawable(getActivity(), PharmaciesFont.Icon.ic_cross)
                .sizeDp(24)
                .color(Color.parseColor("#82C77B"));
        cross.setImageDrawable(iconCross);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            pharmacy = bundle.getParcelable("pharmacy");
            lastLocation = bundle.getParcelable("location");
        }

        return layout;
    }

    public void setData(Pharmacy pharmacy, Location location){
        this.pharmacy = pharmacy;
        this.lastLocation = location;
    }

    public void updateTimes(HashMap<TravelTypes, String> times) throws Exception{

        if (times != null){
            pharmacy.setTimeTravelCarSec(times.get(TravelTypes.CAR));
            pharmacy.setTimeTravelBicycleSec(times.get(TravelTypes.BICYCLE));
            pharmacy.setTimeTravelWalkingSec(times.get(TravelTypes.WALK));
            pharmacy.setTimeTravelTransitSec(times.get(TravelTypes.PUBLIC));
        }

        txtCarTime.setText(Utils.secondsToFormatString(pharmacy.getTimeTravelCarSec()));
        if (pharmacy.getTimeTravelCarSec() != "" && Long.parseLong(pharmacy.getTimeTravelCarSec()) > pharmacy.getSecondsUntilNextClose()) {
            txtCarTime.setTextColor(Color.RED);
        } else {
            txtCarTime.setTextColor(Color.parseColor("#0099CC"));
        }

        txtWalkTime.setText(Utils.secondsToFormatString(pharmacy.getTimeTravelWalkingSec()));
        if (pharmacy.getTimeTravelWalkingSec() != "" && Long.parseLong(pharmacy.getTimeTravelWalkingSec()) > pharmacy.getSecondsUntilNextClose()) {
            txtWalkTime.setTextColor(Color.RED);
        } else {
            txtWalkTime.setTextColor(Color.parseColor("#0099CC"));
        }

        txtBikeTime.setText(Utils.secondsToFormatString(pharmacy.getTimeTravelBicycleSec()));
        if (pharmacy.getTimeTravelBicycleSec() != "" && Long.parseLong(pharmacy.getTimeTravelBicycleSec()) > pharmacy.getSecondsUntilNextClose()) {
            txtBikeTime.setTextColor(Color.RED);
        } else {
            txtBikeTime.setTextColor(Color.parseColor("#0099CC"));
        }

        txtPublicTime.setText(Utils.secondsToFormatString(pharmacy.getTimeTravelTransitSec()));
        if (pharmacy.getTimeTravelTransitSec() != "" && Long.parseLong(pharmacy.getTimeTravelTransitSec()) > pharmacy.getSecondsUntilNextClose()) {
            txtPublicTime.setTextColor(Color.RED);
        } else {
            txtPublicTime.setTextColor(Color.parseColor("#0099CC"));
        }
    }

    public Pharmacy getPharmacy(){
        return pharmacy;
    }

    public void updateData(){
        if (pharmacy != null) {

            txtName = (TextView) layout.findViewById(R.id.pharmacy_name);
            txtHours = (TextView) layout.findViewById(R.id.pharmacy_hour);
            txtState = (TextView) layout.findViewById(R.id.pharmacy_state);
            txtCarTime = (TextView) layout.findViewById(R.id.pharmacy_time_car);
            txtWalkTime = (TextView) layout.findViewById(R.id.pharmacy_time_walking);
            txtBikeTime = (TextView) layout.findViewById(R.id.pharmacy_time_bike);
            txtPublicTime = (TextView) layout.findViewById(R.id.pharmacy_time_public);

            txtCarTime.setText("...");
            txtWalkTime.setText("...");
            txtBikeTime.setText("...");
            txtPublicTime.setText("...");

            txtName.setText(Utils.capitalizeNames(pharmacy.getName()));
            String hours = pharmacy.getHours();
            if (hours.isEmpty()){
                hours = getString(R.string.pharmacy_not_hours_today);
            }
            txtHours.setText(hours);
            if (pharmacy.isGuard()){
                txtState.setText(getString(R.string.pharmacy_guard));
                txtState.setTextColor(Color.parseColor("#F28130"));
            }
            else if (pharmacy.isOpen()){
                txtState.setText(getString(R.string.pharmacy_open));
                txtState.setTextColor(Color.parseColor("#669900"));
            }
            else{
                txtState.setText(getString(R.string.pharmacy_closed));
                txtState.setTextColor(Color.parseColor("#BCBCBC"));
            }

            try {
                updateTimes(null);
            } catch (Exception e){
                Log.e("MARKERDETAIL", "PharmacyMarkerDetailsFragment.updateData "+e.getLocalizedMessage());
            }
        }
    }

}
