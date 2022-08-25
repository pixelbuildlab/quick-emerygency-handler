package com.example.quickemergencyhandler.UserSideScreens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quickemergencyhandler.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PatientProfileActivity extends AppCompatActivity {

    TextView nameTV, emailTV, phoneTV, cnicTV, statusTV;
    ProgressBar progressBar;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String userKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);

        nameTV = findViewById(R.id.nameTVPProfile);
        emailTV = findViewById(R.id.emailTVPProfile);
        phoneTV = findViewById(R.id.phoneTVPProfile);
        cnicTV = findViewById(R.id.cnicTVPProfile);
        statusTV = findViewById(R.id.statusTVPProfile);
        progressBar = findViewById(R.id.progressPProfile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userKey = firebaseAuth.getCurrentUser().getUid();

        //get user data from firebase
        progressBar.setVisibility(View.VISIBLE);
        firebaseFirestore.collection("users").document(userKey).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                progressBar.setVisibility(View.INVISIBLE);
                if(task.isSuccessful())
                {
                    DocumentSnapshot snapshot = task.getResult();
                    String name = snapshot.get("name").toString();
                    String email = snapshot.get("email").toString();
                    String phone = snapshot.get("phoneNo").toString();
                    String cnic = snapshot.get("cnic").toString();
                    String status = snapshot.get("status").toString();

                    nameTV.setText("Name: " + name);
                    emailTV.setText("Email: " + email);
                    phoneTV.setText("Phone: " + phone);
                    cnicTV.setText("CNIC: " + cnic);
                    statusTV.setText("Status: " + status);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}