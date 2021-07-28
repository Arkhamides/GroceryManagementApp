
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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.vishnusivadas.advanced_httpurlconnection.PutData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListGroceryList extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "ListInventoryItemsActivity";

    DatabaseHelper mDatabaseHelper;

    ListView mListView;
    Button btn_add_inventory_item_activity, btn_search;
    EditText ed_search;

    ArrayList<InventoryItem> InventoryItemsList = new ArrayList<>();

    String str_filter;
    User currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.brands_layout);

        currentUser = ((MyApplication) getApplication()).getSelectedUser();

        mListView =(ListView)findViewById(R.id.ListView_Brands);

        mDatabaseHelper = new DatabaseHelper(this);

        getInventoryListServer();


    }

    // Gets Inventory Items data from local database and populates ListView.

    private void getInventoryListServer() {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {

                String[] field = new String[1];
                field[0] = "userID";

                String[] data = new String[1];
                data[0] = currentUser.getID();


                String ip = ((MyApplication) getApplication()).getIP();
                PutData putData = new PutData("http://"+ip+"/GroceryManagementApp/getGroceryList.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {

                        String result = putData.getResult();

                        if(result.equals("-1")) {
                            Toast.makeText(getApplicationContext(), "No items found", Toast.LENGTH_SHORT).show();
                        }else {

                            String results[] = result.split(",", -2);

                            List<HashMap<String,String>> aList = new ArrayList<HashMap<String, String>>();
                            int i = 0;
                            String name,brand, quantity, price, calories, min_quantity,  measurementLabel, expiryDate;
                            InventoryItemsList = new ArrayList<>();

                            while(results.length > i) {
                                name = results[i];
                                i++;
                                brand = results[i];
                                i++;
                                quantity = results[i];
                                i++;
                                price = results[i];
                                i++;
                                calories = results[i];
                                i++;
                                min_quantity = results[i];
                                i++;
                                expiryDate = results[i];
                                i++;

                                InventoryItem i1 = new InventoryItem(name,brand, quantity,price, calories, min_quantity, "0");
                                InventoryItemsList.add(i1);

                                HashMap<String, String> hm = new HashMap<String,String>();
                                hm.put("ListName", "Item: " + name);
                                hm.put("ListBrand", "Brand: " + brand);
                                hm.put("Quantity", "Quantity: " + quantity);
                                hm.put("MinQuantity", "MinQty: " + min_quantity);
                                hm.put("Description", "Expiry Date" + expiryDate);
                                aList.add(hm);
                            }

                            String[] from = {
                                    "ListName","ListBrand", "Description", "Quantity", "MinQuantity"
                            };
                            int[] to = {
                                    R.id.TextView2,   R.id.TextView3, R.id.TextView1, R.id.TV_quantity, R.id.TV_minQuantity
                            };


                            SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(), aList, R.layout.adapter_view_grocery_list,from,to);
                            mListView.setAdapter(simpleAdapter);

                            
                        }


                    }
                }
            }
        });


    }




    private void getSearch() {

        String str_search = ed_search.getText().toString();

        //get the data and append the list
        List<HashMap<String,String>> aList = new ArrayList<HashMap<String, String>>();
        Cursor data = mDatabaseHelper.getFilteredInventoryItems(str_search, str_filter);

        while(data.moveToNext()) {
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("itemName", "Product Name: " + data.getString(1));
            hm.put("brandName", "Brand Name: " + data.getString(2));
            hm.put("quantity", "quantity: " + data.getString(3));
            aList.add(hm);
        }

        String[] from = {
                "itemName","brandName","quantity"
        };
        int[] to = {
                R.id.TextView2,   R.id.TextView3 , R.id.TextView1
        };

        SimpleAdapter simpleAdapter = new SimpleAdapter(this, aList, R.layout.adapter_view_layout,from,to);
        mListView.setAdapter(simpleAdapter);

        //perform listView item click event
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), i ,Toast.LENGTH_LONG).show();//show the selected image in toast according to position

            }
        });

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        str_filter = parent.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        str_filter = "Name";
    }


}
