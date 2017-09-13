package com.emg_soft.businesspuzzle.workcircuit.circuitviews;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.emg_soft.businesspuzzle.CircuitLayout;
import com.emg_soft.businesspuzzle.R;

/**
 * Created by Yura Vyrovoy on 9/9/2017.
 */

public class CircuitTrackView extends View {

    private CircuitViewItem mViewStart;
    private CircuitViewItem mViewEnd;

    private int input;

    private boolean inputLeft;

    private int level;
    private int subLevel;

    private Rect rectVerticalOut;
    private Rect rectVerticalIn;
    private Rect rectHorizontal;

    private Paint mPaintTracksBorder;
    private Paint mPaintTracksBodyFalse;
    private Paint mPaintTracksBodyTrue;


    private String mDescription;

    // constructors
    public CircuitTrackView(CircuitViewItem start, CircuitViewItem end, Context context){

        super(context);

        input = -1;
        setViewStart(start);
        setViewEnd(end);

        rectVerticalOut = new Rect();
        rectVerticalIn = new Rect();
        rectHorizontal = new Rect();


        mPaintTracksBorder = new Paint();
        mPaintTracksBorder.setStyle(Paint.Style.STROKE);
        mPaintTracksBorder.setStrokeWidth(4);
        mPaintTracksBorder.setColor(Color.DKGRAY);

        mPaintTracksBodyFalse = new Paint();
        mPaintTracksBodyFalse.setStyle(Paint.Style.FILL);
        mPaintTracksBodyFalse.setStrokeWidth(4);
        mPaintTracksBodyFalse.setColor(Color.LTGRAY);

        mPaintTracksBodyTrue = new Paint();
        mPaintTracksBodyTrue.setStyle(Paint.Style.FILL);
        mPaintTracksBodyTrue.setStrokeWidth(4);


        mPaintTracksBodyTrue.setColor(ContextCompat.getColor(context, R.color.colorActiveTrack));

        if( (start != null) && (start.getCircuitItem() != null) ){
            mDescription += start.getCircuitItem().getName();
        }
        else
        {
            mDescription += "null";
        }

        mDescription += " -> ";


        if( (end != null) && (end.getCircuitItem() != null) ){
            mDescription += end.getCircuitItem().getName();
        }
        else
        {
            mDescription += "null";
        }

    }

    @Override
    public void onDraw(Canvas canvas){

        int width = getWidth();
        int height = getHeight();

/*
        // TMP drawing borders
        Paint p = new Paint();
        p.setColor(Color.RED);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(4);

        Rect r = new Rect(0,0,width, height);
        canvas.drawRect(r,p);
*/

        // check how in and out relates (left - right) and set start of the track
        int outX = inputLeft ?
                CircuitLayout.STANDARD_BORDER :
                width - CircuitLayout.STANDARD_BORDER;

        int outYstart = 0;
        int outYend = height - CircuitLayout.INPUT_HEIGHT_QUANTUM * (subLevel + 1);

        // check how in and out relates (left - right) and set the end of the track
        int inX = inputLeft ?
                width - CircuitLayout.STANDARD_BORDER :
                CircuitLayout.STANDARD_BORDER;

        int inYstart = height - CircuitLayout.INPUT_HEIGHT_QUANTUM * (subLevel + 1);
        int inYend = height;


        rectVerticalOut.set(outX - CircuitLayout.TRACK_WIDTH,
                outYstart,
                outX + CircuitLayout.TRACK_WIDTH,
                outYend - CircuitLayout.TRACK_WIDTH);

        rectVerticalIn.set(inX - CircuitLayout.TRACK_WIDTH,
                inYstart + CircuitLayout.TRACK_WIDTH,
                inX + CircuitLayout.TRACK_WIDTH,
                inYend);

        if(inputLeft) {
            rectHorizontal.set(outX - CircuitLayout.TRACK_WIDTH,
                    outYend - CircuitLayout.TRACK_WIDTH,
                    inX + CircuitLayout.TRACK_WIDTH,
                    outYend + CircuitLayout.TRACK_WIDTH);
        }
        else {
            rectHorizontal.set(outX + CircuitLayout.TRACK_WIDTH,
                    outYend - CircuitLayout.TRACK_WIDTH,
                    inX - CircuitLayout.TRACK_WIDTH,
                    outYend + CircuitLayout.TRACK_WIDTH);
        }

        normilizeRect(rectHorizontal);

        canvas.drawRect(rectVerticalOut, mPaintTracksBorder);
        canvas.drawRect(rectVerticalIn, mPaintTracksBorder);
        canvas.drawRect(rectHorizontal, mPaintTracksBorder);


        Paint pFill = mPaintTracksBodyTrue;

        if(getViewStart().getCircuitItem().getValue() == false){
            pFill = mPaintTracksBodyFalse;
        }
        else {
            pFill = mPaintTracksBodyTrue;
        }

        canvas.drawRect(rectVerticalOut, mPaintTracksBodyTrue);
        canvas.drawRect(rectVerticalIn, mPaintTracksBodyTrue);
        canvas.drawRect(rectHorizontal, mPaintTracksBodyTrue);

    }

    private void normilizeRect(Rect rect){

        int l = rect.left;
        int r = rect.right;
        int t = rect.top;
        int b = rect.bottom;

        rect.set( Math.min(l,r), Math.min(t,b), Math.max(l,r), Math.max(t,b));
    }

    public void updateState(){

        invalidate();
    }


    public boolean isViewCrossed(CircuitTrackView trackView){

        boolean leftInside = (trackView.getLeft() >= this.getLeft() - CircuitLayout.INPUT_SHIFT ) &&
                                (trackView.getLeft() <= this.getRight() + CircuitLayout.INPUT_SHIFT);

        boolean rightInside = (trackView.getRight() >= this.getLeft() - CircuitLayout.INPUT_SHIFT) &&
                                (trackView.getRight() <= this.getRight() + CircuitLayout.INPUT_SHIFT);

        return (leftInside || rightInside);
    }


    // setters and getters

    public CircuitViewItem getViewStart() {
        return mViewStart;
    }

    public void setViewStart(CircuitViewItem mViewStart) {
        this.mViewStart = mViewStart;
    }

    public CircuitViewItem getViewEnd() {
        return mViewEnd;
    }

    public void setViewEnd(CircuitViewItem mViewEnd) {

        this.mViewEnd = mViewEnd;

        CircuitViewItemOperator opView = null;
        try{
            opView = (CircuitViewItemOperator) mViewEnd;
        }
        catch (ClassCastException ex){}

        level = (opView == null) ? -1 : opView.getLevel();
    }


    public int getLevel() {
        return level;
    }


    public int getSubLevel() {
        return subLevel;
    }

    public void setSubLevel(int subLevel) {
        this.subLevel = subLevel;
    }

    public int getInput() {
        return input;
    }

    public void setInput(int input) {
        this.input = input;
    }

    public boolean isInputLeft() {
        return inputLeft;
    }

    public void setInputLeft(boolean inLeft) {
        this.inputLeft = inLeft;
    }

    public int getIndexWithinLevel(){
        return getViewEnd().getCircuitItem().getIndexInLevel();
    }
}
