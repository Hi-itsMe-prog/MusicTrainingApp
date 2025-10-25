package com.example.musictrainingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private ImageView backbut;
    private TextView tvUserName, tvUserEmail, tvTotalScore, tvTotalTime, tvAchievements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        findViews();
        setListeners();
        loadProfileData();
    }

    private void findViews() {
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvTotalScore = findViewById(R.id.tvTotalScore);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        backbut = findViewById(R.id.backButton);
    }

    private void setListeners() {
        backbut.setOnClickListener(v -> {
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void loadProfileData() {
        tvUserName.setText("Алексей Петров");
        tvUserEmail.setText("alexey@example.com");
        tvTotalScore.setText("Общий счёт: 1250");
        tvTotalTime.setText("Общее время: 15 ч");
        tvAchievements.setText("Получено: 3/10");
    }
}