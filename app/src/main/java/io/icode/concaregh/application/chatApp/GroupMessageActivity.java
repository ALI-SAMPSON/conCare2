package io.icode.concaregh.application.chatApp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import io.icode.concaregh.application.R;
import io.icode.concaregh.application.adapters.GroupMessageAdapter;
import io.icode.concaregh.application.constants.Constants;
import io.icode.concaregh.application.interfaces.APIService;
import io.icode.concaregh.application.models.Admin;
import io.icode.concaregh.application.models.GroupChats;
import io.icode.concaregh.application.models.Groups;
import io.icode.concaregh.application.models.Users;
import io.icode.concaregh.application.notifications.Client;
import io.icode.concaregh.application.notifications.Data;
import io.icode.concaregh.application.notifications.MyResponse;
import io.icode.concaregh.application.notifications.Sender;
import io.icode.concaregh.application.notifications.Token;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupMessageActivity extends AppCompatActivity implements GroupMessageAdapter.OnItemClickListener{

    RelativeLayout relativeLayout;

    Toolbar toolbar;

    CircleImageView groupIcon;
    TextView groupName;

    TextView tv_no_recent_chats;

    RecyclerView recyclerView;

    // loading bar to load messages
    ProgressBar progressBar;

    ProgressDialog progressDialog;

    // instance of Admin Class
    Admin admin;

    GroupChats groupChats;

    FirebaseUser currentUser;

    String group_name;
    String group_image_url;
    String date_created;
    String time_created;

    String admin_uid;

    DatabaseReference groupRef;
    DatabaseReference userRef;
    DatabaseReference adminRef;
    DatabaseReference groupChatRef;

    // editText and Button to send Message
    EditText msg_to_send;
    ImageButton btn_send;

    // variable for MessageAdapter class
    GroupMessageAdapter groupMessageAdapter;

    private List<GroupChats> mGroupChats;

    private List<Groups> mGroups;

    // list to get the ids of selected users from group creating
    private List<String> usersIds;

    // Listener to listener for messages seen
    ValueEventListener seenListener;

    ValueEventListener mDBListener;

    APIService apiService;

    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_message);

        relativeLayout = findViewById(R.id.relativeLayout);

        // getting reference to ids
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // creates APIService using Google API from the APIService Class
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        groupName =  findViewById(R.id.tv_group_name);
        groupIcon =  findViewById(R.id.ci_group_icon);
        msg_to_send =  findViewById(R.id.editTextMessage);
        btn_send =  findViewById(R.id.btn_send);

        tv_no_recent_chats = findViewById(R.id.tv_no_recent_chats);

        //getting reference to the recycler view and setting it up
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        progressBar = findViewById(R.id.progressBar);

        // progressDialog to display before deleting message
        progressDialog = new ProgressDialog(this);
        // setting message on progressDialog
        progressDialog.setMessage("Deleting message...");

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        admin = new Admin();

        groupChats = new GroupChats();

        mGroupChats = new ArrayList<>();

        mGroups = new ArrayList<>();

        // getting strings passed from recyclerView adapter
        group_name = getIntent().getStringExtra("group_name");
        group_image_url = getIntent().getStringExtra("group_icon");
        date_created = getIntent().getStringExtra("date_created");
        time_created = getIntent().getStringExtra("time_created");
        usersIds = getIntent().getStringArrayListExtra("usersIds");

        //groupMessageAdapter = new GroupMessageAdapter(this,mGroupChats,true);

        groupRef = FirebaseDatabase.getInstance().getReference(Constants.GROUP_REF).child(group_name);

        groupChatRef = FirebaseDatabase.getInstance().getReference(Constants.GROUP_CHAT_REF);

        adminRef = FirebaseDatabase.getInstance().getReference(Constants.ADMIN_REF);

        // getAdmin details
        getAdminDetails();

        getGroupDetails();

        //seenMessage(admin_uid);
    }

    private void getGroupDetails(){

        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Groups groups = dataSnapshot.getValue(Groups.class);

                assert groups != null;

                // setting group name
                groupName.setText(groups.getGroupName());

                // setting group icon
                if(group_image_url == null){
                    // loading default icon as image icon
                    Glide.with(getApplicationContext()).load(R.mipmap.group_icon).into(groupIcon);
                }
                else{
                    // loading default icon as image icon
                    Glide.with(getApplicationContext()).load(group_image_url).into(groupIcon);
                }

                // method call
                readGroupMessages(currentUser.getUid(),admin_uid, groups.getGroupIcon());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // display error message if exception occurs
                Toast.makeText(GroupMessageActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    // gets admin details immediately activity is launched
    private void getAdminDetails(){
        // getting uid of admin to send message to
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        DatabaseReference adminRef = rootRef.child(Constants.ADMIN_REF);

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    // getting admin uid
                    admin_uid = snapshot.child("adminUid").getValue(String.class);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // display message if error occurs
                Snackbar.make(relativeLayout,databaseError.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        };

        adminRef.addListenerForSingleValueEvent(eventListener);
    }

    // ImageView OnClickListener to send Message
    public void btnSend(View view) {

        // sets notify to true
        notify = true;

        final String message  = msg_to_send.getText().toString();

        if(!message.equals("")){

            // sending message to admin
            sendGroupMessage(currentUser.getUid(),admin_uid,message);

        }
        else{
            Toast.makeText(GroupMessageActivity.this,
                    getString(R.string.error_type_message),Toast.LENGTH_LONG).show();
        }
        // clear the field after message is sent
        msg_to_send.setText("");
    }

    private void sendGroupMessage(String sender, final String receiver, final String message){

        DatabaseReference groupChatRef = FirebaseDatabase.getInstance().getReference(Constants.GROUP_CHAT_REF);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receivers", new ArrayList<String>(){{add(receiver);}});
        hashMap.put("message",message);
        hashMap.put("isseen", false);

        groupChatRef.push().setValue(hashMap);


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
                // display message if error occurs
                Snackbar.make(relativeLayout,databaseError.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });

    }

    // sends notification to admin as soon as message is sent
    private void sendNotification(String receiver, final String username, final String message){

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(currentUser.getUid(),R.mipmap.app_logo_round,
                            username+": "+message,getString(R.string.app_name) + "GROUP MESSAGE",admin_uid);

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
                // display message if error occurs
                Snackbar.make(relativeLayout,databaseError.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });

    }

    // method to readMessages from the system
    private void readGroupMessages(final String myId, final String adminId, final String imageUrl){

        // display progressBar
        progressBar.setVisibility(View.VISIBLE);

        // array initialization
        mGroupChats = new ArrayList<>();

        mDBListener = groupChatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // clears the chats to avoid reading duplicate message
                mGroupChats.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    GroupChats groupChats = snapshot.getValue(GroupChats.class);
                    // gets the unique keys of the chats
                    groupChats.setKey(snapshot.getKey());

                    assert groupChats != null;
                    if(groupChats.getReceivers().contains(myId) && groupChats.getSender().equals(adminId)
                            || groupChats.getReceivers().contains(adminId) && groupChats.getSender().equals(myId)){
                        mGroupChats.add(groupChats);
                    }

                    // initializing the messageAdapter and setting adapter to recyclerView
                    groupMessageAdapter = new GroupMessageAdapter(GroupMessageActivity.this,mGroupChats,imageUrl);
                    // setting adapter
                    recyclerView.setAdapter(groupMessageAdapter);
                    // notify data change in adapter
                    groupMessageAdapter.notifyDataSetChanged();
                    // dismiss progressBar
                    progressBar.setVisibility(View.GONE);

                    // setting on OnItemClickListener in this activity as an interface
                    groupMessageAdapter.setOnItemClickListener(GroupMessageActivity.this);

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

    // method to check if user or admin has seen the message
    private void seenMessage(final String admin_uid){

        seenListener = groupChatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    GroupChats groupChats = snapshot.getValue(GroupChats.class);
                    assert groupChats != null;
                    if(groupChats.getReceivers().contains(currentUser.getUid()) && groupChats.getSender().equals(admin_uid)
                            || groupChats.getReceivers().contains(admin_uid) && groupChats.getSender().equals(currentUser.getUid())){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // display message if error occurs
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

        AlertDialog.Builder builder = new AlertDialog.Builder(GroupMessageActivity.this);
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
                        GroupChats selectedMessage = mGroupChats.get(position);

                        //gets the key at the selected position
                        String selectedKey = selectedMessage.getKey();

                        groupChatRef.child(selectedKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(GroupMessageActivity.this," Message deleted ",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(GroupMessageActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
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

        adminRef = FirebaseDatabase.getInstance().getReference(Constants.USER_REF).child(currentUser.getUid());
        //.child(adminUid);
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        adminRef.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //method calls
        status("online");
        //currentAdmin(adminUid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // method calls
        status("online");
        //currentAdmin("none");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // set status to offline if activity is destroyed
        status("offline");
        // removes eventListeners when activity is destroyed
        groupChatRef.removeEventListener(seenListener);
        groupChatRef.removeEventListener(mDBListener);

    }
}
