package es.usc.citius.servando.calendula.pharmacies.adapters;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
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

/**
 * Created by isaac on 3/12/16.
 */

public class PharmacyItemAdapter extends ArrayAdapter<PharmacyListItem> {


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
        TextView timeCar = (TextView) convertView.findViewById(R.id.pharmacy_list_time_car);
        TextView timeWalking = (TextView) convertView.findViewById(R.id.pharmacy_list_time_walking);
        TextView timeBike = (TextView) convertView.findViewById(R.id.pharmacy_list_time_bike);
        TextView timeTransit = (TextView) convertView.findViewById(R.id.pharmacy_list_time_transit);

        IconicsDrawable iconOpen;
        IconicsDrawable iconClose;

        PharmacyListItem listItem = getItem(position);

        if (listItem.isOpen()){
            iconOpen = new IconicsDrawable(getContext(), PharmaciesFont.Icon.ic_list)
                    .sizeDp(34)
                    .color(Color.argb(255, 24, 158, 89));
            image.setImageDrawable(iconOpen);
        }
        else{
            iconClose = new IconicsDrawable(getContext(), PharmaciesFont.Icon.ic_list)
                    .sizeDp(34)
                    .color(Color.argb(100, 24, 158, 89));
            image.setImageDrawable(iconClose);
        }

        name.setText(listItem.getName());
        address.setText(listItem.getAddress());
        timeCar.setText(listItem.getTimeTravelCar());
        timeWalking.setText(listItem.getTimeTravelWalking());
        timeBike.setText(listItem.getTimeTravelBicycle());
        timeTransit.setText(listItem.getTimeTravelTransit());

        return convertView;
    }
}
