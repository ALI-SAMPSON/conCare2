package io.icode.concaregh.application.activities;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.icode.concaregh.application.R;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import de.hdodenhof.circleimageview.CircleImageView;
import io.icode.concaregh.application.chatApp.ChatActivity;
import io.icode.concaregh.application.chatApp.MessageActivity;
import io.icode.concaregh.application.constants.Constants;
import io.icode.concaregh.application.constants.TextJustification;
import io.icode.concaregh.application.models.Admin;
import io.icode.concaregh.application.models.Users;
import io.icode.concaregh.application.notifications.Data;
import io.icode.concaregh.application.notifications.Token;
import maes.tech.intentanim.CustomIntent;

@SuppressWarnings("ALL")
public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    // ads unitId
    private static final String AD_UNIT_ID = "ca-app-pub-4501853719724548~4076180577";

    LinearLayout layout_displayBanner;

    AdView adView;

    AdView adView1;

    // global  or class variables
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private Animation shake;

    FirebaseAuth mAuth;

    Users users;

    Admin admin;

    DatabaseReference userInfoRef;

    DatabaseReference adminRef;

    NavigationView navigationView;

    CircleImageView circleImageView;
    TextView username;
    TextView email;

    TextView welcome_msg,text_1,text_2,text_3,text_4;

    ProgressDialog progressDialog;

    FloatingActionButton fab;

    /* boolean variable to launch Alert Dialog
     upon successful login into the application*/
    private  static boolean isFirstRun = true;

    // boolean variable to check for doubleBackPress to exit app
    private boolean doublePressBackToExitApp = false;

    Uri url_no_image;

    // strings to store the details of admin to pass
    String uid;
    String admin_username;
    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(io.icode.concaregh.application.R.layout.activity_home);

        layout_displayBanner = findViewById(R.id.layout_ads);

        mDrawerLayout = findViewById(R.id.drawer);

        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        // setNavigationViewListener;
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // getting reference to the navigation drawer view objects in the nav_header
        circleImageView = navigationView.getHeaderView(0).findViewById(R.id.circleImageView);
        username = navigationView.getHeaderView(0).findViewById(R.id.username);
        email = navigationView.getHeaderView(0).findViewById(R.id.email);

        welcome_msg = findViewById(R.id.welcome_msg);
        text_1 = findViewById(R.id.text_1);
        text_2 = findViewById(R.id.text_2);
        text_3 = findViewById(R.id.text_3);
        text_4 = findViewById(R.id.text_4);

        // code to justify text
        TextJustification.justify(welcome_msg);
        TextJustification.justify(text_1);
        TextJustification.justify(text_2);
        TextJustification.justify(text_3);
        TextJustification.justify(text_4);

        //checks of there is support actionBar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getString(R.string.home));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }


        shake = AnimationUtils.loadAnimation(this, R.anim.anim_scale_out);

        // floating action button onclick Listener and initialization
        fab = findViewById(R.id.fab);

        // getting instance of firebaseAuth
        mAuth = FirebaseAuth.getInstance();

        users = new Users();

        admin = new Admin();

        // reference to the admin class
        adminRef = FirebaseDatabase.getInstance().getReference("Admin");

        // Calling method to display a welcome message
        displayWelcomeMessage();

        // method call
        loadUserInfo();

        // call to the onclick Listener for floating button
        onClickFab();

        // method call for on click listener for imageView
        onClickCircularImageView();

        // method call
        onTextViewClick();

        // method call to change ProgressDialog style based on the android version of user's phone
        changeProgressDialogBackground();

        // update user's device token
        updateToken(FirebaseInstanceId.getInstance().getToken());

        // method call to create ads
        createBanner();

    }

    // Update currentUser's  device token
    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constants.TOKENS_REF);
        Token token1 = new Token(token);
        reference.child(mAuth.getCurrentUser().getUid()).setValue(token1);
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

    // Circular ImageView ClickListener
    private void onClickCircularImageView(){

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Adds a custom animation to the view using Library
                YoYo.with(Techniques.RubberBand).playOn(circleImageView);

                // start EditProfile activity
                startActivity(new Intent(HomeActivity.this,EditProfileActivity.class));

                // Add a custom animation ot the activity
                CustomIntent.customType(HomeActivity.this,"fadein-to-fadeout");

            }
        });

    }

    // Add animation to textViews
    public void onTextViewClick(){

        // adds animation to username TextView
        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.Wave).playOn(username);
            }
        });

        // adds animation to email TextView
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.Shake).playOn(email);
            }
        });
    }

    // ******************************** For Admob

    private void createBanner() {

        MobileAds.initialize(this,getString(R.string.admob_app_id));

        // getting reference to AdView
        adView = findViewById(R.id.adView);
        /*
         * Create an ad request.
         */
        AdRequest adRequest = new AdRequest.Builder().build();

        /*
         * Start loading the ad in the background.
         */
        try {
            adView.loadAd(adRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to display a welcome  message to user when he or she logs in
    private void displayWelcomeMessage() {

        // get current logged in user
        FirebaseUser user = mAuth.getCurrentUser();

        assert user != null;

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
                    builder.setMessage(getString(R.string.welcome_message));
                    builder.setCancelable(false);
                    builder.setIcon(R.mipmap.app_logo_round);

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

                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                    builder.setTitle(" Welcome, " + username);
                    builder.setMessage(getString(R.string.welcome_message));
                    builder.setCancelable(false);
                    builder.setIcon(R.mipmap.app_logo_round);

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
        final FirebaseUser user = mAuth.getCurrentUser();

        userInfoRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        userInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Users users = dataSnapshot.getValue(Users.class);

                if(user != null) {
                    // sets username and email
                    username.setText(" Username : " + user.getDisplayName());
                    email.setText(" Email : " + user.getEmail());
                    // checks if imageUrl is not null
                    if (user.getPhotoUrl() == null) {
                        circleImageView.setImageResource(R.drawable.profile_icon);
                    }
                    else {
                        Glide.with(getApplicationContext()).load(user.getPhotoUrl()).into(circleImageView);
                    }
                }
                else{

                    // sets username, email and image of user
                    username.setText(" Username : " + getString(R.string.dummy_username));
                    email.setText(" Email : " + getString(R.string.dummy_email));
                    circleImageView.setImageResource(R.drawable.profile_icon);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // display error message
                Toast.makeText(HomeActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
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
                // Add a custom animation ot the activity
                CustomIntent.customType(HomeActivity.this,"fadein-to-fadeout");
                break;
            /*case R.id.orders:
                // start orders fragment
                break;*/
             case R.id.menu_about:
                // start About Us Activity
                 startActivity(new Intent(HomeActivity.this,AboutUsActivity.class));
                 // Add a custom animation ot the activity
                 CustomIntent.customType(HomeActivity.this,"bottom-to-up");
                break;
            case R.id.menu_contact:
                // starts the Contact us activity
                startActivity(new Intent(HomeActivity.this,ContactUsActivity.class));
                // Add a custom animation ot the activity
                CustomIntent.customType(HomeActivity.this,"up-to-bottom");
            case R.id.menu_sign_out:
                // a call to logout method
               signOut();

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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        switch(item.getItemId()){
            case R.id.menu_share:
                // method call
                shareIntent();
                break;
            case R.id.menu_chat:
                // navigate user to the message activity
                navigateUserToMessageActivity();
                break;
            case R.id.menu_about:
                // starts the about us activity
                startActivity(new Intent(HomeActivity.this,AboutUsActivity.class));
                // Add a custom animation ot the activity
                CustomIntent.customType(HomeActivity.this,"bottom-to-up");
                break;
            case R.id.menu_contact:
                // starts the Contact us activity
                startActivity(new Intent(HomeActivity.this,ContactUsActivity.class));
                // Add a custom animation ot the activity
                CustomIntent.customType(HomeActivity.this,"up-to-bottom");
                break;
            case R.id.menu_exit:

                // close application
                exitApplication();

                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // method to share app link to other users
    public void shareIntent(){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String sharingSubject = "CONCARE GH";
        String sharingText = "https://play.google.com/store/apps/details?id=io.icode.concaregh.application";
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT,sharingSubject);
        sharingIntent.putExtra(Intent.EXTRA_TEXT,sharingText);
        startActivity(Intent.createChooser(sharingIntent,"Share with"));
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

        // check if drawer is opened
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            // closes the drawer
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }

    }

    // method to log user out of the system
    private void  signOut(){

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle(getString(io.icode.concaregh.application.R.string.logout));
        builder.setMessage(getString(R.string.logout_msg));

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // show dialog
                progressDialog.show();

                // delays the running of the ProgressBar for 3 secs
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        // dismiss dialog
                        progressDialog.dismiss();

                        // logs current user out of the system
                        mAuth.signOut();

                        // starts the activity
                        startActivity(new Intent(HomeActivity.this,SignInActivity.class));

                        // Add a custom animation ot the activity
                        CustomIntent.customType(HomeActivity.this,"fadein-to-fadeout");

                        // finishes the activity
                        finish();

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
    public void finish() {
        super.finish();
        // Add a custom animation ot the activity
        CustomIntent.customType(HomeActivity.this,"fadein-to-fadeout");
    }

    //open the message activity to start a chat conversation with admin (ConCare GH)
    public void onChatUsButtonClick(View view) {

        // method call
        navigateUserToMessageActivity();

    }

    // method to navigate user to the message activity to begin chatting
    private void navigateUserToMessageActivity(){

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference adminRef = rootRef.child(Constants.ADMIN_REF);

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    uid = snapshot.child("adminUid").getValue(String.class);
                    admin_username = snapshot.child("username").getValue(String.class);
                    status = snapshot.child("status").getValue(String.class);

                }

                // starts the chat activity
                Intent intentChat = new Intent(HomeActivity.this,MessageActivity.class);
                intentChat.putExtra("uid",uid);
                intentChat.putExtra("username",admin_username);
                intentChat.putExtra("status",status);
                startActivity(intentChat);
                // Add a custom animation ot the activity
                //CustomIntent.customType(HomeActivity.this,"fadein-to-fadeout");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // display error message
                Toast.makeText(HomeActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        };

        adminRef.addListenerForSingleValueEvent(eventListener);
    }

    public void onOrderButtonClick(View view) {
        // starts the about us activity
        startActivity(new Intent(HomeActivity.this,OrderActivity.class));
        // Add a custom animation ot the activity
        CustomIntent.customType(HomeActivity.this,"fadein-to-fadeout");
    }

    // method to change ProgressDialog style based on the android version of user's phone
    private void changeProgressDialogBackground(){

        // if the build sdk version >= android 5.0
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            //sets the background color according to android version
            progressDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_DARK);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("");
            progressDialog.setMessage("signing out...");
        }
        //else do this
        else{
            //sets the background color according to android version
            progressDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("");
            progressDialog.setMessage("signing out...");
        }

    }
}
