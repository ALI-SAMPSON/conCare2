package io.icode.concaregh.app.activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.StrictMode;
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
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import io.icode.concaregh.app.sms.Sender;
import maes.tech.intentanim.CustomIntent;

public class PlaceOrderActivity extends AppCompatActivity {

    // animation class
    Animation shake;

    //objects of the View Classes
    private EditText editTextPhoneNumber;
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

    private ProgressDialog progressDialog;

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

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);*/

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Place Order");
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        shake = AnimationUtils.loadAnimation(PlaceOrderActivity.this, io.icode.concaregh.app.R.anim.anim_shake);

        nestedScrollView = findViewById(io.icode.concaregh.app.R.id.nestedScrollView);

        button_layout = findViewById(io.icode.concaregh.app.R.id.button_layout);

        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextHostelName = findViewById(R.id.editTextHostel);
        editTextRoomNumber = findViewById(R.id.editTextRoomNumber);
        editTextOtherLocation = findViewById(R.id.editTextOtherLocation);
        editTextOtherContraceptive = findViewById(R.id.editTextOtherContraceptive);

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

        progressDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_DARK);
        progressDialog.setTitle("Placing Order");
        progressDialog.setMessage("please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


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
        String phone_number = editTextPhoneNumber.getText().toString().trim();

        // checks if the fields are not empty
        if(phone_number.isEmpty()){
            // starts animation on this view
            editTextPhoneNumber.clearAnimation();
            editTextPhoneNumber.startAnimation(shake);
            editTextPhoneNumber.setError(getString(io.icode.concaregh.app.R.string.error_text_phone_number));
            editTextPhoneNumber.requestFocus();
            return;
        }
        else if(phone_number.length() != 10){
            // starts animation on this view
            editTextPhoneNumber.clearAnimation();
            editTextPhoneNumber.startAnimation(shake);
            editTextPhoneNumber.setError(getString(io.icode.concaregh.app.R.string.phone_invalid));
            editTextPhoneNumber.requestFocus();
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
            String phone_number = editTextPhoneNumber.getText().toString().trim();
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
            orders.setTelephone_Number(phone_number);
            orders.setCampus(campus);
            orders.setLocation(location);
            orders.setOther_location(other_location);
            orders.setResidence(residence);
            orders.setContraceptive(contraceptive);
            orders.setOther_contraceptive(other_contraceptive);

            // displays the dialog
            //progressBar.setVisibility(View.VISIBLE);
            progressDialog.show();

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
                        sendSMSMessageToAdmins();

                        // sends message to user after placing order
                        //sendSMSMessageToUser();

                    } else {
                        // display error message
                        Snackbar.make(nestedScrollView, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                    }

                    // hides the progressDialog if task is successful
                    //progressBar.setVisibility(View.GONE);
                    progressDialog.dismiss();

                }
            });

    }

    //clears the textfields
    public void clearTextFields(){
        editTextPhoneNumber.setText(null);
        editTextHostelName.setText(null);
        editTextRoomNumber.setText(null);
    }

    // Method to send sms to admin
    // after user places an order
    private void sendSMSMessageToAdmins(){

        //gets text or input from the user
        FirebaseUser user = mAuth.getCurrentUser();

        final String user_name = user.getDisplayName();

        //final String user_name = editTextUsername.getText().toString().trim();
        //getting input from the user
        String phone_number = editTextPhoneNumber.getText().toString().trim();
        String campus = spinnerCampus.getSelectedItem().toString().trim();
        String location = spinnerLocation.getSelectedItem().toString().trim();
        String other_location = editTextOtherLocation.getText().toString().trim();
        String residence = spinnerResidence.getSelectedItem().toString().trim();
        String contraceptive = spinnerContraceptive.getSelectedItem().toString().trim();
        String other_contraceptive = editTextOtherContraceptive.getText().toString().trim();
        String hostel_name = editTextHostelName.getText().toString().trim();
        String room_number = editTextRoomNumber.getText().toString().trim();

        String username = "zent-concare";
        // password that is to be used along with username

        String password = "concare1";
        // Message content that is to be transmitted

        String message =  user_name + " has successfully placed an order for " + contraceptive + ".";

        /**
         * What type of the message that is to be sent
         * <ul>
         * <li>0:means plain text</li>
         * <li>1:means flash</li>
         * <li>2:means Unicode (Message content should be in Hex)</li>
         * <li>6:means Unicode Flash (Message content should be in Hex)</li>
         * </ul>
         */
        String type = "0";
        /**
         * Require DLR or not
         * <ul>
         * <li>0:means DLR is not Required</li>
         * <li>1:means DLR is Required</li>
         * </ul>
         */
        String dlr = "1";
        /**
         * Destinations to which message is to be sent For submitting more than one

         * destination at once destinations should be comma separated Like
         * 91999000123,91999000124
         */
        String destination = "233245134112,233501360324,233249959061";

        // Sender Id to be used for submitting the message
        String source = "ConCare GH";

        // To what server you need to connect to for submission
        final String server = "rslr.connectbind.com";

        // Port that is to be used like 8080 or 8000
        int port = 2345;

        try {
            // Url that will be called to submit the message
            URL sendUrl = new URL("http://" + server + ":" + "/bulksms/bulksms?");
            HttpURLConnection httpConnection = (HttpURLConnection) sendUrl
                    .openConnection();
            // This method sets the method type to POST so that
            // will be send as a POST request
            httpConnection.setRequestMethod("POST");
            // This method is set as true wince we intend to send
            // input to the server
            httpConnection.setDoInput(true);
            // This method implies that we intend to receive data from server.
            httpConnection.setDoOutput(true);
            // Implies do not use cached data
            httpConnection.setUseCaches(false);
            // Data that will be sent over the stream to the server.
            DataOutputStream dataStreamToServer = new DataOutputStream( httpConnection.getOutputStream());
            dataStreamToServer.writeBytes("username="
                    + URLEncoder.encode(username, "UTF-8") + "&password="
                    + URLEncoder.encode(password, "UTF-8") + "&type="
                    + URLEncoder.encode(type, "UTF-8") + "&dlr="
                    + URLEncoder.encode(dlr, "UTF-8") + "&destination="
                    + URLEncoder.encode(destination, "UTF-8") + "&source="
                    + URLEncoder.encode(source, "UTF-8") + "&message="
                    + URLEncoder.encode(message, "UTF-8"));
            dataStreamToServer.flush();
            dataStreamToServer.close();
            // Here take the output value of the server.
            BufferedReader dataStreamFromUrl = new BufferedReader( new InputStreamReader(httpConnection.getInputStream()));
            String dataFromUrl = "", dataBuffer = "";
            // Writing information from the stream to the buffer
            while ((dataBuffer = dataStreamFromUrl.readLine()) != null) {
                dataFromUrl += dataBuffer;
            }
            /**
             * Now dataFromUrl variable contains the Response received from the
             * server so we can parse the response and process it accordingly.
             */
            dataStreamFromUrl.close();
            System.out.println("Response: " + dataFromUrl);
            //Toast.makeText(context, dataFromUrl, Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex) {
            // catches any error that occurs and outputs to the user
            Toast.makeText(PlaceOrderActivity.this,ex.getMessage(),Toast.LENGTH_LONG).show();
        }

    }


    // Method to send sms to admin
    // after user places an order
    private void sendSMSMessageToUser(){

        //gets text or input from the user
        FirebaseUser user = mAuth.getCurrentUser();

        final String user_name = user.getDisplayName();

        //final String user_name = editTextUsername.getText().toString().trim();
        //getting input from the user

        String campus = spinnerCampus.getSelectedItem().toString().trim();
        String location = spinnerLocation.getSelectedItem().toString().trim();
        String other_location = editTextOtherLocation.getText().toString().trim();
        String residence = spinnerResidence.getSelectedItem().toString().trim();
        String contraceptive = spinnerContraceptive.getSelectedItem().toString().trim();
        String other_contraceptive = editTextOtherContraceptive.getText().toString().trim();
        String hostel_name = editTextHostelName.getText().toString().trim();
        String room_number = editTextRoomNumber.getText().toString().trim();

        String username = "zent-concare";
        // password that is to be used along with username

        String password = "concare1";
        // Message content that is to be transmitted

        String message = " Your order has been sent successfully. ";

        /**
         * What type of the message that is to be sent
         * <ul>
         * <li>0:means plain text</li>
         * <li>1:means flash</li>
         * <li>2:means Unicode (Message content should be in Hex)</li>
         * <li>6:means Unicode Flash (Message content should be in Hex)</li>
         * </ul>
         */
        String type = "0";
        /**
         * Require DLR or not
         * <ul>
         * <li>0:means DLR is not Required</li>
         * <li>1:means DLR is Required</li>
         * </ul>
         */
        String dlr = "1";
        /**
         * Destinations to which message is to be sent For submitting more than one

         * destination at once destinations should be comma separated Like
         * 91999000123,91999000124
         */

        // getting mobile number from EditText
        String destination = editTextPhoneNumber.getText().toString().trim();

        // Sender Id to be used for submitting the message
        String source = "ConCare GH";

        // To what server you need to connect to for submission
        final String server = "rslr.connectbind.com";

        // Port that is to be used like 8080 or 8000
        int port = 2345;

        try {
            // Url that will be called to submit the message
            URL sendUrl = new URL("http://" + server + ":" + "/bulksms/bulksms?");
            HttpURLConnection httpConnection = (HttpURLConnection) sendUrl
                    .openConnection();
            // This method sets the method type to POST so that
            // will be send as a POST request
            httpConnection.setRequestMethod("POST");
            // This method is set as true wince we intend to send
            // input to the server
            httpConnection.setDoInput(true);
            // This method implies that we intend to receive data from server.
            httpConnection.setDoOutput(true);
            // Implies do not use cached data
            httpConnection.setUseCaches(false);
            // Data that will be sent over the stream to the server.
            DataOutputStream dataStreamToServer = new DataOutputStream( httpConnection.getOutputStream());
            dataStreamToServer.writeBytes("username="
                    + URLEncoder.encode(username, "UTF-8") + "&password="
                    + URLEncoder.encode(password, "UTF-8") + "&type="
                    + URLEncoder.encode(type, "UTF-8") + "&dlr="
                    + URLEncoder.encode(dlr, "UTF-8") + "&destination="
                    + URLEncoder.encode(destination, "UTF-8") + "&source="
                    + URLEncoder.encode(source, "UTF-8") + "&message="
                    + URLEncoder.encode(message, "UTF-8"));
            dataStreamToServer.flush();
            dataStreamToServer.close();
            // Here take the output value of the server.
            BufferedReader dataStreamFromUrl = new BufferedReader( new InputStreamReader(httpConnection.getInputStream()));
            String dataFromUrl = "", dataBuffer = "";
            // Writing information from the stream to the buffer
            while ((dataBuffer = dataStreamFromUrl.readLine()) != null) {
                dataFromUrl += dataBuffer;
            }
            /**
             * Now dataFromUrl variable contains the Response received from the
             * server so we can parse the response and process it accordingly.
             */
            dataStreamFromUrl.close();
            System.out.println("Response: " + dataFromUrl);
            //Toast.makeText(context, dataFromUrl, Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex) {
            // catches any error that occurs and outputs to the user
            Toast.makeText(PlaceOrderActivity.this,ex.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    // Method to call the Sender class using TextLocal SMS API
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
            String mobile_number = "+233245134112, +233501360324";

            String _apiKey = "tt8BP4xYcFY-1rUOdyp6AqavwmPHrJXZWk2dg4HcNR";

            FirebaseUser user = mAuth.getCurrentUser();

            String user_name = user.getDisplayName();

            // variable to hold the message to send
            String message_to_send =  user_name + " has successfully placed an order for " +
                    contraceptive + ".";

            //String message_to_send = "Ali has successfully placed an order";

            //String key = "3GSbLQ+Jhu4-6PtTkPLyhaLYGgJl7HpfUvuJALKCWB";

                    // Construct data
            // apiKey is fix or constant
            String apiKey = "apikey=" + _apiKey;
            // message always changes
            String message = "&message=" + message_to_send;
            // for free demo u can't change the sender id name
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
                Toast.makeText(PlaceOrderActivity.this, line ,Toast.LENGTH_LONG).show();
            }
            rd.close();

        } catch (Exception e) {
            Toast.makeText(PlaceOrderActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
            //return "Error "+e;
        }

    }
    */

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
       switch (item.getItemId()){
           case android.R.id.home:

               // finishes this activity
               finish();

               //send user back to home activity
               startActivity(new Intent(this, HomeActivity.class));

               // Add a custom animation ot the activity
               CustomIntent.customType(PlaceOrderActivity.this,"fadein-to-fadeout");

               break;
               default:
                   break;
       }
        return super.onOptionsItemSelected(item);
    }

    public void onClearButtonClick(View view) {
        editTextPhoneNumber.setText(null);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // finishes the activity
        finish();

        // starts the Home Activity
        startActivity(new Intent(PlaceOrderActivity.this,HomeActivity.class));

        // Add a custom animation ot the activity
        CustomIntent.customType(PlaceOrderActivity.this,"fadein-to-fadeout");
    }
}
