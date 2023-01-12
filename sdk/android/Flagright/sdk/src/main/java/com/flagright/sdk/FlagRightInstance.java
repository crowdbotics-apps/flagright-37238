package com.flagright.sdk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
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
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.flagright.sdk.interfaces.APIInterface;
import com.flagright.sdk.interfaces.LocationFoundCallback;
import com.flagright.sdk.interfaces.ResponseCallback;
import com.flagright.sdk.models.BatteryInfoModel;
import com.flagright.sdk.models.BluetoothResponseModal;
import com.flagright.sdk.models.RequestModal;
import com.google.gson.Gson;
import com.scottyab.rootbeer.RootBeer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FlagRightInstance {
    private static FlagRightInstance mFlagRightInstance;
    private static RequestModal mRequestModal;
    private enum Type {TRANSACTION, USER_SIGNUP};
    private static final String BASE_URL = "https://stoplight.io/mocks/flagright-device-api/flagright-device-data-api/122980601/";

    /**
     * Private constructor for singleton
     */
    private FlagRightInstance() {
    }

    public void init(Context context, String apiKey, String userId, String transactionId, ResponseCallback responseCallback) {
        mRequestModal.setUserId(userId);
        mRequestModal.setType(transactionId == null?Type.USER_SIGNUP.name():Type.TRANSACTION.name());
        mRequestModal.setTimestamp(System.currentTimeMillis());
        mRequestModal.setDeviceLaungageCode(getDeviceLocaleLanguageCode());
        mRequestModal.setDeviceFingerprint(getFingerprint());
        mRequestModal.setVirtualDevice(isEmulator());
        mRequestModal.setIpAddress(getIPAddress(true));
        mRequestModal.setTotalNumberOfContacts(fetchContactsCount(context));
        mRequestModal.setBatteryLevel(getBatteryLevel(context).getLevel());
        mRequestModal.setExternalTotalStorageInGb(getExternalSdCardSize(false));
        mRequestModal.setExternalFreeStorageInGb(getExternalSdCardSize(true));
        mRequestModal.setManufacturer(getManufactureName());
        mRequestModal.setMainTotalStorageInGb(getTotalInternalStorage());
        mRequestModal.setModel(getModalName());
        RequestModal.OperatingSystem operatingSystem = new RequestModal.OperatingSystem();
        operatingSystem.setName(getOSName());
        operatingSystem.setVersion(getOSVersion());
        mRequestModal.setOperatingSystem(operatingSystem);
        mRequestModal.setDeviceCountryCode(getDeviceLocaleCountry());
        mRequestModal.setRamInGb(getRamSize(context));
        mRequestModal.setDataRoamingEnabled(isDataRoamingEnabled(context));
        mRequestModal.setLocationEnabled(isLocationEnabled(context));
        mRequestModal.setAccessibilityEnabled(isAccessibilityEnabled(context));
        mRequestModal.setBluetoothActive(isBluetoothEnabled().isEnable());
        mRequestModal.setNetworkOperator(getNetworkOperatorName(context));

        // for location
        fetchCurrentLocation(context, new LocationFoundCallback() {
            @Override
            public void locationFound(Location location) {
                RequestModal.Location loc = new RequestModal.Location();
                loc.setLatitude(location.getLatitude());
                loc.setLongitude(location.getLongitude());
                mRequestModal.setLocation(loc);

//                Gson gson = new Gson();
//                System.out.println("RequestModal " +gson.toJson(mRequestModal).toString());

                submitInfo(context, apiKey, responseCallback);
            }

            @Override
            public void locationError(String error) {
                Gson gson = new Gson();
                System.out.println("RequestModal " +gson.toJson(mRequestModal).toString());

                submitInfo(context, apiKey, responseCallback);
            }
        });

//        Gson gson = new Gson();
//        System.out.println("RequestModal " +gson.toJson(mRequestModal).toString());
//
//        submitInfo(context);

    }

    /**
     * Method initialize the instance only one time
     *
     * @return instance of the {@link FlagRightInstance}
     */
    public static FlagRightInstance getInstance() {
        if (mFlagRightInstance == null) {
            mRequestModal = new RequestModal();
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
     *
     * @return size of internal storage in GB
     */
    public double getTotalInternalStorage() {
        return StorageFetcher.getInstance().getTotalInternalStorage();
    }

    /**
     * Method calculate the available internal storage in GB
     *
     * @return available internal storage in GB
     */
    public double getFreeInternalStorage() {
        return StorageFetcher.getInstance().getFreeInternalStorage();
    }

    /**
     * Method check if external SD card is attached with the device and calculate the size of the
     * external card
     *
     * @param forFreeStorage if true, then method will return only the available size;
     *                       otherwise return the total size
     * @return size of the external storage in GB
     */
    public double getExternalSdCardSize(boolean forFreeStorage) {
        return StorageFetcher.getInstance().getExternalSdCardSize(forFreeStorage);
    }

    /**
     * Method fetch the modal name of a calling device
     *
     * @return return the modal name of a device
     */
    public String getModalName() {
        return Build.MODEL;
    }

    /**
     * Method fetch the manufacture name of a calling device
     *
     * @return return the manufacture name of a device
     */
    public String getManufactureName() {
        return Build.MANUFACTURER;
    }

    /**
     * Method fetch the current OS version of a device
     *
     * @return return the OS version of a device
     */
    public String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    public String getOSName() {
        Field[] fields = Build.VERSION_CODES.class.getFields();
        String codeName = "UNKNOWN";
        for (Field field : fields) {
            try {
                if (field.getInt(Build.VERSION_CODES.class) == Build.VERSION.SDK_INT) {
                    codeName = field.getName();
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return codeName;
    }

    /**
     * Method returns the language code of this Locale.
     *
     * @return The language code, or the empty string if none is defined.
     */
    public String getDeviceLocaleLanguageCode() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * Returns the country/region code for this locale, which should either be the empty string,
     * an uppercase ISO 3166 2-letter code, or a UN M.49 3-digit code.
     *
     * @return The country/region code, or the empty string if none is defined.
     */
    public String getDeviceLocaleCountry() {
        return Locale.getDefault().getCountry();
    }

    /**
     * Gets the ID of this time zone.
     *
     * @return the ID of the time zone
     */
    public String getDeviceTimeZone() {
        return TimeZone.getDefault().getID();
    }

    /**
     * Returns the total RAM size of a device
     *
     * @param context Application context
     * @return the RAM size in GB
     */
    public double getRamSize(Context context) {
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        double scale = Math.pow(10, 0);
        double value = ((double) memoryInfo.totalMem) / (1024 * 1024 * 1024);
        return Math.round(value * scale) / scale;
    }

    /**
     * Check if the data roaming is enabled
     *
     * @param context Application context
     * @return true if the data raoming is enabled
     */
    public boolean isDataRoamingEnabled(Context context) {

        try {
            // return true or false if data roaming is enabled or not
            int isEnabled = Settings.Secure.getInt(context.getContentResolver(), Settings.Global.DATA_ROAMING);
            System.out.println("isEnabled: " + isEnabled);
            return isEnabled == 1;
        } catch (Settings.SettingNotFoundException e) {
            // return null if no such settings exist (device with no radio data ?)
            return false;
        }
    }

    /**
     * Check is the accessibility is enabled or not
     *
     * @param context Application context
     * @return true if the accessibility is enabled
     */
    public boolean isAccessibilityEnabled(Context context) {
        boolean enabled = false;
        try {
            enabled = Settings.Secure.getInt(context.getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED) == 1;
        } catch (Settings.SettingNotFoundException ex) {
            enabled = false;
        }
        return enabled;
    }

    /**
     * Required permission
     * <uses-permission android:name="android.permission.BLUETOOTH" android:required="false" />
     * Method checks if bluetooth enabled
     *
     * @return object of {@link BluetoothResponseModal} which returns information about
     * bluetooth is enabled or not along with the error message if device does not support bluetooth
     */
    public BluetoothResponseModal isBluetoothEnabled() {
        BluetoothResponseModal bluetoothResponseModal = new BluetoothResponseModal();
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            bluetoothResponseModal.setEnable(false);
            bluetoothResponseModal.setErrorMessage("Device does not support bluetooth");
        } else if (!mBluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled :)
            bluetoothResponseModal.setEnable(false);
        } else {
            // Bluetooth is enabled
            bluetoothResponseModal.setEnable(true);
        }
        return bluetoothResponseModal;
    }

    /**
     * Gets the network operator name
     *
     * @param context Application context
     * @return name of the network operator
     */
    public String getNetworkOperatorName(Context context) {
        TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telMgr != null) {
            return telMgr.getNetworkOperatorName();
        } else {
            System.err.println("Unable to get network operator name. TelephonyManager was null");
            return "unknown";
        }
    }

    public String getFingerprint() {
        return Build.FINGERPRINT;
    }

    private void submitInfo(Context context, String mAPIKey, ResponseCallback responseCallback)  {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        // on below line we are creating a retrofit
        // builder and passing our base url
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                // as we are sending data in json format so
                // we have to add Gson converter factory
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
        // at last we are building our retrofit builder.
                .build();
        // below line is to create an instance for our APIInterface api class.
        APIInterface retrofitAPI = retrofit.create(APIInterface.class);


        // calling a method to create a post and passing our modal class.
        Call<Void> call = retrofitAPI.sendData(mAPIKey,mRequestModal);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i("API", response.code()+" "+call.request().body()+" "+call.request().headers().toString());
                if (response.code() == 200) {
                   responseCallback.onSuccess();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API", t.getMessage()+" "+call.request().url());
                responseCallback.onFailure(t.getMessage());
            }
        });
    }
}
