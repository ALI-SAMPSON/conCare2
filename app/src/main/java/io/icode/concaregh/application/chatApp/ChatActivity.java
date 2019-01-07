package io.icode.concaregh.application.chatApp;

//import android.app.FragmentManager;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.icode.concaregh.application.activities.OrderActivity;
import io.icode.concaregh.application.R;
import io.icode.concaregh.application.fragments.AdminFragment;
import io.icode.concaregh.application.models.Users;
import io.icode.concaregh.application.notifications.Token;
import maes.tech.intentanim.CustomIntent;

@SuppressWarnings("ALL")
public class ChatActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;


    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference chatDbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // finish activity
                finish();
            }
        });

        //checks of there is support actionBar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        profile_image = findViewById(R.id.profile_image);

        username =  findViewById(R.id.username);

        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();

        chatDbRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

        chatDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Users users = dataSnapshot.getValue(Users.class);

                    username.setText("CHAT US NOW");

                    //text if user's imageUrl is equal to default
                    if(currentUser.getPhotoUrl() == null){
                        profile_image.setImageResource(R.drawable.profile_icon);
                    }
                    else{
                        // load user's Image Url
                        Glide.with(getApplicationContext()).load(currentUser.getPhotoUrl()).into(profile_image);
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // display error message
                Toast.makeText(ChatActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        // method to display fragment
        displayFragment();

        // method call to update token
        updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    // Update currentAdmin's token
    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(currentUser.getUid()).setValue(token1);
    }


    // method to displayy fragment
    private void displayFragment(){

        // creating an instance of the adminFragment
        AdminFragment adminFragment = new AdminFragment();

        // getting support fragment
        FragmentManager fm = getSupportFragmentManager();

        // creating fragment transaction to start
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        // adds fragment to this activities layout file
        fragmentTransaction.add(R.id.fragment_container,
                adminFragment).addToBackStack(null)
                .commit();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_chat,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_order:
                // navigates user to the order Activity
                startActivity(new Intent(ChatActivity.this,OrderActivity.class));
                CustomIntent.customType(ChatActivity.this,"right-to-left");
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // method to set user status to "online" or "offline"
    private void status(String status){
        chatDbRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status",status);
        chatDbRef.updateChildren(hashMap);

    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}
