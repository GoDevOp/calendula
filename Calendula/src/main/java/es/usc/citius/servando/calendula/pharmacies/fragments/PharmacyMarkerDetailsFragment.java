package es.usc.citius.servando.calendula.pharmacies.fragments;

import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MotionEventCompat;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import es.usc.citius.servando.calendula.R;
import es.usc.citius.servando.calendula.pharmacies.activities.PharmaciesMapActivity;
import es.usc.citius.servando.calendula.pharmacies.persistance.Pharmacy;
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

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            pharmacy = bundle.getParcelable("pharmacy");
            lastLocation = bundle.getParcelable("location");
        }

        return layout;
    }

    public void getData(Pharmacy pharmacy, Location location){
        this.pharmacy = pharmacy;
        this.lastLocation = location;
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
            txtHours.setText(pharmacy.getHours());
            if (pharmacy.isOpen()){
                txtState.setText(getString(R.string.pharmacy_open));
                txtState.setTextColor(Color.parseColor("#669900"));
            }
            else{
                txtState.setText(getString(R.string.pharmacy_closed));
                txtState.setTextColor(Color.parseColor("#BCBCBC"));
            }

            GetTravelTimeTask carTask = new GetTravelTimeTask(TravelTypes.CAR);
            carTask.execute();

            GetTravelTimeTask walkTask = new GetTravelTimeTask(TravelTypes.WALK);
            walkTask.execute();

            GetTravelTimeTask bikeTask = new GetTravelTimeTask(TravelTypes.BICYCLE);
            bikeTask.execute();

            GetTravelTimeTask publicTask = new GetTravelTimeTask(TravelTypes.PUBLIC);
            publicTask.execute();

        }
    }

    private void updateTime(TaskResponse time){
        if (time.getMethod().equals(TravelTypes.CAR)){
            pharmacy.setTimeTravelCar(time.getTime());
            txtCarTime.setText(time.getTime());
        }
        else if (time.getMethod().equals(TravelTypes.BICYCLE)){
            pharmacy.setTimeTravelBicycle(time.getTime());
            txtBikeTime.setText(time.getTime());
        }
        else if (time.getMethod().equals(TravelTypes.PUBLIC)){
            pharmacy.setTimeTravelTransit(time.getTime());
            txtPublicTime.setText(time.getTime());
        }
        else if (time.getMethod().equals(TravelTypes.WALK)){
            pharmacy.setTimeTravelWalking(time.getTime());
            txtWalkTime.setText(time.getTime());
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
