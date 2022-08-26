package com.example.quickemergencyhandler.AdminScreens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quickemergencyhandler.R;
import com.example.quickemergencyhandler.UserSideScreens.LoginActivity;
import com.example.quickemergencyhandler.notification_services.AdminNotification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class AdminDashboard extends AppCompatActivity {

    CardView managePatientsCard, manageDriversCard, ridesCard, complaintsCard;
    TextView totalUsersTV, pendingUsersTV;
    ImageView logoutButton, adminNotifications, adminSettings;
    int totalUsers = 0, pendingUsers = 0, notificationsCount = 0;
    Intent i;

    //firebase variables
    FirebaseFirestore firebaseFirestore;
    CollectionReference collectionReference;

    //shared prefs variables
    SharedPreferences sharedPreferences;
    public static final String SHARED_PREFS = "SharedPrefs";
    public static final String userType = "userType";
    public static final String userNodeKey = "userKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        managePatientsCard = findViewById(R.id.patientsCardView);
        manageDriversCard = findViewById(R.id.driverCardView);
        logoutButton = findViewById(R.id.adminLogout);
        totalUsersTV = findViewById(R.id.totalUsers);
        pendingUsersTV = findViewById(R.id.pendingUsers);
        adminNotifications = findViewById(R.id.adminNotifications);
        adminSettings = findViewById(R.id.adminSettings);
        ridesCard = findViewById(R.id.activeEmergenciesCard);
        complaintsCard = findViewById(R.id.complaintsCard);

        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("users");

        i = new Intent(getApplicationContext(), AdminNotification.class);
        startService(i);

        getTotalAndPendingUsers();
        getNotificationsCount();

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        complaintsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboard.this, AdminComplaintsActivity.class);
                startActivity(intent);
            }
        });

        ridesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboard.this, AdminAllRidesActivity.class);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
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

        adminSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboard.this, AdminSettingsActivity.class);
                startActivity(intent);
            }
        });

        adminNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboard.this, AdminNotificationsActivity.class);
                startActivity(intent);
            }
        });

        manageDriversCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboard.this, ManageDriversActivity.class);
                startActivity(intent);
            }
        });

        managePatientsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboard.this, ManagePatientsActivity.class);
                startActivity(intent);
            }
        });


    }

    private void getTotalAndPendingUsers() {
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        totalUsers++;
                        if (snapshot.get("status").toString().equals("pending")) {
                            pendingUsers++;
                        }
                    }
                    totalUsersTV.setText(String.valueOf(totalUsers));
                    pendingUsersTV.setText(String.valueOf(pendingUsers));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getNotificationsCount() {
        firebaseFirestore.collection("notifications").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        if (snapshot.get("isRead").toString().equals("0")) {
                            notificationsCount++;
                        }
                    }
                    System.out.println("***************** " + notificationsCount);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}