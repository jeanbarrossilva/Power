package com.jeanbarrossilva.power;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {
    String appName;
    String versionName;

    SharedPreferences preferences;
    boolean isNightEnabled;

    DecimalFormat format;

    String className;
    String unit;

    Timer timer;

    String empty;
    String space;

    String[] numbersArray = {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
    };

    String dot;
    String comma;
    String[] decimalSeparators;

    String plus;
    String minus;
    String times;
    String division;

    String asterisk;
    String slash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appName = getString(R.string.app_name);
        versionName = BuildConfig.VERSION_NAME;

        preferences = getSharedPreferences("com.jeanbarrossilva.power", Context.MODE_PRIVATE);
        isNightEnabled = preferences.getBoolean("isNightEnabled", false);

        format = new DecimalFormat("#.##");

        className = getLocalClassName();
        unit = null;

        timer = new Timer();

        empty = "";
        space = " ";

        dot = ".";
        comma = ",";
        decimalSeparators = new String[] {
                dot, comma
        };

        plus = getString(R.string.plus);
        minus = getString(R.string.minus);
        times = getString(R.string.times);
        division = getString(R.string.division);

        asterisk = "*";
        slash = "/";
    }

    String getAppName() {
        return appName;
    }

    String getVersionName() {
        return versionName;
    }

    SharedPreferences getPreferences() {
        return preferences;
    }

    boolean getIsNightEnabled() {
        return isNightEnabled;
    }

    String getClassName() {
        return className;
    }

    String getUnit() {
        return unit;
    }

    Timer getTimer() {
        return timer;
    }

    String getEmpty() {
        return empty;
    }

    String getSpace() {
        return space;
    }

    String getComma() {
        return comma;
    }

    String getPlus() {
        return plus;
    }

    String getMinus() {
        return minus;
    }

    String reformatCalc(String calc) {
        if (calc.contains(plus) || calc.contains(minus) || calc.contains(times) || calc.contains(division)) {
            if (calc.contains(plus)) {
                // Replaces " + " by "+".
                calc = calc.replace(space + plus + space, plus);
            }

            if (calc.contains(minus)) {
                // Replaces " - " by "-".
                calc = calc.replace(space + minus + space, minus);
            }

            if (calc.contains(times)) {
                // Replaces " × " by "×".
                calc = calc.replace(space + times + space, "*");
            }

            if (calc.contains(division)) {
                // Replaces " ÷ " by "÷".
                calc = calc.replace(space + division + space, "/");
            }

            System.out.println("Transformed calc: " + calc);
        }

        return calc;
    }

    boolean isInputLastNumber(String calc) {
        for (String number: numbersArray) {
            if (reformatCalc(calc).endsWith(number)) {
                return true;
            }
        }

        return false;
    }

    boolean isInputLastDecimalSeparator(String calc) {
        for (String decimalSeparator: decimalSeparators) {
            if (reformatCalc(calc).endsWith(decimalSeparator)) {
                return true;
            }
        }

        return false;
    }

    boolean isInputLastOperator(String calc) {
        String[] operators = {
                plus, minus, asterisk, slash
        };

        for (String operator: operators) {
            if (reformatCalc(calc).endsWith(operator)) {
                return true;
            }
        }

        return false;
    }

    void inputFormat(EditText input, String calc) {
        if (!(isInputLastOperator(calc) && isInputLastDecimalSeparator(calc))) {
            try {
                Expression expression = new ExpressionBuilder(reformatCalc(calc)).build();
                String result = String.valueOf(expression.evaluate());

                if (result.contains("E")) {
                    result = result.replace("E", "e");
                }

                if (result.endsWith(".0")) {
                    result = result.replace(".0", empty);
                }

                if (result.contains("Infinity")) {
                    result = result.replace("Infinity", getString(R.string.infinity));
                }

                if (result.length() > 16) {
                    result = result.substring(17, result.length() - 1);
                }

                input.setText(result);
                System.out.println("Calc result: " + result);
            } catch (Exception exception) {
                input.setText(getString(R.string.error));
            }
        } else {
            input.append("0");
        }
    }

    void inputFormat(EditText input, TextView conversionResult) {
        if (conversionResult.getText().toString().contains(".")) {
            conversionResult.setText(String.valueOf(format.format(Double.parseDouble(conversionResult.getText().toString()))));
        }

        if (conversionResult.getText().toString().endsWith(".0")) {
            conversionResult.setText(input.getText().toString().replace(".0", ""));
        }

        if (conversionResult.getText().toString().endsWith("E")) {
            conversionResult.setText(input.getText().toString().replace("E", "e"));
        }
    }

    @SuppressLint("ClickableViewAccessibility")
        // The units Button[] must contain all units available, including the selected one.
    void selectUnit(Context context, final Button selectedUnit, final Button[] units) {
        int selectedUnitId = selectedUnit.getId();

        selectedUnit.setTextColor(isNightEnabled ? Color.BLACK : Color.WHITE);
        selectedUnit.setBackgroundResource(R.drawable.option_clicked);

        System.out.println("Selected 'convertFrom' unit: " + selectedUnit.getText());

        for (Button unit: units) {
            if (unit.getId() != selectedUnitId) try {
                unit.setTextColor(isNightEnabled ? Color.WHITE : Color.BLACK);
                unit.setBackgroundResource(R.drawable.option);
            } catch (NullPointerException nullPointerException) {
                Toast.makeText(context, getString(R.string.an_error_occurred), Toast.LENGTH_LONG).show();
                nullPointerException.printStackTrace();
            }
        }
    }

    String convertFrom() {
        if (preferences.getString("convertTo", null) != null) {
            switch (className) {
                case "LengthActivity":
                    switch (Objects.requireNonNull(preferences.getString("convertFrom", null))) {
                        case "lightYear":
                            unit = "lightYear";
                            break;
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
                        case "micrometer":
                            unit = "micrometer";
                            break;
                    }

                    break;
                case "TimeActivity":
                    switch (Objects.requireNonNull(preferences.getString("convertFrom", null))) {
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
                        case "millisecond":
                            unit = "millisecond";
                            break;
                    }

                    break;
                case "TemperatureActivity":
                    switch (Objects.requireNonNull(preferences.getString("convertFrom", null))) {
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

                    break;
            }
        }

        return unit;
    }

    String convertTo() {
        if (preferences.getString("convertTo", null) != null) {
            switch (className) {
                case "LengthActivity":
                    switch (Objects.requireNonNull(preferences.getString("convertTo", null))) {
                        case "lightYear":
                            unit = "lightYear";
                            break;
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
                        case "micrometer":
                            unit = "micrometer";
                            break;
                    }

                    break;
                case "TimeActivity":
                    switch (Objects.requireNonNull(preferences.getString("convertTo", null))) {
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
                        case "millisecond":
                            unit = "millisecond";
                            break;
                    }

                    break;
                case "TemperatureActivity":
                    switch (Objects.requireNonNull(preferences.getString("convertTo", null))) {
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

                    break;
            }
        }

        return unit;
    }

    void calc(EditText input, TextView conversionResult, TextView conversionSymbolResult) {
        if (!input.getText().toString().isEmpty()) try {
            switch (className) {
                case "LengthActivity":
                    switch (convertFrom()) {
                        case "lightYear":
                            switch (convertTo()) {
                                case "lightYear":
                                    conversionResult.setText(input.getText().toString());
                                    break;
                                case "kilometer":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 9461000000000.0));
                                    break;
                                case "hectometer":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 94610000000000.0));
                                    break;
                                case "decameter":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 946100000000000.0));
                                    break;
                                case "meter":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 9461000000000000.0));
                                    break;
                                case "decimeter":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 94607304725809376.0));
                                    break;
                                case "centimeter":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 946073047258093824.0));
                                    break;
                                case "millimeter":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 9460730472580937728.0));
                                    break;
                                case "micrometer":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * Math.pow(9.461, 21)));
                                    break;
                            }

                            break;
                        case "kilometer":
                            switch (convertTo()) {
                                case "lightYear":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 9461000000000.0));
                                    break;
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
                                case "micrometer":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * Math.pow(10, 9)));
                                    System.out.println("Math.pow(10, 9): " + Math.pow(10, 9));
                                    break;
                            }

                            break;
                        case "hectometer":
                            switch (convertTo()) {
                                case "lightYear":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 94610000000000.0));
                                    break;
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
                                case "micrometer":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * Math.pow(10, 8)));
                                    break;
                            }

                            break;
                        case "decameter":
                            switch (convertTo()) {
                                case "lightYear":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 946100000000000.0));
                                    break;
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
                                case "micrometer":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * Math.pow(10, 7)));
                                    break;
                            }
                        case "meter":
                            switch (convertTo()) {
                                case "lightYear":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 9461000000000000.0));
                                    break;
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
                                case "micrometer":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * Math.pow(10, 6)));
                                    break;
                            }

                            break;
                        case "decimeter":
                            switch (convertTo()) {
                                case "lightYear":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 94607304725809376.0));
                                    break;
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
                                case "micrometer":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * Math.pow(10, 5)));
                                    break;
                            }

                            break;
                        case "centimeter":
                            switch (convertTo()) {
                                case "lightYear":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 946073047258093824.0));
                                    break;
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
                                case "micrometer":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * Math.pow(10, 4)));
                                    break;
                            }

                            break;
                        case "millimeter":
                            switch (convertTo()) {
                                case "lightYear":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / (1.057 * Math.pow(10, -19))));
                                    break;
                                case "kilometer":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / Math.pow(10, 6)));
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
                                case "micrometer":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * Math.pow(10, 3)));
                                    break;
                            }
                        case "micrometer":
                            switch (convertTo()) {
                                case "lightYear":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / (1.057 * Math.pow(10, -22))));
                                    break;
                                case "kilometer":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / (1.057 * Math.pow(10, -9))));
                                    break;
                                case "hectometer":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / (1.057 * Math.pow(10, -8))));
                                    break;
                                case "decameter":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / (1.057 * Math.pow(10, -7))));
                                    break;
                                case "meter":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / (1.057 * Math.pow(10, -6))));
                                    break;
                                case "decimeter":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / (1.057 * Math.pow(10, -5))));
                                    break;
                                case "centimeter":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / (1.057 * Math.pow(10, -4))));
                                    break;
                                case "millimeter":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / (1.057 * Math.pow(10, -3))));
                                    break;
                                case "micrometer":
                                    conversionResult.setText(input.getText().toString());
                                    break;
                            }
                    }

                    break;
                case "TimeActivity":
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
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * (31536 * Math.pow(10, 3))));
                                    break;
                                case "millisecond":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * (31536 * Math.pow(10, 6))));
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
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 730.001));
                                    break;
                                case "minute":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 43800.048));
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
                                case "month":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 30.417));
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
                                    conversionResult.setText(String.valueOf((Double.parseDouble(input.getText().toString())) / 60));
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

                    break;
                case "TemperatureActivity":
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

                    break;
            }

            inputFormat(input, conversionResult);
        } catch (Exception exception) {
            conversionResult.setText(getString(R.string.error));
            conversionSymbolResult.setVisibility(View.GONE);
        }
    }
}