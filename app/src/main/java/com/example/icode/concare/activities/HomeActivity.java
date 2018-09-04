package com.example.icode.concare.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.icode.concare.R;
import com.example.icode.concare.fragements.FragmentEditProfile;
import com.example.icode.concare.models.CurrentUsers;
import com.example.icode.concare.models.Orders;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    // global  or class variables
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private AppCompatSpinner spinnerGender;
    private ArrayAdapter<CharSequence> arrayAdapterGender;


    FirebaseAuth mAuth;

    NavigationView navigationView;

    CircleImageView circleImageView;
    TextView username;
    TextView email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        spinnerGender = findViewById(R.id.spinnerGender);
        arrayAdapterGender = ArrayAdapter.createFromResource(this,R.array.gender,R.layout.spinner_item);
        arrayAdapterGender.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerGender.setAdapter(arrayAdapterGender);

        mDrawerLayout = findViewById(R.id.drawer);

        // setNavigationViewListener();
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // getting reference to the navigation drawer view objects in the nav_header
        circleImageView = navigationView.getHeaderView(0).findViewById(R.id.circularImageView);
        username = navigationView.getHeaderView(0).findViewById(R.id.username);
        email = navigationView.getHeaderView(0).findViewById(R.id.email);


        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        // a call to the navigationViewItemListener
        //setNavigationViewListener();

        //checks of there is support actionBar
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();

        // method call
        loadUserInfo();

    }

    /* method to load user info from firebase
    **real-time database into imageView and textView
    */
    private void loadUserInfo(){

        // get current logged in user
        FirebaseUser user = mAuth.getCurrentUser();

        // getting the username and image of current logged in user
        String _photoUrl = user.getPhotoUrl().toString();
        String _username = user.getDisplayName();
        String _email = user.getEmail();

        // checks if current user is not null
        if(user != null){
            // checks if the photo of the current user is not null
            if(user.getPhotoUrl() != null ){
                Glide.with(this)
                        .load(_photoUrl)
                        .into(circleImageView);
            }
            // checks if the username of the current user is not null
            if(user.getDisplayName() != null){
                username.setText("Username : " + _username);
            }
            // checks if the email of the current user is not null
            if(user.getEmail() != null){
                email.setText("Email : " + _email);
            }

        }

    }

    // method to start edit profile fragment
    public void startEditProfileFragment(){

        // an instance of the edit profile fragment class
        FragmentEditProfile fragmentEditProfile = new FragmentEditProfile();
        // use fragment Manager and transactions to add the fragment to the screen
        FragmentManager fragmentManager = getSupportFragmentManager();
        // fragment transaction

        //FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout,fragmentEditProfile)
                        .addToBackStack(null)
                        .commit();

    }

    @Override
    protected void onStart(){
        super.onStart();
        if(mAuth.getCurrentUser() == null){
            finish();
            // starts the login activity currently logged in user is null(no logged in user)
            startActivity(new Intent(this,LoginActivity.class));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item){

        // handle navigation item click
        switch (item.getItemId()){

            case R.id.circularImageView:

                break;

            case R.id.edit_profile:
                startActivity(new Intent(getApplicationContext(),EditProfileActivity.class));
                //start edit_profile fragment
                //startEditProfileFragment();
                break;
            case R.id.orders:
                // start orders fragment
                break;
            case R.id.sign_out:
                Toast.makeText(getApplicationContext(),"You clicked signout",Toast.LENGTH_LONG).show();
                // display a progressDialog
                /*ProgressDialog progressDialog =
                        ProgressDialog.show(this,"","sign out...",true,true);*/
                FirebaseAuth.getInstance().signOut();
                //progressDialog.dismiss();
                break;
                default:
                    break;
        }
        // closes the navigation drawer to the left of the screen
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // method to initialize and set the navigationView Listener
    private void setNavigationViewListener(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_user,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        switch(item.getItemId()){
            case R.id.menu_about:
                //starts the about us activity
                startActivity(new Intent(this,AboutUsActivity.class));
                break;
            case R.id.menu_sign_out:
                // sign user out of the system
                FirebaseAuth.getInstance().signOut();
                break;
            case R.id.menu_exit:
                // close application
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //Click Listener for proceed button on homeActivity
    public void onProceedButtonClick(View view) {

        String gender = spinnerGender.getSelectedItem().toString().trim();

        Intent intent = new Intent(this,PlaceOrderActivity.class);
        intent.putExtra("gender",gender);
        startActivity(intent);

        /*startActivity(new Intent(this, PlaceOrderActivity.class)
        .putExtra(KEY_GENDER,gender));*/

    }
}
