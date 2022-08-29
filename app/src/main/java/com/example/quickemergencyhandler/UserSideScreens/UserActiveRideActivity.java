package com.example.quickemergencyhandler.UserSideScreens;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.example.quickemergencyhandler.R;
import com.example.quickemergencyhandler.models.Booking;
import com.example.quickemergencyhandler.models.NotificationModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class UserActiveRideActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    CardView cardView;
    TextView nameTV, vehicleNumberTV;
    Button cancelBookingButton, homeButton;
    String driverKey, userKey;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    SupportMapFragment mapFragment;
    TextView noRidesTV;
    boolean rideFound = false;
    private double radiusInMeters = 0.02 * 1000.0; //20 meter
    private Handler mHandler;
    int v = 0;
    Date d1, d2;
    private LatLng latLng;
    private static long INTERAl = 10000000;
    private static long FAST_INTERVAL = 600000000;
    private int mInterval = 2000;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location lastLocation;
    private GoogleMap mMap;
    private Marker currentLocationMarker, driverMarker;
    private LocationRequest locationRequest;
    double dlat, dLng;
    private boolean hideMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_active_ride);

        cardView = findViewById(R.id.cardViewUserActiveRide);
        nameTV = findViewById(R.id.nameUserActiveRide);
        vehicleNumberTV = findViewById(R.id.numberUserActiveRide);
        cancelBookingButton = findViewById(R.id.cancelButtonUserActiveRide);
        homeButton = findViewById(R.id.returnHome);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userKey = firebaseAuth.getCurrentUser().getUid();

        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.RECORD_AUDIO
                ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                }).check();
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(UserActiveRideActivity.this, PatientDashboardActivity.class);
                startActivity(in);
            }
        });
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragmentActiveRideUser);
        mapFragment.getMapAsync(this);

        mHandler = new Handler();
        startRepeatingTask();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                //mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(dlat, dLng), 15));
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERAl);
        mLocationRequest.setFastestInterval(FAST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (mGoogleApiClient.isConnected()) {
            if (ContextCompat.checkSelfPermission(this,
                    ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                v++;
                //firebase conditions
                loadMap();
                Log.d("GET_TIMER", "run: " + v);//this function can change value of mInterval.
            } finally {
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    boolean toastShown = false;

    private void loadMap() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("running map");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                try {
                    d1 = dateFormat.parse(currentDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                firebaseFirestore.collection("Booking").whereEqualTo("status", "accepted").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot snapshot : task.getResult()) {
                                String fetchedDate = snapshot.get("date").toString();
                                try {
                                    d2 = dateFormat.parse(fetchedDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (d1.compareTo(d2) == 0){

                                    Log.d("condition d1 d2","true");
                                }
                                if (d1.compareTo(d2) == 0 &&
                                        FirebaseAuth.getInstance().getCurrentUser().getUid().equals(snapshot.get("userID").toString())
                                        &&
                                        snapshot.get("status").toString().equals("accepted")
                                ) {

                                    rideFound = true;
                                    driverKey = snapshot.get("driverID").toString();

                                    //fetch driver name and vehicle number for TVs
                                    firebaseFirestore.collection("users").document(driverKey).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot snapshot1 = task1.getResult();
                                                nameTV.setText("Driver Name: " + snapshot1.get("name").toString());
                                                vehicleNumberTV.setText("Vehicle Number: " + snapshot1.get("vehicleNumber").toString());
                                            }
                                        }
                                    });

                                    firebaseFirestore.collection("Booking").whereEqualTo("driverID", driverKey).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> driverTask) {

                                            if (driverTask.isSuccessful()) {


                                                if (d1.compareTo(d2) == 0 &&
                                                        FirebaseAuth.getInstance().getCurrentUser().getUid().equals(snapshot.get("userID").toString())
                                                        &&
                                                        snapshot.get("status").toString().equals("accepted")
                                                ) {
                                                    cancelBookingButton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            String bookingID = snapshot.get("bookingID").toString();
                                                            Booking cancelBooking = new Booking(
                                                                    bookingID,
                                                                    snapshot.get("userID").toString(),
                                                                    snapshot.get("driverID").toString(),
                                                                    Double.parseDouble(snapshot.get("userLat").toString()),
                                                                    Double.parseDouble(snapshot.get("userLng").toString()),
                                                                    "rejected",
                                                                    snapshot.get("date").toString(),
                                                                    snapshot.get("time").toString(),
                                                                    Double.parseDouble(snapshot.get("driverLat").toString()),
                                                                    Double.parseDouble(snapshot.get("driverLng").toString()),
                                                                    0
                                                            );
                                                            firebaseFirestore.collection("Booking").document(bookingID).set(cancelBooking);

                                                            //send notification to table
                                                            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                                                            String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                                                            String uniqueID = UUID.randomUUID().toString();
                                                            NotificationModel notificationModel = new NotificationModel(uniqueID, firebaseAuth.getCurrentUser().getUid(), snapshot.get("driverID").toString(), "You ride has been cancelled", "Date: " + currentDate + " ,Time: " + currentTime, 2, currentDate, currentTime);
                                                            firebaseFirestore.collection("notifications").document(uniqueID).set(notificationModel);

                                                            Toast.makeText(getApplicationContext(), "The ride has been cancelled", Toast.LENGTH_LONG).show();

                                                            Intent intent2 = new Intent(UserActiveRideActivity.this, PatientDashboardActivity.class);
                                                            finishAffinity();
                                                            startActivity(intent2);
                                                        }
                                                    });
                                                    for (DocumentSnapshot snapshot : driverTask.getResult()) {
                                                        dlat = Double.parseDouble(snapshot.get("driverLat").toString());
                                                        dLng = Double.parseDouble(snapshot.get("driverLng").toString());

                                                        System.out.println("Location driver " + dlat + " " + dLng);
                                                        // loadMap(dlat, dLng);
                                                        if (driverMarker != null) {
                                                            driverMarker.remove();
                                                            hideMarker = false;
                                                           // currentLocationMarker.setVisible(false);
                                                        } else {
                                                            hideMarker = true;
                                                        }

                                                        final Handler handler = new Handler();
                                                        final long start = SystemClock.uptimeMillis();
                                                        Projection proj = mMap.getProjection();
                                                        Point startPoint = proj.toScreenLocation(new LatLng(dlat, dLng));
                                                        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
                                                        final long duration = 50;

                                                        final Interpolator interpolator = new LinearInterpolator();

                                                        handler.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                long elapsed = SystemClock.uptimeMillis() - start;
                                                                float t = interpolator.getInterpolation((float) elapsed
                                                                        / duration);
                                                                double lng = t * dLng + (1 - t)
                                                                        * startLatLng.longitude;
                                                                double lat = t * dlat + (1 - t)
                                                                        * startLatLng.latitude;
                                                                driverMarker.setPosition(new LatLng(lat, lng));

                                                                if (t < 1.0) {
                                                                    // Post again 16ms later.
                                                                    handler.postDelayed(this, 16);
                                                                } else {
                                                                    if (hideMarker) {
                                                                        driverMarker.setVisible(false);
                                                                    } else {
                                                                        driverMarker.setVisible(true);
                                                                    }
                                                                }
                                                            }
                                                        });

                                                        MarkerOptions markerOptions = new MarkerOptions();
                                                        markerOptions.position(new LatLng(dlat, dLng));
                                                        markerOptions.title("Driver Location");
                                                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                                        driverMarker = mMap.addMarker(markerOptions);
                                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(dlat, dLng), 15));
                                                    }
                                                }
                                                else{
                                                    System.out.println("condition out " + driverTask.getResult());
                                                  //  Toast.makeText(UserActiveRideActivity.this, "Out of condition", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                System.out.println("record not found: " + driverTask.getResult());
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            System.out.println(e.getMessage());
                                        }
                                    });
                                }
                            }
                            if (!rideFound && !toastShown) {
                                toastShown = true;
                                Toast.makeText(getApplicationContext(), "You have no active ride currently", Toast.LENGTH_LONG).show();
                                cardView.setVisibility(View.INVISIBLE);
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
        }, 100);

    }

    void startRepeatingTask() {
        mStatusChecker.run();
    }

}