package com.example.quickemergencyhandler.UserSideScreens;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.quickemergencyhandler.R;
import com.example.quickemergencyhandler.models.DriverModel;
import com.example.quickemergencyhandler.models.NotificationModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class RegistrationActivity2 extends AppCompatActivity {

    Spinner modelSpinner;
    ArrayList<String> modelSpinnerItems;
    EditText vehicleNumberET, copyNumberET;
    CheckBox cb1, cb2, cb3, cb4;
    Button registerVehicleButton;
    ProgressBar progressBar;
    ArrayList<Integer> features;
    ImageView vehicleImage;
    //variables for validation
    String vehicleNumber, vehicleCopyNumber, model;

    //firebase variables
    FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;
    Uri imageUri;
    String downloadUrl;


    //permissions variables
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration2);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        String password = intent.getStringExtra("password");
        String phone = intent.getStringExtra("phone");
        String cnic = intent.getStringExtra("cnic");

        //initializing the variables
        registerVehicleButton = findViewById(R.id.registerVehicleButton);
        modelSpinner = (Spinner) findViewById(R.id.spinnerAmbulance);
        vehicleNumberET = findViewById(R.id.vehicleNoEditTextR2);
        copyNumberET = findViewById(R.id.vehicleCopyNoEditTextR2);
        cb1 = findViewById(R.id.stretcherCheckBox);
        cb2 = findViewById(R.id.dripCheckBox);
        cb3 = findViewById(R.id.oxygenCheckBox);
        cb4 = findViewById(R.id.nurseCheckBox);
        progressBar = findViewById(R.id.progressR2);
        vehicleImage = findViewById(R.id.vehicleImageR2);
        features = new ArrayList<>();

        //initializing firebase variables
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        modelSpinnerItems = new ArrayList<String>();
        modelSpinnerItems.add("Model");

        for (int i = 1990; i < 2023; i++) {
            modelSpinnerItems.add(String.valueOf(i));
        }

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, modelSpinnerItems);
        modelSpinner.setAdapter(adapter1);

        vehicleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if the permissions are granted
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RegistrationActivity2.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
                    Toast.makeText(getApplicationContext(), "Provide the permissions to continue", Toast.LENGTH_SHORT).show();
                } else {
                    //pick image from gallery
                    pickImageFromGallery();
                }
            }
        });

        registerVehicleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show the progress bar
                progressBar.setVisibility(View.VISIBLE);
                registerVehicleButton.setVisibility(View.INVISIBLE);

                //validate the input
                boolean validation = inputValidation();

                if (validation) {
                    //create user at firebase auth
                    auth.createUserWithEmailAndPassword(email, password).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //hide the progress bar
                            progressBar.setVisibility(View.INVISIBLE);
                            registerVehicleButton.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                storageReference = FirebaseStorage.getInstance().getReference(auth.getCurrentUser().getUid());
                                UploadTask uploadTask = storageReference.putFile(imageUri);
                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Task<Uri> getDownloadUrl = taskSnapshot.getStorage().getDownloadUrl();
                                        getDownloadUrl.addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if (task.isSuccessful()) {
                                                    downloadUrl = getDownloadUrl.getResult().toString();

                                                    //create record in database
                                                    String userKey = auth.getCurrentUser().getUid();
                                                    DriverModel driverModel = new DriverModel(userKey, name, email, cnic, phone, downloadUrl, "pending", vehicleNumber, vehicleCopyNumber, model, features, "driver", 0.0, 0.0, false);
                                                    firebaseFirestore.collection("users").document(userKey).set(driverModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            //hide the progress bar
                                                            if (task.isSuccessful()) {
                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                registerVehicleButton.setVisibility(View.VISIBLE);

                                                                Toast.makeText(getApplicationContext(), "Signup successful", Toast.LENGTH_SHORT).show();

                                                                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                                                                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

                                                                //add notification to table
                                                                String uniqueID = UUID.randomUUID().toString();
                                                                NotificationModel notificationModel = new NotificationModel(uniqueID, userKey, "admin", name + " just created an account", email, 3, currentDate, currentTime);
                                                                firebaseFirestore.collection("notifications").document(uniqueID).set(notificationModel);
                                                                sendVerifyEmail();
                                                                finishAffinity();
                                                                startActivity(new Intent(RegistrationActivity2.this, LoginActivity.class));

                                                            }

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            //hide the progress bar
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            registerVehicleButton.setVisibility(View.VISIBLE);
                                                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //hide the progress bar
                                                progressBar.setVisibility(View.INVISIBLE);
                                                registerVehicleButton.setVisibility(View.VISIBLE);
                                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //hide the progress bar
                                        progressBar.setVisibility(View.INVISIBLE);
                                        registerVehicleButton.setVisibility(View.VISIBLE);
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                } else {
                    //hide the progress bar
                    progressBar.setVisibility(View.INVISIBLE);
                    registerVehicleButton.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Check all input fields", Toast.LENGTH_SHORT).show();
                }
            }

            private void sendVerifyEmail() {
                Objects.requireNonNull(auth.getCurrentUser()).sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegistrationActivity2.this, "Please Verify your email to continue.", Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegistrationActivity2.this, "Error connecting to server.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    private boolean inputValidation() {
        vehicleNumber = vehicleNumberET.getText().toString();
        vehicleCopyNumber = copyNumberET.getText().toString();
        model = modelSpinner.getSelectedItem().toString();

        if (vehicleNumber.isEmpty() || vehicleNumber.length() < 3) {
            vehicleNumberET.setError("Enter a correct number");
            vehicleNumberET.requestFocus();
            return false;
        }
        if (vehicleCopyNumber.isEmpty() || vehicleCopyNumber.length() < 6) {
            copyNumberET.setError("Enter a correct copy number");
            copyNumberET.requestFocus();
            return false;
        }
        if (model.equals("Model")) {
            Toast.makeText(getApplicationContext(), "Select a model", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (imageUri == null || imageUri.toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Select ambulance image", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (cb1.isChecked()) {
            features.add(1);
        }
        if (cb2.isChecked()) {
            features.add(2);
        }
        if (cb3.isChecked()) {
            features.add(3);
        }
        if (cb4.isChecked()) {
            features.add(4);
        }
        return true;
    }

    public void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            imageUri = data.getData();
            vehicleImage.setImageURI(imageUri);
        }
    }

    public void uploadImage() {
        UploadTask uploadTask = storageReference.putFile(imageUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> getDownloadUrl = taskSnapshot.getStorage().getDownloadUrl();
                downloadUrl = getDownloadUrl.getResult().toString();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}