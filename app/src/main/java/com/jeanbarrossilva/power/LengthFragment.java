package com.jeanbarrossilva.power;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import static com.jeanbarrossilva.power.MainActivity.DEFAULT_BOUNCE_IN_SETTING;

public class LengthFragment extends CalculatorFragment {
    private TextView inputSymbol;

    private TextView conversionResult;
    private TextView conversionSymbolResult;

    private Button unit;
    private Button[] options =  new Button[10];

    private Button lightYear;
    private Button kilometer;
    private Button hectometer;
    private Button decameter;
    private Button mile;
    private Button meter;
    private Button decimeter;
    private Button centimeter;
    private Button millimeter;
    private Button micrometer;

    public LengthFragment() {

    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calculator_length, container, false);
        mainActivity = (MainActivity) getActivity();

        input = view.findViewById(R.id.input);
        inputSymbol = view.findViewById(R.id.input_symbol);

        unit = view.findViewById(R.id.unit);

        conversionResult = view.findViewById(R.id.option_conversion_number_result);
        conversionSymbolResult = view.findViewById(R.id.option_conversion_symbol_result);

        othersHorizontalScrollView = view.findViewById(R.id.others_horizontal_scroll_view);
        othersHorizontalScrollView.setHorizontalScrollBarEnabled(false);

        lightYear = view.findViewById(R.id.light_year);
        kilometer = view.findViewById(R.id.kilometer);
        hectometer = view.findViewById(R.id.hectometer);
        decameter = view.findViewById(R.id.decameter);
        mile = view.findViewById(R.id.mile);
        meter = view.findViewById(R.id.meter);
        decimeter = view.findViewById(R.id.decimeter);
        centimeter = view.findViewById(R.id.centimeter);
        millimeter = view.findViewById(R.id.millimeter);
        micrometer = view.findViewById(R.id.micrometer);

        options[0] = lightYear;
        options[1] = kilometer;
        options[2] = hectometer;
        options[3] = decameter;
        options[4] = mile;
        options[5] = meter;
        options[6] = decimeter;
        options[7] = centimeter;
        options[8] = millimeter;
        options[9] = micrometer;

        keypadButtons[10] = view.findViewById(R.id.decimal_separator);
        calculatorMode = view.findViewById(R.id.calculator_mode);
        delete = view.findViewById(R.id.delete);

        units();
        inputOption();

        // Default configuration (meter to kilometer).
        inputSymbol.setText(getString(R.string.meter_symbol));
        mainActivity.selectButton(meter, options, R.drawable.option_clicked);
        mainActivity.getPreferencesEditor().putString("convertFrom", "meter")
                .apply();

        unit.setText(getString(R.string.kilometer));
        conversionSymbolResult.setText(getString(R.string.kilometer_symbol));
        mainActivity.getPreferencesEditor().putString("convertTo", "kilometer")
                .apply();

        mainActivity.calculatorMode(context, calculatorMode);

