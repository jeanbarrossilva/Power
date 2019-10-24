package com.jeanbarrossilva.power;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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

import com.freshchat.consumer.sdk.Freshchat;
import com.freshchat.consumer.sdk.FreshchatConfig;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("SameParameterValue")
public class MainActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private SharedPreferences.Editor preferencesEditor;

    static final int ERROR = -1;

    private String appName;
    private String versionName;

    private String deviceInfoName;
    private String deviceInfo;

    private boolean shareUsageData;

    private CalculatorFragment calculatorFragment;

    private boolean isNightEnabled;

    private Dialog alertError;

    // private Button dialogBuyProButton;

    private Dialog dialogFeedback;
    private ConstraintLayout dialogFeedbackEmailButton;

    private Dialog dialogOK;
    private TextView dialogOKTitle;
    private TextView dialogOKMessage;

    private Dialog dialogYesNo;
    private TextView dialogYesNoTitle;
    private TextView dialogYesNoMessage;
    private Button dialogYesNoYesButton;
    private Button dialogYesNoNoButton;

    private Dialog dialogInput;
    private EditText dialogInputField;

    private BounceInterpolator bounceInterpolator;

    private DecimalFormat format;

    private String className;

    private Timer timer;
    private String unit;

    static final int LAST_PARENTHESIS_LEFT = 0;
    static final int LAST_PARENTHESIS_RIGHT = 1;

    static final int REMOVE_SQUARE_BRACKET_LEFT = 0;
    static final int REMOVE_SQUARE_BRACKET_RIGHT = 1;
    static final int REMOVE_SQUARE_BRACKET_ALL = 2;

    private Button number;
    final int[] numbers = {
            R.id.zero, R.id.one, R.id.two, R.id.three, R.id.four, R.id.five, R.id.six, R.id.seven, R.id.eight, R.id.nine
    };

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        preferencesEditor = preferences.edit();

        String deviceName = Settings.Secure.getString(getContentResolver(), "bluetooth_name");

        appName = getString(R.string.app_name);
        versionName = BuildConfig.VERSION_NAME;

        calculatorFragment = new CalculatorFragment();

        isNightEnabled = preferences.getBoolean("isNightEnabled", false);

        format = new DecimalFormat("#.##");

        className = getLocalClassName();

        timer = new Timer();

        deviceInfoName = (getString(R.string.name) + StringUtils.Punctuation.COLON + StringUtils.SPACE + deviceName);
        deviceInfo = (getString(R.string.device_model) + StringUtils.Punctuation.COLON + StringUtils.SPACE + (Build.MANUFACTURER + StringUtils.SPACE + Build.MODEL)) + "\n" + (getString(R.string.so_version) + StringUtils.Punctuation.COLON + StringUtils.SPACE + "Android" + StringUtils.SPACE + Build.VERSION.RELEASE + StringUtils.SPACE + StringUtils.Punctuation.LEFT_PARENTHESIS + ("API" + StringUtils.SPACE + Build.VERSION.SDK_INT) + StringUtils.Punctuation.RIGHT_PARENTHESIS);

        setFragment(calculatorFragment);

        if (Build.VERSION.SDK_INT >= 21)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        else
            night(isNightEnabled);

        bottomNav();

        alerts();
        dialogs();

        layoutInfo();
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

    private void layoutInfo() {
        ConstraintLayout layoutInfo = findViewById(R.id.layout_info);
        TextView layoutTitle = findViewById(R.id.layout_title);

        try {
            if (currentFragment().equals("CalculatorFragment"))
                layoutInfo.setVisibility(View.GONE);
            else {
                layoutInfo.setVisibility(View.VISIBLE);
                layoutTitle.setText(currentFragment().replace("Fragment", StringUtils.EMPTY));
            }
        } catch (NullPointerException exception) {
            setContentView(R.layout.blank);

            setDialogYesNoTitle(getString(R.string.error));
            setDialogYesNoMessage(getString(R.string.load_layout_title_error));

            setDialogYesNoYesButtonTitle(getString(R.string.contact));
            setDialogYesNoNoButtonTitle(getString(R.string.cancel_close));

            setDialogYesNoYesButtonOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogFeedbackEmailButton.callOnClick();
                }
            });

            setDialogYesNoNoButtonOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.exit(0);
                }
            });

            dialogYesNo.show();
        }
    }

    boolean getShareUsageData() {
        return shareUsageData;
    }

    void setShareUsageData(boolean share) {
        preferencesEditor.putBoolean("shareUsageData", share)
                .apply();

        shareUsageData = share;

        System.out.println("\"shareUsageData\": " + shareUsageData);
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

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    private String currentFragment() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();

        if (!fragments.isEmpty()) {
            String fragment = String.valueOf(fragments.get(fragments.size() - 1));
            String currentFragment = fragment.replace(fragment.substring(fragment.indexOf("{"), fragment.indexOf("}") + 1), StringUtils.EMPTY);

            if (shareUsageData)
                System.out.println("Current Fragment:" + StringUtils.SPACE + currentFragment);

            return currentFragment;
        }

        return StringUtils.EMPTY;
    }

    private void bottomNav() {
        final HistoryFragment historyFragment = new HistoryFragment();
        final SettingsFragment settingsFragment = new SettingsFragment();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.calculator:
                        setFragment(calculatorFragment);
                        break;
                    case R.id.history:
                        setFragment(historyFragment);
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

    private void setIsNightEnabled(boolean isNightEnabled) {
        this.isNightEnabled = isNightEnabled;
        preferencesEditor.putBoolean("isNightEnabled", this.isNightEnabled)
                .apply();
    }

    Dialog getAlertError() {
        return alertError;
    }

    private void setDialogOKTitle(String title) {
        dialogOKTitle.setText(title);
    }

    private void setDialogOKMessage(String message) {
        dialogOKMessage.setText(message);
    }

    Dialog getDialogFeedback() {
        return dialogFeedback;
    }

    private void setDialogYesNoTitle(String title) {
        dialogYesNoTitle.setText(title);
    }

    private void setDialogYesNoMessage(String message) {
        dialogYesNoMessage.setText(message);
    }

    private void setDialogYesNoYesButtonOnClickListener(View.OnClickListener listener) {
        dialogYesNoYesButton.setOnClickListener(listener);
    }

    private void setDialogYesNoYesButtonTitle(String title) {
        dialogYesNoYesButton.setText(title);
    }

    private void setDialogYesNoNoButtonTitle(String title) {
        dialogYesNoNoButton.setText(title);
    }

    private void setDialogYesNoNoButtonOnClickListener(View.OnClickListener listener) {
        dialogYesNoNoButton.setOnClickListener(listener);
    }

    private void setDialogInputFieldInputType(int type) {
        dialogInputField.setInputType(type);
    }

    Animation getBounceOut() {
        Animation bounceOut = AnimationUtils.loadAnimation(this, R.anim.bounce_out);
        bounceOut.setInterpolator(bounceInterpolator);

        return bounceOut;
    }

    private void alerts() {
        // Success alert (alert with a positive message) declaration.
        Dialog alertSuccess = new Dialog(this);

        // Info alert (alert with a neutral message) declaration.
        Dialog alertInfo = new Dialog(this);

        // Warning alert (alert with a "neutral-negative" message) declaration.
        Dialog alertWarning = new Dialog(this);

        // Error alert (alert with a negative message) declaration.
        alertError = new Dialog(this);

        Dialog[] alerts = new Dialog[]{
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
        alertInfo.setContentView(R.layout.alert_info);
        alertWarning.setContentView(R.layout.alert_warning);
        alertError.setContentView(R.layout.alert_error);
    }

    private void dialogs() {
        // Buy Pro Dialog (dialog with a description of the existing features in Power Pro and a "buy" button) declaration.
        Dialog dialogBuyPro = new Dialog(this);

        // Feedback Dialog (dialog with a "list" of feedback options) declaration.
        dialogFeedback = new Dialog(this);

        // OK Dialog (dialog with an OK neutral button) declaration.
        dialogOK = new Dialog(this);

        // Yes/No Dialog (dialog with a positive Yes and a negative No buttons) declaration.
        dialogYesNo = new Dialog(this);

        // Input Dialog (dialog with a text field) declaration.
        dialogInput = new Dialog(this);

        Dialog[] dialogs = new Dialog[]{
                dialogBuyPro, dialogFeedback, dialogOK, dialogInput, dialogYesNo
        };

        for (final Dialog dialog: dialogs) {
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        // Dialog Buy Pro content.
        dialogBuyPro.setContentView(R.layout.dialog_buy_pro);

        TextView dialogBuyProTitle = dialogBuyPro.findViewById(R.id.title);
        dialogBuyProTitle.setText(String.format(getString(R.string.app_plus), appName));

        TextView dialogBuyProMessage = dialogBuyPro.findViewById(R.id.message);
        dialogBuyProMessage.setText(String.format(getString(R.string.buy_pro_features_description),
                getString(R.string.app_name),
                getString(R.string.conversions_all),
                getString(R.string.dark_mode),
                getString(R.string.ads_removal)));

        // Dialog Feedback content.
        dialogFeedback.setContentView(R.layout.dialog_feedback);

        ConstraintLayout dialogFeedbackReviewButton = dialogFeedback.findViewById(R.id.review);
        ConstraintLayout dialogFeedbackChatButton = dialogFeedback.findViewById(R.id.chat);
        ConstraintLayout dialogFeedbackTweetDirectMessageButton = dialogFeedback.findViewById(R.id.tweet_direct_message);
        dialogFeedbackEmailButton = dialogFeedback.findViewById(R.id.email);
        Button dialogFeedbackCancelButton = dialogFeedback.findViewById(R.id.cancel);

        // Default Dialog Feedback Review button click listener, opens Google Play Store.
        dialogFeedbackReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
            }
        });

        dialogFeedbackChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FreshchatConfig freshchatConfig = new FreshchatConfig("57b32ce0-60a5-4067-b601-288016f4be5e", "75534fd8-ed19-4107-9b54-6b5a9bf811a4");

                freshchatConfig.setGallerySelectionEnabled(true);

                Freshchat.getInstance(MainActivity.this).init(freshchatConfig);
                Freshchat.showConversations(MainActivity.this);
            }
        });

        // Default Dialog Feedback Tweet or Direct Message listener, opens the developer's Twitter profile.
        dialogFeedbackTweetDirectMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/jeanbarrossilva")));
            }
        });

        // Default Dialog Feedback E-mail button click listener, composes a new e-mail message.
        dialogFeedbackEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent email = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:jeanbarrossilva@outlook.com"));

                email.putExtra(Intent.EXTRA_SUBJECT, String.format(getString(R.string.send_feedback_email_subject), appName, versionName));

                deviceInfo = shareUsageData ? deviceInfoName + "\n" + deviceInfo : deviceInfo;
                email.putExtra(Intent.EXTRA_TEXT, "\n\n" + FormatUtils.Companion.repeat(StringUtils.Punctuation.HYPHEN, 50) + "\n\n" + deviceInfo);

                startActivity(Intent.createChooser(email, getString(R.string.send_feedback)));
            }
        });

        dialogFeedbackCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFeedback.dismiss();
            }
        });

        // Dialog OK content.
        dialogOK.setContentView(R.layout.dialog_ok);

        dialogOKTitle = dialogOK.findViewById(R.id.title);
        dialogOKMessage = dialogOK.findViewById(R.id.message);
        Button dialogOKButton = dialogOK.findViewById(R.id.ok);

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

        dialogInputField = dialogInput.findViewById(R.id.input);
        setDialogInputFieldInputType(InputType.TYPE_CLASS_TEXT);

        Button dialogInputButton = dialogInput.findViewById(R.id.ok);

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

    void bounceIn(View view, double amplitude, double frequency) {
        bounceInterpolator = new BounceInterpolator(amplitude, frequency);

        Animation bounceIn = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce_in);
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
                        bounceIn(view, 0.35, 1);
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
                                if (item.getTitle().equals(className.replace("Fragment", StringUtils.EMPTY)))
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
    void selectButton(final Button selected, final Button[] units, int background) {
        int selectedId = selected.getId();

        selected.setTextColor(isNightEnabled ? Color.BLACK : Color.WHITE);
        selected.setBackgroundResource(background);

        for (Button unit: units) {
            if (unit.getId() != selectedId) try {
                unit.setTextColor(isNightEnabled ? Color.WHITE : Color.BLACK);
                unit.setBackgroundResource(R.drawable.option);
            } catch (NullPointerException nullPointerException) {
                alertError.show();
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
                        bounceIn(view, 0.5, 5);
                        break;
                    case MotionEvent.ACTION_UP:
                        number.startAnimation(getBounceOut());

                        if (!calc.equals(getString(R.string.error))) {
                            input.append(number.getText());
                        } else {
                            input.setText(StringUtils.EMPTY);

                            number = (Button) view;
                            input.append(number.getText());
                        }

                        if (shareUsageData)
                            System.out.println("Number '" + number.getText() + "' added.");

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
                        bounceIn(view, 0.5, 1);
                        break;
                    case MotionEvent.ACTION_UP:
                        delete.startAnimation(getBounceOut());
                        delete.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

                        if (!calc.isEmpty()) {
                            // When calc ends with " " (StringUtils.SPACE), it means that it ends with an operator; the operator is shown as "StringUtils.SPACE + operator + StringUtils.SPACE". e. g., " × " and " + ". So, for example, if the input is "2 + 2", while deleting, the input would be equal to "2 + ", "2 +", "2 " and "2", respectively; with this method, the result should be "2 +" and "2", respectively.
                            input.setText(calc.endsWith(StringUtils.SPACE) ? calc.substring(0, calc.length() - 2) : calc.substring(0, calc.length() - 1));

                            calc(input, conversionResult, conversionResultSymbol);
                        }

                        input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (String.valueOf(s).isEmpty())
                                conversionResult.setText(getString(R.string.zero));
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (String.valueOf(s).isEmpty())
                                conversionResult.setText(getString(R.string.zero));
                        }
                    });

                        break;
                }

                return true;
            }
        });
    }

    private String convertFrom() {
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
                        case "mile":
                            unit = "mile";
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
                        case "rankine":
                            unit = "rankine";
                            break;
                        case "reaumur":
                            unit = "reaumur";
                            break;
                    }

                    break;
            }
        }

        return unit;
    }

    private String convertTo() {
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
                        case "mile":
                            unit = "mile";
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
                        case "rankine":
                            unit = "rankine";
                            break;
                        case "reaumur":
                            unit = "reaumur";
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
                                case "mile":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * Math.pow(5.789, 12)));
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
                                case "mile":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 1.609));
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
                                case "mile":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 16.093));
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
                                case "mile":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 160.934));
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

                            break;
                        case "mile":
                            switch (convertTo()) {
                                case "lightYear":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / Math.pow(5.879, 12)));
                                    break;
                                case "kilometer":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 1.609));
                                    break;
                                case "hectometer":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 16.093));
                                    break;
                                case "decameter":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 160.934));
                                    break;
                                case "mile":
                                    conversionResult.setText(input.getText().toString());
                                    break;
                                case "meter":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 1609.344));
                                    break;
                                case "decimeter":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 16093.44));
                                    break;
                                case "centimeter":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 160934.4));
                                    break;
                                case "millimeter":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * Math.pow(1.609, 6)));
                                    break;
                                case "micrometer":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * Math.pow(1.609, 9)));
                                    break;
                            }

                            break;
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
                                case "mile":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 1609.344));
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
                                case "mile":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 16093.44));
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
                                case "mile":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / 160934.4));
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
                                case "mile":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / Math.pow(1.609, 6)));
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

                            break;
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
                                case "mile":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) / Math.pow(1.609, 9)));
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

                            break;
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
                                case "rankine":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 9 / 5 + 491.67));
                                    break;
                                case "reaumur":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 5 / 4));
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
                                case "rankine":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) + 459.67));
                                    break;
                                case "reaumur":
                                    conversionResult.setText(String.valueOf((Double.valueOf(input.getText().toString()) - 32) * 4 / 9));
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
                                case "rankine":
                                    conversionResult.setText(String.valueOf(Double.valueOf(input.getText().toString()) * 1.8));
                                    break;
                                case "reaumur":
                                    conversionResult.setText(String.valueOf((Double.valueOf(input.getText().toString()) - 273.15) * 4 / 5));
                                    break;
                            }

                            break;
                        case "rankine":
                            switch (convertTo()) {
                                case "celsius":
                                    conversionResult.setText(String.valueOf((Double.parseDouble(input.getText().toString()) - 491.67) * 5 / 9));
                                    break;
                                case "fahrenheit":
                                    conversionResult.setText(String.valueOf(Double.parseDouble(input.getText().toString()) - 459.67));
                                    break;
                                case "kelvin":
                                    conversionResult.setText(String.valueOf(Double.parseDouble(input.getText().toString()) * 5 / 9));
                                    break;
                                case "rankine":
                                    conversionResult.setText(input.getText().toString());
                                    break;
                                case "reaumur":
                                    conversionResult.setText(String.valueOf((Double.valueOf(input.getText().toString()) - 491.67) * 4 / 9));
                                    break;
                            }

                            break;
                        case "reaumur":
                            switch (convertTo()) {
                                case "celsius":
                                    conversionResult.setText(String.valueOf((Double.parseDouble(input.getText().toString()) / 0.8)));
                                    break;
                                case "fahrenheit":
                                    conversionResult.setText(String.valueOf(Double.parseDouble(input.getText().toString()) * 9 / 4 + 32));
                                    break;
                                case "kelvin":
                                    conversionResult.setText(String.valueOf(Double.parseDouble(input.getText().toString()) * 5 / 4 + 273.15));
                                    break;
                                case "rankine":
                                    conversionResult.setText(String.valueOf(Double.parseDouble(input.getText().toString()) * 9 / 4 + 491.67));
                                    break;
                                case "reaumur":
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
            if (calc.contains(StringUtils.SPACE))
                calc = calc.replace(StringUtils.SPACE, StringUtils.EMPTY);

            if (calc.contains(StringUtils.Operator.Stylized.TIMES) || calc.contains(StringUtils.Operator.Stylized.DIVISION)) {
                if (calc.contains(StringUtils.Operator.Stylized.TIMES))
                    calc = calc.replace(StringUtils.Operator.Stylized.TIMES, StringUtils.Operator.Raw.TIMES);

                if (calc.contains(StringUtils.Operator.Stylized.DIVISION))
                    calc = calc.replace(StringUtils.Operator.Stylized.DIVISION, StringUtils.Operator.Raw.DIVISION);
            }

            if (FormatUtils.Companion.hasSquareBracket(calc))
                FormatUtils.Companion.removeSquareBracket(calc, REMOVE_SQUARE_BRACKET_ALL);

            System.out.println("Transformed calc: " + FormatUtils.Companion.removeSquareBracket(calc, REMOVE_SQUARE_BRACKET_ALL));

            return FormatUtils.Companion.removeSquareBracket(calc, REMOVE_SQUARE_BRACKET_ALL);
        }

        return null;
    }

    /**
     * "Styles" the result that is shown to the user when pressing the equal button.
     *
     * @param input Represents the text field itself.
     * @param conversionResult Conversion result of the input keypad_button'.
     */
    private void inputFormat(EditText input, TextView conversionResult) {
        if (conversionResult.getText().toString().contains(".")) {
            conversionResult.setText(format.format(Double.parseDouble(conversionResult.getText().toString())));
        }

        if (conversionResult.getText().toString().endsWith(".0"))
            conversionResult.setText(input.getText().toString().replace(".0", ""));

        if (conversionResult.getText().toString().endsWith("E"))
            conversionResult.setText(input.getText().toString().replace("E", "e"));
    }
}