package com.example.musictrainingapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MusicTheory {

    public static class Note {
        public final int semitone;
        public final String name;
        public final int octave;

        public Note(int semitone, String name, int octave) {
            this.semitone = semitone;
            this.name = name;
            this.octave = octave;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class Interval {
        public final Note note1;
        public final Note note2;
        public final String name;

        public Interval(Note note1, Note note2, String name) {
            this.note1 = note1;
            this.note2 = note2;
            this.name = name;
        }
    }

    public static class Chord {
        public final Note root;
        public final Note[] notes;
        public final String type;

        public Chord(Note root, Note[] notes, String type) {
            this.root = root;
            this.notes = notes;
            this.type = type;
        }
    }

    public static class Exercise {
        public final String question;
        public final String correctAnswer;
        public final String[] options;
        public final int[] correctNoteIndexes;
        public final String[] correctNoteNames;

        public Exercise(String question, String correctAnswer, String[] options,
                        int[] correctNoteIndexes, String[] correctNoteNames) {
            this.question = question;
            this.correctAnswer = correctAnswer;
            this.options = options;
            this.correctNoteIndexes = correctNoteIndexes;
            this.correctNoteNames = correctNoteNames;
        }
    }


    private static final String[] NOTE_NAMES = {"C", "D", "E", "F", "G", "A", "B"};
    private static final Map<String, Note> NOTE_MAP = new HashMap<>();
    private static final Map<String, int[]> INTERVAL_MAP = new HashMap<>();
    private static final Map<String, int[]> CHORD_MAP = new HashMap<>();

    // Карта соответствия нот индексам на клавиатуре (2 октавы)
    private static final Map<String, Integer> NOTE_TO_INDEX = new HashMap<>();
    private static final String[] INDEX_TO_NOTE = {
            "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B",  // Первая октава
            "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"   // Вторая октава
    };
    private static final Random random = new Random();



    static {
        // Initialize notes
        NOTE_MAP.put("C", new Note(0, "C", 4));
        NOTE_MAP.put("C#", new Note(1, "C#", 4));
        NOTE_MAP.put("Db", new Note(1, "Db", 4));
        NOTE_MAP.put("D", new Note(2, "D", 4));
        NOTE_MAP.put("D#", new Note(3, "D#", 4));
        NOTE_MAP.put("Eb", new Note(3, "Eb", 4));
        NOTE_MAP.put("E", new Note(4, "E", 4));
        NOTE_MAP.put("F", new Note(5, "F", 4));
        NOTE_MAP.put("F#", new Note(6, "F#", 4));
        NOTE_MAP.put("Gb", new Note(6, "Gb", 4));
        NOTE_MAP.put("G", new Note(7, "G", 4));
        NOTE_MAP.put("G#", new Note(8, "G#", 4));
        NOTE_MAP.put("Ab", new Note(8, "Ab", 4));
        NOTE_MAP.put("A", new Note(9, "A", 4));
        NOTE_MAP.put("A#", new Note(10, "A#", 4));
        NOTE_MAP.put("Bb", new Note(10, "Bb", 4));
        NOTE_MAP.put("B", new Note(11, "B", 4));

        // Initialize intervals
        INTERVAL_MAP.put("perfect unison", new int[]{1, 0});
        INTERVAL_MAP.put("minor 2nd", new int[]{2, 1});
        INTERVAL_MAP.put("major 2nd", new int[]{2, 2});
        INTERVAL_MAP.put("minor 3rd", new int[]{3, 3});
        INTERVAL_MAP.put("major 3rd", new int[]{4, 4});
        INTERVAL_MAP.put("perfect 4th", new int[]{4, 5});
        INTERVAL_MAP.put("augmented 4th", new int[]{4, 6});
        INTERVAL_MAP.put("diminished 5th", new int[]{5, 6});
        INTERVAL_MAP.put("perfect 5th", new int[]{5, 7});
        INTERVAL_MAP.put("augmented 5th", new int[]{5, 8});
        INTERVAL_MAP.put("minor 6th", new int[]{6, 8});
        INTERVAL_MAP.put("major 6th", new int[]{6, 9});
        INTERVAL_MAP.put("minor 7th", new int[]{7, 10});
        INTERVAL_MAP.put("major 7th", new int[]{7, 11});
        INTERVAL_MAP.put("octave", new int[]{8, 12});

        // Initialize chords
        CHORD_MAP.put("major", new int[]{4, 7});
        CHORD_MAP.put("minor", new int[]{3, 7});
        CHORD_MAP.put("diminished", new int[]{3, 6});
        CHORD_MAP.put("augmented", new int[]{4, 8});
        CHORD_MAP.put("major7", new int[]{4, 7, 11});
        CHORD_MAP.put("minor7", new int[]{3, 7, 10});
        CHORD_MAP.put("dominant7", new int[]{4, 7, 10});
        CHORD_MAP.put("half-diminished7", new int[]{3, 6, 10});
        CHORD_MAP.put("diminished7", new int[]{3, 6, 9});
        CHORD_MAP.put("major9", new int[]{4, 7, 11, 14});
        CHORD_MAP.put("minor9", new int[]{3, 7, 10, 14});
        CHORD_MAP.put("dominant9", new int[]{4, 7, 10, 14});

        // Initialize note to index mapping
        for (int i = 0; i < INDEX_TO_NOTE.length; i++) {
            NOTE_TO_INDEX.put(INDEX_TO_NOTE[i], i);
        }
    }

    public static Interval buildInterval(String startNote, String intervalName, boolean isUpward) {
        Note start = NOTE_MAP.get(startNote);
        if (start == null) {
            throw new IllegalArgumentException("Invalid note: " + startNote);
        }

        int[] interval = INTERVAL_MAP.get(intervalName.toLowerCase());
        if (interval == null) {
            throw new IllegalArgumentException("Unknown interval: " + intervalName);
        }

        int diatonicSteps = interval[0];
        int targetSemitones = interval[1];

        // Если интервал строится вниз, инвертируем значения
        if (!isUpward) {
            diatonicSteps = -diatonicSteps;
            targetSemitones = -targetSemitones;
        }

        int rootIndex = -1;
        for (int i = 0; i < NOTE_NAMES.length; i++) {
            if (NOTE_NAMES[i].equals(startNote.substring(0, 1))) {
                rootIndex = i;
                break;
            }
        }

        int targetIndex = (rootIndex + diatonicSteps - 1) % 7;
        if (targetIndex < 0) targetIndex += 7; // Обработка отрицательных индексов

        String targetLetter = NOTE_NAMES[targetIndex];
        int octaveShift = (rootIndex + diatonicSteps - 1) / 7;
        int newOctave = start.octave + octaveShift;

        Note naturalTarget = NOTE_MAP.get(targetLetter);
        int rootPitch = start.semitone + start.octave * 12;
        int naturalTargetPitch = naturalTarget.semitone + newOctave * 12;
        int currentSemitones = naturalTargetPitch - rootPitch;
        int diff = targetSemitones - currentSemitones;
        int finalSemitone = (naturalTarget.semitone + diff) % 12;
        if (finalSemitone < 0) finalSemitone += 12;

        String targetName = findNoteName(finalSemitone, targetLetter);
        Note resultNote = new Note(finalSemitone, targetName, newOctave);

        return new Interval(start, resultNote, intervalName);
    }

    // Перегрузка метода для обратной совместимости (по умолчанию строится вверх)
    public static Interval buildInterval(String startNote, String intervalName) {
        return buildInterval(startNote, intervalName, true);
    }

    public static Chord buildChord(String rootNote, String chordType) {
        Note root = NOTE_MAP.get(rootNote);
        if (root == null) {
            throw new IllegalArgumentException("Invalid root note: " + rootNote);
        }

        int[] intervals = CHORD_MAP.get(chordType.toLowerCase());
        if (intervals == null) {
            throw new IllegalArgumentException("Unknown chord type: " + chordType);
        }

        Note[] notes = new Note[intervals.length + 1];
        notes[0] = root;

        for (int i = 0; i < intervals.length; i++) {
            int semitonesFromRoot = intervals[i];
            int targetSemitone = (root.semitone + semitonesFromRoot) % 12;
            int octaveShift = (root.semitone + semitonesFromRoot) / 12;
            int newOctave = root.octave + octaveShift;

            String targetName = findNoteName(targetSemitone, getExpectedNoteName(root.name, intervals[i]));
            notes[i + 1] = new Note(targetSemitone, targetName, newOctave);
        }

        return new Chord(root, notes, chordType);
    }

    // Добавим методы для правильного отображения нот
    public static String getCorrectNoteName(int semitone, String baseNote) {
        // Определяем основную ноту (без диеза/бемоля)
        String baseName = baseNote.length() > 1 && (baseNote.endsWith("#") || baseNote.endsWith("b"))
                ? baseNote.substring(0, 1) : baseNote;

        // Находим все возможные названия для этого полутона
        List<String> possibleNames = new ArrayList<>();
        for (Map.Entry<String, Note> entry : NOTE_MAP.entrySet()) {
            if (entry.getValue().semitone == semitone) {
                possibleNames.add(entry.getKey());
            }
        }

        // Предпочитаем название, которое начинается с той же буквы, что и базовая нота
        for (String name : possibleNames) {
            if (name.startsWith(baseName)) {
                return name;
            }
        }

        // Если не нашли, возвращаем любое подходящее название
        return possibleNames.isEmpty() ? baseNote : possibleNames.get(0);
    }

    public static String getNoteDisplayName(String noteName) {
        // Преобразуем названия нот в правильный формат для отображения
        if (noteName == null) return "";

        switch (noteName) {
            case "C#": return "C♯";
            case "Db": return "D♭";
            case "D#": return "D♯";
            case "Eb": return "E♭";
            case "F#": return "F♯";
            case "Gb": return "G♭";
            case "G#": return "G♯";
            case "Ab": return "A♭";
            case "A#": return "A♯";
            case "Bb": return "B♭";
            default: return noteName;
        }
    }

    // Методы для генерации упражнений
    public static Exercise generateIntervalExercise() {
        String[] baseNotes = {"C", "D", "E", "F", "G", "A", "B"};
        String[] intervals = {
                "minor 2nd", "major 2nd", "minor 3rd", "major 3rd",
                "perfect 4th", "augmented 4th", "perfect 5th", "minor 6th", "major 6th",
                "minor 7th", "major 7th", "octave"
        };

        String[] intervalNames = {
                "малую секунду", "большую секунду", "малую терцию", "большую терцию",
                "кварту", "увеличенную кварту", "квинту", "малую сексту", "большую сексту",
                "малую септиму", "большую септиму", "октаву"
        };

        String baseNote = baseNotes[random.nextInt(baseNotes.length)];
        int intervalIndex = random.nextInt(intervals.length);
        String intervalType = intervals[intervalIndex];
        String intervalName = intervalNames[intervalIndex];
        boolean isUpward = random.nextBoolean();

        // Строим интервал с учетом направления
        Interval interval = buildInterval(baseNote, intervalType, isUpward);
        String targetNote = interval.note2.name;

        // Формируем вопрос
        String direction = isUpward ? "вверх" : "вниз";
        String question = String.format("Постройте %s от ноты %s %s", intervalName, baseNote, direction);

        // Определяем правильные ноты на клавиатуре
        int baseNoteIndex = NOTE_TO_INDEX.get(baseNote);
        int targetNoteIndex = NOTE_TO_INDEX.get(targetNote);

        int[] correctNoteIndexes = {baseNoteIndex, targetNoteIndex};
        String[] correctNoteNames = {baseNote, targetNote};
        String correctAnswer = baseNote + " → " + targetNote;

        // Генерируем варианты ответов
        String[] options = generateIntervalOptions(targetNote);

        return new Exercise(question, correctAnswer, options, correctNoteIndexes, correctNoteNames);
    }

    public static List<Exercise> generateIntervalExercises(int count) {
        List<Exercise> exercises = new ArrayList<>();
        Set<String> usedQuestions = new HashSet<>();

        while (exercises.size() < count) {
            Exercise exercise = generateIntervalExercise();
            if (!usedQuestions.contains(exercise.question)) {
                exercises.add(exercise);
                usedQuestions.add(exercise.question);
            }
        }

        return exercises;
    }

    public static Exercise generateChordExercise(String chordType) {
        String[] baseNotes = {"C", "D", "E", "F", "G", "A", "B"};
        Map<String, String> chordNames = new HashMap<>();
        chordNames.put("major", "мажорное");
        chordNames.put("minor", "минорное");
        chordNames.put("diminished", "уменьшенное");
        chordNames.put("augmented", "увеличенное");
        chordNames.put("major7", "большой мажорный");
        chordNames.put("minor7", "малый минорный");
        chordNames.put("dominant7", "доминантсептаккорд");

        String baseNote = baseNotes[random.nextInt(baseNotes.length)];
        String chordName = chordNames.get(chordType);

        // Строим аккорд
        Chord chord = buildChord(baseNote, chordType);
        String[] chordNotes = new String[chord.notes.length];
        for (int i = 0; i < chord.notes.length; i++) {
            chordNotes[i] = chord.notes[i].name;
        }

        // Формируем вопрос
        String question = String.format("Постройте %s трезвучие от ноты %s", chordName, baseNote);

        // Определяем правильные ноты на клавиатуре
        int[] correctNoteIndexes = new int[chordNotes.length];
        for (int i = 0; i < chordNotes.length; i++) {
            correctNoteIndexes[i] = NOTE_TO_INDEX.get(chordNotes[i]);
        }

        String correctAnswer = String.join(" ", chordNotes);
        String[] correctNoteNames = chordNotes;

        // Генерируем варианты ответов
        String[] options = generateChordOptions(chordNotes);

        return new Exercise(question, correctAnswer, options, correctNoteIndexes, correctNoteNames);
    }

    private static String[] generateIntervalOptions(String correctNote) {
        String[] allNotes = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
        String[] options = new String[4];
        options[0] = correctNote;

        int count = 1;
        while (count < 4) {
            String randomNote = allNotes[random.nextInt(allNotes.length)];
            if (!randomNote.equals(correctNote) && !contains(options, randomNote)) {
                options[count] = randomNote;
                count++;
            }
        }

        shuffleArray(options);
        return options;
    }

    private static String[] generateChordOptions(String[] correctNotes) {
        String[] allNotes = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
        String[] options = new String[4];
        options[0] = String.join(" ", correctNotes);

        for (int i = 1; i < 4; i++) {
            String[] wrongNotes = new String[correctNotes.length];
            for (int j = 0; j < correctNotes.length; j++) {
                String randomNote;
                do {
                    randomNote = allNotes[random.nextInt(allNotes.length)];
                } while (contains(correctNotes, randomNote) || contains(wrongNotes, randomNote));
                wrongNotes[j] = randomNote;
            }
            options[i] = String.join(" ", wrongNotes);
        }

        shuffleArray(options);
        return options;
    }

    // Вспомогательные методы
    private static String findNoteName(int semitone, String desiredLetter) {
        for (Map.Entry<String, Note> entry : NOTE_MAP.entrySet()) {
            if (entry.getValue().semitone == semitone &&
                    entry.getKey().startsWith(desiredLetter)) {
                return entry.getKey();
            }
        }

        for (Map.Entry<String, Note> entry : NOTE_MAP.entrySet()) {
            if (entry.getValue().semitone == semitone) {
                return entry.getKey();
            }
        }

        return desiredLetter;
    }

    private static String getExpectedNoteName(String rootName, int interval) {
        String rootLetter = rootName.substring(0, 1);
        int rootIndex = -1;
        for (int i = 0; i < NOTE_NAMES.length; i++) {
            if (NOTE_NAMES[i].equals(rootLetter)) {
                rootIndex = i;
                break;
            }
        }

        int steps = 0;
        if (interval == 3) steps = 2;
        else if (interval == 4) steps = 2;
        else if (interval == 6) steps = 3;
        else if (interval == 7) steps = 4;
        else if (interval == 8) steps = 4;
        else if (interval == 10) steps = 5;
        else if (interval == 11) steps = 5;
        else if (interval == 13) steps = 7;
        else if (interval == 14) steps = 7;

        int targetIndex = (rootIndex + steps) % 7;
        return NOTE_NAMES[targetIndex];
    }

    private static boolean contains(String[] array, String value) {
        for (String item : array) {
            if (item != null && item.equals(value)) {
                return true;
            }
        }
        return false;
    }

    private static void shuffleArray(String[] array) {
        List<String> list = Arrays.asList(array);
        Collections.shuffle(list);
        list.toArray(array);
    }

    public static int getNoteIndex(String noteName) {
        // Обрабатываем ноты с диезами/бемолями и разными октавами
        if (noteName == null || noteName.isEmpty()) return -1;

        // Определяем основное имя ноты и октаву
        String baseName;
        int octave = 4; // по умолчанию 4-я октава

        if (noteName.length() > 1 && Character.isDigit(noteName.charAt(noteName.length() - 1))) {
            // Если есть указание октавы (например "A4", "C#5")
            try {
                octave = Integer.parseInt(noteName.substring(noteName.length() - 1));
                baseName = noteName.substring(0, noteName.length() - 1);
            } catch (NumberFormatException e) {
                baseName = noteName;
            }
        } else {
            baseName = noteName;
        }

        // Находим базовый индекс для ноты в 4-й октаве
        Integer baseIndex = NOTE_TO_INDEX.get(baseName);
        if (baseIndex == null) return -1;

        // Вычисляем итоговый индекс с учетом октавы
        int octaveOffset = (octave - 4) * 12; // каждая октава = 12 полутонов
        return baseIndex + octaveOffset;
    }

    public static String getNoteName(int index) {
        if (index < 0 || index >= 48) { // 4 октавы * 12 нот
            return "";
        }

        int octave = 4 + (index / 12);
        int noteIndex = index % 12;

        return INDEX_TO_NOTE[noteIndex] + octave;
    }
}