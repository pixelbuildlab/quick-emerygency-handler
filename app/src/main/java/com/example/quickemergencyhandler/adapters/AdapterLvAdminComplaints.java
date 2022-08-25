package com.example.quickemergencyhandler.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.quickemergencyhandler.R;
import com.example.quickemergencyhandler.models.Rating;

import java.util.ArrayList;

public class AdapterLvAdminComplaints extends BaseAdapter {

    private Context context;
    private ArrayList<Rating> items;
    private static LayoutInflater inflater = null;

    public AdapterLvAdminComplaints(Context context, ArrayList<Rating> items)
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
            vi = inflater.inflate(R.layout.single_item_lv_admin_complaints, null);

        TextView complaintBy, complaintAgainst, reason;

        complaintBy = vi.findViewById(R.id.complaintBySingle);
        complaintAgainst = vi.findViewById(R.id.complaintAgainstSingle);
        reason = vi.findViewById(R.id.complaintReasonSingle);

        complaintBy.setText(items.get(i).getUserID());
        complaintAgainst.setText(items.get(i).getDriverID());
        reason.setText(items.get(i).getComment());

        return vi;
    }
}
