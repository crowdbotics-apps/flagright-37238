@objc(FlagrightSdk)
class FlagrightSdk: NSObject {

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
}
