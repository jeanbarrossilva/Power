package com.jeanbarrossilva.power;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class SecretCodeActivity extends CalculatorActivity {
    String input;

    View[] masks;

    ConstraintLayout number;
    int[] numbers = {
            R.id.zero, R.id.one, R.id.two, R.id.three, R.id.four, R.id.five, R.id.six, R.id.seven, R.id.eight, R.id.nine
    };

    Button close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret_code);

        input = getEmpty();

        masks = new View[] {
                findViewById(R.id.mask_1), findViewById(R.id.mask_2), findViewById(R.id.mask_3), findViewById(R.id.mask_4), findViewById(R.id.mask_5), findViewById(R.id.mask_6)
        };

        close = findViewById(R.id.close);

        hideStatusBar();

        input();
        close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(SecretCodeActivity.this, CalculatorActivity.class));
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);

        finish();
    }

    private void concat(String number) {
        input = input.concat(number);
    }

    private void limit() {
        if (input.length() == 6) {
            if (input.equals(SECRET_CODE_LEVEL_2)) {
                setDialogOKTitle(getString(R.string.congratulations));
                setDialogOKMessage(getString(R.string.congratulations_night_mode_unlock));

                setDialogOKButtonOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setIsNightUnlocked(true);
                        getDialogOK().dismiss();

                        onBackPressed();
                    }
                });

                getDialogOK().setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        setIsNightUnlocked(true);
                        onBackPressed();
                    }
                });

                getDialogOK().show();
            }

            for (View mask: masks) {
                mask.setBackgroundResource(R.drawable.mask_unfill);
            }

            input = getEmpty();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void input() {
        View.OnTouchListener listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                number = (ConstraintLayout) view;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(view, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        switch (number.getId()) {
                            case R.id.zero:
                                concat(getString(R.string.zero));
                                break;
                            case R.id.one:
                                concat(getString(R.string.one));
                                break;
                            case R.id.two:
                                concat(getString(R.string.two));
                                break;
                            case R.id.three:
                                concat(getString(R.string.three));
                                break;
                            case R.id.four:
                                concat(getString(R.string.four));
                                break;
                            case R.id.five:
                                concat(getString(R.string.five));
                                break;
                            case R.id.six:
                                concat(getString(R.string.six));
                                break;
                            case R.id.seven:
                                concat(getString(R.string.seven));
                                break;
                            case R.id.eight:
                                concat(getString(R.string.eight));
                                break;
                            case R.id.nine:
                                concat(getString(R.string.nine));
                                break;
                        }

                        masks[input.length() - 1].setBackgroundResource(R.drawable.mask_fill);
                        limit();

                        System.out.println("Input: " + input);
                        System.out.println("\"Button\" ID: " + number.getId());

                        break;
                }

                return true;
            }
        };

        for (int number: numbers) {
            findViewById(number).setOnTouchListener(listener);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void close() {
        close.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bounceIn(view, DEFAULT_BOUNCE_IN_SETTING);
                        break;
                    case MotionEvent.ACTION_UP:
                        view.startAnimation(getBounceOut());
                        onBackPressed();

                        break;
                }

                return true;
            }
        });
    }
}