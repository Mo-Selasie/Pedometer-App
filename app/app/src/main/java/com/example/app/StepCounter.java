package com.example.app;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.content.SharedPreferences;
import android.util.Log;
import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

import androidx.annotation.Nullable;

public class StepCounter extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    int steps;
    private int initialStepCount = -1;
//    private int currentStepCount = 0;
    private int currentStepCount;
    private SharedPreferences sharedPreferences;
    private String lastUpdateDate;
    DatabaseHelper db;
    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("StepCounterPrefs", MODE_PRIVATE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        db = new DatabaseHelper(getApplicationContext());

        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
        }

        setupInitialStepCount();
    }

    private void setupInitialStepCount() {
        String currentDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
        lastUpdateDate = sharedPreferences.getString("lastUpdateDate", "");

        if (!currentDate.equals(lastUpdateDate)) {
            // New day, reset step count
            User user = UserManager.getInstance().getCurrentUser();
            currentStepCount = checkDb(user, db, currentDate);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("lastUpdateDate", currentDate);
            editor.putInt("currentStepCount", currentStepCount);
            editor.apply();
        } else {
            Log.d("Initialization", "Initializes");
            // Same day, continue from saved count
            User user = UserManager.getInstance().getCurrentUser();
            currentStepCount = checkDb(user, db, currentDate);
//            currentStepCount = sharedPreferences.getInt("currentStepCount", 0);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //required to exist
        return null;
    }

    @Override
    public void onDestroy() {

        sensorManager.unregisterListener(this);
        String currentDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
        Log.d("Destroyed", "step counter is gone");
        User user = UserManager.getInstance().getCurrentUser();
        db.updateDailyData(user.getName(), currentDate, getCurrentStepCount(), user.getHeight(), user.getWeight());
        saveCurrentStepData();

        super.onDestroy();
    }

    private void saveCurrentStepData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("currentStepCount", initialStepCount);
        editor.apply();
    }

    private void broadcastStepCount() {
        Intent intent = new Intent("com.example.app.STEP_COUNT_UPDATE");
        intent.putExtra("step_count", getCurrentStepCount());
        sendBroadcast(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            currentStepCount += 1;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("currentStepCount", currentStepCount);
            broadcastStepCount();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Can be left empty
    }

    public int getCurrentStepCount() {
        return currentStepCount;
    }




    public int getSteps() {
        return this.steps;
    }

    private int checkDb(User user, DatabaseHelper db, String currentDate) {
        Calendar calendar = Calendar.getInstance();
        Cursor cursor = db.getDailyData(user.getName(), currentDate);

        if (cursor == null){
            db.addDailyData(user.getName(), currentDate, 0, user.getHeight(), user.getWeight());
            Log.d("null cursor", "cursor is null");
            return 0;
        }
        if (cursor.getCount() == 0) {
            db.addDailyData(user.getName(), currentDate, 0, user.getHeight(), user.getWeight());
            Log.d("empty cursor", "cursor is 0");
            cursor.close();
            return 0;
        }
        String[] columns = cursor.getColumnNames();
        String steps = columns[2];
        int idx = cursor.getColumnIndex(user.getName());
        Log.d("Row count", String.valueOf(cursor.getCount()));
        Log.d("Column amount", String.valueOf(cursor.getColumnCount()));
        int result;
        if (idx == -1) {
            Log.d("List", String.join(" ", cursor.getColumnNames()));
            Log.d("No user", "User does not exist");
            result = 0;
        }
        else {
            result = cursor.getInt(idx);
        }
        cursor.close();
        return result;
    }
}
