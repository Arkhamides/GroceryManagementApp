package com.joearchondis.grocerymanagement1;

import android.app.Application;

public class MyApplication extends Application {

    private String selectedItem;
    private InventoryItem selectedInventoryItem;
    private User currentUser;
    private String serverIP = "192.168.0.103";

    public InventoryItem getSelectedInventoryItem() {
        return selectedInventoryItem;
    }

    public void setSelectedInventoryItem( InventoryItem someInvItem) {
        this.selectedInventoryItem = someInvItem;
    }

    public String getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(String someVariable) {
        this.selectedItem = someVariable;
    }

    public User getSelectedUser() {
        return currentUser;
    }

    public void setSelectedUser(User user){
        currentUser = user;
    }

    public String getIP(){
        return serverIP;
    }



}