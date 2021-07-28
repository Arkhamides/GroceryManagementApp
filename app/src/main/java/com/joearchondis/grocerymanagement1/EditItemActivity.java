package com.joearchondis.grocerymanagement1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.Calendar;
import java.util.HashMap;

public class EditItemActivity  extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private static final String TAG = "EditItemActivity";
    DatabaseHelper mDatabaseHelper;

    String str_selectedDate;
    Calendar selectedDate;
    String serverIP = ((MyApplication) getApplication()).getIP();

    String str_measurement = "Unit(s)";
    String userID;

    String inventoryName = "Default";

    InventoryItem SelectedInventoryItem;
    String ProductName, BrandName, Price, min_quantity, Calories;

    EditText ed_txt_ProductName,ed_txt_Price,ed_txt_Quantity,ed_txt_BrandName,ed_txt_Calories, ed_txt_min_quantity;
    Button btnSave, btnDelete;

    Spinner spinner_measurements;
    TextView mDisplayDate;
    DatePickerDialog.OnDateSetListener mDateSetListener;


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
        getInventoryItemServer();
        setViews();


        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectCalendar();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                month = month+1;
                str_selectedDate = year + "/" + dayOfMonth + "/" + month;
                mDisplayDate.setText(str_selectedDate);

                selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth, 0, 0);

            }
        };

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
                DeleteInvItemServer();
                Intent intent = new Intent(EditItemActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
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

        mDisplayDate = (TextView) findViewById(R.id.tvDate);

    }

    public void setViews() {

        SelectedInventoryItem = ((MyApplication) getApplication()).getSelectedInventoryItem();

        ed_txt_ProductName.setText(SelectedInventoryItem.name);
        ed_txt_BrandName.setText(SelectedInventoryItem.brand);
        ed_txt_Price.setText(SelectedInventoryItem.price);
        ed_txt_Quantity.setText(SelectedInventoryItem.quantity);
        ed_txt_Calories.setText(SelectedInventoryItem.calories);
        ed_txt_min_quantity.setText(SelectedInventoryItem.min_quantity);

        mDisplayDate.setText(SelectedInventoryItem.exp_date);


        spinner_measurements.setSelection(Integer.parseInt(SelectedInventoryItem.measurementLabel)-1);

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


                PutData putData = new PutData("http://"+ serverIP +"/GroceryManagementApp/EditInventoryItem.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {

                        String result = putData.getResult();

                        Intent intent = new Intent(EditItemActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }

                if(str_selectedDate != "" && str_selectedDate!=null) {
                    UpdateExpDate(userID, newName, newBrand);
                }


            }

        });

    }

    public void SelectCalendar() {

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                EditItemActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year, month, day);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void getInventoryItemServer() {

        SelectedInventoryItem = ((MyApplication) getApplication()).getSelectedInventoryItem();
        userID = ((MyApplication) getApplication()).getSelectedUser().UserID;

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {

                String[] field = new String[3];
                field[0] = "itemName";
                field[1] = "brandName";
                field[2] = "userID";
                //Creating array for data
                String[] data = new String[3];
                data[0] = SelectedInventoryItem.name;
                data[1] = SelectedInventoryItem.brand;
                data[2] = userID;

                String ip = ((MyApplication) getApplication()).getIP();
                PutData putData = new PutData("http://"+ip+"/GroceryManagementApp/getInventoryItem.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {

                        String result = putData.getResult();

                        if(result.equals("-1")) {
                            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                        }else {

                            String results[] = result.split(",", -2);
                            SelectedInventoryItem = new InventoryItem(results[0],results[1],results[2],results[3],results[4],results[5],results[6], results[7]);
                            ((MyApplication) getApplication()).setSelectedInventoryItem(SelectedInventoryItem);
                            mDisplayDate.setText(SelectedInventoryItem.exp_date);

                        }

                    }
                }
            }
        });



    }


    public void DeleteInvItemServer() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {

                String name = SelectedInventoryItem.name;
                String brand = SelectedInventoryItem.brand;
                String userID = ((MyApplication) getApplication()).getSelectedUser().UserID;

                String[] field = new String[3];
                field[0] = "itemName";
                field[1] = "brandName";
                field[2] = "userID";
                //Creating array for data
                String[] data = new String[3];
                data[0] = name;
                data[1] = brand;
                data[2] = userID;

                String ip = ((MyApplication) getApplication()).getIP();
                PutData putData = new PutData("http://"+ip+"/GroceryManagementApp/DeleteInvItem.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {

                        String result = putData.getResult();

                    }
                }


            }

        });

    }


    public void UpdateExpDate(String userID, String itemName, String brandName) {
        if(str_selectedDate == "") {
            return;
        }

        int year = selectedDate.get(Calendar.YEAR);
        int month = selectedDate.get(Calendar.MONTH);
        int dayOfMonth = selectedDate.get(Calendar.DAY_OF_MONTH);


        String[] field = new String[6];
        field[0] = "year";
        field[1] = "month";
        field[2] = "dayOfMonth";
        field[3] = "itemName";
        field[4] = "brandName";
        field[5] = "userID";

        //Creating array for data
        String[] data = new String[6];
        data[0] = String.valueOf(year);
        data[1] = String.valueOf(month);
        data[2] = String.valueOf(dayOfMonth);
        data[3] = itemName;
        data[4] = brandName;
        data[5] = userID;

        String ip = ((MyApplication) getApplication()).getIP();
        PutData putData = new PutData("http://"+ip+"/GroceryManagementApp/UpdateExpDate.php", "POST", field, data);

        if (putData.startPut()) {
            if (putData.onComplete()) {

                String result = putData.getResult();
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();

            }
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
