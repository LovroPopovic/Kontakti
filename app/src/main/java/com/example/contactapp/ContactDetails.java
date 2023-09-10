package com.example.contactapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class ContactDetails extends AppCompatActivity {

    //pogled
    private TextView nameTv, phoneTv, emailTv, addedTimeTv, updatedTimeTv, noteTv;
    private ImageView profileIv;

    private String id;

    //BP helper
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        //inicijalizacija bp
        dbHelper = new DbHelper(this);

        //povlacimo podatke za trenutni kontakt
        Intent intent = getIntent();
        id = intent.getStringExtra("contactId");

        //inicijaliziramo te podatke
        nameTv = findViewById(R.id.nameTv);
        phoneTv = findViewById(R.id.phoneTv);
        emailTv = findViewById(R.id.emailTv);
        addedTimeTv = findViewById(R.id.addedTimeTv);
        updatedTimeTv = findViewById(R.id.updatedTimeTv);
        noteTv = findViewById(R.id.noteTv);

        profileIv = findViewById(R.id.profileIv);

        loadDataById();

    }

    private void loadDataById() {

        String selectQuery = "SELECT * FROM " + Constants.TABLE_NAME + " WHERE " + Constants.C_ID + " =\"" + id + "\"";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                String name = "" + cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_NAME));
                String image = "" + cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_IMAGE));
                String phone = "" + cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_PHONE));
                String email = "" + cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_EMAIL));
                String note = "" + cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_NOTE));
                String addTime = "" + cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_ADDED_TIME));
                String updateTime = "" + cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_UPDATED_TIME));


                Calendar calendar = Calendar.getInstance(Locale.getDefault());

                calendar.setTimeInMillis(Long.parseLong(addTime));
                String timeAdd = "" + DateFormat.format("dd/MM/yy hh:mm:aa", calendar);

                calendar.setTimeInMillis(Long.parseLong(updateTime));
                String timeUpdate = "" + DateFormat.format("dd/MM/yy hh:mm:aa", calendar);


                nameTv.setText(name);
                phoneTv.setText(phone);
                emailTv.setText(email);
                noteTv.setText(note);
                addedTimeTv.setText(timeAdd);
                updatedTimeTv.setText(timeUpdate);

                if (image.equals("null")) {
                    profileIv.setImageResource(R.drawable.ic_baseline_person_24);
                } else {
                    profileIv.setImageURI(Uri.parse(image));
                }

            } while (cursor.moveToNext());
        }

        db.close();

    }
}