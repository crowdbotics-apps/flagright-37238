//
//  NetworkLayer.swift
//  FrameworkFlagright
//
//  Created by tanishq on 11/01/23.
//

import Foundation

public func makePostRequest(userId: String, type: String){

let headers = ["Content-Type": "application/json"]
let deviceData = DataCollection()
    
let parameters = [
  "userId": userId,
  "timestamp": 1672942548000,
  "type": type,
  "transactionId": "string",
  "deviceFingerprint": deviceData.deviceID,
  "isVirtualDevice": deviceData.isSimulator(),
  "ipAddress": deviceData.getIPAddress() ?? "Could not fetch IP",
  "location": [
    "latitude": deviceData.latitude,
    "longitude": deviceData.longitude
  ],
  "totalNumberOfContacts": deviceData.getContacts(),
  "batteryLevel": deviceData.getBattery(),
  "externalTotalStorageInGb": 0,
  "externalFreeStorageInGb": 0,
  "manufacturer": deviceData.maker,
  "mainTotalStorageInGb": deviceData.totalMemory(),
  "model": deviceData.modelName,
  "operatingSystem": [
    "name": deviceData.systemOS,
    "version": deviceData.systemVersion
  ],
  "deviceCountryCode": deviceData.country,
  "deviceLaungageCode": deviceData.language,
  "ramInGb": deviceData.ram,
  "isDataRoamingEnabled": true,
  "isLocationEnabled": deviceData.checkLocationEnabled ?? false,
  "isAccessibilityEnabled": deviceData.checkAccessibilityEnabled(),
  "isBluetoothActive": true,
  "networkOperator": deviceData.carrier
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
