package io.icode.concaregh.app.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import io.icode.concaregh.app.R;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
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
        setContentView(io.icode.concaregh.app.R.layout.activity_splash_screen);

        //app_title = findViewById(R.id.splash_screen_text);

        //watermark = findViewById(R.id.water_mark);

        // firebase instance
        mAuth = FirebaseAuth.getInstance();

        // method call
        runAnimation();

    }


    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){

            // finishes the activity
            finish();

            // starts the activity
            startActivity(new Intent(SplashScreenActivity.this,HomeActivity.class));

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

                    // finishes the activity
                    finish(); // this prevents the app from going back to the splash screen

                    //Creates and start the intent of the next activity
                    startActivity(new Intent(SplashScreenActivity.this,LoginActivity.class));

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

        // setting animation for the App Title on the splashScreen
        TextView app_title = findViewById(R.id.splash_screen_text);

        //add an animation using the YoYo Library
        YoYo.with(Techniques.FadeIn)
                .duration(800)
                .repeat(2)
                .playOn(app_title);


        // setting animation for the App watermark on the splashScreen
        TextView watermark = findViewById(R.id.water_mark);

        //add an animation using the YoYo Library
        YoYo.with(Techniques.BounceInLeft)
                .duration(1000)
                .repeat(2)
                .playOn(watermark);


    }

    @Override
    public void finish() {
        super.finish();
        // Add a custom animation ot the activity
        CustomIntent.customType(SplashScreenActivity.this,"fadein-to-fadeout");
    }
}