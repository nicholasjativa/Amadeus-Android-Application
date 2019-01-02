package com.example.archer.amadeus;

import android.*;
import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;

import android.provider.ContactsContract;;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_ASKED_FOR = 2;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        boolean hasHadFirstLogin = sharedPref.getBoolean(getString(R.string.pref_has_had_first_login), false);
        String firebaseToken = sharedPref.getString(getString(R.string.firebase_token_key), "No Firebase Token currently exists.");
        userId = sharedPref.getInt(getString(R.string.pref_user_id), -1);

        if (!hasHadFirstLogin) {
            sendRegistrationTokenToServer(firebaseToken);
            SharedPreferences.Editor editor =  sharedPref.edit();
            editor.putBoolean(getString(R.string.pref_has_had_first_login), true);
            editor.commit();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            startRequestPermissions();
        } else {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONArray contacts = getContacts();
                    transmitContacts(contacts);
                }
            }).start();

            TextView textView = (TextView) findViewById(R.id.firebaseToken);
            textView.setText(firebaseToken);
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

    private void transmitContacts(final JSONArray contacts) {

        final Context context = this;

        String url = AmadeusApplication.AMADEUS_API_URL + "/contacts";
        Map<String, String> params = new HashMap();
        params.put("contacts", contacts.toString());
        params.put("userId", Integer.toString(userId));
        JSONObject jsonRequest = new JSONObject(params);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, jsonRequest,

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context, "Successfully uploaded contacts", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {

        };

        AmadeusApplication.getInstance().getRequestQueue().add(req);
    }

    private void sendRegistrationTokenToServer(String firebaseToken) {

        final Context self = this;

        String url = AmadeusApplication.AMADEUS_API_URL + "/users/update-registration-token";
        Map<String, String> params = new HashMap();
        params.put("registrationToken", firebaseToken);
        params.put("userId", Integer.toString(userId));
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

                    }
                });

        AmadeusApplication.getInstance().getRequestQueue().add(req);
    }

}
