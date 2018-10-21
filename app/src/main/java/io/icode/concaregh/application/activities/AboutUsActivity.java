package io.icode.concaregh.application.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import io.icode.concaregh.application.R;

import maes.tech.intentanim.CustomIntent;

public class AboutUsActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView overview_CardView;
    private CardView mission_CardView;
    private CardView vision_CardView;

    //text to populate the overview, mission and vision of the company
    private TextView sub_Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("About Us");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        overview_CardView = findViewById(io.icode.concaregh.application.R.id.cardViewOverview);
        mission_CardView = findViewById(io.icode.concaregh.application.R.id.cardViewMission);
        vision_CardView = findViewById(io.icode.concaregh.application.R.id.cardViewVision);

        sub_Text = findViewById(io.icode.concaregh.application.R.id.sub_text_1);

        overview_CardView.setOnClickListener(this);
        mission_CardView.setOnClickListener(this);
        vision_CardView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        //sub text for the various buttons
        String text_overview = "    Concare GH seeks to remove shyness, \n" +
                "        which is evident of their (victims')\n" +
                "        reluctance to purchase contraceptives;\n" +
                "        with our business system and strategy,\n" +
                "        there is relatively much privacy.";

        String text_mission = " Providing a more conducive environment\n" +
                "        for our clients in purchasing contraceptives.";

        String text_vision = " To be the best and leading contraceptive health\n" +
                "        provider in Ghana and the world at large, and to\n" +
                "        also ensure mass participation in contraceptive use.";

        switch (view.getId()){
            case R.id.cardViewOverview:

                // Adds animation to the cardView
                YoYo.with(Techniques.ZoomInDown).playOn(overview_CardView);

                // sets text to this TextView
                sub_Text.setText(text_overview);

                break;
            case R.id.cardViewMission:
                // Adds animation to the cardView
                YoYo.with(Techniques.ZoomInUp).playOn(mission_CardView);

                // sets text to this TextView
                sub_Text.setText(text_mission);

                break;
            case R.id.cardViewVision:
                // Adds animation to the cardView
                YoYo.with(Techniques.ZoomInDown).playOn(vision_CardView);

                // sets text to this TextView
                sub_Text.setText(text_vision);

                break;
                default:
                    break;
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_user,menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                //starts the home activity
                startActivity(new Intent(this,HomeActivity.class));
                // Add a custom animation ot the activity
                CustomIntent.customType(AboutUsActivity.this,"fadein-to-fadeout");
                // finishes the activity
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
        CustomIntent.customType(AboutUsActivity.this,"fadein-to-fadeout");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // open the LoginActivity
        startActivity(new Intent(AboutUsActivity.this,HomeActivity.class));

        // Add a custom animation ot the activity
        CustomIntent.customType(AboutUsActivity.this,"fadein-to-fadeout");

        // finishes the activity
        finish();

    }
}
