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

    // variables for adView
    AdView adView;

    AdRequest adRequest;

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
        adView = findViewById(R.id.adView);
        //AdRequest object contains runtime information about a single ad request
        adRequest = new AdRequest.Builder().build();
        // Load ads into Banner Ads
        adView.loadAd(adRequest);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case android.R.id.home:

                // sends user to Home Activity
                finish();

                break;

                default:
                    break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // finishes the activity
        finish();

    }
}
