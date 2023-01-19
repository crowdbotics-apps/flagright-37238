
# Flagright SDK Android

Flagright SDK gets the hardware releated information from a device and store it to our server for Analytics purpose.

## Integration

```bash
  To import this module into your project, proceed as follows:

    1. Click File > New > Import Module.
    2. In the Source directory box, browse to Flagright SDK.
    3. Click Finish.

Once the module is imported, it appears in the Project window on the left.
```
For more informtion regarding adding an exsting module into Android project, please visit this [link](https://developer.android.com/studio/projects/add-app-module)

#### Add the following permssions at your project Manifest file
```bash
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.READ_CONTACTS"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
<uses-permission android:name="android.permission.BLUETOOTH" android:required="false" />
```
The locations and the read contact permissions are comes under the dangerous permissions. So we also need to ask these two permissions at runtime. Please check this [link](https://developer.android.com/training/permissions/requesting) that describes how we can request the runtime permissions in Android.

##### Once we have the required permssions we can now consume the FlagrightSdk. In order to get the instance of FlagrightSdk add the following line:-
```bash
FlagrightDeviceMetricsSDK flagrightDeviceMetricsSDK = FlagrightDeviceMetricsSDK.getInstance();

```
##### after getting the instance call the init method
```bash
flagrightDeviceMetricsSDK.init( "123", "");
```
#### and then call emit method to send all the fetched attributes over Flagright server
```bash
flagrightDeviceMetricsSDK.emit(this, "1234", null,
                new ResponseCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "Attributes uploaded successfully",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.e("Error", errorMessage);
                    }
                });
```

## Methods
| Method | Description |
| ------ | ------ |
| getInstance() | Get the instance of the Flagright SDK. |
| init(apiKey, region) | Method initialize the api key and the region which is required for the server authentication. |
| emit(context, userId, transactionId, responseCallback) | Method gets all the required attributes from device and send them over server. |


