package com.example.quickemergencyhandler.AdminScreens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quickemergencyhandler.R;
import com.example.quickemergencyhandler.models.PatientModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class PatientDetailActivity extends AppCompatActivity {

    String id, name, email, cnic, phone, status, userType;
    TextView nameTV, emailTV, cnicTV, phoneTV, statusTV;
    Button blockButton;
    PatientModel updatedPatient;
    ProgressBar progressBar;

    //firebase variables
    FirebaseFirestore firebaseFirestore;
    CollectionReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_detail);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        name = intent.getStringExtra("name");
        email = intent.getStringExtra("email");
        cnic = intent.getStringExtra("cnic");
        phone = intent.getStringExtra("phone");
        status = intent.getStringExtra("status");
        userType = intent.getStringExtra("userType");

        //initialize text views
        nameTV = findViewById(R.id.nameTVPDetails);
        emailTV = findViewById(R.id.emailTVPDetails);
        cnicTV = findViewById(R.id.cnicTVPDetails);
        phoneTV = findViewById(R.id.phoneTVPDetails);
        statusTV = findViewById(R.id.statusTVPDetails);
        blockButton = findViewById(R.id.blockPatientButton);
        progressBar = findViewById(R.id.progressManagePDetails);
        firebaseFirestore = FirebaseFirestore.getInstance();
        databaseReference = firebaseFirestore.collection("users");

        //set text to views
        nameTV.setText(name);
        emailTV.setText(email);
        cnicTV.setText(cnic);
        phoneTV.setText(phone);
        statusTV.setText("Status:  "+status);

        if(status.equals("blocked"))
        {
            statusTV.setTextColor(getResources().getColor(R.color.dark_red));
            blockButton.setText("Unblock User");
        }
        else
        {
            updatedPatient = new PatientModel(id, name, email, cnic, phone, "blocked", userType, 0, 0, 0);
            statusTV.setTextColor(getResources().getColor(R.color.g_green));
            blockButton.setText("Block User");
        }

        blockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show the progress bar
                progressBar.setVisibility(View.VISIBLE);
                blockButton.setVisibility(View.INVISIBLE);

                if(status.equals("blocked"))
                {
                    updatedPatient = new PatientModel(id, name, email, cnic, phone, "approved", userType, 0, 0, 0);

                    databaseReference.document(id).set(updatedPatient).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                //change local status
                                status = "approved";
                                statusTV.setText("Status:  "+status);
                                statusTV.setTextColor(getResources().getColor(R.color.g_green));
                                blockButton.setText("Block User");

                                //hide the progress bar
                                progressBar.setVisibility(View.INVISIBLE);
                                blockButton.setVisibility(View.VISIBLE);

                                //show success message
                                Toast.makeText(getApplicationContext(), "User status updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //hide the progress bar
                            progressBar.setVisibility(View.INVISIBLE);
                            blockButton.setVisibility(View.VISIBLE);

                            //show failure message
                            Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                if(status.equals("approved"))
                {
                    updatedPatient = new PatientModel(id, name, email, cnic, phone, "blocked", userType, 0, 0, 0);

                    databaseReference.document(id).set(updatedPatient).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                //change local status
                                status = "blocked";
                                statusTV.setText("Status:  "+status);
                                statusTV.setTextColor(getResources().getColor(R.color.dark_red));
                                blockButton.setText("Unblock User");

                                //hide the progress bar
                                progressBar.setVisibility(View.INVISIBLE);
                                blockButton.setVisibility(View.VISIBLE);

                                //show success message
                                Toast.makeText(getApplicationContext(), "User status updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //hide the progress bar
                            progressBar.setVisibility(View.INVISIBLE);
                            blockButton.setVisibility(View.VISIBLE);

                            //show failure message
                            Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}