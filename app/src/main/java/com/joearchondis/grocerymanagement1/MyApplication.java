package com.joearchondis.grocerymanagement1;

import android.app.Application;

public class MyApplication extends Application {

    private String selectedInventoryItem;
    private String selectedItem;
    private String selectedBrand;

    public String getSelectedInventoryItem() {
        return selectedInventoryItem;
    }

    public void setSelectedInventoryItem(String someVariable) {
        this.selectedInventoryItem = someVariable;
    }

    public String getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(String someVariable) {
        this.selectedItem = someVariable;
    }



}