        mainActivity.inputNumber(view, input, conversionResult, conversionSymbolResult, input.getText().toString());
        inputDecimalSeparator(input);
        mainActivity.delete(input, delete, conversionResult, conversionSymbolResult);

        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void units() {
        unit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mainActivity.bounceIn(unit, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        final PopupMenu units;
                        MenuInflater inflater;

                        units = new PopupMenu(context, unit);
                        inflater = units.getMenuInflater();

                        inflater.inflate(R.menu.units_length, units.getMenu());
                        units.show();

                        units.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if (item.getTitle().equals(getString(R.string.light_year))) {
                                    mainActivity.getPreferencesEditor().putString("convertTo", "lightYear")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.light_year_symbol));
                                } else if (item.getTitle().equals(getString(R.string.kilometer))) {
                                    mainActivity.getPreferencesEditor().putString("convertTo", "kilometer")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.kilometer_symbol));
                                    unit.setText(getString(R.string.kilometer));
                                } else if (item.getTitle().equals(getString(R.string.hectometer))) {
                                    mainActivity.getPreferencesEditor().putString("convertTo", "hectometer")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.hectometer_symbol));
                                    unit.setText(getString(R.string.hectometer));
                                } else if (item.getTitle().equals(getString(R.string.decameter))) {
                                    mainActivity.getPreferencesEditor().putString("convertTo", "decameter")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.decameter_symbol));
                                    unit.setText(getString(R.string.decameter));
                                } else if (item.getTitle().equals(getString(R.string.mile))) {
                                    mainActivity.getPreferencesEditor().putString("convertTo", "mile")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.mile_symbol));
                                    unit.setText(getString(R.string.mile));
                                } else if (item.getTitle().equals(getString(R.string.meter))) {
                                    mainActivity.getPreferencesEditor().putString("convertTo", "meter")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.meter_symbol));
                                    unit.setText(getString(R.string.meter));
                                } else if (item.getTitle().equals(getString(R.string.mile))) {
                                    mainActivity.getPreferencesEditor().putString("convertTo", "mile")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.mile_symbol));
                                    unit.setText(getString(R.string.mile));
                                } else if (item.getTitle().equals(getString(R.string.decimeter))) {
                                    mainActivity.getPreferencesEditor().putString("convertTo", "decimeter")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.decimeter_symbol));
                                    unit.setText(getString(R.string.decimeter));
                                } else if (item.getTitle().equals(getString(R.string.centimeter))) {
                                    mainActivity.getPreferencesEditor().putString("convertTo", "centimeter")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.centimeter_symbol));
                                    unit.setText(getString(R.string.centimeter));
                                } else if (item.getTitle().equals(getString(R.string.millimeter))) {
                                    mainActivity.getPreferencesEditor().putString("convertTo", "millimeter")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.millimeter_symbol));
                                    unit.setText(getString(R.string.millimeter));
                                } else if (item.getTitle().equals(getString(R.string.micrometer))) {
                                    mainActivity.getPreferencesEditor().putString("convertTo", "micrometer")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.micrometer_symbol));
                                    unit.setText(getString(R.string.micrometer));
                                }

                                mainActivity.calc(input, conversionResult, conversionSymbolResult);

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
                        mainActivity.bounceIn(lightYear, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        lightYear.startAnimation(mainActivity.getBounceOut());

                        mainActivity.selectButton(lightYear, options, R.drawable.option_clicked);
                        inputSymbol.setText(getString(R.string.light_year_symbol));

                        mainActivity.getPreferencesEditor().putString("convertFrom", "lightYear")
                                .apply();
                        break;
                }

                mainActivity.calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        kilometer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mainActivity.bounceIn(kilometer, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        kilometer.startAnimation(mainActivity.getBounceOut());

                        mainActivity.selectButton(kilometer, options, R.drawable.option_clicked);
                        inputSymbol.setText(getString(R.string.kilometer_symbol));

                        mainActivity.getPreferencesEditor().putString("convertFrom", "kilometer")
                                .apply();
                        break;
                }

                mainActivity.calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        hectometer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mainActivity.bounceIn(hectometer, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        hectometer.startAnimation(mainActivity.getBounceOut());

                        mainActivity.selectButton(hectometer, options, R.drawable.option_clicked);
                        inputSymbol.setText(getString(R.string.hectometer_symbol));

                        mainActivity.getPreferencesEditor().putString("convertFrom", "hectometer")
                                .apply();
                        break;
                }

                mainActivity.calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        decameter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mainActivity.bounceIn(decameter, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        decameter.startAnimation(mainActivity.getBounceOut());

                        mainActivity.selectButton(decameter, options, R.drawable.option_clicked);
                        inputSymbol.setText(getString(R.string.decameter_symbol));

                        mainActivity.getPreferencesEditor().putString("convertFrom", "decameter")
                                .apply();
                        break;
                }

                mainActivity.calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        mile.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mainActivity.bounceIn(mile, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        meter.startAnimation(mainActivity.getBounceOut());

                        mainActivity.selectButton(mile, options, R.drawable.option_clicked);
                        inputSymbol.setText(getString(R.string.mile_symbol));

                        mainActivity.getPreferencesEditor().putString("convertFrom", "mile")
                                .apply();
                        break;
                }

                mainActivity.calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        meter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mainActivity.bounceIn(meter, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        meter.startAnimation(mainActivity.getBounceOut());

                        mainActivity.selectButton(meter, options, R.drawable.option_clicked);
                        inputSymbol.setText(getString(R.string.meter_symbol));

                        mainActivity.getPreferencesEditor().putString("convertFrom", "meter")
                                .apply();
                        break;
                }

                mainActivity.calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        decimeter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mainActivity.bounceIn(decimeter, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        decimeter.startAnimation(mainActivity.getBounceOut());

                        mainActivity.selectButton(decimeter, options, R.drawable.option_clicked);
                        inputSymbol.setText(getString(R.string.decimeter_symbol));

                        mainActivity.getPreferencesEditor().putString("convertFrom", "decimeter")
                                .apply();
                        break;
                }

                mainActivity.calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        centimeter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mainActivity.bounceIn(centimeter, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        centimeter.startAnimation(mainActivity.getBounceOut());

                        mainActivity.selectButton(centimeter, options, R.drawable.option_clicked);
                        inputSymbol.setText(getString(R.string.centimeter_symbol));

                        mainActivity.getPreferencesEditor().putString("convertFrom", "centimeter")
                                .apply();
                        break;
                }

                mainActivity.calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        millimeter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mainActivity.bounceIn(millimeter, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        millimeter.startAnimation(mainActivity.getBounceOut());

                        mainActivity.selectButton(millimeter, options, R.drawable.option_clicked);
                        inputSymbol.setText(getString(R.string.millimeter_symbol));

                        mainActivity.getPreferencesEditor().putString("convertFrom", "millimeter")
                                .apply();
                        break;
                }

                mainActivity.calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        micrometer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mainActivity.bounceIn(micrometer, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        micrometer.startAnimation(mainActivity.getBounceOut());

                        mainActivity.selectButton(micrometer, options, R.drawable.option_clicked);
                        inputSymbol.setText(getString(R.string.micrometer_symbol));

                        mainActivity.getPreferencesEditor().putString("convertFrom", "micrometer")
                                .apply();
                        break;
                }

                mainActivity.calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });
    }
}