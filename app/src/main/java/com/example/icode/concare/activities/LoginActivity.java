package com.example.icode.concare.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.icode.concare.R;
import com.example.icode.concare.models.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends AppCompatActivity {

    ProgressDialog progressDialog;

    private EditText editTextUsername;
    private EditText editTextPassword;

    private Users users;

    private FirebaseDatabase usersdB;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);

        users = new Users();

        usersdB = FirebaseDatabase.getInstance();
        usersRef = usersdB.getReference().child("Users");

    }

    //onLoginButtonClick method
    public void onLoginButtonClick(View view){

        String error_edit_text = "This is a required field";

        String password_length = "Password length cannot be less than 6";

        //gets text from the editTExt fields
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(username.equals("")){
            editTextUsername.setError(error_edit_text);
        }
        else if(password.equals("")){
            editTextPassword.setError(error_edit_text);
        }
        else if(password.length() < 6 ){
            editTextPassword.setError(password_length);
        }
        else if(username.equals(null) && password.equals(null)){
            Toast.makeText(this,"Both fields cannot be left empty",Toast.LENGTH_LONG).show();
        }
        else{
            onLogin();
        }
    }

    //Method to handle login
    public void onLogin(){

        progressDialog = ProgressDialog.show(LoginActivity.this,"",null,true,true);
        progressDialog.setMessage("Please wait...");

        //gets text from the editTExt fields
        final String username = editTextUsername.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        usersRef.child(username).addValueEventListener(new ValueEventListener() {
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


    }

    //Link to the signUp Interface
    public void onSignUpLinkClick(View view){
        //creates an instance of the intent class
        Intent intentSignUp = new Intent(LoginActivity.this, SignUpActivity.class);
        intentSignUp.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intentSignUp); //starts the instance of the intent class
    }

    public void clearTextFields(){
        editTextUsername.setText(null);
        editTextPassword.setText(null);
    }

}
