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
import java.util.List;

public class PianoKeyboardView extends View {
    private PianoViewModel viewModel;
    private List<Rect> noteRects;

    // Кисти для рисования
    private Paint whiteKeyPaint;
    private Paint blackKeyPaint;
    private Paint pressedWhitePaint;
    private Paint pressedBlackPaint;
    private Paint borderPaint;
    private Paint correctPaint;
    private Paint incorrectPaint;

    // Список постоянно подсвеченных клавиш
    private List<Integer> pressedNotes = new ArrayList<>();

    public interface OnNotePlayedListener {
        void onNotePlayed(int noteIndex, String noteName);
    }

    private OnNotePlayedListener listener;

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
        noteRects = new ArrayList<>();

        // Инициализация кистей
        whiteKeyPaint = new Paint();
        whiteKeyPaint.setColor(Color.WHITE);
        whiteKeyPaint.setStyle(Paint.Style.FILL);

        blackKeyPaint = new Paint();
        blackKeyPaint.setColor(Color.BLACK);
        blackKeyPaint.setStyle(Paint.Style.FILL);

        // Кисти для нажатых клавиш
        pressedWhitePaint = new Paint();
        pressedWhitePaint.setColor(Color.parseColor("#FFA500")); // Оранжевый для нажатых белых клавиш
        pressedWhitePaint.setStyle(Paint.Style.FILL);

        pressedBlackPaint = new Paint();
        pressedBlackPaint.setColor(Color.parseColor("#FF8C00")); // Темно-оранжевый для нажатых черных клавиш
        pressedBlackPaint.setStyle(Paint.Style.FILL);

        borderPaint = new Paint();
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(2);

        correctPaint = new Paint();
        correctPaint.setColor(Color.parseColor("#90EE90")); // Светло-зеленый для правильных нот
        correctPaint.setStyle(Paint.Style.FILL);
        correctPaint.setAlpha(128); // Полупрозрачный

        incorrectPaint = new Paint();
        incorrectPaint.setColor(Color.parseColor("#FFB6C1")); // Светло-красный для неправильных нот
        incorrectPaint.setStyle(Paint.Style.FILL);
        incorrectPaint.setAlpha(128); // Полупрозрачный

