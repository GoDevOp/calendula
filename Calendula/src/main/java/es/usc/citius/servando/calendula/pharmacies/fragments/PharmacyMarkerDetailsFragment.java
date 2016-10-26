package es.usc.citius.servando.calendula.pharmacies.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import es.usc.citius.servando.calendula.R;
import es.usc.citius.servando.calendula.pharmacies.persistance.Pharmacy;
import es.usc.citius.servando.calendula.pharmacies.util.PharmaciesFont;

public class PharmacyMarkerDetailsFragment extends Fragment {

    Pharmacy pharmacy;
    TextView txtName;

    public PharmacyMarkerDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_pharmacy_marker_details, container, false);

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

}
