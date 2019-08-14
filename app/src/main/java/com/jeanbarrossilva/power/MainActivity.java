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
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nisrulz.sensey.Sensey;
import com.github.nisrulz.sensey.TouchTypeDetector;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import id.voela.actrans.AcTrans;

public class MainActivity extends AppCompatActivity {
    String appName;
    String versionName;

    ConstraintLayout activityMain;

    private static final int SETTINGS = 1;

    String empty;
    String space;

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

    Sensey sensey;
    AcTrans.Builder acTrans;

    String hiddenModePassword;
    String textAlignment;

    TextView inputResult;
    HorizontalScrollView inputHorizontalScrollView;
    EditText input;
    String calc;

    String pattern;
    String country;
    DecimalFormat decimalFormat;
    DecimalFormatSymbols language;

    ImageButton calculatorMode;

    ConstraintLayout keypad;

    Button number;
    ConstraintLayout numbersLayout;
    int[] numbers = {
            R.id.zero, R.id.one, R.id.two, R.id.three, R.id.four, R.id.five, R.id.six, R.id.seven, R.id.eight, R.id.nine
    };

    Button decimalSeparator;

    ConstraintLayout othersLayout;

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

    Button equal;
    ImageButton back;

    Expression expression;
    String result;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMain = findViewById(R.id.activity_main);

