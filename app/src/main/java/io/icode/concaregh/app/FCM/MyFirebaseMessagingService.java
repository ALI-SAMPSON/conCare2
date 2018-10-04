package io.icode.concaregh.app.FCM;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM Service";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Log cat to get the Where message is coming from and the message itself
        Log.d(TAG, "From" + remoteMessage.getFrom());

        Log.d(TAG, "Notification Message Body" + remoteMessage.getNotification().getBody());
    }
}
