import type { GeoPosition, GeoError } from 'react-native-geolocation-service';

export type GeolocationType = {
  success: boolean;
  position?: GeoPosition;
  error?: GeoError;
};
