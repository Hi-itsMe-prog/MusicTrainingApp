package com.example.musictrainingapp;

public class User {
    private String userId;
    private String username;
    private String email;
    private int progress;
    private int totalExercises;
    private int accuracy;
    private long totalPracticeTime; // в минутах
    private int totalScore;

    // Обязательный пустой конструктор для Firebase
    public User() {
    }

    public User(String userId, String username, String email, int progress, int totalExercises, int accuracy) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.progress = progress;
        this.totalExercises = totalExercises;
        this.accuracy = accuracy;
        this.totalPracticeTime = 0;
        this.totalScore = 0;
    }

    // Геттеры и сеттеры
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public int getTotalExercises() { return totalExercises; }
    public void setTotalExercises(int totalExercises) { this.totalExercises = totalExercises; }

    public int getAccuracy() { return accuracy; }
    public void setAccuracy(int accuracy) { this.accuracy = accuracy; }

    public long getTotalPracticeTime() { return totalPracticeTime; }
    public void setTotalPracticeTime(long totalPracticeTime) { this.totalPracticeTime = totalPracticeTime; }

    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }
}