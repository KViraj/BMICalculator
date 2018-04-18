package com.booboo.bmicalculator;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AlertDialogLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ViewActivity extends AppCompatActivity {

    ListView lvDisp;
    FloatingActionButton fabDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        lvDisp = (ListView) findViewById(R.id.lvDisp);
        fabDelete = (FloatingActionButton) findViewById(R.id.fabDelete);
        final DatabaseHandler db = new DatabaseHandler(this);
        this.setTitle("View History");

        final Context context = this;
        final ArrayList<String> recordsBMI =  db.getRecords();
        final ArrayAdapter<String> lvAd = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recordsBMI);
        lvDisp.setAdapter(lvAd);

        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.removeAllRecords();
                lvAd.clear();
                Snackbar.make(view, "All Records Erased", Snackbar.LENGTH_SHORT).show();
            }
        });

        lvDisp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int pos = i;
                Toast.makeText(ViewActivity.this, ""+(recordsBMI.size()-pos-1), Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete Entry?")
                        .setMessage("Are you sure you want to delete the entry?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        db.removeRecord(recordsBMI.size() - pos - 1);
                        recordsBMI.remove(pos);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                lvAd.notifyDataSetChanged();
                            }
                        });

                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(ViewActivity.this, "K Bro!", Toast.LENGTH_SHORT).show();
                    }
                }).show();

            }
        });
    }
}
