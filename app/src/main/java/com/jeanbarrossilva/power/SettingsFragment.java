package com.jeanbarrossilva.power;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import static com.jeanbarrossilva.power.MainActivity.DEFAULT_BOUNCE_IN_SETTING;

public class SettingsFragment extends Fragment {
    private Context context;

    private MainActivity mainActivity;

    private ConstraintLayout settingHiddenModeLayout;
    private Switch settingHiddenMode;
    private String pin;

    private Switch settingNight;

    private ConstraintLayout sendFeedback;

    private ConstraintLayout sourceCode;
    private TextView sourceCodeTitle;

    private ConstraintLayout buyPro;

    public SettingsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        context = Objects.requireNonNull(getContext());

        mainActivity = (MainActivity) getActivity();

        ScrollView settingsScrollView = view.findViewById(R.id.settings_scroll_view);
        settingsScrollView.setVerticalScrollBarEnabled(false);

        settingHiddenModeLayout = view.findViewById(R.id.setting_hidden_mode);
        settingHiddenMode = view.findViewById(R.id.setting_hidden_mode_switch);

        ConstraintLayout settingNightLayout = view.findViewById(R.id.setting_night);
        settingNight = view.findViewById(R.id.setting_night_switch);

        sendFeedback = view.findViewById(R.id.send_feedback);

        sourceCode = view.findViewById(R.id.source_code);
        sourceCodeTitle = view.findViewById(R.id.source_code_title);

        buyPro = view.findViewById(R.id.buy_pro);

        TextView version = view.findViewById(R.id.version);
        version.setText(String.format(getString(R.string.version_x), mainActivity.getVersionName()));

        settingHiddenMode();
        sendFeedback();
        sourceCode();
        buyPro();

        // connectionTest();

        if (Build.VERSION.SDK_INT >= 21) {
            settingNightLayout.setVisibility(View.GONE);
        } else {
            settingNightLayout.setVisibility(View.VISIBLE);
            settingNight();
        }

        rememberSettingHiddenMode();

        return view;
    }

    private void settingHiddenMode() {
        if (mainActivity.getIsHiddenModeUnlocked()) {
            settingHiddenMode.setClickable(true);
            settingHiddenModeLayout.setAlpha(1);

            settingHiddenMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mainActivity.setIsHiddenModeEnabled(true);

                        mainActivity.setDialogInputTitle(getString(R.string.hidden_mode));
                        mainActivity.setDialogInputMessage(getString(R.string.hidden_mode_set_password_dialog_message));
                        mainActivity.setDialogInputFieldInputType(InputType.TYPE_CLASS_NUMBER);

                        mainActivity.setDialogInputButtonOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                pin = mainActivity.getDialogInputFieldText();

                                if (!pin.isEmpty()) {
                                    if (pin.length() == 4) {
                                        // settingsToCalculatorActivity.putExtra("pin", pin);

                                        // mainActivity.setResult(RESULT_OK, settingsToCalculatorActivity);

                                        // finish();
                                        mainActivity.acTrans.performSlideToTop();
                                    } else if (pin.length() < 4) {
                                        Toast.makeText(context, getString(R.string.hidden_mode_pin_at_least_4_digits), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, getString(R.string.hidden_only_mode_pin_4_digits), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(context, getString(R.string.hidden_mode_pin_empty), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        mainActivity.getDialogInput().show();

                        mainActivity.getDialogInput().setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                settingHiddenMode.setChecked(false);
                            }
                        });
                    }
                }
            });
        } else {
            settingHiddenMode.setClickable(false);
            settingHiddenModeLayout.setAlpha(0.5f);
        }
    }

    private void settingNight() {
        settingNight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mainActivity.setNight(isChecked);
                mainActivity.setFragment(mainActivity.settingsFragment);
            }
        });

        settingNight.setChecked(mainActivity.getIsNightEnabled());
    }

    private void rememberSettingHiddenMode() {
        if (mainActivity.getIsHiddenModeEnabled()) {
            settingHiddenMode.setChecked(true);
            mainActivity.getDialogInput().dismiss();
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
                        mainActivity.bounceIn(sendFeedback, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        sendFeedback.startAnimation(mainActivity.getBounceOut());

                        Intent email = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:jeanbarrossilva@outlook.com"));

                        email.putExtra(Intent.EXTRA_SUBJECT, String.format(getString(R.string.send_feedback_email_subject), mainActivity.getAppName(), mainActivity.getVersionName()));
                        email.putExtra(Intent.EXTRA_TEXT, "\n\n" + mainActivity.getEmailDivider() + "\n\n" + mainActivity.getDeviceInfo());

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
                        mainActivity.bounceIn(sourceCode, DEFAULT_BOUNCE_IN_SETTING);
                        sourceCodeTitle.setText(getString(R.string.source_code));
                        break;
                    case MotionEvent.ACTION_UP:
                        sourceCode.startAnimation(mainActivity.getBounceOut());
                        sourceCodeTitle.setText(getString(R.string.source_code));

                        Intent powerGitHubRepo = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/jeanbarrossilva/power"));
                        startActivity(powerGitHubRepo);
                }

                return true;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void buyPro() {
        ((TextView) buyPro.findViewById(R.id.buy_pro_title)).setText(String.format(((TextView) buyPro.findViewById(R.id.buy_pro_title)).getText().toString(), mainActivity.getAppName()));

        buyPro.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mainActivity.bounceIn(buyPro, "0.35, 1");
                        break;
                    case MotionEvent.ACTION_UP:
                        buyPro.startAnimation(mainActivity.getBounceOut());
                        break;
                }

                return true;
            }
        });
    }

    /* void connectionTest() {
        Button connectionTest = findViewById(R.id.connection_test);

        connectionTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIsConnected()) {
                    setConnectionTests(getConnectionTests() + 1);
                    getDatabase().getReference().child("connectionTests").setValue(getConnectionTests());

                    getAlertSuccess().show();
                } else {
                    getAlertError().show();
                }
            }
        });
    } */
}
