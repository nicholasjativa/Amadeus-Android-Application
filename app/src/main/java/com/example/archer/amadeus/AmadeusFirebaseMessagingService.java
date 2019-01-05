package com.example.archer.amadeus;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class AmadeusFirebaseMessagingService extends FirebaseMessagingService {
    private Context context;
    private SmsSingleton smsSingleton = SmsSingleton.getInstance();
    ;
    public AmadeusFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {

            Map textInfoJson = remoteMessage.getData();
            //String fromPhoneNumber = textInfoJson.get("fromPhoneNumber").toString();
            String amadeusId = textInfoJson.get("amadeusId").toString();
            String toPhoneNumber = textInfoJson.get("toPhoneNumber").toString();
            String messageBody = textInfoJson.get("textMessageBody").toString();
            sendText(toPhoneNumber, messageBody, amadeusId);

        }

    }


    public void sendText(String toPhoneNumber, String message, String amadeusId) {
        
        SmsManager smsManager = SmsManager.getDefault();
        Intent sI = new Intent(SMSReceiver.SMS_SENT_ACTION);
        sI.putExtra("amadeusId", amadeusId);
        PendingIntent sentIntent = PendingIntent.getBroadcast(getBaseContext(), 0, sI, PendingIntent.FLAG_ONE_SHOT);

        this.smsSingleton.incrementOutgoingSkipCount();

        smsManager.sendTextMessage("+1" + toPhoneNumber, null, message, sentIntent, null);
    }

}
