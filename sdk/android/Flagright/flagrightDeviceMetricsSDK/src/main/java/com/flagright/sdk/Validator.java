package com.flagright.sdk;

import android.content.Context;

import com.flagright.sdk.interfaces.ResponseCallback;
import com.flagright.sdk.models.Region;

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
    public static boolean validateRegion(Region region) {
       return region!= null;
    }

    /**
     *  Validate context
     *
     * @param context Application Context
     * @return true if valid context is pass
     */
    public static boolean validateContext(Context context) {
        return context != null;
    }

    /**
     * Validate {@link ResponseCallback} as an argument
     *
     * @param responseCallback {@link ResponseCallback}
     * @return true if valid {@link ResponseCallback} object is pass
     */
    public static boolean validateOnSuccessListener(ResponseCallback responseCallback) {
       return responseCallback != null;
    }
}
