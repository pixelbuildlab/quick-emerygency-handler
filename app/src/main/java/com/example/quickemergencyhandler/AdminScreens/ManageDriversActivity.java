package com.example.quickemergencyhandler.AdminScreens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.quickemergencyhandler.R;
import com.example.quickemergencyhandler.adapters.AdapterManageDriversRV;
import com.example.quickemergencyhandler.interfaces.IManagePatientClickListener;
import com.example.quickemergencyhandler.models.DriverModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ManageDriversActivity extends AppCompatActivity {

    //variables for fetching data
    String id, name, email, cnic, phone, imageUrl, status, userType, vehicleNumber, vehicleCopyNumber, vehicleModel;
    double lat, lng;
    boolean available;

    //views
    RecyclerView recyclerView;
    AdapterManageDriversRV adapter;
    ArrayList<DriverModel> items;
    ArrayList<Integer> features;
    ProgressBar progressBar;

    //firebase variables
    CollectionReference databaseReference;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_drivers);

        recyclerView = findViewById(R.id.recyclerViewManageDrivers);
        progressBar = findViewById(R.id.progressManageD);
        items = new ArrayList<>();


        //fetch data from database
        fetchData(getApplicationContext());

    }


    public void fetchData(Context ctx) {
        //show the progress bar
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);

        //fetch data
        firestore = FirebaseFirestore.getInstance();
        databaseReference = firestore.collection("users");
        databaseReference.whereEqualTo("userType", "driver").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //hide the progress bar
                progressBar.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        id = document.get("id").toString();
                        name = document.get("name").toString();
                        email = document.get("email").toString();
                        cnic = document.get("cnic").toString();
                        phone = document.get("phoneNo").toString();
                        imageUrl = document.get("imageUrl").toString();
                        status = document.get("status").toString();
                        userType = document.get("userType").toString();
                        vehicleNumber = document.get("vehicleNumber").toString();
                        vehicleCopyNumber = document.get("vehicleCopyNumber").toString();
                        vehicleModel = document.get("vehicleModel").toString();
                        features = new ArrayList<>();
                        features = (ArrayList<Integer>) document.get("vehicleFeatures");
                        lat = Double.parseDouble(document.get("lat").toString());
                        lng = Double.parseDouble(document.get("lng").toString());
                        available = Boolean.parseBoolean(document.get("available").toString());
                        items.add(new DriverModel(id, name, email, cnic, phone, imageUrl, status, vehicleNumber, vehicleCopyNumber, vehicleModel, features, userType, lat, lng, available));
                    }

                    adapter = new AdapterManageDriversRV(items, getApplicationContext(), new IManagePatientClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            Intent intent = new Intent(ManageDriversActivity.this, DriverDetailActivity.class);
                            intent.putExtra("id", items.get(position).getId());
                            intent.putExtra("name", items.get(position).getName());
                            intent.putExtra("email", items.get(position).getEmail());
                            intent.putExtra("cnic", items.get(position).getCnic());
                            intent.putExtra("phone", items.get(position).getPhoneNo());
                            intent.putExtra("imageUrl", items.get(position).getImageUrl());
                            intent.putExtra("status", items.get(position).getStatus());
                            intent.putExtra("userType", items.get(position).getUserType());
                            intent.putExtra("aNo", items.get(position).getVehicleNumber());
                            intent.putExtra("aCopyNo", items.get(position).getVehicleCopyNumber());
                            intent.putExtra("aModel", items.get(position).getVehicleModel());
                            intent.putIntegerArrayListExtra("features", features);
                            intent.putExtra("lat", lat);
                            intent.putExtra("lng", lng);
                            intent.putExtra("available", available);
                            startActivity(intent);
                        }
                    });
                    recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
                    recyclerView.setAdapter(adapter);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //hide the progress bar
                progressBar.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);

                Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
