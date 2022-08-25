package com.example.quickemergencyhandler.manager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    Activity context;
    private final String PREF_FILE = "ambulance_app";
    private final String LOGIN_PREF = "login_pref";
    private final String ID = "id";
    private final String NAME = "name";
    private final String CNIC = "cnic";
    private final String EMAIL = "email";
    private final String PHONE_NO = "phone_no";
    private final String USER_TYPE = "user_type";
    private final String VEHICLE_NO = "vehicle_no";
    private final String COPY_NO = "copy_no";
    private final String MODEL = "model";
    private final String PROFILE_URL = "profile_url";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    /**
     * function for saving teacher screen status
     *
     * @param _context     Activity
     * @param login_status 1 or 0
     */
    public void loginPreference(Activity _context, int login_status) {
        context = _context;
        sharedPreferences = _context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(LOGIN_PREF, login_status);
        editor.apply();
        editor.commit();
    }



    /*public void savecity(Activity _context, String city) {
        context = _context;
        sharedPreferences = _context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CITY, city);
        editor.apply();
        editor.commit();
    }*/



   /* public String getUserID(Activity _context) {
        sharedPreferences = _context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        String id = sharedPreferences.getString(ID, "");
        return id;
    }*/


    /**
     * @param _context Activity
     * @return 1 or 0
     */
    public int getLoginPreference(Activity _context) {
        sharedPreferences = _context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        int getActivityPrefConstant = sharedPreferences.getInt(LOGIN_PREF, 0);
        return getActivityPrefConstant;
    }

    public void savePatientData(Activity _context,
                                String id,
                                String name,
                                String email,
                                String cnic,
                                String phoneNo,
                                String userType) {

        context = _context;
        sharedPreferences = _context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(ID, id);
        editor.putString(NAME, name);
        editor.putString(EMAIL, email);
        editor.putString(CNIC, cnic);
        editor.putString(USER_TYPE, userType);
        editor.putString(PHONE_NO, phoneNo);
        editor.apply();
        editor.commit();

    }

    public void saveDriverData(Activity _context,
                               String id,
                               String name,
                               String email,
                               String cnic,
                               String phoneNo,
                               String vehicleNo,
                               String copyNo,
                               String model) {

        context = _context;
        sharedPreferences = _context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(ID, id);
        editor.putString(NAME, name);
        editor.putString(EMAIL, email);
        editor.putString(CNIC, cnic);
        editor.putString(PHONE_NO, phoneNo);
        editor.putString(VEHICLE_NO, vehicleNo);
        editor.putString(COPY_NO, copyNo);
        editor.putString(MODEL, model);
        editor.apply();
        editor.commit();

    }


    public void clearAllPreference(Activity _context) {
        SharedPreferences clearSettings = _context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        clearSettings.edit().clear().apply();
    }
}
