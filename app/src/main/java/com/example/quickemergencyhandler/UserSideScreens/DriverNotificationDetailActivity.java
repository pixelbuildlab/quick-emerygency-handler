package com.example.quickemergencyhandler.UserSideScreens;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quickemergencyhandler.R;
import com.example.quickemergencyhandler.models.Booking;
import com.example.quickemergencyhandler.models.NotificationModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class DriverNotificationDetailActivity extends FragmentActivity implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    GoogleMap map;
    LatLng userLatLng, driverLatLng;
    Button acceptButton, rejectButton;
    TextView nameTV, dateTV, timeTV;

    String id, userID, driverID, date, time, status;
    double userLat, userLng;
    int cost;
    String driverName;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_notification_detail);

        nameTV = findViewById(R.id.nameNotiDetail);
        dateTV = findViewById(R.id.dateNotiDetail);
        timeTV = findViewById(R.id.timeNotiDetail);
        acceptButton = findViewById(R.id.acceptRide);
        rejectButton = findViewById(R.id.rejectRide);

        Intent intent = getIntent();
        id = intent.getStringExtra("bookingID");
        userID = intent.getStringExtra("userID");
        driverID = intent.getStringExtra("driverID");
        status = intent.getStringExtra("status");
        userLat = intent.getDoubleExtra("userLat", 0.0);
        userLng = intent.getDoubleExtra("userLng", 0.0);
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");
        cost = intent.getIntExtra("cost", 150);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        fetchAndSetDataToViews(userID);
        fetchDriverName();

        userLatLng = new LatLng(userLat, userLng);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragmentDriverNotificationsDetail);
        mapFragment.getMapAsync(this);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Booking booking = new Booking(id, userID, driverID, userLat, userLng, "accepted", date, time, 0.0, 0.0, cost);
                firebaseFirestore.collection("Booking").document(id).set(booking);
                Toast.makeText(getApplicationContext(), "Ride Accepted, go to the Active Ride section from the dashboard to see the ride", Toast.LENGTH_LONG).show();
                Intent intent1 = new Intent(DriverNotificationDetailActivity.this, DriverDashboardActivity.class);
                finishAffinity();
                startActivity(intent1);

                //send notification to table
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                String uniqueID = UUID.randomUUID().toString();
                NotificationModel notificationModel = new NotificationModel(uniqueID, firebaseAuth.getCurrentUser().getUid(), userID, "You ride request was accepted by " + driverName , "Date: " + currentDate + " ,Time: " + currentTime, 1, currentDate, currentTime);
                firebaseFirestore.collection("notifications").document(uniqueID).set(notificationModel);
            }
        });

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Booking booking1 = new Booking(id, userID, driverID, userLat, userLng, "rejected", date, time, 0.0, 0.0, cost);
                firebaseFirestore.collection("Booking").document(id).set(booking1);
                Toast.makeText(getApplicationContext(), "Ride rejected!", Toast.LENGTH_LONG).show();
                Intent intent2 = new Intent(DriverNotificationDetailActivity.this, DriverDashboardActivity.class);
                finishAffinity();
                startActivity(intent2);

                //send notification to table
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                String uniqueID = UUID.randomUUID().toString();
                NotificationModel notificationModel = new NotificationModel(uniqueID, firebaseAuth.getCurrentUser().getUid(), userID, "You ride request was rejected by " + driverName , "Date: " + currentDate + " ,Time: " + currentTime, 1, currentDate, currentTime);
                firebaseFirestore.collection("notifications").document(uniqueID).set(notificationModel);
            }
        });
    }

    private void fetchDriverName()
    {
        firebaseFirestore.collection("users").document(driverID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot snapshot = task.getResult();
                    driverName = snapshot.get("name").toString();
                }
            }
        });
    }

    private void fetchAndSetDataToViews(String userID)
    {
        firebaseFirestore.collection("users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot snapshot = task.getResult();
                    String name = snapshot.get("name").toString();
                    nameTV.setText("Name: " + name);
                    dateTV.setText("Date: " + date);
                    timeTV.setText("Time: " + time);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.addMarker(new MarkerOptions().position(userLatLng).title("User Position"));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15));
    }
}