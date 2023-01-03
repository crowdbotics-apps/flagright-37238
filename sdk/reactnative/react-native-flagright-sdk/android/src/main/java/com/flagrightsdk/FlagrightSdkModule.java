package com.flagrightsdk;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.module.annotations.ReactModule;
import com.scottyab.rootbeer.RootBeer;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

@ReactModule(name = FlagrightSdkModule.NAME)
public class FlagrightSdkModule extends ReactContextBaseJavaModule {
  public static final String NAME = "FlagrightSdk";

  public FlagrightSdkModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }


  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  public void multiply(double a, double b, Promise promise) {
    promise.resolve(a * b);
  }

  @ReactMethod
  public void getDeviceLocaleLanguageCode(Promise promise) {
        promise.resolve(Locale.getDefault().getLanguage());
  }

  @ReactMethod
  public void getDeviceLocaleCountry(Promise promise) {
    promise.resolve(Locale.getDefault().getCountry());
  }

  @ReactMethod
  public void getDeviceTimeZone(Promise promise) {
   promise.resolve(TimeZone.getDefault().getID());
  }

  @ReactMethod
  public void isDataRoamingEnabled(Promise promise) {
    try {
      // return true or false if data roaming is enabled or not
      int isEnabled = Settings.Secure.getInt(getReactApplicationContext().getContentResolver(), Settings.Global.DATA_ROAMING);
      System.out.println("isEnabled: " + isEnabled);
       promise.resolve(isEnabled == 1);
    } catch (Settings.SettingNotFoundException e) {
      // return null if no such settings exist (device with no radio data ?)
       promise.resolve(false);
    }
  }

  @ReactMethod
  public void isAccessibilityEnabled(Promise promise) {
    boolean enabled = false;
    try {
      enabled = Settings.Secure.getInt(getReactApplicationContext().getContentResolver(),
        Settings.Secure.ACCESSIBILITY_ENABLED) == 1;
    } catch (Settings.SettingNotFoundException ex) {
      enabled = false;
    }
     promise.resolve(enabled);
  }

  /**
   * Required permission
   * <uses-permission android:name="android.permission.BLUETOOTH" android:required="false" />
   * Method checks if bluetooth enabled
   *
   * @return promise of object of {@link BluetoothResponseModal} which returns information about
   * bluetooth is enabled or not along with the error message if device does not support bluetooth
   */
  @ReactMethod
  public void isBluetoothEnabled(Promise promise) {
    WritableMap map = Arguments.createMap();
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (mBluetoothAdapter == null) {
      // Device does not support Bluetooth
      map.putBoolean("enable", false);
      map.putString("errorMessage", "Device does not support bluetooth");
    } else if (!mBluetoothAdapter.isEnabled()) {
      // Bluetooth is not enabled :)
      map.putBoolean("enable", false);
    } else {
      // Bluetooth is enabled
      map.putBoolean("enable", true);
    }
     promise.resolve(map);
  }

  /**
     * Return either IPv4 address or IPv6 address
     *
     * @param useIPv4 if true return IPv4 address
     * @return IP address
     */
    @ReactMethod
    public void getIPAddress(boolean useIPv4, Promise promise) {
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
                                promise.resolve(sAddr);
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                promise.resolve(delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase());
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } // for now eat exceptions
        promise.resolve("");
    }

        /**
     * Permission required ACCESS_NETWORK_STATE
     *
     * @return true if device is connected to VPN
     */
     @ReactMethod
    public void isDeviceConnectedToVPN(Promise promise) {
        ConnectivityManager cm = (ConnectivityManager) getReactApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") Network[] networks = cm.getAllNetworks();

        boolean isRunningVPN = false;
        for (Network value : networks) {
            @SuppressLint("MissingPermission") NetworkCapabilities caps = cm.getNetworkCapabilities(value);
            if (caps != null && (caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN) || !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN))) {
                isRunningVPN = true;
                break;
            }
        }
        promise.resolve(isRunningVPN);
    }

  /**
   * Method detects if the device is rooted or not by using https://github.com/scottyab/rootbeer library
   *
   * @return true if the device is rooted
   */
   @ReactMethod
  public void isDeviceRooted(Promise promise) {
    RootBeer rootBeer = new RootBeer(getReactApplicationContext());
     promise.resolve(rootBeer.isRooted());
  }

   /**
     * Requires READ_CONTACTS permission
     * Method fetch total number contacts
     *
     * @return total number of contacts
     */
    @ReactMethod
    public void fetchContactsCount(Promise promise) {
         promise.resolve(ContactsFetcher.getTotalContactsCount(getReactApplicationContext()));
    }

    /**
     *  Method check if external SD card is attached with the device and calculate the size of the
     * external card
     *
     * @param forFreeStorage if true, then method will return only the available size;
     *                       otherwise return the total size
     * @return size of the external storage in GB
     */
    @ReactMethod
    public void getExternalSdCardSize(boolean forFreeStorage,Promise promise) {
      promise.resolve(StorageFetcher.getInstance().getExternalSdCardSize(forFreeStorage));
    }

}
