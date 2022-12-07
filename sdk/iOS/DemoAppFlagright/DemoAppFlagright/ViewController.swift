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
        print(deviceData.name)
        
        print("The device ID is \(deviceData.deviceID)")
        print("The device language is \(deviceData.language ?? "Error")")
        print("The device country is \(deviceData.country ?? "Error")")
        print("The available memory is \(deviceData.ram) GB")
        deviceData.availableMemory()
        deviceData.totalMemory()
        print("The device is \(deviceData.isSimulator())")
        print("The ios version is \(deviceData.systemVersion)")
        print("The model is \(deviceData.modelName)")
        print("The maker is \(deviceData.maker)")

        

        
        // Do any additional setup after loading the view.
    }


}

