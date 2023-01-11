package com.flagright.sdk.models;

public class RequestModal {

    public static class Location {
        private double latitude;
        private double longitude;

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }

    public static class OperatingSystem {
        private String name;
        private String version;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }


    private String userId;
    private long timestamp;
    private String type;
    private String deviceLaungageCode;
    private String deviceFingerprint;
    private boolean isVirtualDevice;
    private String ipAddress;
    private Location location;
    private int totalNumberOfContacts;
    private int batteryLevel;
    private double externalTotalStorageInGb;
    private double externalFreeStorageInGb;
    private String manufacturer;
    private double mainTotalStorageInGb;
    private String model;
    private OperatingSystem operatingSystem;
    private String deviceCountryCode;
    private double ramInGb;
    private boolean isDataRoamingEnabled;
    private boolean isLocationEnabled;
    private boolean isAccessibilityEnabled;
    private boolean isBluetoothActive;
    private String networkOperator;

    public String getNetworkOperator() {
        return networkOperator;
    }

    public void setNetworkOperator(String networkOperator) {
        this.networkOperator = networkOperator;
    }

    public boolean isBluetoothActive() {
        return isBluetoothActive;
    }

    public void setBluetoothActive(boolean bluetoothActive) {
        isBluetoothActive = bluetoothActive;
    }

    public boolean isAccessibilityEnabled() {
        return isAccessibilityEnabled;
    }

    public void setAccessibilityEnabled(boolean accessibilityEnabled) {
        isAccessibilityEnabled = accessibilityEnabled;
    }

    public boolean isLocationEnabled() {
        return isLocationEnabled;
    }

    public void setLocationEnabled(boolean locationEnabled) {
        isLocationEnabled = locationEnabled;
    }

    public boolean isDataRoamingEnabled() {
        return isDataRoamingEnabled;
    }

    public void setDataRoamingEnabled(boolean dataRoamingEnabled) {
        isDataRoamingEnabled = dataRoamingEnabled;
    }

    public double getRamInGb() {
        return ramInGb;
    }

    public void setRamInGb(double ramInGb) {
        this.ramInGb = ramInGb;
    }

    public String getDeviceCountryCode() {
        return deviceCountryCode;
    }

    public void setDeviceCountryCode(String deviceCountryCode) {
        this.deviceCountryCode = deviceCountryCode;
    }

    public OperatingSystem getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(OperatingSystem operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getMainTotalStorageInGb() {
        return mainTotalStorageInGb;
    }

    public void setMainTotalStorageInGb(double mainTotalStorageInGb) {
        this.mainTotalStorageInGb = mainTotalStorageInGb;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public double getExternalFreeStorageInGb() {
        return externalFreeStorageInGb;
    }

    public void setExternalFreeStorageInGb(double externalFreeStorageInGb) {
        this.externalFreeStorageInGb = externalFreeStorageInGb;
    }

    public double getExternalTotalStorageInGb() {
        return externalTotalStorageInGb;
    }

    public void setExternalTotalStorageInGb(double externalTotalStorageInGb) {
        this.externalTotalStorageInGb = externalTotalStorageInGb;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public int getTotalNumberOfContacts() {
        return totalNumberOfContacts;
    }

    public void setTotalNumberOfContacts(int totalNumberOfContacts) {
        this.totalNumberOfContacts = totalNumberOfContacts;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean isVirtualDevice() {
        return isVirtualDevice;
    }

    public void setVirtualDevice(boolean virtualDevice) {
        isVirtualDevice = virtualDevice;
    }

    public String getDeviceFingerprint() {
        return deviceFingerprint;
    }

    public void setDeviceFingerprint(String deviceFingerprint) {
        this.deviceFingerprint = deviceFingerprint;
    }

    public String getDeviceLaungageCode() {
        return deviceLaungageCode;
    }

    public void setDeviceLaungageCode(String deviceLaungageCode) {
        this.deviceLaungageCode = deviceLaungageCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
