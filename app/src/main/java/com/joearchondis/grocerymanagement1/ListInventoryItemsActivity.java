package com.joearchondis.grocerymanagement1;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.ArrayList;

public class ListInventoryItemsActivity extends AppCompatActivity {
    private static final String TAG = "ListInventoryItemsActivity";

    DatabaseHelper mDatabaseHelper;

    ListView mListView;
    ArrayList<Item> ItemsList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_items_layout);

        mListView =(ListView)findViewById(R.id.ListView_Items);
        mDatabaseHelper = new DatabaseHelper(this);

        populateListView();

    }

    // Gets Inventory Items data from local database and populates ListView.
    private void populateListView() {

        //get the data and append the list
        List<HashMap<String,String>> aList = new ArrayList<HashMap<String, String>>();
        Cursor data = mDatabaseHelper.getInventory();


        while(data.moveToNext()) {

            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("ListName",data.getString(0));
            hm.put("ListBrand",data.getString(1));
            aList.add(hm);

        }

        String[] from = {
                "ListName","ListBrand"
        };
        int[] to = {
                R.id.TextView1,   R.id.TextView2
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


}
