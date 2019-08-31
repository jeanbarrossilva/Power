package com.jeanbarrossilva.power;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nisrulz.sensey.Sensey;
import com.github.nisrulz.sensey.TouchTypeDetector;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.Arrays;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import id.voela.actrans.AcTrans;

@SuppressWarnings("SwitchStatementWithTooFewBranches")
public class CalculatorActivity extends AppCompatActivity {
    String appName;
    String versionName;

    ConstraintLayout activityCalculator;

    static final int DIALOG_MAX_HEIGHT = 500;

    private static final int GET_VERSION_TYPE = 1;
    private static final int SETTINGS = 2;

    String empty;
    String space;
    String leftSquareBracket;
    String rightSquareBracket;
    String comma;

    SharedPreferences preferences;
    SharedPreferences.Editor preferencesEditor;

    Dialog dialogOK;
    TextView dialogOKTitle;
    TextView dialogOKMessage;
    Button dialogOKButton;

    Dialog dialogYesNo;
    TextView dialogYesNoTitle;
    TextView dialogYesNoMessage;
    Button dialogYesNoNoButton;
    Button dialogYesNoYesButton;

    int theme;

    boolean isHiddenModeEnabled;
    boolean isHapticFeedbackEnabled;

    public AcTrans.Builder acTrans;

    String hiddenModePassword;
    String textAlignment;

    TextView inputResult;
    HorizontalScrollView inputHorizontalScrollView;
    EditText input;
    String calc;

    HorizontalScrollView othersHorizontalScrollView;

    ImageButton calculatorMode;

    ConstraintLayout keypad;

    BounceInterpolator bounceInterpolator;
    Animation bounceIn;
    Animation bounceOut;

    static final String LOW_BOUNCE_IN_SETTING = "0.1, 10";
    static final String NORMAL_BOUNCE_IN_SETTING = "0.5, 15";
    static final String HIGH_BOUNCE_IN_SETTING = "1, 20";

    static final String DEFAULT_OPTION_BOUNCE_IN_SETTING = "0.1, 5";

    Button number;
    int[] numbers = {
            R.id.zero, R.id.one, R.id.two, R.id.three, R.id.four, R.id.five, R.id.six, R.id.seven, R.id.eight, R.id.nine
    };

    Button decimalSeparator;

    Button operator;
    String plus;
    String minus;
    String times;
    String division;

    String asterisk;
    String slash;

    int[] operators = {
            R.id.plus, R.id.minus, R.id.times, R.id.division
    };

    Button parenthesis;
    int[] parenthesis2 = {
            R.id.left_parenthesis, R.id.right_parenthesis
    };

    ImageButton delete;

    Button equal;

    Expression expression;
    String result;

    boolean isBeta = false;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityCalculator = findViewById(R.id.activity_main);

