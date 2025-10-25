package com.example.musictrainingapp;

import android.os.Bundle;
import java.util.*;

public class TrainingActivityTriads extends BaseTrainingActivity {

    // Предопределенные типы трезвучий с обращениями
    private final String[] triadTypes = {
            "major", "major6", "major64",        // мажор: основное, 1 обращение, 2 обращение
            "minor", "minor6", "minor64",        // минор: основное, 1 обращение, 2 обращение
            "dim", "dim6", "dim64",            // уменьшенное: основное, 1 обращение, 2 обращение
            "aug", "aug6", "aug64"            // увеличенное: основное, 1 обращение, 2 обращение
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

            // Генерируем правильные ноты для трезвучия
            String[] correctNotes = getChordNotesWithCorrectVoicing(rootNote, triadType);
            int[] correctNoteIndexes = getNoteIndexes(correctNotes);

            String question = String.format("Постройте %s от ноты %s",
                    getTriadDisplayName(triadType), rootNote);

            exercises.add(new SimpleExercise(question, correctNotes, correctNoteIndexes));
        }

        return exercises;
    }

    @Override
    protected String[] getChordNotesWithCorrectVoicing(String rootNote, String chordType) {
        switch (chordType) {
            // Мажорные трезвучия
            case "major":
                return new String[]{
                        rootNote,
                        getNoteByInterval(rootNote, 4),
                        getNoteByInterval(rootNote, 7)
                };
            case "major6":
                return new String[]{
                        getNoteByInterval(rootNote, 4), // начинается с терции
                        getNoteByInterval(rootNote, 7),
                        getNoteByInterval(rootNote, 12)
                };
            case "major64": // Соль-До-Ми (от Соль - квартсекстаккорд)
                return new String[]{
                        getNoteByInterval(rootNote, 7), // начинается с квинты
                        getNoteByInterval(rootNote, 12),
                        getNoteByInterval(getNoteByInterval(rootNote, 4), 12)
                };

            // Минорные трезвучия
            case "minor":
                return new String[]{
                        rootNote,
                        getNoteByInterval(rootNote, 3),
                        getNoteByInterval(rootNote, 7)
                };
            case "minor6":
                return new String[]{
                        getNoteByInterval(rootNote, 3), // начинается с терции
                        getNoteByInterval(rootNote, 7),
                        getNoteByInterval(rootNote, 12)
                };
            case "minor64":
                return new String[]{
                        getNoteByInterval(rootNote, 7), // начинается с квинты
                        getNoteByInterval(rootNote, 12),
                        getNoteByInterval(getNoteByInterval(rootNote, 3), 12)
                };

            // Уменьшенные трезвучия
            case "dim":
                return new String[]{
                        rootNote,
                        getNoteByInterval(rootNote, 3),
                        getNoteByInterval(rootNote, 6)
                };
            case "dim6":
                return new String[]{
                        getNoteByInterval(rootNote, 3), // начинается с терции
                        getNoteByInterval(rootNote, 6),
                        getNoteByInterval(rootNote, 12)
                };
            case "dim64":
                return new String[]{
                        getNoteByInterval(rootNote, 6), // начинается с квинты
                        getNoteByInterval(rootNote, 12),
                        getNoteByInterval(getNoteByInterval(rootNote, 3), 12)
                };

            // Увеличенные трезвучия
            case "aug":
                return new String[]{
                        rootNote,
                        getNoteByInterval(rootNote, 4),
                        getNoteByInterval(rootNote, 8)
                };
            case "aug6": // начало через большую терцию от rootNote
                return new String[]{
                        getNoteByInterval(rootNote, 4),
                        getNoteByInterval(rootNote, 8),
                        getNoteByInterval(rootNote, 12)
                };
            case "aug64": // начало через ув квинту от rootNote
                return new String[]{
                        getNoteByInterval(rootNote, 8),
                        getNoteByInterval(rootNote, 12),
                        getNoteByInterval(getNoteByInterval(rootNote, 4), 12)
                };

            default:
                return new String[]{rootNote};
        }
    }

    private String getTriadDisplayName(String triadType) {
        switch (triadType) {
            // Мажорные
            case "major": return "мажорное трезвучие";
            case "major6": return "мажорный секстаккорд";
            case "major64": return "мажорный квартсекстаккорд";

            // Минорные
            case "minor": return "минорное трезвучие";
            case "minor6": return "минорный секстаккорд";
            case "minor64": return "минорный квартсекстаккорд";

            // Уменьшенные
            case "dim": return "уменьшенное трезвучие";
            case "dim6": return "уменьшенный секстаккорд";
            case "dim64": return "уменьшенный квартсекстаккорд";

            // Увеличенные
            case "aug": return "увеличенное трезвучие";
            case "aug6": return "увеличенный секстаккорд";
            case "aug64": return "увеличенный квартсекстаккорд";

            default: return "трезвучие";
        }
    }
}