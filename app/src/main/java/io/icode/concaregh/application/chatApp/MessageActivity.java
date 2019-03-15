package io.icode.concaregh.application.chatApp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import io.icode.concaregh.application.R;
import io.icode.concaregh.application.adapters.MessageAdapter;
import io.icode.concaregh.application.constants.Constants;
import io.icode.concaregh.application.interfaces.APIService;
import io.icode.concaregh.application.models.Admin;
import io.icode.concaregh.application.models.Chats;
import io.icode.concaregh.application.models.Users;
import io.icode.concaregh.application.notifications.Client;
import io.icode.concaregh.application.notifications.Data;
import io.icode.concaregh.application.notifications.MyResponse;
import io.icode.concaregh.application.notifications.Sender;
import io.icode.concaregh.application.notifications.Token;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ALL")
public class MessageActivity extends AppCompatActivity implements MessageAdapter.OnItemClickListener {

    RelativeLayout relativeLayout;

    Toolbar toolbar;

    // fields to contain admin details
    CircleImageView profile_image;
    TextView username;
    TextView admin_status;

    FirebaseUser currentUser;

    DatabaseReference adminRef;

    TextView tv_no_messages;

    DatabaseReference chatRef;

    DatabaseReference userRef;

    // editText and Button to send Message
    EditText msg_to_send;
    ImageButton btn_send;

    // string to get intentExtras
    String admin_uid;

    String admin_username;

    //Variable to store status of the current user
    String status;

    // variable for MessageAdapter class
    MessageAdapter messageAdapter;

    List<Chats> mChats;

    RecyclerView recyclerView;

    ValueEventListener seenListener;

    ValueEventListener mDBListener;

    APIService apiService;

    boolean notify = false;

    private boolean isChat;

    private String theLastMessage;

    // loading bar to load messages
    ProgressBar progressBar;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        relativeLayout = findViewById(R.id.relativeLayout);

        profile_image =  findViewById(R.id.profile_image);
        username =  findViewById(R.id.username);
        admin_status =  findViewById(R.id.admin_status);
        msg_to_send =  findViewById(R.id.editTextMessage);
        btn_send =  findViewById(R.id.btn_send);

        tv_no_messages = findViewById(R.id.tv_no_messages);

        //getting reference to the recyclerview and setting it up
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        admin_uid = getIntent().getStringExtra("uid");
        //admin_username = getIntent().getStringExtra("username");
        // get the current status of admin
        //status = getIntent().getStringExtra("status");

        /*
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MessageActivity.this);
        admin_uid = preferences.getString("uid","");
        admin_username = preferences.getString("username","");
        status = preferences.getString("status","");
        */

        // set status of the admin on toolbar below the username in the message activity
        //admin_status.setText(status);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        adminRef = FirebaseDatabase.getInstance().getReference(Constants.ADMIN_REF)
                .child(admin_uid);

        progressBar =  findViewById(R.id.progressBar);

        // progressDialog to display before deleting message
        progressDialog = new ProgressDialog(this);

        progressDialog.setMessage("Deleting message...");

        // method to load admin details into the imageView
        // and TextView in the toolbar section of this activity's layout resource file
        getAdminDetails();

        //method call to seen message
        seenMessage(admin_uid);

