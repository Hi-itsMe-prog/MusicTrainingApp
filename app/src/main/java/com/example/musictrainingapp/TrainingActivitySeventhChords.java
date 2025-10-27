package com.example.musictrainingapp;

import android.os.Bundle;
import java.util.*;

public class TrainingActivitySeventhChords extends BaseTrainingActivity {

    // Типы септаккордов
    private final String[] chordTypes = {
            "major7",           // большой мажорный
            "minor7",           // малый минорный
            "dominant7",        // доминантовый
            "half-diminished7", // полууменьшенный
            "diminished7"       // уменьшенный
    };

    // Типы обращений
    private final String[] inversionTypes = {
            "root",     // основное положение
            "first",    // первое обращение (квинтсекстаккорд)
            "second",   // второе обращение (терцквартаккорд)
            "third"     // третье обращение (секундаккорд)
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeActivity(R.layout.activity_training_seventh_chords);
    }

    @Override
    protected String getExerciseType() {
        return "seventh_chords";
    }

    @Override
    protected int getMaxNotes() {
        return 4;
    }

    @Override
    protected String getChordName() {
        return "септаккорд";
    }

    @Override
    protected List<SimpleExercise> generateExercises() {
        List<SimpleExercise> exercises = new ArrayList<>();

        for (int i = 0; i < totalQuestions; i++) {
            String rootNote = baseNotes[random.nextInt(baseNotes.length)];
            String chordType = chordTypes[random.nextInt(chordTypes.length)];
            String inversionType = inversionTypes[random.nextInt(inversionTypes.length)];

            // Генерируем правильные ноты для септаккорда с обращением
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
        // Сначала получаем септаккорд в основном положении
        String[] rootPosition = getSeventhChordRootPosition(rootNote, chordType);

        // Затем применяем обращение и получаем басовую ноту
        ChordInversionResult inversionResult = applyInversion(rootPosition, inversionType);

        // Формируем отображаемое название
        String displayName = getSeventhChordDisplayName(chordType, inversionType);

        return new ChordData(inversionResult.notes, inversionResult.bassNote, displayName);
    }

    private String[] getSeventhChordRootPosition(String rootNote, String chordType) {
        switch (chordType) {
            case "major7": // мажорное трезвучие + большая септима
                return new String[]{
                        rootNote,
                        getNoteByInterval(rootNote, 4),  // большая терция
                        getNoteByInterval(rootNote, 7),  // чистая квинта
                        getNoteByInterval(rootNote, 11)  // большая септима
                };
            case "minor7": // минорное трезвучие + малая септима
                return new String[]{
                        rootNote,
                        getNoteByInterval(rootNote, 3),  // малая терция
                        getNoteByInterval(rootNote, 7),  // чистая квинта
                        getNoteByInterval(rootNote, 10)  // малая септима
                };
            case "dominant7": // мажорное трезвучие + малая септима
                return new String[]{
                        rootNote,
                        getNoteByInterval(rootNote, 4),  // большая терция
                        getNoteByInterval(rootNote, 7),  // чистая квинта
                        getNoteByInterval(rootNote, 10)  // малая септима
                };
            case "half-diminished7": // уменьшенное трезвучие + малая септима
                return new String[]{
                        rootNote,
                        getNoteByInterval(rootNote, 3),  // малая терция
                        getNoteByInterval(rootNote, 6),  // уменьшенная квинта
                        getNoteByInterval(rootNote, 10)  // малая септима
                };
            case "diminished7": // уменьшенное трезвучие + уменьшенная септима
                return new String[]{
                        rootNote,
                        getNoteByInterval(rootNote, 3),  // малая терция
                        getNoteByInterval(rootNote, 6),  // уменьшенная квинта
                        getNoteByInterval(rootNote, 9)   // уменьшенная септима
                };
            default:
                return new String[]{rootNote, rootNote, rootNote, rootNote};
        }
    }

    private ChordInversionResult applyInversion(String[] chordNotes, String inversionType) {
        String bassNote;
        String[] notes;

        switch (inversionType) {
            case "root": // основное положение: прима - терция - квинта - септима
                bassNote = chordNotes[0]; // бас - прима
                notes = new String[]{
                        chordNotes[0],
                        chordNotes[1],
                        chordNotes[2],
                        chordNotes[3]
                };
                break;

            case "first": // первое обращение (квинтсекстаккорд): терция - квинта - септима - прима(+октава)
                bassNote = chordNotes[1]; // бас - терция
                notes = new String[]{
                        chordNotes[1],
                        chordNotes[2],
                        chordNotes[3],
                        getNoteByInterval(chordNotes[0], 12) // прима на октаву выше
                };
                break;

            case "second": // второе обращение (терцквартаккорд): квинта - септима - прима(+октава) - терция(+октава)
                bassNote = chordNotes[2]; // бас - квинта
                notes = new String[]{
                        chordNotes[2],
                        chordNotes[3],
                        getNoteByInterval(chordNotes[0], 12), // прима на октаву выше
                        getNoteByInterval(chordNotes[1], 12)  // терция на октаву выше
                };
                break;

            case "third": // третье обращение (секундаккорд): септима - прима(+октава) - терция(+октава) - квинта(+октава)
                bassNote = chordNotes[3]; // бас - септима
                notes = new String[]{
                        chordNotes[3],
                        getNoteByInterval(chordNotes[0], 12), // прима на октаву выше
                        getNoteByInterval(chordNotes[1], 12), // терция на октаву выше
                        getNoteByInterval(chordNotes[2], 12)  // квинта на октаву выше
                };
                break;

            default:
                bassNote = chordNotes[0];
                notes = chordNotes;
        }

        return new ChordInversionResult(notes, bassNote);
    }

    private String getSeventhChordDisplayName(String chordType, String inversionType) {
        String chordName = "";
        String inversionName = "";

        // Название септаккорда
        switch (chordType) {
            case "major7": chordName = "большой мажорный"; break;
            case "minor7": chordName = "малый минорный"; break;
            case "dominant7": chordName = "доминантовый"; break;
            case "half-diminished7": chordName = "полууменьшенный"; break;
            case "diminished7": chordName = "уменьшенный"; break;
        }

        // Название обращения
        switch (inversionType) {
            case "root": inversionName = "септаккорд"; break;
            case "first": inversionName = "квинтсекстаккорд"; break;
            case "second": inversionName = "терцквартаккорд"; break;
            case "third": inversionName = "секундаккорд"; break;
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
        return getSeventhChordRootPosition(rootNote, "major7");
    }
}