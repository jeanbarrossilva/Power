package com.jeanbarrossilva.power;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class HistoryFragment extends CalculatorFragment {
    private History history;
    private SQLiteDatabase database;

    private ArrayList<Calc> calcs;
    private HistoryAdapter adapter;

    public HistoryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        history = new History(context);
        database = history.getWritableDatabase();

        calcs = new ArrayList<>();
        adapter = new HistoryAdapter(getActivity(), calcs);

        ListView historyList = view.findViewById(R.id.history);
        historyList.setAdapter(adapter);

        return view;
    }

    void add(String calc, String result, String id) {
        String[] calcInfo = {
                calc, result, id
        };

        database.execSQL("INSERT INTO" + StringUtils.SPACE + History.DATABASE + StringUtils.SPACE + StringUtils.Punctuation.LEFT_PARENTHESIS + History.CALC + StringUtils.Punctuation.COMMA + StringUtils.SPACE + History.RESULT + StringUtils.Punctuation.RIGHT_PARENTHESIS + StringUtils.SPACE + "VALUES" + StringUtils.SPACE + StringUtils.Punctuation.LEFT_PARENTHESIS + "'" + calc + StringUtils.Punctuation.COMMA + StringUtils.SPACE + result + StringUtils.Punctuation.COMMA + StringUtils.SPACE + id + "'" + StringUtils.Punctuation.RIGHT_PARENTHESIS);

        calcs.add(new Calc(calc, result));
        adapter.notifyDataSetChanged();
    }
}