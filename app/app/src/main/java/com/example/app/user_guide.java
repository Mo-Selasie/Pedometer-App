package com.example.app;

public class user_guide {
    private int user_target;
    private int step_count;
    private int suggested_daily_target;

    //Empty constructor for testing
    public user_guide() {}

    public void setUserTarget(int num){
        if (num <= 0) {
            throw new IllegalArgumentException("User target should be greater than zero");
        }
        this.user_target = num;
        this.setSuggestedDailyTarget();
    }

    public int getUserTarget(){return this.user_target;}

    public void setStepCount(int num){
        if (num < 0) {
            throw new IllegalArgumentException("Step count should not be negative");
        }
        this.step_count = num;
        this.setSuggestedDailyTarget();
    }

    public int getStepCount(){return this.step_count;}

    private void setSuggestedDailyTarget(){
        this.suggested_daily_target = this.user_target - this.step_count;

        //Daily target has been hit if the step count exceed the user target.
        //Current solution is to set daily target to 0 so it is not negative.
        if (this.user_target < this.step_count) {
            this.suggested_daily_target = 0;
        }
    }

    public int getSuggestedDailyTarget(){return this.suggested_daily_target;}

}
