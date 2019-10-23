package com.jeanbarrossilva.power;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.jeanbarrossilva.power.MainActivity.DEFAULT_BOUNCE_IN_SETTING;
import static com.jeanbarrossilva.power.MainActivity.LAST_PARENTHESIS_LEFT;
import static com.jeanbarrossilva.power.MainActivity.LAST_PARENTHESIS_RIGHT;

public class CalculatorFragment extends Fragment {
    View view;
    Context context;

    MainActivity mainActivity;

    private Animation keypadIn;
    private Animation keypadOut;

    private boolean isScientific;

    private View keypad;
    Button[] keypadButtons = new Button[11];

    EditText input;

    HorizontalScrollView othersHorizontalScrollView;

    private Button number;

    private Button[] parenthesis;

    private Button operator;
    private final int[] operators = {
            R.id.plus, R.id.minus, R.id.times, R.id.division
    };

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

        keypadIn = AnimationUtils.loadAnimation(context, R.anim.keypad_in);
        keypadOut = AnimationUtils.loadAnimation(context, R.anim.keypad_out);

        keypad = view.findViewById(R.id.keypad_delete).findViewById(R.id.keypad);

        keypadButtons[0] = keypad.findViewById(R.id.zero);
        keypadButtons[1] = keypad.findViewById(R.id.one);
        keypadButtons[2] = keypad.findViewById(R.id.two);
        keypadButtons[3] = keypad.findViewById(R.id.three);
        keypadButtons[4] = keypad.findViewById(R.id.four);
        keypadButtons[5] = keypad.findViewById(R.id.five);
        keypadButtons[6] = keypad.findViewById(R.id.six);
        keypadButtons[7] = keypad.findViewById(R.id.seven);
        keypadButtons[8] = keypad.findViewById(R.id.eight);
        keypadButtons[9] = keypad.findViewById(R.id.nine);
        keypadButtons[10] = keypad.findViewById(R.id.decimal_separator);

        input = view.findViewById(R.id.input);

        // Disables the keyboard, since the app already has predefined buttons.
        input.setFocusable(false);

        parenthesis = new Button[2];

        parenthesis[0] = view.findViewById(R.id.left_parenthesis);
        parenthesis[1] = view.findViewById(R.id.right_parenthesis);

        othersHorizontalScrollView = view.findViewById(R.id.others_horizontal_scroll_view);
        othersHorizontalScrollView.setHorizontalScrollBarEnabled(false);

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

        inputNumber(input);
        inputDecimalSeparator(input);
        inputOperator(input);
        inputParenthesis(input);

        delete(input, delete);
        calc(false);

