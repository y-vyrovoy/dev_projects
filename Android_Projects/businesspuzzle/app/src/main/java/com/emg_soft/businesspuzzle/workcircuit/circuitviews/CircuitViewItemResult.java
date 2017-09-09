package com.emg_soft.businesspuzzle.workcircuit.circuitviews;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.emg_soft.businesspuzzle.CircuitLayout;
import com.emg_soft.businesspuzzle.workcircuit.circuitdata.CircuitItem;

/**
 * Created by Yura Vyrovoy on 9/9/2017.
 */
public class CircuitViewItemResult
        extends android.support.v7.widget.AppCompatTextView
        implements CircuitViewItem {

    private CircuitItem circuitItem;

    // constructors

    public CircuitViewItemResult(Context context) {
        super(context);
    }

    public CircuitViewItemResult(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircuitViewItemResult(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // getters and setters
    public CircuitItem getCircuitItem() {
        return circuitItem;
    }

    public void setCircuitItem(CircuitItem circuitItem) {
        this.circuitItem = circuitItem;
        calcValue();
    }

    public void calcValue(){

        if(getCircuitItem().getValue() == true) {
            setText("SUCCESS");
        }
        else{
            setText("FAIL");
        }
    }


}