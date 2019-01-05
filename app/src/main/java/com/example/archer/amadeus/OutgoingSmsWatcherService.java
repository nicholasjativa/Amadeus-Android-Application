package com.example.archer.amadeus;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

public class OutgoingSmsWatcherService extends Service {

    public OutgoingSmsWatcherService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        AmadeusLogger.appendLog("OutgoingSmsWatcherService has been started", this);
        SmsOutgoingObserver observer = new SmsOutgoingObserver(new Handler(), this);
        getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, observer);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        AmadeusLogger.appendLog("OutgoingSmsWatcherService is being destroyed", this);
    }

}
