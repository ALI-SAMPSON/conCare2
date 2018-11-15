package io.icode.concaregh.application.chatApp;

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
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.icode.concaregh.application.R;
import io.icode.concaregh.application.fragements.ChatsFragment;
import io.icode.concaregh.application.fragements.UsersFragment;
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
        getSupportActionBar().setTitle("");

        //checks of there is support actionBar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getString(R.string.home));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        profile_image = findViewById(R.id.profile_image);

        username =  findViewById(R.id.username);

        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();

        chatDbRef = FirebaseDatabase.getInstance().getReference("Users");
                //child(user.getUid());

        chatDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                username.setText(users.getUsername());
                //text if user's imageUrl is equal to default
                if(users.getImageUrl() == null){
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

        // getting reference to the views
        TabLayout tabLayout =  findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // adds ChatsFragment and UsersFragment to the viewPager
        viewPagerAdapter.addFragment(new ChatsFragment(), getString(R.string.text_chats));
        viewPagerAdapter.addFragment(new UsersFragment(), getString(R.string.text_users));
        //Sets Adapter view of the ViewPager
        viewPager.setAdapter(viewPagerAdapter);

        //sets tablayout with viewPager
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // ViewPager Adapter Class
    class ViewPagerAdapter extends FragmentPagerAdapter{

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm){
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        // get the positions of the fragments
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        // get Count of fragments
        @Override
        public int getCount() {
            return fragments.size();
        }

        //method to add Fragment and their titles to the list
        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        // Press Ctrl + 0
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

}
