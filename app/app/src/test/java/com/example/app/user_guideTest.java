package com.example.app;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class user_guideTest {

    private user_guide ug;

    @Before
    public void setUp() {
        ug = new user_guide();
    }

    @After
    public void tearDown(){
    }


    @Test
    public void SetUserTargetBasic1() {
        ug.setUserTarget(5);
        assertEquals(5,ug.getUserTarget());
    }

    @Test
    public void SetUserTargetBasic2() {
        ug.setUserTarget(66);
        assertEquals(66,ug.getUserTarget());
    }


    //You should not be able to set a negative target.
    @Test(expected = IllegalArgumentException.class)
    public void SetUserTargetNegative() {
        ug.setUserTarget(-2);
    }

    //No zero target allowed.
    @Test(expected = IllegalArgumentException.class)
    public void SetUserTargetZero() {
        ug.setUserTarget(0);
    }


    //-----------------------------------------
    //Testing step count
    @Test
    public void setStepCountBasic1() {
        ug.setStepCount(5);
        assertEquals(5,ug.getStepCount());
    }

    @Test
    public void setStepCountBasic2() {
        ug.setStepCount(500000067);
        assertEquals(500000067,ug.getStepCount());
    }

    //Negative step count is not allowed.
    @Test(expected = IllegalArgumentException.class)
    public void setStepCountNegative() {
        ug.setStepCount(-55);
    }

    //I think step count zero is allowed
    @Test
    public void setStepCountZero() {
        ug.setStepCount(0);
        assertEquals(0,ug.getStepCount());
    }


    //------------------------------
    //Suggested Daily Target Tests
    @Test
    public void getSuggestedDailyTargetBasic1() {
        ug.setUserTarget(50);
        ug.setStepCount(40);
        assertEquals(10,ug.getSuggestedDailyTarget());
    }

    @Test
    public void getSuggestedDailyTargetBasic2() {
        ug.setUserTarget(67);
        ug.setStepCount(2);
        assertEquals(65,ug.getSuggestedDailyTarget());
    }

    @Test
    public void getSuggestedDailyTargetBasicZero() {
        ug.setUserTarget(67);
        ug.setStepCount(67);
        assertEquals(0,ug.getSuggestedDailyTarget());
    }

    //Current solution is if daily target is negative set it to 0.
    @Test
    public void getSuggestedDailyTargetBasicNegative() {
        ug.setUserTarget(10);
        ug.setStepCount(500);
        assertEquals(0,ug.getSuggestedDailyTarget());
    }


}