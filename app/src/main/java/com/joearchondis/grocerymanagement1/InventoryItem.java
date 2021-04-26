package com.joearchondis.grocerymanagement1;

public class InventoryItem {

    public String name;
    public String brand;
    public String quantity;
    public String price;
    public String calories;
    public String min_quantity;
    public String measurementLabel;

    public InventoryItem(String name, String brand) {
        this.name = name;
        this.brand = brand;
    }

    public InventoryItem(String name, String brand, String quantity,String price,String calories,String min_quantity, String measurementLabel) {
        this.name = name;
        this.brand = brand;
        this.quantity = quantity;
        this.price = price;
        this.calories = calories;
        this.min_quantity = min_quantity;
        this.measurementLabel = measurementLabel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
