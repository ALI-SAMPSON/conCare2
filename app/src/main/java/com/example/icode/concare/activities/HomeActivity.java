package com.example.icode.concare.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
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

import com.example.icode.concare.R;
import com.example.icode.concare.models.CurrentUsers;
import com.example.icode.concare.models.Orders;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    //objects of the View Classes
    private EditText editTextAddress;
    private EditText editTextTelNumber;

    //spinner for contraceptive type and its arrayAdapter
    private AppCompatSpinner spinnerConType;
    private ArrayAdapter<CharSequence> arrayAdapter;

    private AppCompatSpinner spinnerCampus;
    private ArrayAdapter<CharSequence> arrayAdapter1;

    //objects of the classes
    private CurrentUsers currentUsers;
    private Orders orders;

    private FirebaseDatabase database;
    private DatabaseReference orderRef;
    private DatabaseReference currentUsersRef;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mDrawerLayout = findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        //checks of there is support actionBar
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        editTextAddress = findViewById(R.id.editTextAddress);
        editTextTelNumber = findViewById(R.id.editTextTelNumber);

        //reference to the spinner view and array adapter
        spinnerConType = findViewById(R.id.spinnerConType);
        arrayAdapter = ArrayAdapter.createFromResource(this,R.array.con_type,R.layout.spinner_item);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerConType.setAdapter(arrayAdapter);

        spinnerCampus = findViewById(R.id.spinnerCampus);
        arrayAdapter1 = ArrayAdapter.createFromResource(this,R.array.campus,R.layout.spinner_item);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerCampus.setAdapter(arrayAdapter1);

        //object initialization
        currentUsers = new CurrentUsers();
        orders = new Orders();

        database = FirebaseDatabase.getInstance();
        orderRef = database.getReference().child("Orders");
        currentUsersRef = database.getReference().child("CurrentUsers");

    }

    //placing order button
    public void onPlaceOrderButtonClick(View view) {

        //error text
        String error_text_address = "address is a required field";
        String error_text_number = "telephone number is a required field";

        String address = editTextAddress.getText().toString().trim();
        String number = editTextTelNumber.getText().toString().trim();

        if(address.equals("")){
            editTextAddress.setError(error_text_address);
            Snackbar.make(mDrawerLayout,error_text_address,Snackbar.LENGTH_SHORT).show();
        }
        else if(number.equals("")){
            editTextTelNumber.setError(error_text_number);
            Snackbar.make(mDrawerLayout,error_text_number,Snackbar.LENGTH_SHORT).show();
        }
        else{
            placeOrder();
        }

    }

    //method for handling the placing of order
    public void placeOrder(){

        progressDialog = ProgressDialog.show(this,"",null,true,true);
        progressDialog.setMessage("please wait...");

        String address = editTextAddress.getText().toString().trim();
        String number = editTextTelNumber.getText().toString().trim();
        String campus = spinnerCampus.getSelectedItem().toString().trim();
        String con_type = spinnerConType.getSelectedItem().toString().trim();

        orders.setAddress(address);
        orders.setTelephone_Number(number);
        orders.setCampus(campus);
        orders.setCon_type(con_type);

        orderRef.child(number).setValue(orders).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    final Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            timer.cancel();
                        }
                    },10000);
                    //create a snackbar after a successful login
                    Snackbar.make(mDrawerLayout,
                            "You have successfully made an order.." +
                                    "One of our agents will deliver it to you very soon",
                            Snackbar.LENGTH_LONG).show();
                    //clears the fields after order is placed
                    clearTextFields();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(mDrawerLayout,e.getStackTrace().toString(),Snackbar.LENGTH_LONG).show();
            }
        });

    }

    //clears the textfields
    public void clearTextFields(){
        editTextAddress.setText(null);
        editTextTelNumber.setText(null);
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

}
