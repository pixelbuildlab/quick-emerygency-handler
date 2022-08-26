package com.example.quickemergencyhandler.AdminScreens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.example.quickemergencyhandler.R;
import com.example.quickemergencyhandler.adapters.AdapterLvAdminComplaints;
import com.example.quickemergencyhandler.models.Rating;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminComplaintsActivity extends AppCompatActivity {

    ListView listView;
    AdapterLvAdminComplaints adapter;
    ArrayList<Rating> items;

    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_complaints);

        listView = findViewById(R.id.LvAdminComplaints);
        firebaseFirestore = FirebaseFirestore.getInstance();
        items = new ArrayList<>();

        firebaseFirestore.collection("Rating").whereLessThan("rating", 2.6).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        String userID, driverID, reason;
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            userID = snapshot.get("userID").toString();
                            driverID = snapshot.get("driverID").toString();
                            reason = snapshot.get("comment").toString();
                            Rating rating = new Rating("", userID, driverID, reason, 0);
                            items.add(rating);
                        }
                        adapter = new AdapterLvAdminComplaints(getApplicationContext(), items);
                        listView.setAdapter(adapter);
                    } else {
                        Toast.makeText(getApplicationContext(), "There are no complaints yet", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}