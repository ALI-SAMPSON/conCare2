package com.example.icode.concare.models;

import java.security.acl.LastOwnerException;

public class Orders {

    private String telephone_Number;
    private String campus;
    private String location;
    private String other_location;
    private String residence;
    private String contraceptive;
    private String other_contraceptive;
    private String hostel_room_number;

    //default constructor
    public Orders(){ }

    //constructor with one or more parameters
    public Orders(String telephone_Number,String campus,
                  String location,String other_location,String residence,
                  String contraceptive,String other_contraceptive, String hostel_room_number){
        this.telephone_Number = telephone_Number;
        this.campus = campus;
        this.location = location;
        this.other_location = other_location;
        this.residence = residence;
        this.contraceptive = contraceptive;
        this.other_contraceptive = other_contraceptive;
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

    //Getter and Setter Method for other location
    public void setOther_location(String other_location){
        this.other_location = other_location;
    }
    public String getOther_location(){
        return other_location;
    }

    //Getter and Setter Method for residence
    public void setResidence(String residence){
        this.residence = residence;
    }

    public String getResidence(){
        return residence;
    }

    //Getter and Setter Method for gender type of contraceptive
    public void setContraceptive(String contraceptive){
        this.contraceptive = contraceptive;
    }
    public String getContraceptive(){
        return contraceptive;
    }

    //Getter and Setter Method for other contraceptive
    public void setOther_contraceptive(String other_contraceptive){
        this.other_contraceptive = other_contraceptive;
    }
    public String getOther_contraceptive(){
        return other_contraceptive;
    }

    public void setHostel_room_number(String hostel_room_number){
        this.hostel_room_number = hostel_room_number;
    }
    public String getHostel_room_number(){
        return hostel_room_number;
    }


}