        // Создаем ViewModel по умолчанию
        viewModel = new PianoViewModel();
    }

    public void setViewModel(PianoViewModel viewModel) {
        this.viewModel = viewModel;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculateKeyRects(w, h);
    }

    private void calculateKeyRects(int width, int height) {
        noteRects.clear();

        int totalWhiteKeys = 0;
        for (KeyViewData.PianoKey key : KeyViewData.getKeys()) {
            if (!key.isBlackKey()) {
                totalWhiteKeys++;
            }
        }

        int whiteKeyWidth = width / totalWhiteKeys;
        int whiteKeyIndex = 0;

        for (int i = 0; i < KeyViewData.getKeyCount(); i++) {
            KeyViewData.PianoKey key = KeyViewData.getKey(i);

            if (key.isBlackKey()) {
                int blackKeyWidth = whiteKeyWidth * 2 / 3;
                int blackKeyHeight = height * 2 / 3;
                int left = whiteKeyIndex * whiteKeyWidth - blackKeyWidth / 2;
                int right = left + blackKeyWidth;

                noteRects.add(new Rect(left, 0, right, blackKeyHeight));
            } else {
                // Белые клавиши
                int left = whiteKeyIndex * whiteKeyWidth;
                int right = left + whiteKeyWidth;

                noteRects.add(new Rect(left, 0, right, height));
                whiteKeyIndex++;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Рисуем фон
        canvas.drawColor(Color.parseColor("#E0E0E0"));

        // Сначала рисуем белые клавиши
        for (int i = 0; i < noteRects.size(); i++) {
            KeyViewData.PianoKey key = KeyViewData.getKey(i);
            if (!key.isBlackKey()) {
                Rect rect = noteRects.get(i);

                // Выбираем цвет в зависимости от состояния клавиши
                Paint paint;
                if (pressedNotes.contains(i)) {
                    paint = pressedWhitePaint;
                } else {
                    paint = whiteKeyPaint;
                }

                canvas.drawRect(rect, paint);
                canvas.drawRect(rect, borderPaint);
            }
        }

        // Затем рисуем черные клавиши поверх белых
        for (int i = 0; i < noteRects.size(); i++) {
            KeyViewData.PianoKey key = KeyViewData.getKey(i);
            if (key.isBlackKey()) {
                Rect rect = noteRects.get(i);

                // Выбираем цвет в зависимости от состояния клавиши
                Paint paint;
                if (pressedNotes.contains(i)) {
                    paint = pressedBlackPaint;
                } else {
                    paint = blackKeyPaint;
                }

                canvas.drawRect(rect, paint);
                canvas.drawRect(rect, borderPaint);
            }
        }

        // Рисуем результат поверх всего
        if (viewModel.isShowingResult()) {
            for (int noteIndex : viewModel.getCorrectNotes()) {
                if (noteIndex >= 0 && noteIndex < noteRects.size()) {
                    Rect rect = noteRects.get(noteIndex);
                    Paint resultPaint = viewModel.isCorrect() ? correctPaint : incorrectPaint;
                    canvas.drawRect(rect, resultPaint);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!viewModel.isEnabled()) {
            return false;
        }

        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            int touchedNote = -1;

            // Сначала проверяем черные клавиши (они сверху)
            for (int i = noteRects.size() - 1; i >= 0; i--) {
                KeyViewData.PianoKey key = KeyViewData.getKey(i);
                if (key.isBlackKey()) {
                    Rect rect = noteRects.get(i);
                    if (rect.contains((int) x, (int) y)) {
                        touchedNote = i;
                        break;
                    }
                }
            }

            // Если не нашли черную клавишу, проверяем белые
            if (touchedNote == -1) {
                for (int i = noteRects.size() - 1; i >= 0; i--) {
                    KeyViewData.PianoKey key = KeyViewData.getKey(i);
                    if (!key.isBlackKey()) {
                        Rect rect = noteRects.get(i);
                        if (rect.contains((int) x, (int) y)) {
                            touchedNote = i;
                            break;
                        }
                    }
                }
            }

            if (touchedNote != -1) {
                // Добавляем клавишу в список подсвеченных (если ее там еще нет)
                if (!pressedNotes.contains(touchedNote)) {
                    pressedNotes.add(touchedNote);
                }
                handleKeyPress(touchedNote, KeyViewData.getKey(touchedNote));
            }

            return true;
        }

        return true;
    }

    private void handleKeyPress(int noteIndex, KeyViewData.PianoKey key) {
        // Перерисовываем для отображения подсветки
        invalidate();

        // Обновляем состояние ViewModel
        viewModel.selectNote(noteIndex);

        // Уведомляем слушателя
        if (listener != null) {
            listener.onNotePlayed(noteIndex, key.getNoteName());
        }
    }

    /**
     * Подсвечивает клавишу (она останется подсвеченной)
     */
    public void highlightNote(int noteIndex) {
        if (!pressedNotes.contains(noteIndex)) {
            pressedNotes.add(noteIndex);
        }
        invalidate();
    }

    /**
     * Убирает подсветку с клавиши
     */
    public void unhighlightNote(int noteIndex) {
        pressedNotes.remove((Integer) noteIndex);
        invalidate();
    }

    /**
     * Убирает подсветку со всех клавиш
     */
    public void clearHighlights() {
        pressedNotes.clear();
        invalidate();
    }

    // Остальные методы остаются без изменений...
    /**
     * Выделяет ноты на клавиатуре
     */
    public void selectNote(int noteIndex) {
        viewModel.selectNote(noteIndex);
        highlightNote(noteIndex);
    }

    /**
     * Снимает выделение с ноты
     */
    public void deselectNote(int noteIndex) {
        viewModel.deselectNote(noteIndex);
        unhighlightNote(noteIndex);
    }

    /**
     * Очищает все выделения
     */
    public void clearSelection() {
        viewModel.clearSelection();
        clearHighlights();
    }

    public void clearSelection(int noteIndex) {
        viewModel.deselectNote(noteIndex);
        unhighlightNote(noteIndex);
    }

    public List<String> getSelectedNoteNames() {
        List<String> names = new ArrayList<>();
        for (int index : viewModel.getSelectedNotes()) {
            names.add(KeyViewData.getKey(index).getNoteName());
        }
        return names;
    }

    public List<Integer> getSelectedNoteIndexes() {
        return new ArrayList<>(viewModel.getSelectedNotes());
    }

    public int getNoteIndex(String noteName) {
        return KeyViewData.getNoteIndex(noteName);
    }

    public int getNoteCount() {
        return KeyViewData.getKeyCount();
    }

    public boolean isEnabled() {
        return viewModel.isEnabled();
    }

    public boolean isShowingResult() {
        return viewModel.isShowingResult();
    }

    public boolean isCorrect() {
        return viewModel.isCorrect();
    }

    public List<Integer> getCorrectNotes() {
        return viewModel.getCorrectNotes();
    }

    public void setCorrectNotes(List<Integer> correctNoteIndexes) {
        viewModel.setCorrectNotes(correctNoteIndexes);
        invalidate();
    }

    public void showResult(boolean isCorrect) {
        viewModel.showResult(isCorrect);
        invalidate();
    }

    public void setEnabled(boolean enabled) {
        viewModel.setEnabled(enabled);
        invalidate();
    }

    public void setOnNotePlayedListener(OnNotePlayedListener listener) {
        this.listener = listener;
    }

    public String getNoteName(int noteIndex) {
        if (noteIndex >= 0 && noteIndex < KeyViewData.getKeyCount()) {
            return KeyViewData.getKey(noteIndex).getNoteName();
        }
        return "";
    }

    public boolean isBlackKey(int noteIndex) {
        if (noteIndex >= 0 && noteIndex < KeyViewData.getKeyCount()) {
            return KeyViewData.getKey(noteIndex).isBlackKey();
        }
        return false;
    }

    public Rect getNoteRect(int noteIndex) {
        if (noteIndex >= 0 && noteIndex < noteRects.size()) {
            return noteRects.get(noteIndex);
        }
        return null;
    }

    public void reset() {
        viewModel.clearSelection();
        viewModel.setEnabled(true);
        pressedNotes.clear();
        invalidate();
    }
}