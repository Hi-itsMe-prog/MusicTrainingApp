package com.example.musictrainingapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DictionaryActivity extends AppCompatActivity {

    public ImageButton backbut;
    private DatabaseHelper dbHelper;
    private ListView listView;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViews();
        setListeners();
    }

    private void findViews(){
        backbut = findViewById(R.id.backButton);

    }
    private void setListeners(){
        backbut.setOnClickListener(v -> {
            // Завершаем текущую активити и возвращаемся назад
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }






}