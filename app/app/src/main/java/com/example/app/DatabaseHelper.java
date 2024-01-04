package com.example.app;

import static com.example.app.SecurityUtils.hashPassword;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.time.LocalDate;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String Create_User = "create table User (" +
            "name text primary key UNIQUE," +   //Each User name could only appear once
            "password text,"+
            "birthDate text," +
            "gender text," +
            "weight double," +
            "height double)";
    private static final String CREATE_DAILY_DATA = "CREATE TABLE DailyData (" +
            "username TEXT," +
            "date TEXT," +
            "steps INTEGER," +
            "height DOUBLE," +
            "weight DOUBLE," +
            "PRIMARY KEY (username, date),"+
            "FOREIGN KEY (username) REFERENCES User(name))";
    private Context mContext;
    private static final String DATABASE_NAME = "my_database.db";
    private static final int DATABASE_VERSION = 1;
    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    //Creating a database
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Create_User);
        sqLiteDatabase.execSQL(CREATE_DAILY_DATA);
        Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_SHORT).show();    //Good for testing, you can see if its created
    }

    //Adding User to the User table
    public void addUser(String name, LocalDate birthDate, String gender, double weight, double height, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name",name);
        values.put("birthDate", birthDate.toString());
        values.put("gender", gender);
        values.put("weight", weight);
        values.put("height", height);
        values.put("password", password);
        db.insert("User", null, values);
        db.close();
    }

    //Adding daily data to the daily data table
    public void addDailyData(String username, String date, int steps, double height, double weight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("date", date);
        values.put("steps", steps);
        values.put("height", height);
        values.put("weight", weight);
        db.insert("DailyData", null, values);
        db.close();
    }

    public void deleteUser(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("User", "name=?", new String[]{name});
        db.close();
    }

    public void deleteDailyData(String username, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("DailyData", "username=? AND date=?", new String[]{username, date});
        db.close();
    }

    //Get User by name
    public Cursor getUser(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("User", new String[] {"name", "birthDate", "gender", "weight", "height"}, "name=?", new String[]{name}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        return cursor;
    }

    public Cursor getDailyData(String username, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query("DailyData", null, "username=? AND date=?", new String[]{username, date}, null, null, null);
    }

    public String getPassword(String name){
        SQLiteDatabase db = this.getReadableDatabase();
        String hashedPassword = null;

        Cursor cursor= db.query("User", new String[]{"password"}, "name=?",new String[]{name}, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int passwordIndex = cursor.getColumnIndex("password");
                if (passwordIndex != -1) {
                    hashedPassword = cursor.getString(passwordIndex);
                } else {
                    // Handle the error - the column "password" does not exist.
                }
            }
            cursor.close();
        }

        return hashedPassword;
    }

    public boolean checkUserExists(String userName){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("User", new String[]{"name"}, "name=?", new String[]{userName}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    @SuppressLint("NewApi")
    public User getUserByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("User", null, "name=?", new String[]{name}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") User user = new User(
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("gender")),
                    LocalDate.parse(cursor.getString(cursor.getColumnIndex("birthDate"))),
                    cursor.getDouble(cursor.getColumnIndex("weight")),
                    cursor.getDouble(cursor.getColumnIndex("height"))
            );
            cursor.close();
            return user;
        }

        if (cursor != null) {
            cursor.close();
        }

        return null;
    }

    @SuppressLint("NewApi")
    public Object[] getDailyDataByNameDate(String name, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("DailyData", null, "username=? AND date=?", new String[]{name, date}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") Object[] obj = {
                    cursor.getString(cursor.getColumnIndex("username")),
                    cursor.getString(cursor.getColumnIndex("date")),
                    cursor.getInt(cursor.getColumnIndex("steps")),
                    cursor.getDouble(cursor.getColumnIndex("height")),
                    cursor.getDouble(cursor.getColumnIndex("weight"))}
            ;
            cursor.close();
            return obj;
        }

        if (cursor != null) {
            cursor.close();
        }

        return null;
    }


    public void updateUser(String name, LocalDate birthDate, String gender, double weight, double height) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("birthDate", birthDate.toString());
        values.put("gender", gender);
        values.put("weight", weight);
        values.put("height", height);
        db.update("User", values, "name=?", new String[]{name});
        db.close();
    }

    public void updateDailyData(String username, String date, int steps, double height, double weight) {
        Log.d("updated", "User is updated with " + steps + " on " + date);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("steps", steps);
        values.put("height", height);
        values.put("weight", weight);

        db.update("DailyData", values, "username=? AND date=?", new String[]{username, date});
        db.close();
    }

    public boolean checkDailyDataExists(String username, String date){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("DailyData", new String[]{"username", "date"}, "username=? AND date=?", new String[]{username, date}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    //Update the database
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(Create_User);
        onCreate(sqLiteDatabase);
    }
}
