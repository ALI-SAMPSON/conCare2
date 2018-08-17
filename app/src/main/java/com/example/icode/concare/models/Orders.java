package com.example.icode.concare.models;

import java.security.acl.LastOwnerException;

public class Orders {

    private String telephone_Number;
    private String campus;
    private String location;
    private String residence;
    private String type_of_contraceptive;
    private String hostel_room_number;

    //default constructor
    public Orders(){ }

    //constructor with one or more parameters
    public Orders(String telephone_Number,String campus,
                  String location,String residence,
                  String type_of_contraceptive, String hostel_room_number){
        this.telephone_Number = telephone_Number;
        this.campus = campus;
        this.location = location;
        this.residence = residence;
        this.type_of_contraceptive = type_of_contraceptive;
        this.hostel_room_number = hostel_room_number;
    }

    //Getter and Setter Method for telephone number
    public void setTelephone_Number(String telephone_Number){
        this.telephone_Number = telephone_Number;
    }
    public String getTelephone_Number(){
        return telephone_Number;
    }

    //Getter and Setter Method for campus
    public void setCampus(String campus){
        this.campus = campus;
    }
    public String getCampus(){
        return campus;
    }

    //Getter and Setter Method for address
    public void setLocation(String location){
        this.location = location;
    }
    public String getLocation(){
        return location;
    }

    //Getter and Setter Method for residence
    public void setResidence(String residence){
        this.residence = residence;
    }

    public String getResidence(){
        return residence;
    }

    //Getter and Setter Method for gender type of contraceptive
    public void setType_of_contraceptive(String type_of_contraceptive){
        this.type_of_contraceptive = type_of_contraceptive;
    }
    public String getType_of_contraceptive(){
        return type_of_contraceptive;
    }

    public void setHostel_room_number(String hostel_room_number){
        this.hostel_room_number = hostel_room_number;
    }
    public String getHostel_room_number(){
        return hostel_room_number;
    }


}
