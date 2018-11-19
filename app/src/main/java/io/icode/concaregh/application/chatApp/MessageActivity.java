package io.icode.concaregh.application.chatApp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.icode.concaregh.application.R;
import io.icode.concaregh.application.models.Admin;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;

    FirebaseUser currentAdmin;
    DatabaseReference adminRef;

    // editText and Button to send Message
    EditText msg_send;
    ImageButton btn_send;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        profile_image =  findViewById(R.id.profile_image);
        username =  findViewById(R.id.username);
        msg_send =  findViewById(R.id.editText_send);
        btn_send =  findViewById(R.id.btn_send);

        intent = getIntent();
        final String adminId = intent.getStringExtra("uid");

        currentAdmin = FirebaseAuth.getInstance().getCurrentUser();
        adminRef = FirebaseDatabase.getInstance().getReference("Admin").child(adminId);

        getAdminDetails();

    }

    private void getAdminDetails(){

        adminRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Admin admin = dataSnapshot.getValue(Admin.class);
                username.setText(admin.getUsername());
                if(admin.getImageUrl() == null){
                    // sets a default placeholder into imageView if url is null
                    profile_image.setImageResource(R.mipmap.admin_icon_round);
                }
                else{
                    // loads imageUrl into imageView if url is not null
                    Glide.with(MessageActivity.this)
                            .load(admin.getImageUrl()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // display error message
                Toast.makeText(MessageActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    private void sendMessage(String sender, String receiver, String message){

        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver", sender);
        hashMap.put("message",message);

        chatRef.child("Chats").push().setValue(hashMap);

    }

    // ImageButtton to send Message
    public void btnSend(View view) {

        String message  =

    }
}
