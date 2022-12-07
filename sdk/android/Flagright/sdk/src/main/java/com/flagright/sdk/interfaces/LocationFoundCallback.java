package com.flagright.sdk.interfaces;

import android.location.Location;

public interface LocationFoundCallback {
    void locationFound(Location location);
    void locationError(String error);
}
