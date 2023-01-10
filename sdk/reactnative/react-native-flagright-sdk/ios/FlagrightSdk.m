#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(FlagrightSdk, NSObject)

RCT_EXTERN_METHOD(multiply:(float)a withB:(float)b
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(getDeviceLocaleLanguageCode:
 (RCTPromiseResolveBlock)resolve
  rejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(getDeviceLocaleCountry:
 (RCTPromiseResolveBlock)resolve
  rejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(getDeviceTimeZone:
 (RCTPromiseResolveBlock)resolve
  rejecter:(RCTPromiseRejectBlock)reject)

  RCT_EXTERN_METHOD(isAccessibilityEnabled:
 (RCTPromiseResolveBlock)resolve
  rejecter:(RCTPromiseRejectBlock)reject)

  RCT_EXTERN_METHOD(isDeviceConnectedToVPN:
 (RCTPromiseResolveBlock)resolve
  rejecter:(RCTPromiseRejectBlock)reject)

  RCT_EXTERN_METHOD(fetchContactsCount:
 (RCTPromiseResolveBlock)resolve
  rejecter:(RCTPromiseRejectBlock)reject)

  RCT_EXTERN_METHOD(getIPAddress:
 (RCTPromiseResolveBlock)resolve
  rejecter:(RCTPromiseRejectBlock)reject)

  RCT_EXTERN_METHOD(isDeviceRooted:
 (RCTPromiseResolveBlock)resolve
  rejecter:(RCTPromiseRejectBlock)reject)


+ (BOOL)requiresMainQueueSetup
{
  return NO;
}

@end
