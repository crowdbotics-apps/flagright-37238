package com.flagright;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.flagright.sdk.FlagRightInstance;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FlagRightInstance flagRightInstance = FlagRightInstance.getInstance();
        boolean isEmulator= flagRightInstance.isEmulator();

        TextView emulatorTextView = findViewById(R.id.emulator);
        emulatorTextView.setText("Is Emulator: "+isEmulator);

        TextView ip4TextView = findViewById(R.id.addressIP4);
        ip4TextView.setText("Ip4 Address: "+flagRightInstance.getIPAddress(true));
        TextView ip6TextView = findViewById(R.id.addressIP6);
        ip6TextView.setText("Ip6 Address: "+flagRightInstance.getIPAddress(false));
        TextView vpn = findViewById(R.id.vpn);
        vpn.setText("VPN connected: "+flagRightInstance.isDeviceConnectedToVPN(this));

        TextView biometric = findViewById(R.id.biometric);
        biometric.setText("Biometric enabled: "+flagRightInstance.isBioMetricEnabled(this));
        TextView rooted = findViewById(R.id.rooted);
        new Thread(() -> {
            boolean isRooted = flagRightInstance.isDeviceRooted(MainActivity.this);
            runOnUiThread(() -> {
                rooted.setText("Rooted: "+isRooted);
            });
        }).start();


    }
}