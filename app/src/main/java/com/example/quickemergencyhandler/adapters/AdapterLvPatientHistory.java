package com.example.quickemergencyhandler.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.quickemergencyhandler.R;
import com.example.quickemergencyhandler.models.Booking;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdapterLvPatientHistory extends BaseAdapter {

    Context context;
    ArrayList<Booking> items;
    private static LayoutInflater inflater = null;
    FirebaseFirestore firebaseFirestore;

    public AdapterLvPatientHistory(Context context, ArrayList<Booking> items)
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
            vi = inflater.inflate(R.layout.single_item_lv_patient_history, null);

        TextView status, date, cost, name;
        name = vi.findViewById(R.id.namePhistorySingle);
        status = vi.findViewById(R.id.statusPhistorySingle);
        date = vi.findViewById(R.id.datePhistorySingle);
        cost = vi.findViewById(R.id.costPhistorySingle);
        firebaseFirestore = FirebaseFirestore.getInstance();

        //get driver name from driver id
        firebaseFirestore.collection("users").document(items.get(i).getDriverID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot snapshot = task.getResult();
                    String driverName = snapshot.get("name").toString();
                    name.setText("Driver Name: " + driverName);
                    status.setText("Status: " + items.get(i).getStatus());

                    date.setText("Date: " + items.get(i).getDate());
                    cost.setText("Cost: " + items.get(i).getCost());
                }
            }
        });

        return vi;
    }
}
