package com.booboo.bmicalculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {

    EditText etName, etAge, etPhn;
    Button btnRegister;
    RadioGroup rgSex;
    SharedPreferences sp1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = (EditText) findViewById(R.id.etName);
        etAge = (EditText) findViewById(R.id.etAge);
        etPhn = (EditText) findViewById(R.id.etPhn);
        rgSex = (RadioGroup) findViewById(R.id.rgSex);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        this.setTitle("Details");

        sp1 = getSharedPreferences("Pref", MODE_PRIVATE);

        String name = sp1.getString("name", "");

        if (!name.equals("")) {
            Intent i = new Intent(MainActivity.this, CalcActivity.class);
            startActivity(i);
            finish();
        } else {
            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = etName.getText().toString();
                    String age = etAge.getText().toString();
                    String phone = etPhn.getText().toString();

                    if (name.length() == 0) {
                        Snackbar.make(view, "Enter Name Please!", Snackbar.LENGTH_SHORT).show();
                        etName.requestFocus();
                    } else if (age.length() == 0) {
                        Snackbar.make(view, "Enter Age Please!", Snackbar.LENGTH_SHORT).show();
                        etAge.requestFocus();
                    } else if (phone.length() != 10) {
                        Snackbar.make(view, "Enter Phone Please!", Snackbar.LENGTH_SHORT).show();
                        etPhn.requestFocus();
                    } else {
                        SharedPreferences.Editor editor = sp1.edit();
                        editor.putString("name", name);
                        editor.putInt("age", Integer.parseInt(age));
                        editor.putLong("phone", Long.parseLong(phone));
                        RadioButton rbSel = (RadioButton) findViewById(rgSex.getCheckedRadioButtonId());
                        editor.putString("sex", rbSel.getText().toString());
                        editor.commit();
                        Intent i = new Intent(MainActivity.this, CalcActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            });

        }
    }
}
