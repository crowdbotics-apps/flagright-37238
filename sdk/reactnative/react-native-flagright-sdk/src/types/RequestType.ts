export type LocationType = {
  latitude?: number;
  longitude?: number;
};

export type OperatingSystemType = {
  name: string;
  version?: string;
};

export type RequestType = {
  userId: string;
  timestamp: number;
  transactionId?: string;
  type: string;
  deviceFingerprint?: string;
  isVirtualDevice?: boolean;
  ipAddress?: string;
  location?: LocationType;
  totalNumberOfContacts?: number;
  batteryLevel?: number;
  externalTotalStorageInGb?: number;
  externalFreeStorageInGb?: number;
  manufacturer?: string;
  mainTotalStorageInGb?: number;
  model?: string;
  operatingSystem?: OperatingSystemType;
  deviceCountryCode?: string;
  deviceLaungageCode?: string;
  ramInGb?: number;
  isDataRoamingEnabled?: boolean;
  isLocationEnabled?: boolean;
  isAccessibilityEnabled?: boolean;
  isBluetoothActive?: boolean;
  networkOperator?: string;
};
