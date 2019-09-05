package com.jeanbarrossilva.power;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

        othersHorizontalScrollView = findViewById(R.id.others_horizontal_scroll_view);
        othersHorizontalScrollView.setHorizontalScrollBarEnabled(false);

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

        // Default configuration (hour to minute).
        inputSymbol.setText(getString(R.string.hour_symbol));
        selectUnit(TimeActivity.this, hour, options);
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
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(unit, false, "0.1, 5");
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

                                    conversionSymbolResult.setText(getString(R.string.minute_symbol));
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
        year.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                bounceInterpolator = new BounceInterpolator(0.1, 5);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(year, false, "0.1, 5");
                        break;
                    case MotionEvent.ACTION_UP:
                        year.startAnimation(bounceOut);

                        selectUnit(TimeActivity.this, year, options);
                        inputSymbol.setText(getString(R.string.year_symbol));

                        preferencesEditor.putString("convertFrom", "year")
                                .apply();
                        break;
                }

                calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        month.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(month, false, "0.1, 5");
                        break;
                    case MotionEvent.ACTION_UP:
                        month.startAnimation(bounceOut);

                        selectUnit(TimeActivity.this, month, options);
                        inputSymbol.setText(getString(R.string.month_symbol));

                        preferencesEditor.putString("convertFrom", "month")
                                .apply();
                        break;
                }

                calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        day.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(day, false, "0.1, 5");
                        break;
                    case MotionEvent.ACTION_UP:
                        day.startAnimation(bounceOut);

                        selectUnit(TimeActivity.this, day, options);
                        inputSymbol.setText(getString(R.string.day_symbol));

                        preferencesEditor.putString("convertFrom", "day")
                                .apply();
                        break;
                }

                calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        hour.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(hour, false, "0.1, 5");
                        break;
                    case MotionEvent.ACTION_UP:
                        hour.startAnimation(bounceOut);

                        selectUnit(TimeActivity.this, hour, options);
                        inputSymbol.setText(getString(R.string.hour_symbol));

                        preferencesEditor.putString("convertFrom", "hour")
                                .apply();
                        break;
                }

                calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        minute.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(minute, false, "0.1, 5");
                        break;
                    case MotionEvent.ACTION_UP:
                        minute.startAnimation(bounceOut);

                        selectUnit(TimeActivity.this, minute, options);
                        inputSymbol.setText(getString(R.string.minute_symbol));

                        preferencesEditor.putString("convertFrom", "minute")
                                .apply();
                        break;
                }

                calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        second.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(second, false, "0.1, 5");
                        break;
                    case MotionEvent.ACTION_UP:
                        second.startAnimation(bounceOut);

                        selectUnit(TimeActivity.this, second, options);
                        inputSymbol.setText(getString(R.string.second_symbol));

                        preferencesEditor.putString("convertFrom", "second")
                                .apply();
                        break;
                }

                calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        millisecond.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(millisecond, false, "0.1, 5");
                        break;
                    case MotionEvent.ACTION_UP:
                        millisecond.startAnimation(bounceOut);

                        selectUnit(TimeActivity.this, millisecond, options);
                        inputSymbol.setText(getString(R.string.millisecond_symbol));

                        preferencesEditor.putString("convertFrom", "millisecond")
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
                        bounceIn(calculatorMode, false, "0.1, 5");
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
                                if (item.getTitle().equals(getString(R.string.time))) {
                                    calculatorModes.dismiss();
                                } else if (item.getTitle().equals(getString(R.string.calculator))) {
                                    startActivity(new Intent(TimeActivity.this, CalculatorActivity.class));
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                } else if (item.getTitle().equals(getString(R.string.length))) {
                                    startActivity(new Intent(TimeActivity.this, LengthActivity.class));
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                } else if (item.getTitle().equals(getString(R.string.temperature))) {
                                    startActivity(new Intent(TimeActivity.this, TemperatureActivity.class));
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
                        calc(input, conversionResult, conversionSymbolResult);
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
                        bounceIn(delete, false, "0.1, 1.5");
                        break;
                    case MotionEvent.ACTION_UP:
                        delete.startAnimation(bounceOut);

                        if (!input.getText().toString().isEmpty()) {
                            input.setText(input.getText().toString().substring(0, input.getText().toString().length() - 1));
                            calc(input, conversionResult, conversionSymbolResult);
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
}