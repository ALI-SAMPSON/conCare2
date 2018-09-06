package com.example.icode.concare.models;

public class Users {

    //field in the database
    private String email;
    private String username;
    private String gender;
    private String telephoneNumber;


    //default constructor
    public Users(){
    }

    //constructor with one or more parameters
    public Users(String email,String username,String gender,String telephoneNumber){
        this.email = email;
        this.username = username;
        this.gender = gender;
        this.telephoneNumber = telephoneNumber;

    }

    //Getter and Setter Method for Username
    public void setEmail(String email){
        this.email = email;
    }

    public String getEmail(){
        return email;
    }

    //Getter and Setter Method for Username
    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername(){
        return username;
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


}
