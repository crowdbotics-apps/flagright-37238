
# FlagrightDeviceMetricsSDK iOS

Flagright SDK gets the hardware related information from a device and sends it to our server for Analytics and fraud detection purposes.


## Integration

To import this framework into your project, proceed as follows:

```
1. Drag and drop the FrameworkFlagright.xcproject 
   as the topmost file in your target app’s 
   project navigator. 

2. Make sure to place the FrameworkFlagright folder 
   in the same root folder where the target app is 
   present. 

3. Open (or reopen) the target app (.xcworkspace)

4. Click on your App name in the project navigator to 
   view ‘general settings’

5. Click on the plus icon under ‘Frameworks, Libraries 
   and Embedded Content’, select 
   ‘FrameworkFlagright.framework’ and click on add
 ```   
 The framework is imported and ready to be used. 
 
## Add the following permissions to your project/App’s Info.Plist

To use flagrightFramework, the target app must 
have these following permissions

```bash
Privacy - Location Always Usage Description
Privacy - Location Always and When In Use Usage Description
Privacy - Location When In Use Usage Description
Privacy - Contacts Usage Description
Privacy - Bluetooth Always Usage Description
```


### Once we have the required permissions, we can now consume the Flagright Framework. 

[IMPORTANT]
In your application’s `Appdelegate.swift` file, 
add the following to the `didFinishLaunchingWithOptions` function

        FlagrightDeviceMetricsSDK.shared.sync()

This calls the sync method of Singleton class `DataCollection` which fetches and syncs all the attributes/parameters required in the API as soon as the App launches. 





## Emit Method

Emit method is used to fetch the required parameters from user device and store it before making the actual API call/send data to server. 

Call the below method after importing 
`FlagrightFramework`:
```
   DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
             FlagrightDeviceMetricsSDK.shared.emit(userId: "1234", type: "USER_SIGNUP", transactionId: "999")
         }
 ```        
- Example userid value (1234), type ("USER_SIGNUP") and transactionId value (999) need to be replaced and are added just for reference

- These 2 parameters are required : userID and type   transactionId is optional

- All three parameters will be of String type 

- type can take only (either TRANSACTION  OR  "USER_SIGNUP) as its value.


**NOTE:**

If you plan to implement API call in ViewController(), right after the app starts, call it by giving a delay, as stated above.

If you are using API call on button press or after a particular event, call the api normally without `Dispatchqueue.main.async(deadline)`

```FlagrightDeviceMetricsSDK.shared.emit(userId: "1234", type: "USER_SIGNUP", transactionId: "999")```


## Init Method

Init method is used to make the actual API call and send the fetched parameters from 
user device to the Flagright servers.

Call the init method :

 ``` 
 DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) 
 {
     FlagrightDeviceMetricsSDK.shared.`init`(apikey: "123", region: "us")
 } 

```
- Init method should ONLY be called after emit method

- There must be a delay of atleast 0.1 seconds between both the methods

- Call init method with backticks to ensure the system default function init is not called.

- Example apiKey value (123) and region value ("us") need to be replaced and are added just for reference

- Both parameters are required 



## Methods


| Method  | Description |
| :-------- | :------- | 
|  FlagrightDeviceMetricsSDK.shared.emit(userId, type, transactionId) | `fetches the required parameters from user device` | 
| ``` FlagrightDeviceMetricsSDK.shared.`init`(apiKey, region)``` | `makes the actual API call/sends parameters to the server` | 



