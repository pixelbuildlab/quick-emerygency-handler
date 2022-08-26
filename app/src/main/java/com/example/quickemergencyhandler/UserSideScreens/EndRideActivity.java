package com.example.quickemergencyhandler.UserSideScreens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.quickemergencyhandler.R;
import com.example.quickemergencyhandler.models.NotificationModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class EndRideActivity extends AppCompatActivity {

    TextView finalCostTV;
    Button endButton;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_ride);

        finalCostTV = findViewById(R.id.finalCost);
        endButton = findViewById(R.id.amountCollected);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        int cost = intent.getIntExtra("cost", 100);
        finalCostTV.setText("Rs. " + String.valueOf(cost));

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send notification to table
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()).toString();
                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                String uniqueID = UUID.randomUUID().toString();
                NotificationModel notificationModel = new NotificationModel(uniqueID, firebaseAuth.getCurrentUser().getUid(), userID, "You ride is completed, pay the amount to the driver, " + String.valueOf(cost) + " Rs.", "Date: " + currentDate + " ,Time: " + currentTime, 1, currentDate, currentTime);
                firebaseFirestore.collection("notifications").document(uniqueID).set(notificationModel);
                finish();
            }
        });
    }
}