package com.jeanbarrossilva.power;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nisrulz.sensey.Sensey;
import com.github.nisrulz.sensey.TouchTypeDetector;

import java.util.Objects;

import id.voela.actrans.AcTrans;

public class SettingsActivity extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor preferencesEditor;

    Intent settingsActivityToMainActivity;

    String appName;
    String versionName;
    boolean isBeta;

    BounceInterpolator bounceInterpolator;
    Animation bounceIn;
    Animation bounceOut;

    String empty;
    String space;

    Dialog dialogYesNo;
    TextView dialogYesNoTitle;
    TextView dialogYesNoMessage;
    Button dialogYesNoYesButton;
    Button dialogYesNoNoButton;

    Sensey sensey;
    AcTrans.Builder acTrans;

    TextView activityTitle;
    TextView activitySubtitle;

    ConstraintLayout settingHiddenModeLayout;
    Switch settingHiddenMode;
    TextView settingHiddenModeDisclaimer;
    String pin;

    ConstraintLayout settingHapticFeedbackLayout;
    Switch settingHapticFeedback;
    ConstraintLayout settingSendFeedback;

    ConstraintLayout settingCollaborate;
    TextView settingCollaborateTitle;

    ConstraintLayout settingCredits;

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

        appName = getString(R.string.app_name);
        versionName = BuildConfig.VERSION_NAME;

        bounceInterpolator = new BounceInterpolator(0.1, 15);

        bounceIn = AnimationUtils.loadAnimation(this, R.anim.bounce_in);
        bounceIn.setInterpolator(bounceInterpolator);

        bounceOut = AnimationUtils.loadAnimation(this, R.anim.bounce_out);
        bounceOut.setInterpolator(bounceInterpolator);

        empty = "";
        space = " ";

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
        acTrans = new AcTrans.Builder(SettingsActivity.this);

        activityTitle = findViewById(R.id.activity_title);
        activitySubtitle = findViewById(R.id.activity_subtitle);

        settingHiddenModeLayout = findViewById(R.id.setting_hidden_mode);
        settingHiddenMode = findViewById(R.id.setting_hidden_mode_switch);
        settingHiddenModeDisclaimer = findViewById(R.id.setting_hidden_mode_disclaimer);

        settingHapticFeedbackLayout = findViewById(R.id.setting_haptic_feedback);
        settingHapticFeedback = findViewById(R.id.setting_haptic_feedback_switch);

        settingSendFeedback = findViewById(R.id.setting_send_feedback);

        settingCollaborate = findViewById(R.id.setting_collaborate);
        settingCollaborateTitle = findViewById(R.id.setting_collaborate_title);

        settingCredits = findViewById(R.id.setting_credits);

        activityAdditionalInfo = findViewById(R.id.activity_additional_info);
        version = findViewById(R.id.version);

        if (Build.VERSION.SDK_INT >= 21) {
            if (new CalculatorActivity().preferences.getBoolean("isNight", false)) {
                settingHiddenModeLayout.setElevation(2);
                settingHapticFeedbackLayout.setElevation(2);
                settingSendFeedback.setElevation(2);
                settingCollaborate.setElevation(2);

                activityAdditionalInfo.setElevation(1);
            }
        }

        calculator();

        settingHiddenMode();
        settingHapticFeedback();
        settingSendFeedback();
        settingCollaborate();
        settingCredits();

        rememberSettingHapticFeedback();

        version();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK, settingsActivityToMainActivity);
        acTrans.performSlideToRight();

        finish();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        sensey.setupDispatchTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    private void calculator() {
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
                        startActivity(new Intent(SettingsActivity.this, CalculatorActivity.class));
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

        Sensey.getInstance().startTouchTypeDetection(SettingsActivity.this, touchTypeListener);
    }

    private void settingHiddenMode() {
        // Hidden mode is temporarily unavailable.
        settingHiddenMode.setChecked(false);
        settingHiddenMode.setClickable(false);

        if (!(settingHiddenMode.isChecked() & settingHiddenMode.isClickable())) {
            settingHiddenModeDisclaimer.setText(getString(R.string.hidden_mode_unavailable));
        } else {
            settingHiddenModeDisclaimer.setText(getString(R.string.hidden_mode_disable));
        }

        /* settingHiddenMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    preferencesEditor.putBoolean("isHiddenModeEnabled", true)
                            .apply();

                    dialogSettingHiddenModeEnable(buttonView);
                } else {
                    preferencesEditor.putBoolean("isHiddenModeEnabled", false)
                            .apply();
                }
            }
        }); */
    }

    private void dialogSettingHiddenModeEnable(final CompoundButton compoundButton) {
        dialogYesNoTitle.setText(getString(R.string.hidden_mode_dialog_title));
        dialogYesNoMessage.setText(getString(R.string.hidden_mode_dialog_message));

        dialogYesNoYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogYesNo.dismiss();
                dialogSettingHiddenModePassword();
            }
        });

        dialogYesNoNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                compoundButton.setChecked(false);
                dialogYesNo.dismiss();
            }
        });

        dialogYesNo.show();
    }

    private void dialogSettingHiddenModePassword() {
        Dialog dialogInput = new Dialog(SettingsActivity.this);
        Objects.requireNonNull(dialogInput.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogInput.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogInput.setContentView(R.layout.dialog_input);

        TextView dialogInputTitle = dialogInput.findViewById(R.id.title);
        dialogInputTitle.setText(getString(R.string.hidden_mode_set_password_dialog_title));

        TextView dialogInputMessage = dialogInput.findViewById(R.id.message);
        dialogInputMessage.setText(getString(R.string.hidden_mode_set_password_dialog_message));

        final EditText dialogInputInput = dialogInput.findViewById(R.id.input);
        dialogInputInput.setInputType(InputType.TYPE_CLASS_NUMBER);

        TextView dialogInputButton = dialogInput.findViewById(R.id.ok);

        dialogInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pin = dialogInputInput.getText().toString();

                if (pin.length() == 4) {
                    settingsActivityToMainActivity.putExtra("password", pin);

                    setResult(RESULT_OK, settingsActivityToMainActivity);
                    finish();
                } else {
                    Toast.makeText(SettingsActivity.this, "The PIN must contain only 4 digits.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogInput.show();
    }

    private void rememberSettingHiddenMode() {
        if (settingsActivityToMainActivity.getBooleanExtra("isHiddenModeEnabled", true)) {
            settingHiddenMode.setChecked(true);
            dialogYesNo.dismiss();
        } else {
            settingHiddenMode.setChecked(false);
        }
    }

    private void settingHapticFeedback() {
        settingHapticFeedback.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    preferencesEditor.putBoolean("isHapticFeedbackEnabled", true)
                            .apply();
                } else {
                    preferencesEditor.putBoolean("isHapticFeedbackEnabled", false)
                            .apply();
                }
            }
        });
    }

    private void rememberSettingHapticFeedback() {
        if (settingsActivityToMainActivity.getBooleanExtra("isHapticFeedbackEnabled", true)) {
            settingHapticFeedback.setChecked(true);
        } else {
            settingHapticFeedback.setChecked(false);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void settingSendFeedback() {
        settingSendFeedback.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        settingSendFeedback.startAnimation(bounceIn);
                        break;
                    case MotionEvent.ACTION_UP:
                        settingSendFeedback.startAnimation(bounceOut);

                        Intent email = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:jeanbarrossilva@outlook.com"));

                        if (isBeta) {
                            email.putExtra(Intent.EXTRA_SUBJECT, String.format(getString(R.string.send_feedback_email_subject), appName, versionName + space + "beta"));
                        } else {
                            email.putExtra(Intent.EXTRA_SUBJECT, String.format(getString(R.string.send_feedback_email_subject), appName, versionName));
                        }

                        startActivity(Intent.createChooser(email, getString(R.string.send_feedback)));
                }

                return true;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void settingCollaborate() {
        settingCollaborate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        settingCollaborate.startAnimation(bounceIn);
                        settingCollaborateTitle.setText(getString(R.string.source_code));
                        break;
                    case MotionEvent.ACTION_UP:
                        settingCollaborate.startAnimation(bounceOut);
                        settingCollaborateTitle.setText(getString(R.string.collaborate));

                        Intent powerGitHubRepo = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/jeanbarrossilva/power"));
                        startActivity(powerGitHubRepo);
                }

                return true;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void settingCredits() {
        settingCredits.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        settingCredits.startAnimation(bounceIn);
                        break;
                    case MotionEvent.ACTION_UP:
                        settingCredits.startAnimation(bounceOut);

                        startActivity(new Intent(SettingsActivity.this, CreditsActivity.class));
                        acTrans.performSlideToLeft();
                }

                return true;
            }
        });
    }

    private void version() {
        int dots = 0;

        // Counts how many dots are there in 'versionName'.
        for (char dot: versionName.toCharArray()) {
            if (dot == '.') {
                dots ++;
            }
        }

        if (dots == 1) {
            version.setText(String.format(getString(R.string.version), versionName));
            isBeta = false;
        } else if (dots >= 2) {
            version.setText(String.format(getString(R.string.version_beta), versionName));
            isBeta = true;
        }
    }
}