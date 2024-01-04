package com.example.app;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.time.LocalDate;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dpHelper;
    private EditText etUserName;
    private EditText etPassword;
    private Button bLogin;
    private TextView tRegister;
    private String strUserName;
    private String strPassword;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sama_login);

//        dpHelper = new DatabaseHelper(this);
//        dpHelper.getWritableDatabase();

        etUserName = findViewById(R.id.text_userName);
        etPassword = findViewById(R.id.text_password);
        bLogin = findViewById(R.id.buttonLogin);
        tRegister = findViewById(R.id.register);

        bLogin.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("Range")
            @Override
            public void onClick(View view) {
                strUserName = etUserName.getText().toString();
                strPassword = etPassword.getText().toString();

                DatabaseHelper db = new DatabaseHelper(getApplicationContext());

                if(!db.checkUserExists(strUserName)){
                    Toast.makeText(getApplicationContext(),"User has NOT registered", Toast.LENGTH_LONG).show();
                    return;
                }

                String storedPassword = db.getPassword(strUserName);
                //if(storedPassword != null && strPassword.equals(storedPassword)){
                if(storedPassword != null && SecurityUtils.checkPassword(strPassword, storedPassword)){
                    Toast.makeText(getApplicationContext(), "Login succeeded", Toast.LENGTH_SHORT).show();
                    // TO BE CONTINUE
                    // 跳转到另一个界面，进行账户内容的查看以及设置目标。
                    Cursor cursor = db.getUser(strUserName);
                    if (cursor != null && cursor.moveToFirst()) {
                        // 假设 cursor 中包含了所有需要的用户信息
                        @SuppressLint("Range") String gender = cursor.getString(cursor.getColumnIndex("gender"));
                        LocalDate birthDate = LocalDate.parse(cursor.getString(cursor.getColumnIndex("birthDate")));
                        double height = cursor.getDouble(cursor.getColumnIndex("height"));
                        double weight = cursor.getDouble(cursor.getColumnIndex("weight"));
                        User currentUser = new User(strUserName, gender, birthDate, weight, height);
                        UserManager.getInstance().setCurrentUser(currentUser);
                        Intent intent = new Intent(MainActivity.this, UguideActivity.class);
                        intent.putExtra("curr", currentUser);
                        startActivity(intent);
                        finish();   //或许需要销毁当前的登录界面？
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to retrieve user information", Toast.LENGTH_LONG).show();
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                }else if(storedPassword == null){
                    Toast.makeText(getApplicationContext(), "Incorrect password1", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Incorrect password2", Toast.LENGTH_LONG).show();
                }

            }
        });

        tRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Activity_register.class);
                startActivity(intent);
                finish();   //或许需要销毁当前的登录界面？
            }
        });


    }
}