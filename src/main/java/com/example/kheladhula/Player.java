package com.example.kheladhula;

import java.io.Serializable;

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String Country;
    private int age;
    private double height;
    private String Club;
    private String Position;
    private int number;
    private int weeklySalary;
    private boolean isMarketplace;

    public Player(String name, String Country, int age, double height, String Club, String Position, int number,
                  int weeklySalary,boolean isMarketplace) {
        this.name = name;
        this.Country = Country;
        this.age = age;
        this.height = height;
        this.Club = Club;
        this.Position = Position;
        this.number = number;
        this.weeklySalary = weeklySalary;
        this.isMarketplace = isMarketplace;
    }

    // getters
    public String getName() {
        return name;
    }

    public String getCountry() {
        return Country;
    }

    public int getAge() {
        return age;
    }

    public double getHeight() {
        return height;
    }

    public String getClub() {
        return Club;
    }

    public String getPosition() {
        return Position;
    }

    public int getNumber() {
        return number;
    }

    public int getWeeklySalary() {
        return weeklySalary;
    }

    public int getJerseyNumber() {
        return number;
    }

    public boolean getIsMarketplace(){
        return isMarketplace;
    }

    public void setIsMarketPlace(boolean isMarketplace){
        this.isMarketplace = isMarketplace;
    }

    public void setClub(String club){
        this.Club = club;
    }

//    public void setMarketplace(boolean isMarketplace){
//        this.isMarketplace = isMarketplace;
//    }
    // display
    @Override
    public String toString() {
        return "Name: " + name + "\nCountry: " + Country + "\nAge: " + age + "\nHeight: " + height + "\nClub: " + Club
                + "\nPosition: " + Position + "\nNumber: " + number + "\nWeekly Salary: " + weeklySalary + "\nMarketplace: " + isMarketplace;
    }

}
