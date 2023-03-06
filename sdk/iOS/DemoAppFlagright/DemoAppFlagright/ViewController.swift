//
//  ViewController.swift
//  DemoAppFlagright
//
//  Created by tanishq on 06/12/22.
//

import UIKit
import FrameworkFlagright
import SwiftUI

class ViewController: UIViewController
{

     override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

         DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
             FlagrightDeviceMetricsSDK.shared.emit(userId: "1234", type: "USER_SIGNUP", transactionId: "999")
         }

         DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
             FlagrightDeviceMetricsSDK.shared.`init`(apikey: "123", region: "INDIA")
         }

    }

    override func viewDidLoad() {
        super.viewDidLoad()
    }

}
