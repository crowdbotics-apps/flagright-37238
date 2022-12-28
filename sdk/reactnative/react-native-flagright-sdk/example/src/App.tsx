import * as React from 'react';

import { StyleSheet, View, Text } from 'react-native';
import {
  multiply,
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
} from 'react-native-flagright-sdk';

export default function App() {
  const [languageCode, setLanguageCode] = React.useState<string>();
  const [countryCode, setCountryCode] = React.useState<string>();
  const [timeZone, setTimeZone] = React.useState<string>();
  const [roamingEnabled, setRoamingEnabled] = React.useState<boolean>();
  const [accessibility, setAccessibility] = React.useState<boolean>();

  let batteryLevel = getBatteryLevel();
  if (batteryLevel > 0) {
    batteryLevel = batteryLevel * 100;
  }
  // const [fingerPrint, setFingerPrint] = React.useState<string | undefined>();

  React.useEffect(() => {
    getDeviceLocaleLanguageCode().then((code) => setLanguageCode(code));
    getDeviceLocaleCountry().then((country) => setCountryCode(country));
    getDeviceTimeZone().then((timeZone) => setTimeZone(timeZone));
    isDataRoamingEnabled().then((enabled) => setRoamingEnabled(enabled));
    isAccessibilityEnabled().then((enabled) => setAccessibility(enabled));
  }, []);

  return (
    <View style={styles.container}>
      <Text>UnqiueId: {getDeviceId()}</Text>
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
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
