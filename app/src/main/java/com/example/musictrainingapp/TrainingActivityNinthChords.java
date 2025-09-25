package com.example.musictrainingapp;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TrainingActivityNinthChords extends AppCompatActivity implements PianoKeyboardView.OnNotePlayedListener {

    private TextView tvExerciseTitle, tvQuestion, tvSelectedNotes, tvProgress, tvScore;
    private PianoKeyboardView pianoKeyboard;
    private Button btnPlaySound, btnCheckAnswer, btnClear, btnNext;

    private int currentQuestion = 0;
    private int totalQuestions = 8; // Меньше вопросов для нонаккордов
    private int score = 0;

    private List<String> selectedNotes = new ArrayList<>();
    private List<Integer> selectedNoteIndexes = new ArrayList<>();
    private List<NinthChordExercise> exercises = new ArrayList<>();
    private NinthChordExercise currentExercise;
    private Random random = new Random();

    private SoundPool soundPool;
    private int[] pianoSounds;

    private static class NinthChordExercise {
        String question;
        String chordType;
        String rootNote;
        String[] correctNotes;
        int[] correctNoteIndexes;

        NinthChordExercise(String question, String chordType, String rootNote,
                           String[] correctNotes, int[] correctNoteIndexes) {
            this.question = question;
            this.chordType = chordType;
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
        generateNinthChordExercises();
        setupButtonListeners();
        showNextQuestion();
    }

    private void initializeSoundPool() {
        soundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        pianoSounds = new int[24];

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

    private void generateNinthChordExercises() {
        exercises.clear();

        String[] baseNotes = {"C", "D", "E", "F", "G", "A", "B"};
        String[] chordTypes = {"major9", "minor9", "dominant9"};

        for (int i = 0; i < totalQuestions; i++) {
            String rootNote = baseNotes[random.nextInt(baseNotes.length)];
            String chordType = chordTypes[random.nextInt(chordTypes.length)];

            String[] correctNotes = generateNinthChordNotes(rootNote, chordType);
            int[] correctNoteIndexes = getNoteIndexes(correctNotes);

            String question = String.format("Постройте %s нонаккорд от ноты %s",
                    getChordTypeName(chordType), rootNote);

            exercises.add(new NinthChordExercise(
                    question, chordType, rootNote, correctNotes, correctNoteIndexes
            ));
        }
    }

    private String[] generateNinthChordNotes(String rootNote, String chordType) {
        int[] intervals;
        switch (chordType) {
            case "major9":
                intervals = new int[]{4, 7, 11, 14}; // большой мажорный септаккорд + большая нона
                break;
            case "minor9":
                intervals = new int[]{3, 7, 10, 14}; // малый минорный септаккорд + большая нона
                break;
            case "dominant9":
                intervals = new int[]{4, 7, 10, 14}; // доминантсептаккорд + большая нона
                break;
            default:
                intervals = new int[]{4, 7, 10, 14};
        }

        return new String[]{
                rootNote,
                getNoteByInterval(rootNote, intervals[0]),
                getNoteByInterval(rootNote, intervals[1]),
                getNoteByInterval(rootNote, intervals[2]),
                getNoteByInterval(rootNote, intervals[3])
        };
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

    private String getChordTypeName(String chordType) {
        switch (chordType) {
            case "major9": return "большой мажорный";
            case "minor9": return "малый минорный";
            case "dominant9": return "доминантовый";
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

        if (noteIndex >= 0 && noteIndex < pianoSounds.length && pianoSounds[noteIndex] != 0) {
            soundPool.play(pianoSounds[noteIndex], 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }

    private void handleNoteSelection(int noteIndex, String noteName) {
        if (selectedNoteIndexes.contains(noteIndex)) {
            int index = selectedNoteIndexes.indexOf(noteIndex);
            selectedNotes.remove(index);
            selectedNoteIndexes.remove(index);
            pianoKeyboard.deselectNote(noteIndex);
        } else {
            if (selectedNotes.size() >= 5) {
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

        if (selectedNotes.size() == 5) {
            builder.append(" (нонаккорд)");
        } else if (selectedNotes.isEmpty()) {
            builder.append("ничего не выбрано");
        }

        tvSelectedNotes.setText(builder.toString());
    }

    private void playChordSound() {
        if (currentExercise != null && currentExercise.correctNoteIndexes != null) {
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
        if (selectedNotes.size() != 5) {
            return false;
        }

        List<String> selectedCopy = new ArrayList<>(selectedNotes);
        List<String> correctCopy = Arrays.asList(currentExercise.correctNotes);

        Collections.sort(selectedCopy);
        Collections.sort(correctCopy);

        return selectedCopy.equals(correctCopy);
    }

    private void clearSelection() {
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
        intent.putExtra("exercise_type", "ninth_chords");
        startActivity(intent);
        finish();
    }

    private void setupButtonListeners() {
        btnPlaySound.setOnClickListener(v -> playChordSound());
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