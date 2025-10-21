package com.example.musictrainingapp;

import java.util.*;

public abstract class TrainingActivitySeventhChords extends BaseTrainingActivity {

    private final String[] chordTypes = {"major7", "minor7", "dominant7", "half-diminished7", "diminished7"};
    private final String[] inversions = {"основное", "первое обращение", "второе обращение", "третье обращение"};

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
            String inversion = inversions[random.nextInt(inversions.length)];

            // Генерируем правильные ноты для септаккорда
            String[] correctNotes = generateSeventhChordNotes(rootNote, chordType, inversion);
            int[] correctNoteIndexes = getNoteIndexes(correctNotes);

            String question = String.format("Постройте %s %s септаккорд от ноты %s",
                    inversion, getChordTypeName(chordType), rootNote);

            exercises.add(new SimpleExercise(question, correctNotes, correctNoteIndexes));
        }

        return exercises;
    }

    private String[] generateSeventhChordNotes(String rootNote, String chordType, String inversion) {
        int[] intervals;
        switch (chordType) {
            case "major7":
                intervals = new int[]{4, 7, 11}; // мажорное трезвучие + большая септима
                break;
            case "minor7":
                intervals = new int[]{3, 7, 10}; // минорное трезвучие + малая септима
                break;
            case "dominant7":
                intervals = new int[]{4, 7, 10}; // мажорное трезвучие + малая септима
                break;
            case "half-diminished7":
                intervals = new int[]{3, 6, 10}; // уменьшенное трезвучие + малая септима
                break;
            case "diminished7":
                intervals = new int[]{3, 6, 9}; // уменьшенное трезвучие + уменьшенная септима
                break;
            default:
                intervals = new int[]{4, 7, 10};
        }

        String thirdNote = getNoteByInterval(rootNote, intervals[0]);
        String fifthNote = getNoteByInterval(rootNote, intervals[1]);
        String seventhNote = getNoteByInterval(rootNote, intervals[2]);

        switch (inversion) {
            case "первое обращение":
                return new String[]{thirdNote, fifthNote, seventhNote, getNoteByInterval(rootNote, 12)};
            case "второе обращение":
                return new String[]{fifthNote, seventhNote, getNoteByInterval(rootNote, 12), getNoteByInterval(thirdNote, 12)};
            case "третье обращение":
                return new String[]{seventhNote, getNoteByInterval(rootNote, 12), getNoteByInterval(thirdNote, 12), getNoteByInterval(fifthNote, 12)};
            default:
                return new String[]{rootNote, thirdNote, fifthNote, seventhNote};
        }
    }

    private String getChordTypeName(String chordType) {
        switch (chordType) {
            case "major7": return "большой мажорный";
            case "minor7": return "малый минорный";
            case "dominant7": return "доминантовый";
            case "half-diminished7": return "полууменьшенный";
            case "diminished7": return "уменьшенный";
            default: return "";
        }
    }
}