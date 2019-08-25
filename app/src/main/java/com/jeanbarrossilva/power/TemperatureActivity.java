package com.jeanbarrossilva.power;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Objects;

public class TemperatureActivity extends CalculatorActivity {
    CalculatorActivity calculatorActivity;

    SharedPreferences preferences;
    SharedPreferences.Editor preferencesEditor;

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

        preferences = getSharedPreferences("com.jeanbarrossilva.power", Context.MODE_PRIVATE);
        preferencesEditor = preferences.edit();

        calculatorActivity = new CalculatorActivity();

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
        selectOption(celsius, options);
        preferencesEditor.putString("convertFrom", "celsius")
                .apply();

        unit.setText(getString(R.string.fahrenheit));
        conversionSymbolResult.setText(getString(R.string.fahrenheit_symbol));
        preferencesEditor.putString("convertTo", "fahrenheit")
                .apply();

        settings();
        calculatorMode();

        inputNumber();
        inputDecimalSeparator();
        delete();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void units() {
        unit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(unit, false, "0.1, 5");
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
                                    preferencesEditor.putString("convertTo", "celsius")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.celsius_symbol));
                                    unit.setText(getString(R.string.celsius));
                                } else if (item.getTitle().equals(getString(R.string.fahrenheit))) {
                                    preferencesEditor.putString("convertTo", "fahrenheit")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.fahrenheit_symbol));
                                    unit.setText(getString(R.string.fahrenheit));
                                } else if (item.getTitle().equals(getString(R.string.kelvin))) {
                                    preferencesEditor.putString("convertTo", "kelvin")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.kelvin_symbol));
                                    unit.setText(getString(R.string.kelvin));
                                }

                                calc();

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
    // The options Button[] must contain all options available, including the selected one.
    private void selectOption(final Button selectedOption, final Button[] options) {
        int selectedOptionId = selectedOption.getId();

        selectedOption.setTextColor(Color.BLACK);
        selectedOption.setBackgroundResource(R.drawable.option_clicked);

        for (Button option: options) {
            try {
                if (option.getId() != selectedOptionId) {
                    option.setTextColor(Color.WHITE);
                    option.setBackgroundResource(R.drawable.option);
                }
            } catch(NullPointerException nullPointerException) {
                Toast.makeText(TemperatureActivity.this, getString(R.string.an_error_occurred), Toast.LENGTH_LONG).show();
                nullPointerException.printStackTrace();
            }
        }
    }

    private String convertFrom() {
        String unit = empty;

        if (preferences.getString("convertFrom", null) != null) {
            switch(Objects.requireNonNull(preferences.getString("convertFrom", null))) {
                case "celsius":
                    unit = "celsius";
                    break;
                case "fahrenheit":
                    unit = "fahrenheit";
                    break;
                case "kelvin":
                    unit = "kelvin";
                    break;
            }
        }

        return unit;
    }

    private String convertTo() {
        String unit = empty;

        if (preferences.getString("convertTo", null) != null) {
            switch(Objects.requireNonNull(preferences.getString("convertTo", null))) {
                case "celsius":
                    unit = "celsius";
                    break;
                case "fahrenheit":
                    unit = "fahrenheit";
                    break;
                case "kelvin":
                    unit = "kelvin";
                    break;
            }
        }

        return unit;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void inputOption() {
        celsius.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(celsius, false, "0.1, 5");
                        break;
                    case MotionEvent.ACTION_UP:
                        celsius.startAnimation(bounceOut);

                        selectOption(celsius, options);
                        inputSymbol.setText(getString(R.string.celsius_symbol));

                        preferencesEditor.putString("convertFrom", "celsius")
                                .apply();
                        break;
                }

                calc();

                return true;
            }
        });

        fahrenheit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(fahrenheit, false, "0.1, 5");
                        break;
                    case MotionEvent.ACTION_UP:
                        fahrenheit.startAnimation(bounceOut);

                        selectOption(fahrenheit, options);
                        inputSymbol.setText(getString(R.string.fahrenheit_symbol));

                        preferencesEditor.putString("convertFrom", "fahrenheit")
                                .apply();
                        break;
                }

                calc();

                return true;
            }
        });

        kelvin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(kelvin, false, "0.1, 5");
                        break;
                    case MotionEvent.ACTION_UP:
                        kelvin.startAnimation(bounceOut);

                        selectOption(kelvin, options);
                        inputSymbol.setText(getString(R.string.kelvin_symbol));

                        preferencesEditor.putString("convertFrom", "kelvin")
                                .apply();
                        break;
                }

                calc();

                return true;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    void calculatorMode() {
        this.calculatorMode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                bounceInterpolator = new BounceInterpolator(0.1, 5);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(calculatorMode, true);
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

    @SuppressLint("ClickableViewAccessibility")
    private void inputNumber() {
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                number = (Button) view;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(number, false, LOW_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        number.startAnimation(bounceOut);

                        input.append(number.getText());
                        calc();
                }

                return true;
            }
        };

        for (int number: numbers) {
            findViewById(number).setOnTouchListener(onTouchListener);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void inputDecimalSeparator() {
        decimalSeparator.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(decimalSeparator, false, LOW_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        decimalSeparator.startAnimation(bounceOut);

                        input.append(decimalSeparator.getText());

                        System.out.println("Decimal separator added.");
                        System.out.println("Updated 'input.getText().toString()' value: " + input.getText().toString());

                        break;
                }

                return true;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public void delete() {
        delete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(delete, true);
                        break;
                    case MotionEvent.ACTION_UP:
                        delete.startAnimation(bounceOut);

                        if (!input.getText().toString().isEmpty()) {
                            input.setText(input.getText().toString().substring(0, input.getText().toString().length() - 1));
                            calc();
                        }

                        if (input.getText().toString().length() < 1) {
                            conversionResult.setText(getString(R.string.zero));
                        }

                        break;
                }

                return true;
            }
        });
    }

    private void calc() {
        DecimalFormat format = new DecimalFormat("#.##");

        try {
            if (!input.getText().toString().isEmpty()) {
                switch (convertFrom()) {
                    case "celsius":
                        switch(convertTo()) {
                            case "celsius":
                                conversionResult.setText(input.getText().toString());

                                break;
                            case "fahrenheit":
                                conversionResult.setText(String.valueOf((Double.valueOf(input.getText().toString()) * 9 / 5) + 32));
                                break;
                            case "kelvin":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) + 273.15));
                                break;
                        }

                        break;
                    case "fahrenheit":
                        switch (convertTo()) {
                            case "celsius":
                                conversionResult.setText(String.valueOf(1.8 / (Double.parseDouble(input.getText().toString())) - 32));
                                break;
                            case "fahrenheit":
                                conversionResult.setText(input.getText().toString());
                                break;
                            case "kelvin":
                                conversionResult.setText(String.valueOf((Double.parseDouble(input.getText().toString()) - 32) * 5 / 9 + 273.15));
                                break;
                        }

                        break;
                    case "kelvin":
                        switch (convertTo()) {
                            case "celsius":
                                conversionResult.setText(String.valueOf(Double.parseDouble(input.getText().toString()) - 273.15));
                                break;
                            case "fahrenheit":
                                conversionResult.setText(String.valueOf((Double.parseDouble(input.getText().toString()) - 273.15) * 9 / 5 + 32));
                                break;
                            case "kelvin":
                                conversionResult.setText(input.getText().toString());
                                break;
                        }

                        break;
                }

                if (conversionResult.getText().toString().contains(".")) {
                    conversionResult.setText(String.valueOf(format.format(Double.parseDouble(conversionResult.getText().toString()))));
                }

                if (conversionResult.getText().toString().endsWith(".0")) {
                    conversionResult.setText(input.getText().toString().replace(".0", ""));
                }

                if (conversionResult.getText().toString().endsWith("E")) {
                    conversionResult.setText(input.getText().toString().replace("E", "e"));
                }

                System.out.println("Number '" + number.getText() + "' added.");
                System.out.println("Updated 'input.getText().toString()' value: " + input.getText().toString());
            }
        } catch(Exception exception) {
            conversionResult.setText(getString(R.string.error));
            conversionSymbolResult.setVisibility(View.GONE);
        }
    }
}