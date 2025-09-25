package com.example.musictrainingapp;

public class MusicTerm {
    private int id;
    private String foreignTerm;
    private String russianTranslation;
    private String description;
    private boolean isStudied;
    private String category;

    public MusicTerm() {}

    public MusicTerm(String foreignTerm, String russianTranslation, String description, String category) {
        this.foreignTerm = foreignTerm;
        this.russianTranslation = russianTranslation;
        this.description = description;
        this.category = category;
        this.isStudied = false;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getForeignTerm() { return foreignTerm; }
    public void setForeignTerm(String foreignTerm) { this.foreignTerm = foreignTerm; }

    public String getRussianTranslation() { return russianTranslation; }
    public void setRussianTranslation(String russianTranslation) { this.russianTranslation = russianTranslation; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isStudied() { return isStudied; }
    public void setStudied(boolean studied) { isStudied = studied; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}