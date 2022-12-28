//
//  LocationHandler.swift
//  FrameworkFlagright
//
//  Created by tanishq on 19/12/22.
//

import Foundation
import CoreLocation

public class LocationHandler: NSObject, CLLocationManagerDelegate{
    
var locationManager : CLLocationManager!

public override init() {
        super.init()
        locationManager = CLLocationManager()
    }
    
    public func determineMyCurrentLocation() {
            // locationManager = CLLocationManager()
             locationManager.delegate = self
             locationManager.desiredAccuracy = kCLLocationAccuracyBest
             locationManager.requestAlwaysAuthorization()

             if CLLocationManager.locationServicesEnabled() {

                 locationManager.startUpdatingLocation()
                 //locationManager.startUpdatingHeading()
             }
         }
    
    public func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
       let userLocation:CLLocation = locations[0] as CLLocation

       // Call stopUpdatingLocation() to stop listening for location updates,
       // other wise this function will be called every time when user location changes.
    
      manager.stopUpdatingLocation()

       print("user latitude = \(userLocation.coordinate.latitude)")
       print("user longitude = \(userLocation.coordinate.longitude)")

      switch manager.authorizationStatus {
         case .notDetermined, .restricted, .denied:
             print("No access")
         case .authorizedAlways, .authorizedWhenInUse:
             print("Access")
         @unknown default:
             break
     }
   }

  public func locationManager(_ manager: CLLocationManager, didFailWithError error: Error)
   {
       print("Error")
   }

}
