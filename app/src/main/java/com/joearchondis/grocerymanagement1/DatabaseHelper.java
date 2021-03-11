package com.joearchondis.grocerymanagement1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String TAG = "DatabaseHelper";

    // Table Names
    private static final String TABLE_ITEMS = "items";
    private static final String TABLE_BRANDS = "brands";
    private static final String TABLE_INVENTORY = "inventory";

    // items Table Columns
    private static final String KEY_ITEMS_ID = "id";
    private static final String KEY_ITEMS_NAME = "item_name";
    private static final String KEY_ITEMS_BRAND_ID = "brand_id";
    private static final String KEY_ITEMS_DESCRIPTION = "description";

    // brands Table Columns
    private static final String KEY_BRANDS_ID = "id";
    private static final String KEY_BRANDS_NAME = "item_name";

    // inventory Table Columns
    private static final String KEY_ITEM_VALUES_ID = "id";
    private static final String KEY_ITEM_VALUES_ITEM_ID = "item_id";
    private static final String KEY_ITEM_VALUES_MEASUREMENT = ""; // kg, ml, L, packs.
    private static final String KEY_ITEM_VALUES_CALORIES = "calories";
    private static final String KEY_ITEM_VALUES_PRICE = "price";

    // this is for testing purposes Table Columns
    private static final String TABLE_NAME = "test_table";
    private static final String COL1 = "ID";
    private static final String COL2 = "Name";

    public DatabaseHelper(Context context) {
        super(context, "DB_NAME.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL2 +" TEXT)";

        String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS +
                "(" +
                KEY_ITEMS_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                KEY_ITEMS_BRAND_ID + " INTEGER REFERENCES " + TABLE_BRANDS + "," + // Define a foreign key
                KEY_ITEMS_NAME + " TEXT" +
                ")";

        String CREATE_BRANDS_TABLE = "CREATE TABLE " + TABLE_BRANDS +
                "(" +
                KEY_BRANDS_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                KEY_BRANDS_NAME + " TEXT" +
                ")";

        db.execSQL(createTable);
        db.execSQL(CREATE_ITEMS_TABLE);
        db.execSQL(CREATE_BRANDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BRANDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    public boolean addData(String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2,item);

        Log.d(TAG, "addData: Adding " + item + " to " + TABLE_NAME);

        long result = db.insert(TABLE_NAME, null, contentValues);

        //if data is inserted incorrectly it will return -1
        if(result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean addItem(String itemName) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_ITEMS_NAME,itemName);

        Log.d(TAG, "addData: Adding " + itemName + " to " + TABLE_ITEMS);

        long result = db.insert(TABLE_ITEMS, null, contentValues);

        //if data is inserted incorrectly it will return -1
        if(result == -1) {
            return false;
        } else {
            return true;
        }

    }

    public boolean addItem(String itemName, String BrandName) {
        //TODO
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        String BrandID = getBrandID(BrandName);
        if(BrandID == "-1") {
            addBrand(BrandName);
        }
        BrandID = getBrandID(BrandName);

        contentValues.put(KEY_ITEMS_BRAND_ID,BrandID);
        contentValues.put(KEY_ITEMS_NAME,itemName);

        Log.d(TAG, "addData: Adding " + itemName + " to " + TABLE_ITEMS);

        long result = db.insert(KEY_ITEMS_NAME, null, contentValues);

        //if data is inserted incorrectly it will return -1
        if(result == -1) {
            return false;
        } else {
            return true;
        }

    }

    public boolean addBrand(String brandName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_BRANDS_NAME, brandName);

        Log.d(TAG, "addData: Adding " + brandName + " to " + KEY_BRANDS_NAME);

        long result = db.insert(KEY_BRANDS_NAME, null, contentValues);

        //if data is inserted incorrectly it will return -1
        if(result == -1) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * Returns all the data from database
     * @return
     */
    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    public Cursor getItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_ITEMS ;
                //+ " JOIN " + TABLE_BRANDS +
                //" ON " + TABLE_ITEMS + "." + KEY_ITEMS_BRAND_ID + "=" + TABLE_BRANDS + "." + KEY_BRANDS_ID ;
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    /**
     * Returns only the ID that matches the name passed in. Returns -1 if not available
     * @param name
     * @return
     */
    public String getItemID(String name){
        String result = "";
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + KEY_ITEMS_ID + " FROM " + TABLE_ITEMS +
                " WHERE " + KEY_ITEMS_NAME + " = '" + name + "'";
        Cursor data = db.rawQuery(query, null);

        while(data.moveToNext()) {
            result = data.getString(0);
        }
        if(result == "")
            return "-1";
        return result;
    }

    public String getBrandID(String BrandName){
        String result = "";
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + KEY_BRANDS_ID + " FROM " + TABLE_BRANDS +
                " WHERE " + KEY_BRANDS_NAME + " = '" + BrandName + "'";
        Cursor data = db.rawQuery(query, null);

        while(data.moveToNext()) {
            result = data.getString(0);
        }
        if(result == "")
            return "-1";
        return result;
    }


    /**
     * Updates the name field
     * @param newName
     * @param id
     * @param oldName
     */
    public void updateName(String newName, int id, String oldName){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COL2 +
                " = '" + newName + "' WHERE " + COL1 + " = '" + id + "'" +
                " AND " + COL2 + " = '" + oldName + "'";
        Log.d(TAG, "updateName: query: " + query);
        Log.d(TAG, "updateName: Setting name to " + newName);
        db.execSQL(query);
    }





    /**
     * Delete from database
     * @param id
     * @param name
     */
    public void deleteName(int id, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE "
                + COL1 + " = '" + id + "'" +
                " AND " + COL2 + " = '" + name + "'";
        Log.d(TAG, "deleteName: query: " + query);
        Log.d(TAG, "deleteName: Deleting " + name + " from database.");
        db.execSQL(query);
    }

}
