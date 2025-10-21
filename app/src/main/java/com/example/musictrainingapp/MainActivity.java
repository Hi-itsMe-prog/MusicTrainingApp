package com.example.musictrainingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView tvWelcome, tvProgress, tvTotalExercises, tvAccuracy;
    private ProgressBar progressBar;
    private ImageButton btnProfile;
    private CustomButt1 cardExercises, cardDictionary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        loadUserData();
        setupClickListeners();
    }

    // тут ищем компоненты по id
    private void findViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        tvProgress = findViewById(R.id.tvProgress);
        tvTotalExercises = findViewById(R.id.tvTotalExercises);
        tvAccuracy = findViewById(R.id.tvAccuracy);
        progressBar = findViewById(R.id.progressBar);
        btnProfile = findViewById(R.id.btnProfile);

        cardExercises = findViewById(R.id.cardExercises);
        cardDictionary = findViewById(R.id.cardDictionary);
    }

    // тут будет взаимодействие с БД
    private void loadUserData() {
        String username = getString(R.string.default_username);
        int progress = 45;
        int totalExercises = 27;
        int accuracy = 78;

        tvWelcome.setText(getString(R.string.welcome_message, username));
        tvProgress.setText(getString(R.string.progress_percent, progress));
        tvAccuracy.setText(getString(R.string.accuracy_percent, accuracy));
        progressBar.setProgress(progress);
        tvTotalExercises.setText(getString(R.string.total_exercises_count, totalExercises));
    }

    // кнопки для перехода в другие активити
    private void setupClickListeners() {
        btnProfile.setOnClickListener(v -> {
            System.out.println(getString(R.string.debug_profile_clicked));
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });

        if (cardExercises != null) {
            cardExercises.setOnClickListener(v -> {
                System.out.println(getString(R.string.debug_exercises_clicked));
                Intent intent = new Intent(this, ExerciseActivity.class);
                startActivity(intent);
            });
        } else {
            System.out.println(getString(R.string.debug_card_null, "Exercises"));
        }

        if (cardDictionary != null) {
            cardDictionary.setOnClickListener(v -> {
                System.out.println(getString(R.string.debug_dictionary_clicked));
                Intent intent = new Intent(this, DictionaryActivity.class);
                startActivity(intent);
            });
        } else {
            System.out.println(getString(R.string.debug_card_null, "Dictionary"));
        }
    }
}