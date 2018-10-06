package io.icode.concaregh.app.activities;

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
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import io.icode.concaregh.app.R;
import io.icode.concaregh.app.models.Orders;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import io.icode.concaregh.app.sms.Sender;
import maes.tech.intentanim.CustomIntent;

public class PlaceOrderActivity extends AppCompatActivity {

    // animation class
    Animation shake;

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

    private AppCompatButton make_payment;
    private AppCompatButton cancel_payment;

    //objects of the classes
    private Orders orders;

    private FirebaseDatabase database;
    private DatabaseReference orderRef;

    private ProgressBar progressBar;

    private NestedScrollView nestedScrollView;

    private LinearLayout button_layout;

    FirebaseAuth mAuth;

    //textInput layouts
    private TextInputLayout textInputLayoutOtherLocation;
    private TextInputLayout textInputLayoutOtherContraceptive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(io.icode.concaregh.app.R.layout.activity_place_order);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Place Order");
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        shake = AnimationUtils.loadAnimation(PlaceOrderActivity.this, io.icode.concaregh.app.R.anim.anim_shake);

        nestedScrollView = findViewById(io.icode.concaregh.app.R.id.nestedScrollView);

        button_layout = findViewById(io.icode.concaregh.app.R.id.button_layout);

        editTextTelNumber = findViewById(io.icode.concaregh.app.R.id.editTextTelNumber);
        editTextHostelName = findViewById(io.icode.concaregh.app.R.id.editTextHostel);
        editTextRoomNumber = findViewById(io.icode.concaregh.app.R.id.editTextRoomNumber);
        editTextOtherLocation = findViewById(io.icode.concaregh.app.R.id.editTextOtherLocation);
        editTextOtherContraceptive = findViewById(io.icode.concaregh.app.R.id.editTextOtherContraceptive);

        //spinner view campus
        spinnerCampus = findViewById(io.icode.concaregh.app.R.id.spinnerCampus);
        arrayAdapterCampus = ArrayAdapter.createFromResource(this, io.icode.concaregh.app.R.array.campus, io.icode.concaregh.app.R.layout.spinner_item);
        arrayAdapterCampus.setDropDownViewResource(io.icode.concaregh.app.R.layout.spinner_dropdown_item);
        spinnerCampus.setAdapter(arrayAdapterCampus);

        spinnerLocation = findViewById(io.icode.concaregh.app.R.id.spinnerLocation);
        arrayAdapterLocation = ArrayAdapter.createFromResource(this, io.icode.concaregh.app.R.array.location, io.icode.concaregh.app.R.layout.spinner_item);
        arrayAdapterLocation.setDropDownViewResource(io.icode.concaregh.app.R.layout.spinner_dropdown_item);
        spinnerLocation.setAdapter(arrayAdapterLocation);

        spinnerResidence = findViewById(io.icode.concaregh.app.R.id.spinnerResidence);
        arrayAdapterResidence = ArrayAdapter.createFromResource(this, io.icode.concaregh.app.R.array.residence, io.icode.concaregh.app.R.layout.spinner_item);
        arrayAdapterResidence.setDropDownViewResource(io.icode.concaregh.app.R.layout.spinner_dropdown_item);
        spinnerResidence.setAdapter(arrayAdapterResidence);

        //reference to the spinner view and array adapter
        spinnerContraceptive = findViewById(io.icode.concaregh.app.R.id.spinnerContraceptive);

        String gender = getIntent().getExtras().get("gender").toString().trim();

        if(gender.equals("MALE")){
            arrayAdapterContraceptive = ArrayAdapter.createFromResource(this, io.icode.concaregh.app.R.array.con_male, io.icode.concaregh.app.R.layout.spinner_item);
            arrayAdapterContraceptive.setDropDownViewResource(io.icode.concaregh.app.R.layout.spinner_dropdown_item);
            spinnerContraceptive.setAdapter(arrayAdapterContraceptive);
        }
        else if(gender.equals("FEMALE")){
            arrayAdapterContraceptive = ArrayAdapter.createFromResource(this, io.icode.concaregh.app.R.array.con_female, io.icode.concaregh.app.R.layout.spinner_item);
            arrayAdapterContraceptive.setDropDownViewResource(io.icode.concaregh.app.R.layout.spinner_dropdown_item);
            spinnerContraceptive.setAdapter(arrayAdapterContraceptive);
        }
        else{
            //do nothing
        }

