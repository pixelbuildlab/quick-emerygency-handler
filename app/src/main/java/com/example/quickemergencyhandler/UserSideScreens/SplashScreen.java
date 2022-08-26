package com.example.quickemergencyhandler.UserSideScreens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.quickemergencyhandler.AdminScreens.AdminDashboard;
import com.example.quickemergencyhandler.R;

public class SplashScreen extends AppCompatActivity {

    //shared prefs variables
    SharedPreferences sharedPreferences;
    public static final String SHARED_PREFS = "SharedPrefs";
    public static final String userType = "userType";
    public static final String userNodeKey = "userKey";
    private int userTypeInt;
    private String fetchedUserNodeKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        userTypeInt = sharedPreferences.getInt(userType, 0);
        fetchedUserNodeKey = sharedPreferences.getString(userNodeKey, "noKeyFetched");

        //duration of wait in splash screen
        int SPLASH_DISPLAY_LENGTH = 2000;

        //handler to close splash screen after 2 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (userTypeInt == 1) {
                    Intent mainIntent = new Intent(getApplicationContext(), DriverDashboardActivity.class);
                    mainIntent.putExtra("userKey", fetchedUserNodeKey);
                    startActivity(mainIntent);
                    finishAffinity();
                } else if (userTypeInt == 2) {
                    Intent mainIntent = new Intent(getApplicationContext(), PatientDashboardActivity.class);
                    mainIntent.putExtra("userKey", fetchedUserNodeKey);
                    startActivity(mainIntent);
                    finishAffinity();
                } else if (userTypeInt == 3) {
                    Intent mainIntent = new Intent(getApplicationContext(), AdminDashboard.class);
                    mainIntent.putExtra("userKey", fetchedUserNodeKey);
                    startActivity(mainIntent);
                    finishAffinity();
                } else {
                    Intent mainIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(mainIntent);
                    finishAffinity();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}