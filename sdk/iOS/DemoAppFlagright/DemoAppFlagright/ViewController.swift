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
import Accessibility
import Network
import CoreBluetooth
import LocalAuthentication
import SwiftUI

class ViewController: UIViewController, CLLocationManagerDelegate ,CBCentralManagerDelegate {
    func centralManagerDidUpdateState(_ central: CBCentralManager) {
        switch central.state {
        case .poweredOn:
            print("Bluetooth is ON.")
            break
        case .poweredOff:
            print("Bluetooth is Off.")
            break
        case .resetting:
            break
        case .unauthorized:
            break
        case .unsupported:
            break
        case .unknown:
            break
        default:
            break
        }
    }
    
    
    func getBiometricType() -> String {
        var biometricType: LABiometryType {
            let context = LAContext()
            var error: NSError?
            guard context.canEvaluatePolicy(.deviceOwnerAuthentication, error: &error) else { return .none }
            if #available(iOS 11.0, *) {
                switch context.biometryType {
                    case .touchID:
                        return .touchID
                    case .faceID:
                        return .faceID
                    case .none:
                        return .none
                }
            } else {
                guard context.canEvaluatePolicy(.deviceOwnerAuthentication, error: &error) else { return .none }
                return LABiometryType(rawValue: 5) ?? .none //context.canEvaluatePolicy(.deviceOwnerAuthentication, error: nil)
            }
        }
        if(biometricType.rawValue == 1 || biometricType.rawValue == 2){
            return "Biometrics supported"
        }
        else{
            return "Biometrics NOT supported"
    }
    }
    
    var locationManager = CLLocationManager()
    let store = CNContactStore()
    let keysToFetch = [CNContactGivenNameKey]
    var contactArray = [String]()
    var manager:CBCentralManager!

        
    override func viewDidLoad() {
        super.viewDidLoad()
        let deviceData = DataCollection()

        manager = CBCentralManager()
        manager.delegate = self
        
        print("The device ID is \(deviceData.deviceID)")
        print("The device language is \(deviceData.language ?? "Error")")
        print("The device country is \(deviceData.country ?? "Error")")
        print("The device localTimeZone \(deviceData.localTimeZoneAbbreviation)")
        print("The total RAM is \(deviceData.ram)")
        deviceData.availableMemory()
        deviceData.totalMemory()
        print("The device is \(deviceData.isSimulator())")
        print("The ios version is \(deviceData.systemVersion)")
        print("The model is \(deviceData.modelName)")
        print("The maker is \(deviceData.maker)")
        print("The battery is \(deviceData.getBattery())")
        print("The VPN connection status is \(deviceData.isConnectedToVpn)")
        print("The carrier is \(deviceData.carrier1 ?? "Error")")
       // print("The carrier is \(deviceData.carrier2)")

        print("Biometric type \(getBiometricType())")
        
        print("The jailbreak status is \(deviceData.jailBreakStatus())")
        
        print("Checking accesibility \(deviceData.checkAccessibilityEnabled())")
        print("IP adress \(getIPAddress() ?? "Not found")")
        
        let ipAddress:String = getIPAddress() ?? "0"
        if let _ = IPv4Address(ipAddress) {
            print("address \(ipAddress) is a valid IPv4 address")
        } else if let _ = IPv6Address(ipAddress) {
            print("address \(ipAddress) is a valid IPv6 address")
        } else {
            print("address \(ipAddress) is neither an IPv4 address nor an IPv6 address")
        }
    

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
    
    
    
    func getIPAddress() -> String? {
        var address : String?

        // Get list of all interfaces on the local machine:
        var ifaddr : UnsafeMutablePointer<ifaddrs>?
        guard getifaddrs(&ifaddr) == 0 else { return nil }
        guard let firstAddr = ifaddr else { return nil }

        // For each interface ...
        for ifptr in sequence(first: firstAddr, next: { $0.pointee.ifa_next }) {
            let interface = ifptr.pointee

            // Check for IPv4 or IPv6 interface:
            let addrFamily = interface.ifa_addr.pointee.sa_family
            if addrFamily == UInt8(AF_INET) || addrFamily == UInt8(AF_INET6) {

                // Check interface name:
                // wifi = ["en0"]
                // wired = ["en2", "en3", "en4"]
                // cellular = ["pdp_ip0","pdp_ip1","pdp_ip2","pdp_ip3"]
                
                let name = String(cString: interface.ifa_name)
                if  name == "en0" || name == "en2" || name == "en3" || name == "en4" || name == "pdp_ip0" || name == "pdp_ip1" || name == "pdp_ip2" || name == "pdp_ip3" {

                    // Convert interface address to a human readable string:
                    var hostname = [CChar](repeating: 0, count: Int(NI_MAXHOST))
                    getnameinfo(interface.ifa_addr, socklen_t(interface.ifa_addr.pointee.sa_len),
                                &hostname, socklen_t(hostname.count),
                                nil, socklen_t(0), NI_NUMERICHOST)
                    address = String(cString: hostname)
                }
            }
        }
        freeifaddrs(ifaddr)

        return address
    }

}

