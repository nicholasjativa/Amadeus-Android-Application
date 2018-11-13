package com.example.archer.amadeus;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by noble on 9/27/17.
 */

/*
    This is currently a catch-all (unfortunately).
    It fires whenever the Android device send a texts message.
    For our purpose, it should catch whatever messages are sent by hand through Android Messages
    and relay them upstream to the Amadeus Server, where it'll add it to a conversation table.
    From this update, the message should go further upstream to the client and appear as a message
    sent by the client.
    The end goal is to have all messages that are sent by hand on the Android appear on the client
    web app as well.
 */

public class SmsOutgoingObserver extends ContentObserver {
    private Context context;
    private String lastSmsId = "";
    private SmsSingleton smsSingleton;
    private String AMADEUS_API_URL;



    public SmsOutgoingObserver(Handler handler, Context c) {
        super(handler);
        context = c;
        smsSingleton = SmsSingleton.getInstance();
        AMADEUS_API_URL = context.getString(R.string.AMADEUS_BASE_API_URL);
    }

    @Override
    public void onChange(boolean selfChange) {
        // observe outgoing "sent" sms messages

        super.onChange(selfChange);
        //Log.d("AMADEUSTEST", "The incoming skip count in SMOO is " + smsSingleton.getIncomingSkipCount());
        // we only want messages to be sent to the server if it ORIGINATES on the phone
        // and thus NOT if it is a message that came from the webapp

        Uri uriSMSURI = Uri.parse("content://sms/sent");
        Cursor cur = context.getContentResolver().query(uriSMSURI, null, null, null, null);
        cur.moveToNext();

        String _id = cur.getString(cur.getColumnIndex("_id"));

        if (smsSingleton.getOutgoingSkipCount() > 0) {

            // don't send the message up to the server (since it came from the webapp)
            // and decrement the count
            smsSingleton.decrementOutgoingSkipCount();
            saveLastTextIdToFile(_id);
            return;
        } else {


            if (checkForSmsDuplicate(_id)) {
                String thread_id = cur.getString(cur.getColumnIndex("thread_id"));
                String address = cur.getString(cur.getColumnIndex("address"));
                address = address.indexOf("+") > -1 ? address.substring(2, 12) : address;
                String body = cur.getString(cur.getColumnIndex("body"));
                relayOwnMessageUpstream(_id, thread_id, "6313360360", address, body, new Date().getTime());
            }
            cur.close();
        }
    }

    // since outgoing observer fires 3 times in a row, this allows for only one fire to actually be noticed
    public boolean checkForSmsDuplicate(String smsId) {
        boolean flagSms = true;

        if (smsId.equals(lastSmsId)) {
            flagSms = false;
        } else {
            lastSmsId = smsId;
        }

        return flagSms;
    }

    public void relayOwnMessageUpstream(final String _id, final String thread_id, final String fromPhoneNumber, final String toPhoneNumber, final String messageBody, final Long timestamp) {
        if (_id.equals(getLastSentTextId())) {
            return;
        }
        final Context selfContext = context;
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = AMADEUS_API_URL + "/texts/own";

        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(selfContext, "Sent own outgoing message upstream to server: " + response.toString(), Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(selfContext, error.toString(), Toast.LENGTH_SHORT).show();
                        saveError(fromPhoneNumber, toPhoneNumber, messageBody, timestamp, error.toString());

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("msgid_phone_db", _id);
                params.put("thread_id", thread_id);
                params.put("timestamp", timestamp.toString());
                params.put("fromPhoneNumber", fromPhoneNumber);
                params.put("toPhoneNumber", toPhoneNumber);
                params.put("textMessageBody", messageBody);
                return params;
            }
        };
        queue.add(strRequest);
    }

    private void saveError(String fromPhoneNumber, String toPhoneNumber, String messageBody, Long messageTime, String error) {
        AmadeusHelper dbHelper = new AmadeusHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AmadeusContract.Errors.COL_FROM_PHONE_NUMBER, fromPhoneNumber);
        values.put(AmadeusContract.Errors.COL_TO_PHONE_NUMBER, toPhoneNumber);
        values.put(AmadeusContract.Errors.COL_BODY, messageBody);
        values.put(AmadeusContract.Errors.COL_ERROR, error);
        values.put(AmadeusContract.Errors.COL_TIMESTAMP, messageTime.toString());

        db.insert(AmadeusContract.Errors.TABLE_NAME, null, values);
        db.close();
    }

    private void saveLastTextIdToFile(String id) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(context.getString(R.string.last_sent_text_id_key), id);
        editor.commit();
    }

    private String getLastSentTextId() {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String defaultValue = "default_last_text_sent_id";
        String lastSentTextId = sharedPref.getString(context.getString(R.string.last_sent_text_id_key), defaultValue);

        return lastSentTextId;
    }

}
