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
import android.widget.TextView;
import android.widget.EditText;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // If user is found, move to inventory page
        Button LButton = findViewById(R.id.loginButton);
        LButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                EditText username = findViewById(R.id.nameUser);
                EditText password = findViewById(R.id.namePassword);
                if (findUser()) {
                    username.setText("User found");
                    Intent myIntent = new Intent(MainActivity.this, Inv_Page.class);
                    MainActivity.this.startActivity(myIntent);
                } else {
                    username.setText("User not found");
                }
            }
        });

        // Create user
        Button CButton = findViewById(R.id.createUserButton);
        CButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                addUser();
                textView = findViewById(R.id.textUser);
                textView.setText("User created");
            }
        });
    }


    /// <---------------------- USER
    public final class User {
        private User() {
        }

        public class Login implements BaseColumns {
            public static final String TABLE_NAME = "Users";
            public static final String COLUMN_NAME_USERNAME = "Username";
            public static final String COLUMN_NAME_PASSWORD = "Password";
        }
    }

    public static class UserDbHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "User.db";
        private static final int DATABASE_VERSION = 1;

        public UserDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + User.Login.TABLE_NAME + " (" +
                        User.Login.COLUMN_NAME_USERNAME + " TEXT NOT NULL, " +
                        User.Login.COLUMN_NAME_PASSWORD + " TEXT NOT NULL, " +
                        User.Login._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +
                        ");";


        @Override
        public void onCreate(SQLiteDatabase udb) {
            udb.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase udb, int oldVersion, int newVersion) {
            udb.execSQL("DROP TABLE IF EXISTS " + User.Login.TABLE_NAME);
            onCreate(udb);
        }
    }

    public long addUser() {
        SQLiteDatabase Udb = new UserDbHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        EditText username = findViewById(R.id.nameUser);
        EditText password = findViewById(R.id.namePassword);
        values.put(User.Login.COLUMN_NAME_USERNAME, username.getText().toString());
        values.put(User.Login.COLUMN_NAME_PASSWORD, password.getText().toString());
        long rowId = Udb.insert(User.Login.TABLE_NAME, null, values);
        return rowId;
    }
    public boolean findUser() {
        UserDbHelper UserHelper = new UserDbHelper(this);
        SQLiteDatabase udb = UserHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        EditText username = findViewById(R.id.nameUser);
        EditText password = findViewById(R.id.namePassword);
        Cursor cursor = udb.query(
                User.Login.TABLE_NAME,
                null,
                User.Login.COLUMN_NAME_USERNAME + " = ? AND " + User.Login.COLUMN_NAME_PASSWORD + " = ?",
                new String[]{username.getText().toString(), password.getText().toString()},
                null,
                null,
                null
        );
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }
}