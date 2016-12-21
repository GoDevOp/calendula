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

import es.usc.citius.servando.calendula.R;
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

    public Pharmacy getPharmacy(){
        return pharmacy;
    }

    public void updateData(boolean getTimes){
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
                txtState.setTextColor(Color.parseColor("#669900"));
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
                if (getTimes && (pharmacy.getTimeTravelCar() == null || pharmacy.getTimeTravelCar().isEmpty())) {
                    GetTravelTimeTask carTask = new GetTravelTimeTask(TravelTypes.CAR);
                    carTask.execute();
                } else {
                    txtCarTime.setText(Utils.secondsToFormatString(pharmacy.getTimeTravelCarSec()));
                    if (pharmacy.getTimeTravelCarSec() != "" && Long.parseLong(pharmacy.getTimeTravelCarSec()) > pharmacy.getSecondsUntilNextClose()) {
                        txtCarTime.setTextColor(Color.RED);
                    } else {
                        txtCarTime.setTextColor(Color.parseColor("#0099CC"));
                    }
                }

                if (getTimes && (pharmacy.getTimeTravelWalking() == null || pharmacy.getTimeTravelWalking().isEmpty())) {
                    GetTravelTimeTask walkTask = new GetTravelTimeTask(TravelTypes.WALK);
                    walkTask.execute();
                } else {
                    txtWalkTime.setText(Utils.secondsToFormatString(pharmacy.getTimeTravelWalkingSec()));
                    if (pharmacy.getTimeTravelWalkingSec() != "" && Long.parseLong(pharmacy.getTimeTravelWalkingSec()) > pharmacy.getSecondsUntilNextClose()) {
                        txtWalkTime.setTextColor(Color.RED);
                    } else {
                        txtWalkTime.setTextColor(Color.parseColor("#0099CC"));
                    }
                }

                if (getTimes && (pharmacy.getTimeTravelBicycle() == null || pharmacy.getTimeTravelBicycle().isEmpty())) {
                    GetTravelTimeTask bikeTask = new GetTravelTimeTask(TravelTypes.BICYCLE);
                    bikeTask.execute();
                } else {
                    txtBikeTime.setText(Utils.secondsToFormatString(pharmacy.getTimeTravelBicycleSec()));
                    if (pharmacy.getTimeTravelBicycleSec() != "" && Long.parseLong(pharmacy.getTimeTravelBicycleSec()) > pharmacy.getSecondsUntilNextClose()) {
                        txtBikeTime.setTextColor(Color.RED);
                    } else {
                        txtBikeTime.setTextColor(Color.parseColor("#0099CC"));
                    }
                }

                if (getTimes && (pharmacy.getTimeTravelTransit() == null || pharmacy.getTimeTravelTransit().isEmpty())) {
                    GetTravelTimeTask publicTask = new GetTravelTimeTask(TravelTypes.PUBLIC);
                    publicTask.execute();
                } else {
                    txtPublicTime.setText(Utils.secondsToFormatString(pharmacy.getTimeTravelTransitSec()));
                    if (pharmacy.getTimeTravelTransitSec() != "" && Long.parseLong(pharmacy.getTimeTravelTransitSec()) > pharmacy.getSecondsUntilNextClose()) {
                        txtPublicTime.setTextColor(Color.RED);
                    } else {
                        txtPublicTime.setTextColor(Color.parseColor("#0099CC"));
                    }
                }
            } catch (NumberFormatException e){
                Log.e("MARKERDETAIL", "PharmacyMarkerDetailsFragment.updateData "+e.getLocalizedMessage());
            }
        }
    }

    private void updateTime(TaskResponse time){
        if (time.getMethod().equals(TravelTypes.CAR)){
            pharmacy.setTimeTravelCarSec(time.getTime());
            txtCarTime.setText(Utils.secondsToFormatString(time.getTime()));
            if (time.getTime() != "" && Long.parseLong(time.getTime()) > pharmacy.getSecondsUntilNextClose()){
                txtCarTime.setTextColor(Color.RED);
            }
            else{
                txtCarTime.setTextColor(Color.parseColor("#0099CC"));
            }
        }
        else if (time.getMethod().equals(TravelTypes.BICYCLE)){
            pharmacy.setTimeTravelBicycleSec(time.getTime());
            txtBikeTime.setText(Utils.secondsToFormatString(time.getTime()));
            if (time.getTime() != "" && Long.parseLong(time.getTime()) > pharmacy.getSecondsUntilNextClose()){
                txtBikeTime.setTextColor(Color.RED);
            }
            else{
                txtBikeTime.setTextColor(Color.parseColor("#0099CC"));
            }
        }
        else if (time.getMethod().equals(TravelTypes.PUBLIC)){
            pharmacy.setTimeTravelTransitSec(time.getTime());
            txtPublicTime.setText(Utils.secondsToFormatString(time.getTime()));
            if (time.getTime() != "" && Long.parseLong(time.getTime()) > pharmacy.getSecondsUntilNextClose()){
                txtPublicTime.setTextColor(Color.RED);
            }
            else{
                txtPublicTime.setTextColor(Color.parseColor("#0099CC"));
            }
        }
        else if (time.getMethod().equals(TravelTypes.WALK)){
            pharmacy.setTimeTravelWalkingSec(time.getTime());
            txtWalkTime.setText(Utils.secondsToFormatString(time.getTime()));
            if (time.getTime() != "" && Long.parseLong(time.getTime()) > pharmacy.getSecondsUntilNextClose()){
                txtWalkTime.setTextColor(Color.RED);
            }
            else{
                txtWalkTime.setTextColor(Color.parseColor("#0099CC"));
            }
        }
    }

    private class GetTravelTimeTask extends AsyncTask<Void, Void, TaskResponse> {

        private TravelTypes method;

        GetTravelTimeTask(TravelTypes method){
            this.method =  method;
        }

        @Override
        protected TaskResponse doInBackground(Void... params) {

            TaskResponse response = new TaskResponse();

            String timeTravel = TimeTravel.getTime((float)lastLocation.getLatitude(), (float)lastLocation.getLongitude(), pharmacy.getGps()[1], pharmacy.getGps()[0], method.getValue());
            response.setTime(timeTravel);
            response.setMethod(method);

            return response;
        }

        @Override
        protected void onPostExecute(TaskResponse result) {
            updateTime(result);
        }
    }

    private class TaskResponse{
        private String time;
        private TravelTypes method;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public TravelTypes getMethod() {
            return method;
        }

        public void setMethod(TravelTypes method) {
            this.method = method;
        }
    }

}
