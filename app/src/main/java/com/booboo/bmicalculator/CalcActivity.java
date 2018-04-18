package com.booboo.bmicalculator;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class CalcActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    TextView tvMain,tvTemp,tvLocation;
    SharedPreferences sp1;
    Spinner spnFt, spnIn;
    FloatingActionButton fabHistory, fabCalc;
    TextInputEditText tidtWt;
    GoogleApiClient gac;
    int permission;
    Location loc;
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);

        tvMain = (TextView) findViewById(R.id.tvMain);
        spnFt = (Spinner) findViewById(R.id.spnFt);
        spnIn = (Spinner) findViewById(R.id.spnIn);
        fabHistory = (FloatingActionButton) findViewById(R.id.fabHistory);
        fabCalc = (FloatingActionButton) findViewById(R.id.fabCalc);
        tidtWt = (TextInputEditText) findViewById(R.id.tidtWt);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        tvTemp = (TextView)findViewById(R.id.tvTemp);

        sp1 = getSharedPreferences("Pref", MODE_PRIVATE);

        final String name = sp1.getString("name", "");
        if (name.equals("")) tvMain.setText("NONEEE");
        else tvMain.setText("Welcome, " + name);

        final String[] Ft = new String[16];
        for (Integer i = 0; i <= 15; i++) Ft[i] = i.toString();

        ArrayAdapter<String> aFt = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Ft);
        spnFt.setAdapter(aFt);

        String[] In = new String[12];
        for (Integer i = 0; i < 12; i++) In[i] = i.toString();

        ArrayAdapter<String> aIn = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, In);
        spnIn.setAdapter(aIn);

        permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this);
        builder.addApi(LocationServices.API);
        builder.addConnectionCallbacks(this);
        builder.addOnConnectionFailedListener(this);
        gac = builder.build();


        fabCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tidtWt.getText().toString().equals("")) {
                    tidtWt.setError("Enter Weight!");
                    return;
                }
                int feet = spnFt.getSelectedItemPosition();
                int inches = spnIn.getSelectedItemPosition();
                double metres = 2.5 * (feet * 12 + inches) / 100;
                double weight = Integer.parseInt(tidtWt.getText().toString());
                double BMI = Math.round(weight / (metres * metres) * 100) / 100.0;
                Intent i = new Intent(CalcActivity.this, BMIActivity.class);
                i.putExtra("BMI", BMI);
                i.putExtra("weight", weight);
                startActivity(i);
            }
        });

        fabHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CalcActivity.this, ViewActivity.class);
                startActivity(i);
            }
        });

    }

    public void weather () {
        if (location.length() == 0) {
            return;
        }
        Task1 t1 = new Task1();
        t1.execute("http://api.openweathermap.org/data/2.5" + "/weather?units=metric&q=" + location + "&appid=c6e315d09197cec231495138183954bd");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.m1, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.about) {
            Snackbar.make(findViewById(android.R.id.content), "App developed by Viraj Khedekar", Snackbar.LENGTH_LONG).show();
        }
        /*else if(item.getItemId()==android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }*/
        else if (item.getItemId() == R.id.website) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://" + "www.google.co.in"));
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (gac != null) {
            gac.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (gac != null) {
            gac.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (permission == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        }
        if (permission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 69);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 69 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            getLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Suspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Could Not Connect", Toast.LENGTH_SHORT).show();
    }

    public void getLocation() {
        loc = LocationServices.FusedLocationApi.getLastLocation(gac);
        if (loc != null) {
            Geocoder g = new Geocoder(CalcActivity.this, Locale.ENGLISH);
            try {
                List<Address> listt = g.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                android.location.Address addr = listt.get(0);
                location = addr.getLocality();
                String msg = "You're at " + location;
                tvLocation.setText(msg);
                weather();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Check GPS", Toast.LENGTH_SHORT).show();
        }
    }

    class Task1 extends AsyncTask<String, Void, Double> {

        double temp;

        @Override
        protected Double doInBackground(String... params) {
            String line = "", json = "";
            try {
                URL u = new URL(params[0]);
                HttpURLConnection c = (HttpURLConnection) u.openConnection();
                c.connect();

                InputStream is = c.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                while ((line = br.readLine()) != null) {
                    json = json + line + "\n";
                }
                if (json != null) {
                    JSONObject j = new JSONObject(json);
                    JSONObject q = j.getJSONObject("main");
                    temp = q.getDouble("temp");
                }
            } catch (Exception e) {
                Toast.makeText(CalcActivity.this, "" + e, Toast.LENGTH_SHORT).show();
            }
            return temp;

        }

        @Override
        protected void onPostExecute(Double aDouble) {
            super.onPostExecute(aDouble);
            tvTemp.setText("Temperature: " + aDouble +" \u00b0C");
        }

    }
}