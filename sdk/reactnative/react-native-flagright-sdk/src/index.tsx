import { NativeModules, Platform } from 'react-native';
import DeviceInfo from 'react-native-device-info';

const LINKING_ERROR =
  `The package 'react-native-flagright-sdk' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const FlagrightSdk = NativeModules.FlagrightSdk
  ? NativeModules.FlagrightSdk
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function multiply(a: number, b: number): Promise<number> {
  return FlagrightSdk.multiply(a, b);
}

/**
 * This is the sensitive indentifier for teh Google or Huwawi Stores and may lead to the app rejection.
 * Before using it please refer the store's policies.
 * 
 * 

        iOS: This is IDFV or a random string if IDFV is unavaliable. Once UID is generated it is stored in iOS Keychain and NSUserDefaults. So it would stay the same even if you delete the app or reset IDFV. You can carefully consider it a persistent, cross-install unique ID. It can be changed only in case someone manually override values in Keychain/NSUserDefaults or if Apple would change Keychain and NSUserDefaults implementations. Beware: The IDFV is calculated using your bundle identifier and thus will be different in app extensions.
        android: Prior to Oreo, this id (ANDROID_ID) will always be the same once you set up your phone.
        android: Google Play policy, see "persistent device identifiers". Huawei - AppGallery Review Guidelines see "permanent device identifier" and "obtaining user consent".

 
 * @returns unique Id
 */
export function getDeviceId() {
  return DeviceInfo.getUniqueIdSync();
}

/**
 *  Method checks if the running devcie is emulator or not
 *
 * @returns true is the devcie is emulator otherwise false
 */
export function isEmulator() {
  return DeviceInfo.isEmulatorSync();
}

/**
 * Method checks if the location servcie is enabled or not
 *
 * @returns true is the location is enabled otherwise false
 */

export function isLocationEnabled() {
  return DeviceInfo.isLocationEnabledSync();
}

/**
 * Method checks if a PIN number or a fingerprint was set for the device.
 *
 * @returns true if device is having some security like pin or fingerprint
 */
export function isDeviceSecure() {
  return DeviceInfo.isPinOrFingerprintSetSync();
}

/**
 * Gets the battery level of the device as a float comprised between 0 and 1.
 * @returns ballter level betwwen 0 to 1
 *
 * Note: iOS simulator always returns -1
 */
export function getBatteryLevel() {
  return DeviceInfo.getBatteryLevelSync();
}

/**
 * Method gets the total internal memory in bytes (inlcuding both root
 * and data file system). Once the method get the total memory then it
 * convert into GB
 *
 * @returns total storage in GB
 */
export function getTotalInternalStorage() {
  return roundAvoid(
    DeviceInfo.getTotalDiskCapacitySync() / (1024 * 1024 * 1024),
    1
  );
}

/**
 * Method gets the available internal memory in bytes (inlcuding both root
 * and data file system). Once the method get the avaialble memory then it
 * convert into GB
 *
 * @returns available storage in GB
 */
export function getAvailableInternalStorage() {
  return roundAvoid(
    DeviceInfo.getFreeDiskStorageSync() / (1024 * 1024 * 1024),
    1
  );
}

/**
 * Method gets the modal name of a device
 *
 * @returns modal name
 */
export function getModalName() {
  return DeviceInfo.getModel();
}

/**
 * Method gets the manufacture name of a device
 *
 * @returns manufacture name
 */
export function getManufactureName() {
  return DeviceInfo.getManufacturerSync();
}

/**
 * Method gets the OS veriosn of a device
 *
 * @returns OS version
 */
export function getOSVersion() {
  return DeviceInfo.getSystemVersion();
}

export function getDeviceLocaleLanguageCode(): Promise<string> {
  try {
    return FlagrightSdk.getDeviceLocaleLanguageCode();
  } catch (ex) {
    return new Promise((resolve) => resolve('NA'));
  }
}

export function getDeviceLocaleCountry(): Promise<string> {
  try {
    return FlagrightSdk.getDeviceLocaleCountry();
  } catch (ex) {
    return new Promise((resolve) => resolve('NA'));
  }
}

export function getDeviceTimeZone(): Promise<string> {
  try {
    return FlagrightSdk.getDeviceTimeZone();
  } catch (ex) {
    return new Promise((resolve) => resolve('NA'));
  }
}

/**
 * Method gets the total RAM size
 * @returns total RAM of a device in GB
 */
export function getRamSize() {
  return roundAvoid(DeviceInfo.getTotalMemorySync() / (1024 * 1024 * 1024), 0);
}

export function isDataRoamingEnabled(): Promise<boolean> {
  try {
    return FlagrightSdk.isDataRoamingEnabled();
  } catch (ex) {
    return new Promise((resolve) => resolve(false));
  }
}

export function isAccessibilityEnabled(): Promise<boolean> {
  try {
    return FlagrightSdk.isAccessibilityEnabled();
  } catch (ex) {
    return new Promise((resolve) => resolve(false));
  }
}

function roundAvoid(value: number, places: number) {
  const scale = Math.pow(10, places);
  return Math.round(value * scale) / scale;
}
