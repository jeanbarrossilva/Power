package com.jeanbarrossilva.power;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class HiddenMode extends AppCompatActivity {
    Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hidden_mode);

        add = findViewById(R.id.add);

        if (Build.VERSION.SDK_INT >= 21) {
            add.setElevation(5);
        }
    }
}