        return view;
    }

    private boolean isInputLastNumber(EditText input) {
        if (input.getText().toString().length() > 0)
            return Character.isDigit(input.getText().toString().charAt(input.getText().toString().length() - 1));

        return false;
    }

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    private boolean isInputLastDecimalSeparator(EditText input) {
        for (String decimalSeparator: mainActivity.getDecimalSeparators()) {
            return input.getText().toString().endsWith(decimalSeparator);
        }

        return false;
    }

    private boolean isInputLastOperator(EditText input) {
        String[] operators = {
                mainActivity.getPlus(), mainActivity.getMinus(), mainActivity.getAsterisk(), mainActivity.getSlash()
        };

        for (String operator: operators) {
            if (input.getText().toString().length() > 0) {
                StringBuilder buffer = new StringBuilder(input.getText().toString());
                return buffer.charAt(input.length() - 1) == operator.charAt(0);
            }
        }

        return false;
    }

    @SuppressWarnings("SameParameterValue")
    private boolean isInputLastParenthesis(EditText input, int parenthesis) {
        switch (parenthesis) {
            case LAST_PARENTHESIS_LEFT:
                return input.getText().toString().endsWith(mainActivity.getLeftParenthesis());
            case LAST_PARENTHESIS_RIGHT:
                return input.getText().toString().endsWith(mainActivity.getRightParenthesis());
        }

        return false;
    }

    private void inputFormat(EditText input, String result) {
        if (!(isInputLastOperator(input) && isInputLastDecimalSeparator(input))) try {
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

    /**
     * Inputs a number in a text field.
     *
     * @param input Represents the text field itself.
     */
    @SuppressLint("ClickableViewAccessibility")
    private void inputNumber(final EditText input) {
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

                        if (!input.getText().toString().equals(getString(R.string.error))) {
                            input.append(number.getText());
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
     */
    @SuppressLint("ClickableViewAccessibility")
    void inputDecimalSeparator(final EditText input) {
        try {
            keypadButtons[10].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mainActivity.bounceIn(view, DEFAULT_BOUNCE_IN_SETTING);
                            break;
                        case MotionEvent.ACTION_UP:
                            view.startAnimation(mainActivity.getBounceOut());

                            if (isInputLastNumber(input))
                                input.append(mainActivity.getDot());

                            break;
                    }

                    return true;
                }
            });
        } catch (NullPointerException nullPointerException) {
            nullPointerException.printStackTrace();
        }
    }

    /**
     * Inputs an operator in a text field.
     *
     * @param input Represents the text field itself.
     */
    @SuppressLint("ClickableViewAccessibility")
    private void inputOperator(final EditText input) {
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

                        for (String power: mainActivity.getPowers()) {
                            if (!isInputLastOperator(input)) {
                                if ((operator.getId() == R.id.plus || operator.getId() == R.id.minus) || input.getText().toString().endsWith(power)) {
                                    if (!input.getText().toString().isEmpty())
                                        input.append(mainActivity.getSpace() + operator.getText() + mainActivity.getSpace());
                                    else if ((isInputLastNumber(input) || (isInputLastParenthesis(input, LAST_PARENTHESIS_LEFT) || isInputLastParenthesis(input, LAST_PARENTHESIS_LEFT))) & !isInputLastOperator(input))
                                        input.append(operator.getText() + mainActivity.getSpace());
                                    else
                                        input.append(mainActivity.getSpace() + operator.getText() + mainActivity.getSpace());
                                } else if (operator.getId() == R.id.times || operator.getId() == R.id.division) {
                                    if (!(input.getText().toString().isEmpty() && isInputLastDecimalSeparator(input) && isInputLastParenthesis(input, LAST_PARENTHESIS_LEFT)))
                                        input.append(mainActivity.getSpace() + operator.getText() + mainActivity.getSpace());
                                }
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

    /**
     * Inputs a parenthesis in a text field.
     *
     * @param input Represents the text field itself.
     */
    @SuppressLint("ClickableViewAccessibility")
    private void inputParenthesis(final EditText input) {
        View.OnTouchListener listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
            Button parenthesis = (Button) view;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mainActivity.bounceIn(view, DEFAULT_BOUNCE_IN_SETTING);
                    break;
                case MotionEvent.ACTION_UP:
                    parenthesis.startAnimation(mainActivity.getBounceOut());

                    for (Button parenthesisButton: CalculatorFragment.this.parenthesis) {
                        switch (parenthesisButton.getId()) {
                            case R.id.left_parenthesis:
                                if (!isInputLastDecimalSeparator(input))
                                    input.append(mainActivity.getLeftParenthesis());
                                else if (isInputLastNumber(input))
                                    input.append(mainActivity.getSpace() + mainActivity.getTimes() + mainActivity.getSpace() + parenthesis);

                                break;
                            case R.id.right_parenthesis:
                                if (isInputLastNumber(input))
                                    input.append(mainActivity.getRightParenthesis());

                                break;
                        }
                    }

                    break;
            }

            return true;
            }
        };

        for (Button parenthesis: parenthesis) {
            view.findViewById(parenthesis.getId()).setOnTouchListener(listener);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void delete(final EditText input, final ImageButton delete) {
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

                        if (!calc.isEmpty()) try {
                            input.setText(calc.endsWith(mainActivity.getSpace()) ? calc.substring(0, calc.length() - 2) : calc.substring(0, calc.length() - 1));
                        } catch (Exception exception) {
                            input.setText(mainActivity.getEmpty());
                        }

                        break;
                }

                return true;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void calc(final boolean cancel) {
        equal.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, final MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        equal.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (!cancel) {
                            Expression expression;
                            String result = null;

                            if (!input.getText().toString().isEmpty()) {
                                expression = new ExpressionBuilder(mainActivity.reformatCalc(input.getText().toString())).build();

                                try {
                                    result = String.valueOf(expression.evaluate());
                                    input.setText(result);
                                } catch (Exception exception) {
                                    // If the calc is unfinished, "String.valueOf(expression.evaluate())" throws an IllegalArgumentException.
                                    if (!input.getText().toString().isEmpty()) {
                                        input.append(input.getText().toString().endsWith(mainActivity.getSpace()) ? getString(R.string.zero) : mainActivity.getSpace() + getString(R.string.zero));
                                    } else {
                                        input.append(getString(R.string.zero));
                                    }

                                    equal.performClick();
                                }

                                final String previousCalc = input.getText().toString();

                                inputFormat(input, input.getText().toString());

                                if (result != null) try {
                                    new HistoryFragment().add(new Calc(previousCalc, result));
                                } catch (NullPointerException exception) {
                                    System.out.println("Couldn't add expression to History.");
                                }
                            }
                        }

                        System.out.println("\"isScientific\":" + mainActivity.getSpace() + isScientific);

                        break;
                }

                return false;
            }
        });

        equal.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                isScientific = !isScientific;
                scientific();

                return true;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void scientific() {
        calc(true);

        keypad.startAnimation(isScientific ? keypadOut : keypadIn);

        keypadButtons[0].setText(isScientific ? mainActivity.getEmpty() : getString(R.string.zero));
        keypadButtons[1].setText(isScientific ? mainActivity.getEmpty() : getString(R.string.one));
        keypadButtons[2].setText(isScientific ? mainActivity.getEmpty() : getString(R.string.two));
        keypadButtons[3].setText(isScientific ? mainActivity.getEmpty() : getString(R.string.three));
        keypadButtons[4].setText(isScientific ? mainActivity.getEmpty() : getString(R.string.four));
        keypadButtons[5].setText(isScientific ? mainActivity.getEmpty() : getString(R.string.five));
        keypadButtons[6].setText(isScientific ? mainActivity.getEmpty() : getString(R.string.six));
        keypadButtons[7].setText(isScientific ? mainActivity.getEmpty() : getString(R.string.seven));
        keypadButtons[8].setText(isScientific ? mainActivity.getEmpty() : getString(R.string.eight));
        keypadButtons[9].setText(isScientific ? mainActivity.getEmpty() : getString(R.string.nine));
        keypadButtons[10].setText(isScientific ? mainActivity.getEmpty() : mainActivity.getDot());

        for (final Button keypadButton : keypadButtons) {
            for (int index = 0; index < keypadButtons.length; index++) {
                if (isScientific) {
                    if (keypadButtons[index].getId() == keypadButton.getId()) {
                        keypadButton.setTag("placeholder" + index);
                        break;
                    }
                }
            }

            keypadButton.setTypeface(keypadButton.getTypeface(), isScientific ? Typeface.NORMAL : Typeface.BOLD);
            if (isScientific) {
                switch (keypadButton.getId()) {
                    case R.id.seven:
                        keypadButton.setTag("powerTwo");

                        // Dynamic text preview.
                        input.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if (isScientific) {
                                    keypadButton.setText(s.toString().length() == 1 && isInputLastNumber(input) ? s.toString().charAt(0) + mainActivity.getPowerTwo() : getString(R.string.x_power_two));
                                } else {
                                    keypadButton.setText(getString(R.string.seven));
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        keypadButton.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent event) {
                                switch (event.getAction()) {
                                    case MotionEvent.ACTION_DOWN:
                                        mainActivity.bounceIn(view, DEFAULT_BOUNCE_IN_SETTING);
                                        break;
                                    case MotionEvent.ACTION_UP:
                                        view.startAnimation(mainActivity.getBounceOut());

                                        if (isInputLastNumber(input) && isInputLastParenthesis(input, LAST_PARENTHESIS_RIGHT))
                                            input.append(mainActivity.getPowerTwo());

                                        break;
                                }

                                return true;
                            }
                        });

                        break;
                }
            } else {
                inputNumber(input);
            }
        }
    }
}