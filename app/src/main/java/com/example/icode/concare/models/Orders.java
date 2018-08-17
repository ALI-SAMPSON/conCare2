package com.example.icode.concare.models;

public class Orders {

    private String address;
    private String campus;
    private String telephone_Number;
    private String con_type;

    //default constructor
    public Orders(){ }

    //constructor with one or more parameters
    public Orders(String address, String campus, String telephone_Number,String con_type){
        this.address = address;
        this.campus = campus;
        this.telephone_Number = telephone_Number;
        this.con_type = con_type;
    }

    //Getter and Setter Method for address
    public void setAddress(String address){
        this.address = address;
    }

    public String getAddress(){
        return address;
    }

    //Getter and Setter Method for campus
    public void setCampus(String campus){
        this.campus = campus;
    }
    public String getCampus(){
        return campus;
    }

    //Getter and Setter Method for telephone number
    public void setTelephone_Number(String telephone_Number){
        this.telephone_Number = telephone_Number;
    }
    public String getTelephone_Number(){
        return telephone_Number;
    }

    //Getter and Setter Method for contraceptive type
    public void setCon_type(String con_type){
        this.con_type = con_type;
    }
    public String getCon_type(){
        return con_type;
    }
}
