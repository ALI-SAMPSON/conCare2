package io.icode.concaregh.app.firebaseMessaging;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIDService";

    @Override
    public void onTokenRefresh() {

        // Getting Registration Token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // Displaying Token on logcat
        Log.d(TAG,"Refreshed Token " + refreshedToken);

        sendRegistrationToServer(refreshedToken);

        super.onTokenRefresh();
    }

    private void sendRegistrationToServer(String token){
        // you can implement the method to store token in the database
    }
}
