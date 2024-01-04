package com.example.app;

import android.annotation.SuppressLint;
import java.io.Serializable;

import java.time.LocalDate;
import java.time.Period;

public class User implements Serializable{
    private String name;
    private String gender;  //assuming you can set whatever gender u like
    private LocalDate birthDate;    //date of birth
    private int age;
    private double weight;  //assuming weight is in kilograms
    private double height;  //assuming height is in meters


    @SuppressLint("NewApi")
    public User(String name, String gender, LocalDate birthDate, double weight, double height){
        this.name = name;
        this.age = Period.between(birthDate, LocalDate.now()).getYears(); 
        if (age < 0 || age > 130) {
            throw new IllegalArgumentException("Age should be between 0 and 130");
        }
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.birthDate = birthDate;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getGender(){
        return gender;
    }
    public void setGender(String gender){
        this.gender = gender;
    }

    public int getAge(){
        return age;
    }
    public void setAge(int age){
        if (age < 0 || age > 130) {
            throw new IllegalArgumentException("Age should be between 0 and 130");
        }
        this.age = age;
    }

    public LocalDate getBirthDate() {return birthDate;};
    @SuppressLint("NewApi")
    public void setBirthDate(LocalDate birthDate){
        int tmpAge = Period.between(birthDate, LocalDate.now()).getYears();
        if (tmpAge < 0 || tmpAge > 130) {
            throw new IllegalArgumentException("Age should be between 0 and 130");
        }
        this.birthDate = birthDate;
    }

    public double getWeight(){
        return weight;
    }
    public void setWeight(double weight){
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight should greater than 0");
        }
        this.weight = weight;
    }

    public double getHeight(){
        return height;
    }
    public void setHeight(double height){
        if (height <= 0) {
            throw new IllegalArgumentException("Height should be greater than 0");
        }
        this.height = height;
    }

    public double getBMI(){
        return weight / (height * height);
    }
    public double calculateMinHealthyWeight() {
        return 18.5 * (this.height * this.height);
    }
    public double calculateMaxHealthyWeight() {
        return 24.9 * (this.height * this.height);
    }

}
