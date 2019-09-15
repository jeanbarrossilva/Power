package com.jeanbarrossilva.power;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.text.InputType;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Objects;
import java.util.Timer;

import static android.util.DisplayMetrics.DENSITY_HIGH;
import static android.util.DisplayMetrics.DENSITY_LOW;
import static android.util.DisplayMetrics.DENSITY_MEDIUM;
import static android.util.DisplayMetrics.DENSITY_XHIGH;
import static android.util.DisplayMetrics.DENSITY_XXHIGH;
import static android.util.DisplayMetrics.DENSITY_XXXHIGH;

@SuppressWarnings({"FieldCanBeLocal"})
public class MainActivity extends AppCompatActivity {
    private boolean isAuthentic;
    String installerPackage;

    String deviceName;
    private String deviceInfo;

    private ConnectivityManager connectivityManager;
    private NetworkInfo[] networkInfo;

    private boolean isConnected;
    private boolean isConnectedWiFi;
    private boolean isConnectedMobile;

    private FirebaseDatabase database;

    private String appName;
    private String versionName;

    private SharedPreferences preferences;
    private SharedPreferences.Editor preferencesEditor;

    private boolean didSubscribeToProAvailability;
    private int proAvailabilityInterestSubscriptions;

    private boolean isHiddenModeUnlocked;
    private boolean isHiddenModeEnabled;

    private boolean isNightUnlocked;
    private boolean isNightEnabled;

    private Dialog alertSuccess;
    private View alertSuccessIcon;
    private TextView alertSuccessMessage;

    private Dialog[] alerts;

    private Dialog alertInfo;
    private View alertInfoIcon;
    private TextView alertInfoMessage;

    private Dialog alertError;
    private View alertErrorIcon;
    private TextView alertErrorMessage;

    private Dialog dialogOK;
    private TextView dialogOKTitle;
    private TextView dialogOKMessage;
    private Button dialogOKButton;

    private Dialog dialogUnauthentic;

    private Dialog dialogYesNo;
    private TextView dialogYesNoTitle;
    private TextView dialogYesNoMessage;
    private Button dialogYesNoNoButton;
    private Button dialogYesNoYesButton;

    private Dialog dialogInput;
    private TextView dialogInputTitle;
    private TextView dialogInputMessage;
    private EditText dialogInputField;
    private Button dialogInputButton;

    private String pin;

    private BounceInterpolator bounceInterpolator;
    private Animation bounceIn;

    private DecimalFormat format;

    private String className;
    private String unit;

    private Timer timer;

    private double bounceAmplitude;
    private double bounceFrequency;

    static final String SECRET_CODE_LEVEL_1 = "081003";
    static final String SECRET_CODE_LEVEL_2 = "120318";

    static final String DEFAULT_BOUNCE_IN_SETTING = "0.5, 5";

    private String empty;
    private String space;

    private Button number;
    private final int[] numbers = {
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

    private String leftSquareBracket;
    private String rightSquareBracket;

    String dot;
    private String comma;
    private String[] decimalSeparators;

    private String plus;
    private String minus;
    private String times;
    private String division;

    private String asterisk;
    private String slash;
    private String colon;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            isAuthentic = authenticity();
        } catch (Exception exception) {
            isAuthentic = false;
            System.out.println("Couldn't verify app authenticity.");
        }

        deviceName = Settings.Secure.getString(getContentResolver(), "bluetooth_name");
        deviceInfo = getString(R.string.name) + deviceName + "\n" + getString(R.string.device_model) + colon + space + (Build.MANUFACTURER + space + Build.MODEL);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getAllNetworkInfo();

        isConnected = isConnectedWiFi || isConnectedMobile;
        isConnectedWiFi = connectionWiFi();
        isConnectedMobile = connectionMobile();

        database = FirebaseDatabase.getInstance();

        appName = getString(R.string.app_name);
        versionName = BuildConfig.VERSION_NAME;

        preferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        preferencesEditor = preferences.edit();

        didSubscribeToProAvailability = preferences.getBoolean("didSubscribeToProAvailability", false);

        isHiddenModeUnlocked = preferences.getBoolean("isHiddenModeUnlocked", false);
        isHiddenModeEnabled = preferences.getBoolean("isHiddenModeEnabled", false);

        isNightUnlocked = preferences.getBoolean("isNightUnlocked", false);
        isNightEnabled = preferences.getBoolean("isNightEnabled", false);

        alerts = new Dialog[] {
                alertSuccess, alertInfo, alertError
        };

        pin = preferences.getString("pin", null);

        bounceInterpolator = new BounceInterpolator(bounceAmplitude, bounceFrequency);

