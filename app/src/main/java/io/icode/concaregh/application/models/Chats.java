package io.icode.concaregh.application.models;

public class Chats {

    //Users users = new Users();

    private String uid;
    private String username;
    private String imageUrl;

    public Chats(){}

    public Chats(String uid, String username,String imageUrl){
        this.uid = uid;
        this.username = username;
        this.imageUrl = imageUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