        if(spinnerLocation.getSelectedItem().toString().equals("Other")){
            textInputLayoutOtherLocation = findViewById(io.icode.concaregh.app.R.id.textInputLayoutOtherLocation);
            textInputLayoutOtherLocation.setVisibility(View.VISIBLE);
        }
        /*else if(contraceptive.equals("Other")){
            textInputLayoutOtherContraceptive = findViewById(R.id.textInputLayoutOtherContraceptive);
            textInputLayoutOtherContraceptive.setVisibility(View.VISIBLE);
        }*/
        else{
            //do nothing
        }

        // getting view to the buttons
        make_payment = findViewById(io.icode.concaregh.app.R.id.appCompatButtonPayment);
        cancel_payment = findViewById(io.icode.concaregh.app.R.id.appCompatButtonCancel);

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

    // Adds a sliding animation to the buttons
    private void slide_button(){
        Animation slide = AnimationUtils.loadAnimation(this,android.R.anim.bounce_interpolator);
        button_layout.clearAnimation();
        button_layout.startAnimation(slide);
    }

    // on click listener for placing order
    public void onPlaceOrderButtonClick(View view) {

        //getting input from the fields
        String hostel_name = editTextHostelName.getText().toString().trim();
        String room_number = editTextRoomNumber.getText().toString().trim();
        String tel_number = editTextTelNumber.getText().toString().trim();

        // checks if the fields are not empty
        if(tel_number.isEmpty()){
            // starts animation on this view
            editTextTelNumber.clearAnimation();
            editTextTelNumber.startAnimation(shake);
            editTextTelNumber.setError(getString(io.icode.concaregh.app.R.string.error_text_phone_number));
            editTextTelNumber.requestFocus();
            return;
        }
        else if(tel_number.length() != 10){
            // starts animation on this view
            editTextTelNumber.clearAnimation();
            editTextTelNumber.startAnimation(shake);
            editTextTelNumber.setError(getString(io.icode.concaregh.app.R.string.phone_invalid));
            editTextTelNumber.requestFocus();
            return;
        }
        else if(room_number.isEmpty()){
            // starts animation on this view
            editTextRoomNumber.clearAnimation();
            editTextRoomNumber.startAnimation(shake);
            editTextRoomNumber.setError(getString(io.icode.concaregh.app.R.string.error_text_room_number));
            editTextRoomNumber.requestFocus();
            return;
        }
        else if(hostel_name.isEmpty()){
            // starts animation on this view
            editTextHostelName.clearAnimation();
            editTextHostelName.startAnimation(shake);
            editTextHostelName.setError(getString(io.icode.concaregh.app.R.string.error_text_hostel));
            editTextHostelName.requestFocus();
            return;
        }
        else{
            placeOrder();
        }
    }

