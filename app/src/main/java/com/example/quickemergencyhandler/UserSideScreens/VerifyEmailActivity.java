package com.example.quickemergencyhandler.UserSideScreens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.quickemergencyhandler.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class VerifyEmailActivity extends AppCompatActivity {

    Button verifyEmailButton, signOutButton;
    ProgressBar progressBar;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        verifyEmailButton=findViewById(R.id.sendVerifyEmail);
        signOutButton=findViewById(R.id.signOut);

        progressBar = findViewById(R.id.verifyEmailProgressBar);
        auth=FirebaseAuth.getInstance();




        verifyEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (auth.getCurrentUser()!= null){
                    //             progressBar.setVisibility(View.VISIBLE);
                    verifyEmailButton.setVisibility(View.INVISIBLE);
                    signOutButton.setVisibility(View.INVISIBLE);
                    auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                       //     progressBar.setVisibility(View.INVISIBLE);
                            verifyEmailButton.setVisibility(View.VISIBLE);
                            Toast.makeText(VerifyEmailActivity.this, "Verification email sent.", Toast.LENGTH_SHORT).show();
                            Intent i= new Intent(VerifyEmailActivity.this, LoginActivity.class);
                            startActivity(i);
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                       //     progressBar.setVisibility(View.INVISIBLE);
                            verifyEmailButton.setVisibility(View.VISIBLE);
                            signOutButton.setVisibility(View.VISIBLE);
                            Toast.makeText(VerifyEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
               // progressBar.setVisibility(View.INVISIBLE);
                verifyEmailButton.setVisibility(View.VISIBLE);
                signOutButton.setVisibility(View.VISIBLE);

            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  progressBar.setVisibility(View.VISIBLE);
                verifyEmailButton.setVisibility(View.INVISIBLE);
                signOutButton.setVisibility(View.INVISIBLE);
                if (auth.getCurrentUser()!= null){
                    Toast.makeText(VerifyEmailActivity.this, "Signed out successfully.", Toast.LENGTH_SHORT).show();
                    auth.signOut();
                }
             //   progressBar.setVisibility(View.INVISIBLE);
                verifyEmailButton.setVisibility(View.VISIBLE);
                signOutButton.setVisibility(View.VISIBLE);
                Intent i= new Intent(VerifyEmailActivity.this, LoginActivity.class);
                finish();
                startActivity(i);
            }
        });
    }
}