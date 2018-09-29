package com.example.icode.concare.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.icode.concare.R;
import com.example.icode.concare.models.Users;
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
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import maes.tech.intentanim.CustomIntent;

public class SignUpActivity extends AppCompatActivity {

    // instance variables
    private EditText editTextEmail;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextPhoneNumber;

    private AppCompatSpinner spinnerGender;
    private ArrayAdapter<CharSequence> spinnerAdapter;

    private NestedScrollView nestedScrollView;

    //instance of firebase Authentication
    FirebaseAuth mAuth;

    // progressBar to load image uploading to database
    ProgressBar progressBar;

    // progressBar to load signUp user
    ProgressBar progressBar1;

    private CircleImageView circleImageView;
    private EditText username;

    Uri uriProfileImage;

    String profileImageUrl;

    private static final int  REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //initialization of the view objects
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);

        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerAdapter = ArrayAdapter.createFromResource(this,R.array.gender,R.layout.spinner_item_sign_up);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_sign_up);
        spinnerGender.setAdapter(spinnerAdapter);

        circleImageView = findViewById(R.id.circularImageView);

        nestedScrollView = findViewById(R.id.nestedScrollView);

        progressBar = findViewById(R.id.progressBar);

        progressBar1 = findViewById(R.id.progressBar1);

        mAuth = FirebaseAuth.getInstance();

        // a method call to the chooseImage method
        chooseImage();
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
        //checks to make sure the editText fields are not empty
        if(circleImageView.getDrawable() == null){
            Toast.makeText(SignUpActivity.this,"Please select an image to continue",Toast.LENGTH_LONG).show();
            return;
        }
        else if(email.isEmpty()){
            editTextEmail.setError(getString(R.string.error_empty_email));
            return;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError(getString(R.string.email_invalid));
            return;
        }
        else if(username.isEmpty()) {
            editTextUsername.setError(getString(R.string.error_empty_username));
            editTextUsername.requestFocus();
            return;
        }
        else if(password.isEmpty()){
            editTextPassword.setError(getString(R.string.error_empty_password));
            editTextPassword.requestFocus();
            return;
        }
        else if(password.length() < 6 ){
            editTextPassword.setError(getString(R.string.error_password_length));
            editTextPassword.requestFocus();
            return;
        }
        else if(phone.isEmpty()){
            editTextPhoneNumber.setError(getString(R.string.error_empty_phone));
            return;
        }
        else if(phone.length() != 10){
            editTextPhoneNumber.setError(getString(R.string.phone_invalid));
            return;
        }
        else{
            //method call
            signUp();
        }

    }

    // method to select image from gallery
    public void chooseImage(){

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // method to open user's phone gallery
                openGallery();
            }
        });

    }

    // another method to create a gallery intent to choose image from gallery
    private void openGallery(){
        // create an intent object to open user gallery for image
        Intent pickImage = new Intent();
        pickImage.setType("image/*");
        pickImage.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(pickImage,"Select Profile Picture"),REQUEST_CODE);
        // Add a custom animation ot the activity
        CustomIntent.customType(SignUpActivity.this,"fadein-to-fadeout");
    }


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

            } catch (IOException e) {
                Snackbar.make(nestedScrollView,e.getMessage(),Snackbar.LENGTH_LONG).show();
            }

            circleImageView.setImageURI(uriProfileImage);
        }

    }

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
                            profileImageUrl = taskSnapshot.getDownloadUrl().toString();
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

    // method to save username and profile image
    private void saveUserInfo(){

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

    // signUp method
    public void signUp(){

        progressBar1.setVisibility(View.VISIBLE);

        //gets text from the editTExt fields
        final String email = editTextEmail.getText().toString().trim();
        final String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        final String gender = spinnerGender.getSelectedItem().toString().trim();
        final String phone = editTextPhoneNumber.getText().toString().trim();

        // register user details into firebase database
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //initializes the user object
                            Users users = new Users(email, username ,gender, phone);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(mAuth.getCurrentUser().getUid())
                                    .setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        // method call
                                        saveUserInfo();

                                        // Method call to sendVerification link to the email address
                                        sendVerificationEmail();

                                        // display a success message and verification sent
                                        Snackbar.make(nestedScrollView,getString(R.string.text_sign_up_and_verification_sent),Snackbar.LENGTH_LONG).show();

                                        //clears text Fields
                                        clearTextFields();
                                    }
                                    else {
                                        // display a message if there is an error
                                        Snackbar.make(nestedScrollView,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            });

                        }
                        else{
                            // display a message if there is an error
                            Snackbar.make(nestedScrollView,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();
                        }

                        progressBar1.setVisibility(View.GONE);

                    }
                });

    }

    // Method to send verification link to email to user after sign Up
    private void sendVerificationEmail(){

        FirebaseUser user = mAuth.getCurrentUser();

        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    // sign user out after verification link is sent successfully
                    mAuth.signOut();
                }
                else {
                    // display error message
                    Snackbar.make(nestedScrollView,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }

    //link from the Sign Up page to the Login Page
    public void onLoginLinkButtonClick(View view){
        SignUpActivity.this.finish();
        // creates an instance of the intent class and opens the signUp activity
        startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
        // Add a custom animation ot the activity
        CustomIntent.customType(SignUpActivity.this,"fadein-to-fadeout");
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
}
