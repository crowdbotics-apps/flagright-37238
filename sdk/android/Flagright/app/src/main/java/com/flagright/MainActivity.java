package com.flagright;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.flagright.sdk.FlagRightInstance;

public class MainActivity extends AppCompatActivity {

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
        new Thread(() -> {
            boolean isRooted = flagRightInstance.isDeviceRooted(MainActivity.this);
            runOnUiThread(() -> {
                rooted.setText("Rooted: " + isRooted);
            });
        }).start();

        checkContactsPermission();
    }

    private void fetchContacts() {
        TextView contactsCount = findViewById(R.id.contactsCount);
        contactsCount.setText(new StringBuilder().append("Total Contacts: ").append(FlagRightInstance.getInstance().fetchContactsCount(this)).toString());
    }

    private void checkContactsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            fetchContacts();

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.
//                showInContextUI(...);
            } else {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 1001);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1001:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    fetchContacts();
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the feature requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

}


