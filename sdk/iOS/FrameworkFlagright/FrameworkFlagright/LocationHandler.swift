//
//  LocationHandler.swift
//  FrameworkFlagright
//
//  Created by tanishq on 19/12/22.
//

import Foundation
import CoreLocation

public class LocationHandler: NSObject, CLLocationManagerDelegate{

var locationManager: CLLocationManager!

public override init() {
        super.init()
        locationManager = CLLocationManager()
    }

    public func determineMyCurrentLocation() {
             locationManager.delegate = self
             locationManager.desiredAccuracy = kCLLocationAccuracyBest
             locationManager.requestAlwaysAuthorization()

             if CLLocationManager.locationServicesEnabled() {

                 locationManager.startUpdatingLocation()
             }
         }

    public func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
       let userLocation: CLLocation = locations[0] as CLLocation

       // Call stopUpdatingLocation() to stop listening for location updates,
       // other wise this function will be called every time when user location changes.

      manager.stopUpdatingLocation()

   }

    public func getLongitude() -> Double {
        let longitude = CLLocationManager().location?.coordinate.longitude ?? 0.00
        return longitude
    }

    public func getLatitude() -> Double {
        let latitude = CLLocationManager().location?.coordinate.latitude ?? 0.00
        return latitude
    }

    public func isLocationEnabled() -> Bool{
        switch CLLocationManager().authorizationStatus {
         case .notDetermined, .restricted, .denied:
             return false
         case .authorizedAlways, .authorizedWhenInUse:
             return true
         @unknown default:
             break
     }
        return false
    }

  public func locationManager(_ manager: CLLocationManager, didFailWithError error: Error)
   {
       print("Error")
   }

}

