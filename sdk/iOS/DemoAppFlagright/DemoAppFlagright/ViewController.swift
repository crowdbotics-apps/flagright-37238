//
//  ViewController.swift
//  DemoAppFlagright
//
//  Created by tanishq on 06/12/22.
//

import UIKit
import FrameworkFlagright
import SwiftUI

class ViewController: UIViewController {
    var bleManager: BLEManager!
        
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        let deviceData = DataCollection()
        
        
        makePostRequest(userId: "1234", type: "USER_SIGNUP")
        

        print("The device ID is \(deviceData.deviceID)")
        print("The IP adress is \(deviceData.getIPAddress() ?? "Not fetched")")

        
        print("The device language is \(deviceData.language )")
        print("The device country is \(deviceData.country )")
        print("The device localTimeZone \(deviceData.localTimeZoneAbbreviation)")
        print("The total RAM is \(deviceData.ram)")
        deviceData.availableMemory()
        print(deviceData.totalMemory())
        print("The device is \(deviceData.isSimulator())")
        print("The ios version is \(deviceData.systemVersion)")
        print("The model is \(deviceData.modelName)")
        print("The maker is \(deviceData.maker)")
        print("The battery is \(deviceData.getBattery())")
        print("The VPN connection status is \(deviceData.isConnectedToVpn)")
        print("The carrier is \(deviceData.carrier )")
        print("Biometric type \(deviceData.getBiometricSupported())")
        
        print("The jailbreak status is \(deviceData.jailBreakStatus())")
        
        print("Checking accesibility \(deviceData.checkAccessibilityEnabled())")
        print("IP address type is \(deviceData.ipAddressType())")
        print("Total number of contacts are \(deviceData.getContacts())")
        print("Device Location status \(String(describing: deviceData.checkLocationEnabled))")
        print("Device Longitude \(String(describing: deviceData.longitude))")
        print("Device Latitude \(String(describing: deviceData.latitude))")
        // Do any additional setup after loading the view.
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }

}
