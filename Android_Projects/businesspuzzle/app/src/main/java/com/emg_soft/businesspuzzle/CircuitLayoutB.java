package com.emg_soft.businesspuzzle;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yura Vyrovoy on 9/7/2017.
 */

public class CircuitLayoutB extends LinearLayout {

    List<LinearLayout> lstRowLayouts;

    public CircuitLayoutB(Context context) {
        super(context);
        init();
    }

    public CircuitLayoutB(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircuitLayoutB(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CircuitLayoutB(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        lstRowLayouts = new ArrayList<>();

        // add list for inputs
        //lstRowLayouts
    }

    public void setOpeartorsRowsCount(int NRows){

    }

}
