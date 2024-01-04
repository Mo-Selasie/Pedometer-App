package com.example.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class user_guide_ui extends AppCompatActivity{
    private user_guide UserGuide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sama_user_guide);
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        int defaultValue = 0;
        int dailyStepGoal = sharedPreferences.getInt("DailyStepGoal", defaultValue);


        // Initialize the user_guide instance
        user_guide userGuide = new user_guide();

        // Set user target and step count (you can get these values from your app logic)
        userGuide.setUserTarget(dailyStepGoal);
        userGuide.setStepCount(5000);

        // Update and display the suggested_daily_target
        //userGuide.setSuggestedDailyTarget();

        // Update UI components
        updateUI();

    }

    private void updateUI() {
        // Find UI components by their IDs
        TextView textViewStepCount = findViewById(R.id.textViewStepCount);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        TextView textViewSuggestedDailyTarget = findViewById(R.id.textViewSuggestedDailyTarget);

        // Get values from the user_guide instance
        int userTarget = UserGuide.getUserTarget();
        int stepCount = UserGuide.getStepCount();
        int suggestedDailyTarget = UserGuide.getSuggestedDailyTarget();

        // Update UI components
        textViewStepCount.setText("Step Count: " + stepCount);
        progressBar.setMax(userTarget);
        progressBar.setProgress(stepCount);
        textViewSuggestedDailyTarget.setText("Suggested Daily Target: " + suggestedDailyTarget);
    }
}
