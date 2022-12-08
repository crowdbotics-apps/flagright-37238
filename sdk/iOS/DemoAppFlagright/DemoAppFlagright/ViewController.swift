//
//  ViewController.swift
//  DemoAppFlagright
//
//  Created by tanishq on 06/12/22.
//

import UIKit
import FrameworkFlagright

class ViewController: UIViewController {

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


        

        
        // Do any additional setup after loading the view.
    }


}

