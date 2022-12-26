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
} from 'react-native-flagright-sdk';

export default function App() {
  const [result, setResult] = React.useState<number | undefined>();
  const batteryLevel = React.useRef(getBatteryLevel());
  if (batteryLevel?.current > 0) {
    batteryLevel.current = batteryLevel?.current * 100;
  }
  // const [fingerPrint, setFingerPrint] = React.useState<string | undefined>();

  React.useEffect(() => {}, []);

  return (
    <View style={styles.container}>
      <Text>UnqiueId: {getDeviceId()}</Text>
      <Text>isEmulator: {isEmulator() + ''}</Text>
      <Text>IsLocationServciesEnabled: {isLocationEnabled() + ''}</Text>
      <Text>BioMetricEnabled: {isDeviceSecure() + ''}</Text>
      <Text>Battery Level: {batteryLevel?.current}</Text>
      <Text>Internal storage: {getTotalInternalStorage() + ' GB'}</Text>
      <Text>
        Internal storage Free: {getAvailableInternalStorage() + ' GB'}
      </Text>
      <Text>Modal: {getModalName()}</Text>
      <Text>Manufacture: {getManufactureName()}</Text>
      <Text>OS Version: {getOSVersion()}</Text>
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
