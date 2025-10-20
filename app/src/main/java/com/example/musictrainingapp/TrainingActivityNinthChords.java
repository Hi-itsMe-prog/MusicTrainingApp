package com.example.musictrainingapp;

import java.util.*;

public class TrainingActivityNinthChords extends BaseTrainingActivity {

    private final String[] chordTypes = {"major9", "minor9", "dominant9"};

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

            // Генерируем правильные ноты для нонаккорда
            String[] correctNotes = generateNinthChordNotes(rootNote, chordType);
            int[] correctNoteIndexes = getNoteIndexes(correctNotes);

            String question = String.format("Постройте %s нонаккорд от ноты %s",
                    getChordTypeName(chordType), rootNote);

            exercises.add(new SimpleExercise(question, correctNotes, correctNoteIndexes));
        }

        return exercises;
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

    private String getChordTypeName(String chordType) {
        switch (chordType) {
            case "major9": return "большой мажорный";
            case "minor9": return "малый минорный";
            case "dominant9": return "доминантовый";
            default: return "";
        }
    }
}