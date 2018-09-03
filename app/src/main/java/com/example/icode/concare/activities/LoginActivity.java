package com.example.icode.concare.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.icode.concare.R;
import com.example.icode.concare.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    private EditText editTextEmail;
    private EditText editTextPassword;

    private FirebaseAuth mAuth;

    private FirebaseDatabase usersdB;
    private DatabaseReference usersRef;

    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialization of the objects of the views
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        mAuth = FirebaseAuth.getInstance();
        relativeLayout = findViewById(R.id.relativeLayout);

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
            editTextPassword.setError(getString(R.string.email_invalid));
            return;
        }
        else if(_password.isEmpty()){
           editTextPassword.setError(getString(R.string.error_empty_password));
           return;
        }
        else if(_password.length() < 6 ){
            editTextPassword.setError(getString(R.string.error_password_length));
        }
        else{
            loginUser();
        }
    }

    //Method to handle user login
    public void loginUser(){

        // display the progressDialog
        progressDialog = ProgressDialog.show(LoginActivity.this,"",null,true,true);
        progressDialog.setMessage("Please wait...");

        //gets text from the editTExt fields
        final String _email = editTextEmail.getText().toString().trim();
        final String _password = editTextPassword.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(_email,_password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                      if(task.isSuccessful()){
                          // dismiss progress dialog upon a successful login
                          progressDialog.dismiss();
                          // display a success message
                          Snackbar.make(relativeLayout,getString(R.string.login_successful),Snackbar.LENGTH_SHORT).show();
                          // clears the text fields
                          clearTextFields();
                          // starts the home activity
                          startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                      }
                      else{
                          // display a message if there is an error
                          Snackbar.make(relativeLayout,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();
                      }
                    }
                });

        /*usersRef.child(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Users users = dataSnapshot.getValue(Users.class);
                    //checks if password entered equals the one in the database
                    if(password.equals(users.getPassword())){
                        final Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                progressDialog.dismiss(); //dismisses the progress dialog
                                timer.cancel(); // cancels the timer
                            }
                        },10000);
                        //makes a toast
                        Toast.makeText(LoginActivity.this,"Login Successful",Toast.LENGTH_LONG).show();
                        clearTextFields();
                        Intent intentHome = new Intent(LoginActivity.this,HomeActivity.class);
                        startActivity(intentHome);

                    }
                    else if(!password.equals(users.getPassword())){
                        final Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                progressDialog.dismiss(); //dismisses the progress dialog
                                timer.cancel(); // cancels the timer
                            }
                        },5000);
                        //makes a toast
                        Toast.makeText(LoginActivity.this,"Username or Password is incorrect",Toast.LENGTH_LONG).show();
                        clearTextFields();
                    }
                }
                else{
                    final Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            progressDialog.dismiss(); //dismisses the progress dialog
                            timer.cancel(); // cancels the timer
                        }
                    },5000);
                    //makes a toast
                    Toast.makeText(LoginActivity.this,"Invalid Username and Password",Toast.LENGTH_LONG).show();
                    clearTextFields();
                    Intent intentHome = new Intent(LoginActivity.this,HomeActivity.class);
                    startActivity(intentHome);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this,databaseError.toException().toString(),Toast.LENGTH_LONG).show();
            }
        });
        */


    }

    //Link to the signUp Interface
    public void onSignUpLinkClick(View view){
        //creates an instance of the intent class
        Intent intentSignUp = new Intent(LoginActivity.this, SignUpActivity.class);
        intentSignUp.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intentSignUp); //starts the instance of the intent class
    }

    public void clearTextFields(){
        editTextEmail.setText(null);
        editTextPassword.setText(null);
    }

}
