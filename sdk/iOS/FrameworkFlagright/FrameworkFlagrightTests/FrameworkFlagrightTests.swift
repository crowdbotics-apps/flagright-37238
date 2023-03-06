//
//  FrameworkFlagrightTests.swift
//  FrameworkFlagrightTests
//
//  Created by tanishq on 06/12/22.
//

import XCTest
@testable import FrameworkFlagright

class FrameworkFlagrightTests: XCTestCase {
    var sut: FlagrightDeviceMetricsSDK!

    // Enter the userId passed in emit method
    var userIdVar: String = "123"

    // Enter the type passed in emit method
    var typeVar: String = "TRANSACTION"

    // Enter the region passed in `init` method
    var regionVar: String = "us"

    // Enter the apiKey passed in `init` method
    var apiKeyVar: String = "123"

    override func setUpWithError() throws {
        try super.setUpWithError()
        sut = FlagrightDeviceMetricsSDK.shared
    }

    override func tearDownWithError() throws {
        sut = nil
    }

    func testValidateDeviceFingerprint(){
        let result = sut?.deviceID
        XCTAssertNotEqual(result, nil)
    }

    func testValidateIpAddress(){
        let result = sut?.getIPAddress()
        let resultCount = result?.count
        XCTAssertNotEqual(result, nil)
        XCTAssertLessThanOrEqual(resultCount!, 32)
    }

    func testValidateDeviceCountryCode(){
        let result = sut?.language.count
        XCTAssertEqual(result, 2)
        XCTAssertNotEqual(result, nil)
    }

    func testValidateDeviceLaungageCode(){
        let result = sut?.country.count
        XCTAssertEqual(result, 2)
        XCTAssertNotEqual(result, nil)
    }

    func testValidateBatteryLevel(){
        let result = sut?.getBattery()
        XCTAssertNotEqual(result, nil)
        XCTAssertLessThanOrEqual(result!, 100)
    }

    // validates virtuaal device

    func testIsVirtualDevice(){
        let result = sut?.isSimulator()
        XCTAssertEqual(result, false || true)
    }

    func testNumberOfContacts(){
        let result = sut?.getContacts()
        XCTAssertGreaterThanOrEqual(result!, 0)
    }

    func testRam(){
        let result = sut?.ram
        XCTAssertGreaterThanOrEqual(result!, 0)
    }

    func testMainStorageInGb(){
        let result = sut?.totalMemory()
        XCTAssertGreaterThanOrEqual(result!, 0)
    }

    func testValidateEmit(user: String, type: String){
        sut?.sync()
        sut?.emit(userId: user, type: type)
        let paramCount = sut?.parameters?.count
        XCTAssertNotEqual(user, "")
        XCTAssertNotEqual(user, nil)
        XCTAssert(type == "TRANSACTION" || type == "USER_SIGNUP")
        XCTAssertNotEqual(type, nil)
        XCTAssertNotEqual(type, "")
        XCTAssertGreaterThanOrEqual(paramCount!, 0)

    }

    func testValidateInit(apiKey: String, region: String){
        sut?.sync()
        sut?.`init`(apikey: apiKey, region: region)
        XCTAssertNotEqual(apiKey, "")
        XCTAssertNotEqual(apiKey, nil)
        XCTAssertNotEqual(region, nil)
        XCTAssertNotEqual(region, "")
    }

    func testValidateEmitFunction(){
        testValidateEmit(user: userIdVar, type: typeVar)
    }

    func testValidateInitFunction(){
        testValidateInit(apiKey: apiKeyVar, region: regionVar)
    }

    func testExample() throws {
    }

    func testPerformanceExample() throws {
        // This is an example of a performance test case.
        self.measure {
            // Put the code you want to measure the time of here.
        }
    }

}

