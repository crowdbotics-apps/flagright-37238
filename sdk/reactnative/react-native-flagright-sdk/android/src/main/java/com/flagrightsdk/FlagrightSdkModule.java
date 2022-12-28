package com.flagrightsdk;

import android.bluetooth.BluetoothAdapter;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;

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
//  public void isBluetoothEnabled(Promise promise) {
//    BluetoothResponseModal bluetoothResponseModal = new BluetoothResponseModal();
//    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//    if (mBluetoothAdapter == null) {
//      // Device does not support Bluetooth
//      bluetoothResponseModal.setEnable(false);
//      bluetoothResponseModal.setErrorMessage("Device does not support bluetooth");
//    } else if (!mBluetoothAdapter.isEnabled()) {
//      // Bluetooth is not enabled :)
//      bluetoothResponseModal.setEnable(false);
//    } else {
//      // Bluetooth is enabled
//      bluetoothResponseModal.setEnable(true);
//    }
//     promise.resolve(bluetoothResponseModal);
//  }
}
