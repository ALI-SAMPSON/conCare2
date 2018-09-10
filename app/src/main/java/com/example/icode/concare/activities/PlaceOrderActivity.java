package com.example.icode.concare.activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.icode.concare.R;
import com.example.icode.concare.models.Orders;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PlaceOrderActivity extends AppCompatActivity {

    //objects of the View Classes
    private EditText editTextTelNumber;
    private EditText editTextHostelName;
    private EditText editTextRoomNumber;

    private EditText editTextOtherLocation;
    private EditText editTextOtherContraceptive;

    //user's campus spinner view object
    private AppCompatSpinner spinnerCampus;
    private ArrayAdapter<CharSequence> arrayAdapterCampus;

    //user's location spinner view object
    private AppCompatSpinner spinnerLocation;
    private ArrayAdapter<CharSequence> arrayAdapterLocation;

    //user's residence spinner view object
    private AppCompatSpinner spinnerResidence;
    private ArrayAdapter<CharSequence> arrayAdapterResidence;

    //spinner for contraceptive type and its arrayAdapter
    private AppCompatSpinner spinnerContraceptive;
    private ArrayAdapter<CharSequence> arrayAdapterContraceptive;


    //objects of the classes
    private Orders orders;

    private FirebaseDatabase database;
    private DatabaseReference orderRef;

    private ProgressBar progressBar;

    private NestedScrollView nestedScrollView;

    FirebaseAuth mAuth;

    //textInput layouts
    private TextInputLayout textInputLayoutOtherLocation;
    private TextInputLayout textInputLayoutOtherContraceptive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Place Order");
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        nestedScrollView = findViewById(R.id.nestedScrollView);

        editTextTelNumber = findViewById(R.id.editTextTelNumber);
        editTextHostelName = findViewById(R.id.editTextHostel);
        editTextRoomNumber = findViewById(R.id.editTextRoomNumber);
        editTextOtherLocation = findViewById(R.id.editTextOtherLocation);
        editTextOtherContraceptive = findViewById(R.id.editTextOtherContraceptive);

        //spinner view campus
        spinnerCampus = findViewById(R.id.spinnerCampus);
        arrayAdapterCampus = ArrayAdapter.createFromResource(this,R.array.campus,R.layout.spinner_item);
        arrayAdapterCampus.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerCampus.setAdapter(arrayAdapterCampus);

        spinnerLocation = findViewById(R.id.spinnerLocation);
        arrayAdapterLocation = ArrayAdapter.createFromResource(this, R.array.location,R.layout.spinner_item);
        arrayAdapterLocation.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerLocation.setAdapter(arrayAdapterLocation);

        spinnerResidence = findViewById(R.id.spinnerResidence);
        arrayAdapterResidence = ArrayAdapter.createFromResource(this,R.array.residence,R.layout.spinner_item);
        arrayAdapterResidence.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerResidence.setAdapter(arrayAdapterResidence);

        //reference to the spinner view and array adapter
        spinnerContraceptive = findViewById(R.id.spinnerContraceptive);

        String gender = getIntent().getExtras().get("gender").toString().trim();

        if(gender.equals("MALE")){
            arrayAdapterContraceptive = ArrayAdapter.createFromResource(this,R.array.con_male,R.layout.spinner_item);
            arrayAdapterContraceptive.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinnerContraceptive.setAdapter(arrayAdapterContraceptive);
        }
        else if(gender.equals("FEMALE")){
            arrayAdapterContraceptive = ArrayAdapter.createFromResource(this,R.array.con_female,R.layout.spinner_item);
            arrayAdapterContraceptive.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinnerContraceptive.setAdapter(arrayAdapterContraceptive);
        }
        else{
            //do nothing
        }

        if(spinnerLocation.getSelectedItem().toString().equals("Other")){
            textInputLayoutOtherLocation = findViewById(R.id.textInputLayoutOtherLocation);
            textInputLayoutOtherLocation.setVisibility(View.VISIBLE);
        }
        /*else if(contraceptive.equals("Other")){
            textInputLayoutOtherContraceptive = findViewById(R.id.textInputLayoutOtherContraceptive);
            textInputLayoutOtherContraceptive.setVisibility(View.VISIBLE);
        }*/
        else{
            //do nothing
        }


        //object initialization
        orders = new Orders();

        //database = FirebaseDatabase.getInstance();
        //orderRef = database.getReference().child("Orders");

        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressBar);

    }

    @Override
    protected void onStart(){
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            // do nothing
        }
    }

    // on click listener for placing order
    public void onPlaceOrderButtonClick(View view) {

        //getting input from the fields
        String hostel_name = editTextHostelName.getText().toString().trim();
        String room_number = editTextRoomNumber.getText().toString().trim();
        String tel_number = editTextTelNumber.getText().toString().trim();

        // checks if the fields are not empty
        if(tel_number.isEmpty()){
            editTextTelNumber.setError(getString(R.string.error_text_phone_number));
            editTextTelNumber.requestFocus();
            //Snackbar.make(nestedScrollView,error_text_number,Snackbar.LENGTH_SHORT).show();
            return;
        }
        else if(tel_number.length() != 10){
            editTextTelNumber.setError(getString(R.string.phone_invalid));
            editTextTelNumber.requestFocus();
            return;
        }
        else if(room_number.isEmpty()){
            editTextRoomNumber.setError(getString(R.string.error_text_room_number));
            editTextRoomNumber.requestFocus();
            return;
        }
        else if(hostel_name.isEmpty()){
            editTextHostelName.setError(getString(R.string.error_text_hostel));
            editTextHostelName.requestFocus();
            //Snackbar.make(nestedScrollView,error_text_hostel,Snackbar.LENGTH_SHORT).show();
            return;
        }
        else{
            placeOrder();
        }
    }

    //method for handling the placing of order
    public void placeOrder(){

            // creates and initialize a progressDialog
           // progressDialog = ProgressDialog.show(PlaceOrderActivity.this,"Processing",null,true,true);
           // progressDialog.setMessage("Please wait...");

            //getting input from the user
            String tel_number = editTextTelNumber.getText().toString().trim();
            String campus = spinnerCampus.getSelectedItem().toString().trim();
            String location = spinnerLocation.getSelectedItem().toString().trim();
            String other_location = editTextOtherLocation.getText().toString().trim();
            String residence = spinnerResidence.getSelectedItem().toString().trim();
            String contraceptive = spinnerContraceptive.getSelectedItem().toString().trim();
            String other_contraceptive = editTextOtherContraceptive.getText().toString().trim();
            String hostel_name = editTextHostelName.getText().toString().trim();
            String room_number = editTextRoomNumber.getText().toString().trim();

            // setting values to setter methods
            orders.setHostel_name(hostel_name);
            orders.setRoom_number(room_number);
            orders.setTelephone_Number(tel_number);
            orders.setCampus(campus);
            orders.setLocation(location);
            orders.setOther_location(other_location);
            orders.setResidence(residence);
            orders.setContraceptive(contraceptive);
            orders.setOther_contraceptive(other_contraceptive);

            progressBar.setVisibility(View.VISIBLE);

            FirebaseDatabase.getInstance().getReference("Orders")
                    .child(mAuth.getCurrentUser().getDisplayName())
                    .setValue(orders).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        // hides the progressDialog if task is successful
                        progressBar.setVisibility(View.GONE);
                        // display a snackbar after a successful order
                        Snackbar.make(nestedScrollView,
                                "You have successfully made an order.." +
                                        "One of our agents will deliver it " +
                                        "to you very soon",
                                Snackbar.LENGTH_LONG).show();
                        //clears the fields after order is placed
                        clearTextFields();

                        //sends a notification to the user of placing order successfully
                        Intent intent = new Intent(PlaceOrderActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        PendingIntent pendingIntent = PendingIntent.getActivity(PlaceOrderActivity.this, 0, intent, 0);
                        Notification notification = new Notification.Builder(PlaceOrderActivity.this)
                                .setSmallIcon(R.mipmap.app_icon_round)
                                .setContentTitle(getString(R.string.app_name))
                                .setContentText(" You have successfully made an order for "  +
                                        orders.getContraceptive() + "." +
                                        " One of our agents will deliver it " +
                                        " to you very soon ")
                                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                                .setContentIntent(pendingIntent).getNotification();
                        notification.flags = Notification.FLAG_AUTO_CANCEL;
                        NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
                        nm.notify(0, notification);

                    } else {
                        // display error message
                        Snackbar.make(nestedScrollView, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                    }

                }
            });

    }

    //clears the textfields
    public void clearTextFields(){
        editTextTelNumber.setText(null);
        editTextHostelName.setText(null);
        editTextRoomNumber.setText(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
       switch (item.getItemId()){
           case android.R.id.home:
               //send user back to home activity
               startActivity(new Intent(this, HomeActivity.class));
               break;
               default:
                   break;
       }
        return super.onOptionsItemSelected(item);
    }

}
