package com.example.quickemergencyhandler.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.quickemergencyhandler.R;
import com.example.quickemergencyhandler.models.History;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdapterLvDriverReviews extends BaseAdapter {

    private Context context;
    private ArrayList<History> items;
    private static LayoutInflater inflater = null;
    FirebaseFirestore firebaseFirestore;

    public AdapterLvDriverReviews(Context context, ArrayList<History> items) {
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
            vi = inflater.inflate(R.layout.single_item_driver_reviews_lv, null);

        RatingBar ratingBar;
        TextView comment, date, time, cost, name;

        firebaseFirestore = FirebaseFirestore.getInstance();
        name = vi.findViewById(R.id.nameSingleDreviews);
        date = vi.findViewById(R.id.dateSingleDreviews);
        time = vi.findViewById(R.id.timeSingleDreviews);
        cost = vi.findViewById(R.id.costSingleDreviews);
        ratingBar = vi.findViewById(R.id.ratingBarSingleDreviews);
        comment = vi.findViewById(R.id.commentSingleDreviews);

        name.setText("User Name: " + items.get(i).getUserName());
        date.setText("Date: " + items.get(i).getDate());
        time.setText("Time: " + items.get(i).getTime());
        cost.setText("Charges: " + items.get(i).getCharges());
        ratingBar.setRating(items.get(i).getRatingValue());
        comment.setText("Comment: " + items.get(i).getComment());

        return vi;
    }
}
