package com.jeanbarrossilva.power;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.TimerTask;

import static com.jeanbarrossilva.power.MainActivity.DEFAULT_BOUNCE_IN_SETTING;

public class CalculatorFragment extends Fragment {
    View view;
    Context context;

    MainActivity mainActivity;

    EditText input;
    private String calc;

    HorizontalScrollView othersHorizontalScrollView;

    Button number;
    final int[] numbers = {
            R.id.zero, R.id.one, R.id.two, R.id.three, R.id.four, R.id.five, R.id.six, R.id.seven, R.id.eight, R.id.nine
    };
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

        mainActivity.getTimer().schedule(new TimerTask() {
            @Override
            public void run() {
                calc = input.getText().toString();
            }
        }, 0, 100);

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
        hiddenMode();

        return view;
    }

    private void hiddenMode() {
        // Checks if Hidden Mode is enabled.
        mainActivity.setIsHiddenModeEnabled(mainActivity.getPreferences().getBoolean("isHiddenModeEnabled", mainActivity.getIsHiddenModeEnabled()));

        if (mainActivity.getIsHiddenModeEnabled()) {
            mainActivity.setPin(mainActivity.getPreferences().getString("pin", null));

            Toast.makeText(context, getString(R.string.hidden_mode_set_up_success), Toast.LENGTH_LONG).show();
            mainActivity.setIsHiddenModeEnabled(true);

            System.out.println("Apparently, Hidden Mode was successfully set.");
        }
    }

    private boolean isInputLastNumber(String calc) {
        for (String number: numbersArray) {
            if (mainActivity.reformatCalc(calc).endsWith(number)) {
                return true;
            }
        }

        return false;
    }

    // For some reason, this method doesn't return the updated calc value. Results in unnecessary attribution repetition in CalculatorFragment and all the Fragments that extends it.
    String updatedCalcValue(final EditText input) {
        this.input = input;
        final String[] calc = new String[1];

        // Updates the value every 100 milliseconds.
        mainActivity.getTimer().schedule(new TimerTask() {
            @Override
            public void run() {
                calc[0] = input.getText().toString();
            }
        }, 100);

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

                        System.out.println("Number \"" + number.getText() + "\" added.");

                        break;
                }

                return true;
            }
        };

        for (int number: numbers) {
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
                                System.out.println("Decimal separator added.");
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

                        if (operator.getText().toString().equals(mainActivity.getPlus()) || operator.getText().toString().equals(mainActivity.getMinus())) {
                            if (!mainActivity.inputHasReachedCharLimit(input, calc)) {
                                if (!calc.equals(getString(R.string.error))) {
                                    if (!mainActivity.isInputLastOperator(calc)) {
                                        if (calc.isEmpty()) {
                                            input.append(operator.getText());
                                        } else {
                                            input.append(mainActivity.getSpace() + operator.getText() + mainActivity.getSpace());
                                        }
                                    }
                                } else {
                                    input.setText(mainActivity.getEmpty());

                                    operator = (Button) view;
                                    input.append(operator.getText());
                                }
                            }
                        } else {
                            if (!calc.isEmpty()) {
                                if (!mainActivity.inputHasReachedCharLimit(input, calc)) {
                                    if (!calc.equals(getString(R.string.error))) {
                                        // if (!mainActivity.isInputLastOperator(calc)) {
                                            input.append(mainActivity.getSpace() + operator.getText() + mainActivity.getSpace());
                                        // }
                                    } else {
                                        input.setText(mainActivity.getEmpty());

                                        operator = (Button) view;
                                        input.append(operator.getText() + mainActivity.getSpace());
                                    }
                                }
                            }
                        }

                        System.out.println("Operator \"" + operator.getText() + "\" added.");

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
                            System.out.println("Parenthesis added.");
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
                            // When calc ends with " " (getSpace()), it means that it ends with an operator; the operator is shown as "getSpace() + operator + getSpace()". e. g., " × " and " + ". So, for example, if the input is "2 + 2", while deleting, the input would be equal to "2 + ", "2 +", "2 " and "2", respectively; with this method, the result would be "2 +" and "2", respectively.
                            if (calc.endsWith(mainActivity.getSpace())) {
                                input.setText(calc.substring(0, calc.length() - 2));

                                for (String number: numbersArray) {
                                    if (calc.substring(0, calc.length() - 1).endsWith(number)) {
                                        input.setText(calc.substring(0, calc.length() - 1));
                                    }
                                }
                            } else {
                                input.setText(calc.substring(0, calc.length() - 1));
                            }
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
                        mainActivity.bounceIn(view, DEFAULT_BOUNCE_IN_SETTING);
                        equal.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

                        break;
                    case MotionEvent.ACTION_UP:
                        equal.startAnimation(mainActivity.getBounceOut());

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

                            mainActivity.inputFormat(input, calc);
                        }

                        break;
                }

                return true;
            }
        });
    }
}