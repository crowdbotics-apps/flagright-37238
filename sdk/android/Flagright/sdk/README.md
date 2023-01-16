
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
FlagRightInstance flagRightInstance = FlagRightInstance.getInstance();

```
##### after getting the instance call the init method
```bash
flagRightInstance.init(this, "123", "1234", null, new ResponseCallback() {
            @Override
            public void onSuccess() {
                // successfully uploaded the required attributes
            }

            @Override
            public void onFailure(String errorMessage) {
                // error in the call
            }
    });
```

## Methods
| Method | Description |
| ------ | ------ |
| getInstance() | Get the instance of the Flagright SDK |
| init(context, apiKey, userId, transactionId, responseCallback) | Method fetch all the required attributes and send them to the Flagright Server. |


## Integration



```bash
  To import this module into your project, proceed as follows:

    1. Click File > New > Import Module.
    2. In the Source directory box, browse to Flagright SDK.
    3. Click Finish.

Once the module is imported, it appears in the Project window on the left.
```
For more informtion regarding adding an exsting module into Android project, please visit this [link](https://developer.android.com/studio/projects/add-app-module).


## Features

- isEmulator()
- Get IPv4 and IPv6 address
- 
- Cross platform

