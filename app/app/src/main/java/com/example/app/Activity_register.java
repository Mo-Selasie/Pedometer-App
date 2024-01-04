package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Activity_register extends AppCompatActivity {

    private EditText etUserName;
    private EditText etPassword;
    private EditText etV_password;
    private Button bEnroll;
    private EditText dOfB;
    private EditText weight;
    private EditText height;
    private EditText gender;
    private String strUserName, strPassword, strV_Password, strGender, strHeight, strWeight;
    private double dWeight, dHeight;
    private LocalDate ldDofB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sama_register);

        etUserName = findViewById(R.id.text_userName);
        etPassword = findViewById(R.id.text_password1);
        etV_password = findViewById(R.id.text_password2);
        dOfB = findViewById(R.id.text_dateOfBirth);
        weight = findViewById(R.id.text_weight);
        height = findViewById(R.id.text_height);
        gender = findViewById(R.id.text_gender);
        bEnroll = findViewById(R.id.buttonEnroll);

        bEnroll.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

                strUserName = etUserName.getText().toString();
                strPassword = etPassword.getText().toString();
                strV_Password = etV_password.getText().toString();
                strGender = gender.getText().toString();
                ldDofB = LocalDate.parse(dOfB.getText().toString(), formatter);
                strHeight = height.getText().toString();
                strWeight = weight.getText().toString();
                dHeight = Double.parseDouble(strHeight)/100; // To get the height in m
                dWeight = Double.parseDouble(strWeight);

                DatabaseHelper db = new DatabaseHelper(getApplicationContext());

                if(db.checkUserExists(strUserName)){
                    Toast.makeText(getApplicationContext(),"This User name already exists", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!strV_Password.equals(strPassword)){
                    Toast.makeText(getApplicationContext(),"Unmatched passwords", Toast.LENGTH_LONG).show();
                    return;
                }
                if(dHeight < 0.7 || dHeight > 2.5){
                    Toast.makeText(getApplicationContext(),"Height should between 70cm to 250cm", Toast.LENGTH_LONG).show();
                    return;
                }
                if(dWeight < 30.0 || dWeight > 250.0){
                    Toast.makeText(getApplicationContext(),"Weight should between 30kg to 250kg", Toast.LENGTH_LONG).show();
                    return;
                }
                String regexDecimal = "^[0-9]*\\.?[0-9]{0,1}$";
                String regexLetters = "^[A-Za-z]+$";

                if (!strHeight.matches(regexDecimal) || !strWeight.matches(regexDecimal)) {
                    Toast.makeText(getApplicationContext(), "Height and Weight should be a number with at most one decimal place", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!strGender.matches(regexLetters)) {
                    Toast.makeText(getApplicationContext(), "Gender should only contain letters", Toast.LENGTH_LONG).show();
                    return;
                }
                if(Period.between(ldDofB, LocalDate.now()).getYears() > 130 || Period.between(ldDofB, LocalDate.now()).getYears() <= 0){
                    Toast.makeText(getApplicationContext(),"Illegal date of birth", Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    //db.addUser(strUserName, ldDofB, strGender, dWeight, dHeight, strPassword);
                    db.addUser(strUserName, ldDofB, strGender, dWeight, dHeight, SecurityUtils.hashPassword(strPassword));
                    Toast.makeText(getApplicationContext(), "Welcome!", Toast.LENGTH_SHORT).show();
                    User currentUser = new User(strUserName, strGender, ldDofB, dWeight, dHeight);
                    UserManager.getInstance().setCurrentUser(currentUser);
                    String currentDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
                    db.addDailyData(currentUser.getName(), currentDate, 0, currentUser.getHeight(), currentUser.getWeight());
                    Intent intent = new Intent(Activity_register.this, UguideActivity.class);
                    intent.putExtra("newUser", currentUser);
                    startActivity(intent);
                    finish();
                }
            }
        });

        //Selecting date of birth
        EditText editTextDate = findViewById(R.id.text_dateOfBirth);
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        Activity_register.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String formattedMonth = (month + 1) < 10 ? "0" + (month + 1) : String.valueOf(month + 1);
                                String formattedDay = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                                editTextDate.setText(formattedMonth + "/" + formattedDay + "/" + year);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });


    }
}