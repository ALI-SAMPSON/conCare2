package com.example.icode.concare.activities;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Toast;

import com.example.icode.concare.R;
import com.example.icode.concare.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

public class SignUpActivity extends AppCompatActivity {

    // instance variables
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPhoneNumber;

    private AppCompatSpinner spinnerGender;
    private ArrayAdapter<CharSequence> spinnerAdapter;

    private FirebaseDatabase usersdB;
    private DatabaseReference usersRef;

    private NestedScrollView nestedScrollView;

    //instance of firebase Authentication
    private FirebaseAuth mAuth;


    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //initialization of the view objects
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);

        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerAdapter = ArrayAdapter.createFromResource(this,R.array.gender,R.layout.spinner_item_sign_up);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_sign_up);
        spinnerGender.setAdapter(spinnerAdapter);

        nestedScrollView = findViewById(R.id.nestedScrollView);

        mAuth = FirebaseAuth.getInstance();

        //instantiation of the Firebase classes
        /*usersdB = FirebaseDatabase.getInstance();
        usersRef = usersdB.getReference().child("Users");*/

        //instance of th Users class
        //users = new Users();
    }

    //Sign Up Button Method
    public void onSignUpButtonClick(View view){

        //gets text from the editTExt fields
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String phone = editTextPhoneNumber.getText().toString().trim();

        /**
         * Input validation
         */
        //checks to make sure the editText fields are not empty
        if(email.isEmpty()){
            editTextEmail.setError(getString(R.string.error_empty_email));
            return;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError(getString(R.string.email_invalid));
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

    // signUp method
    public void signUp(){

        //displaying the progressDialog when sign Up button is clicked
        progressDialog = ProgressDialog.show(SignUpActivity.this,"",null,true,true);
        progressDialog.setMessage("Please wait...");

        //gets text from the editTExt fields
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        final String gender = spinnerGender.getSelectedItem().toString().trim();
        final String phone = editTextPhoneNumber.getText().toString().trim();

        // register user details into firebase database
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // hides the progressBar
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            //initializes the user object
                            Users users = new Users(email, gender, phone);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(mAuth.getCurrentUser().getUid())
                                    .setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        // avoid going back to the activity
                                        finish();
                                        //dismiss progress dialog upon a successful login
                                        progressDialog.dismiss();
                                        // display a success message
                                        Snackbar.make(nestedScrollView,getString(R.string.sign_up_successful),Snackbar.LENGTH_SHORT).show();
                                    }
                                    else {
                                        // display a message if there is an error
                                        Snackbar.make(nestedScrollView,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });

                        }
                        else{
                            // display a message if there is an error
                            Snackbar.make(nestedScrollView,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();
                            // clears the fields
                            clearTextFields();
                        }

                    }
                });

    }

    //link from the Sign Up page to the Login Page
    public void onLoginLinkButtonClick(View view){
        finish();
        // creates an instance of the intent class and opens the signUpctivity
        startActivity(new Intent(this,LoginActivity.class));
    }

    //clears the textfields
    public void clearTextFields(){
        editTextEmail.setText(null);
        editTextPassword.setText(null);
        editTextPhoneNumber.setText(null);
    }
}