        // update registration token
        updateToken(FirebaseInstanceId.getInstance().getToken());

    }

    // methpod to load admin details
    private void getAdminDetails(){

        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Admin admin = dataSnapshot.getValue(Admin.class);

                username.setText(admin.getUsername());
                // set status of the admin on toolbar below the username in the message activity
                admin_status.setText(admin.getStatus());

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
                readMessages(currentUser.getUid(),admin_uid,admin.getImageUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // display error message
                Toast.makeText(MessageActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    // ImageView OnClickListener to send Message
    public void btnSend(View view) {

        // sets notify to true
        notify = true;

        String message  = msg_to_send.getText().toString();

        if(!message.equals("")){
            // call to method to sendMessage and
            sendMessage(currentUser.getUid(),admin_uid,message);
        }
        else{
            Toast.makeText(MessageActivity.this,
                    "No message to send",Toast.LENGTH_LONG).show();
        }
        // clear the field after message is sent
        msg_to_send.setText("");
    }

    private void sendMessage(String sender, final String receiver, final String message){

        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver", receiver);
        hashMap.put("receivers", new ArrayList<String>(){{add(receiver);}});
        hashMap.put("message",message);
        hashMap.put("isseen", false);

        chatRef.child(Constants.CHAT_REF).push().setValue(hashMap);

        // variable to hold the message to be sent
        final String msg = message;

        userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                assert users != null;
                if(notify) {
                    // method call to send notification to admin as soon as message is sent
                    sendNotification(receiver, users.getUsername(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(relativeLayout,databaseError.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });

    }


    // sends notification to admin as soon as message is sent
    private void sendNotification(String receiver, final String username, final String message){

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(Constants.TOKENS_REF);
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(currentUser.getUid(),R.mipmap.app_logo_round, username+": "+message,
                            getString(R.string.app_name),admin_uid);

                    assert token != null;
                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code() == 200){
                                        if(response.body().success != 1){
                                            Snackbar.make(relativeLayout,"Failed!",Snackbar.LENGTH_LONG).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Snackbar.make(relativeLayout,t.getMessage(),Snackbar.LENGTH_LONG).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(relativeLayout,databaseError.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });

    }

    // methopd to check if user or admin has seen the message
    private void seenMessage(final String admin_uid){

        chatRef = FirebaseDatabase.getInstance().getReference(Constants.CHAT_REF);

        seenListener = chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chats chats = snapshot.getValue(Chats.class);
                    assert chats != null;
                    if(chats.getReceiver().equals(currentUser.getUid()) && chats.getSender().equals(admin_uid)
                            || chats.getReceiver().equals(admin_uid) && chats.getSender().equals(currentUser.getUid())
                            || chats.getReceivers().contains(admin_uid) && chats.getSender().equals(currentUser.getUid())
                            || chats.getReceivers().contains(currentUser.getUid()) && chats.getSender().equals(admin_uid)){
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
    private void readMessages(final String myId, final String adminId, final String imageUrl){

        // display progressBar
        progressBar.setVisibility(View.VISIBLE);

        // array initialization
        mChats = new ArrayList<>();

        chatRef = FirebaseDatabase.getInstance().getReference(Constants.CHAT_REF);

        mDBListener = chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists()){

                    // displays a message to the user
                    tv_no_messages.setVisibility(View.VISIBLE);

                    // hides progressBar
                    progressBar.setVisibility(View.GONE);

                }

                else{

                    // clears the chats to avoid reading duplicate message
                    mChats.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Chats chats = snapshot.getValue(Chats.class);
                        // gets the unique keys of the chats
                        chats.setKey(snapshot.getKey());

                        assert chats != null;
                        if(chats.getReceiver().equals(myId) && chats.getSender().equals(adminId)
                                || chats.getReceiver().equals(adminId) && chats.getSender().equals(myId)
                                || chats.getReceiver().equals("") && chats.getReceivers().contains(myId)
                                && chats.getSender().equals(adminId)
                                || chats.getReceivers().contains(adminId)
                                && chats.getSender().equals(myId)){
                            mChats.add(chats);
                        }

                        // initializing the messageAdapter and setting adapter to recyclerView
                        messageAdapter = new MessageAdapter(MessageActivity.this,mChats,imageUrl);
                        // setting adapter
                        recyclerView.setAdapter(messageAdapter);
                        // notify data change in adapter
                        messageAdapter.notifyDataSetChanged();

                        // hides the text
                        tv_no_messages.setVisibility(View.GONE);

                        // dismiss progressBar
                        progressBar.setVisibility(View.GONE);

                        // setting on OnItemClickListener in this activity as an interface
                        messageAdapter.setOnItemClickListener(MessageActivity.this);

                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // dismiss progressBar
                progressBar.setVisibility(View.GONE);

                // display error message
                Snackbar.make(relativeLayout,databaseError.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });

    }


    /**handling ContextMenu
     Click Listeners in activity
     */
    @Override
    public void onItemClick(int position) {
        Toast.makeText(this," please long click on a message to delete ",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDeleteClick(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
        builder.setTitle(getString(R.string.title_delete_message));
        builder.setMessage(getString(R.string.text_delete_message));

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // show dialog
                progressDialog.show();

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // dismiss dialog
                        progressDialog.dismiss();

                        // gets the position of the selected message
                        Chats selectedMessage = mChats.get(position);

                        //gets the key at the selected position
                        String selectedKey = selectedMessage.getKey();

                        chatRef.child(selectedKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(MessageActivity.this," Message deleted ",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MessageActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });


                    }
                },3000);

            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public void onCancelClick(int position) {
        // do nothing / close ContextMenu
    }

    // keeping track of the current user the admin is chatting to avoid sending notification everytime
    private void currentAdmin(String adminUid){
        SharedPreferences.Editor editor =  getSharedPreferences("PREFS",MODE_PRIVATE).edit();
        editor.putString("currentadmin",adminUid);
        editor.apply();
    }

    // method to set user status to "online" or "offline"
    private void status(String status){

        userRef = FirebaseDatabase.getInstance().getReference(Constants.USER_REF)
                .child(currentUser.getUid());
        //.child(adminUid);
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        userRef.updateChildren(hashMap);
    }

    @Override
    protected void onStart() {
        super.onStart();
        status("online");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        status("online");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //method calls
        status("online");

    }

    @Override
    protected void onPause() {
        super.onPause();
        // method calls
        status("offline");
    }

    @Override
    protected void onStop() {
        super.onStop();
        status("offline");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // set status to offline if activity is destroyed
        status("offline");
        // removes eventListeners when activity is destroyed
        if(seenListener != null && mDBListener != null){
            chatRef.removeEventListener(seenListener);
            chatRef.removeEventListener(mDBListener);
        }

    }

    // Update currentUser's  device token
    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constants.TOKENS_REF);
        Token token1 = new Token(token);
        reference.child(currentUser.getUid()).setValue(token1);
    }
}
