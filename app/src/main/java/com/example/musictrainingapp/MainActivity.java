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
    private View cardExercises, cardDictionary, cardStatistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        loadUserData();
        setupClickListeners();
    }

    private void findViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        tvProgress = findViewById(R.id.tvProgress);
        tvTotalExercises = findViewById(R.id.tvTotalExercises);
        tvAccuracy = findViewById(R.id.tvAccuracy);
        progressBar = findViewById(R.id.progressBar);
        btnProfile = findViewById(R.id.btnProfile);
        cardExercises = findViewById(R.id.cardExercises);
        cardDictionary = findViewById(R.id.cardDictionary);
        cardStatistics = findViewById(R.id.cardStatistics);
    }

    private void loadUserData() {
        String username = "Алексей";
        int progress = 45;
        int totalExercises = 27;
        int accuracy = 78;

        tvWelcome.setText("Добро пожаловать, " + username + "!");
        tvProgress.setText(progress + "%");
        progressBar.setProgress(progress);
        tvTotalExercises.setText(String.valueOf(totalExercises));
        tvAccuracy.setText(accuracy + "%");
    }

    private void setupClickListeners() {
        btnProfile.setOnClickListener(v -> openProfile());
        cardExercises.setOnClickListener(v -> openExerciseActivity());
        cardDictionary.setOnClickListener(v -> openDictionaryActivity());
        cardStatistics.setOnClickListener(v -> openStatisticsActivity());
    }

    private void openExerciseActivity() {
        Intent intent = new Intent(this, ExerciseActivity.class);
        startActivity(intent);
    }

    private void openProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void openDictionaryActivity() {
        // Временная заглушка
        Intent intent = new Intent(this, ExerciseActivity.class);
        startActivity(intent);
    }

    private void openStatisticsActivity() {
        // Временная заглушка
        Intent intent = new Intent(this, ExerciseActivity.class);
        startActivity(intent);
    }
}