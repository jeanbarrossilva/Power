package com.jeanbarrossilva.power;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.InputType;
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
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import id.voela.actrans.AcTrans;

@SuppressWarnings({"FieldCanBeLocal"})
public class MainActivity extends AppCompatActivity {
    private String empty;
    private String space;
    private String colon;

    private String hyphen;

    String deviceName;
    private String deviceInfo;

    private String appName;
    private String versionName;

    private SharedPreferences preferences;
    private SharedPreferences.Editor preferencesEditor;

    // private String userId;

    BottomNavigationView bottomNavigationView;

    CalculatorFragment calculatorFragment;
    SettingsFragment settingsFragment;

    AcTrans.Builder acTrans;

    private boolean isNightEnabled;

    private Dialog alertSuccess;
    // private View alertSuccessIcon;
    // private TextView alertSuccessMessage;

    private Dialog[] alerts;

    private Dialog alertInfo;
    // private View alertInfoIcon;
    // private TextView alertInfoMessage;

    private Dialog alertWarning;
    // private View alertWarningIcon;
    // private TextView alertWarningMessage;

    private Dialog alertError;
    // private View alertErrorIcon;
    // private TextView alertErrorMessage;

    private Dialog[] dialogs;

    private Dialog dialogBuyPro;
    private TextView dialogBuyProTitle;
    private TextView dialogBuyProMessage;
    // private Button dialogBuyProButton;

    private Dialog dialogOK;
    private TextView dialogOKTitle;
    private TextView dialogOKMessage;
    private Button dialogOKButton;

    private Dialog dialogYesNo;
    private TextView dialogYesNoTitle;
    private TextView dialogYesNoMessage;
    private Button dialogYesNoNoButton;
    private Button dialogYesNoYesButton;

    private Dialog dialogInput;
    // private TextView dialogInputTitle;
    // private TextView dialogInputMessage;
    private EditText dialogInputField;
    private Button dialogInputButton;

    private BounceInterpolator bounceInterpolator;

    private DecimalFormat format;

    private String className;

    private Timer timer;
    private String unit;

    private double bounceAmplitude;
    private double bounceFrequency;

    static final String DEFAULT_BOUNCE_IN_SETTING = "0.5, 5";

    static final int LAST_PARENTHESIS_LEFT = 0;
    static final int LAST_PARENTHESIS_RIGHT = 1;

    static final int REMOVE_SQUARE_BRACKET_LEFT = 0;
    static final int REMOVE_SQUARE_BRACKET_RIGHT = 1;
    static final int REMOVE_SQUARE_BRACKET_ALL = 2;

    private String leftParenthesis;
    private String rightParenthesis;

    private String leftSquareBracket;
    private String rightSquareBracket;

    private Button number;
    final int[] numbers = {
            R.id.zero, R.id.one, R.id.two, R.id.three, R.id.four, R.id.five, R.id.six, R.id.seven, R.id.eight, R.id.nine
    };

    String dot;
    private String comma;
    private String[] decimalSeparators;

    private String plus;
    private String minus;
    private String times;
    private String division;

    private String asterisk;
    private String slash;

    private String powerTwo;
    private String[] powers;

    private String infinity;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        empty = "";
        space = " ";
        colon = ":";

        hyphen = "-";

        deviceName = Settings.Secure.getString(getContentResolver(), "bluetooth_name");

        appName = getString(R.string.app_name);
        versionName = BuildConfig.VERSION_NAME;

        preferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        preferencesEditor = preferences.edit();

        calculatorFragment = new CalculatorFragment();
        settingsFragment = new SettingsFragment();

        acTrans = new AcTrans.Builder(MainActivity.this);

        isNightEnabled = preferences.getBoolean("isNightEnabled", false);

        format = new DecimalFormat("#.##");

        className = getLocalClassName();

        timer = new Timer();

        leftParenthesis = getString(R.string.left_parenthesis);
        rightParenthesis = getString(R.string.right_parenthesis);

