package com.example.icode.concare.models;

public class CurrentUsers {

    //field in the database
    private String username;
    private String telephoneNumber;

    //default constructor
    public CurrentUsers(){
    }

    //constructor with one or more parameters
    public CurrentUsers(String username,String telephoneNumber){
        this.username = username;
        this.telephoneNumber = telephoneNumber;

    }

    //Getter and Setter Method for Username
    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername(){
        return username;
    }


    //Getter and Setter Method for TelephoneNumber
    public void setTelephoneNumber(String telephoneNumber){
        this.telephoneNumber = telephoneNumber;
    }
    public String getTelephoneNumber(){
        return telephoneNumber;
    }


}
