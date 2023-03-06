//
//  NetworkLayer.swift
//  FrameworkFlagright
//
//  Created by tanishq on 11/01/23.
//

import Foundation

let baseUrl = "https://stoplight.io/mocks/flagright-device-api/flagright-device-data-api/122980601/device/metric"

func makePostRequest(apiKey: String, region: String, parameterDict: [String: Any]) {

let headers = ["Content-Type": "application/json"]

let postData = try? JSONSerialization.data(withJSONObject: parameterDict, options: [])

let request = NSMutableURLRequest(url: NSURL(string: baseUrl)! as URL,
                                        cachePolicy: .useProtocolCachePolicy,
                                    timeoutInterval: 10.0)
request.httpMethod = "POST"
request.addValue(apiKey, forHTTPHeaderField: "x-api-key")

request.allHTTPHeaderFields = headers
request.httpBody = postData! as Data

let session = URLSession.shared
let dataTask = session.dataTask(with: request as URLRequest, completionHandler: { (_, response, error) -> Void in
  if error != nil {
    print(error)
  } else {
    let httpResponse = response as? HTTPURLResponse
      print(httpResponse?.statusCode)
  }
})

dataTask.resume()
}
