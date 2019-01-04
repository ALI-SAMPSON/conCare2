package io.icode.concaregh.application.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import io.icode.concaregh.application.R;
import io.icode.concaregh.application.chatApp.MessageActivity;

@SuppressWarnings("ALL")
public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String sent = remoteMessage.getData().get("sent");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser != null && sent.equals(firebaseUser.getUid())){
            sendNotification(remoteMessage);
        }


    }

    // sending notification to admin when message is sent
    private void sendNotification(RemoteMessage remoteMessage) {

        String user  = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]",""));
        //Intent intent = new Intent(this,MessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userid",user);
        //intent.putExtras(bundle);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //PendingIntent pendingIntent = PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.app_logo_round)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(false)
                .setSound(defaultSound)
                .setVibrate(new long[]{1000,1000,1000})
                .setWhen(System.currentTimeMillis());
                //.setContentIntent(pendingIntent);

        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        int i = 0;
        if(j > 0){
            i = j;
        }

        nm.notify(i,builder.build());

    }
}
