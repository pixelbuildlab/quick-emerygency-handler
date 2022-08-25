package com.example.quickemergencyhandler.AdminScreens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.quickemergencyhandler.R;
import com.example.quickemergencyhandler.adapters.AdapterAdminNotificationsRV;
import com.example.quickemergencyhandler.models.NotificationModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminNotificationsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    ArrayList<NotificationModel> notifications;
    AdapterAdminNotificationsRV adapter;

    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_notifications);

        recyclerView = findViewById(R.id.recyclerViewAdminNotifications);
        progressBar = findViewById(R.id.progressAdminNotifications);
        notifications = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();

        markAllNotificationsAsRead();

        //show the progress bar
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        //get data from database
        firebaseFirestore.collection("notifications").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    //hide the progress bar
                    progressBar.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);

                    //fetch data
                    String title, subTitle, userID;
                    String notificationID;
                    int isRead;
                    for(QueryDocumentSnapshot snapshot : task.getResult())
                    {
                        if(snapshot.get("to").toString().equals("admin"))
                        {
                            title = snapshot.get("title").toString();
                            subTitle = snapshot.get("subTitle").toString();
                            userID = snapshot.get("from").toString();
                            notificationID = (snapshot.get("notificationID").toString());
                            isRead = Integer.parseInt(snapshot.get("isRead").toString());
                            notifications.add(new NotificationModel(notificationID, userID, "admin", title, subTitle, 0, " ", " "));
                        }
                    }
                    adapter = new AdapterAdminNotificationsRV(getApplicationContext(), notifications, null);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //hide the progress bar
                progressBar.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);

                //show message
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void markAllNotificationsAsRead()
    {
        firebaseFirestore.collection("notifications").document().update("isRead", 1);
    }
}