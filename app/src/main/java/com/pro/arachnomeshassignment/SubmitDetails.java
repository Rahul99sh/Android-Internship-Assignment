package com.pro.arachnomeshassignment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pro.arachnomeshassignment.databinding.ActivitySubmitDetailsBinding;

public class SubmitDetails extends AppCompatActivity implements ApiCallback {

    ActivitySubmitDetailsBinding binding;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubmitDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // retrieving user details
        String userName = getIntent().getStringExtra("userName");
        String userId = getIntent().getStringExtra("userId");
        String contact = getIntent().getStringExtra("contactNumber");
        String location = getIntent().getStringExtra("location");

        binding.name.setText(userName);
        binding.userId.setText(userId);
        binding.contact.setText(contact);
        binding.locationCoordinates.setText(location);

        binding.submit.setOnClickListener(v -> {
            // Create and show the ProgressDialog
            progressDialog = new ProgressDialog(SubmitDetails.this);
            progressDialog.setMessage("Submitting Details...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            // calling api
            SubmitDetailsApi.submitUserDetails(userName, userId, contact, location, this);
        });
    }
    @Override
    public void onSuccess() {
        runOnUiThread(() -> {
            if(progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
            Toast.makeText(SubmitDetails.this, "Details Successfully Submitted!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public void onFailure(String errorMessage) {
        runOnUiThread(() -> {
            if(progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
            Toast.makeText(SubmitDetails.this, errorMessage, Toast.LENGTH_SHORT).show();
        });
    }
}