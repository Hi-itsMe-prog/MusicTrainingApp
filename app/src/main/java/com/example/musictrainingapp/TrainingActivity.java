package com.example.musictrainingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TrainingActivity extends AppCompatActivity implements PianoKeyboardView.OnNotePlayedListener {

    private TextView tvExerciseTitle, tvQuestion, tvSelectedNotes, tvProgress, tvScore;
    private PianoKeyboardView pianoKeyboard;
    private Button btnCheckAnswer, btnClear, btnNext;

    private int currentQuestion = 0;
    private int totalQuestions = 10;
    private int score = 0;

    private List<String> selectedNotes = new ArrayList<>();
    private List<Integer> selectedNoteIndexes = new ArrayList<>();
    private List<IntervalExercise> exercises = new ArrayList<>();
    private IntervalExercise currentExercise;
    private Random random = new Random();

    // Простой класс для упражнений на интервалы
    private static class IntervalExercise {
        String question;
        String correctAnswer;
        int requiredSemitones;
        boolean isUpward;

        IntervalExercise(String question, String correctAnswer, int requiredSemitones, boolean isUpward) {
            this.question = question;
            this.correctAnswer = correctAnswer;
            this.requiredSemitones = requiredSemitones;
            this.isUpward = isUpward;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        Intent intent = getIntent();
        String exerciseName = intent.getStringExtra("exercise_name");

        findViews();
        setupUI(exerciseName);
        generateExercises();
        setupButtonListeners();
        showNextQuestion();
    }

    private void findViews() {
        tvExerciseTitle = findViewById(R.id.tvExerciseTitle);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvSelectedNotes = findViewById(R.id.tvSelectedNotes);
        tvProgress = findViewById(R.id.tvProgress);
        tvScore = findViewById(R.id.tvScore);
        pianoKeyboard = findViewById(R.id.pianoKeyboard);
        btnCheckAnswer = findViewById(R.id.btnCheckAnswer);
        btnClear = findViewById(R.id.btnClear);
        btnNext = findViewById(R.id.btnNext);

        pianoKeyboard.setOnNotePlayedListener(this);
    }

    private void setupUI(String exerciseName) {
        tvExerciseTitle.setText(exerciseName);
        updateProgress();
        updateScore();
        btnCheckAnswer.setEnabled(true);
        btnNext.setEnabled(false);
    }

    private void generateExercises() {
        exercises.clear();

        // Базовая нота и интервалы для упражнений
        String[] baseNotes = {"C", "D", "E", "F", "G", "A", "B"};
        String[] intervalNames = {
                "малую секунду", "большую секунду", "малую терцию", "большую терцию",
                "кварту", "увеличенную кварту", "квинту", "малую сексту", "большую сексту",
                "малую септиму", "большую септиму", "октаву"
        };
        int[] intervalSemitones = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};

        for (int i = 0; i < totalQuestions; i++) {
            String baseNote = baseNotes[random.nextInt(baseNotes.length)];
            int intervalIndex = random.nextInt(intervalNames.length);
            boolean isUpward = random.nextBoolean();

            String direction = isUpward ? "вверх" : "вниз";
            String question = String.format("Постройте %s от ноты %s %s",
                    intervalNames[intervalIndex], baseNote, direction);

            // Для простоты используем только названия нот без конкретных индексов
            String correctAnswer = baseNote + " → [целевая нота]";

            exercises.add(new IntervalExercise(
                    question, correctAnswer, intervalSemitones[intervalIndex], isUpward
            ));
        }
    }

    private void showNextQuestion() {
        if (currentQuestion >= totalQuestions) {
            finishExercise();
            return;
        }

        selectedNotes.clear();
        selectedNoteIndexes.clear();
        pianoKeyboard.clearSelection();
        updateSelectedNotesDisplay();

        currentExercise = exercises.get(currentQuestion);
        tvQuestion.setText(currentExercise.question);
        updateProgress();

        btnCheckAnswer.setEnabled(true);
        btnNext.setEnabled(false);
        pianoKeyboard.setEnabled(true);
        tvSelectedNotes.setTextColor(ContextCompat.getColor(this, android.R.color.black));
    }

    @Override
    public void onNotePlayed(int noteIndex, String noteName) {
        handleNoteSelection(noteIndex, noteName);
    }

    private void handleNoteSelection(int noteIndex, String noteName) {
        // Очищаем предыдущий выбор, если уже выбрано 2 ноты
        if (selectedNotes.size() >= 2) {
            selectedNotes.clear();
            selectedNoteIndexes.clear();
            pianoKeyboard.clearSelection();
        }

        // Добавляем новую ноту в выбор
        selectedNotes.add(noteName);
        selectedNoteIndexes.add(noteIndex);

        // ОБНОВЛЯЕМ ОТОБРАЖЕНИЕ БЕЗ АВТОМАТИЧЕСКОЙ КОРРЕКЦИИ НАПРАВЛЕНИЯ
        // Пользователь должен сам правильно выбрать направление
        updateSelectedNotesDisplay();
    }

    private void swapSelectedNotes() {
        String tempNote = selectedNotes.get(0);
        selectedNotes.set(0, selectedNotes.get(1));
        selectedNotes.set(1, tempNote);

        int tempIndex = selectedNoteIndexes.get(0);
        selectedNoteIndexes.set(0, selectedNoteIndexes.get(1));
        selectedNoteIndexes.set(1, tempIndex);
    }

    private void updateSelectedNotesDisplay() {
        StringBuilder builder = new StringBuilder("Выбрано: ");
        for (String note : selectedNotes) {
            builder.append(note).append(" ");
        }

        if (selectedNotes.size() == 2) {
            int actualSemitones = Math.abs(selectedNoteIndexes.get(1) - selectedNoteIndexes.get(0));
            builder.append("(").append(actualSemitones).append(" полутонов)");
        }

        tvSelectedNotes.setText(builder.toString());
    }

    private void checkAnswer() {
        boolean isCorrect = checkIfAnswerCorrect();

        if (isCorrect) {
            score++;
            updateScore();
            tvSelectedNotes.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
            tvSelectedNotes.setText("✓ Правильно! " + tvSelectedNotes.getText());
        } else {
            tvSelectedNotes.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
            tvSelectedNotes.setText("✗ Неправильно. Нужно: " + currentExercise.requiredSemitones + " полутонов " +
                    (currentExercise.isUpward ? "вверх" : "вниз"));
        }

        btnCheckAnswer.setEnabled(false);
        btnNext.setEnabled(true);
        pianoKeyboard.setEnabled(false);
    }

    private boolean checkIfAnswerCorrect() {
        if (selectedNotes.size() != 2) {
            return false;
        }

        int selectedIndex1 = selectedNoteIndexes.get(0);
        int selectedIndex2 = selectedNoteIndexes.get(1);
        int actualSemitones = Math.abs(selectedIndex2 - selectedIndex1);

        // Проверяем количество полутонов
        if (actualSemitones != currentExercise.requiredSemitones) {
            return false;
        }

        // Проверяем правильное направление (ВАЖНО!)
        if (currentExercise.isUpward) {
            // Для построения вверх: вторая нота должна быть ВЫШЕ первой
            return selectedIndex2 > selectedIndex1;
        } else {
            // Для построения вниз: вторая нота должна быть НИЖЕ первой
            return selectedIndex2 < selectedIndex1;
        }
    }



    private void clearSelection() {
        selectedNotes.clear();
        selectedNoteIndexes.clear();
        pianoKeyboard.clearSelection();
        updateSelectedNotesDisplay();
        tvSelectedNotes.setTextColor(ContextCompat.getColor(this, android.R.color.black));
    }

    private void nextQuestion() {
        currentQuestion++;
        showNextQuestion();
    }

    private void updateProgress() {
        tvProgress.setText(String.format("Вопрос %d/%d", currentQuestion + 1, totalQuestions));
    }

    private void updateScore() {
        tvScore.setText(String.format("Счёт: %d/%d", score, currentQuestion));
    }

    private void finishExercise() {
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("total", totalQuestions);
        intent.putExtra("exercise_type", "intervals");
        startActivity(intent);
        finish();
    }

    private void setupButtonListeners() {
        btnCheckAnswer.setOnClickListener(v -> checkAnswer());
        btnClear.setOnClickListener(v -> clearSelection());
        btnNext.setOnClickListener(v -> nextQuestion());
    }
}