package com.example.archer.amadeus;

import android.provider.BaseColumns;

/**
 * Created by noble on 2/5/18.
 */

public final class AmadeusContract {
    public static final int DATABASE_VERSION = 1;
    public static final String AUTHORITY = "com.example.archer.amadeus";
    public static final String DATABASE_NAME = "amadeus.db";

    public static final String COMMA_SEP = ",";

    private AmadeusContract() {
    }

    public static final class Errors implements BaseColumns {
        public static final String TABLE_NAME = "Errors";
        public static final String _ID = BaseColumns._ID;
        public static final String COL_FROM_PHONE_NUMBER = "fromPhoneNumber";
        public static final String COL_TO_PHONE_NUMBER = "toPhoneNumber";
        public static final String COL_BODY = "body";
        public static final String COL_ERROR = "error";
        public static final String COL_TIMESTAMP = "timestamp";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                                                  + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT " + COMMA_SEP
                                                  + COL_FROM_PHONE_NUMBER + " TEXT" + COMMA_SEP
                                                  + COL_TO_PHONE_NUMBER + " TEXT" + COMMA_SEP
                                                  + COL_BODY + " TEXT" + COMMA_SEP
                                                  + COL_ERROR + " TEXT" + COMMA_SEP
                                                  + COL_TIMESTAMP + " TEXT"
                                                  + ")";

        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}