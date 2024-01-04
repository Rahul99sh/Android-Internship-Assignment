package com.pro.arachnomeshassignment.apis;


import android.util.Log;

import androidx.annotation.NonNull;

import com.pro.arachnomeshassignment.interfaces.ApiCallback;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class SubmitDetailsApi {

    public static void submitUserDetails(String name, String userId, String contact, String location, ApiCallback callback){
        OkHttpClient client = new OkHttpClient();

        final String url = "https://reqres.in/api/users/"; // API endpoint

        String body = "{\"Name\": \"" + name + "\", \"userId\": " + userId +
                ", \"Contact\": \"" + contact + "\", \"Location\": \"" + location + "\"}"; //json body

        // Define the media type as JSON
        MediaType mediaType = MediaType.parse("application/json");
        // Create a RequestBody with the JSON string and media type
        RequestBody requestBody = RequestBody.create(body, mediaType);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Handle failure (e.g., network issues, server errors)
                e.printStackTrace();
                callback.onFailure("Error: " + e.getMessage());
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {

                try {
                    if(response.isSuccessful()){
                        assert response.body() != null;
                        Log.d("response", response.body().string());
                        callback.onSuccess();
                    }else{
                        callback.onFailure("Error: " + response.code());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onFailure("Error: " + e.getMessage());
                } finally {
                    response.close();
                }
            }
        });
    }
}
