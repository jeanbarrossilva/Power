package com.jeanbarrossilva.power;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

public class SettingsFragment extends Fragment {
    private MainActivity mainActivity;
    private Context context;

    private Switch shareUsageData;
    private ConstraintLayout sendFeedback;

    private ConstraintLayout sourceCode;
    private TextView sourceCodeTitle;

    private TextView atDeveloper;

    public SettingsFragment() {

    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        mainActivity = (MainActivity) getActivity();

        ScrollView settingsScrollView = view.findViewById(R.id.settings_scroll_view);
        settingsScrollView.setVerticalScrollBarEnabled(false);

        shareUsageData = view.findViewById(R.id.share_usage_data_switch);
        shareUsageData.setChecked(mainActivity.getShareUsageData());

        sendFeedback = view.findViewById(R.id.send_feedback);

        sourceCode = view.findViewById(R.id.source_code);
        sourceCodeTitle = view.findViewById(R.id.source_code_title);

        atDeveloper = view.findViewById(R.id.at_developer);

        TextView version = view.findViewById(R.id.version);
        version.setText(mainActivity.getVersionName());

        shareUsageData();
        sendFeedback();
        sourceCode();

        developer();

        return view;
    }

    private void shareUsageData() {
        shareUsageData.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mainActivity.dialogYesNoTitle.setText(getString(R.string.share_usage_data));
                    mainActivity.dialogYesNoMessage.setText(getString(R.string.share_usage_data_warning));

                    mainActivity.dialogYesNoYesButton.setText(getString(R.string.ok));

                    mainActivity.dialogYesNoYesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            shareUsageData.setChecked(true);
                            mainActivity.dialogYesNo.dismiss();
                        }
                    });

                    mainActivity.dialogYesNoNoButton.setText(getString(R.string.cancel));

                    mainActivity.dialogYesNoNoButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            shareUsageData.setChecked(false);
                            mainActivity.dialogYesNo.dismiss();
                        }
                    });

                    mainActivity.dialogYesNo.show();
                }

                mainActivity.setShareUsageData(isChecked);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void sendFeedback() {
        sendFeedback.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mainActivity.bounceIn(sendFeedback, 0.35, 1);
                        break;
                    case MotionEvent.ACTION_UP:
                        sendFeedback.startAnimation(mainActivity.getBounceOut());
                        mainActivity.dialogFeedback.show();
                        break;
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
                        mainActivity.bounceIn(sourceCode, 0.35, 1);
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
    private void developer() {
        atDeveloper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP)
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/jeanbarrossilva")));

                return true;
            }
        });
    }
}