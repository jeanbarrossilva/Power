package com.jeanbarrossilva.power;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nisrulz.sensey.Sensey;
import com.github.nisrulz.sensey.TouchTypeDetector;

import java.util.TimerTask;

import id.voela.actrans.AcTrans;

@SuppressWarnings("SwitchStatementWithTooFewBranches")
public class SettingsActivity extends CalculatorActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor preferencesEditor;

    Intent settingsActivityToMainActivity;

    String empty;
    String space;

    Sensey sensey;
    AcTrans.Builder acTrans;

    TextView activityTitle;
    TextView activitySubtitle;

    ScrollView settingsScrollView;
    ConstraintLayout settings;

    ConstraintLayout settingHiddenModeLayout;
    Switch settingHiddenMode;
    TextView settingHiddenModeDisclaimer;
    String pin;

    ConstraintLayout settingNightLayout;
    Switch settingNight;
    TextView settingNightDisclaimer;

    ConstraintLayout sendFeedback;

    ConstraintLayout sourceCode;
    TextView sourceCodeTitle;

    ConstraintLayout credits;

    ConstraintLayout buyPro;

    ConstraintLayout activityAdditionalInfo;
    TextView version;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = getSharedPreferences("com.jeanbarrossilva.power", Context.MODE_PRIVATE);
        preferencesEditor = preferences.edit();

        settingsActivityToMainActivity = new Intent(this, CalculatorActivity.class);

        empty = "";
        space = " ";

        sensey = Sensey.getInstance();
        acTrans = new AcTrans.Builder(SettingsActivity.this);

        activityTitle = findViewById(R.id.activity_title);
        activitySubtitle = findViewById(R.id.activity_subtitle);

        settingsScrollView = findViewById(R.id.settings_scroll_view);
        settingsScrollView.setVerticalScrollBarEnabled(false);

        settings = findViewById(R.id.settings);

        settingHiddenModeLayout = findViewById(R.id.setting_hidden_mode);
        settingHiddenMode = findViewById(R.id.setting_hidden_mode_switch);
        settingHiddenModeDisclaimer = findViewById(R.id.setting_hidden_mode_disclaimer);

        settingNightLayout = findViewById(R.id.setting_night);
        settingNight = findViewById(R.id.setting_night_switch);
        settingNightDisclaimer = findViewById(R.id.setting_night_disclaimer);

        sendFeedback = findViewById(R.id.send_feedback);

        sourceCode = findViewById(R.id.source_code);
        sourceCodeTitle = findViewById(R.id.source_code_title);

        credits = findViewById(R.id.credits);

        buyPro = findViewById(R.id.buy_pro);

        activityAdditionalInfo = findViewById(R.id.activity_additional_info);

        version = findViewById(R.id.version);
        version.setText(String.format(getString(R.string.version_x), getVersionName()));

        back();

        settingHiddenMode();
        sendFeedback();
        sourceCode();
        credits();
        buyPro();

        if (Build.VERSION.SDK_INT >= 21) {
            settingNightLayout.setVisibility(View.GONE);
        } else {
            settingNightLayout.setVisibility(View.VISIBLE);
            settingNight();
        }

        rememberSettingHiddenMode();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        sensey.setupDispatchTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    private void back() {
        TouchTypeDetector.TouchTypListener touchTypeListener = new TouchTypeDetector.TouchTypListener() {
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
                    case TouchTypeDetector.SWIPE_DIR_UP:
                        startActivity(new Intent(SettingsActivity.this, CalculatorActivity.class));
                        acTrans.performSlideToTop();

                        break;
                }
            }

            @Override
            public void onLongPress() {

            }
        };

        Sensey.getInstance().startTouchTypeDetection(SettingsActivity.this, touchTypeListener);
    }

    private void settingHiddenMode() {
        if (getIsHiddenModeUnlocked()) {
            if (getIsAuthentic()) {
                settingHiddenMode.setClickable(true);

                settingHiddenModeLayout.setAlpha(1);

                settingHiddenModeDisclaimer.setVisibility(View.VISIBLE);
                settingHiddenModeDisclaimer.setText(getString(R.string.hidden_mode_definition));

                settingHiddenMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            setIsHiddenModeEnabled(true);
                            settingHiddenModeDisclaimer.setText(getString(R.string.hidden_mode_disable));

                            setDialogInputTitle(getString(R.string.hidden_mode));
                            setDialogInputMessage(getString(R.string.hidden_mode_set_password_dialog_message));
                            setDialogInputFieldInputType(InputType.TYPE_CLASS_NUMBER);

                            setDialogInputButtonOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    pin = getDialogInputFieldText();

                                    if (!pin.isEmpty()) {
                                        if (pin.length() == 4) {
                                            settingsActivityToMainActivity.putExtra("pin", pin);

                                            setResult(RESULT_OK, settingsActivityToMainActivity);

                                            finish();
                                            acTrans.performSlideToTop();
                                        } else if (pin.length() < 4) {
                                            Toast.makeText(SettingsActivity.this, getString(R.string.hidden_mode_pin_at_least_4_digits), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(SettingsActivity.this, getString(R.string.hidden_only_mode_pin_4_digits), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(SettingsActivity.this, getString(R.string.hidden_mode_pin_empty), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            getDialogInput().show();

                            getDialogInput().setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    settingHiddenMode.setChecked(false);
                                }
                            });
                        }
                    }
                });
            }
        } else {
            settingHiddenMode.setClickable(false);

            settingHiddenModeLayout.setAlpha(0.5f);

            settingHiddenModeDisclaimer.setVisibility(View.VISIBLE);
            settingHiddenModeDisclaimer.setText(getString(R.string.hidden_mode_pro_only));
        }
    }

    private void settingNight() {
        if (getIsAuthentic()) {
            if (getIsNightUnlocked()) {
                settingNight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            settingNight.setClickable(true);
                            settingNightLayout.setAlpha(1);

                            setNight(true);
                        } else {
                            setNight(false);
                        }
                    }
                });

                settingNight.setChecked(getIsNightEnabled());
            } else {
                settingNightLayout.setAlpha(0.5f);

                settingNight.setChecked(false);
                settingNight.setClickable(false);

                settingNightDisclaimer.setVisibility(View.VISIBLE);
                settingNightDisclaimer.setText(getString(R.string.night_pro));
            }
        } else {
            settingNightLayout.setAlpha(1);
            settingNight.setClickable(true);

            settingNightDisclaimer.setVisibility(View.GONE);
        }
    }

    private void rememberSettingHiddenMode() {
        if (getIsHiddenModeEnabled()) {
            settingHiddenMode.setChecked(true);
            getDialogInput().dismiss();
        } else {
            settingHiddenMode.setChecked(false);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void sendFeedback() {
        sendFeedback.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(sendFeedback, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        sendFeedback.startAnimation(getBounceOut());

                        Intent email = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:jeanbarrossilva@outlook.com"));
                        email.putExtra(Intent.EXTRA_SUBJECT, String.format(getString(R.string.send_feedback_email_subject), getAppName(), getVersionName()));

                        startActivity(Intent.createChooser(email, getString(R.string.send_feedback)));
                }

                return true;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void sourceCode() {
        sourceCode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(sourceCode, DEFAULT_BOUNCE_IN_SETTING);
                        sourceCodeTitle.setText(getString(R.string.source_code));
                        break;
                    case MotionEvent.ACTION_UP:
                        sourceCode.startAnimation(getBounceOut());
                        sourceCodeTitle.setText(getString(R.string.source_code));

                        Intent powerGitHubRepo = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/jeanbarrossilva/power"));
                        startActivity(powerGitHubRepo);
                }

                return true;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void credits() {
        credits.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(credits, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        credits.startAnimation(getBounceOut());

                        startActivity(new Intent(SettingsActivity.this, CreditsActivity.class));
                        acTrans.performSlideToLeft();
                }

                return true;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void buyPro() {
        buyPro.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(buyPro, "0.35, 1");
                        break;
                    case MotionEvent.ACTION_UP:
                        buyPro.startAnimation(getBounceOut());

                        if (getIsAuthentic()) {
                            if (!getDidSubscribeToProAvailability()) {
                                setDialogYesNoTitle(getString(R.string.buy_pro));
                                setDialogYesNoMessage(getString(R.string.pro_not_available));

                                setDialogYesNoYesButtonOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        getDialogYesNo().dismiss();

                                        if (getIsConnected()) {
                                            // Adds + 1 to the proAvailabilityInterestSubscriptions Firebase database.
                                            addProAvailabilityInterestSubscription();

                                            setAlertSuccessIcon(R.drawable.sent);
                                            setAlertSuccessMessage(getString(R.string.waitlist_request_sent));
                                            getAlertSuccess().show();

                                            setDidSubscribeToProAvailability(true);

                                            getTimer().schedule(new TimerTask() {
                                                @Override
                                                public void run() {
                                                    getAlertSuccess().dismiss();
                                                }
                                            }, 3500);
                                        } else {
                                            setAlertErrorIcon(R.drawable.connection_wifi_unavailable);
                                            setAlertErrorMessage(getString(R.string.no_internet_connection));
                                            getAlertError().show();

                                            getTimer().schedule(new TimerTask() {
                                                @Override
                                                public void run() {
                                                    getAlertError().dismiss();
                                                }
                                            }, 3500);
                                        }
                                    }
                                });

                                getDialogYesNo().show();
                            } else {
                                setAlertInfoMessage(getString(R.string.waitlist_request_already_sent));
                                getAlertInfo().show();


                                getTimer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        getAlertInfo().dismiss();
                                    }
                                }, 3500);
                            }
                        } else {
                            unauthentic();
                        }

                        break;
                }

                return true;
            }
        });
    }

    void backToSettingsActivity(final Context context) {
        TouchTypeDetector.TouchTypListener touchTypeListener = new TouchTypeDetector.TouchTypListener() {
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
                    case TouchTypeDetector.SWIPE_DIR_RIGHT:
                        startActivity(new Intent(context, SettingsActivity.class));
                        acTrans.performSlideToRight();

                        break;
                }
            }

            @Override
            public void onLongPress() {

            }
        };

        Sensey.getInstance().startTouchTypeDetection(context, touchTypeListener);
    }
}