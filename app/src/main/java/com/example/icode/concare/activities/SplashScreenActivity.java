package com.example.icode.concare.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.icode.concare.R;
import com.google.firebase.auth.FirebaseAuth;

import maes.tech.intentanim.CustomIntent;

import static java.lang.Thread.sleep;

public class SplashScreenActivity extends AppCompatActivity {

    private TextView app_title;

    private TextView watermark;

    private final int SPLASH_SCREEN_DISPLAY_TIME = 4000;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //app_title = findViewById(R.id.splash_screen_text);

        //watermark = findViewById(R.id.water_mark);

        // firebase instance
        mAuth = FirebaseAuth.getInstance();

        runAnimation();

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
                    // displays a toast
                    Toast.makeText(SplashScreenActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        };
        //starts the timer
        timer.start();
    }

    // method to set animation on textViews
    private  void runAnimation(){

        Animation slide_in_left = AnimationUtils.loadAnimation(SplashScreenActivity.this, android.R.anim.fade_in);
        slide_in_left.reset();
        // setting animation for the App Title on the splashScreen
        TextView app_title = findViewById(R.id.splash_screen_text);
        app_title.clearAnimation();
        app_title.startAnimation(slide_in_left);

        Animation slide_out_right = AnimationUtils.loadAnimation(SplashScreenActivity.this, android.R.anim.slide_out_right);
        slide_in_left.reset();
        // setting animation for the App watermark on the splashScreen
        TextView watermark = findViewById(R.id.water_mark);
        watermark.clearAnimation();
        watermark.startAnimation(slide_out_right);

    }

    @Override
    public void finish() {
        super.finish();
        // Add a custom animation ot the activity
        CustomIntent.customType(SplashScreenActivity.this,"fadein-to-fadeout");
    }
}