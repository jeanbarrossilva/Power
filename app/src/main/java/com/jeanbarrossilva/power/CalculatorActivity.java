package com.jeanbarrossilva.power;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nisrulz.sensey.Sensey;
import com.github.nisrulz.sensey.TouchTypeDetector;

import java.util.TimerTask;

import id.voela.actrans.AcTrans;

@SuppressWarnings("SwitchStatementWithTooFewBranches")
public class CalculatorActivity extends MainActivity {
    private static final int SETTINGS = 1;

    int theme;

    public AcTrans.Builder acTrans;

    TextView inputResult;
    HorizontalScrollView inputHorizontalScrollView;
    EditText input;
    String calc;

    HorizontalScrollView othersHorizontalScrollView;

    ImageButton calculatorMode;

    ConstraintLayout keypad;

    Button decimalSeparator;

    ImageButton delete;
    Button equal;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!screenSize().equals("small")) {
            setContentView(R.layout.activity_calculator);

            acTrans = new AcTrans.Builder(CalculatorActivity.this);
            Sensey.getInstance().init(this);

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
                    setNight(true);
                } else {
                    setNight(false);
                }
            }

            System.out.println("Local class name: " + getClassName());

            settings();
            calculatorMode();

            inputNumber(input, calc);
            inputDecimalSeparator(input, calc, decimalSeparator);
            inputOperator(input, calc);
            inputParenthesis(input, calc);

            delete(input, delete);
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
            setIsHiddenModeEnabled(false);

            dialogWelcomeTo();
            dialogBetaVersion();

            // Declares that, obviously, from now on, it won't be the first time the app is launched.
            getPreferencesEditor().putBoolean("firstLaunch", false)
                    .apply();
        }

        // Checks if this is the first time the app is being launched since it was last updated.
        if (getPreferences().getBoolean("firstLaunchSinceLastUpdate", true)) {
            // Declares that, from now on, it won't be the first time the app is launched since the last update.
            getPreferencesEditor().putBoolean("firstLaunchSinceLastUpdate", false)
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
                setNight(true);
                System.out.println("Night mode has been enabled.");

                break;
            case Configuration.UI_MODE_NIGHT_NO:
                setNight(false);
                System.out.println("Night mode has been disabled.");

                break;
        }
    }

    private void dialogIncompatibleDevice() {
        setDialogOKTitle(getString(R.string.incompatible_device_dialog_title));
        setDialogOKMessage(String.format(getString(R.string.incompatible_device_dialog_message), getAppName()));

        setDialogOKButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialogOK().dismiss();
                finish();
            }
        });

        getDialogOK().show();
    }

    // Shows a welcome dialog if the user opens the app for the first time.
    private void dialogWelcomeTo() {
        setDialogOKTitle(String.format(getString(R.string.welcome_to_dialog_title), getAppName()));
        setDialogOKMessage(String.format(getString(R.string.welcome_to_dialog_message), getAppName()));
        getDialogOK().show();
    }

    // Shows a dialog warning that the actual build is a beta version if, of course, it is, in fact, a beta version.
    private void dialogBetaVersion() {
        setDialogOKTitle(getString(R.string.beta_version_dialog_title));
        setDialogOKMessage(String.format(getString(R.string.beta_version_dialog_message), getAppName()));
        getDialogOK().show();
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

    private void hiddenMode(Intent data) {
        // Checks if Hidden Mode is enabled.
        setIsHiddenModeEnabled(data.getBooleanExtra("isHiddenModeEnabled", getIsHiddenModeEnabled()));

        if (getIsHiddenModeEnabled()) {
            setPin(data.getStringExtra("pin"));

            Toast.makeText(CalculatorActivity.this, getString(R.string.hidden_mode_set_up_success), Toast.LENGTH_LONG).show();
            setIsHiddenModeEnabled(true);

            System.out.println("Apparently, Hidden Mode was successfully set.");
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    void calculatorMode() {
        calculatorMode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(view, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        view.startAnimation(getBounceOut());

                        if (getIsHiddenModeUnlocked()) {
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
                        } else {
                            getDialogBuyPro().show();
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
                                bounceIn(view, DEFAULT_BOUNCE_IN_SETTING);
                                equal.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

                                break;
                            case MotionEvent.ACTION_UP:
                                equal.startAnimation(getBounceOut());

                                if (!calc.isEmpty()) {
                                    if (getIsHiddenModeEnabled()) {
                                        if (getPin() != null) {
                                            if (calc.equals(getPin())) {
                                                input.setText(getEmpty());
                                                startActivity(new Intent(CalculatorActivity.this, HiddenMode.class));
                                            }
                                        } else {
                                            System.out.println("'pin' is null.");
                                        }
                                    }

                                    if (calc.equals(SECRET_CODE_LEVEL_1)) {
                                        if (getIsAuthentic()) {
                                            if (!getIsNightUnlocked()) {
                                                input.setText(getEmpty());

                                                startActivity(new Intent(CalculatorActivity.this, SecretCodeActivity.class));
                                                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                                            }
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