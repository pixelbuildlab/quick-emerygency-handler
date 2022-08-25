package com.example.quickemergencyhandler.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.quickemergencyhandler.R;
import com.example.quickemergencyhandler.models.Booking;

import java.util.ArrayList;

public class DriverNotificationsLvAdapter extends BaseAdapter {

    private Context context;
    private static LayoutInflater inflater = null;
    private ArrayList<Booking> items;

    public DriverNotificationsLvAdapter(Context context, ArrayList<Booking> items) {
        this.context = context;
        this.items = items;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View vi = view;
        if (vi == null)
            vi = inflater.inflate(R.layout.single_item_driver_notifications_lv, null);

        TextView name, dateTime;
        name = vi.findViewById(R.id.name);
        dateTime = vi.findViewById(R.id.dateTime);

        name.setText("Notification for a new ride!");
        dateTime.setText(items.get(i).getDate() + " - " + items.get(i).getTime());

        return vi;
    }
}
