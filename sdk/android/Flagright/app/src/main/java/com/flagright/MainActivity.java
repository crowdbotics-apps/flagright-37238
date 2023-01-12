package com.flagright;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.flagright.sdk.FlagRightInstance;
import com.flagright.sdk.interfaces.LocationFoundCallback;
import com.flagright.sdk.interfaces.ResponseCallback;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private TextView locationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FlagRightInstance flagRightInstance = FlagRightInstance.getInstance();
        boolean isEmulator = flagRightInstance.isEmulator();

        TextView emulatorTextView = findViewById(R.id.emulator);
        emulatorTextView.setText("Is Emulator: " + isEmulator);

        TextView ip4TextView = findViewById(R.id.addressIP4);
        ip4TextView.setText("Ip4 Address: " + flagRightInstance.getIPAddress(true));
        TextView ip6TextView = findViewById(R.id.addressIP6);
        ip6TextView.setText("Ip6 Address: " + flagRightInstance.getIPAddress(false));
        TextView vpn = findViewById(R.id.vpn);
        vpn.setText("VPN connected: " + flagRightInstance.isDeviceConnectedToVPN(this));

        TextView biometric = findViewById(R.id.biometric);
        biometric.setText("Biometric enabled: " + flagRightInstance.isBioMetricEnabled(this));
        TextView rooted = findViewById(R.id.rooted);
        locationTextView = findViewById(R.id.location);
        new Thread(() -> {
            boolean isRooted = flagRightInstance.isDeviceRooted(MainActivity.this);
            runOnUiThread(() -> {
                rooted.setText("Rooted: " + isRooted);
            });
        }).start();

        requestAppPermissions();

        fetchContacts();

        // for location
        initiateLocation();

        // get Battery level
        TextView batteryLevel = findViewById(R.id.batteryLevel);
        batteryLevel.setText(new StringBuilder()
                .append("Battery Level: ")
                .append(flagRightInstance.getBatteryLevel(this).getLevel()));

       // total internal storage
        TextView internalStorageTextView = findViewById(R.id.internalStorage);
        internalStorageTextView.setText(new StringBuilder()
                .append("Storage: ")
                .append(flagRightInstance.getTotalInternalStorage()).append(" GB"));

        // free internal storage
        TextView freeInternalStorageTextView = findViewById(R.id.freeInternal);
        freeInternalStorageTextView.setText(new StringBuilder()
                .append("Free Storage: ")
                .append(flagRightInstance.getFreeInternalStorage()).append(" GB"));

        // total external storage
        TextView totalExternalStorage = findViewById(R.id.totalExternalStorage);
        totalExternalStorage.setText(new StringBuilder()
                .append("External Storage: ")
                .append(flagRightInstance.getExternalSdCardSize(false)).append(" GB"));
        //free external storage
        TextView freeExternalStorage = findViewById(R.id.freeExternalStorage);
        freeExternalStorage.setText(new StringBuilder()
                .append("External Storage Free: ")
                .append(flagRightInstance.getExternalSdCardSize(true)).append(" GB"));
        // get Modal name
        TextView modal = findViewById(R.id.modal);
        modal.setText(new StringBuilder()
                .append("Modal: ")
                .append(flagRightInstance.getModalName()));
        // get Manufacture name
        TextView manufacture = findViewById(R.id.manufacture);
        manufacture.setText(new StringBuilder()
                .append("Manufacture: ")
                .append(flagRightInstance.getManufactureName()));
        // get OS version
        TextView osVersion = findViewById(R.id.os);
        osVersion.setText(new StringBuilder()
                .append("OS: ")
                .append(flagRightInstance.getOSVersion()));
        // get language version
        TextView languageCode = findViewById(R.id.languageCode);
        languageCode.setText(new StringBuilder()
                .append("Language: ")
                .append(flagRightInstance.getDeviceLocaleLanguageCode()));
        // get country code
        TextView countryCode = findViewById(R.id.countryCode);
        countryCode.setText(new StringBuilder()
                .append("Country Code: ")
                .append(flagRightInstance.getDeviceLocaleCountry()));
        // get timezone
        TextView timeZone = findViewById(R.id.timeZone);
        timeZone.setText(new StringBuilder()
                .append("TimeZone: ")
                .append(flagRightInstance.getDeviceTimeZone()));
        // get ram size
        TextView ramSize = findViewById(R.id.ramSize);
        ramSize.setText(new StringBuilder()
                .append("RAM: ")
                .append(flagRightInstance.getRamSize(this))
                .append(" GB"));
        // data roaming
        TextView roaming = findViewById(R.id.roaming);
        roaming.setText(new StringBuilder()
                .append("Roaming enabled: ")
                .append(flagRightInstance.isDataRoamingEnabled(this)));
        // Accessibility
        TextView accessibility = findViewById(R.id.accessibility);
        accessibility.setText(new StringBuilder()
                .append("Accessibility enabled: ")
                .append(flagRightInstance.isAccessibilityEnabled(this)));
        // Bluetooth
        TextView bluetoothEnabled = findViewById(R.id.bluetooth);
        bluetoothEnabled.setText(new StringBuilder()
                .append("Bluetooth enabled: ")
                .append(flagRightInstance.isBluetoothEnabled().isEnable()));
        // Network Operator
        TextView networkOperator = findViewById(R.id.networkOperator);
        networkOperator.setText(new StringBuilder()
                .append("Network Operator: ")
                .append(flagRightInstance.getNetworkOperatorName(this)));
        // Fingerprint
        TextView fingerPrint = findViewById(R.id.fingerPrint);
        fingerPrint.setText(new StringBuilder()
                .append("Fingerprint: ")
                .append(flagRightInstance.getFingerprint()));
        System.out.println("FingerPrint: "+flagRightInstance.getFingerprint());

        FlagRightInstance.getInstance().init(this, "123", "1234", null, new ResponseCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "Data uploaded successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestAppPermissions() {
        ArrayList<String> permissions = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_CONTACTS);
        }
        if (permissions.size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String[] permissionStrings = new String[permissions.size()];
                for (int i =0; i<permissions.size(); i++) {
                    permissionStrings[i] = permissions.get(i);
                }
                requestPermissions(permissionStrings, 1001);
            }
        }
    }

    private void initiateLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fetchLocation();
        }
    }

    private void fetchLocation() {
        FlagRightInstance.getInstance().fetchCurrentLocation(this, new LocationFoundCallback() {
            @Override
            public void locationFound(Location location) {
                Log.i("Location", location.toString());
                locationTextView.setText(new StringBuilder().append("Location: ")
                        .append(location.getLatitude()).append(", ")
                        .append(location.getLongitude()).toString());
            }

            @Override
            public void locationError(String error) {
                locationTextView.setText(new StringBuilder().append("Location: ").append(error));
            }
        });

        TextView locationEnTextView = findViewById(R.id.locationEnabled);
        locationEnTextView.setText(new StringBuilder().append("Location Enabled: ")
                .append(FlagRightInstance.getInstance().isLocationEnabled(this)));
    }

    private void fetchContacts() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            TextView contactsCount = findViewById(R.id.contactsCount);
            contactsCount.setText(new StringBuilder().append("Total Contacts: ")
                    .append(FlagRightInstance.getInstance().fetchContactsCount(this)).toString());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("grantResults", Arrays.toString(grantResults));
        Log.i("permissions", Arrays.toString(permissions));
        switch (requestCode) {
            case 1001:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 1 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1]== PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    fetchLocation();
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the feature requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    locationTextView.setText(new StringBuilder().append("Location: App does not have location permission"));
                }
                if (grantResults.length>2 && grantResults[2]== PackageManager.PERMISSION_GRANTED) {
                    fetchContacts();
                }
                return;

        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

}


