package com.example.musictrainingapp;

public class MusicTerm {
    private int id;
    private String term;
    private String language;
    private String transcription;
    private String meaning;
    private String category;

    public MusicTerm() {}

    public MusicTerm(int id, String term, String language, String transcription, String meaning, String category) {
        this.id = id;
        this.term = term;
        this.language = language;
        this.transcription = transcription;
        this.meaning = meaning;
        this.category = category;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getTranscription() { return transcription; }
    public void setTranscription(String transcription) { this.transcription = transcription; }

    public String getMeaning() { return meaning; }
    public void setMeaning(String meaning) { this.meaning = meaning; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    @Override
    public String toString() {
        return term + " - " + meaning;
    }
}