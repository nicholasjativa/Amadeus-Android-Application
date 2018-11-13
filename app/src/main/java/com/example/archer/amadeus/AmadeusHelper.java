package com.example.archer.amadeus;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by noble on 2/6/18.
 */

public class AmadeusHelper extends SQLiteOpenHelper {

    public AmadeusHelper(Context context) {
        super(context, AmadeusContract.DATABASE_NAME, null, AmadeusContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AmadeusContract.Errors.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(AmadeusContract.Errors.CREATE_TABLE);
        onCreate(db);
    }
}


