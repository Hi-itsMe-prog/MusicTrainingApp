package com.example.musictrainingapp;

import android.os.Bundle;
import java.util.*;

public class TrainingActivityTriads extends BaseTrainingActivity {

    // Типы трезвучий (только основные, обращения будем генерировать)
    private final String[] triadTypes = {
            "major",    // мажорное
            "minor",    // минорное
            "dim",      // уменьшенное
            "aug"       // увеличенное
    };

    // Типы обращений
    private final String[] inversionTypes = {
            "root",     // основное положение
            "first",    // первое обращение (секстаккорд)
            "second"    // второе обращение (квартсекстаккорд)
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeActivity(R.layout.activity_training_triads);
    }

    @Override
    protected String getExerciseType() {
        return "triads";
    }

    @Override
    protected int getMaxNotes() {
        return 3;
    }

    @Override
    protected String getChordName() {
        return "трезвучие";
    }

    @Override
    protected List<SimpleExercise> generateExercises() {
        List<SimpleExercise> exercises = new ArrayList<>();

        for (int i = 0; i < totalQuestions; i++) {
            String rootNote = baseNotes[random.nextInt(baseNotes.length)];
            String triadType = triadTypes[random.nextInt(triadTypes.length)];
            String inversionType = inversionTypes[random.nextInt(inversionTypes.length)];

            // Генерируем правильные ноты для трезвучия с обращением
            ChordData chordData = getChordNotesWithInversion(rootNote, triadType, inversionType);
            String[] correctNotes = chordData.notes;
            int[] correctNoteIndexes = getNoteIndexes(correctNotes);

            String question = String.format("Постройте %s от ноты %s",
                    chordData.displayName, chordData.bassNote);

            exercises.add(new SimpleExercise(question, correctNotes, correctNoteIndexes));
        }

        return exercises;
    }

    private ChordData getChordNotesWithInversion(String rootNote, String triadType, String inversionType) {
        // Сначала получаем трезвучие в основном положении
        String[] rootPosition = getTriadRootPosition(rootNote, triadType);

        // Затем применяем обращение и получаем басовую ноту
        ChordInversionResult inversionResult = applyInversion(rootPosition, inversionType);

        // Формируем отображаемое название
        String displayName = getTriadDisplayName(triadType, inversionType);

        return new ChordData(inversionResult.notes, inversionResult.bassNote, displayName);
    }

    private String[] getTriadRootPosition(String rootNote, String triadType) {
        switch (triadType) {
            case "major": // большая терция + малая терция
                return new String[]{
                        rootNote,
                        getNoteByInterval(rootNote, 4), // большая терция
                        getNoteByInterval(rootNote, 7)  // малая терция (чистая квинта)
                };
            case "minor": // малая терция + большая терция
                return new String[]{
                        rootNote,
                        getNoteByInterval(rootNote, 3), // малая терция
                        getNoteByInterval(rootNote, 7)  // большая терция (чистая квинта)
                };
            case "dim": // малая терция + малая терция
                return new String[]{
                        rootNote,
                        getNoteByInterval(rootNote, 3), // малая терция
                        getNoteByInterval(rootNote, 6)  // малая терция (уменьшенная квинта)
                };
            case "aug": // большая терция + большая терция
                return new String[]{
                        rootNote,
                        getNoteByInterval(rootNote, 4), // большая терция
                        getNoteByInterval(rootNote, 8)  // большая терция (увеличенная квинта)
                };
            default:
                return new String[]{rootNote, rootNote, rootNote};
        }
    }

    private ChordInversionResult applyInversion(String[] chordNotes, String inversionType) {
        String bassNote;
        String[] notes;

        switch (inversionType) {
            case "root": // основное положение: прима - терция - квинта
                bassNote = chordNotes[0]; // бас - прима
                notes = new String[]{
                        chordNotes[0],
                        chordNotes[1],
                        chordNotes[2]
                };
                break;

            case "first": // первое обращение (секстаккорд): терция - квинта - прима(+октава)
                bassNote = chordNotes[1]; // бас - терция
                notes = new String[]{
                        chordNotes[1],
                        chordNotes[2],
                        getNoteByInterval(chordNotes[0], 12) // прима на октаву выше
                };
                break;

            case "second": // второе обращение (квартсекстаккорд): квинта - прима(+октава) - терция(+октава)
                bassNote = chordNotes[2]; // бас - квинта
                notes = new String[]{
                        chordNotes[2],
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

    private String getTriadDisplayName(String triadType, String inversionType) {
        String triadName = "";
        String inversionName = "";

        // Название трезвучия
        switch (triadType) {
            case "major": triadName = "мажорное"; break;
            case "minor": triadName = "минорное"; break;
            case "dim": triadName = "уменьшенное"; break;
            case "aug": triadName = "увеличенное"; break;
        }

        // Название обращения
        switch (inversionType) {
            case "root": inversionName = "трезвучие"; break;
            case "first": inversionName = "секстаккорд"; break;
            case "second": inversionName = "квартсекстаккорд"; break;
        }

        return triadName + " " + inversionName;
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
        return getTriadRootPosition(rootNote, "major");
    }
}