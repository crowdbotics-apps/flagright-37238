package com.flagright.sdk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.biometrics.BiometricManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.flagright.sdk.interfaces.LocationFoundCallback;
import com.flagright.sdk.models.BatteryInfoModel;
import com.scottyab.rootbeer.RootBeer;

import java.io.File;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class FlagRightInstance {
    private static FlagRightInstance mFlagRightInstance;

    /**
     * Private constructor for singleton
     */
    private FlagRightInstance() {
    }

    /**
     * Method initialize the instance only one time
     *
     * @return instance of the {@link FlagRightInstance}
     */
    public static FlagRightInstance getInstance() {
        if (mFlagRightInstance == null) {
            mFlagRightInstance = new FlagRightInstance();
        }
        return mFlagRightInstance;
    }

    /**
     * Method checks if the application is running on a real device or not
     *
     * @return
     */
    public boolean isEmulator() {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("sdk_gphone64_arm64")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator");
    }

    /**
     * Return either IPv4 address or IPv6 address
     *
     * @param useIPv4 if true return IPv4 address
     * @return IP address
     */
    public String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } // for now eat exceptions
        return "";
    }

    /**
     * Permission required ACCESS_NETWORK_STATE
     *
     * @param context context of the application
     * @return true if device is connected to VPN
     */
    public boolean isDeviceConnectedToVPN(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") Network[] networks = cm.getAllNetworks();

        boolean isRunningVPN = false;
        for (Network value : networks) {
            @SuppressLint("MissingPermission") NetworkCapabilities caps = cm.getNetworkCapabilities(value);
            if (caps != null && (caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN) || !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN))) {
                isRunningVPN = true;
                break;
            }
        }
        return isRunningVPN;
    }

    /**
     * Method check if PIN, pattern or password or a SIM card is currently locked.
     *
     * @param context application context
     * @return true if phone is protected with pin, pattern or password otherwise false
     */
    public boolean isBioMetricEnabled(Context context) {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if (keyguardManager != null) {
            return keyguardManager.isKeyguardSecure();
        }
        System.err.println("Unable to determine keyguard status. KeyguardManager was null");
        return false;
    }

    /**
     * Method detects if the device is rooted or not by using https://github.com/scottyab/rootbeer library
     *
     * @param context application context
     * @return true if the device is rooted
     */
    public boolean isDeviceRooted(Context context) {
        RootBeer rootBeer = new RootBeer(context);
        return rootBeer.isRooted();
    }

    /**
     * Requires READ_CONTACTS permission
     * Method fetch total number contacts
     *
     * @param context Application context
     * @return total number of contacts
     */
    public int fetchContactsCount(Context context) {
        return ContactsFetcher.getTotalContactsCount(context);
    }

    /**
     * Required permissions
     * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
     * <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
     * Method returns the current location
     *
     * @param context               instance of an Activity
     * @param locationFoundCallback {@link LocationFoundCallback} it an interface that returns location on success otherwise error
     */
    public void fetchCurrentLocation(Context context, LocationFoundCallback locationFoundCallback) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationFetcher fetcher = new LocationFetcher();
//            fetcher.init(context, new LocationFoundCallback() {
//                @Override
//                public void locationFound(Location location) {
//                    locationFoundCallback.locationFound(location);
//                }
//
//                @Override
//                public void locationError(String error) {
//                    locationFoundCallback.locationError(error);
//                }
//            });
            fetcher.init(context, locationFoundCallback);
        } else {
            locationFoundCallback.locationError("App does not have location permission");
        }
    }

    /**
     * Methods tells if the device location is enabled or not
     *
     * @param context application context
     * @return true if the location is enabled
     */
    public boolean isLocationEnabled(Context context) {
        LocationFetcher locationFetcher = new LocationFetcher();
        return locationFetcher.isLocationEnabled(context);
    }

    /**
     * Method returns the Battery level
     *
     * @param context Application context
     * @return the object of {@link BatteryInfoModel} which contains level, scale and battery percentage
     */
    public BatteryInfoModel getBatteryLevel(Context context) {
        BatteryFetcher batteryFetcher = new BatteryFetcher();
        return batteryFetcher.getBatteryInfo(context);
    }

    /**
     * Method return the total internal storage in GB provided by {@link Environment} class of Android SDK
     * @return size of internal storage in GB
     */
    public double getTotalInternalStorage() {
        return StorageFetcher.getInstance().getTotalInternalStorage();
    }

    /**
     * Method calculate the available internal storage in GB
     * @return available internal storage in GB
     */
    public double getFreeInternalStorage() {
       return StorageFetcher.getInstance().getFreeInternalStorage();
    }

    /**
     * Method check if external SD card is attached with the device and calculate the size of the
     * external card
     * @param forFreeStorage if true, then method will return only the available size;
     *                       otherwise return the total size
     * @return size of the external storage in GB
     */
    public double getExternalSdCardSize(boolean forFreeStorage) {
       return StorageFetcher.getInstance().getExternalSdCardSize(forFreeStorage);
    }
}
