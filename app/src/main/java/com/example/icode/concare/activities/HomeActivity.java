package com.example.icode.concare.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.icode.concare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

    ProgressDialog progressDialog;

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        spinnerGender = findViewById(R.id.spinnerGender);
        arrayAdapterGender = ArrayAdapter.createFromResource(this,R.array.gender,R.layout.spinner_item_home);
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

        //checks of there is support actionBar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getString(R.string.home));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        progressDialog = ProgressDialog.show(this,"","Please wait...",true,true);

        // floating action button onclick Listener and initialization
        fab = findViewById(R.id.fab);

        // call to the onclick Listener for floating button
        onClickFab();

        mAuth = FirebaseAuth.getInstance();

        // method call
        loadUserInfo();

        onClickCircularImageView();

    }

    @Override
    protected void onStart(){
        super.onStart();
        if(mAuth.getCurrentUser() == null){
            HomeActivity.this.finish();
            // starts the login activity currently logged in user is null(no logged in user)
            startActivity(new Intent(this,LoginActivity.class));
        }
        else if(mAuth.getCurrentUser() != null){
            // starts the home activity if user is already logged in
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // hides the progressBar
                    progressDialog.dismiss();
                    timer.cancel();
                }
            },2000);
            // display a welcome message to user
            /*Toast.makeText(HomeActivity.this,
                    " Welcome " + mAuth.getCurrentUser().getDisplayName(),
                    Toast.LENGTH_LONG).show();*/
        }
    }

    // Onclick Listener method for floating button
    private void onClickFab(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // starts the Contact us activity
                startActivity(new Intent(HomeActivity.this,ContactUsActivity.class));
            }
        });

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

    // Circular ImageView ClickListener
    private void onClickCircularImageView(){

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start EditProfile activity
                startActivity(new Intent(HomeActivity.this,EditProfileActivity.class));
            }
        });

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item){

        // handle navigation item click
        switch (item.getItemId()){
            case R.id.menu_home:
                // do nothing
                break;
            case R.id.menu_edit_profile:
                // start EditProfile activity
                startActivity(new Intent(HomeActivity.this,EditProfileActivity.class));
                break;
            /*case R.id.orders:
                // start orders fragment
                break;*/
             case R.id.menu_about:
                // start About Us Activity
                 startActivity(new Intent(HomeActivity.this,AboutUsActivity.class));
                break;
            case R.id.menu_sign_out:
                // a call to logout method
               logout();
                break;
                default:
                    break;
        }
        // closes the navigation drawer to the left of the screen
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
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
                // starts the about us activity
                startActivity(new Intent(this,AboutUsActivity.class));
                break;
            case R.id.menu_contact:
                // starts the Contact us activity
                startActivity(new Intent(this,ContactUsActivity.class));
                break;
            case R.id.menu_exit:
                // close application
                exitApplication();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    // method to close the app and kill all process
    private void exitApplication(){

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Exit Application");
        builder.setMessage("Are you sure you want to exit application?");
        builder.setCancelable(false);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        // create and displays the alert dialog
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Exit Application?");
        alertDialogBuilder
                .setMessage("Are you sure you want to exit application?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int id) {
                        dialogInterface.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    // method to log user out of the system
    private void logout(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.logout));
        builder.setMessage(getString(R.string.logout_msg));

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        timer.cancel();
                    }
                },10000);
                // logs current user out of the system
                FirebaseAuth.getInstance().signOut();
                HomeActivity.this.finish();
                startActivity(new Intent(HomeActivity.this,LoginActivity.class));
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

    //Click Listener for proceed button on homeActivity
    public void onProceedButtonClick(View view) {
        // gets the text from the spinner and passes it to the next activity
        String gender = spinnerGender.getSelectedItem().toString().trim();

        Intent intent = new Intent(this,PlaceOrderActivity.class);
        intent.putExtra("gender",gender);
        startActivity(intent);

    }
}
