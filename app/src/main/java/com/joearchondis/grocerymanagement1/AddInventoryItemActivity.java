package com.joearchondis.grocerymanagement1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
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

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.Calendar;
import java.util.Date;

public class AddInventoryItemActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "AddInventoryItemActivity";

    DatabaseHelper mDatabaseHelper;

    String inventoryName = "Default";
    String str_measurement = "Unit(s)";
    String measurement_index = "1";
    String str_selectedDate;
    Calendar selectedDate;

    String Name, Brand, Quantity;


    EditText ed_txt_ProductName,ed_txt_Price,ed_txt_Quantity,ed_txt_BrandName,ed_txt_Calories, ed_txt_min_quantity;
    Button btnAdd, btnView;
    User currentUser;

    TextView mDisplayDate;
    DatePickerDialog.OnDateSetListener mDateSetListener;

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

        currentUser = ((MyApplication) getApplication()).getSelectedUser();

        findViews();



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

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addServer();
                //button_add();
            }
        });

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddInventoryItemActivity.this, ListInventoryItemsActivity.class);
                startActivity(intent);
                finish();
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

    public void findViews() {
        ed_txt_ProductName = findViewById(R.id.i_txt_ProductName);
        ed_txt_BrandName = findViewById(R.id.i_txt_BrandName);
        ed_txt_Price = findViewById(R.id.i_txt_Price);
        ed_txt_Quantity = findViewById(R.id.i_txt_Quantity);
        ed_txt_Calories = findViewById(R.id.i_txt_Calories);
        ed_txt_min_quantity = findViewById(R.id.i_txt_MinQuantity);

        btnAdd = findViewById(R.id.btn_add);
        btnView = findViewById(R.id.btn_ViewData);

        mDisplayDate = (TextView) findViewById(R.id.tvDate);
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
        min_quantity = ed_txt_min_quantity.getText().toString();


        boolean insertInventoryItem;

        ////////////////adds record to InventoryItems table////////////////////

        insertInventoryItem = mDatabaseHelper.addInventoryItem(prodName, brandName, str_measurement, price, calories,
                quantity, min_quantity);

        if(str_selectedDate != "") {
            mDatabaseHelper.InsertExpiryDateToInventoryItem(prodName, brandName, selectedDate);
        }

        if(insertInventoryItem) {
            toastMessage("Item successfully inserted to the inventory");
        } else {
            toastMessage("Error adding InventoryItem");
        }
        ////////////////////////////////////////////////////////////////////////

        //Adds InTransaction
        String inventoryItemID = mDatabaseHelper.getInventoryItemID(prodName,brandName);
        addInTransaction(inventoryItemID);

        refresh_data_Table(); //refreshes data table
    }

    public void addInTransaction(String inventoryItemID){
        String price, quantity;
        price = ed_txt_Price.getText().toString();
        quantity = ed_txt_Quantity.getText().toString();

        boolean insertInTransaction;

        insertInTransaction = mDatabaseHelper.addInTransaction(inventoryItemID, quantity, price, "date", "expiry_date");
        if(!insertInTransaction) {
            toastMessage("Error adding InTransaction");
        }

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

    public void SelectCalendar() {

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                AddInventoryItemActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year, month, day);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
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

    public void addINTransaction() {

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {


                    String[] field = new String[4];
                    field[0] = "itemName";
                    field[1] = "brandName";
                    field[2] = "userID";
                    field[3] = "quantity";
                    //Creating array for data
                    String[] data = new String[4];
                    data[0] = Name;
                    data[1] = Brand;
                    data[2] = currentUser.getID();
                    data[3] = Quantity;

                    String ip = ((MyApplication) getApplication()).getIP();
                    PutData putData = new PutData("http://"+ip+"/GroceryManagementApp/AddTransaction.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {

                            String result = putData.getResult();
                            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            });



    }


    public void addServer() {

        final String itemName, brandName, price, calories, quantity, min_quantity;

        itemName = ed_txt_ProductName.getText().toString();
        brandName = ed_txt_BrandName.getText().toString();
        price = ed_txt_Price.getText().toString();
        calories = ed_txt_Calories.getText().toString();
        quantity = ed_txt_Quantity.getText().toString();
        min_quantity = ed_txt_min_quantity.getText().toString();
        Name = itemName;
        Brand = brandName;
        Quantity = quantity;

        String[] field = new String[8];
        field[0] = "itemName";
        field[1] = "brandName";
        field[2] = "measurementLabel";
        field[3] = "price";
        field[4] = "calories";
        field[5] = "quantity";
        field[6] = "min_quantity";
        field[7] = "userID";

        //Creating array for data
        String[] data = new String[8];
        data[0] = itemName;
        data[1] = brandName;
        data[2] = measurement_index;
        data[3] = price;
        data[4] = calories;
        data[5] = quantity;
        data[6] = min_quantity;
        data[7] = currentUser.UserID;

        String ip = ((MyApplication) getApplication()).getIP();
        PutData putData = new PutData("http://"+ip+"/GroceryManagementApp/AddInventoryItem.php", "POST", field, data);
        if (putData.startPut()) {
            if (putData.onComplete()) {

                String result = putData.getResult();

                if(result.equals("1true")) {
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                    addINTransaction();
                    refresh_data_Table();
                } else {
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                }

            }
        }

        if(str_selectedDate != "" && str_selectedDate!=null) {
            UpdateExpDate(currentUser.UserID, itemName, brandName);
        }




    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        str_measurement = parent.getItemAtPosition(position).toString();
        measurement_index = String.valueOf(position+1);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        str_measurement = "Unit(s)";
    }

}


