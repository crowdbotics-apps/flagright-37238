import * as React from 'react';

import {
  StyleSheet,
  View,
  Text,
  Platform,
  PermissionsAndroid,
  SafeAreaView,
} from 'react-native';
import {
  getDeviceId,
  isEmulator,
  isLocationEnabled,
  isDeviceSecure,
  getBatteryLevel,
  getTotalInternalStorage,
  getAvailableInternalStorage,
  getModalName,
  getManufactureName,
  getOSVersion,
  getDeviceLocaleLanguageCode,
  getDeviceLocaleCountry,
  getDeviceTimeZone,
  getRamSize,
  isDataRoamingEnabled,
  isAccessibilityEnabled,
  isBluetoothEnabled,
  getNetworkOperatorName,
  getCurrentLocation,
  getIPAddress,
  isDeviceConnectedToVPN,
  isDeviceRooted,
  fetchContactsCount,
  getExternalSdCardSize,
  init,
  Region,
  emit,
} from 'react-native-flagright-sdk';
import type { BluetoothResponseType } from 'src/types/BluetoothResponseModalType';
import type { GeolocationType } from 'src/types/GeolocationType';

export default function App() {
  const [deviceId, setDeviceId] = React.useState<string>();
  const [languageCode, setLanguageCode] = React.useState<string>();
  const [countryCode, setCountryCode] = React.useState<string>();
  const [timeZone, setTimeZone] = React.useState<string>();
  const [roamingEnabled, setRoamingEnabled] = React.useState<boolean>();
  const [accessibility, setAccessibility] = React.useState<boolean>();
  const [bluetoothEnabled, setBluetoothEnabled] =
    React.useState<BluetoothResponseType>();
  const [location, setLocation] = React.useState<GeolocationType>();
  const [ipv4, setIPv4] = React.useState<string>('');
  const [ipv6, setIPv6] = React.useState<string>('');
  const [isVpnConnected, toggleVpnConnect] = React.useState<boolean>(false);
  const [isDeviceRootedFlag, toggleDeviceRooted] =
    React.useState<boolean>(false);
  const [totalContacts, setTotalContacts] = React.useState<number>(0);
  const [externalStorage, setExternalStorage] = React.useState<{
    total?: number;
    free?: number;
  }>({ total: 0, free: 0 });

  let batteryLevel = getBatteryLevel();
  if (batteryLevel > 0) {
    batteryLevel = batteryLevel * 100;
  }
  // const [fingerPrint, setFingerPrint] = React.useState<string | undefined>();

  React.useEffect(() => {
    init('123', Region.US1)
      .then(() => {
        // emit('1234').then(() => console.log('SDK initialize successfully'));
      })
      .catch((er) => console.log('Error', er));

    emit('1234')
      .then(() => console.log('Attributes uploaded successfully'))
      .catch((e) => console.error('error', e));

    getDeviceId().then((id) => setDeviceId(id));
    getDeviceLocaleLanguageCode().then((code) => setLanguageCode(code));
    getDeviceLocaleCountry().then((country) => setCountryCode(country));
    getDeviceTimeZone().then((timeZone) => setTimeZone(timeZone));
    isDataRoamingEnabled().then((enabled) => setRoamingEnabled(enabled));
    isAccessibilityEnabled().then((enabled) => setAccessibility(enabled));
    isBluetoothEnabled().then((bluetoothObj) => {
      // console.log('bluetoothObj', bluetoothObj);
      setBluetoothEnabled(bluetoothObj);
    });
    if (Platform.OS === 'android') {
      requestLocationPermission();
    } else {
      fetchContactsCount().then((count) => setTotalContacts(count));
      getCurrentLocation().then((loc) => setLocation(loc));
    }
    getIPAddress(true).then((address) => setIPv4(address));
    getIPAddress(false).then((address) => setIPv6(address));
    isDeviceConnectedToVPN().then((flag) => toggleVpnConnect(flag));
    isDeviceRooted().then((flag) => toggleDeviceRooted(flag));
    getExternalSdCardSize(false).then((size) =>
      setExternalStorage({ total: size })
    );
    getExternalSdCardSize(true).then((size) =>
      setExternalStorage({ free: size })
    );
  }, []);

  const requestLocationPermission = async () => {
    try {
      // const granted = await PermissionsAndroid.request(
      //   'android.permission.ACCESS_FINE_LOCATION',
      //   {
      //     title: 'App Location Permission',
      //     message: 'App needs access to your Location.',
      //     buttonNeutral: 'Ask Me Later',
      //     buttonNegative: 'Cancel',
      //     buttonPositive: 'OK',
      //   }
      // );
      // if (granted === PermissionsAndroid.RESULTS.GRANTED) {
      //   console.log('You can use the Location');
      //   try {
      //     const locationResponse = await getCurrentLocation();
      //     if (locationResponse.success) {
      //       setLocation(locationResponse);
      //     }
      //   } catch (ex) {
      //     console.error(ex);
      //   }
      // } else {
      //   console.log('location permission denied');
      // }
      const granted = await PermissionsAndroid.requestMultiple([
        'android.permission.ACCESS_FINE_LOCATION',
        'android.permission.READ_CONTACTS',
      ]);
      if (granted['android.permission.ACCESS_FINE_LOCATION'] === 'granted') {
        try {
          const locationResponse = await getCurrentLocation();
          if (locationResponse.success) {
            setLocation(locationResponse);
          }
        } catch (ex) {
          console.error(ex);
        }
      }
      if (granted['android.permission.READ_CONTACTS'] === 'granted') {
        try {
          const count = await fetchContactsCount();
          setTotalContacts(count);
        } catch (ex) {
          console.error(ex);
        }
      }
    } catch (err) {
      console.warn(err);
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <View>
        <Text>isEmulator: {isEmulator() + ''}</Text>
        <Text>IsLocationServciesEnabled: {isLocationEnabled() + ''}</Text>
        <Text>BioMetricEnabled: {isDeviceSecure() + ''}</Text>
        <Text>Battery Level: {batteryLevel}</Text>
        <Text>Internal storage: {getTotalInternalStorage() + ' GB'}</Text>
        <Text>
          Internal storage Free: {getAvailableInternalStorage() + ' GB'}
        </Text>
        <Text>Modal: {getModalName()}</Text>
        <Text>Manufacture: {getManufactureName()}</Text>
        <Text>OS Version: {getOSVersion()}</Text>
        <Text>Language Code: {languageCode}</Text>
        <Text>Country Code: {countryCode}</Text>
        <Text>TimeZone: {timeZone}</Text>
        <Text>RAM: {getRamSize() + ' GB'}</Text>
        <Text>Roaming Enabled: {roamingEnabled + ''}</Text>
        <Text>Accessibility Enabled: {accessibility + ''}</Text>
        <Text>Bluetooth Enabled: {bluetoothEnabled?.enable + ''}</Text>
        <Text>Network Operator: {getNetworkOperatorName()}</Text>
        {location?.success ? (
          <Text>
            Location:{' '}
            {location?.position?.coords?.latitude +
              ', ' +
              location?.position?.coords?.longitude}
          </Text>
        ) : (
          <Text>Location: NA</Text>
        )}
        <Text>iPV4: {ipv4}</Text>
        <Text>iPV6: {ipv6}</Text>
        <Text>VPN Connected: {isVpnConnected + ''}</Text>
        <Text>Rooted: {isDeviceRootedFlag + ''}</Text>
        <Text>Total Contacts: {totalContacts + ''}</Text>
        {externalStorage?.total ? (
          <>
            <Text>{`Total External Storage: ${externalStorage?.total} + GB`}</Text>
            <Text>Free External Storage: {externalStorage?.free + ' GB'}</Text>
          </>
        ) : (
          <Text>Total External Storage: NA</Text>
        )}
        <Text>UnqiueId: {deviceId}</Text>
        {/* <Button
          title="call"
          onPress={() => {
            init('123', '1234')
              .then(() => {
                console.log('Data submitted successfully');
              })
              .catch((error) => console.log('Error', error));
          }}
        /> */}
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    marginHorizontal: 20,
    marginVertical: 20,
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
