import Contacts
@objc(FlagrightSdk)
class FlagrightSdk: NSObject {

  let store = CNContactStore()
        let keysToFetch = [CNContactGivenNameKey]
        var contactArray = [String]()

  @objc(multiply:withB:withResolver:withRejecter:)
  func multiply(a: Float, b: Float, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
    resolve(a*b)
  }

  // @objc(getDeviceLocaleLanguageCode:withResolver:withRejecter:)
  @objc
  func getDeviceLocaleLanguageCode(_ resolve: @escaping RCTPromiseResolveBlock,
                            rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    resolve(NSLocale.current.languageCode)
  }

  @objc
    func getDeviceLocaleCountry(_ resolve: @escaping RCTPromiseResolveBlock,
                              rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
      resolve(NSLocale.current.regionCode)
    }
    
    @objc
    func getDeviceTimeZone(_ resolve: @escaping RCTPromiseResolveBlock,
                              rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
      resolve(TimeZone.current.abbreviation())
    }

    @objc
    @available(iOS 13.0, *)
    func isAccessibilityEnabled(_ resolve: @escaping RCTPromiseResolveBlock,
                              rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
       var isEnabled = false;
       if UIAccessibility.isAssistiveTouchRunning{
          isEnabled = true
      }
      if UIAccessibility.isBoldTextEnabled{
           isEnabled = true
      }
      if UIAccessibility.isClosedCaptioningEnabled{
           isEnabled = true
      }
      if UIAccessibility.isDarkerSystemColorsEnabled{
           isEnabled = true
      }
      if UIAccessibility.isGrayscaleEnabled{
           isEnabled = true
      }
      if UIAccessibility.isGuidedAccessEnabled{
           isEnabled = true
      }
      if UIAccessibility.isInvertColorsEnabled{
           isEnabled = true
      }
      if UIAccessibility.isMonoAudioEnabled{
           isEnabled = true
      }
      if UIAccessibility.isOnOffSwitchLabelsEnabled{
          isEnabled = true
      }
      if UIAccessibility.isReduceMotionEnabled{
           isEnabled = true
      }
      if UIAccessibility.isReduceTransparencyEnabled{
          isEnabled = true
      }
      if UIAccessibility.isSpeakScreenEnabled{
           isEnabled = true
      }
      if UIAccessibility.isSpeakSelectionEnabled{
           isEnabled = true
      }
      if UIAccessibility.isSwitchControlRunning{
           isEnabled = true
      }
      if UIAccessibility.isVoiceOverRunning{
           isEnabled = true
      }
      if UIAccessibility.shouldDifferentiateWithoutColor{
           isEnabled = true
      }
                             
      resolve(isEnabled)
    }

     @objc
    func isDeviceConnectedToVPN(_ resolve: @escaping RCTPromiseResolveBlock,
                              rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
      var isConnected = false;                         
      if let settings = CFNetworkCopySystemProxySettings()?.takeRetainedValue() as? Dictionary<String, Any>,
                  var scopes = settings["__SCOPED__"] as? [String:Any] {
                  for (key, _) in scopes {
                   if key.contains("tap") || key.contains("tun") || key.contains("ppp") || key.contains("ipsec") {
                          isConnected = true
                      }
                  }
              }
      resolve(isConnected)
    }

    @objc
    func fetchContactsCount(_ resolve: @escaping RCTPromiseResolveBlock,
                              rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
      var count = 0;                          
        
          do {
               try store.enumerateContacts(with: CNContactFetchRequest.init(keysToFetch: keysToFetch as [CNKeyDescriptor]), usingBlock: { (contact, pointer) -> Void in
                  // print("contact = ","\(contact.givenName)")
                   self.contactArray.append(contact.givenName)
               })
           } catch {
               print("something wrong happened in fetching contacts")
           }
              count = self.contactArray.count
          
      resolve(count)
    }


}
