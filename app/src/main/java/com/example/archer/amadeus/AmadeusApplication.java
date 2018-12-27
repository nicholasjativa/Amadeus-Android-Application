package com.example.archer.amadeus;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class AmadeusApplication extends Application {
    public final String AMADEUS_API_URL = getString(R.string.AMADEUS_BASE_API_URL);
    private static AmadeusApplication instance;
    private RequestQueue queue;


    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
    }

    public static synchronized AmadeusApplication getInstance() {
        return instance;
    }

    public RequestQueue getRequestQueue() {

        if (queue == null) {
            queue = Volley.newRequestQueue(getApplicationContext());
        }

        return queue;
    }

    public void addToRequestQueue(Request req) {

        getRequestQueue().add(req);
    }
}