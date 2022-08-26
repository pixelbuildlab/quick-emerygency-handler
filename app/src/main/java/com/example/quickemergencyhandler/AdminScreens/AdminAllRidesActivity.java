package com.example.quickemergencyhandler.AdminScreens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.example.quickemergencyhandler.R;
import com.example.quickemergencyhandler.adapters.AdapterLvAdminAllRides;
import com.example.quickemergencyhandler.models.Booking;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminAllRidesActivity extends AppCompatActivity {

    String userID, driverID, date, time, status;
    int cost;

    ListView listView;
    AdapterLvAdminAllRides adapter;

    ArrayList<Booking> bookings;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_all_rides);

        listView = findViewById(R.id.lvAllRidesEmergenciesAdmin);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        bookings = new ArrayList<>();

        firebaseFirestore.collection("Booking").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            userID = snapshot.get("userID").toString();
                            driverID = snapshot.get("driverID").toString();
                            date = snapshot.get("date").toString();
                            time = snapshot.get("time").toString();
                            cost = Integer.parseInt(snapshot.get("cost").toString());
                            status = snapshot.get("status").toString();
                            Booking booking = new Booking("", userID, driverID, 0.0, 0.0, status, date, time, 0.0, 0.0, cost);
                            bookings.add(booking);
                        }
                        adapter = new AdapterLvAdminAllRides(getApplicationContext(), bookings);
                        listView.setAdapter(adapter);
                    } else {
                        Toast.makeText(getApplicationContext(), "There are no rides yet in the database", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}