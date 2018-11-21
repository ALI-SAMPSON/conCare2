package io.icode.concaregh.application.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import io.icode.concaregh.application.R;
import io.icode.concaregh.application.chatApp.MainActivity;
import maes.tech.intentanim.CustomIntent;

public class OrderActivity extends AppCompatActivity {

    private AppCompatSpinner spinnerGender;
    private ArrayAdapter<CharSequence> arrayAdapterGender;

    private ImageView btn_proceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        //checks of there is support actionBar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getString(R.string.text_welcome));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        spinnerGender = findViewById(io.icode.concaregh.application.R.id.spinnerGender);
        arrayAdapterGender = ArrayAdapter.createFromResource(this, R.array.gender, R.layout.spinner_item_home);
        arrayAdapterGender.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerGender.setAdapter(arrayAdapterGender);

        btn_proceed = findViewById(R.id.proceed_Image);

        // Initializing Google Ads
        MobileAds.initialize(this,"ca-app-pub-4501853719724548~4076180577");
        // getting reference to AdView
        AdView adView = findViewById(R.id.adView);
        //AdRequest object contains runtime information about a single ad request
        AdRequest adRequest = new AdRequest.Builder().build();
        // Load ads into Banner Ads
        adView.loadAd(adRequest);

        //AdView 1
        AdView adView1 = findViewById(R.id.adView1);
        AdRequest adRequest1 = new AdRequest.Builder().build();
        adView1.loadAd(adRequest1);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                // starts the about us activity
                startActivity(new Intent(OrderActivity.this,HomeActivity.class));
                // Add a custom animation ot the activity
                CustomIntent.customType(OrderActivity.this,"fadein-to-fadeout");
                // finish
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // starts the about us activity
        startActivity(new Intent(OrderActivity.this,HomeActivity.class));
        // Add a custom animation ot the activity
        CustomIntent.customType(OrderActivity.this,"fadein-to-fadeout");
        // finish
        finish();
    }

    //Click Listener for proceed button on homeActivity
    public void onProceedButtonClick(View view) {

        // adds a custom animation
        YoYo.with(Techniques.FlipInY).playOn(btn_proceed);

        // gets the text from the spinner and passes it to the next activity
        String gender = spinnerGender.getSelectedItem().toString().trim();

        Intent intent = new Intent(this,PlaceOrderActivity.class);
        intent.putExtra("gender",gender);
        startActivity(intent);
        // Add a custom animation ot the activity
        CustomIntent.customType(OrderActivity.this,"fadein-to-fadeout");
    }

    @Override
    public void finish() {
        super.finish();
        // Add a custom animation ot the activity
        CustomIntent.customType(OrderActivity.this,"fadein-to-fadeout");
    }
}
