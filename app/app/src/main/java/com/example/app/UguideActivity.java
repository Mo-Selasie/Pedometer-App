package com.example.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UguideActivity extends AppCompatActivity {
    private ProgressBar progressBarSteps;
    private TextView textViewSteps, textViewCalories, textViewMiles, textViewTarget;
    private Button buttonUserProfile, buttonUserHistory, buttonSetGoal;
    private BroadcastReceiver stepUpdateReceiver;
    DatabaseHelper db;
    final int[] steps = {0};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uguide);

        User currentUser = UserManager.getInstance().getCurrentUser();

        db = new DatabaseHelper(getApplicationContext());
        if (currentUser == null) {
            Toast.makeText(this, "No active user session. Please log in.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        Intent serviceIntent = new Intent(this, StepCounter.class);
        startService(serviceIntent);

        Intent intent = getIntent();
//        if (intent.hasExtra("curr")) {
//            currentUser = (User) intent.getSerializableExtra("curr");
//        } else if (intent.hasExtra("newUser")) {
//            currentUser = (User) intent.getSerializableExtra("newUser");
//        }

        progressBarSteps = findViewById(R.id.progress_circular);
        textViewSteps = findViewById(R.id.current_steps);
        textViewCalories = findViewById(R.id.calories_burned);
        textViewTarget = findViewById(R.id.target_steps);
        textViewMiles = findViewById(R.id.miles);
        buttonUserProfile = findViewById(R.id.buttonUserProfile);
//        buttonUserTarget = findViewById(R.id.buttonUserTarget);
        buttonUserHistory = findViewById(R.id.buttonUserHistory);
        buttonSetGoal = findViewById(R.id.buttonSetGoal);

        SharedPreferences sharedPreferences =  getSharedPreferences("AppPreferences", MODE_PRIVATE);
        int targetSteps = sharedPreferences.getInt("DailyStepGoal", 1000);
//        final int[] steps = {0};
        //SharedPreferences sharedPreferences2 = getSharedPreferences("StepCounterPrefs", MODE_PRIVATE);
        String currentDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
//        steps[0] = checkDb(currentUser, db, currentDate);
        Object[] curr = db.getDailyDataByNameDate(currentUser.getName(), currentDate);
        steps[0] = ((Number) curr[2]).intValue();

        sharedPreferences = getSharedPreferences("StepCounterPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("currentStepCount", steps[0]);

        int calories = calculateCalories(steps[0], currentUser);
        double miles = stepsToMiles(steps[0], currentUser);

        textViewSteps.setText(steps[0] + " Steps");
        textViewCalories.setText(calories + " Calories");
        textViewMiles.setText(String.format("%.2f", miles));
        textViewTarget.setText(targetSteps + " Steps");
        progressBarSteps.setMax(targetSteps);
        progressBarSteps.setProgress(steps[0]);

        //buttons initialization
        buttonUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UguideActivity.this, Profile.class);
                startActivity(intent);
            }
        });

        buttonSetGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UguideActivity.this, Setting_goal.class);
                startActivity(intent);
            }
        });

        buttonUserHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UguideActivity.this, UserHistoryUI.class);
                startActivity(intent);
            }
        });

        stepUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("com.example.app.STEP_COUNT_UPDATE".equals(intent.getAction())) {
                    steps[0] = intent.getIntExtra("step_count", 0);
                    db.updateDailyData(currentUser.getName(), currentDate, steps[0], currentUser.getHeight(), currentUser.getWeight());
                    textViewSteps.setText(String.valueOf(steps[0]) + " Steps");
//                    int caloriesBurned = calculateCalories(steps[0], UserManager.getInstance().getCurrentUser());
//                    double milesCovered = stepsToMiles(steps[0], UserManager.getInstance().getCurrentUser());
//
//                    textViewCalories.setText(String.valueOf(caloriesBurned) + " Calories");
//                    textViewMiles.setText(String.format("%.2f Miles", milesCovered));

                    SharedPreferences sharedPreferences = getSharedPreferences("StepPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("CurrentStepCount", steps[0]);
                    editor.apply();

                    updateUI(steps[0], currentUser);
                }
            }
        };
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onResume() {
        super.onResume();
        // Register the BroadcastReceiver to receive step count updates
        registerReceiver(stepUpdateReceiver, new IntentFilter("com.example.app.STEP_COUNT_UPDATE"));

        User currentUser = UserManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "No active user session. Please log in.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Read the saved step goal from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        int targetSteps = sharedPreferences.getInt("DailyStepGoal", 1000); // Default value as a fallback

        String currentDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
        //int currentSteps = checkDb(currentUser, db, currentDate);

        // Update the TextViews with the new target and current steps
        textViewTarget.setText(String.valueOf(targetSteps) + " Steps");
        progressBarSteps.setMax(targetSteps);

        textViewSteps.setText(String.valueOf(steps[0]) + " Steps");

//        // Update ProgressBar
        progressBarSteps.setProgress(steps[0]);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the BroadcastReceiver when the activity is not visible
        unregisterReceiver(stepUpdateReceiver);
    }

    @Override
    public void onDestroy() {

        String currentDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
        User user = UserManager.getInstance().getCurrentUser();
        db.updateDailyData(user.getName(), currentDate, steps[0], user.getHeight(), user.getWeight());
        Log.d("Destroyed", "uguide is destroyed");

        super.onDestroy();

    }


    private int calculateCalories(int steps, User user) {
//        int stride = (int) (user.getHeight() * 0.414);
//        int distance = stride * steps;
//        int time = distance/2;
//        return (int) (time * 3.5 * user.getWeight()/(200 * 60));
        return (int) (0.4 * steps);
    }

    private double stepsToMiles(int steps, User user) {
        int stride = (int) (user.getHeight() * 0.414);
        int distance = stride * steps;
        return distance * 0.62;
    }

    private void updateUI(int steps, User user) {
        // Update step count TextView
        textViewSteps.setText(String.valueOf(steps) + " Steps");

        // Update ProgressBar
        progressBarSteps.setProgress(steps);

        // Update calories and miles (if applicable)
        int caloriesBurned = calculateCalories(steps, user);
        double milesCovered = stepsToMiles(steps, user);
        textViewCalories.setText(String.valueOf(caloriesBurned) + " Calories");
        textViewMiles.setText(String.format("%.2f Miles", milesCovered));
    }

    private int checkDb(User user, DatabaseHelper db, String currentDate) {
        Calendar calendar = Calendar.getInstance();
        Cursor cursor = db.getDailyData(user.getName(), currentDate);

        if (cursor == null){
            db.addDailyData(user.getName(), currentDate, 0, user.getHeight(), user.getWeight());
            return 0;
        }
        if (cursor.getCount() == 0) {
            db.addDailyData(user.getName(), currentDate, 0, user.getHeight(), user.getWeight());
            cursor.close();
            return 0;
        }
        int idx = cursor.getColumnIndex(user.getName());
        int result;
        if (idx == -1) result = 0;
        else {
            result = cursor.getInt(idx);
        }
        cursor.close();
        return result;
    }

}
