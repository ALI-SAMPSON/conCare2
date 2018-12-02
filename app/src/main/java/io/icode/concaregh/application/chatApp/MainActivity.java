package io.icode.concaregh.application.chatApp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.icode.concaregh.application.Notifications.Token;
import io.icode.concaregh.application.R;
import io.icode.concaregh.application.activities.HomeActivity;
import io.icode.concaregh.application.adapters.ViewPagerAdapter;
import io.icode.concaregh.application.fragements.AdminFragment;
import io.icode.concaregh.application.fragements.ChatsFragment;
import io.icode.concaregh.application.models.Users;
import maes.tech.intentanim.CustomIntent;

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;


    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference chatDbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

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

                    username.setText(currentUser.getDisplayName());

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
                Toast.makeText(MainActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        // getting reference to the views
        TabLayout tabLayout =  findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // adds ChatsFragment and AdminFragment to the viewPager
        // viewPagerAdapter.addFragment(new ChatsFragment(), getString(R.string.text_chats));
        viewPagerAdapter.addFragment(new AdminFragment(), getString(R.string.text_admin));
        //Sets Adapter view of the ViewPager
        viewPager.setAdapter(viewPagerAdapter);

        //sets tablayout with viewPager
        tabLayout.setupWithViewPager(viewPager);

        // method call to update token
        updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    // Update currentAdmin's token
    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(currentUser.getUid()).setValue(token1);
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
            case android.R.id.home:
                // navigates user to the home Activity
                startActivity(new Intent(MainActivity.this,HomeActivity.class));
                CustomIntent.customType(MainActivity.this,"right-to-left");
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