        // OK dialog (dialog with an OK neutral button) declaration.
        dialogOK = new Dialog(this);
        Objects.requireNonNull(dialogOK.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogOK.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogOK.setContentView(R.layout.dialog_ok);

        dialogOKTitle = dialogOK.findViewById(R.id.title);
        dialogOKMessage = dialogOK.findViewById(R.id.message);
        dialogOKButton = dialogOK.findViewById(R.id.ok);

        if (!screenSize().equals("small")) {
            setContentView(R.layout.activity_calculator);

            appName = getString(R.string.app_name);
            versionName = BuildConfig.VERSION_NAME;

            empty = "";
            space = " ";
            leftSquareBracket = "[";
            rightSquareBracket = "]";
            comma = ",";

            preferences = getSharedPreferences("com.jeanbarrossilva.power", Context.MODE_PRIVATE);
            preferencesEditor = preferences.edit();

            dialogOKButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogOK.dismiss();
                }
            });

            // Yes/No dialog (dialog with a positive Yes and a negative No buttons) declaration.
            dialogYesNo = new Dialog(this);
            Objects.requireNonNull(dialogYesNo.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogYesNo.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogYesNo.setContentView(R.layout.dialog_yes_no);

            dialogYesNoTitle = dialogYesNo.findViewById(R.id.title);
            dialogYesNoMessage = dialogYesNo.findViewById(R.id.message);
            dialogYesNoNoButton = dialogYesNo.findViewById(R.id.no);
            dialogYesNoYesButton = dialogYesNo.findViewById(R.id.yes);

            dialogYesNoNoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogYesNo.dismiss();
                }
            });

            acTrans = new AcTrans.Builder(CalculatorActivity.this);

            Sensey.getInstance().init(this);

            hiddenModePassword = preferences.getString("hiddenModePassword", null);
            textAlignment = preferences.getString("textAlignment", null);

            inputResult = findViewById(R.id.input_result);
            inputHorizontalScrollView = findViewById(R.id.input_horizontal_scroll_view);
            input = findViewById(R.id.input);

            // Disables the keyboard, since the app already has predefined buttons.
            input.setFocusable(false);

            calc = input.getText().toString();

            Timer timer = new Timer();

            // Updates the value of 'calc' every 100 milliseconds (0.1 second).
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    calc = input.getText().toString();
                }
            }, 0, 100);

            calculatorMode = findViewById(R.id.calculator_mode);

            bounceIn = AnimationUtils.loadAnimation(this, R.anim.bounce_in);

            bounceOut = AnimationUtils.loadAnimation(this, R.anim.bounce_out);
            bounceOut.setInterpolator(bounceInterpolator);

            othersHorizontalScrollView = findViewById(R.id.others_horizontal_scroll_view);
            othersHorizontalScrollView.setHorizontalScrollBarEnabled(false);

            keypad = findViewById(R.id.keypad);
            decimalSeparator = findViewById(R.id.decimal_separator);
            equal = findViewById(R.id.equal);

            delete = findViewById(R.id.delete);

            plus = getString(R.string.plus);
            minus = getString(R.string.minus);
            times = getString(R.string.times);
            division = getString(R.string.division);

            asterisk = "*";
            slash = "/";

            calculatorMode = findViewById(R.id.calculator_mode);

            if (Build.VERSION.SDK_INT >= 21) {
                if (Build.VERSION.SDK_INT >= 28) {
                    if (preferences.getBoolean("isNight", true)) {
                        getWindow().setNavigationBarColor(Color.BLACK);
                    } else {
                        getWindow().setNavigationBarColor(Color.WHITE);
                    }
                }
            }

            settings();
            calculatorMode();

            inputNumber();
            inputDecimalSeparator();
            inputOperator();
            inputParenthesis();

            delete();
            equal();
        } else {
            setContentView(R.layout.activity_blank);
            dialogIncompatibleDevice();
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Checks if this is the first time the app is being launched.
        if (preferences.getBoolean("firstLaunch", true)) {
            // Hidden Mode is disabled when the app is first launched.
            isHiddenModeEnabled = false;

            dialogWelcomeTo();
            dialogBetaVersion();

            // Declares that, obviously, from now on, it won't be the first time the app is launched.
            preferencesEditor.putBoolean("firstLaunch", false)
                    .apply();
        }

        // Checks if this is the first time the app is being launched since it was last updated.
        if (preferences.getBoolean("firstLaunchSinceLastUpdate", true)) {
            dialogReleaseNotes();

            // Declares that, from now on, it won't be the first time the app is launched since the last update.
            preferencesEditor.putBoolean("firstLaunchSinceLastUpdate", false)
                    .apply();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case SETTINGS:
                        hiddenMode(data);
                        hapticFeedback(data);

                        break;
                }
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        theme = configuration.uiMode & Configuration.UI_MODE_NIGHT_MASK;

        // Changes the app theme based on if the Android Pie (Android 9, API 28) night mode is enabled or not.
        switch(theme) {
            case Configuration.UI_MODE_NIGHT_YES:
                night(true);
                System.out.println("Night mode has been enabled.");

                break;
            case Configuration.UI_MODE_NIGHT_NO:
                night(false);
                System.out.println("Night mode has been disabled.");

                break;
        }
    }

    private String screenSize() {
        String screenLayoutSize;
        int size = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        switch(size) {
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                screenLayoutSize = "small";
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                screenLayoutSize = "normal";
                break;
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                screenLayoutSize = "large";
                break;
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                screenLayoutSize = "xlarge";
                break;
            default:
                screenLayoutSize = null;
        }

        return screenLayoutSize;
    }

    private void dialogIncompatibleDevice() {
        dialogOKTitle.setText(getString(R.string.incompatible_device_dialog_title));
        dialogOKMessage.setText(String.format(getString(R.string.incompatible_device_dialog_message), appName));

        dialogOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogOK.dismiss();
                finish();
            }
        });

        dialogOK.show();
    }

    // Shows a welcome dialog if the user opens the app for the first time.
    private void dialogWelcomeTo() {
        dialogOKTitle.setText(String.format(getString(R.string.welcome_to_dialog_title), appName));
        dialogOKMessage.setText(String.format(getString(R.string.welcome_to_dialog_message), appName));
        dialogOK.show();
    }

    // Shows a dialog warning that the actual build is a beta version if, of course, it is, in fact, a beta version.
    private void dialogBetaVersion() {
        dialogOKTitle.setText(getString(R.string.beta_version_dialog_title));
        dialogOKMessage.setText(String.format(getString(R.string.beta_version_dialog_message), appName));
        dialogOK.show();
    }

    private void dialogReleaseNotes() {
        dialogOKTitle.setText(getString(R.string.release_notes_dialog_title));
        setContentView(R.layout.activity_calculator);
        dialogOKMessage.setText(getString(R.string.release_notes_dialog_message));
        dialogOK.show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Sensey.getInstance().setupDispatchTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    void settings() {
        TouchTypeDetector.TouchTypListener settings = new TouchTypeDetector.TouchTypListener() {
            @Override
            public void onTwoFingerSingleTap() {

            }

            @Override
            public void onThreeFingerSingleTap() {

            }

            @Override
            public void onDoubleTap() {

            }

            @Override
            public void onScroll(int scrollDirection) {

            }

            @Override
            public void onSingleTap() {

            }

            @Override
            public void onSwipe(int swipeDirection) {
                switch (swipeDirection) {
                    case TouchTypeDetector.SWIPE_DIR_DOWN:
                        startActivityForResult(new Intent(CalculatorActivity.this, SettingsActivity.class), SETTINGS);
                        acTrans.performSlideToBottom();

                        break;
                }
            }

            @Override
            public void onLongPress() {

            }
        };

        Sensey.getInstance().startTouchTypeDetection(CalculatorActivity.this, settings);
    }

    public void night(boolean isNight) {
        if (isNight) {
            if (Build.VERSION.SDK_INT >= 28) {
                AppCompatDelegate.setDefaultNightMode(Configuration.UI_MODE_NIGHT_YES);

                preferencesEditor.putBoolean("isNight", true);
                System.out.println("Night mode has been enabled.");
            } else {
                Toast.makeText(CalculatorActivity.this, getString(R.string.night_incompatibility), Toast.LENGTH_LONG).show();
            }
        } else {
            AppCompatDelegate.setDefaultNightMode(Configuration.UI_MODE_NIGHT_NO);

            preferencesEditor.putBoolean("isNight", false);
            System.out.println("Night mode has been disabled.");
        }
    }

    private void hiddenMode(Intent data) {
        // Checks if Hidden Mode is enabled.
        isHiddenModeEnabled = data.getBooleanExtra("isHiddenModeEnabled", false);

        if (isHiddenModeEnabled) {
            preferencesEditor.putString("hiddenModePassword", data.getStringExtra("password"))
                    .apply();

            hiddenModePassword = preferences.getString("hiddenModePassword", null);

            Toast.makeText(CalculatorActivity.this, getString(R.string.hidden_mode_set_up_success), Toast.LENGTH_LONG).show();
            isHiddenModeEnabled = true;
        }
    }

    private void hapticFeedback(Intent data) {
        isHapticFeedbackEnabled = data.getBooleanExtra("isHapticFeedbackEnabled", false);
        preferencesEditor.putBoolean("isHapticFeedbackEnabled", isHapticFeedbackEnabled);

        if (isHapticFeedbackEnabled) {
            isHapticFeedbackEnabled = preferences.getBoolean("isHapticFeedbackEnabled", true);
        } else {
            isHapticFeedbackEnabled = preferences.getBoolean("isHapticFeedbackEnabled", false);
        }
    }

    private boolean hasSquareBracket(String text) {
        return (text.contains(leftSquareBracket) || text.contains(rightSquareBracket));
    }

    private String squareBracket(String text) {
        String squareBracket = null;

        if (hasSquareBracket(text)) {
            if (text.contains(leftSquareBracket)) {
                squareBracket = "left";
            } else if (text.contains(rightSquareBracket)) {
                squareBracket = "right";
            } else if (text.contains(leftSquareBracket) && text.contains(rightSquareBracket)) {
                squareBracket = "both";
            } else {
                squareBracket = null;
            }
        }

        return squareBracket;
    }

    // Meant to be implemented into MotionEvent.ACTION_DOWN as 'bounceIn(view, false, "amplitude, frequency")' or 'bounceIn(view, true)'. e. g., bounceIn(number, false, NORMAL_BOUNCE_IN_SETTING).
    void bounceIn(View view, boolean useDefaultSetting, String... customSetting) {
        // Default amplitude and frequency values that are replaced by custom settings if useDefaultSetting is false.
        double amplitude = 0.2;
        double frequency = 20;

        if (!useDefaultSetting) {
            if (Arrays.toString(customSetting).contains(comma + space)) {
                String[] parts = Arrays.toString(customSetting).split(comma + space);
                String formatParts = null;

                if (hasSquareBracket(Arrays.toString(parts))) {
                    switch (squareBracket(Arrays.toString(parts))) {
                        case "left":
                            formatParts = Arrays.toString(parts).replace(leftSquareBracket, empty);
                            break;
                        case "right":
                            formatParts = Arrays.toString(parts).replace(rightSquareBracket, empty);
                            break;
                        case "both":
                            formatParts = Arrays.toString(parts).replaceAll(leftSquareBracket, rightSquareBracket);
                            break;
                    }

                    assert formatParts != null;
                    String[] formatValues = formatParts.split(comma + space);

                    String formatAmplitude = formatValues[0];
                    String formatFrequency = formatValues[1];

                    // For some unknown reason, formatFrequency has "]]" at its end.
                    if (formatFrequency.endsWith(rightSquareBracket + rightSquareBracket)) {
                        formatFrequency = formatFrequency.substring(0, formatFrequency.length() - 2);
                    }

                    amplitude = Double.parseDouble(formatAmplitude);
                    frequency = Double.parseDouble(formatFrequency);
                } else {
                    amplitude = Double.parseDouble(parts[1]);
                    frequency = Double.parseDouble(parts[2]);
                }
            } else {
                System.out.println("bounceInterpolator was incorrectly set and doesn't contain '" + comma + space + "'.");
            }
        }

        view.setAnimation(bounceIn);

        bounceInterpolator = new BounceInterpolator(amplitude, frequency);

        view.startAnimation(bounceIn);
        bounceIn.setInterpolator(bounceInterpolator);

        if (String.valueOf(amplitude).endsWith(".0")) {
            String amplitudeString = String.valueOf(amplitude).replace(".0", empty);
            System.out.println("Amplitude: " + amplitudeString);
        } else {
            System.out.println("Amplitude: " + amplitude);
        }

        if (String.valueOf(frequency).endsWith(".0")) {
            String frequencyString = String.valueOf(frequency).replace(".0", empty);
            System.out.println("Frequency: " + frequencyString);
        } else {
            System.out.println("Frequency: " + frequency);
        }
    }

    private void inputResult(boolean isVisible) {
        if (isVisible) {
            if (!calc.isEmpty()) {
                if (!calc.equals(getString(R.string.error))) {
                    if (inputResult.getVisibility() != View.VISIBLE) {
                        inputResult.setVisibility(View.VISIBLE);

                        try {
                            expression = new ExpressionBuilder(reformatCalc(inputResult.getText().toString())).build();
                            result = String.valueOf(expression.evaluate());

                            if (result.contains("E")) {
                                result = result.replace("E", "e");
                            }

                            if (result.endsWith(".0")) {
                                result = result.replace(".0", empty);
                            }

                            inputResult.setText(result);
                            System.out.println("Calc result preview: " + result);
                        } catch (Exception exception) {
                            inputResult.setText(getString(R.string.error));
                        }
                    } else if (inputResult.getVisibility() != View.GONE) {
                        inputResult.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    void calculatorMode() {
        calculatorMode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                bounceInterpolator = new BounceInterpolator(0.1, 5);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(view, true);
                        break;
                    case MotionEvent.ACTION_UP:
                        final PopupMenu calculatorModes;
                        MenuInflater inflater;

                        calculatorModes = new PopupMenu(CalculatorActivity.this, calculatorMode);
                        inflater = calculatorModes.getMenuInflater();

                        inflater.inflate(R.menu.calculator_modes, calculatorModes.getMenu());
                        calculatorModes.show();

                        calculatorModes.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if (item.getTitle().equals(getString(R.string.calculator))) {
                                    calculatorModes.dismiss();
                                } else if (item.getTitle().equals(getString(R.string.length))) {
                                    startActivity(new Intent(CalculatorActivity.this, LengthActivity.class));
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                } else if (item.getTitle().equals(getString(R.string.temperature))) {
                                    startActivity(new Intent(CalculatorActivity.this, TemperatureActivity.class));
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                } else if (item.getTitle().equals(getString(R.string.time))) {
                                    startActivity(new Intent(CalculatorActivity.this, TimeActivity.class));
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

    private String reformatCalc(String calc) {
        if (calc.contains(plus) || calc.contains(minus) || calc.contains(times) || calc.contains(division)) {
            if (calc.contains(plus)) {
                calc = calc.replace(space + plus + space, plus);
            }

            if (calc.contains(minus)) {
                calc = calc.replace(space + minus + space, minus);
            }

            if (calc.contains(times)) {
                calc = calc.replace(space + times + space, "*");
            }

            if (calc.contains(division)) {
                calc = calc.replace(space + division + space, "/");
            }

            System.out.println("Transformed calc: " + calc);
        }

        return calc;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean inputHasReachedCharLimit() {
        if (calc.length() > 11) {
            input.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
        }

        switch (String.valueOf(input.getTextSize())) {
            case "45":
                return calc.length() == 10;
            case "35":
                return calc.length() == 16;
        }

        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void inputNumber() {
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                number = (Button) view;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(view, false, NORMAL_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        number.startAnimation(bounceOut);

                        if (!calc.equals(getString(R.string.error))) {
                            if (!inputHasReachedCharLimit()) {
                                input.append(number.getText());

                                System.out.println("Number '" + number.getText() + "' added.");
                                System.out.println("Updated 'calc' value: " + calc);
                            }
                        } else {
                            input.setText(empty);

                            number = (Button) view;
                            input.append(number.getText());

                            System.out.println("Number '" + number.getText() + "' added.");
                            System.out.println("Updated 'calc' value: " + calc);
                        }

                        // inputResult(true);
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
                        bounceIn(view, true);
                        break;
                    case MotionEvent.ACTION_UP:
                        decimalSeparator.startAnimation(bounceOut);

                        if (!inputHasReachedCharLimit()) {
                            if (isInputLastNumber()) {
                                input.append(decimalSeparator.getText());

                                System.out.println("Decimal separator added.");
                                System.out.println("Updated 'calc' value: " + calc);

                                // inputResult(true);
                            }
                        }

                        break;
                }

                return true;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void inputOperator() {
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                bounceInterpolator = new BounceInterpolator(0.1, 5);
                operator = (Button) view;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(view, true);
                        break;
                    case MotionEvent.ACTION_UP:
                        operator.startAnimation(bounceOut);

                        if (!inputHasReachedCharLimit()) {
                            if (!isInputLastOperator()) {
                                input.append(space + operator.getText() + space);

                                System.out.println("Operator '" + operator.getText() + "' added.");
                                System.out.println("Updated 'calc' value: " + calc);

                                // inputResult(true);
                            }
                        }
                }

                return true;
            }
        };

        for (int operator: operators) {
            findViewById(operator).setOnTouchListener(onTouchListener);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void inputParenthesis() {
        View.OnTouchListener listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                bounceInterpolator = new BounceInterpolator(0.1, 5);
                parenthesis = (Button) view;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(view, true);
                        break;
                    case MotionEvent.ACTION_UP:
                        parenthesis.startAnimation(bounceOut);

                        if (!inputHasReachedCharLimit()) {
                            input.append(parenthesis.getText());

                            System.out.println("Parenthesis added.");
                            System.out.println("Updated 'calc' value: " + calc);
                        }

                        break;
                }

                return true;
            }
        };

        for (int parenthesis: parenthesis2) {
            findViewById(parenthesis).setOnTouchListener(listener);
        }
    }

    boolean isInputLastNumber() {
        String[] numbers = {
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
        };

        for (String number: numbers) {
            if (reformatCalc(calc).endsWith(number)) {
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isInputLastOperator() {
        String[] operators = {
                plus, minus, asterisk, slash
        };

        for (String operator: operators) {
            if (reformatCalc(calc).endsWith(operator)) {
                // inputResult(false);
                return true;
            }
        }

        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void delete() {
        delete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                bounceInterpolator = new BounceInterpolator(0.1, 1.5);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(view, true);
                        break;
                    case MotionEvent.ACTION_UP:
                        delete.startAnimation(bounceOut);

                        if (!calc.isEmpty()) {
                            input.setText(calc.substring(0, calc.length() - 1));
                        }

                        break;
                }

                return true;
            }
        });

        /* delete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Timer timer = new Timer();

                if (isHapticFeedbackEnabled) {
                    delete.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                }

                delete.setImageResource(R.drawable.clear_all);
                input.setText(empty);

                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        delete.setImageResource(R.drawable.delete);
                    }
                }, 1000);

                return true;
            }
        }); */
    }

    @SuppressLint("ClickableViewAccessibility")
    private void equal() {
        equal.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (isHapticFeedbackEnabled) {
                            equal.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                        }

                        bounceIn(view, true);

                        break;
                    case MotionEvent.ACTION_UP:
                        equal.startAnimation(bounceOut);

                        if (!calc.isEmpty()) {
                            if (isHiddenModeEnabled) {
                                if (hiddenModePassword != null) {
                                    if (calc.equals(hiddenModePassword)) {
                                        input.setText(empty);
                                        startActivity(new Intent(CalculatorActivity.this, HiddenMode.class));
                                    }
                                } else {
                                    System.out.println("'hiddenModePassword' is null.");
                                }
                            }

                            if (!isInputLastOperator()) {
                                try {
                                    expression = new ExpressionBuilder(reformatCalc(calc)).build();
                                    result = String.valueOf(expression.evaluate());

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

                            // inputResult(false);
                        }

                        break;
                }

                return true;
            }
        });
    }

    String version() {
        String version = null;
        int dots = 0;

        // Counts how many dots are there in 'versionName'.
        for (char dot: versionName.toCharArray()) {
            if (dot == '.') {
                dots ++;
            }
        }

        if (dots == 1) {
            version = String.format(getString(R.string.version), versionName);
            isBeta = false;
        } else if (dots >= 2) {
            version = String.format(getString(R.string.version_beta), versionName);
            isBeta = true;
        }

        return version;
    }
}