package com.example.archer.amadeus;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public class ErrorLogActivity extends AppCompatActivity {
    private AmadeusHelper dbHelper;
    private ErrorCursorAdapter errorAdapter;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_error_log);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new AmadeusHelper(this);

        setupErrorList();
    }

    public void setupErrorList() {
        db = dbHelper.getReadableDatabase();
        Cursor errorCursor = db.rawQuery("SELECT * FROM " + AmadeusContract.Errors.TABLE_NAME + " ORDER BY " + AmadeusContract.Errors.COL_TIMESTAMP + " DESC ", null);

        ListView lvErrors = (ListView) findViewById(R.id.lvErrors);
        errorAdapter = new ErrorCursorAdapter(this, errorCursor);

        lvErrors.setAdapter(errorAdapter);
        db.close();
    }

    public void deleteAllErrors(View view) {
        db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + AmadeusContract.Errors.TABLE_NAME);

        Cursor cursor = db.rawQuery("SELECT * FROM " + AmadeusContract.Errors.TABLE_NAME + " ORDER BY " + AmadeusContract.Errors.COL_TIMESTAMP + " DESC ", null);
        errorAdapter.swapCursor(cursor);
        errorAdapter.notifyDataSetChanged();
    }

}