        bounceIn = AnimationUtils.loadAnimation(this, R.anim.bounce_in);
        bounceIn.setInterpolator(bounceInterpolator);

        format = new DecimalFormat("#.##");

        className = getLocalClassName();
        unit = null;

        timer = new Timer();

        empty = "";
        space = " ";

        leftSquareBracket = "[";
        rightSquareBracket = "]";

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
        colon = ":";

        alerts();
        dialogs();
    }

    boolean authenticity() {
        installerPackage = getPackageManager().getInstallerPackageName(getPackageName());
        return installerPackage == null;
    }

    boolean getIsAuthentic() {
        return isAuthentic;
    }

    void unauthentic() {
        dialogUnauthentic.show();
    }

    void unauthentic(Switch setting) {
        setting.setChecked(false);
        dialogUnauthentic.show();
    }

    void showStatusBar() {
        getWindow().clearFlags(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    void hideStatusBar() {
        getWindow().addFlags(View.SYSTEM_UI_FLAG_FULLSCREEN);
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

    boolean getIsConnected() {
        return isConnected;
    }

    SharedPreferences getPreferences() {
        return preferences;
    }

    SharedPreferences.Editor getPreferencesEditor() {
        return preferencesEditor;
    }

    boolean getDidSubscribeToProAvailability() {
        return didSubscribeToProAvailability;
    }

    void setDidSubscribeToProAvailability(boolean didSubscribe) {
        didSubscribeToProAvailability = didSubscribe;
        preferencesEditor.putBoolean("didSubscribeToProAvailability", didSubscribeToProAvailability)
                .apply();
    }

    void addProAvailabilityInterestSubscription() {
        proAvailabilityInterestSubscriptions = + 1;
        database.getReference().child("proAvailabilityInterestSubscriptions").setValue(proAvailabilityInterestSubscriptions);
    }

    boolean getIsHiddenModeUnlocked() {
        return isHiddenModeUnlocked;
    }

    void setIsHiddenModeUnlocked(boolean isUnlocked) {
        isHiddenModeUnlocked = isUnlocked;
        preferencesEditor.putBoolean("isHiddenModeUnlocked", isHiddenModeUnlocked)
                .apply();
    }

    boolean getIsHiddenModeEnabled() {
        return isHiddenModeEnabled;
    }

    void setIsHiddenModeEnabled(boolean isHiddenModeEnabled) {
        this.isHiddenModeEnabled = isHiddenModeEnabled;
        preferencesEditor.putBoolean("isHiddenModeEnabled", this.isHiddenModeEnabled)
                .apply();
    }

    boolean getIsNightUnlocked() {
        return isNightUnlocked;
    }

    void setIsNightUnlocked(boolean isUnlocked) {
        isNightUnlocked = isUnlocked;
        preferencesEditor.putBoolean("isNightUnlocked", isNightUnlocked)
                .apply();
    }

    boolean getIsNightEnabled() {
        return isNightEnabled;
    }

    void setIsNightEnabled(boolean isNightEnabled) {
        this.isNightEnabled = isNightEnabled;
        preferencesEditor.putBoolean("isNightEnabled", this.isNightEnabled)
                .apply();
    }

    Dialog getAlertSuccess() {
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
    }

    Dialog getAlertError() {
        return alertError;
    }

    void setAlertErrorIcon(int icon) {
        alertErrorIcon.setBackgroundResource(icon);
    }

    void setAlertErrorMessage(String message) {
        alertErrorMessage.setText(message);
    }

    Dialog getDialogOK() {
        return dialogOK;
    }

    void setDialogOKTitle(String title) {
        dialogOKTitle.setText(title);
    }

    void setDialogOKMessage(String message) {
        dialogOKMessage.setText(message);
    }

    void setDialogOKButtonOnClickListener(View.OnClickListener listener) {
        dialogOKButton.setOnClickListener(listener);
    }

    Dialog getDialogYesNo() {
        return dialogYesNo;
    }

    void setDialogYesNoTitle(String title) {
        dialogYesNoTitle.setText(title);
    }

    void setDialogYesNoMessage(String message) {
        dialogYesNoMessage.setText(message);
    }

    void setDialogYesNoYesButtonOnClickListener(View.OnClickListener listener) {
        dialogYesNoYesButton.setOnClickListener(listener);
    }

    Dialog getDialogInput() {
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
    }

    void setDialogInputFieldInputType(int type) {
        dialogInputField.setInputType(type);
    }

    String getDialogInputFieldText() {
        return dialogInputField.getText().toString();
    }

    String getPin() {
        return pin;
    }

    void setBounceInterpolatorConfig(double amplitude, double frequency) {
        this.bounceAmplitude = amplitude;
        this.bounceFrequency = frequency;

        bounceInterpolator = new BounceInterpolator(this.bounceAmplitude, this.bounceFrequency);
    }

    Animation getBounceOut() {
        Animation bounceOut = AnimationUtils.loadAnimation(this, R.anim.bounce_out);

        bounceInterpolator = new BounceInterpolator(bounceAmplitude, bounceFrequency);
        bounceOut.setInterpolator(bounceInterpolator);

        return bounceOut;
    }

    void setPin(String pin) {
        this.pin = pin;
        preferencesEditor.putString("pin", this.pin)
                .apply();
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

    String[] getNumbersArray() {
        return numbersArray;
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

    String screenSize() {
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

    private boolean connectionWiFi() {
        for (NetworkInfo info: networkInfo) {
            return info.getTypeName().equalsIgnoreCase("WIFI");
        }

        return false;
    }

    private boolean connectionMobile() {
        for (NetworkInfo info: networkInfo) {
            return info.getTypeName().equalsIgnoreCase("MOBILE");
        }

        return false;
    }

    private void alerts() {
        // Success alert (alert with a positive message) declaration.
        alertSuccess = new Dialog(this);
        Objects.requireNonNull(alertSuccess.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertSuccess.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertSuccess.setContentView(R.layout.alert_success);

        alertSuccessIcon = alertSuccess.findViewById(R.id.icon);
        alertSuccessMessage = alertSuccess.findViewById(R.id.message);

        // Info alert (alert with a neutral message) declaration.
        alertInfo = new Dialog(this);
        Objects.requireNonNull(alertInfo.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertInfo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertInfo.setContentView(R.layout.alert_info);

        alertInfoIcon = alertInfo.findViewById(R.id.icon);
        alertInfoMessage = alertInfo.findViewById(R.id.message);

        // Error alert (alert with a negative message) declaration.
        alertError = new Dialog(this);
        Objects.requireNonNull(alertError.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertError.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertError.setContentView(R.layout.alert_error);

        alertErrorIcon = alertError.findViewById(R.id.icon);
        alertErrorMessage = alertError.findViewById(R.id.message);
    }

    private void dialogs() {
        // OK Dialog (dialog with an OK neutral button) declaration.
        dialogOK = new Dialog(this);
        Objects.requireNonNull(dialogOK.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogOK.requestWindowFeature(Window.FEATURE_NO_TITLE);
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

        // Unauthentic Dialog (an OK Dialog, but with a link to Google Play Store) declaration.
        dialogUnauthentic = new Dialog(this);
        Objects.requireNonNull(dialogUnauthentic.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogUnauthentic.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogUnauthentic.setContentView(R.layout.dialog_unauthentic);

        Button dialogUnauthenticButton = dialogUnauthentic.findViewById(R.id.ok);

        // Default Unauthentic Dialog button click listener, opens Google Play.
        dialogUnauthenticButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent power = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.jeanbarrossilva.power"));
                startActivity(power);

                dialogUnauthentic.dismiss();
            }
        });

        // Yes/No Dialog (dialog with a positive Yes and a negative No buttons) declaration.
        dialogYesNo = new Dialog(this);
        Objects.requireNonNull(dialogYesNo.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogYesNo.requestWindowFeature(Window.FEATURE_NO_TITLE);
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

        // Input Dialog (dialog with a text field) declaration.
        dialogInput = new Dialog(this);
        Objects.requireNonNull(dialogInput.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogInput.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogInput.setContentView(R.layout.dialog_input);

        dialogInputTitle = dialogInput.findViewById(R.id.title);
        dialogInputMessage = dialogInput.findViewById(R.id.message);

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

    private void night(boolean enableNight) {
        if (enableNight) {
            setIsNightEnabled(true);

            if (Build.VERSION.SDK_INT < 21) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                getDelegate().applyDayNight();
            }

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            System.out.println("Night mode has been enabled.");
        } else {
            setIsNightEnabled(false);

            if (Build.VERSION.SDK_INT < 21) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                getDelegate().applyDayNight();
            }

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            System.out.println("Night mode has been disabled.");
        }
    }

    void setNight(boolean enableNight) {
        night(enableNight);
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

    // Meant to be implemented into a View.OnTouchListener's MotionEvent.ACTION_DOWN as 'bounceIn(view, "amplitude, frequency")'. e. g., bounceIn(number, DEFAULT_BOUNCE_IN_SETTING).
    void bounceIn(View view, String... customSetting) {
        if (!Arrays.toString(customSetting).isEmpty()) {
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

                    bounceAmplitude = Double.parseDouble(formatAmplitude);
                    bounceFrequency = Double.parseDouble(formatFrequency);
                } else {
                    bounceAmplitude = Double.parseDouble(parts[1]);
                    bounceFrequency = Double.parseDouble(parts[2]);
                }

                setBounceInterpolatorConfig(bounceAmplitude, bounceFrequency);
            } else {
                System.out.println("bounceInterpolator was incorrectly set and doesn't contain '" + getComma() + getSpace() + "'.");
            }
        }

        bounceInterpolator = new BounceInterpolator(bounceAmplitude, bounceFrequency);
        bounceIn.setInterpolator(bounceInterpolator);

        view.startAnimation(bounceIn);

        if (String.valueOf(bounceAmplitude).endsWith(".0")) {
            String amplitudeString = String.valueOf(bounceAmplitude).replace(".0", getEmpty());
            System.out.println("Amplitude: " + amplitudeString);
        } else {
            System.out.println("Amplitude: " + bounceAmplitude);
        }

        if (String.valueOf(bounceFrequency).endsWith(".0")) {
            String frequencyString = String.valueOf(bounceFrequency).replace(".0", getEmpty());
            System.out.println("Frequency: " + frequencyString);
        } else {
            System.out.println("Frequency: " + bounceFrequency);
        }
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
                // Replaces " × " by "*".
                calc = calc.replace(space + times + space, asterisk);
            }

            if (calc.contains(division)) {
                // Replaces " ÷ " by "/".
                calc = calc.replace(space + division + space, slash);
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

    /**
     * "Styles" the result that is shown to the user when pressing the equal button.
     *
     * @param input Represents the text field itself.
     * @param conversionResult Conversion result of the input number'.
     */
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

    /**
     * Checks if a text field has reached its character length limit. Here, it is defined based on the device screen density, where for 'ldpi', 'mdpi' and 'hdpi' it equals 17 and for 'xhdpi', 'xxhdpi', 'xxxhdpi' it also equals 17, but has a different behavior, as shown below.
     *
     * @param input Represents the text field itself.
     * @param calc String from the text field, regularly referred as 'input.getText().toString()'.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean inputHasReachedCharLimit(final EditText input, final String calc) {
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

    /**
     * Inputs a number in a text field.
     *
     * @param input Represents the text field itself.
     * @param calc String from the text field, regularly referred as 'input.getText().toString()'.
     */
    @SuppressLint("ClickableViewAccessibility")
    void inputNumber(final EditText input, final String calc) {
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
                            if (!inputHasReachedCharLimit(input, calc)) {
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

                        break;
                }

                return true;
            }
        };

        for (int number: numbers) {
            findViewById(number).setOnTouchListener(onTouchListener);
        }
    }

    void inputNumber(final EditText input, final TextView conversionResult, final TextView conversionSymbolResult, final String calc) {
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
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
                            if (!inputHasReachedCharLimit(input, calc)) {
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

                        calc(input, conversionResult, conversionSymbolResult);

                        break;
                }

                return true;
            }
        };

        for (int number: numbers) {
            findViewById(number).setOnTouchListener(onTouchListener);
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
                        bounceIn(view, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        decimalSeparator.startAnimation(getBounceOut());

                        if (!inputHasReachedCharLimit(input, calc)) {
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
    void inputOperator(final EditText input, final String calc) {
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                operator = (Button) view;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(view, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        operator.startAnimation(getBounceOut());

                        if (operator.getText().toString().equals(getPlus()) || operator.getText().toString().equals(getMinus())) {
                            if (!inputHasReachedCharLimit(input, calc)) {
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
                                if (!inputHasReachedCharLimit(input, calc)) {
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
    void inputParenthesis(final EditText input, final String calc) {
        View.OnTouchListener listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                parenthesis = (Button) view;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(view, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        parenthesis.startAnimation(getBounceOut());

                        if (!inputHasReachedCharLimit(input, calc)) {
                            input.append(parenthesis.getText());

                            System.out.println("Parenthesis added.");
                            System.out.println("Updated 'calc' value: " + calc);
                        }

                        break;
                }

                return true;
            }
        };

        for (int parenthesis: parenthesisArray) {
            findViewById(parenthesis).setOnTouchListener(listener);
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
                        bounceIn(view, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        delete.startAnimation(getBounceOut());
                        delete.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

                        if (!calc.isEmpty()) {
                            // When calc ends with " " (getSpace()), it means that it ends with an operator; the operator is shown as "getSpace() + operator + getSpace()". e. g., " × " and " + ". So, for example, if the input is "2 + 2", while deleting, the input would be equal to "2 + ", "2 +", "2 " and "2", respectively; with this method, the result would be "2 +" and "2", respectively.
                            if (calc.endsWith(getSpace())) {
                                input.setText(calc.substring(0, calc.length() - 2));

                                for (String number: getNumbersArray()) {
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