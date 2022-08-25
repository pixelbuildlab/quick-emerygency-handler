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

public class AdapterLvDCompletedRides extends BaseAdapter {

    private Context context;
    private ArrayList<Booking> items;
    private static LayoutInflater inflater = null;

    public AdapterLvDCompletedRides(Context context, ArrayList<Booking> items)
    {
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
            vi = inflater.inflate(R.layout.single_item_driver_completed_rides, null);

        TextView dateTV, timeTV, costTV, countTV;

        dateTV = vi.findViewById(R.id.dateTVcompletedRideDSingle);
        timeTV = vi.findViewById(R.id.timeTVcompletedRideDSingle);
        costTV = vi.findViewById(R.id.costTVcompletedRideDSingle);
        countTV = vi.findViewById(R.id.ridesCountSingle);

        dateTV.setText("Date: " + items.get(i).getDate());
        timeTV.setText("Time: " + items.get(i).getTime());
        costTV.setText("Cost: Rs." + items.get(i).getCost());
        countTV.setText(String.valueOf(i+1));

        return vi;
    }
}
