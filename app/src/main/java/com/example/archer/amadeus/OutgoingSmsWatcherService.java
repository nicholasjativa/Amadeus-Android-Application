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
        runAsForeground();
        registerContentObserver();
        return START_STICKY;
    }

    public void runAsForeground() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        Notification notification = new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher).setContentText("Test").setContentIntent(pendingIntent).build();

        startForeground(1, notification);
    }

    private void registerContentObserver() {

    }
}
