package io.icode.concaregh.application.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import io.icode.concaregh.application.R;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;
import io.icode.concaregh.application.models.Admin;
import io.icode.concaregh.application.models.Users;
import maes.tech.intentanim.CustomIntent;

public class SignUpActivity extends AppCompatActivity {

    // instance variables
    //private CircleImageView circleImageView;
    private EditText editTextEmail;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextPhoneNumber;

    Button ButtonSignUp;
    Button ButtonLoginLink;

    private AppCompatSpinner spinnerGender;
    private ArrayAdapter<CharSequence> spinnerAdapter;

    private Animation shake;

    private NestedScrollView nestedScrollView;

    //instance of firebase Authentication
    FirebaseAuth mAuth;

    FirebaseDatabase userdB;

    DatabaseReference userRef;

    DatabaseReference adminRef;

    //FirebaseStorage storage;

    // progressBar to load image uploading to database
    //ProgressBar progressBar;

    // progressBar to load signUp user
    ProgressBar progressBar1;

    private CircleImageView circleImageView;

    private CircleImageView app_logo;

    Uri uriProfileImage;

    String profileImageUrl;

    Users users;

    Admin admin;

    private static final int  REQUEST_CODE = 1;

    private String CHANNEL_ID = "notification_channel_id";

    private int notificationId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nestedScrollView = findViewById(R.id.nestedScrollView);

