package io.icode.concaregh.application.chatApp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.icode.concaregh.application.R;
import io.icode.concaregh.application.adapters.MessageAdapter;
import io.icode.concaregh.application.models.Admin;
import io.icode.concaregh.application.models.Chats;

public class MessageActivity extends AppCompatActivity {

    RelativeLayout relativeLayout;

    CircleImageView profile_image;
    TextView username;

    FirebaseUser currentUser;
    DatabaseReference adminRef;

    DatabaseReference chatRef;

    // editText and Button to send Message
    EditText msg_to_send;
    ImageView btn_send;

    Intent intent;

    // string to get intentExtras
    String adminUid;
    String admin_username;

    // variable for MessageAdapter class
    MessageAdapter messageAdapter;
    List<Chats> mChats;

    RecyclerView recyclerView;

    ValueEventListener seenListener;

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

        relativeLayout = findViewById(R.id.relativeLayout);

        profile_image =  findViewById(R.id.profile_image);
        username =  findViewById(R.id.username);
        msg_to_send =  findViewById(R.id.editText_send);
        btn_send =  findViewById(R.id.btn_send);

        //getting reference to the recyclerview and setting it up
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        intent = getIntent();
        adminUid = intent.getStringExtra("uid");
        admin_username = intent.getStringExtra("username");

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        adminRef = FirebaseDatabase.getInstance().getReference("Admin").child(adminUid);

        getAdminDetails();

        seenMessage(adminUid);

    }

    private void getAdminDetails(){

        adminRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Admin admin = dataSnapshot.getValue(Admin.class);
                username.setText(admin.getUsername());
                if(admin.getImageUrl() == null){
                    // sets a default placeholder into imageView if url is null
                    profile_image.setImageResource(R.drawable.app_logo);
                }
                else{
                    // loads imageUrl into imageView if url is not null
                    Glide.with(getApplicationContext())
                            .load(admin.getImageUrl()).into(profile_image);
                }

                // method call
                readMessages(currentUser.getUid(),adminUid,admin.getImageUrl());
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
        hashMap.put("receiver", receiver);
        hashMap.put("message",message);

        chatRef.child("Chats").push().setValue(hashMap);

    }

    // ImageView OnClickListener to send Message
    public void btnSend(View view) {

        String message  = msg_to_send.getText().toString();

        if(!message.equals("")){
            // call to method to sendMessage and
            // set the editText field to null afterwards
            sendMessage(currentUser.getUid(),adminUid,message);
        }
        else{
             Toast.makeText(MessageActivity.this,
                     "Please type in a message",Toast.LENGTH_LONG).show();
        }
        // clear the field after message is sent
        msg_to_send.setText("");
    }

    // methopd to check if user or admin has seen the message
    private void seenMessage(final String adminUid){

        chatRef = FirebaseDatabase.getInstance().getReference("Chats");

        seenListener = chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chats chats = snapshot.getValue(Chats.class);
                    assert chats != null;
                    if(chats.getReceiver().equals(currentUser.getUid()) && chats.getSender().equals(adminUid)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(relativeLayout,databaseError.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });

    }

    // method to readMessages from the system
    private void readMessages(final String myid, final String userid, final String imageUrl){

        // array initialization
        mChats = new ArrayList<>();

        chatRef = FirebaseDatabase.getInstance().getReference("Chats");
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChats.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chats chats = snapshot.getValue(Chats.class);
                    assert chats != null;
                    if(chats.getReceiver().equals(myid) && chats.getSender().equals(userid) ||
                                chats.getReceiver().equals(userid) && chats.getSender().equals(myid)){
                            mChats.add(chats);
                        }

                        messageAdapter = new MessageAdapter(MessageActivity.this,mChats,imageUrl);
                        recyclerView.setAdapter(messageAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(relativeLayout,databaseError.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });

    }

    // method to set user status to "online" or "offline"
    private void status(String status){

        adminRef = FirebaseDatabase.getInstance().getReference("Admin").child(adminUid);
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        adminRef.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // removes listener
        chatRef.removeEventListener(seenListener);
        status("offline");
    }
}
