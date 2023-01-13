//
//  LocationManager.swift
//  FrameworkFlagright
//
//  Created by tanishq on 12/01/23.
//
//
//import Foundation
//import CoreLocation
//
//public class LocationManager: NSObject, CLLocationManagerDelegate{
//    public static let shared = LocationManager()
//    let manager = CLLocationManager()
//    
//    var completion: ((CLLocation) -> Double)?
//    
//    public func getUserLocation(completion: @escaping ((CLLocation) -> Double)){
//        self.completion = completion
//        manager.requestWhenInUseAuthorization()
//        manager.delegate = self
//        manager.startUpdatingLocation()
//    }
//    
//    public func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
//        guard let location = locations.first else {
//            return
//        }
//        completion?(location)
//        manager.stopUpdatingLocation()
//    }
//}
