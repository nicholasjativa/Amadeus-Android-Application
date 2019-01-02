package com.example.archer.amadeus;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

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

        Toast.makeText(this, "OutgoingSmsWatcherService hit..", Toast.LENGTH_LONG).show();
        SmsOutgoingObserver observer = new SmsOutgoingObserver(new Handler(), this);
        getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, observer);

        return START_STICKY;
    }

}
