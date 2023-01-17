# react-native-flagright-sdk

Flagright ReactNative SDK

## Installation

Download the react native flag right sdk (sdk/reactnative/react-native-flagright-sdk) and place it at the root folder of your project.
Add the following line at your package.json

```sh
"react-native-flagright-sdk": "file:./react-native-flagright-sdk",
```

and then install it

```sh
$ yarn install
```

After this, install the following dependencies

```sh
$ yarn add react-native-device-info react-native-bluetooth-status react-native-geolocation-service
```

## iOS

After installing the package, we need to install the pod

```sh
$ (cd ios && pod install)
# --- or ---
$ npx pod-install
```

Add NSLocationWhenInUseUsageDescription and NSContactsUsageDescription at info.plist with the appropriate explanation.

## Android

Add the following permssions at your project Manifest file

```sh
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.READ_CONTACTS"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
<uses-permission android:name="android.permission.BLUETOOTH" android:required="false" />
```

The locations and the read contact permissions are comes under the dangerous permissions. So we also need to ask these two permissions at runtime. Please check this [link](https://developer.android.com/training/permissions/requesting) that describes how we can request the runtime permissions in Android.

## Usage

```js
import { init } from 'react-native-flagright-sdk';

// ...

const result = await init('123', '1234');
```

With transaction id

```js
const result = await init('123', '1234', '12345678');
```

<!-- ## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob) -->
