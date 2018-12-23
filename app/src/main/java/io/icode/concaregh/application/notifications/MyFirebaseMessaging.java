package io.icode.concaregh.application.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import io.icode.concaregh.application.chatApp.MessageActivity;

@SuppressWarnings("ALL")
public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String sent = remoteMessage.getData().get("sent");
        String admin = remoteMessage.getData().get("user");

        SharedPreferences preferences = getSharedPreferences("PREFS",MODE_PRIVATE);
        String currentAdmin = preferences.getString("currentadmin","none");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser != null && sent.equals(currentUser.getUid())){
            // if current user admin is chatting is not equal to the value stored in sharePreferences
            if(!currentAdmin.equals(admin)) {
                //checks for Build Versions
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // method call
                    sendOreoNotification(remoteMessage);
                } else {
                    // method call
                    sendNormalNotification(remoteMessage);
                }

            }

        }
    }

    // send notification to respective user
    private void sendNormalNotification(RemoteMessage remoteMessage) {
        // get Data from the Data model
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent = new Intent(this,MessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

            builder.setSmallIcon(Integer.parseInt(icon))
                    .setContentTitle(title)
                    .setContentText(body)
                    .setWhen(System.currentTimeMillis())
                    .setSound(defaultSound)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        int i = 0;
        if(j > 0){
            i = j;
        }

        assert notificationManager != null;
        notificationManager.notify(i,builder.build());

    }

    // Method to send Notification to/from Android Devices with Oreo versions(Android 8.0 and above)
    private void sendOreoNotification(RemoteMessage remoteMessage){
        // get Data from the Data model
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent = new Intent(this,MessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoNotification oreoNotification = new OreoNotification(this);
        Notification.Builder builder = oreoNotification.getOreoNotification(title,body,pendingIntent,defaultSound,icon);

        int i = 0;
        if(j > 0){
            i = j;
        }

        oreoNotification.getManager().notify(i,builder.build());

    }
}
