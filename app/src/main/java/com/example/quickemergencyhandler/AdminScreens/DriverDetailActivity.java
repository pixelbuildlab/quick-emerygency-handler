package com.example.quickemergencyhandler.AdminScreens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quickemergencyhandler.R;
import com.example.quickemergencyhandler.models.DriverModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;

public class DriverDetailActivity extends AppCompatActivity {

    TextView nameTV, emailTV, cnicTV, phoneTV, statusTV, ambulanceNoTV, ambulanceCopyNoTV, ambulanceModelTV, ambulancefeatures;
    Button updateStatusButton;
    Spinner changeStatusSpinner;
    ProgressBar progressBar;
    ImageView ambulanceImage;

    String id, name, email, cnic, phone, imageUrl, status, userType, ambulanceNo, ambulanceCopyNo, ambulanceModel;
    double lat, lng;
    boolean available;
    DriverModel driverModel;
    ArrayList<String> spinnerItems;
    ArrayList<Integer> features;

    FirebaseFirestore firebaseFirestore;
    CollectionReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_detail);

        //initializing the variables
        nameTV = findViewById(R.id.nameTVDDetails);
        emailTV = findViewById(R.id.emailTVDDetails);
        cnicTV = findViewById(R.id.cnicTVDDetails);
        phoneTV = findViewById(R.id.phoneTVDDetails);
        statusTV = findViewById(R.id.statusTVDDetails);
        ambulanceNoTV = findViewById(R.id.ambulanceNoTVDDetails);
        ambulanceCopyNoTV = findViewById(R.id.ambulanceCopyNumberTVDDetails);
        ambulanceModelTV = findViewById(R.id.ambulanceModelTVDDetails);
        ambulancefeatures = findViewById(R.id.ambulanceFeaturesList);
        updateStatusButton = findViewById(R.id.updateStatusButtonTVDDetails);
        ambulanceImage = findViewById(R.id.ambulanceImage);
        progressBar = findViewById(R.id.progressbarManageDDetails);
        changeStatusSpinner = findViewById(R.id.spinnerChangeStatusTVDDetails);
        features = new ArrayList<>();
        spinnerItems = new ArrayList<>();
        spinnerItems.add("Select Status");
        spinnerItems.add("Approved");
        spinnerItems.add("Pending");
        spinnerItems.add("Blocked");

        firebaseFirestore = FirebaseFirestore.getInstance();
        databaseReference = firebaseFirestore.collection("users");

        //adapter of spinner
        ArrayAdapter<String> modelAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinnerItems);
        changeStatusSpinner.setAdapter(modelAdapter);

        //getting data from previous activity
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        name = intent.getStringExtra("name");
        email = intent.getStringExtra("email");
        cnic = intent.getStringExtra("cnic");
        phone = intent.getStringExtra("phone");
        imageUrl = intent.getStringExtra("imageUrl");
        status = intent.getStringExtra("status");
        userType = intent.getStringExtra("userType");
        ambulanceNo = intent.getStringExtra("aNo");
        ambulanceCopyNo = intent.getStringExtra("aCopyNo");
        ambulanceModel = intent.getStringExtra("aModel");
        features = intent.getIntegerArrayListExtra("features");
        lat = intent.getDoubleExtra("lat", 33);
        lng = intent.getDoubleExtra("lng", 77);
        available = intent.getBooleanExtra("available", false);


        //set data to views
        nameTV.setText(name);
        emailTV.setText(email);
        cnicTV.setText(cnic);
        phoneTV.setText(phone);
        ambulanceNoTV.setText(ambulanceNo);
        ambulanceCopyNoTV.setText(ambulanceCopyNo);
        ambulanceModelTV.setText(ambulanceModel);
        ambulancefeatures.setText(getFeature());
        Picasso.get().load(imageUrl).into(ambulanceImage);

        if (status.equals("blocked")) {
            statusTV.setText("Status:  " + status);
            statusTV.setTextColor(getResources().getColor(R.color.dark_red));
        } else if (status.equals("approved")) {
            statusTV.setText("Status:  " + status);
            statusTV.setTextColor(getResources().getColor(R.color.g_green));
        } else {
            statusTV.setText("Status:  " + status);
            statusTV.setTextColor(getResources().getColor(R.color.g_yellow));
        }

        updateStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show the progress bar
                progressBar.setVisibility(View.VISIBLE);
                updateStatusButton.setVisibility(View.INVISIBLE);

                if (!spinnerValidation()) {
                    //hide the progress bar
                    progressBar.setVisibility(View.INVISIBLE);
                    updateStatusButton.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Select a status first", Toast.LENGTH_SHORT).show();
                    return;
                }
                String selectedSpinnerItem = changeStatusSpinner.getSelectedItem().toString().toLowerCase(Locale.ROOT);
                if (selectedSpinnerItem.equals(status)) {
                    //hide the progress bar
                    progressBar.setVisibility(View.INVISIBLE);
                    updateStatusButton.setVisibility(View.VISIBLE);
                    //show message
                    Toast.makeText(getApplicationContext(), "This status is already applied", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (selectedSpinnerItem.equals("blocked")) {
                    driverModel = new DriverModel(id, name, email, cnic, phone, imageUrl, "blocked", ambulanceNo, ambulanceCopyNo, ambulanceModel, features, userType, lat, lng, false);

                    databaseReference.document(id).set(driverModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //change local status
                                status = "blocked";
                                statusTV.setText("Status:  " + status);
                                statusTV.setTextColor(getResources().getColor(R.color.g_red));

                                //hide the progress bar
                                progressBar.setVisibility(View.INVISIBLE);
                                updateStatusButton.setVisibility(View.VISIBLE);

                                //show success message
                                Toast.makeText(getApplicationContext(), "User status updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //hide the progress bar
                            progressBar.setVisibility(View.INVISIBLE);
                            updateStatusButton.setVisibility(View.VISIBLE);

                            //show failure message
                            Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (selectedSpinnerItem.equals("approved")) {
                    driverModel = new DriverModel(id, name, email, cnic, phone, imageUrl, "approved", ambulanceNo, ambulanceCopyNo, ambulanceModel, features, userType, lat, lng, available);

                    databaseReference.document(id).set(driverModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //change local status
                                status = "approved";
                                statusTV.setText("Status:  " + status);
                                statusTV.setTextColor(getResources().getColor(R.color.g_green));

                                //hide the progress bar
                                progressBar.setVisibility(View.INVISIBLE);
                                updateStatusButton.setVisibility(View.VISIBLE);

                                //show success message
                                Toast.makeText(getApplicationContext(), "User status updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //hide the progress bar
                            progressBar.setVisibility(View.INVISIBLE);
                            updateStatusButton.setVisibility(View.VISIBLE);

                            //show failure message
                            Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    driverModel = new DriverModel(id, name, email, cnic, phone, imageUrl, "pending", ambulanceNo, ambulanceCopyNo, ambulanceModel, features, userType, lat, lng, false);

                    databaseReference.document(id).set(driverModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //change local status
                                status = "pending";
                                statusTV.setText("Status:  " + status);
                                statusTV.setTextColor(getResources().getColor(R.color.g_yellow));

                                //hide the progress bar
                                progressBar.setVisibility(View.INVISIBLE);
                                updateStatusButton.setVisibility(View.VISIBLE);

                                //show success message
                                Toast.makeText(getApplicationContext(), "User status updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //hide the progress bar
                            progressBar.setVisibility(View.INVISIBLE);
                            updateStatusButton.setVisibility(View.VISIBLE);

                            //show failure message
                            Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    private boolean spinnerValidation() {
        String selectedSpinnerItem = changeStatusSpinner.getSelectedItem().toString();
        if (selectedSpinnerItem == "Select Status") {
            return false;
        }
        return true;
    }

    public String getFeature() {
        String feature = " ";
        for (int i = 0; i < features.size(); i++) {

            if (String.valueOf(features.get(i)).equals("1")) {
                feature = "Stretcher ";
            } else if (String.valueOf(features.get(i)).equals("2")) {
                feature = feature + "| Drip ";
            } else if (String.valueOf(features.get(i)).equals("3")) {
                feature = feature + "| Oxygen Mask ";
            } else if (String.valueOf(features.get(i)).equals("4")) {
                feature = feature + "| Doctor/Nurse";
            }

        }
        Log.d("mylogs", String.valueOf(features.size()));
        return feature;
    }
}

