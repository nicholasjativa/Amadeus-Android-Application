package com.example.archer.amadeus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OutgoingSmsObserverBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        AmadeusLogger.appendLog("OutgoingSmsObserverBroadcastReceiver has received", context);
        Intent outgoingSmsWatcherServiceIntent = new Intent(context, OutgoingSmsWatcherService.class);
        context.startService(outgoingSmsWatcherServiceIntent);
    }
}
