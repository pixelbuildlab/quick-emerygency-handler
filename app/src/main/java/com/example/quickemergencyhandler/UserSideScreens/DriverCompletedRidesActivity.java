package com.example.quickemergencyhandler.UserSideScreens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.example.quickemergencyhandler.R;
import com.example.quickemergencyhandler.adapters.AdapterLvDCompletedRides;
import com.example.quickemergencyhandler.models.Booking;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DriverCompletedRidesActivity extends AppCompatActivity {

    ListView listView;
    AdapterLvDCompletedRides adapter;
    ArrayList<Booking> bookings;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String userKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_completed_rides);

        firebaseAuth = FirebaseAuth.getInstance();
        userKey = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        listView = findViewById(R.id.listViewCompletedRidesD);
        bookings = new ArrayList<>();

        //get bookings data
        firebaseFirestore.collection("Booking").whereEqualTo("driverID", userKey).whereEqualTo("status", "completed").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            String date = snapshot.get("date").toString();
                            String time = snapshot.get("time").toString();
                            int cost = Integer.parseInt(snapshot.get("cost").toString());
                            Booking booking = new Booking("", "", "", 0.0, 0.0, "completed", date, time, 0.0, 0.0, cost);
                            bookings.add(booking);
                        }
                        adapter = new AdapterLvDCompletedRides(getApplicationContext(), bookings);
                        listView.setAdapter(adapter);
                    } else {
                        Toast.makeText(getApplicationContext(), "You have no completed rides", Toast.LENGTH_LONG).show();
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