package com.example.archer.amadeus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SMSReceiver extends BroadcastReceiver {
    public final SmsManager smsManager = SmsManager.getDefault();
    public static final String SMS_SENT_ACTION = "com.archer.amadeus.MESSAGE_SENT";
    private Context context;
    private SmsSingleton smsSingleton = SmsSingleton.getInstance();
    private String AMADEUS_API_URL;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context c, Intent intent) {

        context = c;
        AMADEUS_API_URL = AmadeusApplication.AMADEUS_API_URL;
        String actionName = intent.getAction();
        Bundle bundle = intent.getExtras();

        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        boolean hasHadFirstLogin = sharedPref.getBoolean(context.getString(R.string.pref_has_had_first_login), false);
        int userId = sharedPref.getInt(context.getString(R.string.pref_user_id), -1);
        String userPhoneNumber = sharedPref.getString(context.getString(R.string.pref_user_phone_number), "");

        if (hasHadFirstLogin && userId > -1) {

            if (actionName.equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {

                handleTextMessageReceived(intent, userId, userPhoneNumber);

            } else if (actionName.equals(SMS_SENT_ACTION)) {
                String msgUri = bundle.getString("uri");
                String amadeusId = bundle.getString("amadeusId");
                String msgid_phone_db = msgUri.substring(msgUri.lastIndexOf("/") + 1, msgUri.length());

                updateServerWithWebappMessageId(amadeusId, msgid_phone_db);
            }
        }

    }

    private void handleTextMessageReceived(Intent intent, int userId, String userPhoneNumber) {
        Bundle bundle = intent.getExtras();

        try {
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdus.length; i++) {
                    SmsMessage message = SmsMessage.createFromPdu((byte[]) pdus[i], bundle.getString("format"));
                    String phoneNumber = message.getDisplayOriginatingAddress();
                    String messageBody = message.getMessageBody();
                    Long timestamp = message.getTimestampMillis();
                    postTextToServer(phoneNumber, messageBody, timestamp, userId, userPhoneNumber);
                }

            }
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
    }

    public void postTextToServer(final String phoneNumber, final String messageBody, final Long timestamp, final int userId, final String userPhoneNumber) {

        final Context selfContext = context;
        String url = AMADEUS_API_URL + "/texts/send-sms-to-server";

        Map<String, Object> params = new HashMap<>();
        params.put("fromPhoneNumber", phoneNumber);
        params.put("toPhoneNumber", userPhoneNumber);
        params.put("textMessageBody", messageBody);
        params.put("timestamp", timestamp.toString());
        params.put("userId", userId);

        JSONObject jsonRequest = new JSONObject(params);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, jsonRequest,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String logMsg = String.format("Text message unsuccessfully sent with body: %s and error: %s", messageBody, error.toString());
                        AmadeusLogger.appendLog(logMsg, selfContext);
                        Toast.makeText(selfContext, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        AmadeusApplication.getInstance().getRequestQueue().add(req);
    }

    public void updateServerWithWebappMessageId(final String amadeusId, final String msgid_phone_db) {

        final Context selfContext = context;
        String url = AMADEUS_API_URL + "/texts/update-outgoing-text-message-id";

        Map<String, String> params = new HashMap<>();
        params.put("amadeusId", amadeusId);
        params.put("msgid_phone_db", msgid_phone_db);

        JSONObject jsonRequest = new JSONObject(params);

        JsonObjectRequest req= new JsonObjectRequest(Request.Method.POST, url, jsonRequest,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(selfContext, "Response from Server after updating msgid_phone_db on Server: " + response.toString(), Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(selfContext, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        AmadeusApplication.getInstance().getRequestQueue().add(req);
    }

}


