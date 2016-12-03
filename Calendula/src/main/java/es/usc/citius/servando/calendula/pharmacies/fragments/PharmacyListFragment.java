package es.usc.citius.servando.calendula.pharmacies.fragments;


import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.List;

import es.usc.citius.servando.calendula.R;
import es.usc.citius.servando.calendula.pharmacies.adapters.PharmacyItemAdapter;
import es.usc.citius.servando.calendula.pharmacies.adapters.PharmacyListItem;
import es.usc.citius.servando.calendula.pharmacies.persistance.Pharmacy;
import es.usc.citius.servando.calendula.pharmacies.util.PharmaciesFont;
import es.usc.citius.servando.calendula.pharmacies.util.Utils;
import es.usc.citius.servando.calendula.util.ScreenUtils;


public class PharmacyListFragment extends Fragment {

    View layout;

    FloatingActionButton btnList;
    FloatingActionButton btnMyPosition;
    FloatingActionButton btnMap;

    ListView pharmacyListView;
    PharmacyItemAdapter pharmacyItemAdapter;

    IconicsDrawable iconMap;

    RelativeLayout listLayout;

    List<PharmacyListItem> pharmacies;

    public PharmacyListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        layout =  inflater.inflate(R.layout.fragment_pharmacy_list, container, false);

        iconMap = new IconicsDrawable(getActivity(), PharmaciesFont.Icon.ic_marker)
                .sizeDp(34)
                .color(Color.WHITE);

        btnList = (FloatingActionButton) layout.findViewById(R.id.pharmacies_list_button);
        btnMyPosition = (FloatingActionButton) layout.findViewById(R.id.center_map_pharmacies);

        btnMap = (FloatingActionButton) layout.findViewById(R.id.pharmacies_map_button);
        btnMap.setImageDrawable(iconMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listLayout = (RelativeLayout) getActivity().findViewById(R.id.pharmacies_list);
                listLayout.setVisibility(View.GONE);
                getActivity().getFragmentManager().popBackStack();
            }
        });

        pharmacyListView = (ListView) layout.findViewById(R.id.pharmacies_list_view);

        // Add data to itemAdapter
        pharmacyItemAdapter = new PharmacyItemAdapter(getActivity(), pharmacies);

        // Fill adapter list with data
        pharmacyListView.setAdapter(pharmacyItemAdapter);

        return layout;
    }

    public void setData(List<PharmacyListItem> pharmacies){
        this.pharmacies =  pharmacies;
    }

    public void updateData(){
        pharmacyItemAdapter.clear();
        pharmacyItemAdapter.addAll(pharmacies);
        pharmacyItemAdapter.notifyDataSetChanged();
    }

}
