package com.example.icode.concare.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.icode.concare.R;
import com.example.icode.concare.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

import maes.tech.intentanim.CustomIntent;

public class LoginActivity extends AppCompatActivity {


    private ProgressBar progressBar;

    private EditText editTextEmail;
    private EditText editTextPassword;

    private AppCompatButton appCompatButtonLogin;

    FirebaseAuth mAuth;

    private RelativeLayout relativeLayout;

    Animation shake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialization of the objects of the views
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        appCompatButtonLogin = findViewById(R.id.appCompatButtonLogin);

        relativeLayout = findViewById(R.id.relativeLayout);

        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        // animation to shake button
        shake = AnimationUtils.loadAnimation(this,R.anim.shake);

    }

    @Override
    protected void onStart(){
        super.onStart();
        // checks if user is currently logged in
        if(mAuth.getCurrentUser() != null){
            // start the activity
            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
            // finish the activity
            finish();
            // Add a custom animation ot the activity
            CustomIntent.customType(LoginActivity.this,"fadein-to-fadeout");
        }
    }


    //onLoginButtonClick method
    public void onLoginButtonClick(View view){

        //gets text from the editTExt fields
        String _email = editTextEmail.getText().toString().trim();
        String _password = editTextPassword.getText().toString().trim();

        if(_email.isEmpty()){
            editTextEmail.setError(getString(R.string.error_empty_email));
            appCompatButtonLogin.setAnimation(shake);
            return;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(_email).matches()){
            editTextEmail.setError(getString(R.string.email_invalid));
            appCompatButtonLogin.setAnimation(shake);
            return;
        }
        else if(_password.isEmpty()){
           editTextPassword.setError(getString(R.string.error_empty_password));
            appCompatButtonLogin.setAnimation(shake);
           editTextPassword.requestFocus();
           return;
        }
        else if(_password.length() < 6 ){
            editTextPassword.setError(getString(R.string.error_password_length));
            appCompatButtonLogin.setAnimation(shake);
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

        progressBar.setVisibility(View.VISIBLE);

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

                        progressBar.setVisibility(View.GONE);
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
            Toast.makeText(LoginActivity.this,getString(R.string.login_successful),Toast.LENGTH_SHORT).show();

            // clear the text fields
            clearTextFields();

            // start the home activity
            startActivity(new Intent(LoginActivity.this,HomeActivity.class));

            // finishes this activity(prevents user from going back to this activity when back button is pressed)
            finish();

            // Add a custom animation ot the activity
            CustomIntent.customType(LoginActivity.this,"fadein-to-fadeout");

        }
        else {

            // display a message to the user to verify email
            Toast.makeText(LoginActivity.this,getString(R.string.text_email_not_verified),Toast.LENGTH_LONG).show();

            // signs user out and restarts the Login Activity
            mAuth.signOut();

            // finish the activity
            finish();

            // restarts the activity
            startActivity(new Intent (LoginActivity.this,LoginActivity.class));

            // Add a custom animation ot the activity
            CustomIntent.customType(LoginActivity.this,"fadein-to-fadeout");

        }

    }

    // Link to the signUp Interface
    public void onSignUpLinkClick(View view){
        // creates an instance of the intent class and opens the signUpctivity
        startActivity(new Intent(this,SignUpActivity.class));
        // finish the activity
        finish();
        // Add a custom animation ot the activity
        CustomIntent.customType(LoginActivity.this,"fadein-to-fadeout");
    }

    // Method to clear text fields
    public void clearTextFields(){
        editTextEmail.setText(null);
        editTextPassword.setText(null);
    }

    // Forgot password method
    public void onForgotPasswordClick(View view) {
        // start the ResetPassword Activity
        startActivity(new Intent(LoginActivity.this,ResetPasswordActivity.class));
        // finish the activity
        finish();
        // Add a custom animation ot the activity
        CustomIntent.customType(LoginActivity.this,"fadein-to-fadeout");
    }

    @Override
    public void finish() {
        super.finish();
        // Add a custom animation ot the activity
        CustomIntent.customType(LoginActivity.this,"fadein-to-fadeout");
    }
}
