package com.example.quickemergencyhandler.UserSideScreens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

import com.example.quickemergencyhandler.AdminScreens.AdminLogin;
import com.example.quickemergencyhandler.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    //creating variables
    TextView registerTextView, loginAsAdminTextView, getHelp;
    Button loginButton;
    ImageView showPasswordButton, helpButton;
    EditText emailEditText, passwordEditText;
    ProgressBar progressBar;
    String email, password;

    //firebase variables
    FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore;
    DocumentReference databaseReference;

    //shared prefs variables
    SharedPreferences sharedPreferences;
    public static final String SHARED_PREFS = "SharedPrefs";
    public static final String userType = "userType";
    public static final String userNodeKey = "userKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initializing the variables
        registerTextView = (TextView) findViewById(R.id.registerHere);
        loginAsAdminTextView = (TextView) findViewById(R.id.loginAsAdminTextView);
        loginButton = (Button) findViewById(R.id.loginButton);
        emailEditText = findViewById(R.id.emailEditTextL);
        passwordEditText = findViewById(R.id.passwordEditTextL);
        getHelp = findViewById(R.id.getHelp);
        progressBar = findViewById(R.id.LoginProgressBar);
        showPasswordButton = findViewById(R.id.showPasswordLoginScreen);
        helpButton = findViewById(R.id.helpL);
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        showPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passwordEditText.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:03013073514"));
                startActivity(callIntent);
            }
        });

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.INVISIBLE);
                getHelp.setVisibility(View.INVISIBLE);

                //extract the input from edit texts and validate it
                boolean validation = extractInputAndValidate();
                if (validation) {
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if (auth.getCurrentUser().isEmailVerified()) {
                                    //check the type of user
                                    String userKey = auth.getCurrentUser().getUid().toString();
                                    databaseReference = firebaseFirestore.collection("users").document(userKey);
                                    databaseReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                //hide the progress bar
                                                progressBar.setVisibility(View.INVISIBLE);
                                                loginButton.setVisibility(View.VISIBLE);
                                                getHelp.setVisibility(View.VISIBLE);

                                                //get the result
                                                DocumentSnapshot snapshot = task.getResult();

                                                //check if the user is blocked or pending
                                                if (snapshot.get("status").toString().equals("blocked") || snapshot.get("status").toString().equals("pending")) {
                                                    Toast.makeText(getApplicationContext(), "Your status is not approved yet, wait for the approval from admin", Toast.LENGTH_LONG).show();
                                                    auth.signOut();
                                                    return;
                                                }

                                                if (snapshot.get("userType").toString().equals("driver")) {
                                                    editor.putInt(userType, 1);
                                                    editor.putString(userNodeKey, userKey);
                                                    editor.apply();
                                                    Intent intent = new Intent(LoginActivity.this, DriverDashboardActivity.class);
                                                    startActivity(intent);
                                                    finish();

                                                } else if (snapshot.get("userType").toString().equals("patient")) {
                                                    editor.putInt(userType, 2);
                                                    editor.putString(userNodeKey, userKey);
                                                    editor.apply();
                                                    Intent intent = new Intent(LoginActivity.this, PatientDashboardActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {

                                                    Toast.makeText(getApplicationContext(), "User not found, check the credentials or connection", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //hide the progress bar
                                            progressBar.setVisibility(View.INVISIBLE);
                                            loginButton.setVisibility(View.VISIBLE);
                                            getHelp.setVisibility(View.VISIBLE);

                                            //show message
                                            Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    loginButton.setVisibility(View.VISIBLE);
                                    getHelp.setVisibility(View.VISIBLE);
                                    Toast.makeText(LoginActivity.this, "Please verify email to continue", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //hide the progress bar
                            progressBar.setVisibility(View.INVISIBLE);
                            loginButton.setVisibility(View.VISIBLE);
                            getHelp.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    loginButton.setVisibility(View.VISIBLE);
                    getHelp.setVisibility(View.VISIBLE);
                }
            }
        });

        getHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        loginAsAdminTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, AdminLogin.class);
                startActivity(intent);
            }
        });
    }

    private boolean extractInputAndValidate() {
        //extract input
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();

        //validate input
        if (email.isEmpty()) {
            emailEditText.setError("Email can't be empty");
            emailEditText.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Provide a correct email");
            emailEditText.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            passwordEditText.setError("Password can't be empty");
            passwordEditText.requestFocus();
            return false;
        }
        return true;
    }

    void showBottomSheet() {
        //initializing the bottom sheet
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_login_screen);

        //creating the variables of the bottom sheet
        Button registerAsDriverButton, registerAsPatientButton;
        TextView cancelTextView;

        //initializing the variables of the bottom sheet
        registerAsDriverButton = (Button) bottomSheetDialog.findViewById(R.id.registerAsDriverButton);
        registerAsPatientButton = (Button) bottomSheetDialog.findViewById(R.id.registerAsPatientButton);
        cancelTextView = (TextView) bottomSheetDialog.findViewById(R.id.cancel);

        bottomSheetDialog.show();

        //setting on click listeners
        registerAsDriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity1.class);
                intent.putExtra("type", "driver");
                bottomSheetDialog.hide();
                startActivity(intent);
            }
        });

        registerAsPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity1.class);
                intent.putExtra("type", "patient");
                bottomSheetDialog.hide();
                startActivity(intent);
            }
        });

        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.hide();
            }
        });
    }

}