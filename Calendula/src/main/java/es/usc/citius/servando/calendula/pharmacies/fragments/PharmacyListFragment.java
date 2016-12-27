package es.usc.citius.servando.calendula.pharmacies.fragments;


import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.icons.MaterialDrawerFont;

import java.util.ArrayList;
import java.util.List;

import es.usc.citius.servando.calendula.R;
import es.usc.citius.servando.calendula.pharmacies.activities.PharmaciesMapActivity;
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
    IconicsDrawable iconCar;
    IconicsDrawable iconBike;
    IconicsDrawable iconWalk;
    IconicsDrawable iconTransit;

    ImageButton btnCar;
    ImageButton btnBike;
    ImageButton btnWalk;
    ImageButton btnTransit;

    RelativeLayout listLayout;

    List<PharmacyListItem> pharmacies;

    Toolbar toolbar;

    ShapeDrawable sd;

    public PharmacyListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        layout =  inflater.inflate(R.layout.fragment_pharmacy_list, container, false);

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

        iconMap = new IconicsDrawable(getActivity(), PharmaciesFont.Icon.ic_marker)
                .sizeDp(34)
                .color(Color.WHITE);
        iconCar = new IconicsDrawable(getActivity(), GoogleMaterial.Icon.gmd_directions_car)
                .sizeDp(14)
                .color(Color.WHITE);
        iconBike = new IconicsDrawable(getActivity(), GoogleMaterial.Icon.gmd_directions_bike)
                .sizeDp(14)
                .color(Color.WHITE);
        iconWalk = new IconicsDrawable(getActivity(), GoogleMaterial.Icon.gmd_directions_walk)
                .sizeDp(14)
                .color(Color.WHITE);
        iconTransit = new IconicsDrawable(getActivity(), GoogleMaterial.Icon.gmd_directions_subway)
                .sizeDp(14)
                .color(Color.WHITE);

        btnCar = (ImageButton) layout.findViewById(R.id.pharmacy_toolbar_list_car);
        btnCar.setBackgroundColor(Color.parseColor("#0d7065"));
        btnCar.setImageDrawable(iconCar);
        btnCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pharmacyItemAdapter.changeTime(1);
                pharmacyItemAdapter.notifyDataSetChanged();

                btnCar.setBackgroundColor(Color.parseColor("#0d7065"));
                btnWalk.setBackgroundColor(Color.TRANSPARENT);
                btnBike.setBackgroundColor(Color.TRANSPARENT);
                btnTransit.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        btnWalk = (ImageButton) layout.findViewById(R.id.pharmacy_toolbar_list_walk);
        btnWalk.setImageDrawable(iconWalk);
        btnWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pharmacyItemAdapter.changeTime(2);
                pharmacyItemAdapter.notifyDataSetChanged();

                btnCar.setBackgroundColor(Color.TRANSPARENT);
                btnWalk.setBackgroundColor(Color.parseColor("#0d7065"));
                btnBike.setBackgroundColor(Color.TRANSPARENT);
                btnTransit.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        btnBike = (ImageButton) layout.findViewById(R.id.pharmacy_toolbar_list_bike);
        btnBike.setImageDrawable(iconBike);
        btnBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pharmacyItemAdapter.changeTime(3);
                pharmacyItemAdapter.notifyDataSetChanged();

                btnCar.setBackgroundColor(Color.TRANSPARENT);
                btnWalk.setBackgroundColor(Color.TRANSPARENT);
                btnBike.setBackgroundColor(Color.parseColor("#0d7065"));
                btnTransit.setBackgroundColor(Color.TRANSPARENT);

            }
        });

        btnTransit = (ImageButton) layout.findViewById(R.id.pharmacy_toolbar_list_public);
        btnTransit.setImageDrawable(iconTransit);
        btnTransit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pharmacyItemAdapter.changeTime(4);
                pharmacyItemAdapter.notifyDataSetChanged();

                btnCar.setBackgroundColor(Color.TRANSPARENT);
                btnWalk.setBackgroundColor(Color.TRANSPARENT);
                btnBike.setBackgroundColor(Color.TRANSPARENT);
                btnTransit.setBackgroundColor(Color.parseColor("#0d7065"));

            }
        });

        btnList = (FloatingActionButton) layout.findViewById(R.id.pharmacies_list_button);
        btnMyPosition = (FloatingActionButton) layout.findViewById(R.id.center_map_pharmacies);

        btnMap = (FloatingActionButton) layout.findViewById(R.id.pharmacies_map_button);
        btnMap.setImageDrawable(iconMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScreenUtils.setStatusBarColor(getActivity(), Color.argb(50, 61, 63, 64));
                listLayout = (RelativeLayout) getActivity().findViewById(R.id.pharmacies_list);
                listLayout.setVisibility(View.GONE);
                getActivity().getFragmentManager().popBackStack();
            }
        });

        pharmacyListView = (ListView) layout.findViewById(R.id.pharmacies_list_view);
        pharmacyListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ScreenUtils.setStatusBarColor(getActivity(), Color.argb(50, 61, 63, 64));
                PharmacyListItem itemClicked = (PharmacyListItem) parent.getItemAtPosition(position);
                ((PharmaciesMapActivity)getActivity()).centerPharmacy(itemClicked);
                listLayout = (RelativeLayout) getActivity().findViewById(R.id.pharmacies_list);
                listLayout.setVisibility(View.GONE);
                getActivity().getFragmentManager().popBackStack();
            }
        });

        // Add data to itemAdapter
        pharmacyItemAdapter = new PharmacyItemAdapter(getActivity(), pharmacies);

        // Fill adapter list with data
        if (pharmacyListView != null && pharmacyItemAdapter != null) {
            pharmacyListView.setAdapter(pharmacyItemAdapter);
        }

        return layout;
    }

    public void setData(List<PharmacyListItem> pharmacies){
        this.pharmacies =  new ArrayList<>();
        this.pharmacies.addAll(pharmacies);
    }

    public void updateData(){
        if (pharmacyItemAdapter != null) {
            pharmacyItemAdapter.clear();
            pharmacyItemAdapter.addAll(pharmacies);
            pharmacyItemAdapter.notifyDataSetChanged();
        }
    }

}
