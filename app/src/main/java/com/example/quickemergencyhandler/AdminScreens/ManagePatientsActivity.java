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
import com.example.quickemergencyhandler.adapters.AdapterManagePatientsRecycler;
import com.example.quickemergencyhandler.interfaces.IManagePatientClickListener;
import com.example.quickemergencyhandler.models.PatientModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ManagePatientsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AdapterManagePatientsRecycler adapter;
    ArrayList<PatientModel> items;
    ProgressBar progressBar;

    CollectionReference databaseReference;
    FirebaseFirestore firestore;

    String id, name, email, cnic, phone, status, userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_patients);

        recyclerView = findViewById(R.id.recyclerViewManagePatients);
        progressBar = findViewById(R.id.progressManageP);
        items = new ArrayList<>();

        //fetch data from database
        fetchData(getApplicationContext());
    }

    public void fetchData(Context ctx)
    {
        //show the progress bar
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);

        //fetch data
        firestore = FirebaseFirestore.getInstance();
        databaseReference = firestore.collection("users");

        databaseReference.whereEqualTo("userType", "patient").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //hide the progress bar
                progressBar.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);

                if(task.isSuccessful())
                {
                    for(QueryDocumentSnapshot document : task.getResult())
                    {
                        id = document.get("id").toString();
                        name = document.get("name").toString();
                        email = document.get("email").toString();
                        cnic = document.get("cnic").toString();
                        phone = document.get("phoneNo").toString();
                        status = document.get("status").toString();
                        userType = document.get("userType").toString();
                        items.add(new PatientModel(id ,name, email, cnic, phone, status, userType, 0, 0, 0));
                    }

                    adapter = new AdapterManagePatientsRecycler(items, getApplicationContext(), new IManagePatientClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            Intent intent = new Intent(ManagePatientsActivity.this, PatientDetailActivity.class);
                            intent.putExtra("id", items.get(position).getId());
                            intent.putExtra("name", items.get(position).getName());
                            intent.putExtra("email", items.get(position).getEmail());
                            intent.putExtra("cnic", items.get(position).getCnic());
                            intent.putExtra("phone", items.get(position).getPhoneNo());
                            intent.putExtra("status", items.get(position).getStatus());
                            intent.putExtra("userType", items.get(position).getUserType());
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