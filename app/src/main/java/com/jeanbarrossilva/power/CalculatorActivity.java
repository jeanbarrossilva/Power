package com.jeanbarrossilva.power;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
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

import java.util.Arrays;
import java.util.Objects;
import java.util.TimerTask;

import id.voela.actrans.AcTrans;

import static android.util.DisplayMetrics.DENSITY_HIGH;
import static android.util.DisplayMetrics.DENSITY_LOW;
import static android.util.DisplayMetrics.DENSITY_MEDIUM;
import static android.util.DisplayMetrics.DENSITY_XHIGH;
import static android.util.DisplayMetrics.DENSITY_XXHIGH;
import static android.util.DisplayMetrics.DENSITY_XXXHIGH;

@SuppressWarnings("SwitchStatementWithTooFewBranches")
public class CalculatorActivity extends MainActivity {

    ConstraintLayout activityCalculator;

    private static final int SETTINGS = 2;

    String leftSquareBracket;
    String rightSquareBracket;

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
    static final String HIGH_BOUNCE_IN_SETTING = "1, 25";

    static final String DEFAULT_OPTION_BOUNCE_IN_SETTING = "0.1, 5";

    Button number;
    int[] numbers = {
            R.id.zero, R.id.one, R.id.two, R.id.three, R.id.four, R.id.five, R.id.six, R.id.seven, R.id.eight, R.id.nine
    };

    Button decimalSeparator;

    Button operator;
    int[] operators = {
            R.id.plus, R.id.minus, R.id.times, R.id.division
    };

    Button parenthesis;
    int[] parenthesis2 = {
            R.id.left_parenthesis, R.id.right_parenthesis
    };

    ImageButton delete;

    Button equal;

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

            leftSquareBracket = "[";
            rightSquareBracket = "]";

            preferencesEditor = getPreferences().edit();

            isHiddenModeEnabled = getPreferences().getBoolean("isHiddenModeEnabled", false);

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

            hiddenModePassword = getPreferences().getString("hiddenModePassword", null);
            textAlignment = getPreferences().getString("textAlignment", null);

            inputResult = findViewById(R.id.input_result);
            inputHorizontalScrollView = findViewById(R.id.input_horizontal_scroll_view);
            input = findViewById(R.id.input);

            // Disables the keyboard, since the app already has predefined buttons.
            input.setFocusable(false);

            calc = input.getText().toString();

