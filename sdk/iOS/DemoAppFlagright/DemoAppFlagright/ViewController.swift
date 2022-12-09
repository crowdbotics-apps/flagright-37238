//
//  ViewController.swift
//  DemoAppFlagright
//
//  Created by tanishq on 06/12/22.
//

import UIKit
import FrameworkFlagright
import CoreLocation
import Contacts
import ContactsUI

class ViewController: UIViewController, CLLocationManagerDelegate {
    
    var locationManager = CLLocationManager()
    let store = CNContactStore()
    let keysToFetch = [CNContactGivenNameKey]
    var contactArray = [String]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        let deviceData = DataCollection()

        
        print("The device ID is \(deviceData.deviceID)")
        print("The device language is \(deviceData.language ?? "Error")")
        print("The device country is \(deviceData.country ?? "Error")")
        print("The device localTimeZone \(deviceData.localTimeZoneAbbreviation)")
        print("The available RAM is \(deviceData.ram) GB")
        deviceData.availableMemory()
        deviceData.totalMemory()
        print("The device is \(deviceData.isSimulator())")
        print("The ios version is \(deviceData.systemVersion)")
        print("The model is \(deviceData.modelName)")
        print("The maker is \(deviceData.maker)")
        print("The battery is \(deviceData.getBattery())")
        print("The VPN connection status is \(deviceData.isConnectedToVpn)")
        
        print("The jailbreak status is \(deviceData.jailBreakStatus())")

        do {
             try store.enumerateContacts(with: CNContactFetchRequest.init(keysToFetch: keysToFetch as [CNKeyDescriptor]), usingBlock: { (contact, pointer) -> Void in
                // print("contact = ","\(contact.givenName)")
                 self.contactArray.append(contact.givenName)
             })
         } catch {
             print("something wrong happened in fetching contacts")
         }
        

        print("Total number of contacts are \(self.contactArray.count)")
        // Do any additional setup after loading the view.
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
           
           determineMyCurrentLocation()
    }
    
    func determineMyCurrentLocation() {
           locationManager = CLLocationManager()
           locationManager.delegate = self
           locationManager.desiredAccuracy = kCLLocationAccuracyBest
           locationManager.requestAlwaysAuthorization()
           
           if CLLocationManager.locationServicesEnabled() {

               locationManager.startUpdatingLocation()
               //locationManager.startUpdatingHeading()
           }
       }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
           let userLocation:CLLocation = locations[0] as CLLocation
           
           // Call stopUpdatingLocation() to stop listening for location updates,
           // other wise this function will be called every time when user location changes.
           
          manager.stopUpdatingLocation()
           
           print("user latitude = \(userLocation.coordinate.latitude)")
           print("user longitude = \(userLocation.coordinate.longitude)")
        
          switch locationManager.authorizationStatus {
             case .notDetermined, .restricted, .denied:
                 print("No access")
             case .authorizedAlways, .authorizedWhenInUse:
                 print("Access")
             @unknown default:
                 break
         }
       }
       
       func locationManager(_ manager: CLLocationManager, didFailWithError error: Error)
       {
           print("Error")
       }

}

