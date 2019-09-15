package com.jeanbarrossilva.power;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

public class TemperatureActivity extends CalculatorActivity {
    EditText input;
    TextView inputSymbol;

    TextView conversionResult;
    TextView conversionSymbolResult;

    Button unit;
    Button[] options =  new Button[3];

    Button celsius;
    Button fahrenheit;
    Button kelvin;

    Button decimalSeparator;

    ImageButton delete;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        input = findViewById(R.id.input);
        inputSymbol = findViewById(R.id.input_symbol);

        input.setFocusable(false);

        unit = findViewById(R.id.unit);

        conversionResult = findViewById(R.id.option_conversion_number_result);
        conversionSymbolResult = findViewById(R.id.option_conversion_symbol_result);

        othersHorizontalScrollView = findViewById(R.id.others_horizontal_scroll_view);
        othersHorizontalScrollView.setHorizontalScrollBarEnabled(false);

        celsius = findViewById(R.id.celsius);
        fahrenheit = findViewById(R.id.fahrenheit);
        kelvin = findViewById(R.id.kelvin);

        options[0] = celsius;
        options[1] = fahrenheit;
        options[2] = kelvin;

        calculatorMode = findViewById(R.id.calculator_mode);

        decimalSeparator = findViewById(R.id.decimal_separator);

        delete = findViewById(R.id.delete);

        units();
        inputOption();

        // Default configuration (Celsius to Fahrenheit).
        inputSymbol.setText(getString(R.string.celsius_symbol));
        selectUnit(TemperatureActivity.this, celsius, options);
        getPreferencesEditor().putString("convertFrom", "celsius")
                .apply();

        unit.setText(getString(R.string.fahrenheit));
        conversionSymbolResult.setText(getString(R.string.fahrenheit_symbol));
        getPreferencesEditor().putString("convertTo", "fahrenheit")
                .apply();

        settings();
        calculatorMode();

        inputNumber(input, conversionResult, conversionSymbolResult, calc);
        inputDecimalSeparator(input, calc, decimalSeparator);
        delete(input, delete);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void units() {
        unit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(unit, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        final PopupMenu units;
                        MenuInflater inflater;

                        units = new PopupMenu(TemperatureActivity.this, unit);
                        inflater = units.getMenuInflater();

                        inflater.inflate(R.menu.units_temperature, units.getMenu());
                        units.show();

                        units.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if (item.getTitle().equals(getString(R.string.celsius))) {
                                    getPreferencesEditor().putString("convertTo", "celsius")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.celsius_symbol));
                                    unit.setText(getString(R.string.celsius));
                                } else if (item.getTitle().equals(getString(R.string.fahrenheit))) {
                                    getPreferencesEditor().putString("convertTo", "fahrenheit")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.fahrenheit_symbol));
                                    unit.setText(getString(R.string.fahrenheit));
                                } else if (item.getTitle().equals(getString(R.string.kelvin))) {
                                    getPreferencesEditor().putString("convertTo", "kelvin")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.kelvin_symbol));
                                    unit.setText(getString(R.string.kelvin));
                                }

                                calc(input, conversionResult, conversionSymbolResult);

                                return true;
                            }
                        });

                        break;
                }

                return true;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void inputOption() {
        celsius.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(celsius, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        celsius.startAnimation(getBounceOut());

                        selectUnit(TemperatureActivity.this, celsius, options);
                        inputSymbol.setText(getString(R.string.celsius_symbol));

                        getPreferencesEditor().putString("convertFrom", "celsius")
                                .apply();
                        break;
                }

                calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        fahrenheit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(fahrenheit, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        fahrenheit.startAnimation(getBounceOut());

                        selectUnit(TemperatureActivity.this, fahrenheit, options);
                        inputSymbol.setText(getString(R.string.fahrenheit_symbol));

                        getPreferencesEditor().putString("convertFrom", "fahrenheit")
                                .apply();
                        break;
                }

                calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        kelvin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(kelvin, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        kelvin.startAnimation(getBounceOut());

                        selectUnit(TemperatureActivity.this, kelvin, options);
                        inputSymbol.setText(getString(R.string.kelvin_symbol));

                        getPreferencesEditor().putString("convertFrom", "kelvin")
                                .apply();
                        break;
                }

                calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    void calculatorMode() {
        this.calculatorMode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(calculatorMode, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        final PopupMenu calculatorModes;
                        MenuInflater inflater;

                        calculatorModes = new PopupMenu(TemperatureActivity.this, calculatorMode);
                        inflater = calculatorModes.getMenuInflater();

                        inflater.inflate(R.menu.calculator_modes, calculatorModes.getMenu());
                        calculatorModes.show();

                        calculatorModes.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if (item.getTitle().equals(getString(R.string.temperature))) {
                                    calculatorModes.dismiss();
                                } else if (item.getTitle().equals(getString(R.string.calculator))) {
                                    startActivity(new Intent(TemperatureActivity.this, CalculatorActivity.class));
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                } else if (item.getTitle().equals(getString(R.string.length))) {
                                    startActivity(new Intent(TemperatureActivity.this, LengthActivity.class));
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                } else if (item.getTitle().equals(getString(R.string.time))) {
                                    startActivity(new Intent(TemperatureActivity.this, TimeActivity.class));
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                }

                                return true;
                            }
                        });

                        break;
                }

                return true;
            }
        });
    }
}