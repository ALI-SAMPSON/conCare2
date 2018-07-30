package com.example.icode.concare.models;

public class Users {

    //field in the database
    private String username;
    private String password;
    private String confirmPassword;
    private String gender;
    private String telephoneNumber;
    private String address;
    private String campus;


    //default constructor
    public Users(){
    }

    //constructor with parameters
    public Users(String username,String password,String confirmPassword,String gender,String telephoneNumber,String address,String campus){
        this.username = username;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.gender = gender;
        this.telephoneNumber = telephoneNumber;
        this.address = address;
        this.campus = campus;

    }

    //Getter and Setter Method for Username
    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername(){
        return username;
    }

    //Getter and Setter Method for Password
    public void setPassword(String password){
        this.password = password;
    }
    public String getPassword(){
        return password;
    }

    //Getter and Setter Method for Confirm Password
    public void setConfirmPassword(String confirmPassword){
        this.confirmPassword = confirmPassword;
    }
    public String getConfirmPassword(){
        return confirmPassword;
    }

    //Getter and Setter Method for Gender
    public void setGender(String gender){
        this.gender = gender;
    }
    public String getGender(){
        return gender;
    }

    //Getter and Setter Method for TelephoneNumber
    public void setTelephoneNumber(String telephoneNumber){
        this.telephoneNumber = telephoneNumber;
    }
    public String getTelephoneNumber(){
        return telephoneNumber;
    }

    //Getter and Setter Method for Address
    public void setAddress(String address){
        this.address = address;
    }
    public String getAddress(){
        return address;
    }

    //Getter and Setter Method for Campus
    public void setCampus(String campus){
        this.campus = campus;
    }

    public String getCampus(){
        return campus;
    }




}
