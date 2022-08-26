package com.example.quickemergencyhandler.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.quickemergencyhandler.R;
import com.example.quickemergencyhandler.models.DriverModel;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AdapterLvNearbyAmbulances extends BaseAdapter {

    private Context context;
    private ArrayList<DriverModel> nearbyDrivers;
    private LatLng userLatLng;
    private static LayoutInflater inflater = null;

    public AdapterLvNearbyAmbulances(Context context, ArrayList<DriverModel> nearbyDrivers, LatLng userLatLng) {
        this.context = context;
        this.nearbyDrivers = nearbyDrivers;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.userLatLng = userLatLng;
    }

    @Override
    public int getCount() {
        return nearbyDrivers.size();
    }

    @Override
    public Object getItem(int i) {
        return nearbyDrivers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View vi = view;
        if (vi == null)
            vi = inflater.inflate(R.layout.single_item_nearby_ambulance, null);

        TextView vehicleNumber, driverContact, eta, name, featureName;
        ImageView vehicleImage;
        featureName = vi.findViewById(R.id.featureName);
        vehicleNumber = vi.findViewById(R.id.vehicleNumberSingleNearby);
        driverContact = vi.findViewById(R.id.driverNumberSingleNearby);
        eta = vi.findViewById(R.id.etaSingleNearby);
        vehicleImage = vi.findViewById(R.id.imageSingleNearby);
        name = vi.findViewById(R.id.nameSingleNearby);

        name.setText("Name: " + nearbyDrivers.get(i).getName());
        vehicleNumber.setText("Vehicle Number: " + nearbyDrivers.get(i).getVehicleNumber());
        driverContact.setText("Driver Contact: " + nearbyDrivers.get(i).getPhoneNo());
        vehicleImage.setImageResource(R.drawable.logo);
        featureName.setText(getFeature(i));
        float[] results = new float[1];
        android.location.Location.distanceBetween(userLatLng.latitude, userLatLng.longitude, nearbyDrivers.get(i).getLat(), nearbyDrivers.get(i).getLng(), results);
        DecimalFormat decimalFormat = new DecimalFormat(".#");
        String result = decimalFormat.format(results[0] / 1000);
        eta.setText("Distance: " + String.valueOf(result) + "Km");

        return vi;
    }

    public String getFeature(int j) {
        ArrayList<Integer> features = nearbyDrivers.get(j).getVehicleFeatures();
        String f = "";
        for (int i = 0; i < features.size(); i++) {

            if (String.valueOf(features.get(i)).equals("1")) {
                f = "Stretcher";
            } else if (String.valueOf(features.get(i)).equals("2")) {
                f = f + "  Drip";
            } else if (String.valueOf(features.get(i)).equals("3")) {
                f = f + " Oxygen Mask";
            } else if (String.valueOf(features.get(i)).equals("4")) {
                f = f + " Nurse ";
            }

        }
        Log.d("mylogs", String.valueOf(features.size()));
        return "Features: " + f;
    }
}
