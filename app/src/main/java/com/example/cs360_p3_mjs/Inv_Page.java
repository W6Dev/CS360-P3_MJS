package com.example.cs360_p3_mjs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.EditText;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import android.widget.ArrayAdapter;
import android.widget.GridView;



public class Inv_Page extends AppCompatActivity {

    public ArrayAdapter<String> CustomedAdapter;
    ArrayList<String> items = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inv_page);

        // Read item from database and populate fields
        readItems();

        // move to edit page
        Button AddButton = findViewById(R.id.addItemButton);
        AddButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(Inv_Page.this, Inv_Edit.class);
                myIntent.putExtra("name", "");
                Inv_Page.this.startActivity(myIntent);

            }
        });

        // move to edit page with item name
        GridView Gview = findViewById(R.id.ViewAllItems);
        Gview.setOnItemClickListener((parent, view, position, id) -> {
            String a = items.get(position);
            Intent myIntent = new Intent(Inv_Page.this, Inv_Edit.class);
            myIntent.putExtra("name", a);
            Inv_Page.this.startActivity(myIntent);
        });
    }


    public final class Inventory {
        private Inventory() {
        }

        public class item implements BaseColumns {
            public static final String TABLE_NAME = "item";
            public static final String COLUMN_NAME_NAME = "name";
            public static final String COLUMN_NAME_DESCRIPTION = "description";
            public static final String COLUMN_NAME_QUANTITY = "quantity";
            public static final String COLUMN_NAME_PRICE = "price";
        }

    }

    public class InventoryDbHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "inventory.db";
        private static final int DATABASE_VERSION = 1;

        public InventoryDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            final String SQL_CREATE_ITEM_TABLE = "CREATE TABLE " +
                    Inventory.item.TABLE_NAME + " (" +
                    Inventory.item._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Inventory.item.COLUMN_NAME_NAME + " TEXT NOT NULL, " +
                    Inventory.item.COLUMN_NAME_DESCRIPTION + " TEXT NOT NULL, " +
                    Inventory.item.COLUMN_NAME_QUANTITY + " INTEGER NOT NULL, " +
                    Inventory.item.COLUMN_NAME_PRICE + " REAL NOT NULL" +
                    ");";
            db.execSQL(SQL_CREATE_ITEM_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + Inventory.item.TABLE_NAME);
            onCreate(db);
        }

    }


    public void readItems() {
        SQLiteDatabase db = new InventoryDbHelper(this).getReadableDatabase();
        String[] projection = {
                Inventory.item._ID,
                Inventory.item.COLUMN_NAME_NAME,
                Inventory.item.COLUMN_NAME_DESCRIPTION,
                Inventory.item.COLUMN_NAME_QUANTITY,
                Inventory.item.COLUMN_NAME_PRICE
        };

        String sortOrder =
                Inventory.item.COLUMN_NAME_NAME + " DESC";

        Cursor cursor = db.query(
                Inventory.item.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
        // Read items from database and populate fields
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(Inventory.item.COLUMN_NAME_NAME));
            items.add(name);
        }
        // Create an ArrayAdapter from List
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items);
        // Attach the adapter to a GridView
        GridView Gview = findViewById(R.id.ViewAllItems);
        // Set the layout for all items
        Gview.setAdapter(adapter);
        // Close the cursor
        cursor.close();

    }


}