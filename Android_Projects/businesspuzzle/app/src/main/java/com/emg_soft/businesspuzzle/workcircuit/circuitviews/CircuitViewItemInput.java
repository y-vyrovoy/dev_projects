package com.emg_soft.businesspuzzle.workcircuit.circuitviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.emg_soft.businesspuzzle.workcircuit.circuitdata.CircuitInput;
import com.emg_soft.businesspuzzle.workcircuit.circuitdata.CircuitItem;

import java.util.List;

/**
 * Created by Yura Vyrovoy on 9/9/2017.
 */

public class CircuitViewItemInput
        extends ToggleButton
        implements CircuitViewItem {

    private CircuitItem circuitItem;

    // constructors
    public CircuitViewItemInput(Context context) {
        super(context);
        init();
    }

    public CircuitViewItemInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircuitViewItemInput(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){

        // setting value on users click
        setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((CircuitInput)getCircuitItem()).setValue(isChecked);
            }
        });
    }


    // getters and setters
    public CircuitItem getCircuitItem() {
        return circuitItem;
    }

    public void setCircuitItem(CircuitItem circuitItem) {
        this.circuitItem = circuitItem;
        setText(this.circuitItem.getName());

        setChecked(this.circuitItem.getValue());
    }


}
