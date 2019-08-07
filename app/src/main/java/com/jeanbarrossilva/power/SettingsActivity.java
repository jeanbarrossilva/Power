package com.jeanbarrossilva.power;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor preferencesEditor;

    Intent settingsActivityToMainActivity;

    String appName;
    String versionName;

    Dialog dialogYesNo;
    TextView dialogYesNoTitle;
    TextView dialogYesNoMessage;
    Button dialogYesNoYesButton;
    Button dialogYesNoNoButton;

    TextView activityTitle;
    TextView activitySubtitle;

    Switch settingHiddenMode;
    String pin;

    ConstraintLayout settingSendFeedback;

    TextView version;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = getSharedPreferences("com.jeanbarrossilva.power", Context.MODE_PRIVATE);
        preferencesEditor = preferences.edit();

        settingsActivityToMainActivity = new Intent(this, MainActivity.class);

        appName = getString(R.string.app_name);
        versionName = BuildConfig.VERSION_NAME;

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

        activityTitle = findViewById(R.id.activity_title);
        activitySubtitle = findViewById(R.id.activity_subtitle);

        settingHiddenMode = findViewById(R.id.setting_hidden_mode_preview_switch);
        settingSendFeedback = findViewById(R.id.setting_send_feedback);

        version = findViewById(R.id.version);

        settingHiddenMode();
        settingSendFeedback();

        rememberSettingHiddenMode();

        version();
    }

    private void settingHiddenMode() {
        settingHiddenMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    preferencesEditor.putBoolean("isHiddenModeEnabled", true)
                            .apply();

                    dialogYesNoTitle.setText(getString(R.string.hidden_mode_dialog_title));
                    dialogYesNoMessage.setText(getString(R.string.hidden_mode_dialog_message));

                    dialogYesNoYesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogYesNo.dismiss();

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
                    });

                    dialogYesNo.show();
                } else {
                    preferencesEditor.putBoolean("isHiddenModeEnabled", false)
                            .apply();
                }
            }
        });
    }

    private void rememberSettingHiddenMode() {
        if (settingsActivityToMainActivity.getBooleanExtra("isHiddenModeEnabled", true)) {
            settingHiddenMode.setChecked(true);
            dialogYesNo.dismiss();
        } else {
            settingHiddenMode.setChecked(false);
        }
    }

    private void settingSendFeedback() {
        settingSendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent email = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:jeanbarrossilva@outlook.com"));
                email.putExtra(Intent.EXTRA_SUBJECT, String.format(getString(R.string.send_feedback_email_subject), appName));

                startActivity(Intent.createChooser(email, getString(R.string.send_feedback)));
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
        } else if (dots >= 2) {
            version.setText(String.format(getString(R.string.version_beta), versionName));
        }
    }
}