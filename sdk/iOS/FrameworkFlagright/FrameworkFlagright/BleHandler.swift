////
////  BleHandler.swift
////  FrameworkFlagright
////
////  Created by tanishq on 16/12/22.
////
//
//import Foundation
//import CoreBluetooth
//
//
//public class BLEManager {
//    var centralManager : CBCentralManager!
//    public var bleHandler : BLEHandler // delegate
//    public init() {
//    self.bleHandler = BLEHandler()
//    self.centralManager = CBCentralManager(delegate: self.bleHandler, queue: nil)
//      }
// }
//
//public class BLEHandler : NSObject, CBCentralManagerDelegate {
//    var btManger : CBCentralManager!
//    var delegate : CBCentralManagerDelegate?
//    public override init() {
//        super.init()
//        self.btManger = CBCentralManager()
//    }
//    public func centralManagerDidUpdateState(_ central: CBCentralManager) {
//        switch central.state {
//        case .poweredOn:
//            print("Bluetooth is ON.")
//            break
//        case .poweredOff:
//            print("Bluetooth is Off.")
//
//            break
//        case .resetting:
//            break
//        case .unauthorized:
//            break
//        case .unsupported:
//            break
//        case .unknown:
//            break
//        default:
//            break
//        }
//    }
//    
//    public func getState(){
//        print(" BT \(delegate)")
//    }
//}
