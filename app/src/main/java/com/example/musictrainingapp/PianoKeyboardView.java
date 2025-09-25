package com.example.musictrainingapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PianoKeyboardView extends View {

    public interface OnNotePlayedListener {
        void onNotePlayed(int noteIndex, String noteName);
    }

    // Ноты для 2 октав: от C4 до C6 (24 клавиши)
    private static final String[] NOTE_NAMES = {
            "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B",  // Первая октава
            "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"   // Вторая октава
    };

    private static final boolean[] IS_BLACK_KEY = {
            false, true, false, true, false, false, true, false, true, false, true, false,  // Первая октава
            false, true, false, true, false, false, true, false, true, false, true, false   // Вторая октава
    };

    private List<Rect> noteRects = new ArrayList<>();
    private boolean[] selectedNotes = new boolean[NOTE_NAMES.length]; // Массив для отслеживания выбранных нот
    private List<Integer> correctNotes = new ArrayList<>();
    private OnNotePlayedListener listener;
    private boolean showResult = false;
    private boolean isCorrect = false;
    private boolean isEnabled = true;

    private Paint whiteKeyPaint;
    private Paint blackKeyPaint;
    private Paint selectedWhitePaint;
    private Paint selectedBlackPaint;
    private Paint correctPaint;
    private Paint incorrectPaint;
    private Paint borderPaint;

    public PianoKeyboardView(Context context) {
        super(context);
        init();
    }

    public PianoKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PianoKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // Инициализация массива выбранных нот
        selectedNotes = new boolean[NOTE_NAMES.length];
        Arrays.fill(selectedNotes, false);

        // Инициализация красок для белых клавиш
        whiteKeyPaint = new Paint();
        whiteKeyPaint.setColor(Color.WHITE);
        whiteKeyPaint.setStyle(Paint.Style.FILL);

        // Инициализация красок для черных клавиш
        blackKeyPaint = new Paint();
        blackKeyPaint.setColor(Color.BLACK);
        blackKeyPaint.setStyle(Paint.Style.FILL);

        // Инициализация красок для выбранных белых клавиш
        selectedWhitePaint = new Paint();
        selectedWhitePaint.setColor(Color.parseColor("#FFCC80")); // Светло-оранжевый
        selectedWhitePaint.setStyle(Paint.Style.FILL);

        // Инициализация красок для выбранных черных клавиш
        selectedBlackPaint = new Paint();
        selectedBlackPaint.setColor(Color.parseColor("#FFA726")); // Оранжевый
        selectedBlackPaint.setStyle(Paint.Style.FILL);

        // Инициализация красок для правильных ответов
        correctPaint = new Paint();
        correctPaint.setColor(Color.parseColor("#81C784")); // Светло-зеленый
        correctPaint.setStyle(Paint.Style.FILL);
        correctPaint.setAlpha(150); // Полупрозрачный

        // Инициализация красок для неправильных ответов
        incorrectPaint = new Paint();
        incorrectPaint.setColor(Color.parseColor("#E57373")); // Светло-красный
        incorrectPaint.setStyle(Paint.Style.FILL);
        incorrectPaint.setAlpha(150); // Полупрозрачный

        // Краска для границ
        borderPaint = new Paint();
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(1);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        createKeyboard(w, h);
    }

    private void createKeyboard(int width, int height) {
        noteRects.clear();

        int whiteKeyCount = 14; // 14 белых клавиш в 2 октавах (7 + 7)
        int whiteKeyWidth = width / whiteKeyCount;
        int blackKeyWidth = whiteKeyWidth * 3 / 5;
        int blackKeyHeight = height * 2 / 3;

        int currentWhiteKey = 0;

        for (int i = 0; i < NOTE_NAMES.length; i++) {
            if (!IS_BLACK_KEY[i]) {
                // Белая клавиша
                int left = currentWhiteKey * whiteKeyWidth;
                int right = left + whiteKeyWidth;
                noteRects.add(new Rect(left, 0, right, height));
                currentWhiteKey++;
            } else {
                // Черная клавиша - позиционируем между белыми
                int left = (currentWhiteKey - 1) * whiteKeyWidth + whiteKeyWidth - blackKeyWidth / 2;
                int right = left + blackKeyWidth;
                noteRects.add(new Rect(left, 0, right, blackKeyHeight));
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Рисуем фон
        canvas.drawColor(Color.parseColor("#E0E0E0"));

        // Рисуем белые клавиши
        for (int i = 0; i < noteRects.size(); i++) {
            if (!IS_BLACK_KEY[i]) {
                Rect rect = noteRects.get(i);
                Paint paint = whiteKeyPaint;

                if (selectedNotes[i]) {
                    paint = selectedWhitePaint;
                }

                canvas.drawRect(rect, paint);
                canvas.drawRect(rect, borderPaint);
            }
        }

        // Рисуем черные клавиши поверх белых
        for (int i = 0; i < noteRects.size(); i++) {
            if (IS_BLACK_KEY[i]) {
                Rect rect = noteRects.get(i);
                Paint paint = blackKeyPaint;

                if (selectedNotes[i]) {
                    paint = selectedBlackPaint;
                }

                canvas.drawRect(rect, paint);
            }
        }

        // Рисуем разделитель между октавами (после ноты B4)
        if (noteRects.size() > 12) {
            Rect firstOctaveEnd = noteRects.get(11); // B4 - последняя нота первой октавы
            Paint dividerPaint = new Paint();
            dividerPaint.setColor(Color.RED);
            dividerPaint.setStrokeWidth(3);
            canvas.drawLine(
                    firstOctaveEnd.right,
                    0,
                    firstOctaveEnd.right,
                    getHeight(),
                    dividerPaint
            );
        }

        // Рисуем результат проверки
        if (showResult) {
            for (int noteIndex : correctNotes) {
                if (noteIndex >= 0 && noteIndex < noteRects.size()) {
                    Rect rect = noteRects.get(noteIndex);
                    Paint resultPaint = isCorrect ? correctPaint : incorrectPaint;
                    canvas.drawRect(rect, resultPaint);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled) {
            return false;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            // Проверяем, какая клавиша нажата (с конца, чтобы черные клавиши были поверх)
            for (int i = noteRects.size() - 1; i >= 0; i--) {
                Rect rect = noteRects.get(i);
                if (rect.contains((int) x, (int) y)) {
                    // Для черных клавиш проверяем, что касание в пределах высоты клавиши
                    if (IS_BLACK_KEY[i] && y > rect.height()) {
                        continue; // Игнорируем касание под черной клавишей
                    }
                    toggleNoteSelection(i);
                    return true;
                }
            }
        }
        return true;
    }

    private void toggleNoteSelection(int noteIndex) {
        selectedNotes[noteIndex] = !selectedNotes[noteIndex];

        if (listener != null) {
            listener.onNotePlayed(noteIndex, NOTE_NAMES[noteIndex]);
        }

        invalidate();
    }

    public void setOnNotePlayedListener(OnNotePlayedListener listener) {
        this.listener = listener;
    }

    public void setCorrectNotes(List<Integer> correctNoteIndexes) {
        this.correctNotes = correctNoteIndexes;
        this.showResult = false;
        invalidate();
    }

    public void showResult(boolean isCorrect) {
        this.showResult = true;
        this.isCorrect = isCorrect;
        invalidate();
    }

    public void selectNote(int noteIndex) {
        if (noteIndex >= 0 && noteIndex < selectedNotes.length) {
            selectedNotes[noteIndex] = true;
            invalidate();
        }
    }

    public void deselectNote(int noteIndex) {
        if (noteIndex >= 0 && noteIndex < selectedNotes.length) {
            selectedNotes[noteIndex] = false;
            invalidate();
        }
    }

    public void clearSelection() {
        Arrays.fill(selectedNotes, false);
        showResult = false;
        invalidate();
    }

    public boolean isNoteSelected(int noteIndex) {
        return noteIndex >= 0 && noteIndex < selectedNotes.length && selectedNotes[noteIndex];
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
        invalidate();
    }

    public List<String> getSelectedNoteNames() {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < selectedNotes.length; i++) {
            if (selectedNotes[i]) {
                names.add(NOTE_NAMES[i]);
            }
        }
        return names;
    }

    public List<Integer> getSelectedNoteIndexes() {
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < selectedNotes.length; i++) {
            if (selectedNotes[i]) {
                indexes.add(i);
            }
        }
        return indexes;
    }

    public int getNoteIndex(String noteName) {
        for (int i = 0; i < NOTE_NAMES.length; i++) {
            if (NOTE_NAMES[i].equals(noteName)) {
                return i;
            }
        }
        return -1;
    }

    public int getNoteCount() {
        return NOTE_NAMES.length;
    }
}