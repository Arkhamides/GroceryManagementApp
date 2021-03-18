package com.joearchondis.grocerymanagement1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    DatabaseHelper mDatabaseHelper;

    private ArrayList<String> data_prodName = new ArrayList<String>();
    private ArrayList<String> data_brandName = new ArrayList<String>();
    private ArrayList<String> data_price = new ArrayList<String>();
    private ArrayList<String> data_quantity = new ArrayList<String>();
    private ArrayList<String> data_subTotal = new ArrayList<String>();

    private TableLayout table;

    EditText ed_txt_ProductName,ed_txt_Price,ed_txt_Quantity,ed_txt_Subtotal,ed_txt_BrandName,ed_txt_Calories;
    Button b1, btnView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                Intent intent = new Intent(MainActivity.this, ListDataActivity.class);
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
        int subTotal;
        int price;
        int quantity;
        String prodName;
        String brandName;

        price = Integer.parseInt(ed_txt_Price.getText().toString());
        quantity = Integer.parseInt(ed_txt_Quantity.getText().toString());
        subTotal = price * quantity;

        prodName = ed_txt_ProductName.getText().toString();
        brandName = ed_txt_BrandName.getText().toString();

        data_prodName.add(prodName);
        data_brandName.add(brandName);
        data_price.add(String.valueOf(price));
        data_quantity.add(String.valueOf(quantity));
        data_subTotal.add(String.valueOf(subTotal));

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

        TableLayout table = (TableLayout) findViewById(R.id.tbl_tbl1);
        TableRow row = new TableRow(this);
        TextView t1 = new TextView(this);
        TextView t2 = new TextView(this);
        TextView t3 = new TextView(this);
        TextView t4 = new TextView(this);

        int subTotal = 0;

        // calculates subtotal and adds items to t1, t2, t3, t4.
        for(int i =0; i < data_prodName.size(); i++) {
            String data1 = data_prodName.get(i);
            String data2 = data_price.get(i);
            String data3 = data_quantity.get(i);
            String data4 = data_subTotal.get(i);

            t1.setText(data1);
            t2.setText(data2);
            t3.setText(data3);
            t4.setText(data4);

            subTotal = subTotal + Integer.parseInt(data2)*Integer.parseInt(data3);
        }
        ///////////////////////////////////////////////////////////////

        row.addView(t1);
        row.addView(t2);
        row.addView(t3);
        row.addView(t4);
        table.addView(row);

        ed_txt_ProductName.setText("");
        ed_txt_Price.setText("");
        ed_txt_Quantity.setText("");
        ed_txt_Subtotal.setText(String.valueOf(subTotal));
        ed_txt_BrandName.setText("");
        ed_txt_Calories.setText("");
        ed_txt_ProductName.requestFocus();

    }

}