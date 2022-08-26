package com.example.quickemergencyhandler.UserSideScreens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.quickemergencyhandler.R;
import com.example.quickemergencyhandler.adapters.DriverNotificationsLvAdapter;
import com.example.quickemergencyhandler.models.Booking;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DriverNotificationsActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<Booking> items;

    String id, userID, driverID, date, time, status;
    int cost;
    double userLat, userLng;

    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_notifications);

        listView = findViewById(R.id.listDriverNotifications);
        items = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("Booking").whereEqualTo("status", "pending").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            id = snapshot.get("bookingID").toString();
                            userID = snapshot.get("userID").toString();
                            driverID = snapshot.get("driverID").toString();
                            date = snapshot.get("date").toString();
                            time = snapshot.get("time").toString();
                            status = snapshot.get("status").toString();
                            userLat = Double.parseDouble(snapshot.get("userLat").toString());
                            userLng = Double.parseDouble(snapshot.get("userLng").toString());
                            cost = Integer.parseInt(snapshot.get("cost").toString());
                            Booking booking = new Booking(id, userID, driverID, userLat, userLng, status, date, time, 0.0, 0.0, cost);
                            items.add(booking);
                        }
                        DriverNotificationsLvAdapter adapter = new DriverNotificationsLvAdapter(getApplicationContext(), items);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Intent intent = new Intent(DriverNotificationsActivity.this, DriverNotificationDetailActivity.class);
                                intent.putExtra("bookingID", id);
                                intent.putExtra("userID", userID);
                                intent.putExtra("driverID", driverID);
                                intent.putExtra("date", date);
                                intent.putExtra("time", time);
                                intent.putExtra("status", status);
                                intent.putExtra("userLat", userLat);
                                intent.putExtra("userLng", userLng);
                                intent.putExtra("cost", cost);
                                startActivity(intent);
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "You have no notifications currently", Toast.LENGTH_LONG).show();
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