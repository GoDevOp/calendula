package es.usc.citius.servando.calendula.pharmacies.fragments;


import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import es.usc.citius.servando.calendula.R;
import es.usc.citius.servando.calendula.database.DB;
import es.usc.citius.servando.calendula.util.ScreenUtils;


public class PharmacyFragment extends Fragment {

    View layout;

    Toolbar toolbar;

    public PharmacyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        layout =  inflater.inflate(R.layout.fragment_pharmacy, container, false);

        ScreenUtils.setStatusBarColor(getActivity(), Color.parseColor("#148577"));

        Drawable icon = new IconicsDrawable(layout.getContext(), GoogleMaterial.Icon.gmd_arrow_back)
                .sizeDp(24)
                .paddingDp(2)
                .color(Color.WHITE);

        AppCompatActivity activity = (AppCompatActivity) getActivity();

        toolbar = (android.support.v7.widget.Toolbar) layout.findViewById(R.id.toolbar_pharmacy);
        toolbar.setNavigationIcon(icon);
        activity.getDelegate().setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //set the back arrow in the toolbar
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        activity.getSupportActionBar().setHomeButtonEnabled(true);

        return layout;
    }

}
