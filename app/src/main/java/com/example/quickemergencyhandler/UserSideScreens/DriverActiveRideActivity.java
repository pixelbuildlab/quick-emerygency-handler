package com.example.quickemergencyhandler.UserSideScreens;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quickemergencyhandler.R;
import com.example.quickemergencyhandler.directionHelpers.DirectionsJSONParser;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class DriverActiveRideActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    TextView noRideTV, nameTV, dateTV;
    CardView parentCard;
    Button endRideButton, cancelButton;

    int notiCounter = 0;

    SupportMapFragment mapFragment;
    GoogleMap map;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    double userLat, userLng, driverLat, driverLng;
    Date d1, d2;
    private String fetchedBookingID, fetchedUserID, fetchedDriverID, fetchedStatus, fetchedDate, fetchedTime;
    private double fetchedUserLat, fetchedUserLng, fetchedDriverLat, fetchedDriverLng;
    int cost;
    Booking booking;

    private static long INTERAl = 1000;
    private static long FAST_INTERVAL = 500;
    private static final int REQUEST_LOCATION_CODE = 10;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    int counter = 0;
    Location lastLocation;
    Marker currentLocationMarker;
    boolean hideMarker = false;
    private double getLat, getLng;

    //variables for polyline
    Polyline currentPolyline;
    ArrayList<LatLng> mMarkerPoints;
    int REQUEST_CODE = 1;
    private int PERMISSION_CODE = 1;
    private LatLng mOrigin;
    private LatLng mDestination;
    private Polyline mPolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_active_ride);

        noRideTV = findViewById(R.id.noActiveRideTVdriver);
        parentCard = (CardView) findViewById(R.id.mainCardDriverActiveRide);
        nameTV = findViewById(R.id.nameActiveRideD);
        dateTV = findViewById(R.id.dateActiveRideD);
        cancelButton = findViewById(R.id.returnHome1);
        endRideButton = findViewById(R.id.endRideButton);
        mMarkerPoints = new ArrayList<>();
        noRideTV.setVisibility(View.INVISIBLE);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        buildGoogleApiClient();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragmentActiveRideDriver);
        mapFragment.getMapAsync(this);

        endRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()).toString();
                Booking booking1 = new Booking(fetchedBookingID, fetchedUserID, fetchedDriverID, fetchedUserLat, fetchedUserLng, "completed", currentDate, fetchedTime, fetchedDriverLat, fetchedDriverLng, cost);
                firebaseFirestore.collection("Booking").document(fetchedBookingID).set(booking1);

                Intent intent = new Intent(DriverActiveRideActivity.this, EndRideActivity.class);
                intent.putExtra("userID", fetchedUserID);
                intent.putExtra("cost", cost);
                startActivity(intent);
                finish();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DriverActiveRideActivity.this, DriverDashboardActivity.class);
                finishAffinity();
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

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
                    if (task.getResult().size() > 0) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            String fetchedDate = snapshot.get("date").toString();
                            try {
                                d2 = dateFormat.parse(fetchedDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if (d1.compareTo(d2) == 0 &&
                                    FirebaseAuth.getInstance().getCurrentUser().getUid().equals(snapshot.get("driverID").toString())
                                    &&
                                    snapshot.get("status").toString().equals("accepted")
                            ) {
                                System.out.println("IN CONDITION");
                                fetchedBookingID = snapshot.get("bookingID").toString();
                                fetchedUserID = snapshot.get("userID").toString();
                                fetchedDriverID = snapshot.get("driverID").toString();
                                fetchedStatus = snapshot.get("status").toString();
                                fetchedDate = snapshot.get("date").toString();
                                fetchedTime = snapshot.get("time").toString();
                                fetchedUserLat = Double.parseDouble(snapshot.get("userLat").toString());
                                fetchedUserLng = Double.parseDouble(snapshot.get("userLng").toString());
                                fetchedDriverLat = Double.parseDouble(snapshot.get("driverLat").toString());
                                fetchedDriverLng = Double.parseDouble(snapshot.get("driverLng").toString());
                                cost = Integer.parseInt(snapshot.get("cost").toString());

                                booking = new Booking(fetchedBookingID, fetchedUserID, fetchedDriverID, fetchedUserLat, fetchedUserLng, fetchedStatus, fetchedDate, fetchedTime, 0.0, 0.0, cost);

                                userLat = Double.parseDouble(snapshot.get("userLat").toString());
                                userLng = Double.parseDouble(snapshot.get("userLng").toString());
                                map.addMarker(new MarkerOptions().position(new LatLng(userLat, userLng)).title("User Position"));
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLat, userLng), 15));

                                firebaseFirestore.collection("users").whereEqualTo("id", fetchedUserID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (DocumentSnapshot snapshot1 : task.getResult()) {
                                                nameTV.setText("Name: " + snapshot1.get("name").toString());
                                                dateTV.setText("Contact No: " + snapshot1.get("phoneNo").toString());
                                            }
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                //if no bookings are there
                                noRideTV.setVisibility(View.VISIBLE);
                                mapFragment.getView().setVisibility(View.INVISIBLE);
                                parentCard.setVisibility(View.INVISIBLE);
                            }
                        }
                    } else {
                        //if no bookings are there
                        noRideTV.setVisibility(View.VISIBLE);
                        mapFragment.getView().setVisibility(View.INVISIBLE);
                        parentCard.setVisibility(View.INVISIBLE);
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

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
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

        //changing variables for route
        mDestination = new LatLng(location.getLatitude(), location.getLongitude());
        //drawRoute();

        counter++;
        lastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
            hideMarker = false;
            //currentLocationMarker.setVisible(false);
        } else {
            hideMarker = true;
        }
        getLat = lastLocation.getLatitude();
        getLng = lastLocation.getLongitude();

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = map.getProjection();
        Point startPoint = proj.toScreenLocation(latLng);
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * lastLocation.getLongitude() + (1 - t)
                        * startLatLng.longitude;
                double lat = t * lastLocation.getLatitude() + (1 - t)
                        * startLatLng.latitude;
                currentLocationMarker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        currentLocationMarker.setVisible(false);
                    } else {
                        currentLocationMarker.setVisible(true);
                    }
                }
            }
        });

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("My Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        currentLocationMarker = map.addMarker(markerOptions);

        if (counter == 1) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.5f));
        }
        sendDriverLatLng(getLat, getLng);
    }

    private void sendDriverLatLng(double lat, double lng) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        firebaseFirestore.collection("Booking").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

                        if (d1.compareTo(d2) == 0 &&
                                FirebaseAuth.getInstance().getCurrentUser().getUid().equals(snapshot.get("driverID").toString())
                                &&
                                snapshot.get("status").toString().equals("accepted")
                        ) {

                            System.out.println("location driver updating: " + lat + " " + lng);
                            fetchedBookingID = snapshot.get("bookingID").toString();
                            fetchedUserID = snapshot.get("userID").toString();
                            fetchedDriverID = snapshot.get("driverID").toString();
                            fetchedStatus = snapshot.get("status").toString();
                            fetchedDate = snapshot.get("date").toString();
                            fetchedTime = snapshot.get("time").toString();
                            fetchedUserLat = Double.parseDouble(snapshot.get("userLat").toString());
                            fetchedUserLng = Double.parseDouble(snapshot.get("userLng").toString());
                            fetchedDriverLat = Double.parseDouble(snapshot.get("driverLat").toString());
                            fetchedDriverLng = Double.parseDouble(snapshot.get("driverLng").toString());
                            cost = Integer.parseInt(snapshot.get("cost").toString());
                            booking = new Booking(fetchedBookingID, fetchedUserID, fetchedDriverID, fetchedUserLat, fetchedUserLng, fetchedStatus, fetchedDate, fetchedTime, lat, lng, cost);
                            firebaseFirestore.collection("Booking").document(fetchedBookingID).set(booking);

                            //calculate distance between driver and user and show notification if driver has arrived
                            float[] results = new float[1];
                            Location.distanceBetween(fetchedUserLat, fetchedUserLng, lat, lng, results);
                            float distanceInKm = results[0];

                            //if distance is less than 10 meters
                            notiCounter++;
                            if (distanceInKm < 10) {
                                if (notiCounter == 1) {
                                    Toast.makeText(getApplicationContext(), "You have arrived", Toast.LENGTH_LONG).show();
                                    //send notification to table
                                    String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                                    String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                                    String uniqueID = UUID.randomUUID().toString();
                                    NotificationModel notificationModel = new NotificationModel(uniqueID, firebaseAuth.getCurrentUser().getUid(), fetchedUserID, "Ambulance has arrived at your location", "Date: " + currentDate + " ,Time: " + currentTime, 1, currentDate, currentTime);
                                    firebaseFirestore.collection("notifications").document(uniqueID).set(notificationModel);
                                }
                            }
                        }
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

    //**********************************functions for drawing route***************************************
    private void drawRoute() {

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(mOrigin, mDestination);

        DriverActiveRideActivity.DownloadTask downloadTask = new DriverActiveRideActivity.DownloadTask();

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

            DriverActiveRideActivity.ParserTask parserTask = new DriverActiveRideActivity.ParserTask();

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
                mPolyline = map.addPolyline(lineOptions);

            } else
                Toast.makeText(getApplicationContext(), "No route is found", Toast.LENGTH_LONG).show();
        }
    }

}