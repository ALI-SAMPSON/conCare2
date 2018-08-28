package com.example.icode.concare.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.example.icode.concare.R;
import com.example.icode.concare.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private EditText editTextTelNumber;

    private AppCompatSpinner spinnerGender;
    private ArrayAdapter<CharSequence> spinnerAdapter;

    private FirebaseDatabase usersdB;
    private DatabaseReference usersRef;

    private Users users;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //initialization of the view objects
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextTelNumber = findViewById(R.id.editTextTelephoneNumber);

        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerAdapter = ArrayAdapter.createFromResource(this,R.array.gender,R.layout.spinner_item_sign_up);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_sign_up);
        spinnerGender.setAdapter(spinnerAdapter);

        //instantiation of the Firebase classes
        usersdB = FirebaseDatabase.getInstance();
        usersRef = usersdB.getReference().child("Users");

        //instance of th Users class
        users = new Users();


    }

    //Sign Up Button Method
    public void onSignUpButtonClick(View view){

        String error_edit_text = "This is a required field";

        String password_length = "Password length cannot be less than 6";

        //gets text from the editTExt fields
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirm_password = editTextConfirmPassword.getText().toString().trim();
        String gender = spinnerGender.getSelectedItem().toString().trim();
        String telephone = editTextTelNumber.getText().toString().trim();

        //checks to make sure the editText fields are not empty
        if(username.equals(null)){
            editTextUsername.setError(error_edit_text);
        }
        else if(password.length() < 6 ){
            editTextPassword.setError(password_length);
        }
        else if(password.equals(null)){
            editTextConfirmPassword.equals(error_edit_text);
        }
        else if(confirm_password.equals(null)){
            editTextConfirmPassword.setError(error_edit_text);
        }
        else if(telephone.equals(null)){
            editTextTelNumber.setError(error_edit_text);
        }
        else if(!confirm_password.equals(password)){
            editTextConfirmPassword.setError("Password does not match!");
        }
        else{
            signUp();
        }

    }

    public void signUp(){

        //displaying the progressDialog when sign Up button is clicked
        progressDialog = ProgressDialog.show(SignUpActivity.this,"",null,true,true);
        progressDialog.setMessage("Please wait...");

        //gets text from the editTExt fields
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirm_password = editTextConfirmPassword.getText().toString().trim();
        String gender = spinnerGender.getSelectedItem().toString().trim();
        String telephone = editTextTelNumber.getText().toString().trim();

        //sets the text gotten to the database fields
        users.setUsername(username);
        users.setPassword(password);
        users.setConfirmPassword(confirm_password);
        users.setGender(gender);
        users.setTelephoneNumber(telephone);

        //sets the values to the database
        usersRef.child(username).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    final Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            timer.cancel();
                        }
                    },10000);
                    //create a toast after a successful login
                    Toast.makeText(SignUpActivity.this,"Sign Up Successful,...", Toast.LENGTH_LONG).show();
                    clearTextFields();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this,e.getStackTrace().toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    //link from the Sign Up page to the Login Page
    public void onLoginLinkButtonClick(View view){
        //new instance of the Intent class to open the Login Page
        Intent intentLogin = new Intent(SignUpActivity.this,LoginActivity.class);
        intentLogin.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intentLogin);
    }

    //clears the textfields
    public void clearTextFields(){
        editTextUsername.setText(null);
        editTextPassword.setText(null);
        editTextConfirmPassword.setText(null);
        editTextTelNumber.setText(null);
    }
}
