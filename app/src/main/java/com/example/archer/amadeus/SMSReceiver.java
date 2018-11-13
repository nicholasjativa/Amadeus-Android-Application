package com.example.archer.amadeus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Arrays;
import java.util.Date;
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
        AMADEUS_API_URL = context.getString(R.string.AMADEUS_BASE_API_URL);
        String actionName = intent.getAction();
        Bundle bundle = intent.getExtras();


        if (actionName.equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            handleTextMessageReceived(intent);
        } else if (actionName.equals(SMS_SENT_ACTION)) {
            String msgUri = bundle.getString("uri");
            String amadeusId = bundle.getString("amadeusId");
            String msgid_phone_db = msgUri.substring(msgUri.lastIndexOf("/") + 1, msgUri.length());

            updateServerWithWebappMessageId(amadeusId, msgid_phone_db);
        }

    }

    private void handleTextMessageReceived(Intent intent) {
        Bundle bundle = intent.getExtras();

        try {
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdus.length; i++) {
                    SmsMessage message = SmsMessage.createFromPdu((byte[]) pdus[i], bundle.getString("format"));
                    String phoneNumber = message.getDisplayOriginatingAddress();
                    String messageBody = message.getMessageBody();
                    Long timestamp = message.getTimestampMillis();
                    postTextToServer(phoneNumber, messageBody, timestamp);
                }

            }
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
    }

    public void postTextToServer(final String phoneNumber, final String messageBody, final Long timestamp) {
        final Context selfContext = this.context;
        RequestQueue queue = Volley.newRequestQueue(this.context);
        String url = AMADEUS_API_URL + "/texts";
        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(selfContext, "Response from Server after relaying message to be displayed on Front-End: " + response.toString(), Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {Log.d("pokstTextToServer", "Error sending message to Server" + error.toString());
                        Toast.makeText(selfContext, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("fromPhoneNumber", phoneNumber);
                params.put("toPhoneNumber", "6313360360");
                params.put("textMessageBody", messageBody);
                params.put("timestamp", timestamp.toString());
                return params;
            }
        };
        queue.add(strRequest);
    }

    public void updateServerWithWebappMessageId(final String amadeusId, final String msgid_phone_db) {
        final Context selfContext = this.context;
        String url = AMADEUS_API_URL + "/texts/update-outgoing-text-message-id";
        RequestQueue queue = Volley.newRequestQueue(this.context);

        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(selfContext, "Response from Server after updating msgid_phone_db on Server: " + response.toString(), Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(selfContext, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("amadeusId", amadeusId);
                params.put("msgid_phone_db", msgid_phone_db);
                return params;
            }
        };
        queue.add(strRequest);
    }


    /* ------------------------------- HELPER for printing bundle extras ----------------- */
    public static String intentToString(Intent intent) {
        if (intent == null) {
            return null;
        }

        return intent.toString() + " " + bundleToString(intent.getExtras());
    }

    public static String bundleToString(Bundle bundle) {
        StringBuilder out = new StringBuilder("Bundle[");

        if (bundle == null) {
            out.append("null");
        } else {
            boolean first = true;
            for (String key : bundle.keySet()) {
                if (!first) {
                    out.append(", ");
                }

                out.append(key).append('=');

                Object value = bundle.get(key);

                if (value instanceof int[]) {
                    out.append(Arrays.toString((int[]) value));
                } else if (value instanceof byte[]) {
                    out.append(Arrays.toString((byte[]) value));
                } else if (value instanceof boolean[]) {
                    out.append(Arrays.toString((boolean[]) value));
                } else if (value instanceof short[]) {
                    out.append(Arrays.toString((short[]) value));
                } else if (value instanceof long[]) {
                    out.append(Arrays.toString((long[]) value));
                } else if (value instanceof float[]) {
                    out.append(Arrays.toString((float[]) value));
                } else if (value instanceof double[]) {
                    out.append(Arrays.toString((double[]) value));
                } else if (value instanceof String[]) {
                    out.append(Arrays.toString((String[]) value));
                } else if (value instanceof CharSequence[]) {
                    out.append(Arrays.toString((CharSequence[]) value));
                } else if (value instanceof Parcelable[]) {
                    out.append(Arrays.toString((Parcelable[]) value));
                } else if (value instanceof Bundle) {
                    out.append(bundleToString((Bundle) value));
                } else {
                    out.append(value);
                }

                first = false;
            }
        }

        out.append("]");
        return out.toString();
    }

}


