package com.joearchondis.grocerymanagement1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ItemsListAdapter extends ArrayAdapter<Item> {

    private static final String TAG = "ItemsListAdapter";

    private Context mContext;
    int mResource;

    public ItemsListAdapter(Context context, int resource, ArrayList<Item> objects) {
        super(context, resource, objects);
        this.mContext = mContext;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //get the items information
        String name = getItem(position).getName();
        String brand = getItem(position).getBrand();

        //Create the Item obj
        Item item = new Item(name, brand);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvName = (TextView) convertView.findViewById(R.id.TextView1);
        TextView tvBrand = (TextView) convertView.findViewById(R.id.TextView2);

        tvName.setText(name);
        tvBrand.setText(brand);

        return convertView;
    }
}
