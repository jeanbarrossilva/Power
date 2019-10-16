package com.jeanbarrossilva.power;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsFragment extends Fragment {
    private MainActivity mainActivity;

    private Switch shareUsageData;
    private ConstraintLayout sendFeedback;

    private ConstraintLayout sourceCode;
    private TextView sourceCodeTitle;

    private TextView atDeveloper;

    public SettingsFragment() {

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
                        mainActivity.bounceIn(sendFeedback, "0.35, 1");
                        break;
                    case MotionEvent.ACTION_UP:
                        sendFeedback.startAnimation(mainActivity.getBounceOut());
                        mainActivity.getDialogFeedback().show();
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
                        mainActivity.bounceIn(sourceCode, "0.35, 1");
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