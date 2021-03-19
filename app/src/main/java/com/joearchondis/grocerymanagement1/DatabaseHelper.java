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
    private static final String TABLE_TRANSACTIONS = "transaction_history";

    // items Table Columns
    private static final String KEY_ITEMS_ID = "id";
    private static final String KEY_ITEMS_NAME = "item_name";
    private static final String KEY_ITEMS_BRAND_ID = "brand_id";
    private static final String KEY_ITEMS_DESCRIPTION = "description";

    // brands Table Columns
    private static final String KEY_BRANDS_ID = "id";
    private static final String KEY_BRANDS_NAME = "brand_name";

    //inventory Table Columns
    private static final String KEY_INVENTORY_ID = "id";
    private static final String KEY_INVENTORY_NAME = "inventory_name";

    // inventory_items Table Columns
    private static final String KEY_INVENTORY_ITEM_ID = "id"; //PK
    private static final String KEY_INVENTORY_ITEM_ITEM_ID = "item_id"; //unique FK
    private static final String KEY_INVENTORY_ITEM_INVENTORY_ID = "inventory_id"; //unique FK
    private static final String KEY_INVENTORY_ITEM_QUANTITY = "quantity";

    private static final String KEY_INVENTORY_ITEM_MEASUREMENT = "measurement_label"; // kg, ml, L, packs.
    private static final String KEY_INVENTORY_ITEM_CALORIES = "calories";
    private static final String KEY_INVENTORY_ITEM_PRICE = "price";
    private static final String KEY_INVENTORY_ITEM_MINIMUM_QTY = "minimum_quantity";

    // transaction_history Columns
    private static final String KEY_TRANSACTION_ID = "id";
    private static final String KEY_TRANSACTION_INVENTORY = "inventory_id";
    private static final String KEY_TRANSACTION_ITEM = "item_id";
    private static final String KEY_TRANSACTION_QUANTITY = "quantity";

    public DatabaseHelper(Context context) {
        super(context, "DB_NAME.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_BRANDS_TABLE = "CREATE TABLE " + TABLE_BRANDS +
                "(" +
                    KEY_BRANDS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // Define a primary key
                    KEY_BRANDS_NAME + " TEXT" +
                ")";

        String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS +
                "(" +
                    KEY_ITEMS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // Define a primary key
                    KEY_ITEMS_BRAND_ID + " INTEGER REFERENCES " + TABLE_BRANDS + "(" + KEY_BRANDS_ID + ")" + " ON DELETE SET NULL," + // Define a foreign key
                    KEY_ITEMS_NAME + " TEXT" +
                ")";
        String CREATE_INVENTORY_TABLE = "CREATE TABLE " + TABLE_INVENTORY +
                "(" +
                    KEY_INVENTORY_ITEM_ID + " INTEGER PRIMARY KEY," + // primary key
                    KEY_INVENTORY_ITEM_ITEM_ID + " INTEGER REFERENCES " + TABLE_ITEMS + "(" + KEY_ITEMS_ID + ")," + //unique FK
                    KEY_INVENTORY_ITEM_INVENTORY_ID + " INTEGER REFERENCES " + TABLE_INVENTORY + "(" + KEY_INVENTORY_ID + ")," + //unique FK
                    KEY_INVENTORY_ITEM_QUANTITY + " INTEGER," +

                    KEY_INVENTORY_ITEM_MEASUREMENT + " TEXT," + // kg, ml, L, packs.
                    KEY_INVENTORY_ITEM_PRICE + " INTEGER," +
                    KEY_INVENTORY_ITEM_CALORIES + " INTEGER," +
                    KEY_INVENTORY_ITEM_MINIMUM_QTY + " INTEGER" +
                ")";

        db.execSQL(CREATE_ITEMS_TABLE);
        db.execSQL(CREATE_BRANDS_TABLE);
        db.execSQL(CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BRANDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORY);
        onCreate(db);
    }

    /*************************   ADD FUNCTIONS    **********************************/

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
        Log.d(TAG, "addData: Adding " + BrandID + " to " + TABLE_ITEMS);

        long result = db.insert(TABLE_ITEMS, null, contentValues);

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

        long result = db.insert(TABLE_BRANDS, null, contentValues);

        //if data is inserted incorrectly it will return -1
        if(result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean addInventory(String inventoryName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_INVENTORY_NAME, inventoryName);
        Log.d(TAG, "addData: Adding " + inventoryName + " to " + KEY_INVENTORY_NAME);

        long result = db.insert(TABLE_INVENTORY, null, contentValues);

        //if data is inserted incorrectly it will return -1
        if(result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean addInventoryItem(String itemName,
                                    String brandName,
                                    String measurementLabel,
                                    String calories,
                                    String price,
                                    String quantity,
                                    String min_quantity) {
        String itemID;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        //check if item exists
        itemID = getItemID(itemName);
        if(itemID == "-1"){
            addItem(itemName,brandName);
        }
        itemID = getItemID(itemName);

        contentValues.put(KEY_INVENTORY_ITEM_ITEM_ID, itemID);
        contentValues.put(KEY_INVENTORY_ITEM_MEASUREMENT, measurementLabel);
        contentValues.put(KEY_INVENTORY_ITEM_CALORIES, calories);
        contentValues.put(KEY_INVENTORY_ITEM_PRICE, price);
        contentValues.put(KEY_INVENTORY_ITEM_QUANTITY, quantity);
        contentValues.put(KEY_INVENTORY_ITEM_MINIMUM_QTY, min_quantity);

        long result = db.insert(TABLE_INVENTORY, null, contentValues);

        if(result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean addInventoryItem(String itemName, String inventoryName, String brandName, String quantity) {
        String itemID;
        String inventoryID;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        //check if item exists
        itemID = getItemID(itemName);
        if(itemID == "-1"){
            addItem(itemName,brandName);
        }
        itemID = getItemID(itemName);

        contentValues.put(KEY_INVENTORY_ITEM_ID, itemID);
        contentValues.put(KEY_INVENTORY_ITEM_QUANTITY, quantity);

        long result = db.insert(TABLE_INVENTORY, null, contentValues);

        if(result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /*******************************************************************************/


    /******************************   GET FUNCTIONS   ******************************/

    public Cursor getItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_ITEMS +
                " JOIN " + TABLE_BRANDS +
                " ON " + TABLE_ITEMS + "." + KEY_ITEMS_BRAND_ID + "=" + TABLE_BRANDS + "." + KEY_BRANDS_ID ;
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    public Cursor getBrands() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_BRANDS ;
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    public Cursor getInventory() {

        // fk Item id
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_INVENTORY +
                " JOIN " + TABLE_ITEMS;
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
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + KEY_BRANDS_ID + " FROM " + TABLE_BRANDS +
                " WHERE " + KEY_BRANDS_NAME + " = '" + BrandName + "'";
        Cursor data = db.rawQuery(query, null);

        while(data.moveToNext()) {
            result = data.getString(0);

            if(result == null){return "-1";}
            return result;
        }
        return "-1";
    }

    /*******************************************************************************/

    /**************************    UPDATE FUNCTIONS     ******************************/

    /**
     * Updates the name field
     * @param newName
     * @param id
     * @param oldName
     */
    /*
    public void updateName(String newName, int id, String oldName){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COL2 +
                " = '" + newName + "' WHERE " + COL1 + " = '" + id + "'" +
                " AND " + COL2 + " = '" + oldName + "'";
        Log.d(TAG, "updateName: query: " + query);
        Log.d(TAG, "updateName: Setting name to " + newName);
        db.execSQL(query);
    }*/

    /*******************************************************************************/

    /**
     * Delete from database
     * @param id
     * @param name
     */
    /*
    public void deleteName(int id, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE "
                + COL1 + " = '" + id + "'" +
                " AND " + COL2 + " = '" + name + "'";
        Log.d(TAG, "deleteName: query: " + query);
        Log.d(TAG, "deleteName: Deleting " + name + " from database.");
        db.execSQL(query);
    }*/

}
