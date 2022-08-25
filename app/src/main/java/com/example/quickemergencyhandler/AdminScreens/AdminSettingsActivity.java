package com.example.quickemergencyhandler.AdminScreens;

import static java.lang.Integer.parseInt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.quickemergencyhandler.R;
import com.example.quickemergencyhandler.models.AdminModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminSettingsActivity extends AppCompatActivity {

    EditText baseFareET, perKmET;
    ProgressBar progressBar;
    Button applyButton;
    int baseFare, perKmFare;
    AdminModel adminModel;

    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_settings);

        baseFareET = findViewById(R.id.baseFareET);
        perKmET = findViewById(R.id.perKmET);
        applyButton = findViewById(R.id.applySettingsButton);
        progressBar = findViewById(R.id.progressAdminSettings);
        firebaseFirestore = FirebaseFirestore.getInstance();

        //get default values from database
        firebaseFirestore.collection("Admin").document("Admin").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //fetch data
                adminModel = documentSnapshot.toObject(AdminModel.class);

                //set values to edit texts
                baseFareET.setText(String.valueOf(adminModel.getBaseFare()));
                perKmET.setText(String.valueOf(adminModel.getPerKmFare()));
            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show the progress bar
                progressBar.setVisibility(View.VISIBLE);
                applyButton.setVisibility(View.INVISIBLE);

                //get values
                String base,per;
                base = baseFareET.getText().toString();
                per = perKmET.getText().toString();

                //validate values
                if(base.trim().equals("") || per.trim().equals(""))
                {
                    //hide the progress bar
                    progressBar.setVisibility(View.INVISIBLE);
                    applyButton.setVisibility(View.VISIBLE);

                    //show the message
                    Toast.makeText(getApplicationContext(), "Fill both fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                //set values to database
                AdminModel adminModel1 = new AdminModel(adminModel.getEmail(), adminModel.getPassword(), parseInt(base), parseInt(per));
                firebaseFirestore.collection("Admin").document("Admin").set(adminModel1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            //hide the progress bar
                            progressBar.setVisibility(View.INVISIBLE);
                            applyButton.setVisibility(View.VISIBLE);

                            //show message
                            Toast.makeText(getApplicationContext(), "Settings updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}