package com.jeanbarrossilva.power;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import static com.jeanbarrossilva.power.MainActivity.DEFAULT_BOUNCE_IN_SETTING;

public class ConversionActivity extends AppCompatActivity {
    private MainActivity mainActivity;

    Button number;
    String unit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getApplicationContext();
        unit = null;
    }
}