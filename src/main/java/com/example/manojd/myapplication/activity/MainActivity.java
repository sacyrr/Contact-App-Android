package com.example.manojd.myapplication.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.manojd.myapplication.R;
import com.example.manojd.myapplication.adapter.ContactAdapterRecyclerView;
import com.example.manojd.myapplication.common.ContactClickListner;
import com.example.manojd.myapplication.db.DbHelper;
import com.example.manojd.myapplication.model.Contact;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ContactClickListner {

    //ListView listView;
    RecyclerView recyclerView;
    ArrayList<Contact> contacts = new ArrayList<>();
    //ContactAdapter adapter;
    ContactAdapterRecyclerView adapter;
    //ContactClickListner listner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recyclerview);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.saveFlaotingButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,AddActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        //contacts = getData();
        super.onResume();
        contacts = getData();

        /*listView = findViewById(R.id.listView);
        adapter = new ContactAdapter(this,R.layout.contact_list_items,contacts);
        listView.setAdapter(adapter);*/

        recyclerView = findViewById(R.id.recyclerView);
        //adapter = new ContactAdapterRecyclerView(this,contacts, listner);
        adapter = new ContactAdapterRecyclerView(this,contacts,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        adapter.notifyDataSetChanged();

        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact contact = contacts.get(position);
                Intent intent = new Intent(MainActivity.this,ContactDetails.class);
                intent.putExtra("contact",contact);
                startActivity(intent);
                Toast.makeText(MainActivity.this,"item clicked"+position,Toast.LENGTH_SHORT).show();

            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            Intent intent = new Intent(this,AddActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public ArrayList<Contact> getData(){
        contacts.clear();
        DbHelper helper = new DbHelper(this,DbHelper.DB_NAME,null,DbHelper.DB_VERSION);
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<Contact> contacts = new ArrayList<>();

        String query = "SELECT * FROM "+DbHelper.TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                Contact contact = new Contact();
                contact.setId( Integer.parseInt(cursor.getString(0)) );
                contact.setFirstName( cursor.getString(1) );
                contact.setLastName( cursor.getString(2) );
                contact.setMobile( cursor.getString(3) );
                contact.setEmail( cursor.getString(4) );
                contact.setImage( cursor.getString(5) );

                contacts.add(contact);

            }while (cursor.moveToNext());
        }
        return contacts;
    }

    @Override
    public void onContactClick(Contact contact) {
        Intent intent = new Intent(MainActivity.this,ContactDetails.class);
        intent.putExtra("contact",contact.getId());
        startActivity(intent);
        Toast.makeText(MainActivity.this,"item clicked",Toast.LENGTH_SHORT).show();
    }
}
