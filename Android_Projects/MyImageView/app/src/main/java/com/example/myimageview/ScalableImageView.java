package com.example.myimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * Created by Yura Vyrovoy on 10/17/2017.
 */

public class ScalableImageView extends View {

    private static final String TAG = ScalableImageView.class.getSimpleName();
    public static float RATIO = (float)4/3;

    private Rect _rectVisible = new Rect();
    private Rect _rectView = new Rect();

    private int _width = 0;
    private int _height = 0;

    private int _offsetV = 0;
    private int _offsetH = 0;
    private float _scaleCurrent = 1;
    private float _scaleMinimum = 1;

    // items that should be instantiated once, not in every onDraw
    private Bitmap _btimapSource;
    private Bitmap _btimapToDraw = null;
    private Paint _paintBackground;
    private Paint _paintTransparent;
    private Paint _paintText;


    // Sets up interactions
    private ScaleGestureDetector _scaleGestureDetector;
    private GestureDetectorCompat _gestureDetector;
    // Sets up interactions


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Constructors and initializers
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ScalableImageView(Context context) {
        super(context);
        init(context, null);
    }

    public ScalableImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ScalableImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context,AttributeSet attrs) {
        _scaleGestureDetector = new ScaleGestureDetector(context, _scaleGestureListener);
        _gestureDetector = new GestureDetectorCompat(context, _gestureListener);

        if(attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ScalableImageView);
            Drawable drawableSrc = a.getDrawable(R.styleable.ScalableImageView_src);
            if (drawableSrc != null) {
                _btimapSource = ((BitmapDrawable) drawableSrc).getBitmap();
            }

            //Don't forget this
            a.recycle();
        }

        _paintBackground = new Paint();
        _paintBackground.setStyle(Paint.Style.FILL);
        _paintBackground.setColor(Color.GRAY);

        _paintTransparent = new Paint();
        _paintTransparent.setStyle(Paint.Style.STROKE);
        _paintTransparent.setColor(Color.GREEN);
        _paintTransparent.setStrokeWidth(1);

        _paintText = new Paint();
        _paintText.setColor(Color.RED);
        _paintText.setTextSize(40);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Other stuff
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void setImageBitmap(Bitmap bitmap) {
        _btimapSource = bitmap;

        initScaleOffset();
        initBitmapToDraw();

        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {

        canvas.drawRect(_rectVisible, _paintBackground);
        canvas.drawRect(_rectView, _paintTransparent);

        if(_btimapToDraw != null) {
            canvas.drawBitmap(_btimapToDraw,
                                _rectVisible.left + (_rectVisible.width() - _btimapToDraw.getWidth())/2,
                                _rectVisible.top, null);
        }

        canvas.drawText("Scale: " + _scaleCurrent, 0, 30, _paintText);
        canvas.drawText("Min Scale: " + _scaleMinimum, 0, 70, _paintText);
        canvas.drawText("offsetV: " + _offsetV, 0, 110, _paintText);
        canvas.drawText("offsetH: " + _offsetH, 0, 150, _paintText);
    }

    @Override
    public void onSizeChanged (int w, int h, int oldw, int oldh) {

        // TODO paddings

       if( w * RATIO >= h ) {
           _height = h;
           _width = (int)(h / RATIO);
        } else {
           _height = (int)(w * RATIO);
           _width = w;
        }
        _rectVisible.set( (w - _width)/2 , (h - _height)/2, (w + _width)/2 , (h + _height)/2);
        _rectView.set(0, 0, w - 1, h - 1);
    }

    /**
     * Initializes scale and offset basing on sizes of source bitmap and the view size.
     * Scale is initialized to fit bitmap by height. So vertical offset equals 0.
     * Horizontal offset is set to place the bitmap that will be drawn in the middle of the view.
     * Offsets are negative if bitmap size is less than view's size.
     */
    private void initScaleOffset() {
        if(_btimapSource == null) {
            _scaleCurrent = 1;
            _offsetV = 0;
            _offsetH = 0;
            return;
        }

        int visibleHeight = _rectVisible.height();
        int visibleWidth = _rectVisible.width();

        // Initial scale is relation between source bitmap height and visible rect
        _scaleCurrent = (float) visibleHeight / _btimapSource.getHeight();

        // This is minimal allowed scale
        _scaleMinimum = _scaleCurrent;

        // Initial vertical offset = 0
        _offsetV = 0;

        float bitmapRatio = (float)_btimapSource.getHeight() / _btimapSource.getWidth();
        int proportionalWidth = (int)((float)visibleHeight / bitmapRatio);

        // if bitmap width is less than visible width offset is negative.
        // This means that bitmap should be drawn in the middle of visible area
        if (proportionalWidth > visibleWidth) {
            _offsetH = (proportionalWidth - visibleWidth) / 2;
        } else {
            _offsetH = -1;
        }
    }

    /**
     * Prepares _btimapToDraw basing on source bitmap, current scale and offset
     */
    private void initBitmapToDraw() {

        // Scaled bitmap sizes
        int scaledBitmapWidth = (int)(_btimapSource.getWidth()*_scaleCurrent);
        int scaledBitmapHeight = (int)(_btimapSource.getHeight()*_scaleCurrent);

        // View's visible area sizes
        int visibleHeight = _rectVisible.height();
        int visibleWidth = _rectVisible.width();

        // calculating left and width of source bitmap to cut
        int cutLeft;
        int cutWidth;
        if(_offsetH >= 0) {
            cutLeft = _offsetH;
            cutWidth = Math.min(visibleWidth, scaledBitmapWidth);
        } else {
            cutLeft = 0;
            cutWidth = scaledBitmapWidth;
        }

        // calculating top and height of source bitmap to cut
        int cutTop;
        int cutHeight;
        if(_offsetV >= 0) {
            cutTop = _offsetV;
            cutHeight = Math.min(visibleHeight, scaledBitmapHeight);
        } else {
            cutTop = 0;
            cutHeight = scaledBitmapHeight;
        }



        Bitmap bmpScaled = Bitmap.createScaledBitmap(_btimapSource,
                                                        scaledBitmapWidth, scaledBitmapHeight,
                                                        false);

        // scaling and cutting bitmap
        _btimapToDraw = Bitmap.createBitmap(bmpScaled, cutLeft, cutTop, cutWidth, cutHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        boolean retVal = _scaleGestureDetector.onTouchEvent(event);
        retVal = _gestureDetector.onTouchEvent(event) || retVal;
        return retVal || super.onTouchEvent(event);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Getters and setters
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void setScale(float scale , boolean invalidate) {
        if((_btimapSource == null) || (_btimapToDraw == null)) {return;}

        if(scale >= _scaleMinimum) {
            _scaleCurrent = scale;
            initBitmapToDraw();
        }

        if(invalidate == true) {
            invalidate();
        }
    }

    public float getScale() {
        return _scaleCurrent;
    }

    public float getMinScale() {
        return _scaleMinimum;
    }

    public void setOffsetV(int offsetV, boolean invalidate) {

        if((_btimapSource == null) || (_btimapToDraw == null)) {return;}

        // visible area should always be within the bitmap
        if(offsetV < 0 )
        {
            offsetV = 0;
        } else {
            if ( offsetV + _rectVisible.height() > _btimapSource.getHeight() * _scaleCurrent ){
                offsetV  = (int)(_btimapSource.getHeight() * _scaleCurrent - _rectVisible.height());
            }
        }

        _offsetV = offsetV;
        initBitmapToDraw();

        if (invalidate == true) {
            invalidate();
        }
    }

    public int getOffsetV() {
        return _offsetV;
    }

    public void setOffsetH(int offsetH, boolean invalidate) {
        if((_btimapSource == null) || (_btimapToDraw == null)) {return;}

        // visible area should always be within the bitmap
        if(offsetH < 0 )
        {
            offsetH = 0;
        } else {
            if ( offsetH + _rectVisible.width() > _btimapSource.getWidth() * _scaleCurrent ){
                offsetH  = (int)(_btimapSource.getWidth() * _scaleCurrent - _rectVisible.width());
            }
        }

        _offsetH = offsetH;

        if (invalidate == true) {
            invalidate();
        }

    }

    public int getOffsetH() {
        return _offsetH;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // The scroll listener, used for scrolling
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private final GestureDetector.SimpleOnGestureListener _gestureListener
            = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            setOffsetH((int) (_offsetH + distanceX), false);
            setOffsetV((int) (_offsetV + distanceY), false);
            initBitmapToDraw();
            ViewCompat.postInvalidateOnAnimation(ScalableImageView.this);
            return true;
        }
    };


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // The scale listener, used for handling multi-finger scale gestures.
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private final ScaleGestureDetector.OnScaleGestureListener _scaleGestureListener
                                    = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        private double lastSpan;

        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            float lastSpanX = scaleGestureDetector.getCurrentSpanX();
            float lastSpanY = scaleGestureDetector.getCurrentSpanY();
            lastSpan = (float)Math.sqrt(Math.pow(lastSpanX, 2) + Math.pow(lastSpanY, 2));
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            float spanX = scaleGestureDetector.getCurrentSpanX();
            float spanY = scaleGestureDetector.getCurrentSpanY();
            double currentSpan = Math.sqrt(Math.pow(spanX, 2) + Math.pow(spanY, 2));

            float focusX = scaleGestureDetector.getFocusX();
            float focusY = scaleGestureDetector.getFocusY();

            float absX = (_offsetH + focusX) / _scaleCurrent;
            float absY = (_offsetV + focusY) / _scaleCurrent;

            _scaleCurrent = (float)Math.max( (double) (_scaleMinimum), _scaleCurrent * currentSpan / lastSpan);

            setOffsetH((int)(absX * _scaleCurrent - focusX), false) ;
            setOffsetV((int)(absY * _scaleCurrent - focusY), false) ;
            initBitmapToDraw();

            ViewCompat.postInvalidateOnAnimation(ScalableImageView.this);

            lastSpan = currentSpan;
            return true;
        }
    };


}

