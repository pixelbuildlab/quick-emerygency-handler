package com.example.quickemergencyhandler.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.quickemergencyhandler.R;
import com.example.quickemergencyhandler.models.Booking;

import java.util.ArrayList;

public class AdapterLvAdminAllRides extends BaseAdapter {

    private Context context;
    private ArrayList<Booking> items;
    private static LayoutInflater inflater = null;

    public AdapterLvAdminAllRides(Context context, ArrayList<Booking> items)
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
            vi = inflater.inflate(R.layout.single_item_all_rides_admin, null);

        ImageView statusImage;
        TextView driverID, userID, date, cost, status;

        statusImage = vi.findViewById(R.id.statusIvAllRidesAdminSingle);
        status = vi.findViewById(R.id.statusAllRidesAdminSingle);
        driverID = vi.findViewById(R.id.driverIdAllRidesAdminSingle);
        userID = vi.findViewById(R.id.userIdAllRidesAdminSingle);
        date = vi.findViewById(R.id.dateAllRidesAdminSingle);
        cost = vi.findViewById(R.id.costAllRidesAdminSingle);

        status.setText("Status: " + items.get(i).getStatus());
        driverID.setText("DriverID: " + items.get(i).getDriverID());
        userID.setText("UserID: " + items.get(i).getUserID());
        date.setText("Date: " + items.get(i).getDate());
        cost.setText("Cost: " + items.get(i).getCost());

        if(items.get(i).getStatus().equals("pending"))
        {
            statusImage.setColorFilter(ContextCompat.getColor(context, R.color.g_yellow),
                    PorterDuff.Mode.MULTIPLY);
        }
        else if(items.get(i).getStatus().equals("accepted"))
        {
            statusImage.setColorFilter(ContextCompat.getColor(context, R.color.g_blue),
                    PorterDuff.Mode.MULTIPLY);
        }
        else if(items.get(i).getStatus().equals("completed"))
        {
            statusImage.setColorFilter(ContextCompat.getColor(context, R.color.g_green),
                    PorterDuff.Mode.MULTIPLY);
        }
        else
        {
            statusImage.setColorFilter(ContextCompat.getColor(context, R.color.dark_red),
                    PorterDuff.Mode.MULTIPLY);
        }

        return vi;
    }
}
