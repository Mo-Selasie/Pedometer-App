package com.example.app;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.os.Bundle;

import java.util.Calendar;
import java.util.OptionalInt;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import android.content.BroadcastReceiver;

public class UserHistoryUI extends AppCompatActivity {
    private final String user;
    private BroadcastReceiver stepUpdateReceiver;
    private final int[] steps = {0};
    private int allSteps = 0;

    public UserHistoryUI() {
        user = UserManager.getInstance().getCurrentUser().getName();
    }


    private OptionalInt lookupSteps(DatabaseHelper db, String date) {
        Cursor cursor = db.getDailyData(user, date);
        if (cursor == null) {
            return OptionalInt.empty();
        }
        else if (cursor.getCount() == 0) {
            cursor.close();
            return OptionalInt.empty();
        }
        Object[] curr = db.getDailyDataByNameDate(user, date);
        //username, date, steps, height, weight
        OptionalInt result = OptionalInt.of(((Number) curr[2]).intValue());
        cursor.close();
        return result;
    }

    private int sumDays(DatabaseHelper db, Calendar calendar, int numDays) {
        int total = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        for (int i = 0; i < numDays; ++i) {
            String date = dateFormat.format(calendar.getTime());
            OptionalInt maybeSteps = lookupSteps(db, date);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            if (maybeSteps == null || !maybeSteps.isPresent()) {
                return total;
            }
            total += maybeSteps.getAsInt();
        }
        return total;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_history_ui);

        DatabaseHelper db = new DatabaseHelper(getApplicationContext());

        int[] dayIDs = {R.id.textViewToday, R.id.textViewYesterday, R.id.textViewD2, R.id.textViewD3,
                R.id.textViewD4, R.id.textViewD5, R.id.textViewD6};

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm-dd-yy");

        int weekSteps = 0;
        for (int id : dayIDs) {
            String date = dateFormat.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            OptionalInt maybeSteps = lookupSteps(db, date);
            String stepStr;
            if (maybeSteps == null || !maybeSteps.isPresent()) {
                stepStr = "0";
            }
            else {
                weekSteps += maybeSteps.getAsInt();
                stepStr = maybeSteps.toString();
            }
            TextView view = findViewById(id);
            view.append(stepStr);
        }

        int monthSteps = weekSteps + sumDays(db, calendar, 30 - 7);
        //int monthSteps = 0;
        TextView view = findViewById(R.id.textViewM);
        view.append(String.valueOf(monthSteps));

        int yearSteps = monthSteps + sumDays(db, calendar, 365 - 30);
        //int yearSteps = 0;
        view = findViewById(R.id.textViewYr);
        view.append(String.valueOf(yearSteps));

        allSteps = yearSteps + sumDays(db, calendar, Integer.MAX_VALUE);
        view = findViewById(R.id.textViewAll);
        view.append(String.valueOf(allSteps));
    }
}

