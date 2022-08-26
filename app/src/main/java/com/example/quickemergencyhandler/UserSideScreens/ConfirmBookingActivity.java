package com.example.quickemergencyhandler.UserSideScreens;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quickemergencyhandler.R;
import com.example.quickemergencyhandler.directionHelpers.DirectionsJSONParser;
import com.example.quickemergencyhandler.models.Booking;
import com.example.quickemergencyhandler.models.NotificationModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ConfirmBookingActivity extends FragmentActivity implements OnMapReadyCallback {

    //variable for views
    TextView estimatedCostTV, vehicleNoTV;
    ImageView callNowButton;
    Button confirmBookingButton, cancelButton;
    ProgressBar progressBar;
    String currentUserName = " ";

    //variables for intent & processing
    String selectedDriverID, name, phoneNo, vehicleNo;
    ArrayList<Integer> features;
    double userLat, userLng, driverLat, driverLng, distanceInKm;
    int baseFare, perKmFare, estimatedCost;
    Booking booking;

    //variables for map
    SupportMapFragment supportMapFragment;
    GoogleMap googleMap;
    //MarkerOptions userPlace, driverPlace;
    Polyline currentPolyline;
    ArrayList<LatLng> mMarkerPoints;
    int REQUEST_CODE = 1;
    private int PERMISSION_CODE = 1;
    private LatLng mOrigin;
    private LatLng mDestination;
    private Polyline mPolyline;

    //variables for firebase
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_booking);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragmentConfirmBooking);
        supportMapFragment.getMapAsync(this);

        confirmBookingButton = findViewById(R.id.confirmBookingButton);
        cancelButton = findViewById(R.id.cancelButton);
        estimatedCostTV = findViewById(R.id.estimatedCost);
        vehicleNoTV = findViewById(R.id.vehicleNumberConfirm);
        callNowButton = findViewById(R.id.callConfirm);
        progressBar = findViewById(R.id.confirmBookingProgressBar);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        features = new ArrayList<>();
        mMarkerPoints = new ArrayList<>();

        Intent intent = getIntent();
        selectedDriverID = intent.getStringExtra("id");
        name = intent.getStringExtra("name");
        phoneNo = intent.getStringExtra("phoneNo");
        vehicleNo = intent.getStringExtra("vehicleNo");
        features = intent.getIntegerArrayListExtra("features");
        userLat = intent.getDoubleExtra("userLat", 0);
        userLng = intent.getDoubleExtra("userLng", 0);
        driverLat = intent.getDoubleExtra("driverLat", 0);
        driverLng = intent.getDoubleExtra("driverLng", 0);
        distanceInKm = intent.getDoubleExtra("distance", 0);

        getCurrentUserName();

        //get the value of base fare and per km fare to calculate estimated cost
        getFareValues();

        vehicleNoTV.setText("Ambulance Number: " + vehicleNo);

        callNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phoneNo));
                startActivity(callIntent);
            }
        });

        confirmBookingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uniqueID = UUID.randomUUID().toString();
                String userID = firebaseAuth.getCurrentUser().getUid();
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                booking = new Booking(uniqueID, userID, selectedDriverID, userLat, userLng, "pending", currentDate, currentTime, driverLat, driverLng, estimatedCost);
                firebaseFirestore.collection("Booking").document(uniqueID).set(booking).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Ride request submitted, wait for the driver", Toast.LENGTH_LONG).show();

                            //send notification to table
                            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                            String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                            String uniqueID = UUID.randomUUID().toString();
                            NotificationModel notificationModel = new NotificationModel(uniqueID, userID, selectedDriverID, "You have a new ride request from " + currentUserName, "Date: " + currentDate + " ,Time: " + currentTime, 2, currentDate, currentTime);
                            firebaseFirestore.collection("notifications").document(uniqueID).set(notificationModel);

                            //move to dashboard
                            Intent intent1 = new Intent(ConfirmBookingActivity.this, PatientDashboardActivity.class);
                            finishAffinity();
                            startActivity(intent1);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //hide the progress bar
                        //progressBar.setVisibility(View.INVISIBLE);
                        //confirmBookingButton.setVisibility(View.VISIBLE);

                        //show message
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(ConfirmBookingActivity.this, PatientDashboardActivity.class);
                startActivity(home);
            }
        });
    }

    private void getCurrentUserName() {
        firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    currentUserName = snapshot.get("name").toString();
                }
            }
        });
    }


    private void getFareValues() {
        firebaseFirestore.collection("Admin").document("Admin").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot snapshot = task.getResult();
                baseFare = Integer.parseInt(snapshot.get("baseFare").toString());
                Log.d("Mylogs, basefare", String.valueOf(baseFare));
                perKmFare = Integer.parseInt(snapshot.get("perKmFare").toString());
                Log.d("Mylogs, perKMfare", String.valueOf(perKmFare));

                estimatedCost = (int) ((distanceInKm * perKmFare) + baseFare);
                Log.d("Mylogs, distanceInKM", String.valueOf(distanceInKm));

                Log.d("Mylogs, estimated", String.valueOf(estimatedCost));
                int valueWithoutBaseFare = (int) (distanceInKm * perKmFare);
                int finalValue = valueWithoutBaseFare + baseFare;
                Log.d("Mylogs, valuewithoutbaseFare", String.valueOf(valueWithoutBaseFare));
                Log.d("Mylogs, finalValue", String.valueOf(finalValue));
                estimatedCostTV.setText("Estimated Cost: Rs " + String.valueOf(finalValue));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        LatLng userLatLng = new LatLng(userLat, userLng);
        LatLng driverLatLng = new LatLng(driverLat, driverLng);
        mOrigin = new LatLng(userLatLng.latitude, userLatLng.longitude);
        mDestination = new LatLng(driverLatLng.latitude, driverLatLng.longitude);
        mMarkerPoints.add(userLatLng);
        mMarkerPoints.add(driverLatLng);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(userLatLng);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        markerOptions.title("User");
        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.position(driverLatLng);
        markerOptions1.title("Driver");
        googleMap.addMarker(markerOptions);
        googleMap.addMarker(markerOptions1);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(driverLat, driverLng), 15));
        drawRoute();
    }

    private void drawRoute() {

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(mOrigin, mDestination);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Key
        String key = "key=AIzaSyByVdzDd2TZwqXqFxfoJRPgJJJviizwGdM";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception on download", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("DownloadTask", "DownloadTask : " + data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                if (mPolyline != null) {
                    mPolyline.remove();
                }
                mPolyline = googleMap.addPolyline(lineOptions);

            } else
                Toast.makeText(getApplicationContext(), "No route is found", Toast.LENGTH_LONG).show();
        }
    }
}