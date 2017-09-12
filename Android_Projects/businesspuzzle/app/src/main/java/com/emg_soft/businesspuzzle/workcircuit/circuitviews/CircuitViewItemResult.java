package com.emg_soft.businesspuzzle.workcircuit.circuitviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;

import com.emg_soft.businesspuzzle.CircuitLayout;
import com.emg_soft.businesspuzzle.R;
import com.emg_soft.businesspuzzle.workcircuit.circuitdata.CircuitItem;
import com.emg_soft.businesspuzzle.workcircuit.circuitdata.CircuitResult;

/**
 * Created by Yura Vyrovoy on 9/9/2017.
 */
public class CircuitViewItemResult
        extends android.support.v7.widget.AppCompatTextView
        implements CircuitViewItem {

    private CircuitItem circuitItem;
    private Drawable drawableBackground;

    // constructors

    public CircuitViewItemResult(Context context) {
        super(context);
        init();
    }

    public CircuitViewItemResult(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircuitViewItemResult(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Bitmap bitmapSource = BitmapFactory.decodeResource(getResources(), R.drawable.ic_result);
        Bitmap bitmapScaled = CircuitLayout.scaleDown(bitmapSource, CircuitLayout.RESULT_MAX_SIZE, true);
        drawableBackground = new BitmapDrawable(getResources(), bitmapScaled);

        setBackground(drawableBackground);
        setTextColor(Color.LTGRAY);
        setGravity(Gravity.CENTER);

        setTextSize(getTextSize() * 2);
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
            setText(((CircuitResult)circuitItem).getmTextSuccess());
        }
        else{
            setText(((CircuitResult)circuitItem).getmTextFail());
        }
    }


}