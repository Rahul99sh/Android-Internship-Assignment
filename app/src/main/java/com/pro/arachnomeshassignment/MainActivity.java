package com.pro.arachnomeshassignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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
    }
}