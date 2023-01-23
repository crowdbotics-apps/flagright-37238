package com.flagright.sdk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Environment;
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
import com.flagright.sdk.models.InitResponse;
import com.scottyab.rootbeer.RootBeer;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FlagrightDeviceMetricsSDK {
    private static final String BASE_URL = "https://stoplight.io/mocks/flagright-device-api/flagright-device-data-api/122980601/";
    private static FlagrightDeviceMetricsSDK mFlagrightDeviceMetricsSDK;
    //    private static RequestModal mRequestModal;
    private static String mApiKey;
    private static String mRegion;


    /**
     * Private constructor for singleton
     */
    private FlagrightDeviceMetricsSDK() {
    }

    /**
     * Method initialize the instance only one time
     *
     * @return instance of the {@link FlagrightDeviceMetricsSDK}
     */
    public static FlagrightDeviceMetricsSDK getInstance() {
        if (mFlagrightDeviceMetricsSDK == null) {
//            mRequestModal = new RequestModal();
            mFlagrightDeviceMetricsSDK = new FlagrightDeviceMetricsSDK();
        }
        return mFlagrightDeviceMetricsSDK;
    }

    /**
     * Method accepted the API key which would be required for Server Authentication purpose
     *
     * @param apiKey API key required to authenticate FlagRight Server
     * @param region Region is required by the API
     */
    public InitResponse init(String apiKey, String region) {
        InitResponse initResponse = new InitResponse();
        if (Validator.validateAPIKey(apiKey) && Validator.validateRegion(region)) {
            mApiKey = apiKey;
            mRegion = region;
           initResponse.setSuccess(true);
        } else {
            initResponse.setSuccess(false);
            if (!Validator.validateAPIKey(apiKey)) {
               initResponse.setError("Please add valid apiKey");
            } else {
                initResponse.setError("Please add valid region");
            }
        }
        return initResponse;
    }

    /**
     * This method needs to call after the init method. This method gets all the required
     * attributes from device and then call the
     * {@link #submitInfo(Context, ResponseCallback, JSONObject) submitInfo} method
     *
     * @param context Application context
     * @param userId user id
     * @param transactionId transaction id
     * @param responseCallback callback that inform success and failure
     */
    public void emit(Context context, String userId, String transactionId, ResponseCallback responseCallback) {
        if (Validator.validateUserId(userId) && Validator.validateContext(context) && Validator.validateOnSuccessListener(responseCallback)) {
            JSONObject requestJsonObject = new JSONObject();
            try {
                requestJsonObject.put("userId", userId);
                requestJsonObject.put("type", transactionId == null ? Type.USER_SIGNUP.name() :
                        Type.TRANSACTION.name());
                requestJsonObject.put("timestamp", System.currentTimeMillis());
                if (transactionId != null) {
                    requestJsonObject.put("transactionId", transactionId);
                }
                requestJsonObject.put("deviceFingerprint", getFingerprint());
                requestJsonObject.put("isVirtualDevice", isEmulator());
                String ipAddress4 = getIPAddress(true);
                if (ipAddress4 != null)
                    requestJsonObject.put("ipAddress", getIPAddress(true));
                int totalContacts = fetchContactsCount(context);
                // -1 means permission not granted
                if (totalContacts != -1)
                    requestJsonObject.put("totalNumberOfContacts", totalContacts);
                requestJsonObject.put("batteryLevel", getBatteryLevel(context).getLevel());
                double totalExternalStorage = getExternalSdCardSize((false));
                if (totalExternalStorage != 0) {
                    requestJsonObject.put("externalTotalStorageInGb", totalExternalStorage);
                    // check for free external storage
                    requestJsonObject.put("externalFreeStorageInGb", getExternalSdCardSize((true)));
                }
                requestJsonObject.put("manufacturer", getManufactureName());
                double mainTotalStorage = getTotalInternalStorage();
                if (mainTotalStorage != 0) {
                    requestJsonObject.put("mainTotalStorageInGb", mainTotalStorage);
                }
                requestJsonObject.put("model", getModalName());
                // operating system object
                JSONObject osObject = new JSONObject();
                osObject.put("name", "Android");
                osObject.put("version", getOSVersion());
                requestJsonObject.put("operatingSystem", osObject);
                requestJsonObject.put("deviceCountryCode", getDeviceLocaleCountry());
                requestJsonObject.put("deviceLaungageCode", getDeviceLocaleLanguageCode());
                requestJsonObject.put("ramInGb", getRamSize(context));
                requestJsonObject.put("isDataRoamingEnabled", isDataRoamingEnabled(context));
                requestJsonObject.put("isLocationEnabled", isLocationEnabled(context));
                requestJsonObject.put("isAccessibilityEnabled", isAccessibilityEnabled(context));
                requestJsonObject.put("isBluetoothActive", isBluetoothEnabled().isEnable());
                requestJsonObject.put("networkOperator", getNetworkOperatorName(context));

                // for location
                fetchCurrentLocation(context, new LocationFoundCallback() {
                    @Override
                    public void locationFound(Location location) {
                        try {
                            JSONObject locationObject = new JSONObject();
                            locationObject.put("latitude", location.getLatitude());
                            locationObject.put("longitude", location.getLongitude());
                            requestJsonObject.put("location", locationObject);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                        submitInfo(context, responseCallback, requestJsonObject);
                    }

                    @Override
                    public void locationError(String error) {
                        submitInfo(context, responseCallback, requestJsonObject);
                    }
                });
            } catch (JSONException ex) {
                responseCallback.onFailure(ex.getMessage());
            }
        }else {
            if (!Validator.validateContext(context))
            responseCallback.onFailure("Context is not valid");
            else if (!Validator.validateUserId(userId))
                responseCallback.onFailure("User Id is not valid");
            else if (!Validator.validateOnSuccessListener(responseCallback)) {
                Log.e("Error", "Please pass the Valid onSuccess listener");
            }
        }
    }

    /**
     * Method checks if the application is running on a real device or not
     *
     * @return true if the app is running on the emulator
     */
    public boolean isEmulator() {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) || Build.FINGERPRINT.startsWith("generic") || Build.FINGERPRINT.startsWith("unknown") || Build.HARDWARE.contains("goldfish") || Build.HARDWARE.contains("ranchu") || Build.MODEL.contains("google_sdk") || Build.MODEL.contains("Emulator") || Build.MODEL.contains("Android SDK built for x86") || Build.MANUFACTURER.contains("Genymotion") || Build.PRODUCT.contains("sdk_google") || Build.PRODUCT.contains("google_sdk") || Build.PRODUCT.contains("sdk") || Build.PRODUCT.contains("sdk_x86") || Build.PRODUCT.contains("sdk_gphone64_arm64") || Build.PRODUCT.contains("vbox86p") || Build.PRODUCT.contains("emulator") || Build.PRODUCT.contains("simulator");
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
                        boolean isIPv4 = (sAddr != null ? sAddr.indexOf(':') : 0) < 0;
                        if (useIPv4) {
                            if (isIPv4) return sAddr;
                        } else {
                            if (!isIPv4) {
                                assert sAddr != null;
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
        return null;
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
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
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
     * @return true if the data raaming is enabled
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
        boolean enabled;
        try {
            enabled = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED) == 1;
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
        // Bluetooth is not enabled :)
        // Bluetooth is enabled
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            bluetoothResponseModal.setEnable(false);
            bluetoothResponseModal.setErrorMessage("Device does not support bluetooth");
        } else bluetoothResponseModal.setEnable(mBluetoothAdapter.isEnabled());
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

    /**
     * Method gets the device fingerprint
     *
     * @return device fingerprint
     */
    public String getFingerprint() {
        return Build.FINGERPRINT;
    }

    /**
     * Method send all attributes to the Flagright server
     *
     * @param context          Context of the Application
     * @param responseCallback {@link ResponseCallback}
     */
    private void submitInfo(Context context, ResponseCallback responseCallback, JSONObject requestJsonObject) {
        if (Validator.validateAPIKey(mApiKey)) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);

            // on below line we are creating a retrofit
            // builder and passing our base url
            Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                    // as we are sending data in json format so
                    // we have to add Gson converter factory
                    .addConverterFactory(GsonConverterFactory.create()).client(httpClient.build())
                    // at last we are building our retrofit builder.
                    .build();
            // below line is to create an instance for our APIInterface api class.
            APIInterface retrofitAPI = retrofit.create(APIInterface.class);


            // calling a method to create a post and passing our modal class.
            RequestBody requestBody = RequestBody.create(MediaType.parse
                    ("application/json; charset=utf-8"), requestJsonObject.toString());
            Call<Void> call = retrofitAPI.sendData(mApiKey, requestBody);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.i("API", response.code() + " " + call.request().body() + " " + call.request().headers().toString());
                    if (response.code() == 200) {
                        responseCallback.onSuccess();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("API", t.getMessage() + " " + call.request().url());
                    responseCallback.onFailure(t.getMessage());
                }
            });
        } else {
            responseCallback.onFailure("API key is not assigned");
        }
    }

    private enum Type {TRANSACTION, USER_SIGNUP}
}
