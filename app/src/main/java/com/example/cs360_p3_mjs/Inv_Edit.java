package com.example.cs360_p3_mjs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.app.Instrumentation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.widget.EditText;
import androidx.core.app.ActivityCompat;
import android.Manifest;

import android.telephony.SmsManager;
import android.widget.PopupWindow;

public class Inv_Edit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inv_edit);

        // Read item from database and populate fields
        readItems();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("name");
            EditText name = findViewById(R.id.itemName);
            name.setText(value);
        }

        // Add / update item in database and return to main inventory page
        Button AddButton = findViewById(R.id.addItemButton);
        AddButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (extras != null) {
                    String value = extras.getString("name");
                    addItem(value);
                } else {
                    addItem("");
                }
                Intent myIntent = new Intent(Inv_Edit.this, Inv_Page.class);
                Inv_Edit.this.startActivity(myIntent);
            }
        });

        // Delete item from database and return to main inventory page
        Button DeleteButton = findViewById(R.id.deleteItemButton);
        DeleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                EditText name = findViewById(R.id.itemName);
                deleteItem(name.getText().toString());
                Intent myIntent = new Intent(Inv_Edit.this, Inv_Page.class);
                Inv_Edit.this.startActivity(myIntent);
            }
        });

        // Return to main inventory page
        Button BackButton = findViewById(R.id.BackButton);
        BackButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(Inv_Edit.this, Inv_Page.class);
                Inv_Edit.this.startActivity(myIntent);
            }
        });


        // Check if SMS permission is granted, if granted send SMS then return to main inventory page
        Button SMSButton = findViewById(R.id.SMSButton);
        SMSButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                requestSmsPermission();
                Intent myIntent = new Intent(Inv_Edit.this, Inv_Page.class);
                Inv_Edit.this.startActivity(myIntent);

            }
        });

    }

    private static final int SMS_PERMISSION_REQUEST_CODE = 1234;

    public void requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
               sendSMS();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
            }
        } else {
            sendSMS();
        }
    }

    public void sendSMS() {
        EditText name = findViewById(R.id.itemName);
        EditText description = findViewById(R.id.itemDescription);
        EditText quantity = findViewById(R.id.itemQuantity);
        EditText price = findViewById(R.id.itemPrice);
        EditText phone = findViewById(R.id.itemSMSContact);
        String message = "Name: " + name.getText().toString() + "\nDescription: " + description.getText().toString() + "\nQuantity: " + quantity.getText().toString() + "\nPrice: " + price.getText().toString();
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone.getText().toString(), null, message, null, null);


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
                    Inv_Page.Inventory.item.TABLE_NAME + " (" +
                    Inv_Page.Inventory.item._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Inv_Page.Inventory.item.COLUMN_NAME_NAME + " TEXT NOT NULL, " +
                    Inv_Page.Inventory.item.COLUMN_NAME_DESCRIPTION + " TEXT NOT NULL, " +
                    Inv_Page.Inventory.item.COLUMN_NAME_QUANTITY + " INTEGER NOT NULL, " +
                    Inv_Page.Inventory.item.COLUMN_NAME_PRICE + " REAL NOT NULL" +
                    ");";
            db.execSQL(SQL_CREATE_ITEM_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + Inv_Page.Inventory.item.TABLE_NAME);
            onCreate(db);
        }

    }



    public void deleteItem(String name) {
        SQLiteDatabase db = new InventoryDbHelper(this).getReadableDatabase();
        String selection = Inv_Page.Inventory.item.COLUMN_NAME_NAME + " LIKE ?";
        String[] selectionArgs = {name};
        db.delete(Inv_Page.Inventory.item.TABLE_NAME, selection, selectionArgs);
    }


    public void readItems() {
        SQLiteDatabase db = new InventoryDbHelper(this).getReadableDatabase();

        EditText name = findViewById(R.id.itemName);
        EditText description = findViewById(R.id.itemDescription);
        EditText quantity = findViewById(R.id.itemQuantity);
        EditText price = findViewById(R.id.itemPrice);

        ContentValues values = new ContentValues();

        String[] projection = {
                Inv_Page.Inventory.item._ID,
                Inv_Page.Inventory.item.COLUMN_NAME_NAME,
                Inv_Page.Inventory.item.COLUMN_NAME_DESCRIPTION,
                Inv_Page.Inventory.item.COLUMN_NAME_QUANTITY,
                Inv_Page.Inventory.item.COLUMN_NAME_PRICE
        };

        Cursor cursor = db.query(
                Inv_Page.Inventory.item.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while(cursor.moveToNext()) {
            String itemName = name.getText().toString();
            if (itemName.equals(name.getText().toString())) {
                String itemDescription = cursor.getString(cursor.getColumnIndexOrThrow(Inv_Page.Inventory.item.COLUMN_NAME_DESCRIPTION));
                int itemQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(Inv_Page.Inventory.item.COLUMN_NAME_QUANTITY));
                double itemPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(Inv_Page.Inventory.item.COLUMN_NAME_PRICE));
                description.setText(itemDescription);
                quantity.setText(Integer.toString(itemQuantity));
                price.setText(Double.toString(itemPrice));

            }
            else
            {
                name.setText("");
                description.setText("");
                quantity.setText("");
                price.setText("");
            }
        }

        cursor.close();
    }


    public void addItem(String NameID) {
        SQLiteDatabase db = new InventoryDbHelper(this).getReadableDatabase();
        EditText name = findViewById(R.id.itemName);
        EditText description = findViewById(R.id.itemDescription);
        EditText quantity = findViewById(R.id.itemQuantity);
        EditText price = findViewById(R.id.itemPrice);

        String selection = Inv_Page.Inventory.item.COLUMN_NAME_NAME + " LIKE ?";
        String[] selectionArgs = {NameID};
        db.delete(Inv_Page.Inventory.item.TABLE_NAME, selection, selectionArgs);

        ContentValues values = new ContentValues();
        values.put(Inventory.item.COLUMN_NAME_NAME, name.getText().toString());
        values.put(Inventory.item.COLUMN_NAME_DESCRIPTION, description.getText().toString());
        values.put(Inventory.item.COLUMN_NAME_QUANTITY, Integer.parseInt(quantity.getText().toString()));
        values.put(Inventory.item.COLUMN_NAME_PRICE, Double.parseDouble(price.getText().toString()));

        long newRowId = db.insert(Inventory.item.TABLE_NAME, null, values);

    }




}