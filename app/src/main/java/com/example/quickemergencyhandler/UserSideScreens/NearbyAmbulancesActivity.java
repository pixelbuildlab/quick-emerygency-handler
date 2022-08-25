package com.example.quickemergencyhandler.UserSideScreens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.quickemergencyhandler.R;
import com.example.quickemergencyhandler.adapters.AdapterLvNearbyAmbulances;
import com.example.quickemergencyhandler.models.DriverModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NearbyAmbulancesActivity extends AppCompatActivity {

    ListView listViewNearby;
    ProgressBar progressBar;
    AdapterLvNearbyAmbulances adapterLvNearbyAmbulances;
    //ArrayList<AmbulanceModel> ambulanceModels;
    ArrayList<DriverModel> nearbyDrivers;
    ArrayList<Integer> features;
    double distanceInKm;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;

    private LatLng userLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_ambulances);

        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        nearbyDrivers = new ArrayList<>();
        features = new ArrayList<>();
        listViewNearby = (ListView) findViewById(R.id.listViewNearbyAmbulances);
        progressBar = findViewById(R.id.progressNearbyAmbulance);


        Intent intent = getIntent();
        userLatLng = new LatLng(intent.getDoubleExtra("lat", 0.0), intent.getDoubleExtra("lng", 0.0));

        progressBar.setVisibility(View.VISIBLE);
        listViewNearby.setVisibility(View.INVISIBLE);

        firebaseFirestore.collection("users").whereEqualTo("userType","driver").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    progressBar.setVisibility(View.INVISIBLE);
                    listViewNearby.setVisibility(View.VISIBLE);

                    double userLat, userLng, driverLat, driverLng;
                    userLat = userLatLng.latitude;
                    userLng = userLatLng.longitude;
                    for(DocumentSnapshot snapshot : task.getResult())
                    {
                        //calculate distance between driver and user
                        driverLat = Double.parseDouble(snapshot.get("lat").toString());
                        driverLng = Double.parseDouble(snapshot.get("lng").toString());
                        float[] results = new float[1];
                        Location.distanceBetween(userLat, userLng, driverLat, driverLng, results);
                        distanceInKm = results[0]/1000;
                        System.out.println("distance nearby activity: " + String.valueOf(distanceInKm));

                        //get the available status of driver
                        boolean available = Boolean.parseBoolean(snapshot.get("available").toString());

                        features = (ArrayList<Integer>) snapshot.get("vehicleFeatures");


                        if(distanceInKm < 20 && available)
                        {
                            //fetch data of driver and add it to the nearby list
                            nearbyDrivers.add(new DriverModel(
                                    snapshot.get("id").toString(),
                                    snapshot.get("name").toString(),
                                    snapshot.get("email").toString(),
                                    snapshot.get("cnic").toString(),
                                    snapshot.get("phoneNo").toString(),
                                    snapshot.get("imageUrl").toString(),
                                    snapshot.get("status").toString(),
                                    snapshot.get("vehicleNumber").toString(),
                                    snapshot.get("vehicleCopyNumber").toString(),
                                    snapshot.get("vehicleModel").toString(),
                                    features,
                                    snapshot.get("userType").toString(),
                                    Double.parseDouble(snapshot.get("lat").toString()),
                                    Double.parseDouble(snapshot.get("lng").toString()),
                                    Boolean.parseBoolean(snapshot.get("available").toString())
                            ));
                        }
                    }
                    //create adapter and set on item click
                    adapterLvNearbyAmbulances = new AdapterLvNearbyAmbulances(getApplicationContext(), nearbyDrivers, userLatLng);
                    listViewNearby.setAdapter(adapterLvNearbyAmbulances);
                    listViewNearby.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent intent1 = new Intent(NearbyAmbulancesActivity.this, ConfirmBookingActivity.class);
                            intent1.putExtra("id", nearbyDrivers.get(i).getId());
                            intent1.putExtra("name", nearbyDrivers.get(i).getName());
                            intent1.putExtra("phoneNo", nearbyDrivers.get(i).getPhoneNo());
                            intent1.putExtra("vehicleNo", nearbyDrivers.get(i).getVehicleNumber());
                            intent1.putIntegerArrayListExtra("features", nearbyDrivers.get(i).getVehicleFeatures());
                            intent1.putExtra("userLat", userLat);
                            intent1.putExtra("userLng", userLng);
                            intent1.putExtra("driverLat", nearbyDrivers.get(i).getLat());
                            intent1.putExtra("driverLng", nearbyDrivers.get(i).getLng());
                            intent1.putExtra("distance", distanceInKm);
                            startActivity(intent1);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                listViewNearby.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}