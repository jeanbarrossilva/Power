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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    String appName;
    String versionName;

    private static final int HIDDEN_MODE = 1;

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

    Button settings;

    String hiddenModePassword;

    HorizontalScrollView inputHorizontalScrollView;
    EditText input;
    String calc;

    String pattern;
    String country;
    DecimalFormat decimalFormat;
    DecimalFormatSymbols language;

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
    int[] operators = {
            R.id.plus, R.id.minus, R.id.times, R.id.division
    };

    Button clearAll;
    ImageButton delete;
    Button equal;

    Expression expression;
    String result;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

            settings = findViewById(R.id.settings);

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

            delete = findViewById(R.id.delete);
            clearAll = findViewById(R.id.clear_all);
            decimalSeparator = findViewById(R.id.decimal_separator);

            plus = getString(R.string.plus);
            minus = getString(R.string.minus);
            times = getString(R.string.times);
            division = getString(R.string.division);

            equal = findViewById(R.id.equal);

            settings();

            inputNumber();
            inputDecimalSeparator();
            inputOperator();

            delete();
            clearAll();
            equal();
        } else {
            setContentView(R.layout.activity_blank);
            dialogIncompatibleDevice();
        }
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
                case HIDDEN_MODE:
                    Toast.makeText(this, getString(R.string.hidden_mode_set_up_success), Toast.LENGTH_LONG).show();
                    hiddenModePassword = data.getStringExtra("password");

                    dialogHiddenMode();
                    isHiddenModeEnabled = true;

                    break;
            }
        } else {
            Toast.makeText(MainActivity.this, "Activity returned null.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        theme = configuration.uiMode & Configuration.UI_MODE_NIGHT_MASK;

        // Changes the app theme based on if the system dark mode is enabled or not.
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
        dialogOKMessage.setText(String.format(getString(R.string.release_notes_dialog_message), appName, versionName));
        dialogOK.show();
    }

    private void dialogHiddenMode() {
        dialogYesNoTitle.setText(getString(R.string.hidden_mode_dialog_title));
        dialogYesNoMessage.setText(getString(R.string.hidden_mode_enter_dialog_message));

        dialogYesNoYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, HiddenMode.class));
            }
        });

        dialogYesNo.show();
    }

    private void settings() {
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, SettingsActivity.class), HIDDEN_MODE);
            }
        });
    }

    private void night(boolean isNight) {
        if (isNight) {
            AppCompatDelegate.setDefaultNightMode(Configuration.UI_MODE_NIGHT_YES);
            preferencesEditor.putBoolean("isNight", true);

            System.out.println("Night mode has been enabled.");
        } else {
            AppCompatDelegate.setDefaultNightMode(Configuration.UI_MODE_NIGHT_NO);
            preferencesEditor.putBoolean("isNight", false);
            System.out.println("Night mode has been disabled.");
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean inputHasReachedCharLimit() {
        return calc.length() > 10;
    }

    private void inputNumber() {
        if (!inputHasReachedCharLimit()) {
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    number = (Button) view;
                    input.append(number.getText());

                    System.out.println("Number '" + number.getText() + "' added.");
                    System.out.println("Updated 'calc' value: " + calc);
                }
            };

            for (int id: numbers) {
                findViewById(id).setOnClickListener(onClickListener);
            }
        }
    }

    private void inputDecimalSeparator() {
        if (!inputHasReachedCharLimit()) {
            decimalSeparator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    input.append(decimalSeparator.getText());
                    System.out.println("Decimal separator added.");
                    System.out.println("Updated 'calc' value: " + calc);
                }
            });
        }
    }

    private void inputOperator() {
        if (!inputHasReachedCharLimit()) {
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    operator = (Button) view;
                    input.append(space + operator.getText() + space);

                    System.out.println("Operator '" + operator.getText() + "' added.");
                    System.out.println("Updated 'calc' value: " + calc);
                }
            };

            for (int id: operators) {
                findViewById(id).setOnClickListener(onClickListener);
            }
        }
    }

    private void delete() {
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                input.setText(calc.substring(0, calc.length() - 1));
                System.out.println("Character deleted.");
            }
        });
    }

    private void clearAll() {
        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                input.setText(empty);
                System.out.println("Input cleared.");
            }
        });
    }

    private void equal() {
        equal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                try {
                    expression = new ExpressionBuilder(calc).build();
                    result = String.valueOf(expression.evaluate());

                    if (result.endsWith(".0")) {
                        result = result.replace(".0", empty);
                    }

                    input.setText(result);
                    System.out.println("Calc result: " + result);
                } catch(Exception exception) {
                    input.setText(getString(R.string.error));
                }
            }
        });
    }
}