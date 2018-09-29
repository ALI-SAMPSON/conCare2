package com.example.icode.concare.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.icode.concare.R;
import com.google.firebase.auth.FirebaseAuth;

import maes.tech.intentanim.CustomIntent;

import static java.lang.Thread.sleep;

public class SplashScreenActivity extends AppCompatActivity {

    private final int SPLASH_SCREEN_DISPLAY_TIME = 3000;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // firebase instance
        mAuth = FirebaseAuth.getInstance();

    }


    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            // starts the activity
            startActivity(new Intent(SplashScreenActivity.this,HomeActivity.class));
            // finishes the activity
            finish();
            // Add a custom animation ot the activity
            CustomIntent.customType(SplashScreenActivity.this,"fadein-to-fadeout");
        }
        else{
            // open splash screen first
            splashScreen();
        }
    }

    //class to the handle the splash screen activity
    public void splashScreen() {

        Thread timer = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(SPLASH_SCREEN_DISPLAY_TIME);
                    //Creates and start the intent of the next activity
                    Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(intent);
                    // finishes the activity
                    finish();//this prevents the app from going back to the splash screen
                    // Add a custom animation ot the activity
                    CustomIntent.customType(SplashScreenActivity.this,"fadein-to-fadeout");
                    super.run();
                } catch (InterruptedException e) {
                    Toast.makeText(SplashScreenActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        };
        //starts the timer
        timer.start();
    }

    @Override
    public void finish() {
        super.finish();
        // Add a custom animation ot the activity
        CustomIntent.customType(SplashScreenActivity.this,"fadein-to-fadeout");
    }
}