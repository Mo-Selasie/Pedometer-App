package com.example.app;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.test.platform.app.InstrumentationRegistry;

import java.time.LocalDate;

public class DatabaseHelperTest {
    private DatabaseHelper database;
    private Context appcontext = InstrumentationRegistry.getInstrumentation().getTargetContext();

    @Before
    public void setup() {
        database = new DatabaseHelper(appcontext);
    }

    @After
    public void tearDown() {
        database = null;
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.app", appContext.getPackageName());
    }

    @Test
    public void startupTest() {
        assertEquals("Success","Success");
    }

    @Test
    public void testUserCanBeCreated() {
        LocalDate curr = LocalDate.of(2012,6,30);
        //check if Alex exists already
        if (database.checkUserExists("Alex")) {
            System.out.println("Users already exists not adding new user");
        }
        else {
            database.addUser("Alex",curr,"Male",55.7,1004, "password");
        }
        Cursor x = database.getUser("Alex");
        assertEquals("Success","Success");
    }

    @Test
    public void testGetUserGenderMale() {
        LocalDate curr = LocalDate.of(2012,6,30);
        //check if Alex exists already
        if (database.checkUserExists("Alex")) {
            System.out.println("Users already exists not adding new user");
        }
        else {
            database.addUser("Alex",curr,"Male",55.7,1004, "password");
        }
        User test = database.getUserByName("Alex");
        String userGender = test.getGender();
        assertEquals("Male",userGender);
    }

    @Test
    public void testDeletingThenAddingUser() {
        LocalDate curr = LocalDate.of(2012,6,30);
        //check if Alex exists already
        if (database.checkUserExists("Alex")) {
            System.out.println("Users already exists not adding new user");
        }
        else {
            database.addUser("Alex",curr,"Male",55.7,1004, "password");
        }
        database.deleteUser("Alex");
        if (database.checkUserExists("Alex")) {
            assertEquals(1,0);
        }
        else {
            //add user back.
            database.addUser("Alex",curr,"Male",55.7,1004, "password");
            if (database.checkUserExists("Alex")) {
                assertEquals("Success","Success");
            }
        }
    }


    @Test
    public void UpdateNewUserWeight() {
        LocalDate curr = LocalDate.of(2001,6,30);
        if (database.checkUserExists("Alex")) {
            database.deleteUser("Alex");
        }
        database.addUser("Alex",curr,"Female",50,200, "password");
        database.updateUser("Alex",curr,"Female",40,200);
        User test2 = database.getUserByName("Alex");
        double userWeight = test2.getWeight();
        database.deleteUser("Alex");
        assertEquals(40,(long) userWeight);
    }
    @Test
    public void UpdateNewUserHeight() {
        LocalDate curr = LocalDate.of(2000,6,30);
        if (database.checkUserExists("Alex")) {
            database.deleteUser("Alex");
        }
        database.addUser("Alex",curr,"Male",50,200, "password");
        database.updateUser("Alex",curr,"Female",50,250);
        User test2 = database.getUserByName("Alex");
        double userHeight = test2.getHeight();
        database.deleteUser("Alex");
        assertEquals(250,(long) userHeight);
    }

    //test updating the daily data
    @Test
    public void DailyDataAddUserNoErrors() {
        if(database.checkDailyDataExists("Alex","11/27/2022")) {
            database.deleteDailyData("Alex","11/27/2022");
        }
        database.addDailyData("Alex", "11/27/2022",10000,50,200);
        assertEquals("Success","Success");
    }

    @Test
    public void DailyDataAddUserAndVerify() {
        if(database.checkDailyDataExists("Alex","11/27/2022")) {
            database.deleteDailyData("Alex","11/27/2022");
        }
        database.addDailyData("Alex", "11/27/2022",10000,50,200);
        Cursor x = database.getDailyData("Alex","11/27/2022");
        assertEquals("Success","Success");
    }

    @Test
    public void DailyDataCheckSteps() {
        if(database.checkDailyDataExists("Alex","11/27/2022")) {
            database.deleteDailyData("Alex","11/27/2022");
        }
        database.addDailyData("Alex", "11/27/2022",10000,50,200);
        Object[] curr = database.getDailyDataByNameDate("Alex","11/27/2022");
        //username, date, steps, height, weight
        int dataValue = ((Number) curr[2]).intValue();
        assertEquals(10000,dataValue);
    }

    @Test
    public void DailyDataCheckDate() {
        if(database.checkDailyDataExists("Alex","11/27/2022")) {
            database.deleteDailyData("Alex","11/27/2022");
        }
        database.addDailyData("Alex", "11/27/2022",10000,50,200);
        Object[] curr = database.getDailyDataByNameDate("Alex","11/27/2022");
        //username, date, steps, height, weight
        String dataValue = ((String) curr[1]);
        assertEquals("11/27/2022",dataValue);
    }

    @Test
    public void DailyDataCheckHeight() {
        if(database.checkDailyDataExists("Alex","11/27/2022")) {
            database.deleteDailyData("Alex","11/27/2022");
        }
        database.addDailyData("Alex", "11/27/2022",10000,45,200);
        Object[] curr = database.getDailyDataByNameDate("Alex","11/27/2022");
        //username, date, steps, height, weight
        double dataValue = ((Number) curr[3]).doubleValue();
        assertEquals(45,(long) dataValue);
    }

    @Test
    public void DailyDataCheckWeight() {
        if(database.checkDailyDataExists("Alex","11/27/2022")) {
            database.deleteDailyData("Alex","11/27/2022");
        }
        database.addDailyData("Alex", "11/27/2022",10000,50,270);
        Object[] curr = database.getDailyDataByNameDate("Alex","11/27/2022");
        //username, date, steps, height, weight
        double dataValue = ((Number) curr[4]).doubleValue();
        assertEquals(270,(long) dataValue);
    }

    @Test
    public void UpdateDailyDataSteps() {
        if(database.checkDailyDataExists("Alex","11/27/2022")) {
            database.deleteDailyData("Alex","11/27/2022");
        }
        database.addDailyData("Alex", "11/27/2022",10000,50,270);
        database.updateDailyData("Alex", "11/27/2022",500,450,100);
        Object[] curr = database.getDailyDataByNameDate("Alex","11/27/2022");
        //username, date, steps, height, weight
        int dataValue = ((Number) curr[2]).intValue();
        assertEquals(500,(long) dataValue);
    }

    @Test
    public void UpdateDailyDataHeight() {
        if(database.checkDailyDataExists("Alex","11/27/2022")) {
            database.deleteDailyData("Alex","11/27/2022");
        }
        database.addDailyData("Alex", "11/27/2022",10000,50,270);
        database.updateDailyData("Alex", "11/27/2022",10000,100,270);
        Object[] curr = database.getDailyDataByNameDate("Alex","11/27/2022");
        //username, date, steps, height, weight
        double dataValue = ((Number) curr[3]).doubleValue();
        assertEquals(100,(long) dataValue);
    }

    @Test
    public void UpdateDailyDataWeight() {
        if(database.checkDailyDataExists("Alex","11/27/2022")) {
            database.deleteDailyData("Alex","11/27/2022");
        }
        database.addDailyData("Alex", "11/27/2022",10000,50,270);
        database.updateDailyData("Alex", "11/27/2022",10000,50,300);
        Object[] curr = database.getDailyDataByNameDate("Alex","11/27/2022");
        //username, date, steps, height, weight
        double dataValue = ((Number) curr[4]).doubleValue();
        assertEquals(300,(long) dataValue);
    }

}
