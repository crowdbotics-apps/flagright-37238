package com.flagright.sdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.flagright.sdk.interfaces.LocationFoundCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;

class LocationFetcher {
    private FusedLocationProviderClient fusedLocationClient;
    @SuppressLint("MissingPermission")
    public void init(Context context, LocationFoundCallback foundCallback) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        fusedLocationClient.getCurrentLocation(100, new CancellationToken() {
            @Override
            public boolean isCancellationRequested() {
                return false;
            }

            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                return null;
            }
        }).addOnSuccessListener((Activity) context, location -> {
            if (location!= null) {
                Log.d("FlagRight", String.valueOf(location));
                foundCallback.locationFound(location);
            } else {
                foundCallback.locationError("Location not found");
            }
        });
    }

 }

