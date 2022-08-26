package com.example.quickemergencyhandler.UserSideScreens;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.quickemergencyhandler.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class SelectLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    SupportMapFragment supportMapFragment;
    GoogleMap mMap;
    SearchView searchView;
    Button nextButton, currentLocationButton;

    int REQUEST_CODE = 1;
    private int PERMISSION_CODE = 1;

    //variables for location
    private double currentLatitude;
    private double currentLongitude;
    private LatLng selectedLatLng;
    private LocationManager locationManager;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        searchView = findViewById(R.id.searchView);
        nextButton = (Button) findViewById(R.id.nextButton);
        currentLocationButton = (Button) findViewById(R.id.currentLocationButton);
        selectedLatLng = new LatLng(0, 0);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        supportMapFragment.getMapAsync(this);

        searchView = (SearchView) findViewById(R.id.searchView);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(SelectLocationActivity.this);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                mMap.clear();
                String location = searchView.getQuery().toString();
                List<Address> addresses = null;
                if (location != null || !location.trim().equals("")) {
                    Geocoder geocoder = new Geocoder(SelectLocationActivity.this);
                    try {
                        addresses = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addresses.size() > 0) {
                        Address address = addresses.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location").draggable(true));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        selectedLatLng = new LatLng(latLng.latitude, latLng.longitude);
                    } else {
                        Toast.makeText(getApplicationContext(), "Location not found", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectLocationActivity.this, NearbyAmbulancesActivity.class);
                if (selectedLatLng.latitude == 0.0 || selectedLatLng.longitude == 0.0) {
                    Toast.makeText(getApplicationContext(), "Select a pickup location first from tapping on the map or choosing current location", Toast.LENGTH_SHORT).show();
                } else {
                    intent.putExtra("lat", selectedLatLng.latitude);
                    intent.putExtra("lng", selectedLatLng.longitude);
                    startActivity(intent);
                }
            }
        });

        currentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check if the permissions are granted
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SelectLocationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
                    Toast.makeText(getApplicationContext(), "Provide the permissions to continue", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!checkGPS()) {
                    buildAlertMessageNoGps();
                    return;
                } else {
                    //get current location of user
                    LocationServices.getFusedLocationProviderClient(SelectLocationActivity.this).requestLocationUpdates(locationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(@NonNull LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            LocationServices.getFusedLocationProviderClient(SelectLocationActivity.this).removeLocationUpdates(this);
                            if (locationResult != null && locationResult.getLocations().size() > 0) {
                                int index = locationResult.getLocations().size() - 1;
                                currentLatitude = locationResult.getLocations().get(index).getLatitude();
                                currentLongitude = locationResult.getLocations().get(index).getLongitude();

                                //show on map
                                mMap.clear();
                                LatLng latLng = new LatLng(currentLatitude, currentLongitude);
                                mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location").draggable(true));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                                selectedLatLng = new LatLng(currentLatitude, currentLongitude);

                                //move to next screen
                                //Intent intent = new Intent(SelectLocationActivity.this, NearbyAmbulancesActivity.class);
                                //startActivity(intent);
                            }
                        }
                    }, Looper.getMainLooper());
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng temp = new LatLng(33.633471, 73.066838);
        mMap.addMarker(new MarkerOptions().position(temp).title("Selected location").draggable(true));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(temp, 15));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                Toast.makeText(getApplicationContext(), latLng.toString(), Toast.LENGTH_SHORT).show();
                mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location").draggable(true));
                LatLng choosenLatlng = new LatLng(latLng.latitude, latLng.longitude);
                selectedLatLng = new LatLng(choosenLatlng.latitude, choosenLatlng.longitude);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions Granted..", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please provide the permissions to continue..", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean checkGPS() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return false;
        } else {
            return true;
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, turn on the gps to continue")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}