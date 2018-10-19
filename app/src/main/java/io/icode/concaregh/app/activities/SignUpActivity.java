package io.icode.concaregh.app.activities;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import io.icode.concaregh.app.R;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import io.icode.concaregh.app.models.Users;
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

    private static final int  REQUEST_CODE = 1;

    private String CHANNEL_ID = "notification_channel_id";

    private int notificationId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

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

        nestedScrollView = findViewById(R.id.nestedScrollView);

        //circleImageView = findViewById(R.id.circularImageView);

        mAuth = FirebaseAuth.getInstance();

        userdB = FirebaseDatabase.getInstance();

        userRef = userdB.getReference("Users");

        users = new Users();

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

        /**
         * Input validation
         */
        if(email.isEmpty()){
            editTextEmail.clearAnimation();
            editTextEmail.startAnimation(shake);
            editTextEmail.setError(getString(io.icode.concaregh.app.R.string.error_empty_email));
            return;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.clearAnimation();
            editTextEmail.startAnimation(shake);
            editTextEmail.setError(getString(io.icode.concaregh.app.R.string.email_invalid));
            return;
        }
        else if(username.isEmpty()) {
            editTextUsername.clearAnimation();
            editTextUsername.startAnimation(shake);
            editTextUsername.setError(getString(io.icode.concaregh.app.R.string.error_empty_username));
            editTextUsername.requestFocus();
            return;
        }
        else if(password.isEmpty()){
            editTextPassword.clearAnimation();
            editTextPassword.startAnimation(shake);
            editTextPassword.setError(getString(io.icode.concaregh.app.R.string.error_empty_password));
            editTextPassword.requestFocus();
            return;
        }
        else if(password.length() < 6 ){
            editTextPassword.clearAnimation();
            editTextPassword.startAnimation(shake);
            editTextPassword.setError(getString(R.string.error_password_length));
            editTextPassword.requestFocus();
            return;
        }
        else if(phone.isEmpty()){
            editTextPhoneNumber.clearAnimation();
            editTextPhoneNumber.startAnimation(shake);
            editTextPhoneNumber.setError(getString(R.string.error_empty_phone));
            return;
        }
        else if(phone.length() != 10){
            editTextPhoneNumber.clearAnimation();
            editTextPhoneNumber.startAnimation(shake);
            editTextPhoneNumber.setError(getString(R.string.phone_invalid));
            return;
        }
        else{
            //method call
            signUp();
        }

    }

    // method to select image from gallery

    /*
    public void chooseImage(){

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Adds a custom animation to the view using Library
                YoYo.with(Techniques.FlipInX).playOn(circleImageView);

                // method to open user's phone gallery
                openGallery();
            }
        });

    }
    */

    // another method to create a gallery intent to choose image from gallery

    /*
    private void openGallery(){
        // create an intent object to open user gallery for image
        Intent pickImage = new Intent();
        pickImage.setType("image/*");
        pickImage.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(pickImage,"Select Profile Picture"),REQUEST_CODE);
        // Add a custom animation ot the activity
        CustomIntent.customType(SignUpActivity.this,"fadein-to-fadeout");
    }
    */

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){
            uriProfileImage = data.getData();
            try {
                // sets the picked image to the imageView
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uriProfileImage);
                circleImageView.setImageBitmap(bitmap);
                uploadImage();

            }
            catch(IOException e){
                Snackbar.make(nestedScrollView,e.getMessage(),Snackbar.LENGTH_LONG).show();
            }

            circleImageView.setImageURI(uriProfileImage);
        }

    }
    */

    /*
    private void uploadImage(){

        final StorageReference profileImageRef = FirebaseStorage.getInstance()
                .getReference("Profile Pic/" + System.currentTimeMillis() + ".jpg");

        if(uriProfileImage != null){
            // displays the progressBar
            progressBar.setVisibility(View.VISIBLE);
            profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setVisibility(View.GONE);
                            //profileImageUrl = taskSnapshot.getDownloadUrl().toString();
                            // gets the download Url of the image
                            profileImageRef.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUrl = uri;
                                    profileImageUrl = downloadUrl.toString();
                                    users.setImageUrl(downloadUrl.toString());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(nestedScrollView,e.getMessage(),Snackbar.LENGTH_LONG).show();
                }
            });
        }

    }

    */


    // method to save username and profile image
    /*private void saveUserInfo(){

        String _username = editTextUsername.getText().toString().trim();

        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null && profileImageUrl != null){
            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(_username)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();

            // updates user info with the passed username and image
            user.updateProfile(userProfileChangeRequest)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                // dismiss progress bar
                                progressBar1.setVisibility(View.GONE);
                            }
                            else{
                                // dismiss progress dialog
                                progressBar1.setVisibility(View.GONE);
                                // display an error message
                                Snackbar.make(nestedScrollView,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
            }

    }
    */

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

                            users.setEmail(email);
                            users.setUsername(username);
                            users.setGender(gender);
                            users.setPhoneNumber(phone);

                            FirebaseUser user = mAuth.getCurrentUser();

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
                                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        PendingIntent pendingIntent = PendingIntent.getActivity(SignUpActivity.this, 0, intent, 0);

                                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(SignUpActivity.this, CHANNEL_ID)
                                                .setSmallIcon(R.mipmap.app_logo_round)
                                                .setContentTitle(getString(R.string.app_name))
                                                .setContentText("Sign Up Successful. A verification link has been\n" +
                                                        " sent to " + mAuth.getCurrentUser().getEmail() + "." +
                                                        " Please visit your inbox to verify\n" +
                                                        " our email address. Thanks for joining us!")
                                                .setStyle(new NotificationCompat.BigTextStyle()
                                                .bigText("Sign Up Successful. A verification link has been\n" +
                                                        " sent to " + mAuth.getCurrentUser().getEmail() + "." +
                                                        " Please visit your inbox to verify\n" +
                                                        " our email address. Thanks for joining us!"))
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
                                        sendVerificationEmail();

                                        // display a success message and verification sent
                                        Snackbar.make(nestedScrollView,getString(R.string.text_sign_up_and_verification_sent),Snackbar.LENGTH_LONG).show();

                                        //Snackbar.make(nestedScrollView,getString(R.string.sign_up_successful),Snackbar.LENGTH_LONG).show();

                                        //mAuth.signOut();

                                        //clears text Fields
                                        clearTextFields();

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

                    /** sign user out
                      after verification link is sent successfully
                     *
                     */
                    mAuth.signOut();

                }
                else {

                    /** sign user out
                     after verification link is sent successfully
                     *
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

        mAuth.signOut();

        // starts this activity
        startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
        // Add a custom animation ot the activity
        CustomIntent.customType(SignUpActivity.this,"fadein-to-fadeout");

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
    public void finish() {
        super.finish();
        // Add a custom animation ot the activity
        CustomIntent.customType(SignUpActivity.this,"fadein-to-fadeout");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // open the LoginActivity
        startActivity(new Intent(SignUpActivity.this,LoginActivity.class));

        // Add a custom animation ot the activity
        CustomIntent.customType(SignUpActivity.this,"fadein-to-fadeout");

        // finishes the activity
        finish();

    }
}
