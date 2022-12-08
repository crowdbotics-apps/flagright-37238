//
//  DataCollection.swift
//  FrameworkFlagright
//
//  Created by tanishq on 06/12/22.
//

import Foundation
import UIKit

public class DataCollection {
    
    public init() {
    }
     
    public let deviceID = UIDevice.current.identifierForVendor!.uuidString
    public let language = NSLocale.current.languageCode
    public let country = NSLocale.current.regionCode
    public let ram = ProcessInfo.processInfo.physicalMemory/(1024 * 1024 * 1024)
    public let systemVersion = UIDevice.current.systemVersion
    public let maker = "Apple"
    public let modelName = UIDevice.modelName
    
    public func isSimulator() -> String{
#if targetEnvironment(simulator)
  return "Simulator"
#else
  return "Real Device"
#endif
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
    
}