        //initialization of the view objects
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);

        ButtonSignUp = findViewById(R.id.buttonSignUp);
        ButtonLoginLink = findViewById(R.id.buttonLoginLink);

        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.gender, R.layout.spinner_item_sign_up);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_sign_up);
        spinnerGender.setAdapter(spinnerAdapter);

        //circleImageView = findViewById(R.id.circularImageView);

        mAuth = FirebaseAuth.getInstance();

        users = new Users();

        admin = new Admin();


        userdB = FirebaseDatabase.getInstance();

        userRef = userdB.getReference("Users");

        adminRef = FirebaseDatabase.getInstance().getReference("Admin");

        //progressBar = findViewById(R.id.progressBar);
        // sets a custom color on progressBar
        //progressBar.getIndeterminateDrawable().setColorFilter(0xFE5722,PorterDuff.Mode.MULTIPLY);

        progressBar1 = findViewById(R.id.progressBar1);
        // sets a custom color on progressBar
        //progressBar1.getIndeterminateDrawable().setColorFilter(0xFE5722,PorterDuff.Mode.MULTIPLY);

        shake = AnimationUtils.loadAnimation(SignUpActivity.this, R.anim.anim_shake);

        app_logo = findViewById(R.id.circularImageView);

        // a method call to the chooseImage method
        //chooseImage();

        animateLogo();

    }

    // method to animate the app logo
    public void animateLogo(){
        // animate Logo when imageView is clicked
        app_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.RotateIn).playOn(app_logo);
            }
        });
    }

    //Sign Up Button Method
    public void onSignUpButtonClick(View view){

        //gets text from the editTExt fields
        String email = editTextEmail.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String phone = editTextPhoneNumber.getText().toString().trim();

        /*
         * Input validation
         */
        if(TextUtils.isEmpty(email)){
            editTextEmail.clearAnimation();
            editTextEmail.startAnimation(shake);
            editTextEmail.setError(getString(io.icode.concaregh.application.R.string.error_empty_email));
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.clearAnimation();
            editTextEmail.startAnimation(shake);
            editTextEmail.setError(getString(io.icode.concaregh.application.R.string.email_invalid));
        }
        else if(TextUtils.isEmpty(username)) {
            editTextUsername.clearAnimation();
            editTextUsername.startAnimation(shake);
            editTextUsername.setError(getString(io.icode.concaregh.application.R.string.error_empty_username));
            editTextUsername.requestFocus();
        }
        else if(TextUtils.isEmpty(password)){
            editTextPassword.clearAnimation();
            editTextPassword.startAnimation(shake);
            editTextPassword.setError(getString(io.icode.concaregh.application.R.string.error_empty_password));
            editTextPassword.requestFocus();
        }
        else if(password.length() < 6 ){
            editTextPassword.clearAnimation();
            editTextPassword.startAnimation(shake);
            editTextPassword.setError(getString(R.string.error_password_length));
            editTextPassword.requestFocus();
        }
        else if(TextUtils.isEmpty(phone)){
            editTextPhoneNumber.clearAnimation();
            editTextPhoneNumber.startAnimation(shake);
            editTextPhoneNumber.setError(getString(R.string.error_empty_phone));
            editTextPhoneNumber.requestFocus();
        }
        else if(phone.length() != 10){
            editTextPhoneNumber.clearAnimation();
            editTextPhoneNumber.startAnimation(shake);
            editTextPhoneNumber.setError(getString(R.string.phone_invalid));
            editTextPhoneNumber.requestFocus();
        }
        else{
            //method call
            signUp();
        }

    }

    // method to save username and profile image
    private void saveUsername(){

        String _username = editTextUsername.getText().toString().trim();

        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null){
            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(_username)
                    .build();

            // updates user info with the passed username and image
            user.updateProfile(userProfileChangeRequest)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                // dismiss progress bar
                                //progressBar1.setVisibility(View.GONE);
                            }
                            else{
                                // display an error message
                                Snackbar.make(nestedScrollView,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
            }

    }

    // signUp method
    public void signUp(){

        // displays the progressBar
        progressBar1.setVisibility(View.VISIBLE);

        //gets text from the editTExt fields
        final String email = editTextEmail.getText().toString().trim();
        final String username = editTextUsername.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String gender = spinnerGender.getSelectedItem().toString().trim();
        final String phone = editTextPhoneNumber.getText().toString().trim();

        // register user details into firebase database
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            final FirebaseUser user = mAuth.getCurrentUser();

                            assert user != null;

                            users.setEmail(email);
                            users.setUsername(username);
                            users.setGender(gender);
                            users.setPhoneNumber(phone);
                            users.setUid(user.getUid());
                            users.setImageUrl("");
                            users.setStatus("offline"); // set status to offline by default
                            users.setSearch(username.toLowerCase());

                            userRef.child(user.getUid()).setValue(users)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        // method call to save
                                        // username and profile picture
                                        //saveUserInfo();

                                        saveUsername();

                                        // Sends a notification to the user after signing up successfully
                                        // Creating an explicit intent for the activity in the app
                                        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        PendingIntent pendingIntent = PendingIntent.getActivity(SignUpActivity.this, 0, intent, 0);

                                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(SignUpActivity.this, CHANNEL_ID)
                                                .setSmallIcon(R.mipmap.app_logo_round)
                                                .setContentTitle(getString(R.string.app_name))
                                                .setContentText("Sign Up Successful" + " -> " + "(" + username + ")" + "." +
                                                        " Please proceed to login and " + "Thank you for joining us!")

                                                .setStyle(new NotificationCompat.BigTextStyle()
                                                .bigText("Sign Up Successful" + " -> " + "(" + username + ")" + "." +
                                                        " Please proceed to login and " + "Thank you for joining us!"))
                                                // Set the intent that will fire when the user taps the notification
                                                .setWhen(System.currentTimeMillis())
                                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                                                .setContentIntent(pendingIntent)
                                                .setAutoCancel(true);
                                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(SignUpActivity.this);
                                        notificationManager.notify(notificationId,mBuilder.build());

                                        // Method call to sendVerification
                                        // link to users's email address
                                        //sendVerificationEmail();

                                        // display a success message and verification sent
                                        Snackbar.make(nestedScrollView,getString(R.string.sign_up_successful),Snackbar.LENGTH_LONG).show();

                                        // sign out user
                                        // after signing Up successfully
                                        mAuth.signOut();

                                        //clears text Fields
                                        clearTextFields();

                                        // start the login Activity after Sign Up is successful
                                        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));

                                        // Add a custom animation ot the activity
                                        CustomIntent.customType(SignUpActivity.this,"fadein-to-fadeout");

                                        // finish the activity
                                        finish();

                                    }
                                    else {

                                        // display a message if there is an error
                                        Snackbar.make(nestedScrollView,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();

                                        // sign out user
                                        mAuth.signOut();

                                    }
                                }
                            });



                        }
                        else{
                            // display a message if there is an error
                            Snackbar.make(nestedScrollView,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();

                            // sign out user
                            mAuth.signOut();
                        }

                        // dismisses the progressBar
                        progressBar1.setVisibility(View.GONE);

                    }
                });


    }

    // Method to send verification link to email to user after sign Up
    private void sendVerificationEmail(){

        FirebaseUser user = mAuth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    /* sign user out
                      after verification
                      link is sent successfully
                     */
                    mAuth.signOut();

                }
                else {

                    /* sign user out
                       after verification
                       link is sent successfully
                     */
                    mAuth.signOut();

                    // display error message
                    Snackbar.make(nestedScrollView,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();

                }
            }
        });

    }

    //link from the Sign Up page to the Login Page
    public void onLoginLinkButtonClick(View view){

        // starts this activity
        startActivity(new Intent(SignUpActivity.this,SignInActivity.class));

        // Add a custom animation ot the activity
        CustomIntent.customType(SignUpActivity.this,"fadein-to-fadeout");

        // finish activity
        finish();

    }

    //clears the textfields
    public void clearTextFields(){
        editTextEmail.setText(null);
        editTextUsername.setText(null);
        editTextPassword.setText(null);
        editTextPhoneNumber.setText(null);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // starts this activity
        startActivity(new Intent(SignUpActivity.this,SignInActivity.class));

        // Add a custom animation ot the activity
        CustomIntent.customType(SignUpActivity.this,"fadein-to-fadeout");

        // finishes the activity
        finish();

    }
}
