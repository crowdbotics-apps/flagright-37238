//
//  NetworkLayer.swift
//  FrameworkFlagright
//
//  Created by tanishq on 11/01/23.
//

import Foundation

public func makePostRequest(){

let headers = ["Content-Type": "application/json"]
let deviceData = DataCollection()
    
let parameters = [
  "userId": "1234",
  "timestamp": 1672942548000,
  "type": "USER_SIGNUP",
  "transactionId": deviceData.deviceID,
  "deviceFingerprint": "string",
  "isVirtualDevice": true,
  "ipAddress": "string",
  "location": [
    "latitude": 13.0033,
    "longitude": 76.1004
  ],
  "totalNumberOfContacts": 31,
  "batteryLevel": 44,
  "externalTotalStorageInGb": 64,
  "externalFreeStorageInGb": 3,
  "manufacturer": "Samsung",
  "mainTotalStorageInGb": 32,
  "model": "Galaxy S7",
  "operatingSystem": [
    "name": "Android",
    "version": "12.3.1˜"
  ],
  "deviceCountryCode": "IN",
  "deviceLaungageCode": "KA",
  "ramInGb": 4,
  "isDataRoamingEnabled": true,
  "isLocationEnabled": true,
  "isAccessibilityEnabled": true,
  "isBluetoothActive": true,
  "networkOperator": "Airtel"
] as [String : Any]

let postData = try? JSONSerialization.data(withJSONObject: parameters, options: [])

let request = NSMutableURLRequest(url: NSURL(string: "https://stoplight.io/mocks/flagright-device-api/flagright-device-data-api/122980601/device/metric")! as URL,
                                        cachePolicy: .useProtocolCachePolicy,
                                    timeoutInterval: 10.0)
request.httpMethod = "POST"
request.addValue("123", forHTTPHeaderField: "x-api-key")

request.allHTTPHeaderFields = headers
request.httpBody = postData! as Data

let session = URLSession.shared
let dataTask = session.dataTask(with: request as URLRequest, completionHandler: { (data, response, error) -> Void in
  if (error != nil) {
    print(error)
  } else {
    let httpResponse = response as? HTTPURLResponse
      print(httpResponse?.statusCode)
  }
})

dataTask.resume()
}
