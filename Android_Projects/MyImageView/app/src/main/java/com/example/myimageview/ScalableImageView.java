package com.example.myimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
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

    private float _scaleCurrent = 1;
    private float _scaleMinimum = 1;
    private float _unscaledOffsetX = 0;
    private float _unscaledOffsetY = 0;

    private float _transitionX = 0;
    private float _transitionY = 0;

    // items that should be instantiated once, not in every onDraw
    private Bitmap _btimapSource;
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
                setImageBitmap(((BitmapDrawable) drawableSrc).getBitmap());
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
        calcTransition();

        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {

        // Scaled bitmap sizes
        canvas.drawRect(_rectVisible, _paintBackground);
        canvas.drawRect(_rectView, _paintTransparent);

        Matrix matrix = new Matrix();
        matrix.preScale(_scaleCurrent, _scaleCurrent);
        matrix.postTranslate( _transitionX, _rectVisible.top + _transitionY);

        canvas.clipRect(_rectVisible);
        canvas.drawBitmap(_btimapSource, matrix, null);
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

        _unscaledOffsetX = 0;
        _unscaledOffsetY = 0;


        if(_btimapSource == null) {
            _scaleCurrent = 1;
            return;
        }


        int visibleHeight = _rectVisible.height();
        int visibleWidth = _rectVisible.width();

        // Initial scale is relation between source bitmap height and visible rect
        _scaleCurrent = (float) visibleHeight / _btimapSource.getHeight();

        // This is minimal allowed scale
        _scaleMinimum = _scaleCurrent;
    }

    /**
     * Prepares _btimapToDraw basing on source bitmap, current scale and offset
     */
    private void calcTransition() {

        // Scaled bitmap sizes
        float scaledBitmapWidth = _btimapSource.getWidth() * _scaleCurrent;
        float scaledBitmapHeight = _btimapSource.getHeight() * _scaleCurrent;

        float scaledOffsetX = _unscaledOffsetX * _scaleCurrent;
        float scaledOffsetY = _unscaledOffsetY * _scaleCurrent;

        // View's visible area sizes
        int visibleWidth = _rectVisible.width();
        int visibleHeight = _rectVisible.height();

        // calculating left and width of source bitmap to cut

        if( _width > scaledBitmapWidth ) {
            // scaled bitmap width is less than view width
            _transitionX = (visibleWidth - scaledBitmapWidth)/2;
            _unscaledOffsetX = 0;
        } else if(scaledOffsetX < visibleWidth/2 - scaledBitmapWidth/2) {
            // SCALED bitmap left is greater than view's left -> move bitmap left to view's left
            scaledOffsetX = visibleWidth/2 - scaledBitmapWidth/2;
            _unscaledOffsetX = scaledOffsetX / _scaleCurrent;
            _transitionX = 0;
        } else if(scaledOffsetX > (scaledBitmapWidth - visibleWidth) / 2) {
            // SCALED bitmap right is less than view's right -> move bitmap right to view's right
            scaledOffsetX = (scaledBitmapWidth - visibleWidth) / 2;
            _unscaledOffsetX = scaledOffsetX / _scaleCurrent;
            _transitionX = visibleWidth/2 - scaledBitmapWidth/2 - scaledOffsetX;
        } else {
            _transitionX = visibleWidth/2 - scaledBitmapWidth/2 - scaledOffsetX;
        }

        if( _height > scaledBitmapHeight ) {
            // scaled bitmap heigth is less than view height
            _transitionY = (visibleHeight - scaledBitmapHeight)/2;
            _unscaledOffsetY = 0;
        } else if(scaledOffsetY < visibleHeight/2 - scaledBitmapHeight/2) {
            // SCALED bitmap top is greater than view's top -> move bitmap top to view's top
            scaledOffsetY = visibleHeight/2 - scaledBitmapHeight/2;
            _unscaledOffsetY = scaledOffsetY / _scaleCurrent;
            _transitionY = 0;
        } else if(scaledOffsetY > (scaledBitmapHeight - visibleHeight) / 2) {
            // SCALED bitmap bottom is less than view's bottom -> move bitmap bottom to view's bottom
            scaledOffsetY = (scaledBitmapHeight - visibleHeight) / 2;
            _unscaledOffsetY = scaledOffsetY / _scaleCurrent;
            _transitionY = visibleHeight/2 - scaledBitmapHeight/2 - scaledOffsetY;
        } else {
            _transitionY = visibleHeight/2 - scaledBitmapHeight/2 - scaledOffsetY;
        }

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

    public void setScale(float scale) {
        if(_btimapSource == null) {return;}

        if(scale >= _scaleMinimum) {
            _scaleCurrent = scale;
        }
    }

    public float getScale() {
        return _scaleCurrent;
    }

    public float getMinScale() {
        return _scaleMinimum;
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
            _unscaledOffsetX += distanceX / _scaleCurrent;
            _unscaledOffsetY += distanceY / _scaleCurrent;
            calcTransition();

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

            setScale((float)Math.max( (double) (_scaleMinimum), _scaleCurrent * currentSpan / lastSpan));
            calcTransition();

            ViewCompat.postInvalidateOnAnimation(ScalableImageView.this);

            lastSpan = currentSpan;
            return true;
        }
    };


}

