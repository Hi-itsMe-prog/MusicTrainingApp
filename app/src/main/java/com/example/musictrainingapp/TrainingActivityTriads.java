package com.example.musictrainingapp;

import java.util.*;

public abstract class TrainingActivityTriads extends BaseTrainingActivity {

    private final String[] triadTypes = {"major", "minor", "diminished", "augmented"};
    private final String[] inversions = {"основное", "первое обращение", "второе обращение"};

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
            String inversion = inversions[random.nextInt(inversions.length)];

            // Генерируем правильные ноты для трезвучия
            String[] correctNotes = generateTriadNotes(rootNote, triadType, inversion);
            int[] correctNoteIndexes = getNoteIndexes(correctNotes);

            String question = String.format("Постройте %s %s трезвучие от ноты %s",
                    inversion, getTriadTypeName(triadType), rootNote);

            exercises.add(new SimpleExercise(question, correctNotes, correctNoteIndexes));
        }

        return exercises;
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
            case "первое обращение": // Секстаккорд: терция - квинта - прима (на октаву выше)
                return new String[]{thirdNote, fifthNote, getNoteByInterval(rootNote, 12)};
            case "второе обращение": // Квартсекстаккорд: квинта - прима (на октаву выше) - терция (на октаву выше)
                return new String[]{fifthNote, getNoteByInterval(rootNote, 12), getNoteByInterval(thirdNote, 12)};
            default: // Основное положение: прима - терция - квинта
                return new String[]{rootNote, thirdNote, fifthNote};
        }
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
}