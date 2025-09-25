package com.example.musictrainingapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail, tvTotalScore, tvTotalTime, tvAchievements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        findViews();
        loadProfileData();
    }

    private void findViews() {
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvTotalScore = findViewById(R.id.tvTotalScore);
        tvTotalTime = findViewById(R.id.tvTotalTime);;
    }

    private void loadProfileData() {
        tvUserName.setText("Алексей Петров");
        tvUserEmail.setText("alexey@example.com");
        tvTotalScore.setText("Общий счёт: 1250");
        tvTotalTime.setText("Общее время: 15 ч");
        tvAchievements.setText("Получено: 3/10");
    }

}