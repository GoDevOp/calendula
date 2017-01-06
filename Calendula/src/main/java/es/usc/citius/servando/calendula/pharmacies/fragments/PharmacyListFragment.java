package es.usc.citius.servando.calendula.pharmacies.fragments;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.mikepenz.iconics.view.IconicsButton;
import com.mikepenz.iconics.view.IconicsImageView;
import com.mikepenz.materialdrawer.icons.MaterialDrawerFont;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import es.usc.citius.servando.calendula.R;
import es.usc.citius.servando.calendula.pharmacies.activities.PharmaciesMapActivity;
import es.usc.citius.servando.calendula.pharmacies.adapters.PharmacyItemAdapter;
import es.usc.citius.servando.calendula.pharmacies.adapters.PharmacyListItem;
import es.usc.citius.servando.calendula.pharmacies.persistance.Pharmacy;
import es.usc.citius.servando.calendula.pharmacies.persistance.Query;
import es.usc.citius.servando.calendula.pharmacies.util.PharmaciesFont;
import es.usc.citius.servando.calendula.pharmacies.util.Utils;
import es.usc.citius.servando.calendula.util.ScreenUtils;

import static android.content.Context.SYSTEM_HEALTH_SERVICE;
import static es.usc.citius.servando.calendula.pharmacies.activities.PharmaciesMapActivity.GET_TEXT_QUERY_PHARMACIES;


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
    IconicsDrawable iconArrow;
    IconicsDrawable iconMic;
    IconicsDrawable iconClose;

    EditText searchTxt;
    IconicsButton btnClear;
    IconicsButton btnMic;

    RelativeLayout listLayout;

    List<PharmacyListItem> pharmacies;

    Toolbar toolbar;

    ProgressBar progressBarMap;

    boolean cursorVisible = true;

    public PharmacyListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        layout =  inflater.inflate(R.layout.fragment_pharmacy_list, container, false);

        ScreenUtils.setStatusBarColor(getActivity(), Color.parseColor("#148577"));

        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();

        toolbar = (android.support.v7.widget.Toolbar) layout.findViewById(R.id.toolbar_pharmacy);

        appCompatActivity.getDelegate().setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        iconMap = new IconicsDrawable(getActivity(), PharmaciesFont.Icon.ic_marker)
                .sizeDp(34)
                .color(Color.WHITE);

        iconArrow = new IconicsDrawable(getActivity(), GoogleMaterial.Icon.gmd_arrow_back)
                .sizeDp(14)
                .color(Color.GRAY);
        iconMic = new IconicsDrawable(getActivity(), GoogleMaterial.Icon.gmd_mic)
                .sizeDp(20)
                .color(Color.GRAY);
        iconClose = new IconicsDrawable(getActivity(), GoogleMaterial.Icon.gmd_close)
                .sizeDp(14)
                .color(Color.GRAY);

        btnList = (FloatingActionButton) layout.findViewById(R.id.pharmacies_list_button);
        btnMyPosition = (FloatingActionButton) layout.findViewById(R.id.center_map_pharmacies);
        progressBarMap = (ProgressBar) layout.findViewById(R.id.search_pharmacies_loading);

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
                hideKeyboard();
                ScreenUtils.setStatusBarColor(getActivity(), Color.argb(50, 61, 63, 64));
                PharmacyListItem itemClicked = (PharmacyListItem) parent.getItemAtPosition(position);
                ((PharmaciesMapActivity)getActivity()).centerPharmacy(itemClicked);
                listLayout = (RelativeLayout) getActivity().findViewById(R.id.pharmacies_list);
                listLayout.setVisibility(View.GONE);
                getActivity().getFragmentManager().popBackStack();
            }
        });

        ImageView imgFlecha = (ImageView) layout.findViewById(R.id.back_pharmacies_image);
        imgFlecha.setImageDrawable(iconArrow);
        imgFlecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScreenUtils.setStatusBarColor(getActivity(), Color.argb(50, 61, 63, 64));
                ((PharmaciesMapActivity) getActivity()).setSearch(searchTxt.getText().toString());
                hideKeyboard();
                btnMap.callOnClick();
            }
        });

        searchTxt = (EditText) layout.findViewById(R.id.search_pharmacies_text);
        searchTxt.setHint(R.string.pharmacy_search);
        searchTxt.setCursorVisible(cursorVisible);
        searchTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    PharmaciesMapActivity activity = (PharmaciesMapActivity) getActivity();
                    if (v.getText().toString().isEmpty()){
                        Toast.makeText(getContext(), getString(R.string.pharmacy_search_without_text), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    activity.setSearch(v.getText().toString());
                    activity.setClearVisibility(true);
                    hideKeyboard();
                    searchTxt.setCursorVisible(false);
                    if (activity.getApiTaskNearest() != null && activity.getApiTaskNearest().getStatus() != AsyncTask.Status.FINISHED){
                        activity.getApiTaskNearest().cancel(true);
                    }
                    if (activity.getApiTask() != null && activity.getApiTask().getStatus() != AsyncTask.Status.FINISHED){
                        activity.getApiTask().cancel(true);
                    }
                    if (activity.getGetTimesTask() != null && activity.getGetTimesTask().getStatus() != AsyncTask.Status.FINISHED){
                        activity.getGetTimesTask().cancel(true);
                    }
                    Query searchQuery = new Query();
                    searchQuery.setQueryType(GET_TEXT_QUERY_PHARMACIES);
                    searchQuery.setSearch(v.getText().toString());
                    activity.setApiTaskSearch(activity.startSearchTask(searchQuery));

                    return true;
                }
                return false;
            }
        });
        searchTxt.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0){
                    btnClear.setVisibility(View.VISIBLE);
                }
                else{
                    btnClear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnClear = (IconicsButton) layout.findViewById(R.id.clear_search_pharmacies);
        btnClear.setCompoundDrawables(iconClose, null, null, null);
        btnClear.setVisibility(View.GONE);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PharmaciesMapActivity activity = (PharmaciesMapActivity) getActivity();
                if (activity.getApiTaskSearch() != null && activity.getApiTaskSearch().getStatus() == AsyncTask.Status.RUNNING){
                    activity.getApiTaskSearch().cancel(true);
                }
                activity.setApiTaskSearch(null);
                activity.centerMap();
                searchTxt.setText("");
                activity.setSearch("");
                activity.throwLocationQuery();
                btnClear.setVisibility(View.GONE);
                activity.setClearVisibility(false);
            }
        });

        btnMic = (IconicsButton) layout.findViewById(R.id.voice_search_pharmacies);
        btnMic.setCompoundDrawables(iconMic, null, null, null);
        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PharmaciesMapActivity activity = (PharmaciesMapActivity) getActivity();
                activity.listen();
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

    public void setCursorVisible(boolean cursorVisible) {
        this.cursorVisible = cursorVisible;
        if (searchTxt != null) {
            this.searchTxt.setCursorVisible(cursorVisible);
            if (cursorVisible){
                this.searchTxt.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


            }
        }
    }

    public void setSearch(String search){
        if (searchTxt != null){
            this.searchTxt.setText(search);
            if (search.length() > 0){
                this.btnClear.setVisibility(View.VISIBLE);
            }
        }
    }

    public void hideKeyboard(){
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void setProgressBarVisibility(Integer visibility){
        if (progressBarMap != null) {
            progressBarMap.setVisibility(visibility);
        }
    }
}
