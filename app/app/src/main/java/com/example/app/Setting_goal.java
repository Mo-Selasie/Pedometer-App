package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Locale;

public class Setting_goal extends Activity {
    private EditText goalWeightEditText;
    private EditText daysEditText;
    private TextView suggestedStepsTextView;
    private double currentWeight;
    private Button bComplete;

    private EditText goalSteps;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sama_goal);

        TextView textViewHello = findViewById(R.id.text_hello);
        User currentUser = UserManager.getInstance().getCurrentUser();
        String greeting = "Hello " + currentUser.getName();
        textViewHello.setText(greeting);

        TextView textViewBMI = findViewById(R.id.text_BMI);
        goalWeightEditText = findViewById(R.id.text_goal_weight);
        daysEditText = findViewById(R.id.text_goal_date);
        suggestedStepsTextView = findViewById(R.id.text_suggest_steps);
        bComplete = findViewById(R.id.buttonComplete);
        goalSteps = findViewById(R.id.text_goal_steps);

        if (currentUser != null) {
            double bmiValue = currentUser.getBMI();
            double minWeight = currentUser.calculateMinHealthyWeight();
            double maxWeight = currentUser.calculateMaxHealthyWeight();

            textViewBMI.setText(String.format(Locale.getDefault(), "BMI Value: %.2f", bmiValue));
            String healthyWeightMessage = String.format(Locale.getDefault(),
                    "Suggested Weight: %.1f kg - %.1f kg",
                    minWeight,
                    maxWeight);

            TextView textViewHealthyWeightRange = findViewById(R.id.text_suggest_weight);
            textViewHealthyWeightRange.setText(healthyWeightMessage);

            if (bmiValue < 18.5) {
                textViewBMI.setTextColor(Color.YELLOW);
            } else if (bmiValue >= 18.5 && bmiValue <= 24.9) {
                textViewBMI.setTextColor(Color.GREEN);
            } else {
                textViewBMI.setTextColor(Color.RED);
            }
        } else {
            textViewBMI.setText("User not found"); // 如果没有获取到用户，显示错误消息
        }

        TextWatcher textWatcher = new TextWatcher() {
            public void afterTextChanged(Editable s) {
                calculateAndUpdateSteps();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };

        goalWeightEditText.addTextChangedListener(textWatcher);
        daysEditText.addTextChangedListener(textWatcher);
        currentWeight = currentUser.getWeight();

        bComplete.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                saveDailySteps();
                finish();
            }
        });
    }

    private void saveDailySteps() {
        String stepsStr = goalSteps.getText().toString();
        try {
            int steps = Integer.parseInt(stepsStr);
            // 保存步数到SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("DailyStepGoal", steps);
            editor.apply();

            // 可选：反馈保存成功
            Toast.makeText(this, "Daily step goal saved!", Toast.LENGTH_SHORT).show();

            // 可选：跳转到另一个Activity（如果需要）
            //Intent intent = new Intent(this, OtherActivity.class);
            //intent.putExtra("DailyStepGoal", steps);
            //startActivity(intent);
        } catch (NumberFormatException e) {
            // 反馈没有输入步数 我不会说中文！
            Toast.makeText(this, "Invalid Step Goal", Toast.LENGTH_SHORT).show();
        }
    }

    private void calculateAndUpdateSteps() {
        String goalWeightStr = goalWeightEditText.getText().toString();
        String daysStr = daysEditText.getText().toString();
        TextView view = suggestedStepsTextView;

        if (!goalWeightStr.isEmpty() && !daysStr.isEmpty()) {
            try {
                double goalWeight = Double.parseDouble(goalWeightStr);
                int days = Integer.parseInt(daysStr);
                if (days > 0) {
                    goalWeight = Math.round(goalWeight * 100.0) / 100.0;
                    int suggestedSteps = calculateSuggestedSteps(goalWeight, days);
                    view.setText(parseStepsGoal(suggestedSteps));
                } else {
                    view.setText("");
                }
            } catch (NumberFormatException e) {
                view.setText("");
            }
        } else {
            view.setText("");
        }
    }

    private String parseStepsGoal(int suggestedSteps) {
        int age = UserManager.getInstance().getCurrentUser().getAge();
        int minSteps = (age < 60) ? 10000 : 6000;
        // Get minimum of 1000 steps, and nearest 100 steps
        suggestedSteps = Math.max(suggestedSteps, minSteps);
        suggestedSteps = Math.min(suggestedSteps, 100000);
        suggestedSteps = (int)((double) suggestedSteps / 100.0) * 100;
        String res = String.format(
            getString(R.string.suggested_steps_format), suggestedSteps
        );
        if (suggestedSteps == 100000) {
            res += "+";
        }
        else if (suggestedSteps == minSteps) {
            res += " (min)";
        }
        return res;
    }

    private int calculateSuggestedSteps(double goalWeight, int days) {
        double weightToLose = this.currentWeight - goalWeight;
        double dailyCalorieDeficit = weightToLose * 7700 / days;
        int stepsPerCalorie = 25;
        double suggestedSteps = dailyCalorieDeficit * stepsPerCalorie;
        return (int) suggestedSteps;
    }

}
