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

public class LengthActivity extends CalculatorActivity {
    CalculatorActivity calculatorActivity;

    SharedPreferences preferences;
    SharedPreferences.Editor preferencesEditor;

    EditText input;
    TextView inputSymbol;

    TextView conversionResult;
    TextView conversionSymbolResult;

    Button unit;
    Button[] options =  new Button[7];

    Button kilometer;
    Button hectometer;
    Button decameter;
    Button meter;
    Button decimeter;
    Button centimeter;
    Button millimeter;

    Button decimalSeparator;

    ImageButton delete;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_length);

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

        kilometer = findViewById(R.id.kilometer);
        hectometer = findViewById(R.id.hectometer);
        decameter = findViewById(R.id.decameter);
        meter = findViewById(R.id.meter);
        decimeter = findViewById(R.id.decimeter);
        centimeter = findViewById(R.id.centimeter);
        millimeter = findViewById(R.id.millimeter);

        options[0] = kilometer;
        options[1] = hectometer;
        options[2] = decameter;
        options[3] = meter;
        options[4] = decimeter;
        options[5] = centimeter;
        options[6] = millimeter;

        calculatorMode = findViewById(R.id.calculator_mode);

        decimalSeparator = findViewById(R.id.decimal_separator);

        delete = findViewById(R.id.delete);

        units();
        inputOption();

        // Default configuration (meter to kilometer).
        inputSymbol.setText(getString(R.string.meter_symbol));
        selectOption(meter, options);
        preferencesEditor.putString("convertFrom", "meter")
                .apply();

        unit.setText(getString(R.string.kilometer));
        conversionSymbolResult.setText(getString(R.string.kilometer_symbol));
        preferencesEditor.putString("convertTo", "kilometer")
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

                        if (unit.getAnimation().hasEnded()) {
                            unit.setScaleX(0.8f);
                            unit.setScaleY(0.8f);
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        final PopupMenu units;
                        MenuInflater inflater;

                        units = new PopupMenu(LengthActivity.this, unit);
                        inflater = units.getMenuInflater();

                        inflater.inflate(R.menu.units_length, units.getMenu());
                        units.show();

                        units.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if (item.getTitle().equals(getString(R.string.kilometer))) {
                                    preferencesEditor.putString("convertTo", "kilometer")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.kilometer_symbol));
                                    unit.setText(getString(R.string.kilometer));
                                } else if (item.getTitle().equals(getString(R.string.hectometer))) {
                                    preferencesEditor.putString("convertTo", "hectometer")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.hectometer_symbol));
                                    unit.setText(getString(R.string.hectometer));
                                } else if (item.getTitle().equals(getString(R.string.decameter))) {
                                    preferencesEditor.putString("convertTo", "decameter")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.decameter_symbol));
                                    unit.setText(getString(R.string.decameter));
                                } else if (item.getTitle().equals(getString(R.string.meter))) {
                                    preferencesEditor.putString("convertTo", "meter")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.meter_symbol));
                                    unit.setText(getString(R.string.meter));
                                } else if (item.getTitle().equals(getString(R.string.decimeter))) {
                                    preferencesEditor.putString("convertTo", "decimeter")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.decimeter_symbol));
                                    unit.setText(getString(R.string.decimeter));
                                } else if (item.getTitle().equals(getString(R.string.centimeter))) {
                                    preferencesEditor.putString("convertTo", "centimeter")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.centimeter_symbol));
                                    unit.setText(getString(R.string.centimeter));
                                } else if (item.getTitle().equals(getString(R.string.millimeter))) {
                                    preferencesEditor.putString("convertTo", "millimeter")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.millimeter_symbol));
                                    unit.setText(getString(R.string.millimeter));
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
                Toast.makeText(LengthActivity.this, getString(R.string.an_error_occurred), Toast.LENGTH_LONG).show();
                nullPointerException.printStackTrace();
            }
        }
    }

    private String convertFrom() {
        String unit = empty;

        if (preferences.getString("convertFrom", null) != null) {
            switch(Objects.requireNonNull(preferences.getString("convertFrom", null))) {
                case "kilometer":
                    unit = "kilometer";
                    break;
                case "hectometer":
                    unit = "hectometer";
                    break;
                case "decameter":
                    unit = "decameter";
                    break;
                case "meter":
                    unit = "meter";
                    break;
                case "decimeter":
                    unit = "decimeter";
                    break;
                case "centimeter":
                    unit = "centimeter";
                    break;
                case "millimeter":
                    unit = "millimeter";
                    break;
            }
        }

        return unit;
    }

    private String convertTo() {
        String unit = empty;

        if (preferences.getString("convertTo", null) != null) {
            switch(Objects.requireNonNull(preferences.getString("convertTo", null))) {
                case "kilometer":
                    unit = "kilometer";
                    break;
                case "hectometer":
                    unit = "hectometer";
                    break;
                case "decameter":
                    unit = "decameter";
                    break;
                case "meter":
                    unit = "meter";
                    break;
                case "decimeter":
                    unit = "decimeter";
                    break;
                case "centimeter":
                    unit = "centimeter";
                    break;
                case "millimeter":
                    unit = "millimeter";
                    break;
            }
        }

        return unit;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void inputOption() {
        kilometer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(kilometer, false, "0.1, 5");
                        break;
                    case MotionEvent.ACTION_UP:
                        kilometer.startAnimation(bounceOut);

                        selectOption(kilometer, options);
                        inputSymbol.setText(getString(R.string.kilometer_symbol));

                        preferencesEditor.putString("convertFrom", "kilometer")
                                .apply();
                        break;
                }

                calc();

                return true;
            }
        });

        hectometer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(hectometer, false, "0.1, 5");
                        break;
                    case MotionEvent.ACTION_UP:
                        hectometer.startAnimation(bounceOut);

                        selectOption(hectometer, options);
                        inputSymbol.setText(getString(R.string.hectometer_symbol));

                        preferencesEditor.putString("convertFrom", "hectometer")
                                .apply();
                        break;
                }

                calc();

                return true;
            }
        });

        decameter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(decameter, false, "0.1, 5");
                        break;
                    case MotionEvent.ACTION_UP:
                        decameter.startAnimation(bounceOut);

                        selectOption(decameter, options);
                        inputSymbol.setText(getString(R.string.decameter_symbol));

                        preferencesEditor.putString("convertFrom", "decameter")
                                .apply();
                        break;
                }

                calc();

                return true;
            }
        });

        meter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(meter, false, "0.1, 5");
                        break;
                    case MotionEvent.ACTION_UP:
                        meter.startAnimation(bounceOut);

                        selectOption(meter, options);
                        inputSymbol.setText(getString(R.string.meter_symbol));

                        preferencesEditor.putString("convertFrom", "meter")
                                .apply();
                        break;
                }

                calc();

                return true;
            }
        });

        decimeter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(decimeter, false, "0.1, 5");
                        break;
                    case MotionEvent.ACTION_UP:
                        decimeter.startAnimation(bounceOut);

                        selectOption(decimeter, options);
                        inputSymbol.setText(getString(R.string.decimeter_symbol));

                        preferencesEditor.putString("convertFrom", "decimeter")
                                .apply();
                        break;
                }

                calc();

                return true;
            }
        });

        centimeter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(centimeter, false, "0.1, 5");
                        break;
                    case MotionEvent.ACTION_UP:
                        centimeter.startAnimation(bounceOut);

                        selectOption(centimeter, options);
                        inputSymbol.setText(getString(R.string.centimeter_symbol));

                        preferencesEditor.putString("convertFrom", "centimeter")
                                .apply();
                        break;
                }

                calc();

                return true;
            }
        });

        millimeter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(millimeter, false, "0.1, 5");
                        break;
                    case MotionEvent.ACTION_UP:
                        millimeter.startAnimation(bounceOut);

                        selectOption(millimeter, options);
                        inputSymbol.setText(getString(R.string.millimeter_symbol));

                        preferencesEditor.putString("convertFrom", "millimeter")
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
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(calculatorMode, false, "0.1, 5");
                        break;
                    case MotionEvent.ACTION_UP:
                        final PopupMenu calculatorModes;
                        MenuInflater inflater;

                        calculatorModes = new PopupMenu(LengthActivity.this, calculatorMode);
                        inflater = calculatorModes.getMenuInflater();

                        inflater.inflate(R.menu.calculator_modes, calculatorModes.getMenu());
                        calculatorModes.show();

                        calculatorModes.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if (item.getTitle().equals(getString(R.string.length))) {
                                    calculatorModes.dismiss();
                                } else if (item.getTitle().equals(getString(R.string.calculator))) {
                                    startActivity(new Intent(LengthActivity.this, CalculatorActivity.class));
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                } else if (item.getTitle().equals(getString(R.string.temperature))) {
                                    startActivity(new Intent(LengthActivity.this, TemperatureActivity.class));
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                } else if (item.getTitle().equals(getString(R.string.time))) {
                                    startActivity(new Intent(LengthActivity.this, TimeActivity.class));
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
                    case "kilometer":
                        switch (convertTo()) {
                            case "kilometer":
                                conversionResult.setText(input.getText().toString());
                                break;
                            case "hectometer":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 10));
                                break;
                            case "decameter":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 100));
                                break;
                            case "meter":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 1000));
                                break;
                            case "decimeter":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 10000));
                                break;
                            case "centimeter":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 100000));
                                break;
                            case "millimeter":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 1000000));
                                break;
                        }

                        break;
                    case "hectometer":
                        switch (convertTo()) {
                            case "kilometer":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 10));
                                break;
                            case "hectometer":
                                conversionResult.setText(input.getText().toString());
                                break;
                            case "decameter":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 10));
                                break;
                            case "meter":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 100));
                                break;
                            case "decimeter":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 1000));
                                break;
                            case "centimeter":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 10000));
                                break;
                            case "millimeter":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 100000));
                                break;
                        }

                        break;
                    case "decameter":
                        switch (convertTo()) {
                            case "kilometer":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 100));
                                break;
                            case "hectometer":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 10));
                                break;
                            case "decameter":
                                conversionResult.setText(input.getText().toString());
                                break;
                            case "meter":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 10));
                                break;
                            case "decimeter":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 100));
                                break;
                            case "centimeter":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 1000));
                                break;
                            case "millimeter":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 10000));
                                break;
                        }
                    case "meter":
                        switch (convertTo()) {
                            case "kilometer":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 1000));
                                break;
                            case "hectometer":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 100));
                                break;
                            case "decameter":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 10));
                                break;
                            case "meter":
                                conversionResult.setText(input.getText().toString());
                                break;
                            case "decimeter":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 10));
                                break;
                            case "centimeter":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 100));
                                break;
                            case "millimeter":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 1000));
                                break;
                        }

                        break;
                    case "decimeter":
                        switch (convertTo()) {
                            case "kilometer":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 10000));
                                break;
                            case "hectometer":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 1000));
                                break;
                            case "decameter":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 100));
                                break;
                            case "meter":
                                conversionResult.setText(String.valueOf(1.8 / (Double.parseDouble(input.getText().toString())) / 10));
                                break;
                            case "decimeter":
                                conversionResult.setText(input.getText().toString());
                                break;
                            case "centimeter":
                                conversionResult.setText(String.valueOf(Double.parseDouble(input.getText().toString()) * 10));
                                break;
                            case "millimeter":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 100));
                                break;
                        }

                        break;
                    case "centimeter":
                        switch (convertTo()) {
                            case "kilometer":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 100000));
                                break;
                            case "hectometer":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 10000));
                                break;
                            case "decameter":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 1000));
                                break;
                            case "meter":
                                conversionResult.setText(String.valueOf(Double.parseDouble(input.getText().toString()) / 100));
                                break;
                            case "decimeter":
                                conversionResult.setText(String.valueOf((Double.parseDouble(input.getText().toString())) / 10));
                                break;
                            case "centimeter":
                                conversionResult.setText(input.getText().toString());
                                break;
                            case "millimeter":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 10));
                                break;
                        }

                        break;
                    case "millimeter":
                        switch (convertTo()) {
                            case "kilometer":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 1000000));
                                break;
                            case "hectometer":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 100000));
                                break;
                            case "decameter":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 10000));
                                break;
                            case "meter":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 1000));
                                break;
                            case "decimeter":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 100));
                                break;
                            case "centimeter":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 10));
                                break;
                            case "millimeter":
                                conversionResult.setText(input.getText().toString());
                                break;
                        }
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