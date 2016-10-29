package es.usc.citius.servando.calendula.pharmacies.fragments;

import android.graphics.Color;
import android.os.Bundle;
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

public class PharmacyMarkerDetailsFragment extends Fragment {

    Pharmacy pharmacy;
    TextView txtName;

    View layout;

    private GestureDetector mGestureDetector;

    public PharmacyMarkerDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_pharmacy_marker_details, container, false);

        // Create an object of our Custom Gesture Detector Class
        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        // Create a GestureDetector
        mGestureDetector = new GestureDetector(getActivity(), customGestureDetector);
        // Attach listeners that'll be called for double-tap and related gestures
        mGestureDetector.setOnDoubleTapListener(customGestureDetector);

        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, final MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                return true;
            }
        });

        IconicsDrawable iconList = new IconicsDrawable(this.getContext(), GoogleMaterial.Icon.gmd_directions)
                .sizeDp(24)
                .color(Color.WHITE);

        ImageButton btnList = (ImageButton) layout.findViewById(R.id.get_pharmacy_route);
        btnList.setImageDrawable(iconList);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            pharmacy = bundle.getParcelable("pharmacy");
        }

        return layout;
    }

    public void getData(Pharmacy pharmacy){
        this.pharmacy = pharmacy;
    }

    public void updateData(){
        if (pharmacy != null) {
            txtName = (TextView) getView().findViewById(R.id.pharmacy_name);
            txtName.setText(pharmacy.getName());
        }
    }

    class CustomGestureDetector implements GestureDetector.OnGestureListener,
            GestureDetector.OnDoubleTapListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.v("","onSingleTapConfirmed");
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.v("","onDoubleTap");
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.v("","onDoubleTapEvent");
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            Log.v("","onDown");
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            Log.v("","onShowPress");
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.v("","onSingleTapUp");
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.v("","onScroll");
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.v("","onLongPress");
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if (e1.getY() < e2.getY()) {
                ((PharmaciesMapActivity) PharmacyMarkerDetailsFragment.this.getActivity()).showFragment(PharmacyMarkerDetailsFragment.this, false);
            }

            if (e1.getY() > e2.getY()) {
                Log.d("FLING", "Show pharmacy details");
            }

            return true;
        }
    }

}
