package com.example.app;

import android.content.Context;

public class AuthenticationManager {
    private DatabaseHelper db;

    public AuthenticationManager(Context context) {
        db = new DatabaseHelper(context);
    }

    public User loginUser(String name, String plainTextPassword) {
        String storedHashPassword = db.getPassword(name);
        if (storedHashPassword != null && SecurityUtils.checkPassword(plainTextPassword, storedHashPassword)) {
            return db.getUserByName(name);
        } else {
            return null;
        }
    }
}
