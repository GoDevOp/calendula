package es.usc.citius.servando.calendula.pharmacies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import es.usc.citius.servando.calendula.R;

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

        TextView name = (TextView) convertView.findViewById(R.id.pharmacy_list_name);
        TextView address = (TextView) convertView.findViewById(R.id.pharmacy_list_address);
        TextView timeCar = (TextView) convertView.findViewById(R.id.pharmacy_list_time_car);
        TextView timeWalking = (TextView) convertView.findViewById(R.id.pharmacy_list_time_walking);
        TextView timeBike = (TextView) convertView.findViewById(R.id.pharmacy_list_time_bike);
        TextView timeTransit = (TextView) convertView.findViewById(R.id.pharmacy_list_time_transit);

        PharmacyListItem listItem = getItem(position);

        name.setText(listItem.getName());
        address.setText(listItem.getAddress());
        timeCar.setText(listItem.getTimeTravelCar());
        timeWalking.setText(listItem.getTimeTravelWalking());
        timeBike.setText(listItem.getTimeTravelBicycle());
        timeTransit.setText(listItem.getTimeTravelTransit());

        return convertView;
    }
}
