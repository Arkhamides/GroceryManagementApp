package com.joearchondis.grocerymanagement1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddInventoryItemActivity extends AppCompatActivity{

    private static final String TAG = "AddInventoryItemActivity";

    DatabaseHelper mDatabaseHelper;

    EditText ed_txt_ProductName,ed_txt_Price,ed_txt_Quantity,ed_txt_Subtotal,ed_txt_BrandName,ed_txt_Calories;
    Button b1, btnView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_inventory_item);
        mDatabaseHelper = new DatabaseHelper(this);

        findViews();

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newEntry = ed_txt_ProductName.getText().toString();
                if(ed_txt_ProductName.length() > 0) {
                    addItem();
                } else {
                    String s = mDatabaseHelper.getItemID("asdafsf");
                    toastMessage(s);
                }

            }
        });

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddInventoryItemActivity.this, ListItemsActivity.class);
                startActivity(intent);
            }
        });

    }

    /////////////FUNCTIONS//////////////

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Sets EditText ed_txt_ProductName, ed_txt_Price, ed_txt_Quantity, ed_txt_Subtotal
     * Sets Button b1, btnView
     */
    public void findViews() {
        ed_txt_ProductName = findViewById(R.id.i_txt_ProductName);
        ed_txt_BrandName = findViewById(R.id.i_txt_BrandName);
        ed_txt_Price = findViewById(R.id.i_txt_Price);
        ed_txt_Quantity = findViewById(R.id.i_txt_Quantity);
        ed_txt_Subtotal = findViewById(R.id.txt_Subtotal);
        ed_txt_Calories = findViewById(R.id.i_txt_Calories);

        b1 = findViewById(R.id.btn_add);
        btnView = findViewById(R.id.btn_ViewData);
    }

    /**
     * Adds prodName, brandName to the local DB
     */
    public void addItem(){
        int price;
        int quantity;
        String prodName;
        String brandName;

        price = Integer.parseInt(ed_txt_Price.getText().toString());
        quantity = Integer.parseInt(ed_txt_Quantity.getText().toString());

        prodName = ed_txt_ProductName.getText().toString();
        brandName = ed_txt_BrandName.getText().toString();


        ////////////////adds to database////////////////////
        boolean insertData;
        if(brandName.length()>0) {
            insertData = mDatabaseHelper.addItem(prodName,brandName);
        } else {
            insertData = mDatabaseHelper.addItem(prodName);
        }

        if(insertData) {
            toastMessage(prodName + " Item successfully inserted");
        } else {
            toastMessage("Error adding prodName");
        }
        ///////////////////////////////////////////////////

        refresh_data_Table(); //refreshes data table
    }

    public void refresh_data_Table(){

        ed_txt_ProductName.setText("");
        ed_txt_Price.setText("");
        ed_txt_Quantity.setText("");
        ed_txt_Subtotal.setText("");
        ed_txt_BrandName.setText("");
        ed_txt_Calories.setText("");
        ed_txt_ProductName.requestFocus();

    }

}


