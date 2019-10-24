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

public class TemperatureFragment extends CalculatorFragment {
    private TextView inputSymbol;

    private TextView conversionResult;
    private TextView conversionSymbolResult;

    private Button unit;
    private Button[] options =  new Button[5];

    private Button celsius;
    private Button fahrenheit;
    private Button kelvin;
    private Button rankine;
    private Button reaumur;

    public TemperatureFragment() {

    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calculator_temperature, container, false);
        mainActivity = (MainActivity) getActivity();

        input = view.findViewById(R.id.input);
        inputSymbol = view.findViewById(R.id.input_symbol);

        input.setFocusable(false);

        unit = view.findViewById(R.id.unit);

        conversionResult = view.findViewById(R.id.option_conversion_number_result);
        conversionSymbolResult = view.findViewById(R.id.option_conversion_symbol_result);

        othersHorizontalScrollView = view.findViewById(R.id.others_horizontal_scroll_view);
        othersHorizontalScrollView.setHorizontalScrollBarEnabled(false);

        celsius = view.findViewById(R.id.celsius);
        fahrenheit = view.findViewById(R.id.fahrenheit);
        kelvin = view.findViewById(R.id.kelvin);
        rankine = view.findViewById(R.id.rankine);
        reaumur = view.findViewById(R.id.reaumur);

        options[0] = celsius;
        options[1] = fahrenheit;
        options[2] = kelvin;
        options[3] = rankine;
        options[4] = reaumur;

        calculatorMode = view.findViewById(R.id.calculator_mode);
        keypadButtons[10] = view.findViewById(R.id.decimal_separator);
        delete = view.findViewById(R.id.delete);

        units();
        inputOption();

        try {
            // Default configuration (Celsius to Fahrenheit).
            inputSymbol.setText(getString(R.string.celsius_symbol));
            mainActivity.selectButton(celsius, options, R.drawable.option_clicked);
            mainActivity.getPreferences().edit().putString("convertFrom", "celsius")
                    .apply();

            unit.setText(getString(R.string.fahrenheit));
            conversionSymbolResult.setText(getString(R.string.fahrenheit_symbol));
            mainActivity.getPreferences().edit().putString("convertTo", "fahrenheit")
                    .apply();
        } catch (Exception exception) {
            mainActivity.getAlertError().show();
        }

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
                        mainActivity.bounceIn(unit, 0.5, 5);
                        break;
                    case MotionEvent.ACTION_UP:
                        final PopupMenu units;
                        MenuInflater inflater;

                        units = new PopupMenu(context, unit);
                        inflater = units.getMenuInflater();

                        inflater.inflate(R.menu.units_temperature, units.getMenu());
                        units.show();

                        units.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if (item.getTitle().equals(getString(R.string.celsius))) {
                                    mainActivity.getPreferences().edit().putString("convertTo", "celsius")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.celsius_symbol));
                                    unit.setText(getString(R.string.celsius));
                                } else if (item.getTitle().equals(getString(R.string.fahrenheit))) {
                                    mainActivity.getPreferences().edit().putString("convertTo", "fahrenheit")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.fahrenheit_symbol));
                                    unit.setText(getString(R.string.fahrenheit));
                                } else if (item.getTitle().equals(getString(R.string.kelvin))) {
                                    mainActivity.getPreferences().edit().putString("convertTo", "kelvin")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.kelvin_symbol));
                                    unit.setText(getString(R.string.kelvin));
                                } else if (item.getTitle().equals(getString(R.string.rankine))) {
                                    mainActivity.getPreferences().edit().putString("convertTo", "rankine")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.rankine_symbol));
                                    unit.setText(getString(R.string.rankine));
                                } else if (item.getTitle().equals(getString(R.string.reaumur))) {
                                    mainActivity.getPreferences().edit().putString("convertTo", "reaumur")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.reaumur_symbol));
                                    unit.setText(getString(R.string.reaumur));
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
        celsius.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mainActivity.bounceIn(celsius, 0.5, 5);
                        break;
                    case MotionEvent.ACTION_UP:
                        celsius.startAnimation(mainActivity.getBounceOut());

                        mainActivity.selectButton(celsius, options, R.drawable.option_clicked);
                        inputSymbol.setText(getString(R.string.celsius_symbol));

                        mainActivity.getPreferences().edit().putString("convertFrom", "celsius")
                                .apply();
                        break;
                }

                mainActivity.calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        fahrenheit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mainActivity.bounceIn(fahrenheit, 0.5, 5);
                        break;
                    case MotionEvent.ACTION_UP:
                        fahrenheit.startAnimation(mainActivity.getBounceOut());

                        mainActivity.selectButton(fahrenheit, options, R.drawable.option_clicked);
                        inputSymbol.setText(getString(R.string.fahrenheit_symbol));

                        mainActivity.getPreferences().edit().putString("convertFrom", "fahrenheit")
                                .apply();
                        break;
                }

                mainActivity.calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        kelvin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mainActivity.bounceIn(kelvin, 0.5, 5);
                        break;
                    case MotionEvent.ACTION_UP:
                        kelvin.startAnimation(mainActivity.getBounceOut());

                        mainActivity.selectButton(kelvin, options, R.drawable.option_clicked);
                        inputSymbol.setText(getString(R.string.kelvin_symbol));

                        mainActivity.getPreferences().edit().putString("convertFrom", "kelvin")
                                .apply();
                        break;
                }

                mainActivity.calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        rankine.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mainActivity.bounceIn(rankine, 0.5, 5);
                        break;
                    case MotionEvent.ACTION_UP:
                        rankine.startAnimation(mainActivity.getBounceOut());

                        mainActivity.selectButton(rankine, options, R.drawable.option_clicked);
                        inputSymbol.setText(getString(R.string.rankine_symbol));

                        mainActivity.getPreferences().edit().putString("convertFrom", "rankine")
                                .apply();
                        break;
                }

                mainActivity.calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        reaumur.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mainActivity.bounceIn(reaumur, 0.5, 5);
                        break;
                    case MotionEvent.ACTION_UP:
                        reaumur.startAnimation(mainActivity.getBounceOut());

                        mainActivity.selectButton(reaumur, options, R.drawable.option_clicked);
                        inputSymbol.setText(getString(R.string.reaumur_symbol));

                        mainActivity.getPreferences().edit().putString("convertFrom", "reaumur")
                                .apply();
                        break;
                }

                mainActivity.calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });
    }
}