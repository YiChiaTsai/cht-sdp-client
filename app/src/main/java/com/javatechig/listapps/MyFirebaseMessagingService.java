package com.javatechig.listapps;

/**
 * Created by Yi-Chia Tsai on 12/15/2016.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Belal on 5/27/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    static double transfer_rate_bit = 0;  // For server busy detection, factor 1, mb

    static double transfer_rate_threshold = 25;
    static int signal_rate_threshold = -40;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        //Displaying data in log
        //It is optional
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        // parse "data"
        Map<String, String> fcm_data = remoteMessage.getData();
        ArrayList<String> keyList = new ArrayList<String>();
        ArrayList<String> valueList = new ArrayList<String>();

        for(Map.Entry<String, String> pair : fcm_data.entrySet()){
            String key = pair.getKey();
            String value = pair.getValue();
            Log.d(TAG, "key: " +key);
            Log.d(TAG, "value: " +value);
            keyList.add(key);
            valueList.add(value);

            FirebaseMessaging.getInstance().unsubscribeFromTopic(key);
        }

        String deviceId="";
        try {
            deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            System.out.println("deviceID:"+deviceId);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Log.d(TAG, "DeviceID: "+deviceId);
        // 種類Key 含有 UID value, 則註冊
        if (valueList.contains(deviceId)){
            String category = keyList.get(valueList.indexOf(deviceId));
            Log.d(TAG, "category: " + category);
            FirebaseMessaging.getInstance().subscribeToTopic(category);
        }
        else {
            //Calling method to generate notification
            if (ActivityThree.busyornot.equals("Good")){  //不壅塞
                sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle());
            }
        }


//        String key1 = fcm_data.get()

    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody, String messageTitle) {
        Intent intent = new Intent(this, ActivityOne.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
