package com.joearchondis.grocerymanagement1;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.HashMap;

public class EditItemActivity  extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private static final String TAG = "EditItemActivity";
    DatabaseHelper mDatabaseHelper;

    String str_measurement = "Unit(s)";

    String inventoryName = "Default";

    InventoryItem SelectedInventoryItem;
    String ProductName, BrandName, Price, min_quantity, Calories;

    EditText ed_txt_ProductName,ed_txt_Price,ed_txt_Quantity,ed_txt_BrandName,ed_txt_Calories, ed_txt_min_quantity;
    Button btnSave, btnDelete;

    Spinner spinner_measurements;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_inventory_item);
        mDatabaseHelper = new DatabaseHelper(this);

        spinner_measurements = findViewById(R.id.spinner_measurement);
        ArrayAdapter<CharSequence> adapter_filterBy = ArrayAdapter.createFromResource(this,R.array.measurements, android.R.layout.simple_spinner_item);
        adapter_filterBy.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_measurements.setAdapter(adapter_filterBy);
        spinner_measurements.setOnItemSelectedListener(this);

        findViews();
        setViews();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SaveEdit();
                SaveEditServer();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteInvItem();
            }
        });

    }


    public void findViews() {
        ed_txt_ProductName = findViewById(R.id.i_txt_ProductName);
        ed_txt_BrandName = findViewById(R.id.i_txt_BrandName);
        ed_txt_Price = findViewById(R.id.i_txt_Price);
        ed_txt_Quantity = findViewById(R.id.i_txt_Quantity);
        ed_txt_Calories = findViewById(R.id.i_txt_Calories);
        ed_txt_min_quantity = findViewById(R.id.i_txt_MinQuantity);

        btnSave = findViewById(R.id.btn_save);
        btnDelete = findViewById(R.id.btn_delete);


    }

    public void setViews() {

        SelectedInventoryItem = ((MyApplication) getApplication()).getSelectedInventoryItem();

        ed_txt_ProductName.setText(SelectedInventoryItem.name);
        ed_txt_BrandName.setText(SelectedInventoryItem.brand);
        ed_txt_Price.setText(SelectedInventoryItem.price);
        ed_txt_Quantity.setText(SelectedInventoryItem.quantity);
        ed_txt_Calories.setText(SelectedInventoryItem.calories);
        ed_txt_min_quantity.setText(SelectedInventoryItem.min_quantity);

        spinner_measurements.setSelection(Integer.parseInt(SelectedInventoryItem.measurementLabel));

    }

    public void SaveEdit() {

        String oldName = SelectedInventoryItem.name;
        String oldBrand = SelectedInventoryItem.brand;
        String newName = ed_txt_ProductName.getText().toString();
        String newBrand = ed_txt_BrandName.getText().toString();

        String newCalories = ed_txt_Calories.getText().toString();
        String newQuantity = ed_txt_Quantity.getText().toString();
        String newPrice = ed_txt_Price.getText().toString();
        String newMeasurement = str_measurement;
        String newMinQty = ed_txt_min_quantity.getText().toString();


        mDatabaseHelper.updateInventoryItemRows(oldName,  oldBrand,  newCalories, newQuantity,  newPrice,  newMeasurement ,  newMinQty);
        mDatabaseHelper.updateInventoryItem( oldName,  oldBrand,  newName,  newBrand);

        SelectedInventoryItem.name = newName;
        SelectedInventoryItem.brand = newBrand;

        ((MyApplication) getApplication()).setSelectedInventoryItem(SelectedInventoryItem);

        Intent intent = new Intent(EditItemActivity.this, ListInventoryItemsActivity.class);
        startActivity(intent);
        finish();

    }

    public void deleteInvItem() {

        mDatabaseHelper.deleteInventoryItem(SelectedInventoryItem.name, SelectedInventoryItem.brand);

    }

    public void SaveEditServer() {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {

                String oldName = SelectedInventoryItem.name;
                String oldBrand = SelectedInventoryItem.brand;
                String userID = ((MyApplication) getApplication()).getSelectedUser().UserID;

                String newName = ed_txt_ProductName.getText().toString();
                String newBrand = ed_txt_BrandName.getText().toString();
                String newCalories = ed_txt_Calories.getText().toString();
                String newQuantity = ed_txt_Quantity.getText().toString();
                String newPrice = ed_txt_Price.getText().toString();
                String newMeasurement = String.valueOf(spinner_measurements.getSelectedItemPosition()+1);
                String newMinQty = ed_txt_min_quantity.getText().toString();

                String[] field = new String[10];
                field[0] = "userID";
                field[1] = "oldName";
                field[2] = "oldBrand";
                field[3] = "newName";
                field[4] = "newBrand";
                field[5] = "newCalories";
                field[6] = "newQuantity";
                field[7] = "newPrice";
                field[8] = "newMeasurement";
                field[9] = "newMinQty";
                //Creating array for data
                String[] data = new String[10];
                data[0] = userID;
                data[1] = oldName;
                data[2] = oldBrand;
                data[3] = newName;
                data[4] = newBrand;
                data[5] = newCalories;
                data[6] = newQuantity;
                data[7] = newPrice;
                data[8] = newMeasurement;
                data[9] = newMinQty;

                PutData putData = new PutData("http://192.168.0.106/GroceryManagementApp/EditInventoryItem.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {

                        String result = putData.getResult();

                        Intent intent = new Intent(EditItemActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }
            }
        });

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
