package com.example.udptest;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class PidTuneView extends LinearLayout implements SeekBar.OnSeekBarChangeListener {
    SeekBar pSeekBar;
    SeekBar iSeekBar;
    SeekBar dSeekBar;
    SeekBar totalSeekBar;
    TextView labelP;
    TextView labelI;
    TextView labelD;
    TextView labelTotal;

    MainActivity ma;
    String TAG = "PIDTuneView";

    public PidTuneView(Context context) {
        super(context);
        setOrientation(LinearLayout.VERTICAL);
        ma = (MainActivity) context;
        pSeekBar = new SeekBar(context);
        iSeekBar = new SeekBar(context);
        dSeekBar = new SeekBar(context);
        totalSeekBar = new SeekBar(context);
        labelP = new TextView(context);
        labelP.setText("p:");
        addView(labelP);

        addView(pSeekBar);
        labelI = new TextView(context);
        labelI.setText("i:");
        addView(labelI);
        addView(iSeekBar);
        labelD = new TextView(context);
        labelD.setText("d:");
        addView(labelD);
        addView(dSeekBar);
        labelTotal = new TextView(context);
        labelTotal.setText("total:");
        addView(labelTotal);
        addView(totalSeekBar);

        pSeekBar.setOnSeekBarChangeListener(this);
        iSeekBar.setOnSeekBarChangeListener(this);
        dSeekBar.setOnSeekBarChangeListener(this);
        totalSeekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        String msg = "PID," + pSeekBar.getProgress() +"," +iSeekBar.getProgress() +","+ dSeekBar.getProgress() +","+ totalSeekBar.getProgress();
        labelTotal.setText(msg);
        ma.sendMessage(msg);
    }
}
