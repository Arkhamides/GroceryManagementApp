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

public class ListHistoryActivity extends AppCompatActivity {

    private static final String TAG = "ListHistoryActivity";

    DatabaseHelper mDatabaseHelper;
    ListView mListView;
    User currentUser;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_layout);

        currentUser = ((MyApplication) getApplication()).getSelectedUser();

        mListView =(ListView)findViewById(R.id.ListView_History);
        mDatabaseHelper = new DatabaseHelper(this);

        getHistoryList();


    }


    private void populateListView() {
        Log.d(TAG, "populateListView: Displaying data in the ListView");

        //get the data and append the list
        List<HashMap<String,String>> aList = new ArrayList<HashMap<String, String>>();
        Cursor data = mDatabaseHelper.getHistory(); //TODO Order by date
        while(data.moveToNext()) {

            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("ListName", "Name: " + data.getString(0));
            hm.put("ListBrand", "Brand: " + data.getString(0));
            hm.put("Quantity", "");
            hm.put("Price", "");
            hm.put("In", "");
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
                Toast.makeText(getApplicationContext(),"" + i ,Toast.LENGTH_LONG).show();//toast according to position
            }
        });

    }

    private void getHistoryList() {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {

                String[] field = new String[1];
                field[0] = "userID";

                String[] data = new String[1];
                data[0] = currentUser.getID();


                String ip = ((MyApplication) getApplication()).getIP();
                PutData putData = new PutData("http://"+ip+"/GroceryManagementApp/getHistoryItems.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {

                        String result = putData.getResult();

                        if(result.equals("-1")) {
                            Toast.makeText(getApplicationContext(), "No items found", Toast.LENGTH_SHORT).show();
                        }else {

                            String results[] = result.split(",", -2);

                            List<HashMap<String,String>> aList = new ArrayList<HashMap<String, String>>();
                            int i = 0;
                            String name,brand, quantity, date;


                            while(results.length > i) {
                                name = results[i];
                                i++;
                                brand = results[i];
                                i++;
                                quantity = results[i];
                                i++;
                                date = results[i];
                                i++;


                                HashMap<String, String> hm = new HashMap<String,String>();
                                hm.put("ListName", name + " " + brand);
                                hm.put("quantity", "quantity: " + quantity);
                                hm.put("Date", "Date: " + date);
                                aList.add(hm);
                            }

                            String[] from = {
                                    "ListName","quantity", "Date"
                            };
                            int[] to = {
                                    R.id.TextView2,  R.id.TextView3, R.id.TextView1
                            };


                            SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(), aList, R.layout.adapter_view_layout,from,to);
                            mListView.setAdapter(simpleAdapter);



                        }


                    }
                }
            }
        });

    }



}
