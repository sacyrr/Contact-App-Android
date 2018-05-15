package com.example.manojd.myapplication.activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manojd.myapplication.R;
import com.example.manojd.myapplication.db.DbHelper;
import com.example.manojd.myapplication.model.Contact;

import java.util.ArrayList;

public class ContactDetails extends AppCompatActivity {
    TextView fName,lName,mobile,email;
    ImageView imageView;
    ImageButton call,message,mail;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details1);

        fName = findViewById(R.id.detailsFirstName);
        lName = findViewById(R.id.detailsLastName);
        mobile = findViewById(R.id.detailsMobileNumber);
        email = findViewById(R.id.detailsEmailId);
        imageView = findViewById(R.id.imageViewDetails);
        call =(ImageButton) findViewById(R.id.buttonCall);
        message =(ImageButton) findViewById(R.id.buttonMessage);
        mail =(ImageButton) findViewById(R.id.buttonEmail);

        Contact contact = getData();
        fName.setText(contact.getFirstName());
        lName.setText(contact.getLastName());
        mobile.setText(contact.getMobile());
        email.setText(contact.getEmail());
        if(contact.getImage() == null){
            imageView.setImageResource(R.drawable.contacts_icon);
        }
        else {
            byte [] arr = Base64.decode(contact.getImage(),Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(arr,0,arr.length);
            imageView.setImageBitmap(bitmap);
        }

            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(this,
                        new String[] { Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE },
                        1);
            }


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(contact.getFirstName());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.saveFlaotingButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData();
                finish();
            }
        });
    }

    public Contact getData() {
        Intent intent = getIntent();
        int id = intent.getIntExtra("contact", 0);
        DbHelper helper = new DbHelper(this, DbHelper.DB_NAME, null, DbHelper.DB_VERSION);
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<Contact> contacts = new ArrayList<>();

        String query = "SELECT * FROM " + DbHelper.TABLE_NAME + " WHERE ID=" + id;
        Cursor cursor = db.rawQuery(query, null);
        Contact contact = null;
        if (cursor.moveToFirst()) {
            do {
                contact = new Contact();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setFirstName(cursor.getString(1));
                contact.setLastName(cursor.getString(2));
                contact.setMobile(cursor.getString(3));
                contact.setEmail(cursor.getString(4));
                contact.setImage(cursor.getString(5));

                contacts.add(contact);

            } while (cursor.moveToNext());
        }

        return contact;
    }


    public void updateData(){
        Intent intent = getIntent();
        int id = intent.getIntExtra("contact",0);
        Intent intent1 = new Intent(this,UpdateContact.class);
        intent1.putExtra("updateContact",id);
        startActivity(intent1);
    }

    public void deleteData(){
        Intent intent = getIntent();
        int id = intent.getIntExtra("contact",0);
        DbHelper helper = new DbHelper(this,DbHelper.DB_NAME,null,DbHelper.DB_VERSION);
        SQLiteDatabase db = helper.getWritableDatabase();
        int result = db.delete(DbHelper.TABLE_NAME,"ID="+id,null);
        Log.v("TAG","Item deleted "+result+" "+id);
        Toast.makeText(this,"Item deleted "+id,Toast.LENGTH_SHORT).show();
        db.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            deleteData();
            finish();
            return true;
        }
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1){
            if ((grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                call.setEnabled(true);
                Log.d("ContactDetails", "Permission granted");
            } else {
                Log.d("ContactDetails", "Permission denied");
            }
        }
    }

    public void call(View view){
        Contact contact = getData();
        Intent intent1 = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:0"+contact.getMobile()));
        //intent1.setData(Uri.parse("tel:+91"+contact.getMobile()));
        startActivity(intent1);
        Log.v("Number",contact.getMobile());
    }

    public void message(View view){
        Contact contact = getData();
        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("sms:"+contact.getMobile()));
        startActivity(intent);
    }

    public void email(View view){
        Contact contact = getData();
        String email=contact.getEmail();
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts( "mailto",email, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }
}
