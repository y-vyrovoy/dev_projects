package com.example.mycustomview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Yura Vyrovoy on 10/14/2017.
 */

public class SplitImageView extends View {

    private static final String TAG = SplitImageView.class.getSimpleName();

    private static final int SPLITTER_HALF_WIDTH = 3;
    private static final int SPLITTER_TOP = 10;

    private int _width;
    private int _height;

    private Bitmap _bmpSource;
    private Bitmap _bmpScaled;
    private Bitmap _bmpPointer;

    private int _splitterPosition;
    private int _splitterMin;
    private int _splitterMax;

    private Rect _rectPointer;
    private int _pointerHalfWidth;
    private int _scaledBmpTop;

    private boolean _isPointerDragged;

    private Paint _paintBorder;
    private Paint _paintFill;



    public SplitImageView(Context context) {
        super(context);
        initialize();
    }

    public SplitImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public SplitImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public SplitImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    private void initialize() {
        _bmpSource = BitmapFactory.decodeResource(getResources(), R.drawable.nobody);
        _bmpPointer = BitmapFactory.decodeResource(getResources(), R.drawable.pointer);
        _pointerHalfWidth = _bmpPointer.getWidth()/2;
        _rectPointer = new Rect();
        _isPointerDragged = false;

        _paintBorder = new Paint();
        _paintBorder.setColor(ContextCompat.getColor(this.getContext(), R.color.colorSplitterBorder));
        _paintBorder.setStyle(Paint.Style.STROKE);
        _paintBorder.setStrokeWidth(1);

        _paintFill = new Paint();
        _paintFill.setColor(ContextCompat.getColor(this.getContext(), R.color.colorSplitterFill));
        _paintFill.setStyle(Paint.Style.FILL);

    }


    private void setPointerX(int x) {
        _splitterPosition = x;

        _rectPointer.set(_splitterPosition - _pointerHalfWidth,
                            SPLITTER_TOP,
                            _splitterPosition    + _pointerHalfWidth,
                            SPLITTER_TOP + _bmpPointer.getHeight());
                }

    private void setupNewSize(int w, int h) {

        float wRatio = (float)( w - _bmpPointer.getWidth( )) / _bmpSource.getWidth();
        float hRatio = ((float)h) / _bmpSource.getHeight();

        float ratio = (wRatio < hRatio) ? wRatio : hRatio;

        _bmpScaled = Bitmap.createScaledBitmap(_bmpSource,
                (int)(_bmpSource.getWidth() * ratio),
                (int)(_bmpSource.getHeight() * ratio),
                false);

        _scaledBmpTop = _rectPointer.top + (int)(_bmpPointer.getHeight()*0.6);

        setPointerX(w/2);

        _splitterMin = (w - _bmpScaled.getWidth())/2;
        _splitterMax = (w + _bmpScaled.getWidth())/2;

    }

    public void setSourceBitmap(Bitmap bmp) {
        _bmpSource = bmp;
        setupNewSize(_width, _height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        _width = w;
        _height = h;

        setupNewSize(_width, _height);
    }

    @Override
    public void onDraw(Canvas canvas){

        int width = getWidth();
        int height = getHeight();



        canvas.drawBitmap(_bmpScaled,
                            (width - _bmpScaled.getWidth())/2,
                _scaledBmpTop,
                            null);

        canvas.drawBitmap(_bmpPointer, _rectPointer.left, _rectPointer.top, null);

        canvas.drawRect(_splitterPosition - SPLITTER_HALF_WIDTH,
                _bmpPointer.getHeight() + 2,
                _splitterPosition + SPLITTER_HALF_WIDTH,
                _scaledBmpTop + _bmpScaled.getHeight(),
                _paintFill);

        canvas.drawRect(_splitterPosition - SPLITTER_HALF_WIDTH,
                        _bmpPointer.getHeight() + 2,
                        _splitterPosition + SPLITTER_HALF_WIDTH,
                _scaledBmpTop + _bmpScaled.getHeight(),
                        _paintBorder);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        int action = event.getActionMasked();

        int x = (int) event.getX();
        int y = (int) event.getY();

        switch(action) {
            case MotionEvent.ACTION_DOWN:

                //_isPointerDragged = _rectPointer.contains((int)x, (int)y);
                _isPointerDragged = ( (y >= _scaledBmpTop) &&
                                        (y <= _scaledBmpTop + _bmpScaled.getHeight()) &&
                                        (x >= _splitterPosition - _pointerHalfWidth * 2) &&
                                        (x <= _splitterPosition + _pointerHalfWidth * 2));


                Log.i(TAG, "onTouchEvent().ACTION_DOWN. In pointer = " + _isPointerDragged);

                break;

            case MotionEvent.ACTION_MOVE:

                if( _isPointerDragged == true ) {

                    if( (x - _pointerHalfWidth >= _splitterMin) &&
                            (x + _pointerHalfWidth <= _splitterMax) ) {

                        setPointerX( x );
                        invalidate();
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                Log.i(TAG, "onTouchEvent().ACTION_UP");

                _isPointerDragged = false;
                break;

            case MotionEvent.ACTION_CANCEL:
                Log.i(TAG, "onTouchEvent().ACTION_CANCEL");
                break;
        }

        return true;
    }

}
