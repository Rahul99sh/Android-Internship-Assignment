package com.pro.arachnomeshassignment.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.pro.arachnomeshassignment.databinding.ActivityMainBinding;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        binding.getLocation.setOnClickListener(v -> {
            fetchLocation();
            binding.getLocation.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
        });

        binding.submit.setOnClickListener(v -> {
            String userId = Objects.requireNonNull(binding.userId.getText()).toString();
            String userName = Objects.requireNonNull(binding.name.getText()).toString();
            String contactNumber = Objects.requireNonNull(binding.contact.getText()).toString();
            String location = binding.locationCoordinates.getText().toString();
            if(userId.isEmpty() || userName.isEmpty() || contactNumber.isEmpty() || location.isEmpty()){
                Toast.makeText(this, "All Fields are Required!", Toast.LENGTH_SHORT).show();
            }else{
                //switch to second activity
                Intent switchActivity = new Intent(this, SubmitDetails.class);
                switchActivity.putExtra("userId", userId);
                switchActivity.putExtra("userName", userName);
                switchActivity.putExtra("contactNumber", contactNumber);
                switchActivity.putExtra("location", location);
                startActivity(switchActivity);
            }
        });
    }
    // callback after permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, fetch location
                fetchLocation();
            } else {
                // Permission denied
                binding.getLocation.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to fetch location
    private void fetchLocation() {
        if(isLocationEnabled()) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_PERMISSION);
                return;
            }
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            // Use latitude and longitude
                            String loc = latitude + ", " + longitude;

                            binding.locationCoordinates.setText(loc);
                            binding.getLocation.setVisibility(View.VISIBLE);
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    })
                    .addOnFailureListener(this, e -> {
                        binding.getLocation.setVisibility(View.VISIBLE);
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Failed to fetch location. Try Again!", Toast.LENGTH_SHORT).show();
                    });
        }else{ // location service is turned off
            showLocationAlertDialog();
        }
    }
    // Method for requesting location services
    public void showLocationAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Location Services Disabled");
        builder.setMessage("Please enable location services to use this feature.");
        builder.setPositiveButton("Go to Settings", (dialogInterface, i) -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
            binding.getLocation.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
            binding.getLocation.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
            dialogInterface.dismiss();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    // Checking location services working ?
    public boolean isLocationEnabled() { // checking if location service is turned on or off
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        return false;
    }

}