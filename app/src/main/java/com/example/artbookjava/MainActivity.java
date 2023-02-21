package com.example.artbookjava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.artbookjava.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;  // this is the binding object
    ArrayList<Art> artArrayList;  // this is the array list of art objects
    ArtAdapter artAdapter;  // this is the art adapter object


    @Override
    protected void onCreate(Bundle savedInstanceState) {  // this method for link menu with activity
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        artArrayList = new ArrayList<>();  // this is the array list of art objects

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        artAdapter = new ArtAdapter(artArrayList);  // this is the art adapter object
        binding.recyclerView.setAdapter(artAdapter);  // this method is used to set the adapter

        getData();  // this method is used to get the data from the database


    }

    public void getData() { // or private void getData() {

        // get data from database

        try{

            SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("artsInBook", MODE_PRIVATE, null);

            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM artsInBook", null);

            int nameIx = cursor.getColumnIndex("artName");
            int idIx = cursor.getColumnIndex("id");

            while (cursor.moveToNext()) {
                String name = cursor.getString(nameIx);
                int id = cursor.getInt(idIx);

                Art art = new Art(name, id);

                artArrayList.add(art); // this is the array list of art objects
            }
            //update the recycler view with the new data
            artAdapter.notifyDataSetChanged();  // this method is used to notify the adapter that the data has changed

            cursor.close();


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {  // this method is called when the menu is created
        // Inflater
        MenuInflater menuInflater = getMenuInflater();  // this method is used to get the menu inflater
        menuInflater.inflate(R.menu.art_menu, menu);  // this method is used to inflate the menu

        return super.onCreateOptionsMenu(menu);  // this method is used to return the menu
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {  // this method is called when an item is selected
        if (item.getItemId() == R.id.add_art) {  // this method is used to get the id of the item
            // navigate to art activity
            Intent intent = new Intent(MainActivity.this, ArtActivity2nd.class);

            intent.putExtra("info", "new");  // this method controls the data flow between activities

            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);  // this method is used to return the item
    }



}