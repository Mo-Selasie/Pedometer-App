package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;


public class Update_profile extends AppCompatActivity {
    private EditText etUserName;
    private EditText etWeight;
    private EditText etHeight;
    private EditText etGender;
    private EditText etDofB;
    private String strUserName, strGender, strHeight, strWeight;
    private double dWeight, dHeight;
    private LocalDate ldDofB;
    private Button bUpdate;

    private String formatDateMDY(String wrongDate) {
        String[] subStrs = wrongDate.split("-");
        return subStrs[1] + "/" + subStrs[2] + "/" + subStrs[0];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sama_update_profile);

        etUserName = findViewById(R.id.text_userName);
        etHeight = findViewById(R.id.text_height);
        etWeight = findViewById(R.id.text_weight);
        etGender = findViewById(R.id.text_gender);
        etDofB = findViewById(R.id.text_dateOfBirth);
        bUpdate = findViewById(R.id.buttonEnroll);

        // get current user
        User currentUser = UserManager.getInstance().getCurrentUser();
        DatabaseHelper db = new DatabaseHelper(this);
        User dbUser = db.getUserByName(currentUser.getName());

        if (dbUser != null) {
            etHeight.setText(String.valueOf(dbUser.getHeight()*100)); // User Conversion
            etWeight.setText(String.valueOf(dbUser.getWeight()));
            etUserName.setText(String.valueOf(dbUser.getName()));
            etGender.setText(String.valueOf(dbUser.getGender()));
            etDofB.setText(formatDateMDY(String.valueOf(dbUser.getBirthDate())));

        } else {
            Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
        }

        bUpdate.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                strUserName = etUserName.getText().toString();
                strGender = etGender.getText().toString();
                ldDofB = LocalDate.parse(etDofB.getText().toString(), formatter);
                ldDofB = LocalDate.parse(etDofB.getText().toString(), formatter);
                strHeight = etHeight.getText().toString();
                strWeight = etWeight.getText().toString();
                dHeight = Double.parseDouble(strHeight)/100;
                dWeight = Double.parseDouble(strWeight);

                DatabaseHelper db = new DatabaseHelper(getApplicationContext());

//                if(db.checkUserExists(strUserName)){
//                    Toast.makeText(getApplicationContext(),"This User name already exists", Toast.LENGTH_LONG).show();
//                    return;
//                }
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
                else{
                    db.updateUser(strUserName, ldDofB, strGender, dWeight, dHeight);
                    Toast.makeText(getApplicationContext(), "Complete!", Toast.LENGTH_SHORT).show();
                    User updatedUser = new User(strUserName, strGender, ldDofB, dWeight, dHeight);
                    UserManager.getInstance().setCurrentUser(updatedUser);
                    Intent intent = new Intent(Update_profile.this, Profile.class);
                    intent.putExtra("CurrUser", updatedUser);
                    startActivity(intent);
                    finish();
                }
            }
        });

        EditText editTextDate = findViewById(R.id.text_dateOfBirth);
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        Update_profile.this,
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
