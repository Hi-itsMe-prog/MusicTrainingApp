package com.example.musictrainingapp;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "music_terms.db";
    private static final int DATABASE_VERSION = 1;
    private final Context context;
    private SQLiteDatabase database;
    private String databasePath;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.databasePath = context.getDatabasePath(DATABASE_NAME).getPath();
        copyDatabaseIfNeeded();
    }

    private void copyDatabaseIfNeeded() {
        File dbFile = context.getDatabasePath(DATABASE_NAME);

        if (!dbFile.exists()) {
            try {
                copyDatabase();
            } catch (IOException e) {
                throw new RuntimeException("Error copying database", e);
            }
        }
    }

    private void copyDatabase() throws IOException {
        InputStream inputStream = context.getAssets().open("databases/" + DATABASE_NAME);
        File outputFile = context.getDatabasePath(DATABASE_NAME);

        // Создаем папки если их нет
        outputFile.getParentFile().mkdirs();

        OutputStream outputStream = new FileOutputStream(outputFile);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // База данных уже создана, ничего не делаем
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Логика обновления БД при изменении версии
    }

    public void openDatabase() throws SQLException {
        database = SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {
        if (database != null) {
            database.close();
        }
        super.close();
    }

    // Получить все термины
    public List<MusicTerm> getAllTerms() {
        List<MusicTerm> terms = new ArrayList<>();
        openDatabase();

        Cursor cursor = null;
        try {
            cursor = database.rawQuery("SELECT * FROM music_terms ORDER BY term", null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    MusicTerm term = new MusicTerm();
                    term.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    term.setTerm(cursor.getString(cursor.getColumnIndexOrThrow("term")));
                    term.setLanguage(cursor.getString(cursor.getColumnIndexOrThrow("language")));
                    term.setTranscription(cursor.getString(cursor.getColumnIndexOrThrow("transcription")));
                    term.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow("meaning")));
                    term.setCategory(cursor.getString(cursor.getColumnIndexOrThrow("category")));

                    terms.add(term);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            close();
        }
        return terms;
    }

    // Поиск по названию термина
    public List<MusicTerm> searchTerms(String query) {
        List<MusicTerm> terms = new ArrayList<>();
        openDatabase();

        Cursor cursor = null;
        try {
            String sql = "SELECT * FROM music_terms WHERE term LIKE ? OR meaning LIKE ? ORDER BY term";
            String[] selectionArgs = new String[]{"%" + query + "%", "%" + query + "%"};

            cursor = database.rawQuery(sql, selectionArgs);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    MusicTerm term = new MusicTerm();
                    term.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    term.setTerm(cursor.getString(cursor.getColumnIndexOrThrow("term")));
                    term.setLanguage(cursor.getString(cursor.getColumnIndexOrThrow("language")));
                    term.setTranscription(cursor.getString(cursor.getColumnIndexOrThrow("transcription")));
                    term.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow("meaning")));
                    term.setCategory(cursor.getString(cursor.getColumnIndexOrThrow("category")));

                    terms.add(term);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            close();
        }
        return terms;
    }

    // Получить термины по категории
    public List<MusicTerm> getTermsByCategory(String category) {
        List<MusicTerm> terms = new ArrayList<>();
        openDatabase();

        Cursor cursor = null;
        try {
            String sql = "SELECT * FROM music_terms WHERE category = ? ORDER BY term";
            String[] selectionArgs = new String[]{category};

            cursor = database.rawQuery(sql, selectionArgs);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    MusicTerm term = new MusicTerm();
                    term.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    term.setTerm(cursor.getString(cursor.getColumnIndexOrThrow("term")));
                    term.setLanguage(cursor.getString(cursor.getColumnIndexOrThrow("language")));
                    term.setTranscription(cursor.getString(cursor.getColumnIndexOrThrow("transcription")));
                    term.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow("meaning")));
                    term.setCategory(cursor.getString(cursor.getColumnIndexOrThrow("category")));

                    terms.add(term);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            close();
        }
        return terms;
    }

    // Получить все категории
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        openDatabase();

        Cursor cursor = null;
        try {
            cursor = database.rawQuery("SELECT DISTINCT category FROM music_terms ORDER BY category", null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    categories.add(cursor.getString(cursor.getColumnIndexOrThrow("category")));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            close();
        }
        return categories;
    }

    // Получить случайный термин
    public MusicTerm getRandomTerm() {
        openDatabase();

        Cursor cursor = null;
        try {
            cursor = database.rawQuery("SELECT * FROM music_terms ORDER BY RANDOM() LIMIT 1", null);

            if (cursor != null && cursor.moveToFirst()) {
                MusicTerm term = new MusicTerm();
                term.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                term.setTerm(cursor.getString(cursor.getColumnIndexOrThrow("term")));
                term.setLanguage(cursor.getString(cursor.getColumnIndexOrThrow("language")));
                term.setTranscription(cursor.getString(cursor.getColumnIndexOrThrow("transcription")));
                term.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow("meaning")));
                term.setCategory(cursor.getString(cursor.getColumnIndexOrThrow("category")));

                return term;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            close();
        }
        return null;
    }
}