    //method for handling the placing of order
    public void placeOrder(){

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
                    .child(mAuth.getCurrentUser().getUid())
                    .setValue(orders).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){

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
                                .setSmallIcon(R.mipmap.app_logo_round)
                                .setContentTitle(getString(R.string.app_name))
                                .setContentText(" You have successfully made an order for "  +
                                        orders.getContraceptive() + "." +
                                        " One of our agents will deliver it " +
                                        " to you very soon. Thank you... ")
                                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                                .setContentIntent(pendingIntent).getNotification();
                        notification.flags = Notification.FLAG_AUTO_CANCEL;
                        NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
                        nm.notify(0, notification);

                        // Method call to sendSMS
                        // to company phone number
                        // after users successfully
                        // place order
                        sendSMSMessage();

                    } else {
                        // display error message
                        Snackbar.make(nestedScrollView, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                    }

                    // hides the progressDialog if task is successful
                    progressBar.setVisibility(View.GONE);

                    // Method call to sendSMS to phone number
                    sendSMSMessage();
                }
            });

    }

    //clears the textfields
    public void clearTextFields(){
        editTextTelNumber.setText(null);
        editTextHostelName.setText(null);
        editTextRoomNumber.setText(null);
    }

    // Method to call the Sender class using Zentech SMS
    /*private void sendSMSMessage(){

        try {

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

            // receivers mobile number
            String mobile_number = "0245134112";

            FirebaseUser user = mAuth.getCurrentUser();

            String user_name = user.getDisplayName();

            // variable to hold the message to send
            String message =  user_name + " has successfully placed an order for " +
                    contraceptive + "/" + other_contraceptive + "." +
                    " Mobile Number " + tel_number + "," +
                    " location " + location + "," +
                    " Other Location " + other_location + "," +
                    " Residence " + residence + "," +
                    " Hostel name " + hostel_name + " and " +
                    " Room Number " + room_number;

            // Below example is for sending Plain text
            Sender s = new Sender("rslr.connectbind.com",
                    2345, "tester909",
                    "test11",
                    message,
                    "1",
                    "0",
                    mobile_number,
                    getString(R.string.app_name));

            // submit message using an object of the Sender Class for Unicode Message
            s.submitMessage();

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

    }*/

    // Method to call the Sender class using TextLocal SMS API
    private void sendSMSMessage(){

        try {

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

            // receivers mobile number
            String mobile_number = "+233245134112";

            String _apiKey = "tt8BP4xYcFY-373uinGmMK5fJF8YvdkrHRAIaU1jXk";

            FirebaseUser user = mAuth.getCurrentUser();

            String user_name = user.getDisplayName();

            // variable to hold the message to send
            String message_to_send =  user_name + " has successfully placed an order for " +
                    contraceptive + "/" + other_contraceptive + "." +
                    " Mobile Number " + tel_number + "," +
                    " location " + location + "," +
                    " Other Location " + other_location + "," +
                    " Residence " + residence + "," +
                    " Hostel name " + hostel_name + " and " +
                    " Room Number " + room_number;

            // Construct data
            String apiKey = "apikey=" + _apiKey;
            String message = "&message=" + message_to_send;
            String sender = "&sender=" + getString(R.string.app_name);
            String numbers = "&numbers=" + mobile_number;

            // Send data
            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.txtlocal.com/send/?").openConnection();
            String data = apiKey + numbers + message + sender;
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
            conn.getOutputStream().write(data.getBytes("UTF-8"));
            final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                stringBuffer.append(line);
                Toast.makeText(PlaceOrderActivity.this, "message" + line ,Toast.LENGTH_SHORT).show();
            }
            rd.close();

            //return stringBuffer.toString();

        } catch (Exception e) {
            System.out.println("Error SMS "+e);
            // display error message
            Snackbar.make(nestedScrollView,e.getMessage(),Snackbar.LENGTH_SHORT).show();
            //return "Error "+e;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
       switch (item.getItemId()){
           case android.R.id.home:
               //send user back to home activity
               startActivity(new Intent(this, HomeActivity.class));
               // finishes this activity
               finish();
               // Add a custom animation ot the activity
               CustomIntent.customType(PlaceOrderActivity.this,"fadein-to-fadeout");
               break;
               default:
                   break;
       }
        return super.onOptionsItemSelected(item);
    }

    public void onClearButtonClick(View view) {
        editTextTelNumber.setText(null);
        editTextOtherLocation.setText(null);
        editTextOtherContraceptive.setText(null);
        editTextHostelName.setText(null);
        editTextRoomNumber.setText(null);
    }

    @Override
    public void finish() {
        super.finish();
        // Add a custom animation ot the activity
        CustomIntent.customType(PlaceOrderActivity.this,"fadein-to-fadeout");
    }
}
