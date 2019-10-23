package com.jeanbarrossilva.power;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class HistoryFragment extends CalculatorFragment {
    private History history;
    private ArrayList<Calc> calcs;

    private ListView historyList;

    public HistoryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        history = new History(context);
        calcs = new ArrayList<>();

        HistoryAdapter adapter = new HistoryAdapter(getActivity(), calcs);

        historyList = view.findViewById(R.id.history);
        historyList.setAdapter(adapter);

        return view;
    }

    void add(Calc calc) {
        calcs.add(calc);
        historyList.notifyAll();
    }
}