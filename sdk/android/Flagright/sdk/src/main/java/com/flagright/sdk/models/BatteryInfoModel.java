package com.flagright.sdk.models;

public class BatteryInfoModel {
    private  int level;
    private  int scale;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public float getBatteryPercentage() {
        return level * 100 / (float)scale;
    }
}
