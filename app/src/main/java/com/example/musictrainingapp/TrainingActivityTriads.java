package com.example.musictrainingapp;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TrainingActivityTriads extends AppCompatActivity implements PianoKeyboardView.OnNotePlayedListener {

    private TextView tvExerciseTitle, tvQuestion, tvSelectedNotes, tvProgress, tvScore;
    private PianoKeyboardView pianoKeyboard;
    private Button btnPlaySound, btnCheckAnswer, btnClear, btnNext;

    private int currentQuestion = 0;
    private int totalQuestions = 10;
    private int score = 0;

    private List<String> selectedNotes = new ArrayList<>();
    private List<Integer> selectedNoteIndexes = new ArrayList<>();
    private List<TriadExercise> exercises = new ArrayList<>();
    private TriadExercise currentExercise;
    private Random random = new Random();

    private SoundPool soundPool;
    private int[] pianoSounds;

    // Класс для упражнений на трезвучия
    private static class TriadExercise {
        String question;
        String triadType;
        String inversion;
        String rootNote;
        String[] correctNotes;
        int[] correctNoteIndexes;

        TriadExercise(String question, String triadType, String inversion, String rootNote,
                      String[] correctNotes, int[] correctNoteIndexes) {
            this.question = question;
            this.triadType = triadType;
            this.inversion = inversion;
            this.rootNote = rootNote;
            this.correctNotes = correctNotes;
            this.correctNoteIndexes = correctNoteIndexes;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        Intent intent = getIntent();
        String exerciseName = intent.getStringExtra("exercise_name");

        initializeSoundPool();
        findViews();
        setupUI(exerciseName);
        generateTriadExercises();
        setupButtonListeners();
        showNextQuestion();
    }

    private void initializeSoundPool() {
        soundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        pianoSounds = new int[24];

        // Загрузка звуков пианино (замените на ваши реальные ресурсы)
        for (int i = 0; i < 24; i++) {
            int resourceId = getResources().getIdentifier("piano_" + (i + 1), "raw", getPackageName());
            if (resourceId != 0) {
                pianoSounds[i] = soundPool.load(this, resourceId, 1);
            }
        }
    }

    private void findViews() {
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

    private void setupUI(String exerciseName) {
        tvExerciseTitle.setText(exerciseName);
        updateProgress();
        updateScore();
        btnCheckAnswer.setEnabled(true);
        btnNext.setEnabled(false);
    }

    private void generateTriadExercises() {
        exercises.clear();

        String[] baseNotes = {"C", "D", "E", "F", "G", "A", "B"};
        String[] triadTypes = {"major", "minor", "diminished", "augmented"};
        String[] inversions = {"основное", "первое обращение", "второе обращение"};

        for (int i = 0; i < totalQuestions; i++) {
            String rootNote = baseNotes[random.nextInt(baseNotes.length)];
            String triadType = triadTypes[random.nextInt(triadTypes.length)];
            String inversion = inversions[random.nextInt(inversions.length)];

            // Генерируем правильные ноты для трезвучия
            String[] correctNotes = generateTriadNotes(rootNote, triadType, inversion);
            int[] correctNoteIndexes = getNoteIndexes(correctNotes);

            String question = String.format("Постройте %s %s трезвучие от ноты %s",
                    inversion, getTriadTypeName(triadType), rootNote);

            exercises.add(new TriadExercise(
                    question, triadType, inversion, rootNote, correctNotes, correctNoteIndexes
            ));
        }
    }

    private String[] generateTriadNotes(String rootNote, String triadType, String inversion) {
        // Определяем интервалы для каждого типа трезвучия
        int thirdInterval;
        int fifthInterval;

        switch (triadType) {
            case "major":
                thirdInterval = 4; // большая терция
                fifthInterval = 7; // чистая квинта
                break;
            case "minor":
                thirdInterval = 3; // малая терция
                fifthInterval = 7; // чистая квинта
                break;
            case "diminished":
                thirdInterval = 3; // малая терция
                fifthInterval = 6; // уменьшенная квинта
                break;
            case "augmented":
                thirdInterval = 4; // большая терция
                fifthInterval = 8; // увеличенная квинта
                break;
            default:
                thirdInterval = 4;
                fifthInterval = 7;
        }

        // Получаем ноты трезвучия
        String thirdNote = getNoteByInterval(rootNote, thirdInterval);
        String fifthNote = getNoteByInterval(rootNote, fifthInterval);

        // Применяем обращения
        switch (inversion) {
            case "первое обращение":// Секстаккорд: терция - квинта - прима (на октаву выше)
                return new String[]{thirdNote, fifthNote, getNoteByInterval(rootNote, 12)};

            case "второе обращение":// Квартсекстаккорд: квинта - прима (на октаву выше) - терция (на октаву выше)
                return new String[]{fifthNote, getNoteByInterval(rootNote, 12), getNoteByInterval(thirdNote, 12)};

            default: // Основное положение: прима - терция - квинта
                return new String[]{rootNote, thirdNote, fifthNote};
        }
    }

    private String getNoteByInterval(String baseNote, int semitones) {
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

    private int[] getNoteIndexes(String[] notes) {
        int[] indexes = new int[notes.length];
        for (int i = 0; i < notes.length; i++) {
            indexes[i] = getNoteIndexOnKeyboard(notes[i]);
        }
        return indexes;
    }

    private int getNoteIndexOnKeyboard(String noteName) {
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

    private String getTriadTypeName(String triadType) {
        switch (triadType) {
            case "major": return "мажорное";
            case "minor": return "минорное";
            case "diminished": return "уменьшенное";
            case "augmented": return "увеличенное";
            default: return "";
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

        // Воспроизводим звук при нажатии на клавишу
        if (noteIndex >= 0 && noteIndex < pianoSounds.length && pianoSounds[noteIndex] != 0) {
            soundPool.play(pianoSounds[noteIndex], 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }

    private void handleNoteSelection(int noteIndex, String noteName) {
        // Если нота уже выбрана, убираем её
        if (selectedNoteIndexes.contains(noteIndex)) {
            int index = selectedNoteIndexes.indexOf(noteIndex);
            selectedNotes.remove(index);
            selectedNoteIndexes.remove(index);
            pianoKeyboard.deselectNote(noteIndex);
        } else {
            // Добавляем новую ноту (максимум 3 ноты)
            if (selectedNotes.size() >= 3) {
                // Убираем самую старую ноту если выбрано больше 3
                int oldestIndex = selectedNoteIndexes.get(0);
                selectedNotes.remove(0);
                selectedNoteIndexes.remove(0);
                pianoKeyboard.deselectNote(oldestIndex);
            }

            selectedNotes.add(noteName);
            selectedNoteIndexes.add(noteIndex);
            pianoKeyboard.selectNote(noteIndex);
        }

        updateSelectedNotesDisplay();
    }

    private void updateSelectedNotesDisplay() {
        StringBuilder builder = new StringBuilder("Выбрано: ");
        for (int i = 0; i < selectedNotes.size(); i++) {
            if (i > 0) builder.append(" - ");
            builder.append(selectedNotes.get(i));
        }

        if (selectedNotes.size() == 3) {
            builder.append(" (трезвучие)");
        } else if (selectedNotes.isEmpty()) {
            builder.append("ничего не выбрано");
        }

        tvSelectedNotes.setText(builder.toString());
    }

    private void playTriadSound() {
        if (currentExercise != null && currentExercise.correctNoteIndexes != null) {
            // Воспроизводим правильное трезвучие
            for (int noteIndex : currentExercise.correctNoteIndexes) {
                if (noteIndex >= 0 && noteIndex < pianoSounds.length && pianoSounds[noteIndex] != 0) {
                    soundPool.play(pianoSounds[noteIndex], 1.0f, 1.0f, 1, 0, 1.0f);
                    try { Thread.sleep(300); } catch (InterruptedException e) {}
                }
            }
        }
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

            StringBuilder correctAnswer = new StringBuilder("Правильно: ");
            for (String note : currentExercise.correctNotes) {
                correctAnswer.append(note).append(" ");
            }

            tvSelectedNotes.setText("✗ Неправильно. " + correctAnswer.toString());
        }

        btnCheckAnswer.setEnabled(false);
        btnNext.setEnabled(true);
        pianoKeyboard.setEnabled(false);
    }

    private boolean checkIfAnswerCorrect() {
        if (selectedNotes.size() != 3) {
            return false;
        }

        // Создаем копии для сравнения (порядок не важен)
        List<String> selectedCopy = new ArrayList<>(selectedNotes);
        List<String> correctCopy = Arrays.asList(currentExercise.correctNotes);

        Collections.sort(selectedCopy);
        Collections.sort(correctCopy);

        return selectedCopy.equals(correctCopy);
    }

    private void clearSelection() {
        // Снимаем выделение со всех нот
        for (int noteIndex : selectedNoteIndexes) {
            pianoKeyboard.deselectNote(noteIndex);
        }

        selectedNotes.clear();
        selectedNoteIndexes.clear();
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
        tvScore.setText(String.format("Счёт: %d/%d", score, currentQuestion + 1));
    }

    private void finishExercise() {
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("total", totalQuestions);
        intent.putExtra("exercise_type", "triads");
        startActivity(intent);
        finish();
    }

    private void setupButtonListeners() {
        btnPlaySound.setOnClickListener(v -> playTriadSound());
        btnCheckAnswer.setOnClickListener(v -> checkAnswer());
        btnClear.setOnClickListener(v -> clearSelection());
        btnNext.setOnClickListener(v -> nextQuestion());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}