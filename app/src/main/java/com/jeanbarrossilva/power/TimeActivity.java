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

public class TimeActivity extends CalculatorActivity {
    CalculatorActivity calculatorActivity;

    SharedPreferences preferences;
    SharedPreferences.Editor preferencesEditor;

    EditText input;
    TextView inputSymbol;

    TextView conversionResult;
    TextView conversionSymbolResult;

    Button unit;
    Button[] options =  new Button[7];

    Button year;
    Button month;
    Button day;
    Button hour;
    Button minute;
    Button second;
    Button millisecond;

    Button decimalSeparator;

    ImageButton delete;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);

        preferences = getSharedPreferences("com.jeanbarrossilva.power", Context.MODE_PRIVATE);
        preferencesEditor = preferences.edit();

        calculatorActivity = new CalculatorActivity();

        input = findViewById(R.id.input);
        inputSymbol = findViewById(R.id.input_symbol);

        input.setFocusable(false);

        unit = findViewById(R.id.unit);

        conversionResult = findViewById(R.id.option_conversion_number_result);
        conversionSymbolResult = findViewById(R.id.option_conversion_symbol_result);

        year = findViewById(R.id.year);
        month = findViewById(R.id.month);
        day = findViewById(R.id.day);
        hour = findViewById(R.id.hour);
        minute = findViewById(R.id.minute);
        second = findViewById(R.id.second);
        millisecond = findViewById(R.id.millisecond);

        options[0] = year;
        options[1] = month;
        options[2] = day;
        options[3] = hour;
        options[4] = minute;
        options[5] = second;
        options[6] = millisecond;

        calculatorMode = findViewById(R.id.calculator_mode);

        decimalSeparator = findViewById(R.id.decimal_separator);

        delete = findViewById(R.id.delete);

        units();
        inputOption();

        // Default configuration (Celsius to Fahrenheit).
        inputSymbol.setText(getString(R.string.hour_symbol));
        selectOption(hour, options);
        preferencesEditor.putString("convertFrom", "hour")
                .apply();

        unit.setText(getString(R.string.minute));
        conversionSymbolResult.setText(getString(R.string.minute_symbol));
        preferencesEditor.putString("convertTo", "minute")
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
                bounceInterpolator = new BounceInterpolator(0.1, 5);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        unit.startAnimation(bounceIn);
                        break;
                    case MotionEvent.ACTION_UP:
                        final PopupMenu units;
                        MenuInflater inflater;

                        units = new PopupMenu(TimeActivity.this, unit);
                        inflater = units.getMenuInflater();

                        inflater.inflate(R.menu.units_time, units.getMenu());
                        units.show();

                        units.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if (item.getTitle().equals(getString(R.string.year))) {
                                    preferencesEditor.putString("convertTo", "year")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.year_symbol));
                                    unit.setText(getString(R.string.year));
                                } else if (item.getTitle().equals(getString(R.string.month))) {
                                    preferencesEditor.putString("convertTo", "month")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.month_symbol));
                                    unit.setText(getString(R.string.month));
                                } else if (item.getTitle().equals(getString(R.string.day))) {
                                    preferencesEditor.putString("convertTo", "day")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.day_symbol));
                                    unit.setText(getString(R.string.day));
                                } else if (item.getTitle().equals(getString(R.string.hour))) {
                                    preferencesEditor.putString("convertTo", "hour")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.hour_symbol));
                                    unit.setText(getString(R.string.hour));
                                } else if (item.getTitle().equals(getString(R.string.minute))) {
                                    preferencesEditor.putString("convertTo", "minute")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.fahrenheit_symbol));
                                    unit.setText(getString(R.string.minute));
                                } else if (item.getTitle().equals(getString(R.string.second))) {
                                    preferencesEditor.putString("convertTo", "second")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.second_symbol));
                                    unit.setText(getString(R.string.second));
                                } else if (item.getTitle().equals(getString(R.string.millisecond))) {
                                    preferencesEditor.putString("convertTo", "millisecond")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.millisecond_symbol));
                                    unit.setText(getString(R.string.millisecond));
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
                Toast.makeText(TimeActivity.this, getString(R.string.an_error_occurred), Toast.LENGTH_LONG).show();
                nullPointerException.printStackTrace();
            }
        }
    }

    private String convertFrom() {
        String unit = empty;

        if (preferences.getString("convertFrom", null) != null) {
            switch(Objects.requireNonNull(preferences.getString("convertFrom", null))) {
                case "year":
                    unit = "year";
                    break;
                case "month":
                    unit = "month";
                    break;
                case "day":
                    unit = "day";
                    break;
                case "hour":
                    unit = "hour";
                    break;
                case "minute":
                    unit = "minute";
                    break;
                case "second":
                    unit = "second";
                    break;
            }
        }

        return unit;
    }

    private String convertTo() {
        String unit = empty;

        if (preferences.getString("convertTo", null) != null) {
            switch(Objects.requireNonNull(preferences.getString("convertTo", null))) {
                case "year":
                    unit = "year";
                    break;
                case "month":
                    unit = "month";
                    break;
                case "day":
                    unit = "day";
                    break;
                case "hour":
                    unit = "hour";
                    break;
                case "minute":
                    unit = "minute";
                    break;
                case "second":
                    unit = "second";
                    break;
            }
        }

        return unit;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void inputOption() {
        year.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                bounceInterpolator = new BounceInterpolator(0.1, 5);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        year.startAnimation(bounceIn);
                        break;
                    case MotionEvent.ACTION_UP:
                        year.startAnimation(bounceOut);

                        selectOption(year, options);
                        inputSymbol.setText(getString(R.string.year_symbol));

                        preferencesEditor.putString("convertFrom", "year")
                                .apply();
                        break;
                }

                calc();

                return true;
            }
        });

        month.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                bounceInterpolator = new BounceInterpolator(0.1, 5);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        month.startAnimation(bounceIn);
                        break;
                    case MotionEvent.ACTION_UP:
                        month.startAnimation(bounceOut);

                        selectOption(month, options);
                        inputSymbol.setText(getString(R.string.month_symbol));

                        preferencesEditor.putString("convertFrom", "month")
                                .apply();
                        break;
                }

                calc();

                return true;
            }
        });

        day.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                bounceInterpolator = new BounceInterpolator(0.1, 5);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        day.startAnimation(bounceIn);
                        break;
                    case MotionEvent.ACTION_UP:
                        day.startAnimation(bounceOut);

                        selectOption(day, options);
                        inputSymbol.setText(getString(R.string.day_symbol));

                        preferencesEditor.putString("convertFrom", "day")
                                .apply();
                        break;
                }

                calc();

                return true;
            }
        });

        hour.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                bounceInterpolator = new BounceInterpolator(0.1, 5);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        hour.startAnimation(bounceIn);
                        break;
                    case MotionEvent.ACTION_UP:
                        hour.startAnimation(bounceOut);

                        selectOption(hour, options);
                        inputSymbol.setText(getString(R.string.hour_symbol));

                        preferencesEditor.putString("convertFrom", "hour")
                                .apply();
                        break;
                }

                calc();

                return true;
            }
        });

        minute.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                bounceInterpolator = new BounceInterpolator(0.1, 5);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        minute.startAnimation(bounceIn);
                        break;
                    case MotionEvent.ACTION_UP:
                        minute.startAnimation(bounceOut);

                        selectOption(minute, options);
                        inputSymbol.setText(getString(R.string.minute_symbol));

                        preferencesEditor.putString("convertFrom", "minute")
                                .apply();
                        break;
                }

                calc();

                return true;
            }
        });

        second.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                bounceInterpolator = new BounceInterpolator(0.1, 5);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        second.startAnimation(bounceIn);
                        break;
                    case MotionEvent.ACTION_UP:
                        second.startAnimation(bounceOut);

                        selectOption(second, options);
                        inputSymbol.setText(getString(R.string.second_symbol));

                        preferencesEditor.putString("convertFrom", "second")
                                .apply();
                        break;
                }

                calc();

                return true;
            }
        });

        millisecond.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                bounceInterpolator = new BounceInterpolator(0.1, 5);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        millisecond.startAnimation(bounceIn);
                        break;
                    case MotionEvent.ACTION_UP:
                        millisecond.startAnimation(bounceOut);

                        selectOption(millisecond, options);
                        inputSymbol.setText(getString(R.string.millisecond_symbol));

                        preferencesEditor.putString("convertFrom", "millisecond")
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
                        calculatorMode.startAnimation(bounceIn);
                        break;
                    case MotionEvent.ACTION_UP:
                        final PopupMenu calculatorModes;
                        MenuInflater inflater;

                        calculatorModes = new PopupMenu(TimeActivity.this, calculatorMode);
                        inflater = calculatorModes.getMenuInflater();

                        inflater.inflate(R.menu.calculator_modes, calculatorModes.getMenu());
                        calculatorModes.show();

                        calculatorModes.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if (item.getTitle().equals(getString(R.string.temperature))) {
                                    calculatorModes.dismiss();
                                } else if (item.getTitle().equals(getString(R.string.calculator))) {
                                    startActivity(new Intent(TimeActivity.this, CalculatorActivity.class));
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                } else if (item.getTitle().equals(getString(R.string.time))) {
                                    startActivity(new Intent(TimeActivity.this, TimeActivity.class));
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
                bounceInterpolator = new BounceInterpolator(0.1, 10);
                number = (Button) view;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        number.startAnimation(bounceIn);
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
                bounceInterpolator = new BounceInterpolator(0.1, 10);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        decimalSeparator.startAnimation(bounceIn);
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
                bounceInterpolator = new BounceInterpolator(0.1, 1.5);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        delete.startAnimation(bounceIn);
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
                    case "year":
                        switch (convertTo()) {
                            case "year":
                                conversionResult.setText(input.getText().toString());
                                break;
                            case "month":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 12));
                                break;
                            case "day":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 365));
                                break;
                            case "hour":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 8760));
                                break;
                            case "minute":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 525600));
                                break;
                            case "second":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 31536000));
                                break;
                            case "millisecond":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 31536000000.0));
                                break;
                        }

                        break;
                    case "month":
                        switch (convertTo()) {
                            case "year":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 12));
                                break;
                            case "month":
                                conversionResult.setText(input.getText().toString());
                                break;
                            case "day":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 30.417));
                                break;
                            case "hour":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 30.417));
                                break;
                            case "minute":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 730.001));
                                break;
                            case "second":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 2628002.88));
                                break;
                            case "millisecond":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 2628002880.0));
                                break;
                        }

                        break;
                    case "day":
                        switch (convertTo()) {
                            case "year":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 365));
                                break;
                            case "day":
                                conversionResult.setText(input.getText().toString());
                                break;
                            case "hour":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 24));
                                break;
                            case "minute":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 1440));
                                break;
                            case "second":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 86400));
                                break;
                            case "millisecond":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 86400000));
                                break;
                        }
                    case "hour":
                        switch (convertTo()) {
                            case "year":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 8760));
                                break;
                            case "month":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 730.001));
                                break;
                            case "day":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 24));
                                break;
                            case "hour":
                                conversionResult.setText(input.getText().toString());
                                break;
                            case "minute":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 60));
                                break;
                            case "second":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 3600));
                                break;
                            case "millisecond":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 3600000));
                                break;
                        }

                        break;
                    case "minute":
                        switch (convertTo()) {
                            case "year":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 525600));
                                break;
                            case "month":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 43800.048));
                                break;
                            case "day":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 1440));
                                break;
                            case "hour":
                                conversionResult.setText(String.valueOf(1.8 / (Double.parseDouble(input.getText().toString())) / 60));
                                break;
                            case "minute":
                                conversionResult.setText(input.getText().toString());
                                break;
                            case "second":
                                conversionResult.setText(String.valueOf((Double.parseDouble(input.getText().toString()) - 32) * 60));
                                break;
                            case "millisecond":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 60000));
                                break;
                        }

                        break;
                    case "second":
                        switch (convertTo()) {
                            case "year":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 31400000));
                                break;
                            case "month":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 2628000));
                                break;
                            case "day":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 86400));
                                break;
                            case "hour":
                                conversionResult.setText(String.valueOf(Double.parseDouble(input.getText().toString()) / 3600));
                                break;
                            case "minute":
                                conversionResult.setText(String.valueOf((Double.parseDouble(input.getText().toString()) - 273.15) / 60));
                                break;
                            case "second":
                                conversionResult.setText(input.getText().toString());
                                break;
                            case "millisecond":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 1000));
                                break;
                        }

                        break;
                    case "millisecond":
                        switch (convertTo()) {
                            case "year":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 31540000000.0));
                                break;
                            case "month":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 2628000000.0));
                                break;
                            case "day":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 86400000));
                                break;
                            case "hour":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 3600000));
                                break;
                            case "minute":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 60000));
                                break;
                            case "second":
                                conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 1000));
                                break;
                            case "millisecond":
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