package com.example.musictrainingapp;

import java.util.ArrayList;
import java.util.List;

public class KeyViewData {

    public static class PianoKey {
        private String noteName;
        private boolean isBlackKey;
        private boolean isPressed;

        public PianoKey(String noteName, boolean isBlackKey) {
            this.noteName = noteName;
            this.isBlackKey = isBlackKey;
            this.isPressed = false;
        }

        public String getNoteName() {
            return noteName;
        }

        public boolean isBlackKey() {
            return isBlackKey;
        }

        public boolean isPressed() {
            return isPressed;
        }

        public void setPressed(boolean pressed) {
            isPressed = pressed;
        }
    }

    private static final List<PianoKey> keys = new ArrayList<>();

    static {
        // Добавляем две октавы
        addOctave("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B");
        addOctave("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B");
    }

    private static void addOctave(String... notes) {
        boolean[] blackPattern = {false, true, false, true, false, false, true, false, true, false, true, false};

        for (int i = 0; i < notes.length; i++) {
            keys.add(new PianoKey(notes[i], blackPattern[i]));
        }
    }

    public static List<PianoKey> getKeys() {
        return new ArrayList<>(keys);
    }

    public static int getKeyCount() {
        return keys.size();
    }

    public static PianoKey getKey(int index) {
        if (index >= 0 && index < keys.size()) {
            return keys.get(index);
        }
        return null;
    }

    public static int getNoteIndex(String noteName) {
        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i).getNoteName().equals(noteName)) {
                return i;
            }
        }
        return -1;
    }
}