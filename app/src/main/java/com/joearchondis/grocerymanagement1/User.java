package com.joearchondis.grocerymanagement1;

public class User {
    public String UserID;
    public String userName;
    public String email;

    public User(String UserID, String userName, String email) {
        this.UserID = UserID;
        this.userName = userName;
        this.email = email;
    }

    public String getID() {
        return UserID;
    }

    public void setID(String UserID) {
        this.UserID = UserID;
    }

    public String getUsername() {
        return userName;
    }

    public void setUsername(String userName) {
        this.userName = userName;
    }
}