            // Updates the value of 'calc' every 100 milliseconds (0.1 second).
            getTimer().schedule(new TimerTask() {
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

            calculatorMode = findViewById(R.id.calculator_mode);

            if (Build.VERSION.SDK_INT >= 21) {
                if (getIsNightEnabled()) {
                    getWindow().setNavigationBarColor(Color.BLACK);
                } else {
                    getWindow().setNavigationBarColor(Color.WHITE);
                }
            } else {
                if (getIsNightEnabled()) {
                    night(true);
                } else {
                    night(false);
                }
            }

            System.out.println("Local class name: " + getClassName());

            settings();
            calculatorMode();

            inputNumber();
            inputDecimalSeparator();
            inputOperator();
            inputParenthesis();

            delete();
            calc();
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
        if (getPreferences().getBoolean("firstLaunch", true)) {
            // Hidden Mode is disabled when the app is first launched.
            isHiddenModeEnabled = false;

            dialogWelcomeTo();
            dialogBetaVersion();

            // Declares that, obviously, from now on, it won't be the first time the app is launched.
            preferencesEditor.putBoolean("firstLaunch", false)
                    .apply();
        }

        // Checks if this is the first time the app is being launched since it was last updated.
        if (getPreferences().getBoolean("firstLaunchSinceLastUpdate", true)) {
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
                        break;
                }
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        theme = configuration.uiMode & Configuration.UI_MODE_NIGHT_MASK;

        // Changes the app theme based on if the Android Pie (Android 9, API 28) night mode is enabled or not.
        switch (theme) {
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

        switch (size) {
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

    private String screenDensity() {
        String density;
        int dpi = getResources().getDisplayMetrics().densityDpi;

        switch (dpi) {
            case DENSITY_LOW:
                density = "ldpi";
                break;
            case DENSITY_MEDIUM:
                density = "mdpi";
                break;
            case DENSITY_HIGH:
                density = "hdpi";
                break;
            case DENSITY_XHIGH:
                density = "xhdpi";
                break;
            case DENSITY_XXHIGH:
                density = "xxhdpi";
                break;
            case DENSITY_XXXHIGH:
                density = "xxxhdpi";
                break;
            default:
                density = null;
        }

        return density;
    }

    private void dialogIncompatibleDevice() {
        dialogOKTitle.setText(getString(R.string.incompatible_device_dialog_title));
        dialogOKMessage.setText(String.format(getString(R.string.incompatible_device_dialog_message), getAppName()));

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
        dialogOKTitle.setText(String.format(getString(R.string.welcome_to_dialog_title), getAppName()));
        dialogOKMessage.setText(String.format(getString(R.string.welcome_to_dialog_message), getAppName()));
        dialogOK.show();
    }

    // Shows a dialog warning that the actual build is a beta version if, of course, it is, in fact, a beta version.
    private void dialogBetaVersion() {
        dialogOKTitle.setText(getString(R.string.beta_version_dialog_title));
        dialogOKMessage.setText(String.format(getString(R.string.beta_version_dialog_message), getAppName()));
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

    public void night(boolean enableNight) {
        if (enableNight) {
            preferencesEditor.putBoolean("isNightEnabled", true)
                    .apply();

            if (Build.VERSION.SDK_INT < 21) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                getDelegate().applyDayNight();
            }

            AppCompatDelegate.setDefaultNightMode(Configuration.UI_MODE_NIGHT_YES);
            System.out.println("Night mode has been enabled.");
        } else {
            preferencesEditor.putBoolean("isNightEnabled", false)
                    .apply();

            if (Build.VERSION.SDK_INT < 21) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                getDelegate().applyDayNight();
            }

            AppCompatDelegate.setDefaultNightMode(Configuration.UI_MODE_NIGHT_NO);
            System.out.println("Night mode has been disabled.");
        }
    }

    private void hiddenMode(Intent data) {
        // Checks if Hidden Mode is enabled.
        isHiddenModeEnabled = data.getBooleanExtra("isHiddenModeEnabled", false);

        if (isHiddenModeEnabled) {
            preferencesEditor.putString("hiddenModePassword", data.getStringExtra("password"))
                    .apply();

            hiddenModePassword = getPreferences().getString("hiddenModePassword", null);

            Toast.makeText(CalculatorActivity.this, getString(R.string.hidden_mode_set_up_success), Toast.LENGTH_LONG).show();
            isHiddenModeEnabled = true;
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
            if (Arrays.toString(customSetting).contains(getComma() + getSpace())) {
                String[] parts = Arrays.toString(customSetting).split(getComma() + getSpace());
                String formatParts = null;

                if (hasSquareBracket(Arrays.toString(parts))) {
                    switch (squareBracket(Arrays.toString(parts))) {
                        case "left":
                            formatParts = Arrays.toString(parts).replace(leftSquareBracket, getEmpty());
                            break;
                        case "right":
                            formatParts = Arrays.toString(parts).replace(rightSquareBracket, getEmpty());
                            break;
                        case "both":
                            formatParts = Arrays.toString(parts).replaceAll(leftSquareBracket, rightSquareBracket);
                            break;
                    }

                    assert formatParts != null;
                    String[] formatValues = formatParts.split(getComma() + getSpace());

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
                System.out.println("bounceInterpolator was incorrectly set and doesn't contain '" + getComma() + getSpace() + "'.");
            }
        }

        view.setAnimation(bounceIn);

        bounceInterpolator = new BounceInterpolator(amplitude, frequency);

        view.startAnimation(bounceIn);
        bounceIn.setInterpolator(bounceInterpolator);

        if (String.valueOf(amplitude).endsWith(".0")) {
            String amplitudeString = String.valueOf(amplitude).replace(".0", getEmpty());
            System.out.println("Amplitude: " + amplitudeString);
        } else {
            System.out.println("Amplitude: " + amplitude);
        }

        if (String.valueOf(frequency).endsWith(".0")) {
            String frequencyString = String.valueOf(frequency).replace(".0", getEmpty());
            System.out.println("Frequency: " + frequencyString);
        } else {
            System.out.println("Frequency: " + frequency);
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

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean inputHasReachedCharLimit() {
        try {
            if (screenDensity().equals("ldpi") || screenDensity().equals("mdpi") || screenDensity().equals("hdpi")) {
                switch (calc.length()) {
                    case 14:
                        input.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
                        break;
                    case 17:
                        input.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                        break;
                    case 19:
                        return true;
                }
            } else if (screenDensity().equals("xhdpi") || screenDensity().equals("xxhdpi") || screenDensity().equals("xxxhdpi")) {
                switch (calc.length()) {
                    case 12:
                        input.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
                        break;
                    case 15:
                        input.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                        break;
                    case 17:
                        return true;
                }
            }
        } catch (NullPointerException nullPointerException) {
            System.out.println("Couldn't check if the input has reached its limit.");
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
                            input.setText(getEmpty());

                            number = (Button) view;
                            input.append(number.getText());

                            System.out.println("Number '" + number.getText() + "' added.");
                            System.out.println("Updated 'calc' value: " + calc);
                        }
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
                            if (isInputLastNumber(calc)) {
                                input.append(decimalSeparator.getText());

                                System.out.println("Decimal separator added.");
                                System.out.println("Updated 'calc' value: " + calc);
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

                        if (operator.getText().toString().equals(getPlus()) || operator.getText().toString().equals(getMinus())) {
                            if (!inputHasReachedCharLimit()) {
                                if (!calc.equals(getString(R.string.error))) {
                                    if (!isInputLastOperator(calc)) {
                                        if (calc.isEmpty()) {
                                            input.append(operator.getText());

                                            System.out.println("Operator '" + operator.getText() + "' added.");
                                            System.out.println("Updated 'calc' value: " + calc);
                                        } else {
                                            input.append(getSpace() + operator.getText() + getSpace());

                                            System.out.println("Operator '" + operator.getText() + "' added.");
                                            System.out.println("Updated 'calc' value: " + calc);
                                        }
                                    }
                                } else {
                                    input.setText(getEmpty());

                                    operator = (Button) view;
                                    input.append(operator.getText());

                                    System.out.println("Operator '" + number.getText() + "' added.");
                                    System.out.println("Updated 'calc' value: " + calc);
                                }
                            }
                        } else {
                            if (!calc.isEmpty()) {
                                if (!inputHasReachedCharLimit()) {
                                    if (!calc.equals(getString(R.string.error))) {
                                        if (!isInputLastOperator(calc)) {
                                            input.append(getSpace() + operator.getText() + getSpace());

                                            System.out.println("Operator '" + operator.getText() + "' added.");
                                            System.out.println("Updated 'calc' value: " + calc);
                                        }
                                    } else {
                                        input.setText(getEmpty());

                                        operator = (Button) view;
                                        input.append(operator.getText() + getSpace());

                                        System.out.println("Operator '" + number.getText() + "' added.");
                                        System.out.println("Updated 'calc' value: " + calc);
                                    }
                                }
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
                        delete.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

                        if (!calc.isEmpty()) {
                            // When calc ends with " " (getSpace()), it means that it ends with an operator; the operator is shown as "getSpace() + operator + getSpace()". e. g., " × " and " + ". So, for example, if the input is "2 + 2", while deleting, the input would be equal to "2 + ", "2 +", "2 " and "2", respectively; with this method, the result would be "2 +" and "2", respectively.
                            if (calc.endsWith(getSpace())) {
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
        switch (getClassName()) {
            case "CalculatorActivity":
                equal.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                bounceIn(view, false, HIGH_BOUNCE_IN_SETTING);
                                equal.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

                                break;
                            case MotionEvent.ACTION_UP:
                                equal.startAnimation(bounceOut);

                                if (!calc.isEmpty()) {
                                    if (isHiddenModeEnabled) {
                                        if (hiddenModePassword != null) {
                                            if (calc.equals(hiddenModePassword)) {
                                                input.setText(getEmpty());
                                                startActivity(new Intent(CalculatorActivity.this, HiddenMode.class));
                                            }
                                        } else {
                                            System.out.println("'hiddenModePassword' is null.");
                                        }
                                    }

                                    inputFormat(input, calc);
                                }

                                break;
                        }

                        return true;
                    }
                });

                break;
        }
    }
}