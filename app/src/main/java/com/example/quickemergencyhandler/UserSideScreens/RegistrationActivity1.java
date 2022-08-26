package com.example.quickemergencyhandler.UserSideScreens;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quickemergencyhandler.R;
import com.example.quickemergencyhandler.models.NotificationModel;
import com.example.quickemergencyhandler.models.PatientModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class RegistrationActivity1 extends AppCompatActivity {

    //creating variables
    TextView loginInsteadTextView;
    Button registerButton;
    ImageView showPassword;
    EditText nameEditText, emailEditText, passwordEditText, phoneEditText, cnicEditText;
    ProgressBar progressBar;

    //variables for validation and intent
    String name, email, password, phone, cnic;

    //firebase variables
    FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore;

    //permissions variables
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration1);

        //get the type of registration
        Intent intent = getIntent();
        String userType = intent.getStringExtra("type"); //driver or patient

        //initializing the variables
        loginInsteadTextView = (TextView) findViewById(R.id.loginInstead);
        registerButton = (Button) findViewById(R.id.registerButton);
        nameEditText = findViewById(R.id.nameEditTextR1);
        emailEditText = findViewById(R.id.emailEditTextR1);
        passwordEditText = findViewById(R.id.passwordEditTextR1);
        phoneEditText = findViewById(R.id.phoneEditTextR1);
        showPassword = findViewById(R.id.showPasswordR1);
        cnicEditText = findViewById(R.id.CNICEditTextR1);
        progressBar = findViewById(R.id.progressR1);

        //initializing firebase variables
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passwordEditText.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        //setting the on click listeners
        loginInsteadTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

//        userImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //check if the permissions are granted
//                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(RegistrationActivity1.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
//                    Toast.makeText(getApplicationContext(), "Provide the permissions to continue", Toast.LENGTH_SHORT).show();
//                }
//                else
//                {
//                    //pick image from gallery
//                    pickImageFromGallery();
//                }
//            }
//        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show the progress bar
                progressBar.setVisibility(View.VISIBLE);
                registerButton.setVisibility(View.INVISIBLE);
                loginInsteadTextView.setVisibility(View.INVISIBLE);

                //validate the input
                boolean validation = inputValidation();

                //check the user type
                //if user type is driver then move to 2nd registration screen
                //else move to main screen
                if (validation) {
                    if (userType.equals("driver")) {
                        Intent intent = new Intent(RegistrationActivity1.this, RegistrationActivity2.class);
                        intent.putExtra("name", name);
                        intent.putExtra("email", email);
                        intent.putExtra("password", password);
                        intent.putExtra("phone", phone);
                        intent.putExtra("cnic", cnic);

                        //hide the progress bar
                        progressBar.setVisibility(View.INVISIBLE);
                        registerButton.setVisibility(View.VISIBLE);
                        loginInsteadTextView.setVisibility(View.VISIBLE);

                        startActivity(intent);
                    } else if (userType.equals("patient")) {
                        auth.createUserWithEmailAndPassword(email, password).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //hide the progress bar
                                progressBar.setVisibility(View.INVISIBLE);
                                registerButton.setVisibility(View.VISIBLE);
                                loginInsteadTextView.setVisibility(View.VISIBLE);
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    //create record in database
                                    PatientModel patientModel = new PatientModel(auth.getCurrentUser().getUid().toString(), name, email, cnic, phone, "approved", "patient", 1, 0, 0);
                                    firebaseFirestore.collection("users").document(auth.getCurrentUser().getUid()).set(patientModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //hide the progress bar
                                                progressBar.setVisibility(View.INVISIBLE);
                                                registerButton.setVisibility(View.VISIBLE);
                                                loginInsteadTextView.setVisibility(View.VISIBLE);


                                                //send notification in table
                                                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                                                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                                                String uniqueID = UUID.randomUUID().toString();
                                                NotificationModel notificationModel = new NotificationModel(uniqueID, auth.getCurrentUser().getUid().toString(), "admin", name + " just created an account", email, 3, currentDate, currentTime);
                                                firebaseFirestore.collection("notifications").document(uniqueID).set(notificationModel);
                                                Toast.makeText(getApplicationContext(), "User created in database", Toast.LENGTH_SHORT).show();
                                                sendVerifyEmail();
                                            }
                                            //hide the progress bar

                                            //Toast.makeText(getApplicationContext(), "User created in database", Toast.LENGTH_SHORT).show();
                                            finishAffinity();

                                            startActivity(new Intent(RegistrationActivity1.this, LoginActivity.class));
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //hide the progress bar
                                            progressBar.setVisibility(View.INVISIBLE);
                                            registerButton.setVisibility(View.VISIBLE);
                                            loginInsteadTextView.setVisibility(View.VISIBLE);
                                            Toast.makeText(getApplicationContext(), "User not created in database " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Check all input fields", Toast.LENGTH_SHORT).show();
                    //hide the progress bar
                    progressBar.setVisibility(View.INVISIBLE);
                    registerButton.setVisibility(View.VISIBLE);
                    loginInsteadTextView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void sendVerifyEmail() {
        Objects.requireNonNull(auth.getCurrentUser()).sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegistrationActivity1.this, "Please Verify your email to continue.", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegistrationActivity1.this, "Error connecting to server.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                    pickImageFromGallery();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied..", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE)
//        {
//            userImage.setImageURI(data.getData());
//        }
//    }

    private boolean inputValidation() {
        name = nameEditText.getText().toString();
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
        phone = phoneEditText.getText().toString();
        cnic = cnicEditText.getText().toString();

        if (name.isEmpty() || name.length() < 5) {
            nameEditText.setError("Name too short");
            nameEditText.requestFocus();
            return false;
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Provide a correct email");
            emailEditText.requestFocus();
            return false;
        }
        if (password.isEmpty() || password.length() < 6) {
            passwordEditText.setError("At least 6 characters required");
            passwordEditText.requestFocus();
            return false;
        }
        if (phone.isEmpty() || phone.length() < 11) {
            phoneEditText.setError("Phone number not correct");
            phoneEditText.requestFocus();
            return false;
        }
        if (cnic.isEmpty() || cnic.length() < 13) {
            cnicEditText.setError("CNIC not correct");
            cnicEditText.requestFocus();
            return false;
        } else if (cnic.length() > 13) {
            cnicEditText.setError("Provide 13 Digit CNIC without dashes");
            cnicEditText.requestFocus();
            return false;
        }
        return true;
    }

}