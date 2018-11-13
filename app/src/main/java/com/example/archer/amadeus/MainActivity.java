package com.example.archer.amadeus;

import android.*;
import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_ASKED_FOR = 2;
    private String AMADEUS_API_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AMADEUS_API_URL = String.format("%s", getString(R.string.AMADEUS_BASE_API_URL));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            startRequestPermissions();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //JSONArray conversationSnippets = getConversationsList();
                    //transmitConversationSnippets(conversationSnippets);
                    JSONArray contacts = getContacts();
                    transmitContacts(contacts);
                }
            }).start();

            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String firebaseToken = sharedPref.getString(getString(R.string.firebase_token_key), "No Firebase Token currently exists.");
            TextView textView = (TextView) findViewById(R.id.firebaseToken);
            textView.setText(firebaseToken);

            setupOutgoingSmsObserver();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_ASKED_FOR: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Thanks for granting that permission!", Toast.LENGTH_SHORT).show();
                } else {
                }
            }
        }
    }

    public void goToErrorLogActivity(View view) {
        Intent goToErrorLogIntent = new Intent(this, ErrorLogActivity.class);
        startActivity(goToErrorLogIntent);
    }

    public void startRequestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_CONTACTS
        }, PERMISSIONS_ASKED_FOR);
    }

    private JSONArray getContacts() {
        ContentResolver cr = getContentResolver();
        String [] projections = {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor contactsCursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projections, null, null, null);
        JSONArray contactsArr = new JSONArray();

        int contactIdIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts._ID);
        int displayNameIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        int phoneNumberIndex = contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

        while(contactsCursor.moveToNext()) {
            JSONObject contactObj = new JSONObject();
            Long contactId = contactsCursor.getLong(contactIdIndex);
            String displayName = contactsCursor.getString(displayNameIndex);
            String phoneNumber = contactsCursor.getString(phoneNumberIndex);

            try {
                contactObj.put("contactId", contactId.toString());
                contactObj.put("displayName", displayName);
                contactObj.put("phoneNumber", phoneNumber);
                contactsArr.put(contactObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return contactsArr;
    }

    public JSONArray getConversationsList() {
        JSONArray jConversationArray = new JSONArray();
        String[] projections = {Telephony.Sms.Conversations.SNIPPET, Telephony.Sms.Conversations.THREAD_ID};
        Cursor cursor = getContentResolver().query(Telephony.Sms.Conversations.CONTENT_URI, projections, null, null, null);
        int threadIdColIndex = cursor.getColumnIndex(projections[1]);
        int snippetColIndex = cursor.getColumnIndex(projections[0]);

        while (cursor.moveToNext()) {
            String name = "";
            String contactId = "";
            String address = "";
            String timestamp = "";
            String type = "";
            String threadId = cursor.getString(threadIdColIndex);
            String body = cursor.getString(snippetColIndex);

            JSONObject jConversationInfo = new JSONObject();
            Uri uri = Telephony.Sms.Inbox.CONTENT_URI;
            String[] projections2 = {"thread_id", "address", "date", "type"};
            String selection = "thread_id=" + threadId;
            Cursor cursor2 = getContentResolver().query(uri, projections2, selection, null, null);
            if (cursor2.getCount() > 0) {
                while (cursor2.moveToNext()) {
                    address = cursor2.getString(cursor2.getColumnIndex("address"));
                    type = cursor2.getString(cursor2.getColumnIndex("type"));
                    timestamp = cursor2.getString(cursor2.getColumnIndex("date"));
                    break;
                }
            }
            cursor2.close();

            if (address.equals("") || address.length() < 10) {
                continue;
            }

            Uri contactsUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
            String[] contactsProjection = {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.CONTACT_ID, ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI};
            Cursor contactsCursor = getContentResolver().query(contactsUri, contactsProjection, null, null, null);
            if (contactsCursor.getCount() > 0) {
                while (contactsCursor.moveToNext()) {
                    name = contactsCursor.getString(contactsCursor.getColumnIndex(contactsProjection[0]));
                    contactId = contactsCursor.getString(contactsCursor.getColumnIndex(contactsProjection[1]));
                    break;
                }
            }
            contactsCursor.close();

            try {
                jConversationInfo.put("address", address);
                jConversationInfo.put("body", body);
                jConversationInfo.put("contactId", contactId);
                jConversationInfo.put("name", name);
                jConversationInfo.put("threadId", threadId);
                jConversationInfo.put("timestamp", timestamp);
                jConversationInfo.put("type", type);
                jConversationArray.put(jConversationInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return jConversationArray;
    }

    public void setupOutgoingSmsObserver() {
        SmsOutgoingObserver observer = new SmsOutgoingObserver(new Handler(), this);
        getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, observer);
    }

    public void transmitConversationSnippets(final JSONArray conversationSnippets) {
        final Context context = this;
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest strRequest = new StringRequest(Request.Method.POST, AMADEUS_API_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("transmitSnippets", response.toString());
                        Toast.makeText(context, "Response from Server after transmitting conversation snippets: " + response.toString(), Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("transmitSnippets", "Error sending conversation snippets to Server" + error.toString());
                        Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("snippets", conversationSnippets.toString());
                return params;
            }
        };
        queue.add(strRequest);
    }

    private void transmitContacts(final JSONArray contacts) {
        final Context context = this;
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = AMADEUS_API_URL + "/contacts";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, "Successfully uploaded contacts", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("contacts", contacts.toString());
                return params;
            }
        };

        queue.add(stringRequest);
    }

}
