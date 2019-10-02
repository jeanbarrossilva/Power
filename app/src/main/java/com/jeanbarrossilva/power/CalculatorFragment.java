package com.jeanbarrossilva.power;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

import static com.jeanbarrossilva.power.MainActivity.DEFAULT_BOUNCE_IN_SETTING;
import static com.jeanbarrossilva.power.MainActivity.LAST_PARENTHESIS_LEFT;
import static com.jeanbarrossilva.power.MainActivity.LAST_PARENTHESIS_RIGHT;

public class CalculatorFragment extends Fragment {
    View view;
    Context context;

    MainActivity mainActivity;

    EditText input;
    String calc;

    HorizontalScrollView othersHorizontalScrollView;

    Button number;
    private final String[] numbersArray = {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
    };

    private Button parenthesis;
    private final int[] parenthesisArray = {
            R.id.left_parenthesis, R.id.right_parenthesis
    };

    private Button operator;
    private final int[] operators = {
            R.id.plus, R.id.minus, R.id.times, R.id.division
    };

    Button decimalSeparator;
    ImageButton calculatorMode;
    ImageButton delete;

    private Button equal;

    public CalculatorFragment() {

    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calculator, container, false);
        context = Objects.requireNonNull(getContext());

        mainActivity = (MainActivity) getActivity();

        input = view.findViewById(R.id.input);

        // Disables the keyboard, since the app already has predefined buttons.
        input.setFocusable(false);

        calc = updatedCalcValue(input);

        othersHorizontalScrollView = view.findViewById(R.id.others_horizontal_scroll_view);
        othersHorizontalScrollView.setHorizontalScrollBarEnabled(false);

        decimalSeparator = view.findViewById(R.id.decimal_separator);
        calculatorMode = view.findViewById(R.id.calculator_mode);
        delete = view.findViewById(R.id.delete);

        equal = view.findViewById(R.id.equal);

        if (Build.VERSION.SDK_INT >= 21) {
            if (mainActivity.getIsNightEnabled()) {
                mainActivity.getWindow().setNavigationBarColor(Color.BLACK);
            } else {
                mainActivity.getWindow().setNavigationBarColor(Color.WHITE);
            }
        }

        mainActivity.calculatorMode(context, calculatorMode);

        inputNumber(input, calc);
        inputDecimalSeparator(input, calc, decimalSeparator);
        inputOperator(input, calc);
        inputParenthesis(input, calc);

        delete(input, delete);
        calc();

