package com.example.musictrainingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class ExerciseActivity extends AppCompatActivity {

    private CustomButt2 cardIntervals, cardChords, cardSeventhChords, cardNinthChords, cardDictionary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        findViews();
        loadExerciseData();
        setupClickListeners();
    }

    private void findViews() {
        // Находим кастомные компоненты
        cardIntervals = findViewById(R.id.cardIntervals);
        cardChords = findViewById(R.id.cardChords);
        cardSeventhChords = findViewById(R.id.cardSeventhChords);
        cardNinthChords = findViewById(R.id.cardNinthChords);
        cardDictionary = findViewById(R.id.cardDictionary);
    }

    private void loadExerciseData() {
        // Данные о прогрессе
        int intervalAccuracy = 75;
        int chordAccuracy = 60;
        int seventhChordAccuracy = 45;
        int ninthChordAccuracy = 30;
        int dictionaryProgress = 25;

        // Обновляем подзаголовки через методы кастомного компонента
        if (cardIntervals != null) {
            cardIntervals.setSubtitle("Точность: " + intervalAccuracy + "%");
        }
        if (cardChords != null) {
            cardChords.setSubtitle("Точность: " + chordAccuracy + "%");
        }
        if (cardSeventhChords != null) {
            cardSeventhChords.setSubtitle("Точность: " + seventhChordAccuracy + "%");
        }
        if (cardNinthChords != null) {
            cardNinthChords.setSubtitle("Точность: " + ninthChordAccuracy + "%");
        }
        if (cardDictionary != null) {
            cardDictionary.setSubtitle("Изучено: " + dictionaryProgress + "%");
        }
    }

    private void setupClickListeners() {
        // Устанавливаем обработчики кликов на кастомные компоненты
        if (cardIntervals != null) {
            cardIntervals.setOnClickListener(v -> startSpecificExercise(R.id.cardIntervals));
        }
        if (cardChords != null) {
            cardChords.setOnClickListener(v -> startSpecificExercise(R.id.cardChords));
        }
        if (cardSeventhChords != null) {
            cardSeventhChords.setOnClickListener(v -> startSpecificExercise(R.id.cardSeventhChords));
        }
        if (cardNinthChords != null) {
            cardNinthChords.setOnClickListener(v -> startSpecificExercise(R.id.cardNinthChords));
        }
        if (cardDictionary != null) {
            cardDictionary.setOnClickListener(v -> startSpecificExercise(R.id.cardDictionary));
        }
    }

    private void startSpecificExercise(int cardId) {
        Intent intent;
        String exerciseType = "";
        String exerciseName = "";

        if (cardId == R.id.cardIntervals) {
            exerciseType = "intervals";
            exerciseName = "Интервалы";
            intent = new Intent(this, TrainingIntervalsActivity.class);
        }
        else if (cardId == R.id.cardChords) {
            exerciseType = "chords";
            exerciseName = "Трезвучия";
            intent = new Intent(this, TrainingActivityTriads.class);
        }
        else if (cardId == R.id.cardSeventhChords) {
            exerciseType = "seventh_chords";
            exerciseName = "Септаккорды";
            intent = new Intent(this, TrainingActivitySeventhChords.class);
        }
        else if (cardId == R.id.cardNinthChords) {
            exerciseType = "ninth_chords";
            exerciseName = "Нонаккорды";
            intent = new Intent(this, TrainingActivityNinthChords.class);
        }
        else if (cardId == R.id.cardDictionary) {
            exerciseType = "dictionary";
            exerciseName = "Словарь терминов";
            intent = new Intent(this, DictionaryActivity.class);
        } else {
            intent = new Intent(this, TrainingIntervalsActivity.class);
        }

        intent.putExtra("exercise_type", exerciseType);
        intent.putExtra("exercise_name", exerciseName);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}