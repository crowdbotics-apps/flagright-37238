//
//  DataCollection.swift
//  FrameworkFlagright
//
//  Created by tanishq on 06/12/22.
//

import Foundation
import UIKit
import Contacts
import ContactsUI
import CoreTelephony
import Network
import LocalAuthentication
import CoreBluetooth

let networkInfo = CTTelephonyNetworkInfo()

public class DataCollection: NSObject, CBCentralManagerDelegate{
    
    var manager: CBCentralManager!
    var locationManager: LocationHandler!
    var checkLocationEnabled: Bool?
    var longitude: Double?
    var latitude: Double?
    
    let deviceID:String = UIDevice.current.identifierForVendor!.uuidString
    let language:String = NSLocale.current.languageCode ?? "Error getting language code"
    let country:String = NSLocale.current.regionCode ?? "Error getting country code"
    let ram:Int = (Int(ProcessInfo.processInfo.physicalMemory) / (1024 * 1024 * 1024))
    let systemOS = "iOS"
    let systemVersion = UIDevice.current.systemVersion
    let maker = "Apple"
    let modelName = UIDevice.modelName
    
    let store = CNContactStore()
    let keysToFetch = [CNContactGivenNameKey]
    var contactArray = [String]()

    let jailBreakStatus = UIDevice.isJailBroken(UIDevice.current)
        
    
    private override init() {
    }
    
// Singleton instance of DataCollections. Initiates all the attributes and should be called in App Delegate
    
    public static let shared = { () -> DataCollection in
        let instance = DataCollection()
        return instance
    } ()
    
    public func centralManagerDidUpdateState(_ central: CBCentralManager) {

    }
    
    
    public func sync(){

        self.manager = CBCentralManager(delegate: self, queue: nil)
        locationManager = LocationHandler()
        locationManager.determineMyCurrentLocation()
        checkLocationEnabled = locationManager.isLocationEnabled()
        longitude = locationManager.getLongitude()
        latitude = locationManager.getLatitude()
    
}
    
    func carrier() -> String {
        let carrier1  = Array(arrayLiteral: networkInfo.serviceSubscriberCellularProviders)[0]?.first?.value.carrierName
        let carrier2 = Array(arrayLiteral: networkInfo.serviceSubscriberCellularProviders)[0]?.reversed().first?.value.carrierName

         if carrier1 != nil
         {
             return carrier1 ?? "Error"
         }
         else if carrier2 != nil
         {
            return carrier2 ?? "Error"
         }
        return "Error getting carrier"
        
    }
    
    func getContacts() -> Int{
    do {
         try store.enumerateContacts(with: CNContactFetchRequest.init(keysToFetch: keysToFetch as [CNKeyDescriptor]), usingBlock: { (contact, pointer) -> Void in
             self.contactArray.append(contact.givenName)
         })
     } catch {
         print("something wrong happened in fetching contacts")
     }
        return contactArray.count
    }
       
    func isSimulator() -> Bool{
#if targetEnvironment(simulator)
  return true
#else
  return false
#endif
    }
    
    func getBiometricSupported() -> String {
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
    
     func checkAccessibilityEnabled()->Bool{
        if UIAccessibility.isAssistiveTouchRunning{
            return true
        }
        if UIAccessibility.isBoldTextEnabled{
            return true
        }
        if UIAccessibility.isClosedCaptioningEnabled{
            return true
        }
        if UIAccessibility.isDarkerSystemColorsEnabled{
            return true
        }
        if UIAccessibility.isGrayscaleEnabled{
            return true
        }
        if UIAccessibility.isGuidedAccessEnabled{
            return true
        }
        if UIAccessibility.isInvertColorsEnabled{
            return true
        }
        if UIAccessibility.isMonoAudioEnabled{
            return true
        }
        if UIAccessibility.isOnOffSwitchLabelsEnabled{
            return true
        }
        if UIAccessibility.isReduceMotionEnabled{
            return true
        }
        if UIAccessibility.isReduceTransparencyEnabled{
            return true
        }
        if UIAccessibility.isSpeakScreenEnabled{
            return true
        }
        if UIAccessibility.isSpeakSelectionEnabled{
            return true
        }
//        if UIAccessibility.isShakeToUndoEnabled{
//            return true
//        }
        if UIAccessibility.isSwitchControlRunning{
            return true
        }
//        if UIAccessibility.isVideoAutoplayEnabled{
//            return true
//        }
        if UIAccessibility.isVoiceOverRunning{
            return true
        }
        if UIAccessibility.shouldDifferentiateWithoutColor{
            return true
        }
        return false
    }
 
    
    func totalMemory()-> Int{
    let fileURL = URL(fileURLWithPath: NSHomeDirectory() as String)
    do {
        let values = try fileURL.resourceValues(forKeys: [.volumeTotalCapacityKey])
        if let capacity = values.volumeTotalCapacity {
            return (capacity / (1024 * 1024 * 1024))
        } else {
            print("Capacity is unavailable")
        }
    } catch {
        print("Error retrieving capacity: \(error.localizedDescription)")
    }
        return 0
    }
    
     var localTimeZoneAbbreviation: String { return TimeZone.current.abbreviation() ?? "" }
    
     func getBattery()->Int{
        UIDevice.current.isBatteryMonitoringEnabled = true
        let level = UIDevice.current.batteryLevel
        return Int(level*100)
    }
    
    var isConnectedToVpn: Bool {
        if let settings = CFNetworkCopySystemProxySettings()?.takeRetainedValue() as? Dictionary<String, Any>,
            let scopes = settings["__SCOPED__"] as? [String:Any] {
            for (key, _) in scopes {
             if key.contains("tap") || key.contains("tun") || key.contains("ppp") || key.contains("ipsec") {
                    return true
                }
            }
        }
        return false
    }
    
    func ipAddressType() -> String{
        let ipAddress:String = getIPAddress() ?? "0"
        if let _ = IPv4Address(ipAddress) {
            return "valid IPv4 address"
        } else if let _ = IPv6Address(ipAddress) {
           return "valid IPv6 address"
        } else {
            return "neither an IPv4 address nor an IPv6 address"
        }
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

        return address ?? "IP Address fetch failed"
    }
    
    func currentTimestamp()-> Int
        {
            let currentDate = Date()
            let since1970 = currentDate.timeIntervalSince1970
            return Int(since1970 * 1000)
        }
    
    public func apiCall(userId: String, type: String){
        let parameters = [
          "userId": userId,
          "timestamp": currentTimestamp(),
          "type": type,
          "transactionId": "string",
          "deviceFingerprint": deviceID,
          "isVirtualDevice": isSimulator(),
          "ipAddress": getIPAddress() ?? "Could not fetch IP",
          "location": [
            "latitude": latitude ?? 0.00,
            "longitude": longitude ?? 0.00
          ],
          "totalNumberOfContacts": getContacts(),
          "batteryLevel": getBattery(),
          "manufacturer": maker,
          "mainTotalStorageInGb": totalMemory(),
          "model": modelName,
          "operatingSystem": [
            "name": systemOS,
            "version": systemVersion
          ],
          "deviceCountryCode": country,
          "deviceLaungageCode": language,
          "ramInGb": ram,
          "isLocationEnabled": checkLocationEnabled ?? false,
          "isAccessibilityEnabled": checkAccessibilityEnabled(),
          "isBluetoothActive": manager.state == .poweredOn,
          "networkOperator": carrier()
        ] as [String : Any]
        print(parameters)
        makePostRequest(userId: userId, type: type, parameterDict: parameters)
        
    }
 
}
