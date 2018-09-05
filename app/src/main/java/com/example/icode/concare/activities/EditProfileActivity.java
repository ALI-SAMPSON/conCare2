package com.example.icode.concare.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.Toolbar;
//import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.icode.concare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    // class variables
    private CircleImageView circleImageView;
    private EditText username;

    Uri uriProfileImage;

    String profileImageUrl;

    ProgressBar progressBar;

    ProgressBar progressBar1;

    ProgressDialog progressDialog;

    RelativeLayout relativeLayout;

    FirebaseAuth mAuth;

    private static final int  REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        circleImageView = findViewById(R.id.circularImageView);
        username = findViewById(R.id.editTextUsername);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getString(R.string.title_edit_profile));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        progressBar = findViewById(R.id.progressBar);
        progressBar1 = findViewById(R.id.progressBar1);

        relativeLayout = findViewById(R.id.relativeLayout);

        mAuth = FirebaseAuth.getInstance();

        // a method call to the chooseImage method
        chooseImage();

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
                Snackbar.make(relativeLayout,e.getMessage(),Snackbar.LENGTH_LONG).show();
                //e.printStackTrace();
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
                    Snackbar.make(relativeLayout,e.getMessage(),Snackbar.LENGTH_LONG).show();
                }
            });
        }

    }

    // save button listener
    public void onUpdateButtonClick(View view) {
        saveUserInfo(); // method call
    }

    // method to save username and profile image
    private void saveUserInfo(){

        String _username = username.getText().toString().trim();

        if(_username.isEmpty()) {
            username.setError(getString(R.string.error_empty_field));
            username.requestFocus();
            return;
        }

        /*progressDialog = ProgressDialog.show(this, getString(R.string.text_uploading_details),
                getString(R.string.text_please_wait),true,true);*/

        // progressBar for update Button
        progressBar1.setVisibility(View.VISIBLE);

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
                                // display a success message
                                Toast.makeText(EditProfileActivity.this,"Profile Updated Successfully",Toast.LENGTH_LONG).show();
                            }
                            else{
                                // dismiss progress dialog
                                progressDialog.dismiss();
                                // display an error message
                                Snackbar.make(relativeLayout,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
        }

    }

    // method to load user information
    private void loadUserInfo(){

        FirebaseUser user  = mAuth.getCurrentUser();

        String _photoUrl = user.getPhotoUrl().toString();
        String _username = user.getDisplayName();

        if(user != null){

            if(user.getPhotoUrl() != null){
                Glide.with(this)
                        .load(_photoUrl)
                        .into(circleImageView);
            }
            if(user.getDisplayName() != null){
                username.setText(_username);
            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                //start home activity when back button is pressed
                startActivity(new Intent(this,HomeActivity.class));
                finish();
                default:
                    break;
        }
        return super.onOptionsItemSelected(item);
    }

}
