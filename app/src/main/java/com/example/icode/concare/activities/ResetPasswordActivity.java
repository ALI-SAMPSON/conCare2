package com.example.icode.concare.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.icode.concare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Timer;
import java.util.TimerTask;

public class ResetPasswordActivity extends AppCompatActivity {

    // class variables
    ProgressBar progressBar;

    ProgressDialog progressDialog;

    CoordinatorLayout coordinatorLayout;

    FirebaseAuth mAuth;

    private EditText editTextEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // getting references to the views
        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        editTextEmail = findViewById(R.id.email);

        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressBar);

        progressDialog = ProgressDialog.show(this,"","Please wait...",true,true);

    }

    @Override
    protected void onStart(){
        super.onStart();
        // checks if user is not currently logged in
        if(mAuth.getCurrentUser() == null){
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

    // method to send user back to login Activity
    public void goBackButton(View view) {
        ResetPasswordActivity.this.finish();
        startActivity(new Intent(ResetPasswordActivity.this,LoginActivity.class));

    }

    // onClick listener method for reset password Button
    public void resetPasswordButton(View view) {

        String email = editTextEmail.getText().toString().trim();

        if(email.isEmpty()){
            editTextEmail.setError(getString(R.string.email_registered));
            editTextEmail.requestFocus();
            return;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError(getString(R.string.email_registered));
            editTextEmail.requestFocus();
            return;
        }
        else{
            //method call
            resetPassword();
        }

    }

    // method to reset password
    private void resetPassword(){

        // displays the progressBar
        progressBar.setVisibility(View.VISIBLE);

        String email = editTextEmail.getText().toString().trim();

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ResetPasswordActivity.this,"We have sent you instructions to reset your password!",Toast.LENGTH_LONG).show();
                        }
                        else{
                            // displays an error message
                            Snackbar.make(coordinatorLayout,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();
                        }

                        progressBar.setVisibility(View.GONE);
                    }
                });
    }


}
