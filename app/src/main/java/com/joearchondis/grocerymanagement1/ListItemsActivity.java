package com.joearchondis.grocerymanagement1;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListItemsActivity extends AppCompatActivity {

    private static final String TAG = "ListDataActivity";

    DatabaseHelper mDatabaseHelper;

    ListView mListView;
    ArrayList<Item> ItemsList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.items_layout);

        mListView =(ListView)findViewById(R.id.ListView_Items);
        mDatabaseHelper = new DatabaseHelper(this);

        getItemsServer();
    }

    // Gets Items  data from local database and populates ListView.
    private void populateListView() {


        //get the data and append the list
        List<HashMap<String,String>> aList = new ArrayList<HashMap<String, String>>();
        Cursor data = mDatabaseHelper.getItems();
        while(data.moveToNext()) {
            Item i1 = new Item(data.getString(2),data.getString(4));
            ItemsList.add(i1);

            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("ListName", "Item: " + i1.name);
            hm.put("ListBrand", "Brand: " + i1.brand);
            hm.put("Description", "");
            aList.add(hm);
        }

        String[] from = {
                "ListName","ListBrand", "Description"
        };
        int[] to = {
                R.id.TextView2,   R.id.TextView3, R.id.TextView1
        };

        SimpleAdapter simpleAdapter = new SimpleAdapter(this, aList, R.layout.adapter_view_layout,from,to);
        mListView.setAdapter(simpleAdapter);

        //perform listView item click event
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                ((MyApplication) getApplication()).setSelectedItem(ItemsList.get(i).name);
                String s = ((MyApplication) getApplication()).getSelectedItem();
                Toast.makeText(getApplicationContext(), s ,Toast.LENGTH_LONG).show();
            }
        });

    }


    public void getItemsServer() {

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {

                    String[] field = new String[0];
                    String[] data = new String[0];


                    PutData putData = new PutData("http://192.168.0.106/GroceryManagementApp/getItems.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {

                            String result = putData.getResult();

                            if(result.equals("-1")) {
                                Toast.makeText(getApplicationContext(), "No items found", Toast.LENGTH_SHORT).show();
                            }else {

                                String results[] = result.split(",", -2);

                                List<HashMap<String,String>> aList = new ArrayList<HashMap<String, String>>();
                                int i = 0;
                                String name;
                                String brand;
                                ItemsList = new ArrayList<>();

                                while(results.length > i) {
                                    name = results[i];
                                    i++;
                                    brand = results[i];
                                    i++;

                                    Item i1 = new Item(name,brand);
                                    ItemsList.add(i1);

                                    HashMap<String, String> hm = new HashMap<String,String>();
                                    hm.put("ListName", "Item: " + name);
                                    hm.put("ListBrand", "Brand: " + brand);
                                    hm.put("Description", "");
                                    aList.add(hm);
                                }

                                String[] from = {
                                        "ListName","ListBrand", "Description"
                                };
                                int[] to = {
                                        R.id.TextView2,   R.id.TextView3, R.id.TextView1
                                };


                                SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(), aList, R.layout.adapter_view_layout,from,to);
                                mListView.setAdapter(simpleAdapter);


                                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                        ((MyApplication) getApplication()).setSelectedItem(ItemsList.get(i).name);
                                        String s = ((MyApplication) getApplication()).getSelectedItem();
                                        Toast.makeText(getApplicationContext(), s ,Toast.LENGTH_LONG).show();

                                    }
                                });
                            }


                        }
                    }
                }
            });


    }









}
