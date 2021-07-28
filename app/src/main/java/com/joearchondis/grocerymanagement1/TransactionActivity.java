package com.joearchondis.grocerymanagement1;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class TransactionActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private static final String TAG = "TransactionActivity";
    DatabaseHelper mDatabaseHelper;

    String userID;
    String str_newQuantity;

    String str_measurement = "Unit(s)";
    int TransactionNB;

    String inventoryName = "Default";

    InventoryItem SelectedInventoryItem;
    String ProductName, BrandName, Price, min_quantity, Calories;

    TextView ed_txt_ProductName,ed_txt_Price,ed_txt_Quantity,ed_txt_BrandName,ed_txt_Calories, ed_txt_min_quantity, ed_txt_Subtotal;
    Button btnEdit, btnDelete, btnIN, btnOUT;
    EditText ed_transactionNB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        mDatabaseHelper = new DatabaseHelper(this);

        findViews();
        getInventoryItemServer();
        setViews();

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransactionActivity.this, EditItemActivity.class);
                startActivity(intent);

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteInvItemServer();
                Intent intent = new Intent(TransactionActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addINTransaction();
            }
        });

        btnOUT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOUTTransaction();
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
        ed_txt_Subtotal = findViewById(R.id.i_txt_Subtotal);

        btnEdit = findViewById(R.id.btn_edit);
        btnDelete = findViewById(R.id.btn_delete);
        btnIN = findViewById(R.id.btn_In);
        btnOUT = findViewById(R.id.btn_Out);


        ed_transactionNB = findViewById(R.id.EditTxt_number);
    }

    public void setSelectedInventoryItemLocal() {

        SelectedInventoryItem = ((MyApplication) getApplication()).getSelectedInventoryItem();

        ProductName = SelectedInventoryItem.name;
        BrandName = SelectedInventoryItem.brand;

        Cursor data = mDatabaseHelper.getInventoryItem("default", ProductName, BrandName);

        while(data.moveToNext()) {
            SelectedInventoryItem.name = data.getString(10);
            SelectedInventoryItem.brand = data.getString(12);
            SelectedInventoryItem.price = data.getString(5);
            SelectedInventoryItem.calories = data.getString(6);
            SelectedInventoryItem.min_quantity = data.getString(7);
            SelectedInventoryItem.quantity = data.getString(3);
            SelectedInventoryItem.measurementLabel = data.getString(4);
        }

        ed_txt_ProductName.setText(SelectedInventoryItem.name);
        ed_txt_BrandName.setText(SelectedInventoryItem.brand);
        ed_txt_Price.setText(SelectedInventoryItem.price);
        ed_txt_Quantity.setText(SelectedInventoryItem.quantity);
        ed_txt_Calories.setText(SelectedInventoryItem.calories);
        ed_txt_min_quantity.setText(SelectedInventoryItem.min_quantity);

    }

    public void setViews() {
        ed_txt_ProductName.setText(SelectedInventoryItem.name);
        ed_txt_BrandName.setText(SelectedInventoryItem.brand);
        ed_txt_Price.setText(SelectedInventoryItem.price);
        ed_txt_Quantity.setText(SelectedInventoryItem.quantity);
        ed_txt_Calories.setText(SelectedInventoryItem.calories);
        ed_txt_min_quantity.setText(SelectedInventoryItem.min_quantity);

        int Subtotal = Integer.parseInt(SelectedInventoryItem.price) * Integer.parseInt(SelectedInventoryItem.quantity);
        String str_Subtotal = String.valueOf(Subtotal);
        ed_txt_Subtotal.setText(str_Subtotal);
    }

    public void deleteInvItem() {

        mDatabaseHelper.deleteInventoryItem(SelectedInventoryItem.name, SelectedInventoryItem.brand);

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
                        }

                    }
                }
            }
        });

    }

    public void addINTransaction() {

        TransactionNB = Integer.parseInt(ed_transactionNB.getText().toString());
        userID = ((MyApplication) getApplication()).getSelectedUser().UserID;

        if(TransactionNB < 0){
            Toast.makeText(this, "Number must be positive", Toast.LENGTH_SHORT).show();
        } else {


            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {

                    String str_transaction = String.valueOf(TransactionNB);

                    String[] field = new String[4];
                    field[0] = "itemName";
                    field[1] = "brandName";
                    field[2] = "userID";
                    field[3] = "quantity";
                    //Creating array for data
                    String[] data = new String[4];
                    data[0] = SelectedInventoryItem.name;
                    data[1] = SelectedInventoryItem.brand;
                    data[2] = userID;
                    data[3] = str_transaction;

                    String ip = ((MyApplication) getApplication()).getIP();
                    PutData putData = new PutData("http://"+ip+"/GroceryManagementApp/AddTransaction.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {

                            String result = putData.getResult();
                            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                            resetandUpdate();

                        }
                    }
                }
            });


        }
    }

    public void addOUTTransaction() {
        TransactionNB = Integer.parseInt(ed_transactionNB.getText().toString());
        userID = ((MyApplication) getApplication()).getSelectedUser().UserID;


        if(TransactionNB < 0){
            Toast.makeText(this, "Number must be positive", Toast.LENGTH_SHORT).show();
        } else {

            TransactionNB = -TransactionNB;

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
                    data[0] = SelectedInventoryItem.name;
                    data[1] = SelectedInventoryItem.brand;
                    data[2] = userID;
                    data[3] = String.valueOf(TransactionNB);

                    PutData putData = new PutData("http://192.168.0.106/GroceryManagementApp/AddTransaction.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {

                            String result = putData.getResult();
                            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                            resetandUpdate();

                        }
                    }
                }
            });


        }

    }

    public void resetandUpdate() {

        ed_transactionNB.setText("0");

        int oldQuantity = Integer.parseInt(ed_txt_Quantity.getText().toString());
        int price = Integer.parseInt(ed_txt_Price.getText().toString());

        int newQuantity = oldQuantity + TransactionNB;
        int newSubTotal = price*newQuantity;

        ed_txt_Subtotal.setText(String.valueOf(newSubTotal));

        str_newQuantity = String.valueOf(newQuantity);
        ed_txt_Quantity.setText(str_newQuantity);



        updateQuantity();

    }

    public void updateQuantity() {

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
                data[0] = SelectedInventoryItem.name;
                data[1] = SelectedInventoryItem.brand;
                data[2] = userID;
                data[3] = str_newQuantity;

                String ip = ((MyApplication) getApplication()).getIP();
                PutData putData = new PutData("http://"+ip+"/GroceryManagementApp/UpdateInventoryItemQuantity.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {

                        String result = putData.getResult();

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




    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        str_measurement = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        str_measurement = "Unit(s)";
    }




}
