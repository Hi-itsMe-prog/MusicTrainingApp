package com.example.musictrainingapp;

import java.util.ArrayList;
import java.util.List;

public class PianoViewModel {
    private List<Integer> selectedNotes = new ArrayList<>();
    private List<Integer> correctNotes = new ArrayList<>();
    private boolean showResult = false;
    private boolean isCorrect = false;
    private boolean isEnabled = true;

    public void selectNote(int noteIndex) {
        if (!isEnabled) return;

        if (selectedNotes.contains(noteIndex)) {
            selectedNotes.remove(Integer.valueOf(noteIndex));
        } else {
            selectedNotes.add(noteIndex);
        }
    }

    public void deselectNote(int noteIndex) {
        if (selectedNotes.contains(noteIndex)) {
            selectedNotes.remove(Integer.valueOf(noteIndex));
        }
    }

    public void clearSelection() {
        selectedNotes.clear();
        showResult = false;
    }

    public boolean isNoteSelected(int noteIndex) {
        return selectedNotes.contains(noteIndex);
    }

    public void setCorrectNotes(List<Integer> correctNotes) {
        this.correctNotes = correctNotes;
        showResult = false;
    }

    public void showResult(boolean isCorrect) {
        this.isCorrect = isCorrect;
        showResult = true;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public List<Integer> getSelectedNotes() {
        return new ArrayList<>(selectedNotes);
    }

    public List<Integer> getCorrectNotes() {
        return new ArrayList<>(correctNotes);
    }

    public boolean isShowingResult() {
        return showResult;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public boolean isEnabled() {
        return isEnabled;
    }
}