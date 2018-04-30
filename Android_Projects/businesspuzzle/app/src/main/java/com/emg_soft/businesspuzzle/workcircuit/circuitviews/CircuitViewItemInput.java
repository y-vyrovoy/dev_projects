package com.emg_soft.businesspuzzle.workcircuit.circuitviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
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


        Bitmap bitmapSourceOn = BitmapFactory.decodeResource(getResources(), R.drawable.ic_input_on);
        Bitmap bitmapScaledOn = CircuitLayout.scaleDown(bitmapSourceOn, CircuitLayout.INPUT_MAX_SIZE, true);
        drawableOn = new BitmapDrawable(getResources(), bitmapScaledOn);

        Bitmap bitmapSourceOff = BitmapFactory.decodeResource(getResources(), R.drawable.ic_input_off);
        Bitmap bitmapScaledOff = CircuitLayout.scaleDown(bitmapSourceOff, CircuitLayout.INPUT_MAX_SIZE, true);
        drawableOff = new BitmapDrawable(getResources(), bitmapScaledOff);


        setTextOff(null);
        setTextOn(null);

        updateBackground();

        setTextColor(Color.LTGRAY);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
        //setText(null);
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
            setTypeface(null, Typeface.NORMAL);
        }
        else{
            setBackground(drawableOn);
            setTypeface(getTypeface(), Typeface.BOLD);
        }

    }

/*
    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);

        Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(4);
        p.setColor(Color.RED);

        canvas.drawLine(0, 0, getWidth(), 0, p);
        canvas.drawLine(getWidth(), 0, getWidth(), getHeight(), p);
        canvas.drawLine(getWidth(), getHeight(), 0, getHeight(), p);
        canvas.drawLine(0,  getHeight(), 0, 0, p);

    }
*/

}
