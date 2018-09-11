package com.example.icode.concare.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
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

public class LoginActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    private ProgressBar progressBar;

    private EditText editTextEmail;
    private EditText editTextPassword;

    FirebaseAuth mAuth;

    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialization of the objects of the views
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        progressBar = findViewById(R.id.progressBar);

        progressDialog = ProgressDialog.show(this,"","Please wait...",true,true);

        mAuth = FirebaseAuth.getInstance();
        relativeLayout = findViewById(R.id.relativeLayout);

    }

    @Override
    protected void onStart(){
        super.onStart();
        // checks if user is currently logged in
        if(mAuth.getCurrentUser() != null){
            LoginActivity.this.finish();
            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
        }
        // checks if user is not currently logged in
        else if(mAuth.getCurrentUser() == null){
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    timer.cancel();
                }
            },2000);
        }

    }


    //onLoginButtonClick method
    public void onLoginButtonClick(View view){

        //gets text from the editTExt fields
        String _email = editTextEmail.getText().toString().trim();
        String _password = editTextPassword.getText().toString().trim();

        if(_email.isEmpty()){
            editTextEmail.setError(getString(R.string.error_empty_email));
            return;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(_email).matches()){
            editTextEmail.setError(getString(R.string.email_invalid));
            return;
        }
        else if(_password.isEmpty()){
           editTextPassword.setError(getString(R.string.error_empty_password));
           editTextPassword.requestFocus();
           return;
        }
        else if(_password.length() < 6 ){
            editTextPassword.setError(getString(R.string.error_password_length));
            editTextPassword.requestFocus();
            return;
        }
        else{
            // a call to the loginUser method
            loginUser();
        }
    }

    //Method to handle user login
    public void loginUser(){

        // display the progressDialog
        /*progressDialog = ProgressDialog.show(LoginActivity.this,"",null,true,true);
        progressDialog.setMessage("Please wait...");*/

        progressBar.setVisibility(View.VISIBLE);

        //gets text from the editTExt fields
        final String _email = editTextEmail.getText().toString().trim();
        final String _password = editTextPassword.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(_email,_password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                      if(task.isSuccessful()){
                          // dismiss progress bar upon a successful login
                          progressBar.setVisibility(View.GONE);
                          // clears the text fields
                          clearTextFields();
                          // display a success message
                          Toast.makeText(LoginActivity.this,getString(R.string.login_successful),Toast.LENGTH_SHORT).show();
                          // starts the home activity
                          LoginActivity.this.finish();
                          startActivity(new Intent(LoginActivity.this,HomeActivity.class));

                      }
                      else{
                          progressBar.setVisibility(View.GONE);
                          // display a message if there is an error
                          Snackbar.make(relativeLayout,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();
                          //progressDialog.dismiss();
                      }

                        progressBar.setVisibility(View.GONE);
                    }
                });

    }

    //Link to the signUp Interface
    public void onSignUpLinkClick(View view){
        LoginActivity.this.finish();
        // creates an instance of the intent class and opens the signUpctivity
        startActivity(new Intent(this,SignUpActivity.class));
    }

    //method to clear text fields
    public void clearTextFields(){
        editTextEmail.setText(null);
        editTextPassword.setText(null);
    }

    // forgot password method
    public void onForgotPasswordClick(View view) {
        LoginActivity.this.finish();
        startActivity(new Intent(LoginActivity.this,ResetPasswordActivity.class));
        // do nothing for now
        //Toast.makeText(getApplicationContext(),"Forgot Password",Toast.LENGTH_LONG).show();
    }
}
