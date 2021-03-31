package com.joearchondis.grocerymanagement1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddInventoryItemActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "AddInventoryItemActivity";

    DatabaseHelper mDatabaseHelper;

    String inventoryName = "Default";
    String str_measurement = "";

    EditText ed_txt_ProductName,ed_txt_Price,ed_txt_Quantity,ed_txt_BrandName,ed_txt_Calories;
    Button btnAdd, btnView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_inventory_item);
        mDatabaseHelper = new DatabaseHelper(this);

        Spinner spinner_measurements = findViewById(R.id.spinner_measurement);
        ArrayAdapter<CharSequence> adapter_filterBy = ArrayAdapter.createFromResource(this,R.array.measurements, android.R.layout.simple_spinner_item);
        adapter_filterBy.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_measurements.setAdapter(adapter_filterBy);
        spinner_measurements.setOnItemSelectedListener(this);

        findViews();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ed_txt_ProductName.length() > 0) {
                    addInventoryItem();
                } else {
                    toastMessage("Wrong inputs");
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
     * Sets Button btnAdd, btnView
     */
    public void findViews() {
        ed_txt_ProductName = findViewById(R.id.i_txt_ProductName);
        ed_txt_BrandName = findViewById(R.id.i_txt_BrandName);
        ed_txt_Price = findViewById(R.id.i_txt_Price);
        ed_txt_Quantity = findViewById(R.id.i_txt_Quantity);
        ed_txt_Calories = findViewById(R.id.i_txt_Calories);

        btnAdd = findViewById(R.id.btn_add);
        btnView = findViewById(R.id.btn_ViewData);
    }

    /**
     * Adds prodName, brandName to the local DB
     */
    public void addInventoryItem(){

        String prodName;
        String brandName;

        int price;
        int calories;
        int quantity;
        String measurementLabel;

        prodName = ed_txt_ProductName.getText().toString();
        brandName = ed_txt_BrandName.getText().toString();

        price = Integer.parseInt(ed_txt_Price.getText().toString());
        calories = Integer.parseInt(ed_txt_Calories.getText().toString());
        quantity = Integer.parseInt(ed_txt_Quantity.getText().toString());

        boolean insertInventoryItem;

        ////////////////adds Inventory Item to database////////////////////

        insertInventoryItem = mDatabaseHelper.addInventoryItem(prodName, brandName, inventoryName, Integer.toString(quantity));

        if(insertInventoryItem) {
            toastMessage("Item successfully inserted to the inventory");
        } else {
            toastMessage("Error adding InventoryItem");
        }
        ///////////////////////////////////////////////////

        refresh_data_Table(); //refreshes data table
    }

    public void refresh_data_Table(){

        ed_txt_ProductName.setText("");
        ed_txt_Price.setText("");
        ed_txt_Quantity.setText("");
        ed_txt_BrandName.setText("");
        ed_txt_Calories.setText("");
        ed_txt_ProductName.requestFocus();

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        str_measurement = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(),str_measurement,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        str_measurement = "";
    }

}


