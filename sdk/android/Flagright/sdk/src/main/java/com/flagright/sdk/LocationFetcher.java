package com.flagright.sdk;

import static android.provider.Settings.System.getString;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.flagright.sdk.interfaces.LocationFoundCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;

class LocationFetcher {
    private FusedLocationProviderClient fusedLocationClient;

    /**
     * Method initialize {@Link FusedLocationProviderClient}
     * @param context instance of Activity
     * @param foundCallback interface {@link LocationFoundCallback}
     */
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

    /**
     * Method checks if the location is enabled or not
     * @param context Application context
     * @return true if the device location is enabled
     */
    public boolean isLocationEnabled(Context context) {
        boolean locationEnabled=false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            LocationManager mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            try {
                locationEnabled = mLocationManager.isLocationEnabled();
            } catch (Exception e) {
                System.err.println("Unable to determine if location enabled. LocationManager was null");
                return false;
            }
        } else {
            int locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
            locationEnabled = locationMode != Settings.Secure.LOCATION_MODE_OFF;
        }
        return locationEnabled;
    }

 }

