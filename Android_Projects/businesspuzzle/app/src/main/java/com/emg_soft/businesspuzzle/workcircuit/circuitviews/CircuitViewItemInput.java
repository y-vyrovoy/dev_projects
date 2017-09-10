package com.emg_soft.businesspuzzle.workcircuit.circuitviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.emg_soft.businesspuzzle.CircuitLayout;
import com.emg_soft.businesspuzzle.R;
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

    private Drawable drawableOn;
    private Drawable drawableOff;

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
                updateBackground();
            }
        });

        Bitmap bitmapSource = BitmapFactory.decodeResource(getResources(), R.drawable.ic_input_on);
        Bitmap bitmapScaled = CircuitLayout.scaleDown(bitmapSource, CircuitLayout.INPUT_MAX_SIZE, true);
        drawableOn = new BitmapDrawable(getResources(), bitmapScaled);

        bitmapSource = BitmapFactory.decodeResource(getResources(), R.drawable.ic_input_off);
        bitmapScaled = CircuitLayout.scaleDown(bitmapSource, CircuitLayout.INPUT_MAX_SIZE, true);
        drawableOff = new BitmapDrawable(getResources(), bitmapScaled);

        setTextOff(null);
        setTextOn(null);

        updateBackground();
        setTextColor(Color.LTGRAY);

    }


    // getters and setters
    public CircuitItem getCircuitItem() {
        return circuitItem;
    }

    public void setCircuitItem(CircuitItem circuitItem) {
        this.circuitItem = circuitItem;
        setText(this.circuitItem.getCaption());

        setChecked(this.circuitItem.getValue());
    }

    private void updateBackground(){

        if( (getCircuitItem() == null) ||
            (getCircuitItem().getValue() == false) ){
            setBackground(drawableOff);
        }
        else{
            setBackground(drawableOn);
        }

    }


}
