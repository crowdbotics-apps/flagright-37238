import Contacts
import UIKit


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

     @objc
     func getIPAddress(_ resolve: @escaping RCTPromiseResolveBlock,
                              rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
              
             var address: String?
        var ifaddr: UnsafeMutablePointer<ifaddrs>?
        
        if getifaddrs(&ifaddr) == 0 {
            
            var ptr = ifaddr
            while ptr != nil {
                defer { ptr = ptr?.pointee.ifa_next } // memory has been renamed to pointee in swift 3 so changed memory to pointee
                
                guard let interface = ptr?.pointee else {
                    address = nil
                    resolve(address)
                    return
                }
                let addrFamily = interface.ifa_addr.pointee.sa_family
                if addrFamily == UInt8(AF_INET) || addrFamily == UInt8(AF_INET6) {
                    
                    guard let ifa_name = interface.ifa_name else {
                        address = nil
                        resolve(address);
                        return
                    }
                    let name: String = String(cString: ifa_name)
                    
                    if name == "en0" {  // String.fromCString() is deprecated in Swift 3. So use the following code inorder to get the exact IP Address.
                        var hostname = [CChar](repeating: 0, count: Int(NI_MAXHOST))
                        getnameinfo(interface.ifa_addr, socklen_t((interface.ifa_addr.pointee.sa_len)), &hostname, socklen_t(hostname.count), nil, socklen_t(0), NI_NUMERICHOST)
                        address = String(cString: hostname)
                    }
                    
                }
            }
            freeifaddrs(ifaddr)
        }
        
     //    return address
      resolve(address);
    }

    @objc
    func isDeviceRooted(_ resolve: @escaping RCTPromiseResolveBlock,
                              rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        
        
                                   
         func isJailBroken() -> Bool {
                 if JailBrokenHelper.isSimulator() { return false }
                 if JailBrokenHelper.hasCydiaInstalled() { return true }
                 if JailBrokenHelper.isContainsSuspiciousApps() { return true }
                 if JailBrokenHelper.isSuspiciousSystemPathsExists() { return true }
                 return JailBrokenHelper.canEditSystemFiles()
         }

        
         struct JailBrokenHelper {
               static func isSimulator() -> Bool {
                    return TARGET_OS_SIMULATOR != 0
               }
            static func hasCydiaInstalled() -> Bool {
                return UIApplication.shared.canOpenURL(URL(string: "cydia://")!)
            }
            
            static func isContainsSuspiciousApps() -> Bool {
                for path in suspiciousAppsPathToCheck {
                    if FileManager.default.fileExists(atPath: path) {
                        return true
                    }
                }
                return false
            }
            
            static func isSuspiciousSystemPathsExists() -> Bool {
                for path in suspiciousSystemPathsToCheck {
                    if FileManager.default.fileExists(atPath: path) {
                        return true
                    }
                }
                return false
            }
            
            static func canEditSystemFiles() -> Bool {
                let jailBreakText = "Developer Insider"
                do {
                    try jailBreakText.write(toFile: jailBreakText, atomically: true, encoding: .utf8)
                    return true
                } catch {
                    return false
                }
            }
            
            /**
             Add more paths here to check for jail break
             */
            static var suspiciousAppsPathToCheck: [String] {
                return ["/Applications/Cydia.app",
                        "/Applications/blackra1n.app",
                        "/Applications/FakeCarrier.app",
                        "/Applications/Icy.app",
                        "/Applications/IntelliScreen.app",
                        "/Applications/MxTube.app",
                        "/Applications/RockApp.app",
                        "/Applications/SBSettings.app",
                        "/Applications/WinterBoard.app"
                ]
            }
            
            static var suspiciousSystemPathsToCheck: [String] {
                return ["/Library/MobileSubstrate/DynamicLibraries/LiveClock.plist",
                        "/Library/MobileSubstrate/DynamicLibraries/Veency.plist",
                        "/private/var/lib/apt",
                        "/private/var/lib/apt/",
                        "/private/var/lib/cydia",
                        "/private/var/mobile/Library/SBSettings/Themes",
                        "/private/var/stash",
                        "/private/var/tmp/cydia.log",
                        "/System/Library/LaunchDaemons/com.ikey.bbot.plist",
                        "/System/Library/LaunchDaemons/com.saurik.Cydia.Startup.plist",
                        "/usr/bin/sshd",
                        "/usr/libexec/sftp-server",
                        "/usr/sbin/sshd",
                        "/etc/apt",
                        "/bin/bash",
                        "/Library/MobileSubstrate/MobileSubstrate.dylib"
                ]
            }
        }
      resolve(isJailBroken())
    }

}
