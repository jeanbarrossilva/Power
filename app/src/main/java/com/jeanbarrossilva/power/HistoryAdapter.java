package com.jeanbarrossilva.power;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class HistoryAdapter extends BaseAdapter {
    private Activity activity;
    private List<Calc> calcs;

    HistoryAdapter(Activity activity, List<Calc> calcs) {
        this.activity = activity;
        this.calcs = calcs;
    }

    @Override
    public int getCount() {
        return calcs.size();
    }

    @Override
    public Object getItem(int position) {
        return calcs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Calc calc = calcs.get(position);

        if (convertView != null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.history_item, parent, false);

            TextView expression = convertView.findViewById(R.id.expression);
            expression.setText(calc.getCalc());
        }

        return convertView;
    }
}