        deviceInfo = (getString(R.string.name) + colon + space + deviceName) + "\n" + (getString(R.string.device_model) + colon + space + (Build.MANUFACTURER + space + Build.MODEL)) + "\n" + (getString(R.string.version) + colon + space + "Android" + space + Build.VERSION.RELEASE + space + leftParenthesis + ("API" + space + Build.VERSION.SDK_INT + rightParenthesis));

        leftSquareBracket = "[";
        rightSquareBracket = "]";

        dot = getString(R.string.dot);
        comma = getString(R.string.comma);
        decimalSeparators = new String[]{
                dot, comma
        };

        plus = getString(R.string.plus);
        minus = getString(R.string.minus);
        times = getString(R.string.times);
        division = getString(R.string.division);

        asterisk = "*";
        slash = "/";

        powerTwo = "²";
        powers = new String[]{
                powerTwo
        };

        infinity = "∞";

        setFragment(calculatorFragment);

        if (Build.VERSION.SDK_INT >= 21)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        else
            night(isNightEnabled);

        bottomNav();

        alerts();
        dialogs();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (preferences.getBoolean("isFirstTime", true)) {
            // Defines that, for now on, it isn't the first time the user's opening the app.
            preferencesEditor.putBoolean("isFirstTime", false)
                    .apply();

            dialogWelcome();
        }
    }

    String getDeviceInfo() {
        return deviceInfo;
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

    SharedPreferences.Editor getPreferencesEditor() {
        return preferencesEditor;
    }

    void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    String currentFragment() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();

        if (!fragments.isEmpty()) {
            String fragment = String.valueOf(fragments.get(fragments.size() - 1));
            String currentFragment = fragment.replace(fragment.substring(fragment.indexOf("{"), fragment.indexOf("}") + 1), empty);

            System.out.println("Current Fragment: " + currentFragment);
            return currentFragment;
        }

        return null;
    }

    void bottomNav() {
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.calculator:
                        setFragment(calculatorFragment);
                        break;
                    case R.id.settings:
                        setFragment(settingsFragment);
                        break;
                }

                return true;
            }
        });
    }

    boolean getIsNightEnabled() {
        return isNightEnabled;
    }

    void setIsNightEnabled(boolean isNightEnabled) {
        this.isNightEnabled = isNightEnabled;
        preferencesEditor.putBoolean("isNightEnabled", this.isNightEnabled)
                .apply();
    }

    /* Dialog getAlertSuccess() {
        return alertSuccess;
    }

    void setAlertSuccessIcon(int icon) {
        alertSuccessIcon.setBackgroundResource(icon);
    }

    void setAlertSuccessMessage(String message) {
        alertSuccessMessage.setText(message);
    }

    Dialog getAlertInfo() {
        return alertInfo;
    }

    void setAlertInfoIcon(int icon) {
        alertInfoIcon.setBackgroundResource(icon);
    }

    void setAlertInfoMessage(String message) {
        alertInfoMessage.setText(message);
    } */

    Dialog getAlertError() {
        return alertError;
    }

    /* void setAlertErrorIcon(int icon) {
        alertErrorIcon.setBackgroundResource(icon);
    }

    void setAlertErrorMessage(String message) {
        alertErrorMessage.setText(message);
    }

    Dialog getDialogBuyPro() {
        return dialogBuyPro;
    } */

    /* Dialog getDialogOK() {
        return dialogOK;
    } */

    void setDialogOKTitle(String title) {
        dialogOKTitle.setText(title);
    }

    void setDialogOKMessage(String message) {
        dialogOKMessage.setText(message);
    }

    /* void setDialogOKButtonOnClickListener(View.OnClickListener listener) {
        dialogOKButton.setOnClickListener(listener);
    }

    Dialog getDialogYesNo() {
        return dialogYesNo;
    } */

    void setDialogYesNoTitle(String title) {
        dialogYesNoTitle.setText(title);
    }

    void setDialogYesNoMessage(String message) {
        dialogYesNoMessage.setText(message);
    }

    void setDialogYesNoYesButtonOnClickListener(View.OnClickListener listener) {
        dialogYesNoYesButton.setOnClickListener(listener);
    }

    /* Dialog getDialogInput() {
        return dialogInput;
    }

    void setDialogInputTitle(String title) {
        dialogInputTitle.setText(title);
    }

    void setDialogInputMessage(String message) {
        dialogInputMessage.setText(message);
    }

    void setDialogInputButtonOnClickListener(View.OnClickListener listener) {
        dialogInputButton.setOnClickListener(listener);
    } */

    void setDialogInputFieldInputType(int type) {
        dialogInputField.setInputType(type);
    }

    /* String getDialogInputFieldText() {
        return dialogInputField.getText().toString();
    } */

    void setBounceInterpolatorConfig(double amplitude, double frequency) {
        this.bounceAmplitude = amplitude;
        this.bounceFrequency = frequency;

        bounceInterpolator = new BounceInterpolator(this.bounceAmplitude, this.bounceFrequency);
    }

    Animation getBounceOut() {
        Animation bounceOut = AnimationUtils.loadAnimation(this, R.anim.bounce_out);
        bounceOut.setInterpolator(bounceInterpolator);

        return bounceOut;
    }

    /* Timer getTimer() {
        return timer;
    } */

    String repeat(final String text, int times) {
        StringBuilder result = new StringBuilder(empty);

        for (int quantity = 0; quantity < times; quantity++)
            result = result.append(text);

        return result.toString();
    }

    String getLeftParenthesis() {
        return leftParenthesis;
    }

    String getRightParenthesis() {
        return rightParenthesis;
    }

    String getEmpty() {
        return empty;
    }

    String getSpace() {
        return space;
    }

    String getDot() {
        return dot;
    }

    String getComma() {
        return comma;
    }

    String getHyphen() {
        return hyphen;
    }

    String[] getDecimalSeparators() {
        return decimalSeparators;
    }

    String getPlus() {
        return plus;
    }

    String getMinus() {
        return minus;
    }

    String getTimes() {
        return times;
    }

    /* String getDivision() {
        return division;
    } */

    String getAsterisk() {
        return asterisk;
    }

    String getSlash() {
        return slash;
    }

    String getPowerTwo() {
        return powerTwo;
    }

    String[] getPowers() {
        return powers;
    }

    String getInfinity() {
        return infinity;
    }

    /* String screenSize() {
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
    } */

    private void alerts() {
        // Success alert (alert with a positive message) declaration.
        alertSuccess = new Dialog(this);

        // Info alert (alert with a neutral message) declaration.
        alertInfo = new Dialog(this);

        // Warning alert (alert with a "neutral-negative" message) declaration.
        alertWarning = new Dialog(this);

        // Error alert (alert with a negative message) declaration.
        alertError = new Dialog(this);

        alerts = new Dialog[] {
                alertSuccess, alertInfo, alertWarning, alertError
        };

        for (final Dialog alert: alerts) {
            Objects.requireNonNull(alert.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alert.requestWindowFeature(Window.FEATURE_NO_TITLE);

            if (alert.isShowing()) {
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        alert.dismiss();
                    }
                }, 3500);
            }
        }

        alertSuccess.setContentView(R.layout.alert_success);

        // alertSuccessIcon = alertSuccess.findViewById(R.id.icon);
        // alertSuccessMessage = alertSuccess.findViewById(R.id.message);

        alertInfo.setContentView(R.layout.alert_info);

        // alertInfoIcon = alertInfo.findViewById(R.id.icon);
        // alertInfoMessage = alertInfo.findViewById(R.id.message);

        alertWarning.setContentView(R.layout.alert_warning);

        // alertWarningIcon = alertWarning.findViewById(R.id.icon);
        // alertWarningMessage = alertWarning.findViewById(R.id.message);

        alertError.setContentView(R.layout.alert_error);

        // alertErrorIcon = alertError.findViewById(R.id.icon);
        // alertErrorMessage = alertError.findViewById(R.id.message);
    }

    private void dialogs() {
        // Buy Pro Dialog (dialog with a description of the existing features in Power Pro and a "buy" button) declaration.
        dialogBuyPro = new Dialog(this);

        // OK Dialog (dialog with an OK neutral button) declaration.
        dialogOK = new Dialog(this);

        // Yes/No Dialog (dialog with a positive Yes and a negative No buttons) declaration.
        dialogYesNo = new Dialog(this);

        // Input Dialog (dialog with a text field) declaration.
        dialogInput = new Dialog(this);

        dialogs = new Dialog[] {
                dialogBuyPro, dialogOK, dialogInput, dialogYesNo
        };

        for (final Dialog dialog: dialogs) {
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        // Dialog Buy Pro content.
        dialogBuyPro.setContentView(R.layout.dialog_buy_pro);

        dialogBuyProTitle = dialogBuyPro.findViewById(R.id.title);
        dialogBuyProTitle.setText(String.format(getString(R.string.app_plus), appName));

        dialogBuyProMessage = dialogBuyPro.findViewById(R.id.message);
        dialogBuyProMessage.setText(String.format(getString(R.string.buy_pro_features_description),
                getString(R.string.app_name),
                getString(R.string.hidden_mode),
                getString(R.string.conversions_all),
                getString(R.string.dark_mode),
                getString(R.string.ads_removal)));

        // dialogBuyProButton = dialogBuyPro.findViewById(R.id.buy);

        // Dialog OK content.
        dialogOK.setContentView(R.layout.dialog_ok);

        dialogOKTitle = dialogOK.findViewById(R.id.title);
        dialogOKMessage = dialogOK.findViewById(R.id.message);
        dialogOKButton = dialogOK.findViewById(R.id.ok);

        // Default OK Dialog button click listener, dismisses the dialog.
        dialogOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogOK.dismiss();
            }
        });

        // Dialog Yes/No content.
        dialogYesNo.setContentView(R.layout.dialog_yes_no);

        dialogYesNoTitle = dialogYesNo.findViewById(R.id.title);
        dialogYesNoMessage = dialogYesNo.findViewById(R.id.message);
        dialogYesNoNoButton = dialogYesNo.findViewById(R.id.no);
        dialogYesNoYesButton = dialogYesNo.findViewById(R.id.yes);

        // Default Yes/No dialog No button click listener, dismisses the dialog.
        dialogYesNoNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogYesNo.dismiss();
            }
        });

        // Dialog Input content.
        dialogInput.setContentView(R.layout.dialog_input);

        // dialogInputTitle = dialogInput.findViewById(R.id.title);
        // dialogInputMessage = dialogInput.findViewById(R.id.message);

        dialogInputField = dialogInput.findViewById(R.id.input);
        setDialogInputFieldInputType(InputType.TYPE_CLASS_TEXT);

        dialogInputButton = dialogInput.findViewById(R.id.ok);

        // Default Input Dialog button click listener, dismisses the dialog.
        dialogInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogInput.dismiss();
            }
        });
    }

    private void dialogWelcome() {
        setDialogOKTitle(String.format(getString(R.string.welcome_to_dialog_title), appName));
        setDialogOKMessage(String.format(getString(R.string.welcome_to_dialog_message), appName));

        dialogOK.show();

        if (Build.VERSION.SDK_INT >= 29) {
            dialogOK.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    setDialogYesNoTitle(getString(R.string.dark_mode));
                    setDialogYesNoMessage(String.format(getString(R.string.dark_mode_compatible_message), appName));

                    setDialogYesNoYesButtonOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            night(true);
                            dialogYesNo.dismiss();
                        }
                    });

                    dialogYesNo.show();
                }
            });
        }
    }

    private void night(boolean enableNight) {
        AppCompatDelegate.setDefaultNightMode(enableNight ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        setIsNightEnabled(enableNight);
    }

    void setNight(boolean enable) {
        night(enable);
    }

    boolean hasSquareBracket(String text) {
        return text.contains(leftSquareBracket) || text.contains(rightSquareBracket);
    }

    private String whichSquareBracket(String text) {
        String whichSquareBracket = null;

        if (hasSquareBracket(text)) {
            if (text.contains(leftSquareBracket))
                whichSquareBracket = "left";
            else if (text.contains(rightSquareBracket))
                whichSquareBracket = "right";
            else if (text.contains(leftSquareBracket) && text.contains(rightSquareBracket))
                whichSquareBracket = "both";
            else
                whichSquareBracket = null;
        }

        return whichSquareBracket;
    }

    String removeSquareBracket(String calc, int squareBracket) {
        switch (squareBracket) {
            case REMOVE_SQUARE_BRACKET_LEFT:
                calc = calc.replace(leftSquareBracket, empty);
                break;
            case REMOVE_SQUARE_BRACKET_RIGHT:
                calc = calc.replace(rightSquareBracket, empty);
                break;
            case REMOVE_SQUARE_BRACKET_ALL:
                calc = calc.replace(leftSquareBracket, empty);
                calc = calc.replace(rightSquareBracket, empty);

                break;
        }

        return calc;
    }

    /**
     * Applies a bounce in animation effect on a View, usually when it's being tapped.
     * @param view The View that's supposed to receive the bounce in animation.
     * @param customSetting Float values for the animation's amplitude and frequency, respectively, separated by a coma + space (", ").
     */
    void bounceIn(View view, String... customSetting) {
        if (!Arrays.toString(customSetting).isEmpty()) {
            if (Arrays.toString(customSetting).contains(getComma() + getSpace())) {
                String[] parts = Arrays.toString(customSetting).split(getComma() + getSpace());
                String formatParts = null;

                if (hasSquareBracket(Arrays.toString(parts))) {
                    switch (whichSquareBracket(Arrays.toString(parts))) {
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
                    if (formatFrequency.endsWith(rightSquareBracket + rightSquareBracket))
                        formatFrequency = formatFrequency.substring(0, formatFrequency.length() - 2);

                    bounceAmplitude = Double.parseDouble(formatAmplitude);
                    bounceFrequency = Double.parseDouble(formatFrequency);
                } else {
                    bounceAmplitude = Double.parseDouble(parts[1]);
                    bounceFrequency = Double.parseDouble(parts[2]);
                }

                setBounceInterpolatorConfig(bounceAmplitude, bounceFrequency);
            } else
                System.out.println("bounceInterpolator was incorrectly set and doesn't contain '" + getComma() + getSpace() + "'.");
        } else {
            customSetting = new String[] {
                    DEFAULT_BOUNCE_IN_SETTING
            };

            bounceIn(view, customSetting);
        }

        Animation bounceIn = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce_in);
        bounceInterpolator = new BounceInterpolator(bounceAmplitude, bounceFrequency);
        bounceIn.setInterpolator(bounceInterpolator);

        view.startAnimation(bounceIn);
    }

    @SuppressLint("ClickableViewAccessibility")
    void calculatorMode(final Context context, final ImageButton calculatorMode) {
        calculatorMode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(view, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        view.startAnimation(getBounceOut());

                        final PopupMenu calculatorModes;
                        MenuInflater inflater;

                        calculatorModes = new PopupMenu(context, calculatorMode);
                        inflater = calculatorModes.getMenuInflater();

                        inflater.inflate(R.menu.calculator_modes, calculatorModes.getMenu());
                        calculatorModes.show();

                        calculatorModes.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if (item.getTitle().equals(className.replace("Fragment", empty)))
                                    calculatorModes.dismiss();
                                else {
                                    switch (item.getTitleCondensed().toString()) {
                                        case "Calculator":
                                            setFragment(new CalculatorFragment());
                                            break;
                                        case "Length":
                                            setFragment(new LengthFragment());
                                            break;
                                        case "Time":
                                            setFragment(new TimeFragment());
                                            break;
                                        case "Temperature":
                                            setFragment(new TemperatureFragment());
                                            break;
                                    }
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

    @SuppressLint("ClickableViewAccessibility")
        // The units Button[] must contain all the unit buttons available, including the selected one.
    void selectUnit(Context context, final Button selectedUnit, final Button[] units) {
        int selectedUnitId = selectedUnit.getId();

        selectedUnit.setTextColor(isNightEnabled ? Color.BLACK : Color.WHITE);
        selectedUnit.setBackgroundResource(R.drawable.option_clicked);

        System.out.println("Selected \"convertFrom\" unit: " + selectedUnit.getText());

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

    @SuppressLint("ClickableViewAccessibility")
    void inputNumber(View view, final EditText input, final TextView conversionResult, final TextView conversionSymbolResult, final String calc) {
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                number = (Button) view;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(view, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        number.startAnimation(getBounceOut());

                        if (!calc.equals(getString(R.string.error))) {
                            input.append(number.getText());
                            System.out.println("Number '" + number.getText() + "' added.");
                        } else {
                            input.setText(empty);

                            number = (Button) view;
                            input.append(number.getText());

                            System.out.println("Number '" + number.getText() + "' added.");
                        }

                        calc(input, conversionResult, conversionSymbolResult);

                        break;
                }

                return true;
            }
        };

        for (int number: numbers)
            view.findViewById(number).setOnTouchListener(onTouchListener);
    }

    @SuppressLint("ClickableViewAccessibility")
    void delete(final EditText input, final ImageButton delete, final TextView conversionResult, final TextView conversionResultSymbol) {
        delete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                String calc = input.getText().toString();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(view, "0.5, 1");
                        break;
                    case MotionEvent.ACTION_UP:
                        delete.startAnimation(getBounceOut());
                        delete.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

                        if (!calc.isEmpty()) {
                            // When calc ends with " " (space), it means that it ends with an operator; the operator is shown as "space + operator + space". e. g., " × " and " + ". So, for example, if the input is "2 + 2", while deleting, the input would be equal to "2 + ", "2 +", "2 " and "2", respectively; with this method, the result should be "2 +" and "2", respectively.
                            input.setText(calc.endsWith(space) ? calc.substring(0, calc.length() - 2) : calc.substring(0, calc.length() - 1));

                            calc(input, conversionResult, conversionResultSymbol);
                        } else
                            conversionResult.setText(getString(R.string.zero));

                        break;
                }

                return true;
            }
        });
    }

    String convertFrom() {
        if (preferences.getString("convertTo", null) != null) {
            switch (currentFragment()) {
                case "LengthFragment":
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
                case "TimeFragment":
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
                case "TemperatureFragment":
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
            switch (currentFragment()) {
                case "LengthFragment":
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
                case "TimeFragment":
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
                case "TemperatureFragment":
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
            switch (currentFragment()) {
                case "LengthFragment":
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
                case "TimeFragment":
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
                case "TemperatureFragment":
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

    String reformatCalc(String calc) {
        if (calc != null) {
            if (calc.contains(space))
                calc = calc.replace(space, empty);

            if (calc.contains(times) || calc.contains(division)) {
                if (calc.contains(times))
                    calc = calc.replace(times, asterisk);

                if (calc.contains(division))
                    calc = calc.replace(division, slash);
            }

            if (hasSquareBracket(calc))
                removeSquareBracket(calc, REMOVE_SQUARE_BRACKET_ALL);

            System.out.println("Transformed calc: " + removeSquareBracket(calc, REMOVE_SQUARE_BRACKET_ALL));
        }

        return removeSquareBracket(calc, REMOVE_SQUARE_BRACKET_ALL);
    }

    /**
     * "Styles" the result that is shown to the user when pressing the equal button.
     *
     * @param input Represents the text field itself.
     * @param conversionResult Conversion result of the input keypad_button'.
     */
    void inputFormat(EditText input, TextView conversionResult) {
        if (conversionResult.getText().toString().contains(".")) {
            conversionResult.setText(format.format(Double.parseDouble(conversionResult.getText().toString())));
        }

        if (conversionResult.getText().toString().endsWith(".0"))
            conversionResult.setText(input.getText().toString().replace(".0", ""));

        if (conversionResult.getText().toString().endsWith("E"))
            conversionResult.setText(input.getText().toString().replace("E", "e"));
    }
}