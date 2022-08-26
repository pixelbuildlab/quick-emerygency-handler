package com.example.quickemergencyhandler.UserSideScreens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quickemergencyhandler.R;
import com.example.quickemergencyhandler.models.Rating;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class UserReviewActivity extends AppCompatActivity {

    String userID, driverID, bookingID;

    RatingBar ratingBar;
    Button submitButton;
    EditText reviewET;
    TextView alreadyRatedTV, headingTV;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_review);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        driverID = intent.getStringExtra("driverID");
        bookingID = intent.getStringExtra("bookingID");

        ratingBar = findViewById(R.id.ratingBar);
        submitButton = findViewById(R.id.giveReviewButton);
        reviewET = findViewById(R.id.ratingTextArea);
        alreadyRatedTV = findViewById(R.id.alreadyRatedTV);
        headingTV = findViewById(R.id.heading12);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("Rating").whereEqualTo("bookingID", bookingID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        alreadyRatedTV.setVisibility(View.VISIBLE);
                        ratingBar.setVisibility(View.INVISIBLE);
                        submitButton.setVisibility(View.INVISIBLE);
                        reviewET.setVisibility(View.INVISIBLE);
                        headingTV.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation()) {
                    String comment = reviewET.getText().toString().trim();
                    float ratingValue = ratingBar.getRating();
                    Rating rating = new Rating(bookingID, userID, driverID, comment, ratingValue);
                    firebaseFirestore.collection("Rating").document(bookingID).set(rating).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Review submitted", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

    }

    private boolean validation() {
        if (reviewET.getText().toString().trim().equals("")) {
            Toast.makeText(getApplicationContext(), "Review field can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (reviewET.getText().toString().trim().length() < 6) {
            Toast.makeText(getApplicationContext(), "Review too short", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}