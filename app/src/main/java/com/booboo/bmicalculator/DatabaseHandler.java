package com.booboo.bmicalculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by kvira on 15-12-2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    SQLiteDatabase db;
    Context context;

    public DatabaseHandler(Context context) {
        super(context,"bmiRecords.db",null,1);

        this.context = context;
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE bmiRecords (id INTEGER PRIMARY KEY AUTOINCREMENT,date VARCHAR2 NOT NULL, bmi DOUBLE, weight DOUBLE);");
        Log.d("DB_bmiRecords","TABLE CREATED");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS bmiRecords");
        onCreate(sqLiteDatabase);
        Log.d("DB_bmiRecords", "TABLE DROPPED");
    }

    public void addRecord(String date, double BMI, double weight) {
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("bmi",BMI);
        values.put("weight",weight);
        long rid = db.insert("bmiRecords",null, values);

        if (rid<0)  Log.d("DB_bmiRecords","INSERT UNSUCCESSFUL");
        else Log.d("DB_bmiRecords","INSERTED");
    }

    public void removeRecord (int id) {
        long rid = db.delete("bmiRecords","id="+id,null);
        if (rid>0)
            Log.d("DB_bmiRecords","Success");
        else
            Log.d("DB_bmiRecords","fail");
    }

    public ArrayList<String> getRecords () {
        Cursor cursor = db.query("bmiRecords",new String[] {"id","date", "bmi","weight"},
                        null,null,null,null,"id DESC");

        ArrayList<String> recordStr= new ArrayList<>(cursor.getCount());
        int dateCol = cursor.getColumnIndex("date");
        int bmiCol = cursor.getColumnIndex("bmi");
        int wtCol = cursor.getColumnIndex("weight");

        cursor.moveToFirst();

        if (cursor!=null &&(cursor.getCount()> 0)) {
            do {
                String date = cursor.getString(dateCol);
                String bmi = cursor.getString(bmiCol);
                String weight = cursor.getString(wtCol);

                recordStr.add("\nDate: "+date+"\nBMI: "+bmi+"\tWeight: "+weight+"\n");
            }while (cursor.moveToNext());
        }
        else Log.d("DB_bmiRecords","Fetch error");

        cursor.close();
        return recordStr;
    }

    public void removeAllRecords () {
        db.execSQL("DELETE FROM bmiRecords");
    }

}
