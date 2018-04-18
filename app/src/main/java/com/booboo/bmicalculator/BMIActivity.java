package com.booboo.bmicalculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Date;

public class BMIActivity extends AppCompatActivity {

    TextView tvInfo, tvUnder, tvNormal, tvOver, tvObese;
    Button btnSave;
    FloatingActionButton fabShare;
    SharedPreferences sp1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);

        tvInfo = (TextView) findViewById(R.id.tvInfo);
        tvUnder = (TextView) findViewById(R.id.tvUnder);
        tvNormal = (TextView) findViewById(R.id.tvNormal);
        tvOver = (TextView) findViewById(R.id.tvOver);
        tvObese = (TextView) findViewById(R.id.tvObese);
        btnSave = (Button) findViewById(R.id.btnSave);
        fabShare = (FloatingActionButton)findViewById(R.id.fabShare);
        sp1 = getSharedPreferences("Pref", MODE_PRIVATE);

        final DatabaseHandler db = new DatabaseHandler(this);

        this.setTitle("BMI Result");

        Intent i = getIntent();
        final double BMI = i.getDoubleExtra("BMI", 0.0);
        final double weight = i.getDoubleExtra("weight",0.0);

        String disp = "Your BMI is " + BMI + " and you're ";
        final String[] state = {"Underweight", "Normal", "OverWeight", "Obese"};
        final int stateNum;

        if (BMI <= 18.5) {
            stateNum = 0;
            tvUnder.setTextColor(Color.parseColor("#536DFE"));
        } else if (BMI <= 25) {
            stateNum = 1;
            tvNormal.setTextColor(Color.parseColor("#536DFE"));
        } else if (BMI <= 30) {
            stateNum = 2;
            tvOver.setTextColor(Color.parseColor("#536DFE"));
        } else {
            stateNum = 3;
            tvObese.setTextColor(Color.parseColor("#536DFE"));
        }
        disp += state[stateNum];
        tvInfo.setText(disp);

        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = "Name: " + sp1.getString("name", "") + "\nAge: " + sp1.getInt("age", 0) +
                        "\nPhone: " + sp1.getLong("phone", 0) + "\nSex: " + sp1.getString("sex", "") + "\nBMI: " + BMI + "\nYou are " + state[stateNum];
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, msg);
                if (i.resolveActivity(getPackageManager()) != null) startActivity(i);
                else Toast.makeText(BMIActivity.this, "No Proper App Installed", Toast.LENGTH_SHORT).show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date = new Date();
                db.addRecord(date.toString(),BMI,weight);
                Snackbar.make(view,"Record inserted Successfully! ",Snackbar.LENGTH_SHORT).show();
                btnSave.setEnabled(false);
            }
        });
    }
}
