package com.joearchondis.grocerymanagement1;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ListDataActivity extends AppCompatActivity {

    private static final String TAG = "ListDataActivity";

    DatabaseHelper mDatabaseHelper;

    ListView mListView;
    ArrayList<Item> ItemsList = new ArrayList<Item>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);

        Log.d(TAG, "onCreate: Started");
        mListView =(ListView)findViewById(R.id.ListView_Items);
        mDatabaseHelper = new DatabaseHelper(this);

        populateListView();
    }

    // Gets Items  data from local database and populates ListView.
    private void populateListView() {
        Log.d(TAG, "populateListView: Displaying data in the ListView");

        //get the data and append the list
        Cursor data = mDatabaseHelper.getItems();
        while(data.moveToNext()) {
            Item i1 = new Item(data.getString(2),data.getString(4));
            ItemsList.add(i1);
        }

        ItemsListAdapter adapter = new ItemsListAdapter(this, R.layout.adapter_view_layout, ItemsList);
        mListView.setAdapter(adapter);



        //perform listView item click event
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), i ,Toast.LENGTH_LONG).show();//show the selected image in toast according to position
            }
        });

    }
}
