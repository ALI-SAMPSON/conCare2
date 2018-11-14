package io.icode.concaregh.application.chatApp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import io.icode.concaregh.application.R;
import io.icode.concaregh.application.models.Users;

public class MainActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;


    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference chatDbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.text_chat_us));

        profile_image = findViewById(R.id.profile_image);

        username =  findViewById(R.id.username);

        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();

        chatDbRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        chatDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                username.setText(users.getUsername());
                //text if user's imageUrl is equal to default
                if(users.getImageUrl().equals("default")){
                    profile_image.setImageResource(R.drawable.avatar_placeholder);
                }
                else{
                    // load user's Image Url
                    Glide.with(MainActivity.this).load(users.getImageUrl()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
