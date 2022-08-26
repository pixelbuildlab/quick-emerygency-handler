package com.example.quickemergencyhandler.UserSideScreens;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.quickemergencyhandler.R;
import com.example.quickemergencyhandler.notification_services.UserNotification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class PatientDashboardActivity extends AppCompatActivity {

    CardView logoutCard, bookRideCard, profileCard, activeRideCard, historyCard;
    ImageView helpButton;
    Intent i;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String currentUserKey;

    private int PERMISSION_CODE = 1;

    //shared prefs variables
    public static final String SHARED_PREFS = "SharedPrefs";
    public static final String userType = "userType";
    public static final String userNodeKey = "userKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_dashboard);

        // check if the permissions are granted
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PatientDashboardActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
            Toast.makeText(getApplicationContext(), "Provide the permissions to continue", Toast.LENGTH_SHORT).show();
          //  return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        currentUserKey = firebaseAuth.getCurrentUser().getUid();

        logoutCard = findViewById(R.id.logoutCardPDashboard);
        bookRideCard = findViewById(R.id.bookRideCardPDashboard);
        profileCard = findViewById(R.id.profileCardUserD);
        activeRideCard = findViewById(R.id.activeRidePatient);
        historyCard = findViewById(R.id.historyCardPatient);
        helpButton = findViewById(R.id.helpP);

        i = new Intent(getApplicationContext(), UserNotification.class);
        startService(i);

        historyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientDashboardActivity.this, PatientHistoryActivity.class);
                startActivity(intent);
            }
        });

        activeRideCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientDashboardActivity.this, UserActiveRideActivity.class);
                startActivity(intent);
            }
        });

        profileCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientDashboardActivity.this, PatientProfileActivity.class);
                startActivity(intent);
            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:03013073514"));
                startActivity(callIntent);
            }
        });

        logoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putInt(userType, 0);
                editor.putString(userNodeKey, "empty");
                editor.apply();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
                stopService(i);
            }
        });

        bookRideCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if this user has already a ride ongoing
                firebaseFirestore.collection("Booking").whereEqualTo("userID", currentUserKey).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            boolean rideFound = false;

                            if(task.getResult().size() == 0)
                            {
                                Intent intent = new Intent(PatientDashboardActivity.this, SelectLocationActivity.class);
                                startActivity(intent);
                            }
                            else
                            {
                                for(DocumentSnapshot snapshot : task.getResult())
                                {
                                    if(!snapshot.get("status").toString().equals("completed") && !snapshot.get("status").toString().equals("rejected"))
                                    {
                                        rideFound = true;
                                    }
                                }
                            }

                            if(rideFound)
                            {
                                Toast.makeText(getApplicationContext(), "You have already a ride ongoing", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Intent intent = new Intent(PatientDashboardActivity.this, SelectLocationActivity.class);
                                startActivity(intent);
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
        });
    }
}