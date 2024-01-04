package com.pro.arachnomeshassignment;

public interface ApiCallback {
    void onSuccess();

    void onFailure(String errorMessage);
}
