package io.icode.concaregh.application.models;

public class Admin {

    //field in the database
    private String email;
    private String username;
    private String adminUid;
    private String gender;
    private String phoneNumber;
    private String imageUrl;


    //default constructor
    public Admin(){}

    //constructor with one or more parameters
    public Admin(String email,String username, String adminUid, String gender,String phoneNumber, String imageUrl){
        this.email = email;
        this.username = username;
        this.adminUid = adminUid;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.imageUrl = imageUrl;

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

    //Getter and Setter Method for uid
    public String getAdminUid() {
        return adminUid;
    }

    public void setAdminUid(String adminUid) {
        this.adminUid = adminUid;
    }

    //Getter and Setter Method for Gender
    public void setGender(String gender){
        this.gender = gender;
    }

    public String getGender(){
        return gender;
    }

    //Getter and Setter Method for TelephoneNumber
    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }

    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public String getImageUrl(){
        return imageUrl;
    }

}
