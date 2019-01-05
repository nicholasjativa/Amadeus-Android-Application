package com.example.archer.amadeus;

import android.app.Application;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class AmadeusApplication extends Application {
    public static String AMADEUS_API_URL;
    private static AmadeusApplication instance;
    private static int MY_SOCKET_TIMEOUT_MS = 3000;
    private RequestQueue queue;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        AMADEUS_API_URL = getString(R.string.AMADEUS_BASE_API_URL);
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

        req.setRetryPolicy(new DefaultRetryPolicy(
            MY_SOCKET_TIMEOUT_MS,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        getRequestQueue().add(req);
    }
}
