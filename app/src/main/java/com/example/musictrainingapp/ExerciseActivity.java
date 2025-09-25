package com.example.musictrainingapp;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.musictrainingapp.R;

public class ExerciseActivity extends AppCompatActivity {

    private TextView tvRecommendation;
    private TextView tvIntervalProgress, tvChordProgress, tvSeventhChordProgress, tvNinthChordProgress, tvDictionaryProgress;
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        resources = getResources();
        findViews();
        loadExerciseData();
        setupClickListeners();
    }

    private void findViews() {
        tvRecommendation = findViewById(R.id.tvRecommendation);
        tvIntervalProgress = findViewById(R.id.tvIntervalProgress);
        tvChordProgress = findViewById(R.id.tvChordProgress);
        tvSeventhChordProgress = findViewById(R.id.tvSeventhChordProgress);
        tvNinthChordProgress = findViewById(R.id.tvNinthChordProgress);
        tvDictionaryProgress = findViewById(R.id.tvDictionaryProgress);
    }

    private void loadExerciseData() {
        String recommendation = resources.getString(R.string.recommendation_default);
        int intervalAccuracy = 0;
        int chordAccuracy = 0;
        int seventhChordAccuracy = 0;
        int ninthChordAccuracy = 0;
        int dictionaryProgress = 0;

        tvRecommendation.setText(recommendation);
        tvIntervalProgress.setText(String.format(resources.getString(R.string.progress_accuracy), intervalAccuracy));
        tvChordProgress.setText(String.format(resources.getString(R.string.progress_accuracy), chordAccuracy));
        tvSeventhChordProgress.setText(String.format(resources.getString(R.string.progress_accuracy), seventhChordAccuracy));
        tvNinthChordProgress.setText(String.format(resources.getString(R.string.progress_accuracy), ninthChordAccuracy));
        tvDictionaryProgress.setText(String.format(resources.getString(R.string.progress_studied), dictionaryProgress));
    }

    private void setupClickListeners() {
        int[] cardIds = {
                R.id.cardIntervals,
                R.id.cardChords,
                R.id.cardSeventhChords,
                R.id.cardNinthChords,
                R.id.cardDictionary
        };

        for (int cardId : cardIds) {
            findViewById(cardId).setOnClickListener(v -> startSpecificExercise(v.getId()));
        }
    }

    private void startSpecificExercise(int cardId) {
        Intent intent;
        String exerciseType = "";
        String exerciseName = "";

        if (cardId == R.id.cardIntervals) {
            exerciseType = getString(R.string.exercise_type_intervals);
            exerciseName = getString(R.string.exercise_name_intervals);
            intent = new Intent(this, TrainingActivity.class);
        }
        else if (cardId == R.id.cardChords) {
            exerciseType = getString(R.string.exercise_type_chords);
            exerciseName = getString(R.string.exercise_name_chords);
            intent = new Intent(this, TrainingActivityTriads.class);
        }
        else if (cardId == R.id.cardSeventhChords) {
            exerciseType = getString(R.string.exercise_type_seventh_chords);
            exerciseName = getString(R.string.exercise_name_seventh_chords);
            intent = new Intent(this, TrainingActivitySeventhChords.class);
        }
        else if (cardId == R.id.cardNinthChords) {
            exerciseType = getString(R.string.exercise_type_ninth_chords);
            exerciseName = getString(R.string.exercise_name_ninth_chords);
            intent = new Intent(this, TrainingActivityNinthChords.class);
        }
        else if (cardId == R.id.cardDictionary) {
            exerciseType = getString(R.string.exercise_type_dictionary);
            exerciseName = getString(R.string.exercise_name_dictionary);
            intent = new Intent(this, TrainingActivity.class); // или создайте отдельную активити
        } else {
            intent = new Intent(this, TrainingActivity.class);
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

    @Override
    protected void onResume() {
        super.onResume();
        loadExerciseData();
    }
}