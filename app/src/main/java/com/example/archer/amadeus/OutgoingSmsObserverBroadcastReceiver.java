package com.example.archer.amadeus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;

public class OutgoingSmsObserverBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "OutgoingSmsObserverBroadcastReceiver hit..", Toast.LENGTH_LONG).show();
        Intent outgoingSmsWatcherServiceIntent = new Intent(context, OutgoingSmsWatcherService.class);
        context.startService(outgoingSmsWatcherServiceIntent);
    }
}
