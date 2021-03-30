package com.joearchondis.grocerymanagement1;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.ArrayList;

public class ListInventoryItemsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "ListInventoryItemsActivity";

    DatabaseHelper mDatabaseHelper;

    ListView mListView;

    Button btn_add_inventory_item_activity, btn_search;

    EditText ed_search;

    String str_filter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_items_layout);

        mListView =(ListView)findViewById(R.id.ListView_Items);
        ed_search = (EditText)findViewById(R.id.searchFilterInventory);

        mDatabaseHelper = new DatabaseHelper(this);

        Spinner spinner_filterBy = findViewById(R.id.spinner_filterBy);
        ArrayAdapter<CharSequence> adapter_filterBy = ArrayAdapter.createFromResource(this,R.array.filterBy, android.R.layout.simple_spinner_item);
        adapter_filterBy.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_filterBy.setAdapter(adapter_filterBy);
        spinner_filterBy.setOnItemSelectedListener(this);

        btn_add_inventory_item_activity = findViewById(R.id.btn_add_inventory_item_activity);
        btn_search = findViewById(R.id.btn_search);



        btn_add_inventory_item_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListInventoryItemsActivity.this, AddInventoryItemActivity.class);
                startActivity(intent);
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSearch();
            }
        });

        populateListView();

    }

    // Gets Inventory Items data from local database and populates ListView.
    private void populateListView() {

        //get the data and append the list
        List<HashMap<String,String>> aList = new ArrayList<HashMap<String, String>>();
        Cursor data = mDatabaseHelper.getInventoryItems();

        while(data.moveToNext()) {
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("itemName", "Product Name: " + data.getString(1));
            hm.put("brandName", "Brand Name: " + data.getString(2));
            hm.put("quantity", "ID: " + data.getString(0));
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

    private void getSearch() {

        String str_search = ed_search.getText().toString();

        //get the data and append the list
        List<HashMap<String,String>> aList = new ArrayList<HashMap<String, String>>();
        Cursor data = mDatabaseHelper.getFilteredInventoryItems(str_search, str_filter);

        while(data.moveToNext()) {
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("itemName", "Product Name: " + data.getString(1));
            hm.put("brandName", "Brand Name: " + data.getString(2));
            hm.put("quantity", "ID: " + data.getString(0));
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
        Toast.makeText(parent.getContext(),str_filter,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        str_filter = "Name";
    }
}
