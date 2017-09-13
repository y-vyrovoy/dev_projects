package com.emg_soft.businesspuzzle.workcircuit.circuitviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;

import com.emg_soft.businesspuzzle.CircuitLayout;
import com.emg_soft.businesspuzzle.R;
import com.emg_soft.businesspuzzle.workcircuit.circuitdata.CircuitItem;
import com.emg_soft.businesspuzzle.workcircuit.circuitdata.CircuitOperator;

/**
 * Created by Yura Vyrovoy on 9/9/2017.
 */

public class CircuitViewItemOperator
        extends android.support.v7.widget.AppCompatTextView
        implements CircuitViewItem {

    private CircuitItem circuitItem;

    // constructors

    public CircuitViewItemOperator(Context context) {
        super(context);
    }

    public CircuitViewItemOperator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircuitViewItemOperator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // getters and setters
    public CircuitItem getCircuitItem() {
        return circuitItem;
    }

    public void setCircuitItem(CircuitItem circuitItem) {

        this.circuitItem = circuitItem;
        //setText( ((CircuitOperator) this.circuitItem).getOperatorType().toString());
        setGravity(Gravity.CENTER );


        Bitmap bitmapSource = BitmapFactory.decodeResource(getResources(), getOperatorImageId());
        Bitmap bitmapScaled = CircuitLayout.scaleDown(bitmapSource, CircuitLayout.OPERATOR_MAX_SIZE, true);
        setBackground(new BitmapDrawable(getResources(), bitmapScaled) );
    }

    private int getOperatorImageId(){

        switch (((CircuitOperator)this.circuitItem).getOperatorType()){

            case OP_AND:
                return R.drawable.ic_and;

            case OP_OR:
                return R.drawable.ic_or;

            case OP_XOR:
                return R.drawable.ic_xor;

            case OP_NOT:
                return R.drawable.ic_not;

            default:
                return R.drawable.ic_def_op;
        }
    }


    public int getLevel() {
        return ((CircuitOperator)getCircuitItem()).getOperatorLevel();
    }


}
