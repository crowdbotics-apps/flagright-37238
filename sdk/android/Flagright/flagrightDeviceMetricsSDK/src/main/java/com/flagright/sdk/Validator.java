package com.flagright.sdk;

import android.content.Context;

import com.flagright.sdk.interfaces.ResponseCallback;

/**
 * Class validates the integration calls
 */
public class Validator {
    /**
     * Method checks if the valid apiKey string is pass
     *
     * @param apiKey id of the user
     * @return true if the userId is valid
     */
    public static boolean validateAPIKey(String apiKey) {
        return apiKey != null && apiKey.trim().length() > 0;
    }

    /**
     * Method checks if the valid userId is pass
     *
     * @param userId id of the user
     * @return true if the userId is valid
     */
    public static boolean validateUserId(String userId) {
        return userId != null && userId.trim().length() > 0;
    }

    /**
     * Method checks if the valid userId is pass
     *
     * @param region region
     * @return true if the region is valid
     */
    public static boolean validateRegion(String region) {
        return region != null && region.trim().length() > 0;
    }

    public static boolean validateContext(Context context) {
        return context != null;
    }

    public static boolean validateOnSuccessListener(ResponseCallback responseCallback) {
       return responseCallback != null;
    }
}
