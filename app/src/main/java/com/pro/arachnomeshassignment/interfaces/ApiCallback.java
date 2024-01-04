package com.pro.arachnomeshassignment.interfaces;

public interface ApiCallback {
    void onSuccess();

    void onFailure(String errorMessage);
}
