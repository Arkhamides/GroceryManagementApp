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
    String str_measurement = "Unit(s)";

    EditText ed_txt_ProductName,ed_txt_Price,ed_txt_Quantity,ed_txt_BrandName,ed_txt_Calories, ed_txt_min_quantity;
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
                button_add();
            }
        });

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddInventoryItemActivity.this, ListInventoryItemsActivity.class);
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
        ed_txt_min_quantity = findViewById(R.id.i_txt_MinQuantity);

        btnAdd = findViewById(R.id.btn_add);
        btnView = findViewById(R.id.btn_ViewData);
    }

    /**
     * Adds prodName, brandName to the local DB
     */
    public void addInventoryItem(){

        String prodName, brandName, price, calories, quantity, min_quantity;

        prodName = ed_txt_ProductName.getText().toString();
        brandName = ed_txt_BrandName.getText().toString();
        price = ed_txt_Price.getText().toString();
        calories = ed_txt_Calories.getText().toString();
        quantity = ed_txt_Quantity.getText().toString();
        min_quantity = ed_txt_Quantity.getText().toString();

        //TODO UI for adding. if no quantity selected then quantity = 0

        boolean insertInventoryItem;

        ////////////////adds Inventory Item to database////////////////////

        insertInventoryItem = mDatabaseHelper.addInventoryItem(prodName, brandName, str_measurement, price, calories,
                quantity, min_quantity);

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
        ed_txt_min_quantity.setText("");
        ed_txt_ProductName.requestFocus();

    }

    public void button_add() {

        if(ed_txt_ProductName.length() > 0) {

            String prodName = ed_txt_ProductName.getText().toString();
            String brandName = ed_txt_BrandName.getText().toString();

            String InventoryID =  mDatabaseHelper.getInventoryItemID(prodName, brandName);

            if(InventoryID != "-1") {
                toastMessage("Item already exists in inventory");
            } else {
                addInventoryItem();
            }

        } else {
            toastMessage("Wrong inputs");
        }

    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        str_measurement = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        str_measurement = "Unit(s)";
    }

}


