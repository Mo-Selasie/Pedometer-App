package com.example.app;

public class UserManager {
    private static UserManager instance;
    private User currentUser;

    private UserManager() { }

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    // 其他可能的方法，比如更新用户信息等
}
