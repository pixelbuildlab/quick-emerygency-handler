package com.example.quickemergencyhandler.UserSideScreens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.example.quickemergencyhandler.R;
import com.example.quickemergencyhandler.adapters.AdapterLvDriverReviews;
import com.example.quickemergencyhandler.models.History;
import com.example.quickemergencyhandler.models.Rating;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DriverReviewsActivity extends AppCompatActivity {

    ListView listView;
    AdapterLvDriverReviews adapter;
    ArrayList<Rating> items;
    ArrayList<History> histories;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String driverID;

    String date, time, cost, userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_reviews);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        driverID = firebaseAuth.getCurrentUser().getUid();

        histories = new ArrayList<>();
        listView = findViewById(R.id.driverReviewsLV);
        items = new ArrayList<>();

        firebaseFirestore.collection("Rating").whereEqualTo("driverID", driverID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    if(task.getResult().size() > 0)
                    {
                        for(DocumentSnapshot snapshot : task.getResult())
                        {
                            History history = new History();

                            //get rating stars and comment
                            float ratingValue = Float.parseFloat(snapshot.get("rating").toString());
                            String comment = snapshot.get("comment").toString();
                            history.setRatingValue(ratingValue);
                            history.setComment(comment);
                            //Rating rating = new Rating("", "", "", comment, ratingValue);
                            System.out.println("got rating and comment");
                            //get date time and cost
                            firebaseFirestore.collection("Booking").document(snapshot.get("bookingID").toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                                    if(task.isSuccessful())
                                    {
                                        DocumentSnapshot snapshot1 = task1.getResult();
                                        date = snapshot1.get("date").toString();
                                        time = snapshot1.get("time").toString();
                                        cost = snapshot1.get("cost").toString();
                                        history.setDate(date);
                                        history.setTime(time);
                                        history.setCharges(cost);
                                        System.out.println("got date time and cost");

                                        String userID = snapshot1.get("userID").toString();
                                        //get user name
                                        firebaseFirestore.collection("users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful())
                                                {
                                                    DocumentSnapshot snapshot2 = task.getResult();
                                                    userName = snapshot2.get("name").toString();
                                                    history.setUserName(userName);
                                                    histories.add(history);
                                                    System.out.println("got user name");
                                                    System.out.println("History added");
                                                    adapter = new AdapterLvDriverReviews(getApplicationContext(), histories);
                                                    listView.setAdapter(adapter);
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "You have no reviews yet", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}