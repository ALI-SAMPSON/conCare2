package io.icode.concaregh.application.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import io.icode.concaregh.application.R;

import maes.tech.intentanim.CustomIntent;

public class ContactUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getString(io.icode.concaregh.application.R.string.text_contact));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Initializing Google Ads
        MobileAds.initialize(this,"ca-app-pub-4501853719724548~4076180577");
        // getting reference to AdView
        AdView adView = findViewById(R.id.adView);
        //AdRequest object contains runtime information about a single ad request
        AdRequest adRequest = new AdRequest.Builder().build();
        // Load ads into Banner Ads
        adView.loadAd(adRequest);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(ContactUsActivity.this,HomeActivity.class));
                // Add a custom animation ot the activity
                CustomIntent.customType(ContactUsActivity.this,"fadein-to-fadeout");
                // sends user to Home Activity
                finish();
                break;
                default:
                    break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        // Add a custom animation ot the activity
        CustomIntent.customType(ContactUsActivity.this,"fadein-to-fadeout");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // open the LoginActivity
        startActivity(new Intent(ContactUsActivity.this,HomeActivity.class));

        // Add a custom animation ot the activity
        CustomIntent.customType(ContactUsActivity.this,"fadein-to-fadeout");

        // finishes the activity
        finish();

    }
}
