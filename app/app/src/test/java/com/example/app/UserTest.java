package com.example.app;
import java.time.LocalDate;
import java.time.Period;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.junit.Test;

import java.time.LocalDate;

public class UserTest {
    private User newuser;
    private LocalDate birthday;
    @Before public void setUp() {
        birthday = LocalDate.of(2012,6,30);
        newuser = new User("Alex","Male", birthday,56.7,53.4);
    }

    @After public void tearDown() {
        newuser = null;
    }

    @Test
    public void getNameTest1() {
        newuser.setName("Alex");
        assertEquals("Alex", newuser.getName());
    }

    //Test if you don't set the name originally if it works
    @Test
    public void getNameTest2() {
        assertEquals("Alex", newuser.getName());
    }

    @Test
    public void getGenderTest1() {
        newuser.setGender("Female");
        assertEquals("Female", newuser.getGender());
    }

    //Test if dont set gender with set function.
    @Test
    public void getGenderTest2() {
        assertEquals("Male", newuser.getGender());
    }

    @Test
    public void getAgeTest1() {
        newuser.setAge(44);
        assertEquals(44, newuser.getAge());
    }

    //Test if you don't set it first.
    @Test
    public void getAgeTest2() {
        assertEquals(11, newuser.getAge());
    }

    //Test if age is negative
    @Test(expected = IllegalArgumentException.class)
    public void getAgeTestNegative() {
        newuser.setAge(-5);
        newuser.getAge();
    }

    //Test if age is zero
    @Test
    public void getAgeTestZero() {
        newuser.setAge(0);
        newuser.getAge();
    }

    @Test
    public void getWeightTest1() {
        newuser.setWeight(44.4);
        assertEquals(44.4, newuser.getWeight(),.1);
    }

    //Test if you don't set beforehand
    @Test
    public void getWeightTest2() {
        assertEquals(56.7, newuser.getWeight(),.1);
    }

    //Test if weight is negative
    @Test(expected = IllegalArgumentException.class)
    public void getWeightTestNegative() {
        newuser.setWeight(-1007.55555);
        newuser.getWeight();
    }

    //Test if weight is zero (this should also fail in my opinion).
    @Test(expected = IllegalArgumentException.class)
    public void getWeightTestZero() {
        newuser.setWeight(0);
        newuser.getWeight();
    }

    @Test
    public void getHeightTest1() {
        newuser.setHeight(44.7);
        assertEquals(44.7, newuser.getHeight(),.1);
    }

    //Test if you don't set beforehand
    @Test
    public void getHeightTest2() {
        assertEquals(53.4, newuser.getHeight(),.1);
    }

    //Test if height is set to a negative value
    @Test(expected = IllegalArgumentException.class)
    public void getHeightTestNegative() {
        newuser.setHeight(-1007.55555);
        newuser.getHeight();
    }

    //Test if height is set to a value of zero
    @Test(expected = IllegalArgumentException.class)
    public void getHeightTestZero() {
        newuser.setHeight(0);
        newuser.getHeight();
    }


    @Test
    public void getBMITest1() {
        newuser.setWeight(16);
        newuser.setHeight(2);
        assertEquals(4, newuser.getBMI(),.1);
    }

    //Test if you don't set beforehand
    @Test
    public void getBMITest2() {
        assertEquals(.019, newuser.getBMI(),.001);
    }

    //Test if bmi attempts to divide by 0.
    @Test(expected = IllegalArgumentException.class)
    public void getBMITestDiv0() {
        newuser.setHeight(0);
        newuser.getBMI();
    }
}