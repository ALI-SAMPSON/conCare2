package io.icode.concaregh.app.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import io.icode.concaregh.app.R;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;
import maes.tech.intentanim.CustomIntent;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    // global  or class variables
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private AppCompatSpinner spinnerGender;
    private ArrayAdapter<CharSequence> arrayAdapterGender;

    private ImageView btn_proceed;

    private Animation shake;

    FirebaseAuth mAuth;

    NavigationView navigationView;

    CircleImageView circleImageView;
    TextView username;
    TextView email;

    ProgressDialog progressDialog;

    FloatingActionButton fab;

    /* boolean variable to launch Alert Dialog
     upon successful login into the application*/
    private  static boolean isFirstRun = true;

    // boolean variable to check for doubleBackPress to exit app
    private boolean doublePressBackToExitApp = false;

    Uri url_no_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(io.icode.concaregh.app.R.layout.activity_home);

        spinnerGender = findViewById(io.icode.concaregh.app.R.id.spinnerGender);
        arrayAdapterGender = ArrayAdapter.createFromResource(this, io.icode.concaregh.app.R.array.gender, io.icode.concaregh.app.R.layout.spinner_item_home);
        arrayAdapterGender.setDropDownViewResource(io.icode.concaregh.app.R.layout.spinner_dropdown_item);
        spinnerGender.setAdapter(arrayAdapterGender);

        mDrawerLayout = findViewById(io.icode.concaregh.app.R.id.drawer);

        // setNavigationViewListener;
        navigationView = findViewById(io.icode.concaregh.app.R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // getting reference to the navigation drawer view objects in the nav_header
        circleImageView = navigationView.getHeaderView(0).findViewById(io.icode.concaregh.app.R.id.circularImageView);
        username = navigationView.getHeaderView(0).findViewById(io.icode.concaregh.app.R.id.username);
        email = navigationView.getHeaderView(0).findViewById(io.icode.concaregh.app.R.id.email);

        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, io.icode.concaregh.app.R.string.open, io.icode.concaregh.app.R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        //checks of there is support actionBar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getString(io.icode.concaregh.app.R.string.home));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }


        // checks if phone supports Google Play Services
        GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        btn_proceed = findViewById(io.icode.concaregh.app.R.id.proceed_Image);

        shake = AnimationUtils.loadAnimation(this, io.icode.concaregh.app.R.anim.anim_scale_out);

        //progressDialog = ProgressDialog.show(this,"","Please wait...",true,true);

        // floating action button onclick Listener and initialization
        fab = findViewById(io.icode.concaregh.app.R.id.fab);

        // call to the onclick Listener for floating button
        onClickFab();

        mAuth = FirebaseAuth.getInstance();

        onClickCircularImageView();

        // Calling method to display a welcome message
        displayWelcomeMessage();

        // method call
        loadUserInfo();

    }

    @Override
    protected void onStart(){
        super.onStart();
        if(mAuth.getCurrentUser() == null){
            // starts the login activity currently logged in user is null(login_bg_1 logged in user)
            startActivity(new Intent(HomeActivity.this,LoginActivity.class));
            // finishes the activity
            finish();
            // Add a custom animation ot the activity
            CustomIntent.customType(HomeActivity.this,"fadein-to-fadeout");

        }
        /*else if(mAuth.getCurrentUser() != null){
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

        }*/
    }

    // Onclick Listener method for floating button
    private void onClickFab(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // starts the Contact us activity
                startActivity(new Intent(HomeActivity.this,ContactUsActivity.class));
                // Add a custom animation ot the activity
                CustomIntent.customType(HomeActivity.this,"up-to-bottom");
            }
        });

    }

    // Method to display a welcome  message to user when he or she logs in
    private void displayWelcomeMessage() {

        // get current logged in user
        FirebaseUser user = mAuth.getCurrentUser();

        /* getting username of the currently
         Logged In user and storing in a string */
        String username = user.getDisplayName();

        // checks if user is not equal to null
        if (user != null) {

            if (isFirstRun) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    AlertDialog.Builder builder = new
                            AlertDialog.Builder(HomeActivity.this,
                            android.R.style.Theme_Material_Dialog_Alert);
                    builder.setTitle(" Welcome, " + username);
                    builder.setMessage(getString(io.icode.concaregh.app.R.string.welcome_message));
                    builder.setCancelable(false);
                    builder.setIcon(io.icode.concaregh.app.R.mipmap.app_icon_round);

                    builder.setPositiveButton("DISMISS", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // closes the Alert dialog
                            dialogInterface.dismiss();
                        }
                    });
                    // Creates and displays the alert Dialog
                    AlertDialog alert = builder.create();
                    alert.show();

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                    builder.setTitle(" Welcome, " + username);
                    builder.setMessage(getString(io.icode.concaregh.app.R.string.welcome_message));
                    builder.setCancelable(false);
                    builder.setIcon(io.icode.concaregh.app.R.mipmap.app_icon_round);

                    builder.setPositiveButton("DISMISS", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // closes the Alert dialog
                            dialogInterface.dismiss();
                        }
                    });
                    // Creates and displays the alert Dialog
                    AlertDialog alert = builder.create();
                    //builder.show();
                    alert.show();
                }
            }
        }
        // sets it to false
        isFirstRun = false;

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
                Glide.with(HomeActivity.this)
                        .load(_photoUrl)
                        .into(circleImageView);
            }
            // checks if the username of the current user is not null
            if(user.getDisplayName() != null){
                username.setText(" Username : " + _username);
            }
            // checks if the email of the current user is not null
            if(user.getEmail() != null){
                email.setText(" Email : " + _email);
            }

        }

    }

    private void loadUserInfoWithoutImage(){

        // get current logged in user
        FirebaseUser user = mAuth.getCurrentUser();

        // getting the username and image of current logged in user
        String _photoUrl = user.getPhotoUrl().toString();
        String _username = user.getDisplayName();
        String _email = user.getEmail();

        if(user != null){
            // checks if the photeUrl of the current user is null
            if(_username != null && _email != null && _photoUrl == null){
                username.setText(" Username : " + _username);
                email.setText( " Email : " + _email);
                //Load image for user
                Glide.with(this)
                        .asBitmap()
                        .load(io.icode.concaregh.app.R.drawable.no_image)
                        .into(circleImageView);
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
                // Add a custom animation ot the activity
                CustomIntent.customType(HomeActivity.this,"fadein-to-fadeout");
            }
        });

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item){

        // handle navigation item click
        switch (item.getItemId()){
            case io.icode.concaregh.app.R.id.menu_home:
                // do nothing
                break;
            case io.icode.concaregh.app.R.id.menu_edit_profile:
                // start EditProfile activity
                startActivity(new Intent(HomeActivity.this,EditProfileActivity.class));
                // Add a custom animation ot the activity
                CustomIntent.customType(HomeActivity.this,"fadein-to-fadeout");
                break;
            /*case R.id.orders:
                // start orders fragment
                break;*/
             case io.icode.concaregh.app.R.id.menu_about:
                // start About Us Activity
                 startActivity(new Intent(HomeActivity.this,AboutUsActivity.class));
                 // Add a custom animation ot the activity
                 CustomIntent.customType(HomeActivity.this,"bottom-to-up");
                break;
            case io.icode.concaregh.app.R.id.menu_sign_out:
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
        getMenuInflater().inflate(io.icode.concaregh.app.R.menu.menu_user,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        switch(item.getItemId()){
            case io.icode.concaregh.app.R.id.menu_about:
                // starts the about us activity
                startActivity(new Intent(this,AboutUsActivity.class));
                // Add a custom animation ot the activity
                CustomIntent.customType(HomeActivity.this,"bottom-to-up");
                break;
            case io.icode.concaregh.app.R.id.menu_contact:
                // starts the Contact us activity
                startActivity(new Intent(this,ContactUsActivity.class));
                // Add a custom animation ot the activity
                CustomIntent.customType(HomeActivity.this,"up-to-bottom");
                break;
            case io.icode.concaregh.app.R.id.menu_exit:
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

        if(doublePressBackToExitApp){
            super.onBackPressed();
            return;
        }
        doublePressBackToExitApp = true;
        // display a toast message to user
        Toast.makeText(HomeActivity.this,getString(io.icode.concaregh.app.R.string.exit_app_message),Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        },2000);

    }

    // method to log user out of the system
    private void logout(){

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle(getString(io.icode.concaregh.app.R.string.logout));
        builder.setMessage(getString(io.icode.concaregh.app.R.string.logout_msg));

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // logs current user out of the system
                mAuth.signOut();
                // finishes the activity
                finish();
                // starts the activity
                startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                // Add a custom animation ot the activity
                CustomIntent.customType(HomeActivity.this,"fadein-to-fadeout");
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

        // sets animation on the button
        btn_proceed.clearAnimation();
        btn_proceed.startAnimation(shake);
        // gets the text from the spinner and passes it to the next activity
        String gender = spinnerGender.getSelectedItem().toString().trim();

        Intent intent = new Intent(this,PlaceOrderActivity.class);
        intent.putExtra("gender",gender);
        startActivity(intent);
        // Add a custom animation ot the activity
        CustomIntent.customType(HomeActivity.this,"fadein-to-fadeout");
    }

    @Override
    public void finish() {
        super.finish();
        // Add a custom animation ot the activity
        CustomIntent.customType(HomeActivity.this,"fadein-to-fadeout");
    }
}
