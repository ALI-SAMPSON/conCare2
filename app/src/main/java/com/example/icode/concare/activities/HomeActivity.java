package com.example.icode.concare.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.icode.concare.R;
import com.example.icode.concare.fragements.FragmentEditProfile;
import com.example.icode.concare.models.CurrentUsers;
import com.example.icode.concare.models.Orders;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private AppCompatSpinner spinnerGender;
    private ArrayAdapter<CharSequence> arrayAdapterGender;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        spinnerGender = findViewById(R.id.spinnerGender);
        arrayAdapterGender = ArrayAdapter.createFromResource(this,R.array.gender,R.layout.spinner_item);
        arrayAdapterGender.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerGender.setAdapter(arrayAdapterGender);

        mDrawerLayout = findViewById(R.id.drawer);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        // a call to the navigationViewItemListener
        //setNavigationViewListener();

        //checks of there is support actionBar
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();

    }

    public void editProfileFragment(){

        // an instance of the edit profile fragment class
        FragmentEditProfile fragmentEditProfile = new FragmentEditProfile();

        // use fragment Manager and transactions to add the fragment to the screen
        FragmentManager fragmentManager = getSupportFragmentManager();

        // fragment transaction
        fragmentManager.beginTransaction()
                .add(R.id.profile_container,fragmentEditProfile)
                .commit();

    }

    @Override
    protected void onStart(){
        super.onStart();
        if(mAuth.getCurrentUser() == null){
            finish();
            // starts the login activity currently logged in user is null(no logged in user)
            startActivity(new Intent(this,LoginActivity.class));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item){
        // handle navigation item click
        switch (item.getItemId()){
            case R.id.edit_profile:
                editProfileFragment();
                //start edit_profile fragment
                break;
            case R.id.orders:
                // start orders fragment
                break;
            case R.id.sign_out:
                Toast.makeText(getApplicationContext(),"You clicked signout",Toast.LENGTH_LONG).show();
                // display a progressDialog
                /*ProgressDialog progressDialog =
                        ProgressDialog.show(this,"","sign out...",true,true);*/
                FirebaseAuth.getInstance().signOut();
                //progressDialog.dismiss();
                break;
                default:
                    break;
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // method to set the navigationView Listener
    private void setNavigationViewListener(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_user,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        switch(item.getItemId()){
            case R.id.about_us:
                //starts the about us activity
                startActivity(new Intent(this,AboutUsActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //Click Listener for proceed button on homeActivity
    public void onProceedButtonClick(View view) {

        String gender = spinnerGender.getSelectedItem().toString().trim();

        Intent intent = new Intent(this,PlaceOrderActivity.class);
        intent.putExtra("gender",gender);
        startActivity(intent);

        /*startActivity(new Intent(this, PlaceOrderActivity.class)
        .putExtra(KEY_GENDER,gender));*/

    }
}
