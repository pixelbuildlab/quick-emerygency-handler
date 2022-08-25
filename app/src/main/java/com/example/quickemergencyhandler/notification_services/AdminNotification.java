package com.example.quickemergencyhandler.notification_services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quickemergencyhandler.AdminScreens.AdminNotificationsActivity;
import com.example.quickemergencyhandler.R;
import com.example.quickemergencyhandler.models.NotificationModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class AdminNotification extends Service {

    FirebaseFirestore firebaseFirestore;
    static int notificationID = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //get the info
      /*  String title = intent.getStringExtra("title");
        String subTitle = intent.getStringExtra("subTitle");
        String from = intent.getStringExtra("from");
        String to = intent.getStringExtra("to");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");*/


        //create a notification instance
        //String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        //String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        //NotificationModel notificationModel = new NotificationModel(notificationID, from, to, title, subTitle, 1, date, time);


        firebaseFirestore.collection("notifications").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot snapshot : task.getResult())
                {
                    if(snapshot.get("to").toString().equals("admin") && Integer.parseInt(snapshot.get("isRead").toString()) == 3)
                    {
                        notificationID++;
                        adminNotification(snapshot.get("title").toString(), snapshot.get("subTitle").toString(),notificationID);
                        String id = snapshot.get("notificationID").toString();
                        NotificationModel replaceModel = new NotificationModel(
                                snapshot.get("notificationID").toString(),
                                snapshot.get("from").toString(),
                                snapshot.get("to").toString(),
                                snapshot.get("title").toString(),
                                snapshot.get("subTitle").toString(),
                                0,
                                snapshot.get("date").toString(),
                                snapshot.get("time").toString()
                        );
                        firebaseFirestore.collection("notifications").document(id).set(replaceModel);
                    }
                }
            }
        });

        return START_NOT_STICKY;
}

    public void adminNotification(String title, String subTitle, int notificationID) {
        Intent mainIntent = new Intent(getApplicationContext(), AdminNotificationsActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, mainIntent, 0);
        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(subTitle)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_MESSAGE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "REMINDERS";
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Reminder",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }
        notificationManager.notify(notificationID, builder.build());
    }
}
