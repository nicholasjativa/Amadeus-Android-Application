package com.example.archer.amadeus;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by noble on 2/7/18.
 */

public class ErrorCursorAdapter extends CursorAdapter {
    private SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");

    public ErrorCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_error_log, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvFrom = (TextView) view.findViewById(R.id.tvFromPhoneNumber);
        TextView tvTo = (TextView) view.findViewById(R.id.tvToPhoneNumber);
        TextView tvMessageBody = (TextView) view.findViewById(R.id.tvMessageBody);
        TextView tvMessageTime = (TextView) view.findViewById(R.id.tvMessageTime);
        TextView tvErrorDescription = (TextView) view.findViewById(R.id.tvErrorDescription);

        String from = cursor.getString(cursor.getColumnIndexOrThrow(AmadeusContract.Errors.COL_FROM_PHONE_NUMBER));
        String to = cursor.getString(cursor.getColumnIndexOrThrow(AmadeusContract.Errors.COL_TO_PHONE_NUMBER));
        String messageBody = cursor.getString(cursor.getColumnIndexOrThrow(AmadeusContract.Errors.COL_BODY));
        String messageTime = cursor.getString(cursor.getColumnIndexOrThrow(AmadeusContract.Errors.COL_TIMESTAMP));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(AmadeusContract.Errors.COL_ERROR));

        tvFrom.setText(from);
        tvTo.setText(to);
        tvMessageBody.setText(messageBody);
        tvErrorDescription.setText(description);

        Date d = new Date(Long.parseLong(messageTime));
        String date = df.format(d);
        tvMessageTime.setText(date);

    }
}
