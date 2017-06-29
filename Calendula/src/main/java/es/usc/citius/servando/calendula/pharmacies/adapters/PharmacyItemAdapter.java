package es.usc.citius.servando.calendula.pharmacies.adapters;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.iconics.IconicsDrawable;

import java.util.List;

import es.usc.citius.servando.calendula.R;
import es.usc.citius.servando.calendula.pharmacies.util.PharmaciesFont;
import es.usc.citius.servando.calendula.pharmacies.util.Utils;

/**
 * Created by isaac on 3/12/16.
 */

public class PharmacyItemAdapter extends ArrayAdapter<PharmacyListItem> {

    Integer visibleTime = 1;

    TextView timeCar;
    TextView timeBike;
    TextView timeWalk;
    TextView timePublic;
    TextView guardText;
    PharmacyListItem listItem;

    public PharmacyItemAdapter(Context context, List<PharmacyListItem> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (null == convertView) {
            convertView = inflater.inflate(R.layout.pharmacy_list_item, parent, false);
        }

        ImageView image = (ImageView) convertView.findViewById(R.id.pharmacy_list_icon);
        TextView name = (TextView) convertView.findViewById(R.id.pharmacy_list_name);
        TextView address = (TextView) convertView.findViewById(R.id.pharmacy_list_address);
        timeCar = (TextView) convertView.findViewById(R.id.pharmacy_list_time_car);
        timeBike = (TextView) convertView.findViewById(R.id.pharmacy_list_time_bike);
        timeWalk = (TextView) convertView.findViewById(R.id.pharmacy_list_time_walking);
        timePublic = (TextView) convertView.findViewById(R.id.pharmacy_list_time_public);
        guardText = (TextView) convertView.findViewById(R.id.guard_text);

        IconicsDrawable iconOpen;
        IconicsDrawable iconClose;
        IconicsDrawable iconGuard;

        listItem = getItem(position);

        if (listItem.isGuard()){
            iconGuard = new IconicsDrawable(getContext(), PharmaciesFont.Icon.ic_list)
                    .sizeDp(34)
                    .color(Color.parseColor("#F28130"));
            image.setImageDrawable(iconGuard);
        }
        else if (listItem.isOpen()){
            iconOpen = new IconicsDrawable(getContext(), PharmaciesFont.Icon.ic_list)
                    .sizeDp(34)
                    .color(Color.parseColor("#82C77B"));
            image.setImageDrawable(iconOpen);
        }
        else{
            iconClose = new IconicsDrawable(getContext(), PharmaciesFont.Icon.ic_list)
                    .sizeDp(34)
                    .color(Color.parseColor("#BCBCBC"));
            image.setImageDrawable(iconClose);
        }

        if (listItem.isGuard()){
            guardText.setVisibility(View.VISIBLE);
        }
        else{
            guardText.setVisibility(View.GONE);
        }
        name.setText(listItem.getName());
        address.setText(listItem.getAddress());
        timeCar.setText(Utils.secondsToFormatString(listItem.getTimeTravelCar(), false));
        timeBike.setText(Utils.secondsToFormatString(listItem.getTimeTravelBicycle(), false));
        timeWalk.setText(Utils.secondsToFormatString(listItem.getTimeTravelWalking(), false));
        timePublic.setText(Utils.secondsToFormatString(listItem.getTimeTravelTransit(), false));

        try {

            if (Long.parseLong(listItem.getTimeTravelCar()) > listItem.getSecondsUntilClose()) {
                timeCar.setTextColor(Color.RED);
            }
            if (Long.parseLong(listItem.getTimeTravelBicycle()) > listItem.getSecondsUntilClose()) {
                timeBike.setTextColor(Color.RED);
            }
            if (Long.parseLong(listItem.getTimeTravelWalking()) > listItem.getSecondsUntilClose()) {
                timeWalk.setTextColor(Color.RED);
            }
            if (Long.parseLong(listItem.getTimeTravelTransit()) > listItem.getSecondsUntilClose()) {
                timePublic.setTextColor(Color.RED);
            }
        }catch(Exception e){
            Log.e("PharmacyItemAdapter", "TimeParse "+e.getLocalizedMessage());
        }

        return convertView;
    }

    public void changeTime(Integer codTime){
        visibleTime = codTime;
    }
}