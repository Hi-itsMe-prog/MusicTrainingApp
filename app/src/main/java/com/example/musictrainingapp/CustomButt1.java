package com.example.musictrainingapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomButt1 extends LinearLayout {

    private ImageView iconView;
    private TextView titleView;

    public CustomButt1(Context context) {
        this(context, null);
    }

    public CustomButt1(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomButt1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        // Устанавливаем ориентацию и другие свойства КОРНЕВОГО layout
        setOrientation(LinearLayout.HORIZONTAL);
        setClickable(true);
        setFocusable(true);
        setBackgroundResource(R.drawable.cards);

        // Inflate layout
        LayoutInflater.from(context).inflate(R.layout.custom_butt1, this, true);

        iconView = findViewById(R.id.icon);
        titleView = findViewById(R.id.title);

        // Чтение кастомных атрибутов
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(
                    attrs,
                    R.styleable.CustomButt1,
                    defStyleAttr,
                    0
            );

            try {
                int icon = typedArray.getResourceId(R.styleable.CustomButt1_icon, 0);
                String title = typedArray.getString(R.styleable.CustomButt1_title);
                int iconTint = typedArray.getColor(R.styleable.CustomButt1_iconTint, 0);

                if (icon != 0) {
                    setIcon(icon);
                }

                if (title != null) {
                    setTitle(title);
                }

                if (iconTint != 0) {
                    setIconTint(iconTint);
                }

            } finally {
                typedArray.recycle();
            }
        }
    }

    public void setIcon(int iconRes) {
        iconView.setImageResource(iconRes);
    }

    public void setTitle(String title) {
        titleView.setText(title);
    }

    public void setIconTint(int color) {
        iconView.setColorFilter(color);
    }
}