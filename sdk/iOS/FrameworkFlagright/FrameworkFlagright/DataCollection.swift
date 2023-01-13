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

let networkInfo = CTTelephonyNetworkInfo()

public class DataCollection{
    
    public init() {
    }
     
    public let deviceID = UIDevice.current.identifierForVendor!.uuidString
    public let language = NSLocale.current.languageCode
    public let country = NSLocale.current.regionCode
    public let ram = ProcessInfo.processInfo.physicalMemory
    public let systemVersion = UIDevice.current.systemVersion
    public let maker = "Apple"
    public let modelName = UIDevice.modelName

    public let jailBreakStatus = UIDevice.isJailBroken(UIDevice.current)
    
    public let carrier:String? = Array(arrayLiteral: networkInfo.serviceSubscriberCellularProviders)[0]?.first?.value.carrierName
    
    let store = CNContactStore()
    let keysToFetch = [CNContactGivenNameKey]
    var contactArray = [String]()
    
    public func getContacts() -> Int{
    do {
         try store.enumerateContacts(with: CNContactFetchRequest.init(keysToFetch: keysToFetch as [CNKeyDescriptor]), usingBlock: { (contact, pointer) -> Void in
            // print("contact = ","\(contact.givenName)")
             self.contactArray.append(contact.givenName)
         })
     } catch {
         print("something wrong happened in fetching contacts")
     }
        return self.contactArray.count
    }
       
    public func isSimulator() -> String{
#if targetEnvironment(simulator)
  return "Simulator"
#else
  return "Real Device"
#endif
    }
    
   public func getBiometricSupported() -> String {
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
    
    public func checkAccessibilityEnabled()->Bool{
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
 
    public func availableMemory(){
    let fileURL = URL(fileURLWithPath: NSHomeDirectory() as String)
    do {
        let values = try fileURL.resourceValues(forKeys: [.volumeAvailableCapacityKey])
        if let capacity = values.volumeAvailableCapacity{
            print("Available capacity: \(capacity)")
        } else {
            print("Capacity is unavailable")
        }
    } catch {
        print("Error retrieving capacity: \(error.localizedDescription)")
    }
    }
    
    public func totalMemory(){
    let fileURL = URL(fileURLWithPath: NSHomeDirectory() as String)
    do {
        let values = try fileURL.resourceValues(forKeys: [.volumeTotalCapacityKey])
        if let capacity = values.volumeTotalCapacity {
            print("Total capacity: \(capacity)")
        } else {
            print("Capacity is unavailable")
        }
    } catch {
        print("Error retrieving capacity: \(error.localizedDescription)")
    }
    }
    
    public var localTimeZoneAbbreviation: String { return TimeZone.current.abbreviation() ?? "" }
    
    public func getBattery()->Float{
        UIDevice.current.isBatteryMonitoringEnabled = true
        let level = UIDevice.current.batteryLevel
        return level
    }
    
    public var isConnectedToVpn: Bool {
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
    
    public func ipAddressType() -> String{
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

        return address
    }
}
