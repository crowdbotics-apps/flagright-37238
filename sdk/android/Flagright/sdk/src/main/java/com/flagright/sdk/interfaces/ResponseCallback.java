package com.flagright.sdk.interfaces;

public interface ResponseCallback {
    void onSuccess();
    void onFailure(String errorMessage);
}