        // OK dialog (dialog with an OK neutral button) declaration.
        dialogOK = new Dialog(this);
        Objects.requireNonNull(dialogOK.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogOK.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogOK.setContentView(R.layout.dialog_ok);

        dialogOKTitle = dialogOK.findViewById(R.id.title);
        dialogOKMessage = dialogOK.findViewById(R.id.message);
        dialogOKButton = dialogOK.findViewById(R.id.ok);

        if (!screenSize().equals("small")) {
            setContentView(R.layout.activity_main);

            appName = getString(R.string.app_name);
            versionName = BuildConfig.VERSION_NAME;

            empty = "";
            space = " ";

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

            sensey = Sensey.getInstance();
            acTrans = new AcTrans.Builder(MainActivity.this);

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

            pattern = "###,###";
            country = getResources().getConfiguration().locale.getCountry();

            language = new DecimalFormatSymbols(new Locale("en"));
            language.setDecimalSeparator('.');
            language.setGroupingSeparator(',');

            decimalFormat = new DecimalFormat(pattern, language);

            // calculatorMode = findViewById(R.id.calculator_mode);

            keypad = findViewById(R.id.keypad);
            numbersLayout = findViewById(R.id.numbers);
            othersLayout = findViewById(R.id.others);

            decimalSeparator = findViewById(R.id.decimal_separator);

            plus = getString(R.string.plus);
            minus = getString(R.string.minus);
            times = getString(R.string.times);
            division = getString(R.string.division);

            asterisk = "*";
            slash = "/";

            equal = findViewById(R.id.equal);
            back = findViewById(R.id.back);

            settings();
            // calculatorMode();

            inputNumber();
            inputDecimalSeparator();

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
            switch (requestCode) {
                case SETTINGS:
                    hiddenMode(data);
                    hapticFeedback(data);

                    break;
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        theme = configuration.uiMode & Configuration.UI_MODE_NIGHT_MASK;

        // Changes the app theme based on if the Android Oreo (Android 8, API 26) Night mode is enabled or not.
        switch(theme) {
            case Configuration.UI_MODE_NIGHT_YES:
                isNight(true);
                System.out.println("Night mode has been enabled.");

                break;
            case Configuration.UI_MODE_NIGHT_NO:
                isNight(false);
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
        dialogOKMessage.setText(String.format(getString(R.string.release_notes_dialog_message), appName, versionName));
        dialogOK.show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        sensey.setupDispatchTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    private void settings() {
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
                switch (scrollDirection) {
                    case TouchTypeDetector.SCROLL_DIR_UP:
                        break;
                    case TouchTypeDetector.SCROLL_DIR_LEFT:
                        break;
                    case TouchTypeDetector.SCROLL_DIR_RIGHT:
                        break;
                    case TouchTypeDetector.SCROLL_DIR_DOWN:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onSingleTap() {

            }

            @Override
            public void onSwipe(int swipeDirection) {
                switch (swipeDirection) {
                    case TouchTypeDetector.SWIPE_DIR_UP:
                        break;
                    case TouchTypeDetector.SWIPE_DIR_LEFT:
                        break;
                    case TouchTypeDetector.SWIPE_DIR_RIGHT:
                        startActivityForResult(new Intent(MainActivity.this, SettingsActivity.class), SETTINGS);
                        acTrans.performSlideToRight();

                        break;
                    case TouchTypeDetector.SWIPE_DIR_DOWN:
                        break;
                }
            }

            @Override
            public void onLongPress() {

            }
        };

        Sensey.getInstance().startTouchTypeDetection(MainActivity.this, settings);
    }

    public void isNight(boolean night) {
        if (night) {
            if (Build.VERSION.SDK_INT >= 28) {
                AppCompatDelegate.setDefaultNightMode(Configuration.UI_MODE_NIGHT_YES);
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.night_incompatibility), Toast.LENGTH_LONG).show();
            }

            preferencesEditor.putBoolean("isNight", true);
            System.out.println("Night mode has been enabled.");
        } else {
            AppCompatDelegate.setDefaultNightMode(Configuration.UI_MODE_NIGHT_NO);
            preferencesEditor.putBoolean("isNight", false);
            System.out.println("Night mode has been disabled.");
        }
    }

    private void hiddenMode(Intent data) {
        // Checks if Hidden Mode was enabled.
        isHiddenModeEnabled = data.getBooleanExtra("isHiddenModeEnabled", false);

        if (isHiddenModeEnabled) {
            preferencesEditor.putString("hiddenModePassword", data.getStringExtra("password"))
                    .apply();

            hiddenModePassword = preferences.getString("hiddenModePassword", null);

            Toast.makeText(MainActivity.this, getString(R.string.hidden_mode_set_up_success), Toast.LENGTH_LONG).show();
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

    private void inputResult(boolean isVisible) {
        if (isVisible) {
            if (!calc.isEmpty()) {
                if (calc.equals(getString(R.string.error))) {
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
                    }
                }
            } else if (inputResult.getVisibility() != View.GONE) {
                inputResult.setVisibility(View.GONE);
            }
        }
    }

    private void calculatorMode() {
        calculatorMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogOKTitle.setText(getString(R.string.unavailable_feature_dialog_title));
                dialogOKMessage.setText(getString(R.string.unavailable_feature_yet_dialog_message));
                dialogOK.show();
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
    private boolean inputHasReachedCharLimit() {
        if (calc.length() > 11) {
            input.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
        }

        return calc.length() == 14;
    }

    private void inputNumber() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!calc.equals(getString(R.string.error))) {
                    if (!inputHasReachedCharLimit()) {
                        number = (Button) view;
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

                inputResult(true);
            }
        };

        for (int id: numbers) {
            findViewById(id).setOnClickListener(onClickListener);
        }
    }

    private void inputDecimalSeparator() {
        decimalSeparator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (!inputHasReachedCharLimit()) {
                if (isInputLastNumber()) {
                    input.append(decimalSeparator.getText());

                    System.out.println("Decimal separator added.");
                    System.out.println("Updated 'calc' value: " + calc);

                    inputResult(true);
                }
            }
            }
        });
    }

    private void inputOperator() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (!inputHasReachedCharLimit()) {
                if (!isInputLastOperator()) {
                    operator = (Button) view;
                    input.append(space + operator.getText() + space);

                    System.out.println("Operator '" + operator.getText() + "' added.");
                    System.out.println("Updated 'calc' value: " + calc);

                    inputResult(true);
                }
            }
            }
        };

        for (int id: operators) {
            findViewById(id).setOnClickListener(onClickListener);
        }
    }

    private void inputParenthesis() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!inputHasReachedCharLimit()) {
                    parenthesis = (Button) view;
                    input.append(parenthesis.getText());

                    System.out.println("Parenthesis added.");
                    System.out.println("Updated 'calc' value: " + calc);
                }
            }
        };

        for (int id: parenthesis2) {
            findViewById(id).setOnClickListener(listener);
        }
    }

    private boolean isInputLastNumber() {
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

    private boolean isInputLastOperator() {
        String[] operators = {
                plus, minus, asterisk, slash
        };

        for (String operator: operators) {
            if (reformatCalc(calc).endsWith(operator)) {
                inputResult(false);
                return true;
            }
        }

        return false;
    }

    private void delete() {
        TouchTypeDetector.TouchTypListener delete = new TouchTypeDetector.TouchTypListener() {
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
                switch (scrollDirection) {
                    case TouchTypeDetector.SCROLL_DIR_UP:
                        break;
                    case TouchTypeDetector.SCROLL_DIR_LEFT:
                        break;
                    case TouchTypeDetector.SCROLL_DIR_RIGHT:
                        break;
                    case TouchTypeDetector.SCROLL_DIR_DOWN:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onSingleTap() {

            }

            @Override
            public void onSwipe(int swipeDirection) {
                switch (swipeDirection) {
                    case TouchTypeDetector.SWIPE_DIR_UP:
                        break;
                    case TouchTypeDetector.SWIPE_DIR_LEFT:
                        if (!calc.isEmpty()) {
                            if (isInputLastOperator()) {
                                input.setText(calc.substring(0, calc.length() - 3));
                                System.out.println("Operator deleted.");
                            } else {
                                input.setText(calc.substring(0, calc.length() - 1));
                                System.out.println("Number deleted.");
                            }

                            inputResult(true);
                        }

                        break;
                    case TouchTypeDetector.SWIPE_DIR_RIGHT:
                        break;
                    case TouchTypeDetector.SWIPE_DIR_DOWN:
                        break;
                }
            }

            @Override
            public void onLongPress() {

            }
        };

        Sensey.getInstance().startTouchTypeDetection(input.getContext(), delete);
    }

    private void equal() {
        equal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!calc.isEmpty()) {
                    if (isHiddenModeEnabled) {
                        if (hiddenModePassword != null) {
                            if (calc.equals(hiddenModePassword)) {
                                input.setText(empty);
                                startActivity(new Intent(MainActivity.this, HiddenMode.class));
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

                            input.setText(result);
                            System.out.println("Calc result: " + result);
                        } catch (Exception exception) {
                            input.setText(getString(R.string.error));
                        }
                    } else {
                        input.append("0");
                    }

                    inputResult(false);
                }
            }
        });

        equal.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (isHapticFeedbackEnabled) {
                    equal.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                }

                numbersLayout.setVisibility(View.GONE);
                othersLayout.setVisibility(View.VISIBLE);

                inputOperator();
                inputParenthesis();

                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        othersLayout.setVisibility(View.GONE);
                        numbersLayout.setVisibility(View.VISIBLE);
                    }
                });

                return true;
            }
        });
    }
}