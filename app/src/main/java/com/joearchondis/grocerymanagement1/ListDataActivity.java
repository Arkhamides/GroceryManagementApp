package com.joearchondis.grocerymanagement1;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ListDataActivity extends AppCompatActivity {

    private static final String TAG = "ListDataActivity";

    DatabaseHelper mDatabaseHelper;
    private ListView mListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        mListView =(ListView) findViewById(R.id.listView);
        mDatabaseHelper = new DatabaseHelper(this);

        populateListView();
    }

    // Gets Items  data from local database and populates ListView.
    private void populateListView() {
        Log.d(TAG, "populateListView: Displaying data in the ListView");
             //
        //get the data and append the list
        Cursor data = mDatabaseHelper.getItems();
        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()) {
            listData.add(0, data.getString(0)); //get the value from column and adds to ArrayList
            listData.add(1, data.getString(2)); // column 2: ItemsName
        }

        //create the list adapter and set the adapter
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        mListView.setAdapter(adapter);
    }
}
