import { NativeModules, Platform } from 'react-native';
import DeviceInfo from 'react-native-device-info';
import Geolocation from 'react-native-geolocation-service';
import { BluetoothStatus } from 'react-native-bluetooth-status';
import type { BluetoothResponseType } from './types/BluetoothResponseModalType';
import type { GeolocationType } from './types/GeolocationType';
import type { LocationType, RequestType } from './types/RequestType';
import { sendData } from './services/APIHandler';

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

enum Type {
  TRANSACTION = 'TRANSACTION',
  USER_SIGNUP = 'USER_SIGNUP',
}

export async function init(
  apiKey: string,
  userId: string,
  transactionId: string | undefined = undefined
): Promise<any> {
  let requestType: RequestType = {
    userId,
    type: transactionId ? Type.TRANSACTION : Type.USER_SIGNUP,
    timestamp: Date.now(),
  };
  requestType.transactionId = transactionId;
  try {
    requestType.deviceFingerprint = await getDeviceId();
    requestType.isVirtualDevice = isEmulator();
    requestType.ipAddress = await getIPAddress(true);
    const geolocation = await getCurrentLocation();
    if (geolocation.success) {
      const location: LocationType = {
        latitude: geolocation?.position?.coords?.latitude,
        longitude: geolocation?.position?.coords?.longitude,
      };
      requestType.location = location;
    }
    requestType.totalNumberOfContacts = await fetchContactsCount();
    requestType.batteryLevel = await getBatteryLevel();
    if (requestType.batteryLevel > 0) {
      requestType.batteryLevel = requestType.batteryLevel * 100;
    }
    requestType.externalTotalStorageInGb = await getExternalSdCardSize(false);
    requestType.externalFreeStorageInGb = await getExternalSdCardSize(true);
    requestType.manufacturer = getManufactureName();
    requestType.mainTotalStorageInGb = getTotalInternalStorage();
    requestType.model = getModalName();
    requestType.operatingSystem = {
      name: Platform.OS === 'ios' ? 'iOS' : 'Android',
      version: getOSVersion(),
    };
    requestType.deviceCountryCode = await getDeviceLocaleCountry();
    requestType.deviceLaungageCode = await getDeviceLocaleLanguageCode();
    requestType.ramInGb = getRamSize();
    requestType.isDataRoamingEnabled = await isDataRoamingEnabled();
    requestType.isLocationEnabled = isLocationEnabled();
    requestType.isAccessibilityEnabled = await isAccessibilityEnabled();
    const bluetoothObj = await isBluetoothEnabled();
    requestType.isBluetoothActive = bluetoothObj.enable;
    requestType.networkOperator = getNetworkOperatorName();
  } catch (ex) {
    console.error('Error', ex);
  }
  console.log('RequestParams', JSON.stringify(requestType));
  return sendData(apiKey, requestType);
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
export function getDeviceId(): Promise<string> {
  if (Platform.OS === 'android') {
    return FlagrightSdk.getFingerprint();
  } else return new Promise((resolve) => resolve(DeviceInfo.getUniqueIdSync()));
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

async function getBluetoothStatus() {
  const isEnabled = await BluetoothStatus.state();
  return isEnabled;
}
/**
 * <uses-permission android:name="android.permission.BLUETOOTH" android:required="false" />
 * Method checks if bluetooth enabled
 * @returns Promise with true or false. True if bluetooth is enabled
 */
export function isBluetoothEnabled(): Promise<BluetoothResponseType> {
  try {
    if (Platform.OS === 'ios') {
      return new Promise((resolve) => {
        getBluetoothStatus()
          .then((btStatus) => {
            resolve({ enable: btStatus ?? false } as BluetoothResponseType);
          })
          .catch((er) => {
            resolve({ enable: false } as BluetoothResponseType);
            console.error('error', er);
          });
      });
    } else return FlagrightSdk.isBluetoothEnabled();
  } catch (ex) {
    return new Promise((resolve) =>
      resolve({ enable: false, errorMessage: 'NA' })
    );
  }
}

export function getNetworkOperatorName() {
  return DeviceInfo.getCarrierSync();
}

export function getCurrentLocation(
  options: Geolocation.GeoOptions = {
    enableHighAccuracy: true,
    timeout: 15000,
    maximumAge: 10000,
  }
): Promise<GeolocationType> {
  return new Promise((resolve) => {
    if (Platform.OS === 'ios') {
      Geolocation.requestAuthorization('whenInUse')
        .then((value) => {
          if (value === 'granted') {
            Geolocation.getCurrentPosition(
              (position) => resolve({ success: true, position }),
              (error) => resolve({ success: false, error }),
              options
            );
          } else {
            resolve({ success: false });
          }
        })
        .catch((ex: any) => {
          console.error('Error', ex);
          resolve({ success: false });
        });
    } else
      Geolocation.getCurrentPosition(
        (position) => resolve({ success: true, position }),
        (error) => resolve({ success: false, error }),
        options
      );
  });
}

/**
 * Method returns the iP address
 *
 * @param useIPV4 true for iPV4 address; otherwise get iPV6 address
 * @returns ipAddress (either iPV4 or iPV6)
 */
export function getIPAddress(useIPV4: boolean): Promise<string> {
  try {
    if (Platform.OS === 'ios') return FlagrightSdk.getIPAddress();
    else return FlagrightSdk.getIPAddress(useIPV4);
  } catch (ex) {
    return new Promise((resolve) => resolve('NA'));
  }
}

/**
 * For Android: Permission required ACCESS_NETWORK_STATE
 * Methods checks if the device is connected to VPN or not
 *
 * @returns true if the device is connected to VPN
 */

export function isDeviceConnectedToVPN(): Promise<boolean> {
  try {
    return FlagrightSdk.isDeviceConnectedToVPN();
  } catch (ex) {
    return new Promise((resolve) => resolve(false));
  }
}

/**
 * Method detects if the device is jailbreak (or rooted)
 *
 * @returns true if the device is jailbreak or rooted
 */
export function isDeviceRooted(): Promise<boolean> {
  try {
    return FlagrightSdk.isDeviceRooted();
  } catch (ex) {
    return new Promise((resolve) => resolve(false));
  }
}

/**
 * Requires READ_CONTACTS permission
 * Method fetch total number contacts
 *
 * For iOS: required to add NSContactsUsageDescription key with the valid reason at Info.plist
 *
 * @return total number of contacts
 */
export function fetchContactsCount(): Promise<number> {
  console.log('Calling fetchContactsCount', Platform.OS);
  try {
    return FlagrightSdk.fetchContactsCount();
  } catch (ex) {
    console.log('Error', ex);
    return new Promise((resolve) => resolve(0));
  }
}

/**
 *  Method check if external SD card is attached with the device and calculate the size of the
 * external card
 *
 * @param forFreeStorage if true, then method will return only the available size;
 *                       otherwise return the total size
 * @return size of the external storage in GB
 */
export function getExternalSdCardSize(
  forFreeStorage: boolean
): Promise<number> {
  try {
    return FlagrightSdk.getExternalSdCardSize(forFreeStorage);
  } catch (ex) {
    return new Promise((resolve) => resolve(0));
  }
}

function roundAvoid(value: number, places: number) {
  const scale = Math.pow(10, places);
  return Math.round(value * scale) / scale;
}
