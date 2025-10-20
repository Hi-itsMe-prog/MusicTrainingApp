package com.example.musictrainingapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ResultsActivity extends AppCompatActivity {

    private TextView tvScore, tvPercentage, tvResultMessage, tvMistakes, tvCorrectAnswers;
    private Button btnRestart, btnMainMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        initializeViews();
        setupClickListeners();
        displayResults();
    }

    private void initializeViews() {
        tvScore = findViewById(R.id.tvScore);
        tvPercentage = findViewById(R.id.tvPercentage);
        tvResultMessage = findViewById(R.id.tvResultMessage);
        tvMistakes = findViewById(R.id.tvMistakes);
        tvCorrectAnswers = findViewById(R.id.tvCorrectAnswers);
        btnRestart = findViewById(R.id.btnRestart);
        btnMainMenu = findViewById(R.id.btnMainMenu);
    }

    private void setupClickListeners() {
        btnRestart.setOnClickListener(v -> restartExercise());
        btnMainMenu.setOnClickListener(v -> returnToMainMenu());
    }

    private void displayResults() {
        Intent intent = getIntent();
        int score = intent.getIntExtra("score", 0);
        int total = intent.getIntExtra("total", 10);
        String exerciseType = intent.getStringExtra("exercise_type");

        // Отображаем результаты
        tvScore.setText(String.format("%d/%d", score, total));

        double percentage = (score * 100.0) / total;
        tvPercentage.setText(String.format("%.1f%%", percentage));

        tvMistakes.setText(String.format("Ошибок: %d", total - score));
        tvCorrectAnswers.setText(String.format("Правильных ответов: %d", score));

        // Устанавливаем сообщение в зависимости от результата
        setResultMessage(percentage);
    }

    private void setResultMessage(double percentage) {
        String message;

        if (percentage >= 90) {
            message = "Отличный результат! Вы мастер интервалов!";
        } else if (percentage >= 70) {
            message = "Хорошая работа! Продолжайте в том же духе!";
        } else if (percentage >= 50) {
            message = "Неплохо! Рекомендуем повторить материал.";
        } else {
            message = "Попрактикуйтесь ещё. Вы обязательно улучшите результат!";
        }

        tvResultMessage.setText(message);
    }

    private void restartExercise() {
        Intent intent = getIntent();
        Intent newIntent = new Intent(this, TrainingIntervalsActivity.class);
        newIntent.putExtra("exercise_type", intent.getStringExtra("exercise_type"));
        newIntent.putExtra("exercise_name", getExerciseName(intent.getStringExtra("exercise_type")));
        startActivity(newIntent);
        finish();
    }

    private void returnToMainMenu() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private String getExerciseName(String exerciseType) {
        switch (exerciseType) {
            case "intervals": return "Интервалы";
            case "chords": return "Аккорды";
            case "scales": return "Гаммы";
            default: return "Упражнение";
        }
    }
}