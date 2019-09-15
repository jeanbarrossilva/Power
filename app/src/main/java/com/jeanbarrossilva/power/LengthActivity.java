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

public class LengthActivity extends CalculatorActivity {
    EditText input;
    TextView inputSymbol;

    TextView conversionResult;
    TextView conversionSymbolResult;

    Button unit;
    Button[] options =  new Button[9];

    Button lightYear;
    Button kilometer;
    Button hectometer;
    Button decameter;
    Button meter;
    Button decimeter;
    Button centimeter;
    Button millimeter;
    Button micrometer;

    Button decimalSeparator;

    ImageButton delete;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_length);

        input = findViewById(R.id.input);
        inputSymbol = findViewById(R.id.input_symbol);

        input.setFocusable(false);

        unit = findViewById(R.id.unit);

        conversionResult = findViewById(R.id.option_conversion_number_result);
        conversionSymbolResult = findViewById(R.id.option_conversion_symbol_result);

        othersHorizontalScrollView = findViewById(R.id.others_horizontal_scroll_view);
        othersHorizontalScrollView.setHorizontalScrollBarEnabled(false);

        lightYear = findViewById(R.id.light_year);
        kilometer = findViewById(R.id.kilometer);
        hectometer = findViewById(R.id.hectometer);
        decameter = findViewById(R.id.decameter);
        meter = findViewById(R.id.meter);
        decimeter = findViewById(R.id.decimeter);
        centimeter = findViewById(R.id.centimeter);
        millimeter = findViewById(R.id.millimeter);
        micrometer = findViewById(R.id.micrometer);

        options[0] = lightYear;
        options[1] = kilometer;
        options[2] = hectometer;
        options[3] = decameter;
        options[4] = meter;
        options[5] = decimeter;
        options[6] = centimeter;
        options[7] = millimeter;
        options[8] = micrometer;

        calculatorMode = findViewById(R.id.calculator_mode);

        decimalSeparator = findViewById(R.id.decimal_separator);

        delete = findViewById(R.id.delete);

        units();
        inputOption();

        // Default configuration (meter to kilometer).
        inputSymbol.setText(getString(R.string.meter_symbol));
        selectUnit(LengthActivity.this, meter, options);
        getPreferencesEditor().putString("convertFrom", "meter")
                .apply();

        unit.setText(getString(R.string.kilometer));
        conversionSymbolResult.setText(getString(R.string.kilometer_symbol));
        getPreferencesEditor().putString("convertTo", "kilometer")
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

                        units = new PopupMenu(LengthActivity.this, unit);
                        inflater = units.getMenuInflater();

                        inflater.inflate(R.menu.units_length, units.getMenu());
                        units.show();

                        units.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if (item.getTitle().equals(getString(R.string.light_year))) {
                                    getPreferencesEditor().putString("convertTo", "lightYear")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.light_year_symbol));
                                } else if (item.getTitle().equals(getString(R.string.kilometer))) {
                                    getPreferencesEditor().putString("convertTo", "kilometer")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.kilometer_symbol));
                                    unit.setText(getString(R.string.kilometer));
                                } else if (item.getTitle().equals(getString(R.string.hectometer))) {
                                    getPreferencesEditor().putString("convertTo", "hectometer")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.hectometer_symbol));
                                    unit.setText(getString(R.string.hectometer));
                                } else if (item.getTitle().equals(getString(R.string.decameter))) {
                                    getPreferencesEditor().putString("convertTo", "decameter")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.decameter_symbol));
                                    unit.setText(getString(R.string.decameter));
                                } else if (item.getTitle().equals(getString(R.string.meter))) {
                                    getPreferencesEditor().putString("convertTo", "meter")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.meter_symbol));
                                    unit.setText(getString(R.string.meter));
                                } else if (item.getTitle().equals(getString(R.string.decimeter))) {
                                    getPreferencesEditor().putString("convertTo", "decimeter")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.decimeter_symbol));
                                    unit.setText(getString(R.string.decimeter));
                                } else if (item.getTitle().equals(getString(R.string.centimeter))) {
                                    getPreferencesEditor().putString("convertTo", "centimeter")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.centimeter_symbol));
                                    unit.setText(getString(R.string.centimeter));
                                } else if (item.getTitle().equals(getString(R.string.millimeter))) {
                                    getPreferencesEditor().putString("convertTo", "millimeter")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.millimeter_symbol));
                                    unit.setText(getString(R.string.millimeter));
                                } else if (item.getTitle().equals(getString(R.string.micrometer))) {
                                    getPreferencesEditor().putString("convertTo", "micrometer")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.micrometer_symbol));
                                    unit.setText(getString(R.string.micrometer));
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
        lightYear.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(lightYear, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        lightYear.startAnimation(getBounceOut());

                        selectUnit(LengthActivity.this, lightYear, options);
                        inputSymbol.setText(getString(R.string.light_year_symbol));

                        getPreferencesEditor().putString("convertFrom", "lightYear")
                                .apply();
                        break;
                }

                calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        kilometer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(kilometer, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        kilometer.startAnimation(getBounceOut());

                        selectUnit(LengthActivity.this, kilometer, options);
                        inputSymbol.setText(getString(R.string.kilometer_symbol));

                        getPreferencesEditor().putString("convertFrom", "kilometer")
                                .apply();
                        break;
                }

                calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        hectometer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(hectometer, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        hectometer.startAnimation(getBounceOut());

                        selectUnit(LengthActivity.this, hectometer, options);
                        inputSymbol.setText(getString(R.string.hectometer_symbol));

                        getPreferencesEditor().putString("convertFrom", "hectometer")
                                .apply();
                        break;
                }

                calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        decameter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(decameter, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        decameter.startAnimation(getBounceOut());

                        selectUnit(LengthActivity.this, decameter, options);
                        inputSymbol.setText(getString(R.string.decameter_symbol));

                        getPreferencesEditor().putString("convertFrom", "decameter")
                                .apply();
                        break;
                }

                calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        meter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(meter, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        meter.startAnimation(getBounceOut());

                        selectUnit(LengthActivity.this, meter, options);
                        inputSymbol.setText(getString(R.string.meter_symbol));

                        getPreferencesEditor().putString("convertFrom", "meter")
                                .apply();
                        break;
                }

                calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        decimeter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(decimeter, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        decimeter.startAnimation(getBounceOut());

                        selectUnit(LengthActivity.this, decimeter, options);
                        inputSymbol.setText(getString(R.string.decimeter_symbol));

                        getPreferencesEditor().putString("convertFrom", "decimeter")
                                .apply();
                        break;
                }

                calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        centimeter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(centimeter, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        centimeter.startAnimation(getBounceOut());

                        selectUnit(LengthActivity.this, centimeter, options);
                        inputSymbol.setText(getString(R.string.centimeter_symbol));

                        getPreferencesEditor().putString("convertFrom", "centimeter")
                                .apply();
                        break;
                }

                calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        millimeter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(millimeter, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        millimeter.startAnimation(getBounceOut());

                        selectUnit(LengthActivity.this, millimeter, options);
                        inputSymbol.setText(getString(R.string.millimeter_symbol));

                        getPreferencesEditor().putString("convertFrom", "millimeter")
                                .apply();
                        break;
                }

                calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        micrometer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(micrometer, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        micrometer.startAnimation(getBounceOut());

                        selectUnit(LengthActivity.this, micrometer, options);
                        inputSymbol.setText(getString(R.string.micrometer_symbol));

                        getPreferencesEditor().putString("convertFrom", "micrometer")
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
}