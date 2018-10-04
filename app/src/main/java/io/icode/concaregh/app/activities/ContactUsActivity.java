package io.icode.concaregh.app.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import io.icode.concaregh.app.R;

import maes.tech.intentanim.CustomIntent;

public class ContactUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(io.icode.concaregh.app.R.layout.activity_contact_us);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getString(io.icode.concaregh.app.R.string.text_contact));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case android.R.id.home:
                // sends user to Home Activity
                finish();
                startActivity(new Intent(ContactUsActivity.this,HomeActivity.class));
                // Add a custom animation ot the activity
                CustomIntent.customType(ContactUsActivity.this,"fadein-to-fadeout");
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

        // finishes the activity
        finish();

        // open the LoginActivity
        startActivity(new Intent(ContactUsActivity.this,HomeActivity.class));

        // Add a custom animation ot the activity
        CustomIntent.customType(ContactUsActivity.this,"fadein-to-fadeout");

    }
}
