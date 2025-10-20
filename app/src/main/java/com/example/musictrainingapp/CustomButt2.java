package com.example.musictrainingapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomButt2 extends LinearLayout {

    private ImageView iconView;
    private TextView titleView;
    private TextView subtitleView;

    public CustomButt2(Context context) {
        this(context, null);
    }

    public CustomButt2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomButt2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        // Устанавливаем свойства КОРНЕВОГО layout
        setOrientation(LinearLayout.HORIZONTAL);
        setClickable(true);
        setFocusable(true);
        setBackgroundResource(R.drawable.cards);

        // Inflate layout
        LayoutInflater.from(context).inflate(R.layout.custom_butt2, this, true);

        iconView = findViewById(R.id.icon);
        titleView = findViewById(R.id.title);
        subtitleView = findViewById(R.id.subtitle);

        // Чтение кастомных атрибутов
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(
                    attrs,
                    R.styleable.CustomButt2,
                    defStyleAttr,
                    0
            );

            try {
                int icon = typedArray.getResourceId(R.styleable.CustomButt2_icon, 0);
                String title = typedArray.getString(R.styleable.CustomButt2_title);
                String subtitle = typedArray.getString(R.styleable.CustomButt2_subtitle);
                int iconTint = typedArray.getColor(R.styleable.CustomButt2_iconTint, 0);

                if (icon != 0) {
                    setIcon(icon);
                }

                if (title != null) {
                    setTitle(title);
                }

                if (subtitle != null) {
                    setSubtitle(subtitle);
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

    public void setSubtitle(String subtitle) {
        subtitleView.setText(subtitle);
    }

    public void setIconTint(int color) {
        iconView.setColorFilter(color);
    }
}