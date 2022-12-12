package com.flagright.sdk;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.flagright.sdk.models.BatteryInfoModel;

class BatteryFetcher {
    public BatteryInfoModel getBatteryInfo(Context context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        BatteryInfoModel batteryInfoModel = new BatteryInfoModel();
        batteryInfoModel.setLevel(level);
        batteryInfoModel.setScale(scale);

        return batteryInfoModel;
    }
}
