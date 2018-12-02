package io.icode.concaregh.application.Notifications;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

@SuppressWarnings("ALL")
public class MyFirebaseIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String refreshedToken  = FirebaseInstanceId.getInstance().getToken();
        if(currentUser != null){
            updateToken(refreshedToken);
        }
    }

    private void updateToken(String refreshedToken) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(refreshedToken);
        assert currentUser != null;
        reference.child(currentUser.getUid()).setValue(token);

    }
}
