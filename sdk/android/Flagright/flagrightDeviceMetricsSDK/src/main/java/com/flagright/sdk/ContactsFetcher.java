package com.flagright.sdk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.core.content.ContextCompat;

/**
 *  Class responsible for fetching the Contacts information
 */
class ContactsFetcher {
    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION =
            {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.LOOKUP_KEY,
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
            };

    /**
     *  Method fetch total number contacts
     * @param context Application context
     * @return total number of contacts; if the READ_CONTACTS permission is not provided then
     * function returns -1
     */
    public static int getTotalContactsCount(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            int count = 0;
            Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                    PROJECTION, null, null,
                    null);
            if ((cursor != null ? cursor.getCount() : 0) > 0) {
                count = cursor.getCount();
            }
            if (cursor != null) {
                cursor.close();
            }
            Log.d(ContactsFetcher.class.getSimpleName(), "Total count: " + count);
            return count;
        } else {
            return -1;
        }
    }
}
