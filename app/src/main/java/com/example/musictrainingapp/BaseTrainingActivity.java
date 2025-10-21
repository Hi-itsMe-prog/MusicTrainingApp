package com.example.musictrainingapp;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.util.*;

public abstract class BaseTrainingActivity extends AppCompatActivity
        implements PianoKeyboardView.OnNotePlayedListener {

    // Общие UI элементы
    protected TextView tvExerciseTitle, tvQuestion, tvSelectedNotes, tvProgress, tvScore;
    protected PianoKeyboardView pianoKeyboard;
    protected Button btnPlaySound, btnCheckAnswer, btnClear, btnNext;

    // Общие переменные состояния
    protected int currentQuestion = 0;
    protected int totalQuestions = 10;
    protected int score = 0;
    protected List<String> selectedNotes = new ArrayList<>();
    protected List<Integer> selectedNoteIndexes = new ArrayList<>();
    protected List<SimpleExercise> exercises = new ArrayList<>();
    protected SimpleExercise currentExercise;
    protected Random random = new Random();


    // Простая структура для упражнений
    public static class SimpleExercise {
        public final String question;
        public final String[] correctNotes;
        public final int[] correctNoteIndexes;

        public SimpleExercise(String question, String[] correctNotes, int[] correctNoteIndexes) {
            this.question = question;
            this.correctNotes = correctNotes;
            this.correctNoteIndexes = correctNoteIndexes;
        }
    }

    // Абстрактные методы, которые должны реализовать дочерние классы
    protected abstract String getExerciseType();
    protected abstract int getMaxNotes();
    protected abstract String getChordName();
    protected abstract List<SimpleExercise> generateExercises();

    // Базовые ноты для всех упражнений
    protected final String[] baseNotes = {"C", "D", "E", "F", "G", "A", "B"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_intervals);

        Intent intent = getIntent();
        String exerciseName = intent.getStringExtra("exercise_name");

        findViews();
        setupUI(exerciseName);
        exercises = generateExercises();
        setupButtonListeners();
        showNextQuestion();
    }

    // ОБЩИЕ МЕТОДЫ ДЛЯ ВСЕХ АКТИВНОСТЕЙ

    protected void findViews() {
        tvExerciseTitle = findViewById(R.id.tvExerciseTitle);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvSelectedNotes = findViewById(R.id.tvSelectedNotes);
        tvProgress = findViewById(R.id.tvProgress);
        tvScore = findViewById(R.id.tvScore);
        pianoKeyboard = findViewById(R.id.pianoKeyboard);
        btnPlaySound = findViewById(R.id.btnPlaySound);
        btnCheckAnswer = findViewById(R.id.btnCheckAnswer);
        btnClear = findViewById(R.id.btnClear);
        btnNext = findViewById(R.id.btnNext);

        pianoKeyboard.setOnNotePlayedListener(this);
    }

    protected void setupUI(String exerciseName) {
        if (tvExerciseTitle != null) {
            tvExerciseTitle.setText(exerciseName);
        }
        updateProgress();
        updateScore();

        if (btnCheckAnswer != null) {
            btnCheckAnswer.setEnabled(true);
        }
        if (btnNext != null) {
            btnNext.setEnabled(false);
        }

        if (pianoKeyboard != null) {
            pianoKeyboard.setEnabled(true);
        }
    }

    protected void showNextQuestion() {
        if (currentQuestion == totalQuestions) {
            finishExercise();
            return;
        }

        // Очищаем предыдущий выбор
        selectedNotes.clear();
        selectedNoteIndexes.clear();
        if (pianoKeyboard != null) {
            pianoKeyboard.clearSelection();
        }

        // Показываем следующий вопрос
        currentExercise = exercises.get(currentQuestion);
        if (tvQuestion != null) {
            tvQuestion.setText(currentExercise.question);
        }
        updateProgress();

        // Обновляем состояние кнопок
        if (btnCheckAnswer != null) {
            btnCheckAnswer.setEnabled(true);
        }
        if (btnNext != null) {
            btnNext.setEnabled(false);
        }
        if (pianoKeyboard != null) {
            pianoKeyboard.setEnabled(true);
        }

        if (tvSelectedNotes != null) {
            tvSelectedNotes.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        }
    }

    // ОБРАБОТКА НАЖАТИЙ КЛАВИШ

    protected void handleNoteSelection(int noteIndex, String noteName) {
        // Если нота уже выбрана - убираем её
        if (selectedNoteIndexes.contains(noteIndex)) {
            int index = selectedNoteIndexes.indexOf(noteIndex);
            selectedNotes.remove(index);
            selectedNoteIndexes.remove(index);
            if (pianoKeyboard != null) {
                pianoKeyboard.clearSelection(noteIndex);
            }
        } else {
            // Если достигли максимума нот - убираем самую старую
            if (selectedNotes.size() >= getMaxNotes()) {
                int oldestIndex = selectedNoteIndexes.get(0);
                selectedNotes.remove(0);
                selectedNoteIndexes.remove(0);
                if (pianoKeyboard != null) {
                    pianoKeyboard.clearSelection(oldestIndex);
                }
            }

            // Добавляем новую ноту
            selectedNotes.add(noteName);
            selectedNoteIndexes.add(noteIndex);
            if (pianoKeyboard != null) {
                pianoKeyboard.selectNote(noteIndex);
            }
        }
    }




    // ПРОВЕРКА ОТВЕТА
    protected void checkAnswer() {
        boolean isCorrect = checkIfAnswerCorrect();

        if (tvSelectedNotes != null) {
            if (isCorrect) {
                score++;
                updateScore();
                tvSelectedNotes.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
                tvSelectedNotes.setText("✓ Правильно! " + tvSelectedNotes.getText());
            } else {
                tvSelectedNotes.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));

                StringBuilder correctAnswer = new StringBuilder("Правильно: ");
                for (String note : currentExercise.correctNotes) {
                    correctAnswer.append(note).append(" ");
                }

                tvSelectedNotes.setText("✗ Неправильно. " + correctAnswer.toString());
            }
        }

        // Обновляем состояние кнопок
        if (btnCheckAnswer != null) {
            btnCheckAnswer.setEnabled(false);
        }
        if (btnNext != null) {
            btnNext.setEnabled(true);
        }
        if (pianoKeyboard != null) {
            pianoKeyboard.setEnabled(false);
        }
    }

    protected boolean checkIfAnswerCorrect() {
        if (selectedNotes.size() != getMaxNotes()) {
            return false;
        }

        // Создаем копии для сравнения (порядок не важен для аккордов)
        List<String> selectedCopy = new ArrayList<>(selectedNotes);
        List<String> correctCopy = new ArrayList<>(Arrays.asList(currentExercise.correctNotes));

        Collections.sort(selectedCopy);
        Collections.sort(correctCopy);

        return selectedCopy.equals(correctCopy);
    }

    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    protected void clearSelection() {
        // Снимаем выделение со всех нот
        for (int noteIndex : selectedNoteIndexes) {
            if (pianoKeyboard != null) {
                pianoKeyboard.clearSelection(noteIndex);
            }
        }

        selectedNotes.clear();
        selectedNoteIndexes.clear();

        if (tvSelectedNotes != null) {
            tvSelectedNotes.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        }
    }

    /**кнопка перехода на следующий вопрос*/
    protected void nextQuestion() {
        currentQuestion++;
        showNextQuestion();
    }

    protected void updateProgress() {
        if (tvProgress != null) {
            tvProgress.setText(String.format("Вопрос %d/%d", currentQuestion + 1, totalQuestions));
        }
    }

    protected void updateScore() {
        if (tvScore != null) {
            tvScore.setText(String.format("Счёт: %d/%d", score, currentQuestion + 1));
        }
    }

    protected void finishExercise() {
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("total", totalQuestions);
        intent.putExtra("exercise_type", getExerciseType());
        startActivity(intent);
        finish();
    }

    protected void setupButtonListeners() {

        if (btnCheckAnswer != null) {
            btnCheckAnswer.setOnClickListener(v -> checkAnswer());
        }

        if (btnClear != null) {
            btnClear.setOnClickListener(v -> clearSelection());
        }

        if (btnNext != null) {
            btnNext.setOnClickListener(v -> nextQuestion());
        }
    }

    // УТИЛИТНЫЕ МЕТОДЫ ДЛЯ РАБОТЫ С НОТАМИ

    protected int[] getNoteIndexes(String[] notes) {
        int[] indexes = new int[notes.length];
        for (int i = 0; i < notes.length; i++) {
            indexes[i] = getNoteIndexOnKeyboard(notes[i]);
        }
        return indexes;
    }

    protected int getNoteIndexOnKeyboard(String noteName) {
        String[] keyboardNotes = {
                "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B",
                "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"
        };

        for (int i = 0; i < keyboardNotes.length; i++) {
            if (keyboardNotes[i].equals(noteName)) {
                return i;
            }
        }
        return 0;
    }

    protected String getNoteByInterval(String baseNote, int semitones) {
        String[] chromaticScale = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

        int baseIndex = -1;
        for (int i = 0; i < chromaticScale.length; i++) {
            if (chromaticScale[i].equals(baseNote)) {
                baseIndex = i;
                break;
            }
        }

        if (baseIndex == -1) return baseNote;

        int targetIndex = (baseIndex + semitones) % 12;
        if (targetIndex < 0) targetIndex += 12;

        return chromaticScale[targetIndex];
    }
}