        return view;
    }

    /* private void hiddenMode() {
        // Checks if Hidden Mode is enabled.
        mainActivity.setIsHiddenModeEnabled(mainActivity.getPreferences().getBoolean("isHiddenModeEnabled", mainActivity.getIsHiddenModeEnabled()));

        if (mainActivity.getIsHiddenModeEnabled()) {
            mainActivity.setPin(mainActivity.getPreferences().getString("pin", null));

            Toast.makeText(context, getString(R.string.hidden_mode_set_up_success), Toast.LENGTH_LONG).show();
            mainActivity.setIsHiddenModeEnabled(true);

            System.out.println("Apparently, Hidden Mode was successfully set.");
        }
    } */

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    private boolean isInputLastNumber(String calc) {
        for (String number: numbersArray) {
            return mainActivity.reformatCalc(calc).endsWith(number);
        }

        return false;
    }

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    private boolean isInputLastDecimalSeparator(String calc) {
        for (String decimalSeparator: mainActivity.getDecimalSeparators()) {
            return mainActivity.reformatCalc(calc).endsWith(decimalSeparator);
        }

        return false;
    }

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    private boolean isInputLastOperator(String calc) {
        String[] operators = {
                mainActivity.getPlus(), mainActivity.getMinus(), mainActivity.getTimes(), mainActivity.getDivision()
        };

        for (String operator: operators) {
            return calc.substring(0, calc.length() - 2).endsWith(operator);
        }

        return false;
    }

    @SuppressWarnings("SameParameterValue")
    private boolean isInputLastParenthesis(String calc, int parenthesis) {
        switch (parenthesis) {
            case LAST_PARENTHESIS_LEFT:
                return calc.endsWith(mainActivity.getLeftParenthesis());
            case LAST_PARENTHESIS_RIGHT:
                return calc.endsWith(mainActivity.getRightParenthesis());
        }

        return false;
    }

    private void inputFormat(EditText input, String calc, String result) {
        if (!(isInputLastOperator(calc) && isInputLastDecimalSeparator(calc))) try {
            if (result.contains("E")) {
                result = result.replace("E", "e");
            }

            if (result.endsWith(".0")) {
                result = result.replace(".0", mainActivity.getEmpty());
            }

            if (result.contains("Infinity")) {
                result = result.replace("Infinity", mainActivity.getInfinity());
            }

            if (result.length() > 16) {
                result = result.substring(17, result.length() - 1);
            }

            input.setText(result);
            System.out.println("Calc result: " + result);
        } catch (Exception exception) {
            input.setText(getString(R.string.error));
        } else {
            input.setText("0");
        }
    }

    String updatedCalcValue(final EditText input) {
        final String[] calc = new String[1];

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calc[0] = String.valueOf(s);
                System.out.println("Updated \"calc\" value: " + (Arrays.toString(calc).isEmpty() ? "[empty]" : s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return Arrays.toString(calc);
    }

    /**
     * Inputs a number in a text field.
     *
     * @param input Represents the text field itself.
     * @param calc String from the text field, regularly referred as 'input.getText().toString()'.
     */
    @SuppressLint("ClickableViewAccessibility")
    private void inputNumber(final EditText input, final String calc) {
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                number = (Button) view;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mainActivity.bounceIn(view, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        number.startAnimation(mainActivity.getBounceOut());

                        if (!calc.equals(getString(R.string.error))) {
                            if (!mainActivity.inputHasReachedCharLimit(input, calc)) {
                                input.append(number.getText());
                            }
                        } else {
                            input.setText(mainActivity.getEmpty());

                            number = (Button) view;
                            input.append(number.getText());
                        }

                        break;
                }

                return true;
            }
        };

        for (int number: mainActivity.numbers) {
            view.findViewById(number).setOnTouchListener(onTouchListener);
        }
    }

    /**
     * Inputs a decimal separator in a text field.
     *
     * @param input Represents the text field itself.
     * @param calc String from the text field, regularly referred as 'input.getText().toString()'.
     */
    @SuppressLint("ClickableViewAccessibility")
    void inputDecimalSeparator(final EditText input, final String calc, final Button decimalSeparator) {
        decimalSeparator.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mainActivity.bounceIn(view, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        decimalSeparator.startAnimation(mainActivity.getBounceOut());

                        if (!mainActivity.inputHasReachedCharLimit(input, calc)) {
                            if (isInputLastNumber(calc)) {
                                input.append(decimalSeparator.getText());
                            }
                        }

                        break;
                }

                return true;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void inputOperator(final EditText input, final String calc) {
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                operator = (Button) view;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mainActivity.bounceIn(view, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        operator.startAnimation(mainActivity.getBounceOut());

                        if (operator.getId() == R.id.plus || operator.getId() == R.id.minus) {
                            if (!calc.isEmpty()) {
                                input.append(mainActivity.getSpace() + operator.getText() + mainActivity.getSpace());
                            } else if ((isInputLastNumber(calc) || (isInputLastParenthesis(calc, LAST_PARENTHESIS_LEFT) || isInputLastParenthesis(calc, LAST_PARENTHESIS_LEFT))) & !isInputLastOperator(calc)) {
                                input.append(operator.getText() + mainActivity.getSpace());
                            }
                        } else if (operator.getId() == R.id.times || operator.getId() == R.id.division) {
                            if (!(calc.isEmpty() && isInputLastDecimalSeparator(calc) && isInputLastParenthesis(calc, LAST_PARENTHESIS_LEFT))) {
                                input.append(mainActivity.getSpace() + operator.getText() + mainActivity.getSpace());
                            }
                        }

                        break;
                }

                return true;
            }
        };

        for (int operator: operators) {
            view.findViewById(operator).setOnTouchListener(onTouchListener);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void inputParenthesis(final EditText input, final String calc) {
        View.OnTouchListener listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                parenthesis = (Button) view;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mainActivity.bounceIn(view, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        parenthesis.startAnimation(mainActivity.getBounceOut());

                        if (!mainActivity.inputHasReachedCharLimit(input, calc)) {
                            input.append(parenthesis.getText());
                        }

                        break;
                }

                return true;
            }
        };

        for (int parenthesis: parenthesisArray) {
            view.findViewById(parenthesis).setOnTouchListener(listener);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    void delete(final EditText input, final ImageButton delete) {
        delete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                String calc = input.getText().toString();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mainActivity.bounceIn(view, "0.5, 1");
                        break;
                    case MotionEvent.ACTION_UP:
                        delete.startAnimation(mainActivity.getBounceOut());
                        delete.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

                        if (!calc.isEmpty()) {
                            input.setText(calc.endsWith(mainActivity.getSpace()) ? calc.substring(0, calc.length() - 2) : calc.substring(0, calc.length() - 1));
                        }

                        break;
                }

                return true;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void calc() {
        equal.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mainActivity.bounceIn(view, "0.5, 1");
                        equal.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

                        break;
                    case MotionEvent.ACTION_UP:
                        equal.startAnimation(mainActivity.getBounceOut());

                        Expression expression;
                        String result;

                        if (calc != null) {
                            if (!calc.isEmpty()) {
                                if (mainActivity.getIsHiddenModeEnabled()) {
                                    if (mainActivity.getPin() != null) {
                                        if (calc.equals(mainActivity.getPin())) {
                                            input.setText(mainActivity.getEmpty());
                                            startActivity(new Intent(context, HiddenMode.class));
                                        }
                                    } else {
                                        System.out.println("'pin' is null.");
                                    }
                                }

                                try {
                                    calc = input.getText().toString();

                                    expression = new ExpressionBuilder(mainActivity.reformatCalc(calc)).build();
                                    result = String.valueOf(expression.evaluate());

                                    inputFormat(input, calc, result);

                                    calc = updatedCalcValue(input);
                                } catch (Exception exception) {
                                    // If the calc is unfinished, "String.valueOf(expression.evaluate())" throws an IllegalArgumentException.
                                    input.append(mainActivity.getSpace() + "0");

                                    equal.performClick();
                                }
                            }
                        }

                        break;
                }

                return true;
            }
        });
    }
}