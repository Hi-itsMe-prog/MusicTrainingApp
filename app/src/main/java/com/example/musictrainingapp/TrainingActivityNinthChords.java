package com.example.musictrainingapp;

import android.os.Bundle;
import java.util.*;

public class TrainingActivityNinthChords extends BaseTrainingActivity {

    // Типы нонаккордов
    private final String[] chordTypes = {
            "major9",      // большой мажорный
            "minor9",      // малый минорный
            "dominant9"    // доминантовый
    };

    // Типы обращений (для нонаккордов можно использовать меньше обращений)
    private final String[] inversionTypes = {
            "root",     // основное положение
            "first",    // первое обращение
            "second"    // второе обращение
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeActivity(R.layout.activity_training_ninth_chords);
    }

    @Override
    protected String getExerciseType() {
        return "ninth_chords";
    }

    @Override
    protected int getMaxNotes() {
        return 5;
    }

    @Override
    protected String getChordName() {
        return "нонаккорд";
    }

    @Override
    protected List<SimpleExercise> generateExercises() {
        // Меньше вопросов для нонаккордов
        totalQuestions = 8;

        List<SimpleExercise> exercises = new ArrayList<>();

        for (int i = 0; i < totalQuestions; i++) {
            String rootNote = baseNotes[random.nextInt(baseNotes.length)];
            String chordType = chordTypes[random.nextInt(chordTypes.length)];
            String inversionType = inversionTypes[random.nextInt(inversionTypes.length)];

            // Генерируем правильные ноты для нонаккорда с обращением
            ChordData chordData = getChordNotesWithInversion(rootNote, chordType, inversionType);
            String[] correctNotes = chordData.notes;
            int[] correctNoteIndexes = getNoteIndexes(correctNotes);

            String question = String.format("Постройте %s от ноты %s",
                    chordData.displayName, chordData.bassNote);

            exercises.add(new SimpleExercise(question, correctNotes, correctNoteIndexes));
        }

        return exercises;
    }

    private ChordData getChordNotesWithInversion(String rootNote, String chordType, String inversionType) {
        // Сначала получаем нонаккорд в основном положении
        String[] rootPosition = getNinthChordRootPosition(rootNote, chordType);

        // Затем применяем обращение и получаем басовую ноту
        ChordInversionResult inversionResult = applyInversion(rootPosition, inversionType);

        // Формируем отображаемое название
        String displayName = getNinthChordDisplayName(chordType, inversionType);

        return new ChordData(inversionResult.notes, inversionResult.bassNote, displayName);
    }

    private String[] getNinthChordRootPosition(String rootNote, String chordType) {
        switch (chordType) {
            case "major9": // большой мажорный септаккорд + большая нона
                return new String[]{
                        rootNote,
                        getNoteByInterval(rootNote, 4),  // большая терция
                        getNoteByInterval(rootNote, 7),  // чистая квинта
                        getNoteByInterval(rootNote, 11), // большая септима
                        getNoteByInterval(rootNote, 14)  // большая нона
                };
            case "minor9": // малый минорный септаккорд + большая нона
                return new String[]{
                        rootNote,
                        getNoteByInterval(rootNote, 3),  // малая терция
                        getNoteByInterval(rootNote, 7),  // чистая квинта
                        getNoteByInterval(rootNote, 10), // малая септима
                        getNoteByInterval(rootNote, 14)  // большая нона
                };
            case "dominant9": // доминантсептаккорд + большая нона
                return new String[]{
                        rootNote,
                        getNoteByInterval(rootNote, 4),  // большая терция
                        getNoteByInterval(rootNote, 7),  // чистая квинта
                        getNoteByInterval(rootNote, 10), // малая септима
                        getNoteByInterval(rootNote, 14)  // большая нона
                };
            default:
                return new String[]{rootNote, rootNote, rootNote, rootNote, rootNote};
        }
    }

    private ChordInversionResult applyInversion(String[] chordNotes, String inversionType) {
        String bassNote;
        String[] notes;

        switch (inversionType) {
            case "root": // основное положение
                bassNote = chordNotes[0]; // бас - прима
                notes = new String[]{
                        chordNotes[0],
                        chordNotes[1],
                        chordNotes[2],
                        chordNotes[3],
                        chordNotes[4]
                };
                break;

            case "first": // первое обращение
                bassNote = chordNotes[1]; // бас - терция
                notes = new String[]{
                        chordNotes[1],
                        chordNotes[2],
                        chordNotes[3],
                        chordNotes[4],
                        getNoteByInterval(chordNotes[0], 12) // прима на октаву выше
                };
                break;

            case "second": // второе обращение
                bassNote = chordNotes[2]; // бас - квинта
                notes = new String[]{
                        chordNotes[2],
                        chordNotes[3],
                        chordNotes[4],
                        getNoteByInterval(chordNotes[0], 12), // прима на октаву выше
                        getNoteByInterval(chordNotes[1], 12)  // терция на октаву выше
                };
                break;

            default:
                bassNote = chordNotes[0];
                notes = chordNotes;
        }

        return new ChordInversionResult(notes, bassNote);
    }

    private String getNinthChordDisplayName(String chordType, String inversionType) {
        String chordName = "";
        String inversionName = "";

        // Название нонаккорда
        switch (chordType) {
            case "major9": chordName = "большой мажорный"; break;
            case "minor9": chordName = "малый минорный"; break;
            case "dominant9": chordName = "доминантовый"; break;
        }

        // Название обращения
        switch (inversionType) {
            case "root": inversionName = "нонаккорд"; break;
            case "first": inversionName = "нонаккорд (1 обращение)"; break;
            case "second": inversionName = "нонаккорд (2 обращение)"; break;
        }

        return chordName + " " + inversionName;
    }

    // Вспомогательные классы для хранения данных аккорда
    private static class ChordData {
        String[] notes;
        String bassNote;
        String displayName;

        ChordData(String[] notes, String bassNote, String displayName) {
            this.notes = notes;
            this.bassNote = bassNote;
            this.displayName = displayName;
        }
    }

    private static class ChordInversionResult {
        String[] notes;
        String bassNote;

        ChordInversionResult(String[] notes, String bassNote) {
            this.notes = notes;
            this.bassNote = bassNote;
        }
    }

    // Старый метод для обратной совместимости
    @Override
    protected String[] getChordNotesWithCorrectVoicing(String rootNote, String chordType) {
        // Для простоты используем только основное положение
        return getNinthChordRootPosition(rootNote, "major9");
    }
}