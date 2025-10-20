package com.example.musictrainingapp;

import java.util.*;

public class TrainingIntervalsActivity extends BaseTrainingActivity {

    private final String[] intervalNames = {
            "малую секунду", "большую секунду", "малую терцию", "большую терцию",
            "кварту", "увеличенную кварту", "квинту", "малую сексту", "большую сексту",
            "малую септиму", "большую септиму", "октаву"
    };

    private final int[] intervalSemitones = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};

    @Override
    protected String getExerciseType() {
        return "intervals";
    }

    @Override
    protected int getMaxNotes() {
        return 2;
    }

    @Override
    protected String getChordName() {
        return "интервал";
    }

    @Override
    protected List<SimpleExercise> generateExercises() {
        List<SimpleExercise> exercises = new ArrayList<>();

        for (int i = 0; i < totalQuestions; i++) {
            String baseNote = baseNotes[random.nextInt(baseNotes.length)];
            int intervalIndex = random.nextInt(intervalNames.length);
            boolean isUpward = random.nextBoolean();

            String direction = isUpward ? "вверх" : "вниз";
            String question = String.format("Постройте %s от ноты %s %s",
                    intervalNames[intervalIndex], baseNote, direction);

            // Генерируем правильные ноты для интервала
            String[] correctNotes = generateIntervalNotes(baseNote, intervalSemitones[intervalIndex], isUpward);
            int[] correctNoteIndexes = getNoteIndexes(correctNotes);

            exercises.add(new SimpleExercise(question, correctNotes, correctNoteIndexes));
        }

        return exercises;
    }

    private String[] generateIntervalNotes(String baseNote, int semitones, boolean isUpward) {
        String targetNote = getNoteByInterval(baseNote, isUpward ? semitones : -semitones);
        return new String[]{baseNote, targetNote};
    }

    // Переопределяем логику выбора нот для интервалов (порядок важен)
    @Override
    protected void handleNoteSelection(int noteIndex, String noteName) {
        // Очищаем предыдущий выбор, если уже выбрано 2 ноты
        if (selectedNotes.size() >= 2) {
            selectedNotes.clear();
            selectedNoteIndexes.clear();
            pianoKeyboard.clearSelection();
        }

        // Добавляем новую ноту в выбор
        selectedNotes.add(noteName);
        selectedNoteIndexes.add(noteIndex);

        updateSelectedNotesDisplay();
    }

    // Переопределяем проверку для интервалов (учитываем порядок нот)
    @Override
    protected boolean checkIfAnswerCorrect() {
        if (selectedNotes.size() != 2) {
            return false;
        }

        // Для интервалов порядок важен - сравниваем напрямую
        return Arrays.equals(selectedNotes.toArray(new String[0]),
                currentExercise.correctNotes);
    }

    @Override
    protected void updateSelectedNotesDisplay() {
        if (tvSelectedNotes == null) return;


        if (selectedNotes.size() == 2) {
            int semitones = Math.abs(selectedNoteIndexes.get(1) - selectedNoteIndexes.get(0));

        }


    }
}