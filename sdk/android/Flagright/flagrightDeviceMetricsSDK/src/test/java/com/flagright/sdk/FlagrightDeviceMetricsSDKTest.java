package com.flagright.sdk;

import junit.framework.TestCase;

/**
 * Unit test cases for
 */
public class FlagrightDeviceMetricsSDKTest extends TestCase {

    public void testApiValidatorCorrectValue() {
        assertTrue(Validator.validateAPIKey("123"));
    }

    public void testAPIValidatorNullValue() {
        assertFalse(Validator.validateAPIKey(null));
    }

    public void testAPIValidatorEmptyValue() {
        assertFalse(Validator.validateAPIKey(""));
    }

    public void testAPIValidatorEmptyWithSpaceValue() {
        assertFalse(Validator.validateAPIKey("   "));
    }

    public void testRegionValidatorCorrectValue() {
        assertTrue(Validator.validateRegion("region"));
    }

    public void testRegionValidatorNullValue() {
        assertFalse(Validator.validateRegion(null));
    }

    public void testRegionValidatorEmptyValue() {
        assertFalse(Validator.validateRegion(""));
    }

    public void testRegionValidatorEmptyWithSpaceValue() {
        assertFalse(Validator.validateRegion("   "));
    }

    public void testUserIdValidatorCorrectValue() {
        assertTrue(Validator.validateUserId("123"));
    }

    public void testUserIdValidatorNullValue() {
        assertFalse(Validator.validateUserId(null));
    }

    public void testUserIdValidatorEmptyValue() {
        assertFalse(Validator.validateUserId(""));
    }

    public void testUserIdValidatorEmptyWithSpaceValue() {
        assertFalse(Validator.validateUserId("   "));
    }

}