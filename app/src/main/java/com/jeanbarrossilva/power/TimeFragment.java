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

public class TimeFragment extends CalculatorFragment {
    private TextView inputSymbol;

    private TextView conversionResult;
    private TextView conversionSymbolResult;

    private Button unit;
    private Button[] options =  new Button[7];

    private Button year;
    private Button month;
    private Button day;
    private Button hour;
    private Button minute;
    private Button second;
    private Button millisecond;

    public TimeFragment() {

    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_time, container, false);
        mainActivity = (MainActivity) getActivity();

        input = view.findViewById(R.id.input);
        inputSymbol = view.findViewById(R.id.input_symbol);

        unit = view.findViewById(R.id.unit);

        conversionResult = view.findViewById(R.id.option_conversion_number_result);
        conversionSymbolResult = view.findViewById(R.id.option_conversion_symbol_result);

        othersHorizontalScrollView = view.findViewById(R.id.others_horizontal_scroll_view);
        othersHorizontalScrollView.setHorizontalScrollBarEnabled(false);

        year = view.findViewById(R.id.year);
        month = view.findViewById(R.id.month);
        day = view.findViewById(R.id.day);
        hour = view.findViewById(R.id.hour);
        minute = view.findViewById(R.id.minute);
        second = view.findViewById(R.id.second);
        millisecond = view.findViewById(R.id.millisecond);

        options[0] = year;
        options[1] = month;
        options[2] = day;
        options[3] = hour;
        options[4] = minute;
        options[5] = second;
        options[6] = millisecond;

        keypadButtons[10] = view.findViewById(R.id.decimal_separator);
        calculatorMode = view.findViewById(R.id.calculator_mode);
        delete = view.findViewById(R.id.delete);

        units();
        inputOption();

        try {
            // Default configuration (hour to minute).
            inputSymbol.setText(getString(R.string.hour_symbol));
            mainActivity.selectUnit(context, hour, options);
            mainActivity.getPreferences().edit().putString("convertFrom", "hour")
                    .apply();

            unit.setText(getString(R.string.minute));
            conversionSymbolResult.setText(getString(R.string.minute_symbol));
            mainActivity.getPreferences().edit().putString("convertTo", "minute")
                    .apply();
        } catch (Exception exception) {
            mainActivity.getAlertError().show();
        }

        mainActivity.calculatorMode(context, calculatorMode);

        mainActivity.inputNumber(view, input, conversionResult, conversionSymbolResult, input.getText().toString());
        inputDecimalSeparator(input, keypadButtons[10]);
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

                        inflater.inflate(R.menu.units_time, units.getMenu());
                        units.show();

                        units.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if (item.getTitle().equals(getString(R.string.year))) {
                                    mainActivity.getPreferences().edit().putString("convertTo", "year")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.year_symbol));
                                    unit.setText(getString(R.string.year));
                                } else if (item.getTitle().equals(getString(R.string.month))) {
                                    mainActivity.getPreferences().edit().putString("convertTo", "month")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.month_symbol));
                                    unit.setText(getString(R.string.month));
                                } else if (item.getTitle().equals(getString(R.string.day))) {
                                    mainActivity.getPreferences().edit().putString("convertTo", "day")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.day_symbol));
                                    unit.setText(getString(R.string.day));
                                } else if (item.getTitle().equals(getString(R.string.hour))) {
                                    mainActivity.getPreferences().edit().putString("convertTo", "hour")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.hour_symbol));
                                    unit.setText(getString(R.string.hour));
                                } else if (item.getTitle().equals(getString(R.string.minute))) {
                                    mainActivity.getPreferences().edit().putString("convertTo", "minute")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.minute_symbol));
                                    unit.setText(getString(R.string.minute));
                                } else if (item.getTitle().equals(getString(R.string.second))) {
                                    mainActivity.getPreferences().edit().putString("convertTo", "second")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.second_symbol));
                                    unit.setText(getString(R.string.second));
                                } else if (item.getTitle().equals(getString(R.string.millisecond))) {
                                    mainActivity.getPreferences().edit().putString("convertTo", "millisecond")
                                            .apply();

                                    conversionSymbolResult.setText(getString(R.string.millisecond_symbol));
                                    unit.setText(getString(R.string.millisecond));
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
        year.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mainActivity.bounceIn(year, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        year.startAnimation(mainActivity.getBounceOut());

                        mainActivity.selectUnit(context, year, options);
                        inputSymbol.setText(getString(R.string.year_symbol));

                        mainActivity.getPreferences().edit().putString("convertFrom", "year")
                                .apply();
                        break;
                }

                mainActivity.calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        month.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mainActivity.bounceIn(month, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        month.startAnimation(mainActivity.getBounceOut());

                        mainActivity.selectUnit(context, month, options);
                        inputSymbol.setText(getString(R.string.month_symbol));

                        mainActivity.getPreferences().edit().putString("convertFrom", "month")
                                .apply();
                        break;
                }

                mainActivity.calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        day.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mainActivity.bounceIn(day, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        day.startAnimation(mainActivity.getBounceOut());

                        mainActivity.selectUnit(context, day, options);
                        inputSymbol.setText(getString(R.string.day_symbol));

                        mainActivity.getPreferences().edit().putString("convertFrom", "day")
                                .apply();
                        break;
                }

                mainActivity.calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        hour.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mainActivity.bounceIn(hour, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        hour.startAnimation(mainActivity.getBounceOut());

                        mainActivity.selectUnit(context, hour, options);
                        inputSymbol.setText(getString(R.string.hour_symbol));

                        mainActivity.getPreferences().edit().putString("convertFrom", "hour")
                                .apply();
                        break;
                }

                mainActivity.calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        minute.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mainActivity.bounceIn(minute, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        minute.startAnimation(mainActivity.getBounceOut());

                        mainActivity.selectUnit(context, minute, options);
                        inputSymbol.setText(getString(R.string.minute_symbol));

                        mainActivity.getPreferences().edit().putString("convertFrom", "minute")
                                .apply();
                        break;
                }

                mainActivity.calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        second.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mainActivity.bounceIn(second, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        second.startAnimation(mainActivity.getBounceOut());

                        mainActivity.selectUnit(context, second, options);
                        inputSymbol.setText(getString(R.string.second_symbol));

                        mainActivity.getPreferences().edit().putString("convertFrom", "second")
                                .apply();
                        break;
                }

                mainActivity.calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });

        millisecond.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mainActivity.bounceIn(millisecond, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        millisecond.startAnimation(mainActivity.getBounceOut());

                        mainActivity.selectUnit(context, millisecond, options);
                        inputSymbol.setText(getString(R.string.millisecond_symbol));

                        mainActivity.getPreferences().edit().putString("convertFrom", "millisecond")
                                .apply();
                        break;
                }

                mainActivity.calc(input, conversionResult, conversionSymbolResult);

                return true;
            }
        });
    }
}