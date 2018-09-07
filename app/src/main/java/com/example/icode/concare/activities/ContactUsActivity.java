package com.example.icode.concare.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.icode.concare.R;

public class ContactUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getString(R.string.text_contact));
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
                ContactUsActivity.this.finish();
                startActivity(new Intent(ContactUsActivity.this,HomeActivity.class));
                break;
                default:
                    break;
        }
        return super.onOptionsItemSelected(item);
    }
}
