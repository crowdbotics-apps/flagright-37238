package com.flagrightsdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

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
   * @return total number of contacts
   */
  public static int getTotalContactsCount(Context context) {
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
  }
}
