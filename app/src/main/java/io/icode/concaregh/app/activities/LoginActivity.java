package io.icode.concaregh.app.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import io.icode.concaregh.app.R;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;
import maes.tech.intentanim.CustomIntent;

public class LoginActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    private ProgressDialog progressDialog;

    private EditText editTextEmail;
    private EditText editTextPassword;

    private Button forgot_password;
    private Button buttonLogin;
    Button buttonSignUpLink;

    private CardView my_card;

    private CircleImageView app_logo;

    FirebaseAuth mAuth;

    private RelativeLayout relativeLayout;

    private Animation shake;

    private boolean isCircularImageViewClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        app_logo = findViewById(R.id.app_logo);

        // scales the image in and out
        app_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // instance of the animation class
                Animation scale_image = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_scale_imageview);
                app_logo.clearAnimation();
                app_logo.startAnimation(scale_image);
            }
        });

        // initialization of the objects of the views
        editTextEmail = findViewById(io.icode.concaregh.app.R.id.editTextEmail);
        editTextPassword = findViewById(io.icode.concaregh.app.R.id.editTextPassword);

        my_card = findViewById(io.icode.concaregh.app.R.id.login_cardView);

        // getting the ids of the views
        forgot_password = findViewById(R.id.forgot_password);
        buttonLogin = findViewById(R.id.ButtonLogin);
        buttonSignUpLink = findViewById(R.id.ButtonSignUpLink);

        relativeLayout = findViewById(io.icode.concaregh.app.R.id.relativeLayout);

        progressBar = findViewById(io.icode.concaregh.app.R.id.progressBar);

        progressDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("please wait...");

        mAuth = FirebaseAuth.getInstance();

        // animation to anim_shake button
        shake = AnimationUtils.loadAnimation(LoginActivity.this, io.icode.concaregh.app.R.anim.anim_shake);

        // animation to bounce  App logo on Login screen
        bounce_views();

        // method call to add animation to card
        onCardViewClickAnim();

    }

    @Override
    protected void onStart(){
        super.onStart();
        // checks if user is currently logged in
        if(mAuth.getCurrentUser() != null){

            // start the activity
            startActivity(new Intent(LoginActivity.this,HomeActivity.class));

            // Add a custom animation ot the activity
            CustomIntent.customType(LoginActivity.this,"fadein-to-fadeout");

            // finish the activity
            finish();
        }
    }


    // method to animate the app logo
    private void bounce_views(){

        // animation to bounce image
        //YoYo.with(Techniques.ZoomIn).repeat(2).playOn(app_logo);

        // bounce the Login Button
        YoYo.with(Techniques.Shake)
                .repeat(1).playOn(my_card);

    }

    // animation loaded when cardView is clicked
    private void onCardViewClickAnim(){

        my_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // adds a waving animation to card
                YoYo.with(Techniques.Wave).playOn(my_card);

            }
        });

    }

    //onLoginButtonClick method
    public void onLoginButtonClick(View view){

        //gets text from the editTExt fields
        String _email = editTextEmail.getText().toString().trim();
        String _password = editTextPassword.getText().toString().trim();

        if(_email.isEmpty()){
            editTextEmail.clearAnimation();
            editTextEmail.startAnimation(shake);
            editTextEmail.setError(getString(io.icode.concaregh.app.R.string.error_empty_email));
            return;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(_email).matches()){
            editTextEmail.clearAnimation();
            editTextEmail.startAnimation(shake);
            editTextEmail.setError(getString(io.icode.concaregh.app.R.string.email_invalid));
            return;
        }
        else if(_password.isEmpty()){
            editTextPassword.clearAnimation();
            editTextPassword.startAnimation(shake);
            editTextPassword.setError(getString(io.icode.concaregh.app.R.string.error_empty_password));
            editTextPassword.requestFocus();
           return;
        }
        else if(_password.length() < 6 ){
            editTextPassword.clearAnimation();
            editTextPassword.startAnimation(shake);
            editTextPassword.setError(getString(io.icode.concaregh.app.R.string.error_password_length));
            editTextPassword.requestFocus();
            return;
        }
        else{
            // a call to the loginUser method
            loginUser();
        }
    }

    // Method to handle user login
    public void loginUser(){

        // shakes the button
        buttonLogin.clearAnimation();
        buttonLogin.startAnimation(shake);

        // shows the progressBar
        //progressBar.setVisibility(View.VISIBLE);
        progressDialog.show();

        //gets text from the editTExt fields
        final String _email = editTextEmail.getText().toString().trim();
        final String _password = editTextPassword.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(_email,_password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                      if(task.isSuccessful()){

                          // Method to check if email is Verified
                          checkIfEmailIsVerified();

                      }
                      else{
                          // display a message if there is an error
                          Snackbar.make(relativeLayout,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();
                      }
                        // dismisses the progressBar
                        //progressBar.setVisibility(View.GONE);
                      progressDialog.dismiss();
                    }
                });

    }

    // Method that checks if the email enter is verified
    private void checkIfEmailIsVerified(){

        FirebaseUser user = mAuth.getCurrentUser();

        boolean isEmailVerified = user.isEmailVerified();

        // Check is emailVerified is true
        if(isEmailVerified){

            // display a successful login message
            Toast.makeText(LoginActivity.this,getString(io.icode.concaregh.app.R.string.login_successful),Toast.LENGTH_SHORT).show();

            // clear the text fields
            clearTextFields();

            // start the home activity
            startActivity(new Intent(LoginActivity.this,HomeActivity.class));

            // Add a custom animation ot the activity
            CustomIntent.customType(LoginActivity.this,"fadein-to-fadeout");

            // finishes this activity(prevents user from going back to this activity when back button is pressed)
            finish();

        }
        else {

            // display a message to the user to verify email
            Toast.makeText(LoginActivity.this,getString(io.icode.concaregh.app.R.string.text_email_not_verified),Toast.LENGTH_LONG).show();

            // signs user out and restarts the Login Activity
            mAuth.signOut();

            // restarts the activity
            startActivity(new Intent (LoginActivity.this,LoginActivity.class));

            // Add a custom animation ot the activity
            CustomIntent.customType(LoginActivity.this,"fadein-to-fadeout");

            // finish the activity
            finish();

        }

    }

    // Link to the signUp Interface
    public void onSignUpLinkClick(View view){

        // creates an instance of the intent class and opens the signUpctivity
        startActivity(new Intent(this,SignUpActivity.class));

        // Add a custom animation ot the activity
        CustomIntent.customType(LoginActivity.this,"fadein-to-fadeout");

        // finish the activity
        finish();
    }

    // Method to clear text fields
    public void clearTextFields(){
        editTextEmail.setText(null);
        editTextPassword.setText(null);
    }

    // Forgot password method
    public void onForgotPasswordClick(View view) {

        // shakes the button when clicked
        YoYo.with(Techniques.FlipOutX).playOn(forgot_password);

        // start the ResetPassword Activity
        startActivity(new Intent(LoginActivity.this,ResetPasswordActivity.class));

        // Add a custom animation ot the activity
        CustomIntent.customType(LoginActivity.this,"fadein-to-fadeout");

        // finish the activity
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        // Add a custom animation ot the activity
        CustomIntent.customType(LoginActivity.this,"fadein-to-fadeout");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);

        // finishes the activity
        finish();

        // Add a custom animation ot the activity
        //CustomIntent.customType(LoginActivity.this,"fadein-to-fadeout");
    }
}
