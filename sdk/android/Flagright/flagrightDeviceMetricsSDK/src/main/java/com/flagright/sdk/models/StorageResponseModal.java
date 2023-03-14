package com.flagright.sdk.models;

public class StorageResponseModal {
    private boolean found;
    private double storageInGB;

    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    public double getStorageInGB() {
        return storageInGB;
    }

    public void setStorageInGB(double storageInGB) {
        this.storageInGB = storageInGB;
    }
}
