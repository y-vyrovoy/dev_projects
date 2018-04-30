package com.example.myimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_POINTER_DOWN;
import static android.view.MotionEvent.ACTION_POINTER_UP;
import static android.view.MotionEvent.ACTION_UP;

/**
 * Created by Yura Vyrovoy on 10/17/2017.
 */

public class ScalableImageView extends View {

    private static final String TAG = ScalableImageView.class.getSimpleName();
    public static float RATIO = (float)4/3;
    private static final int SPLITTER_HALF_WIDTH = 3;

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

    private float _bitmapCenterX = 0;
    private float _bitmapCenterY = 0;


    private boolean _isScaled;

    private Point _fingerOne;
    private Point _fingerTwo;
    private Point _lastFingerOne;
    private Point _lastFingerTwo;

    private float _rotationAngle;

    // items that should be instantiated once, not in every onDraw
    private Bitmap _bitmapSource;
    private Paint _paintBackground;
    private Paint _paintTransparent;
    private Paint _paintText;
    private Paint _paintDividerFill;
    private Paint _paintDividerBorder;
    private Paint _paintTransformed;
    private Paint _paintCenterLine;
    private Paint _paintTextTech;

    private RectF _rectBitmapTransformed = new RectF();

    // Sets up interactions
    private ScaleGestureDetector _scaleGestureDetector;
    private GestureDetectorCompat _gestureDetector;


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

    private void init(Context context, AttributeSet attrs) {
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

        _rotationAngle = 0;

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


        _paintDividerBorder = new Paint();
        _paintDividerBorder.setColor(ContextCompat.getColor(this.getContext(), R.color.colorSplitterBorder));
        _paintDividerBorder.setStyle(Paint.Style.STROKE);
        _paintDividerBorder.setStrokeWidth(1);

        _paintDividerFill = new Paint();
        _paintDividerFill.setColor(ContextCompat.getColor(this.getContext(), R.color.colorSplitterFill));
        _paintDividerFill.setStyle(Paint.Style.FILL);

        _paintTransformed = new Paint();
        //_paintTransformed.setColor(ContextCompat.getColor(this.getContext(), ));
        _paintTransformed.setStyle(Paint.Style.FILL);


        int colorDarkGrey = Color.argb(100, 30, 30, 30);
        int colorLightGrey = Color.argb(100, 220, 220, 220);

        _paintCenterLine = new Paint();
        _paintCenterLine.setStrokeWidth(1);
        _paintCenterLine.setColor(Color.RED);

        _paintTextTech = new Paint();
        _paintTextTech.setTextSize(50);
        _paintTextTech.setColor(colorDarkGrey);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Other stuff
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void setImageBitmap(Bitmap bitmap) {
        Log.i(TAG,"setImageBitmap()");

        _bitmapSource = bitmap;

        initScaleOffset();
        calcTransition();

        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        Log.i(TAG,"onDraw()");

        canvas.clipRect(_rectVisible);

        // Scaled bitmap sizes
        canvas.drawRect(_rectVisible, _paintBackground);
        canvas.drawRect(_rectView, _paintTransparent);

        if(_bitmapSource != null) {

            canvas.drawRect(_rectBitmapTransformed, _paintTransformed);

            drawBitmapRotated(canvas,
                            _rectVisible.centerX() + _unscaledOffsetX * _scaleCurrent,
                            _rectVisible.centerY() + _unscaledOffsetY * _scaleCurrent,
                            _scaleCurrent, _rotationAngle, _bitmapSource);
        }

        // divider that shows where picture will be divided
        canvas.drawRect(_rectVisible.centerX() - SPLITTER_HALF_WIDTH,
                _rectVisible.top,
                _rectVisible.centerX() + SPLITTER_HALF_WIDTH,
                _rectVisible.bottom,
                _paintDividerFill);

        canvas.drawRect(_rectVisible.centerX() - SPLITTER_HALF_WIDTH,
                _rectVisible.top + 2,
                _rectVisible.centerX() + SPLITTER_HALF_WIDTH,
                _rectVisible.bottom - 2,
                _paintDividerBorder);

    }

    @Override
    public void onSizeChanged (int w, int h, int oldw, int oldh) {
        Log.i(TAG,"onSizeChanged()");

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

        calcTransition();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * Initializes scale and offset basing on sizes of source bitmap and the view size.
     * Scale is initialized to fit bitmap by height. So vertical offset equals 0.
     * Horizontal offset is set to place the bitmap that will be drawn in the middle of the view.
     * Offsets are negative if bitmap size is less than view's size.
     */
    private void initScaleOffset() {
        Log.i(TAG,"initScaleOffset()");

        _unscaledOffsetX = 0;
        _unscaledOffsetY = 0;

        if( (_bitmapSource == null) || _rectVisible.isEmpty()) {
            _scaleCurrent = 1;
            _scaleMinimum = 1;
            return;
        }

        int visibleHeight = _rectVisible.height();
        int visibleWidth = _rectVisible.width();

        // Initial scale is relation between source bitmap height and visible rect
        _scaleCurrent = (float) visibleHeight / _bitmapSource.getHeight();

        // This is minimal allowed scale
        _scaleMinimum = _scaleCurrent;
    }

    /**
     * Prepares _btimapToDraw basing on source bitmap, current scale and offset
     */
    private void calcTransition() {
        Log.i(TAG,"calcTransition()");

        if(_bitmapSource == null) {
            Log.e(TAG, "calcTransition(): _bitmapSource == null");
            _bitmapCenterX = 0;
            _bitmapCenterY = 0;
            return;
        }

        RectF rectBounds = getMappedRect(0, 0,
                                            _scaleCurrent, _rotationAngle, _bitmapSource);

        float scaledBitmapWidth = rectBounds.width();
        float scaledBitmapHeight = rectBounds.height();

        float scaledOffsetX = _unscaledOffsetX * _scaleCurrent;
        float scaledOffsetY = _unscaledOffsetY * _scaleCurrent;

        // View's visible area sizes
        int visibleWidth = _rectVisible.width();
        int visibleHeight = _rectVisible.height();

        // calculating left and width of source bitmap to cut

        if( _width > scaledBitmapWidth ) {
            // SCALED bitmap width is less than view width
            _unscaledOffsetX = 0;
        } else if(scaledOffsetX < visibleWidth/2 - scaledBitmapWidth/2) {
            // SCALED bitmap left is greater than view's left -> move bitmap left to view's left
            _unscaledOffsetX = (visibleWidth/2 - scaledBitmapWidth/2) / _scaleCurrent;
        } else if(scaledOffsetX > (scaledBitmapWidth - visibleWidth) / 2) {
            _unscaledOffsetX = -1 * (visibleWidth/2 - scaledBitmapWidth/2) / _scaleCurrent;
        }


        if( _height > scaledBitmapHeight ) {
            // SCALED bitmap height is less than view height
            _unscaledOffsetY = 0;
        } else if(scaledOffsetY < visibleHeight/2 - scaledBitmapHeight/2) {
            // SCALED bitmap top is greater than view's top -> move bitmap top to view's top
            _unscaledOffsetY = (visibleHeight/2 - scaledBitmapHeight/2) / _scaleCurrent;
        } else if(scaledOffsetY > (scaledBitmapHeight - visibleHeight) / 2) {
            // SCALED bitmap bottom is less than view's bottom -> move bitmap bottom to view's bottom
            _unscaledOffsetY = -1 * (visibleHeight/2 - scaledBitmapHeight/2) / _scaleCurrent;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        int action = event.getAction();
        int pointerCount = event.getPointerCount();

        switch (action & MotionEvent.ACTION_MASK) {
            case ACTION_DOWN:
                break;

            case ACTION_POINTER_DOWN:
                if (pointerCount > 1) {
                    _fingerOne = new Point((int)event.getX(0), (int)event.getY(0));
                    _fingerTwo = new Point((int)event.getX(1), (int)event.getY(1));
                    onRotationStart();
                }
                break;

            case ACTION_MOVE:
                if(_isScaled && (pointerCount > 1)) {
                    _fingerOne = new Point((int)event.getX(0), (int)event.getY(0));
                    _fingerTwo = new Point((int)event.getX(1), (int)event.getY(1));
                    onRotate();
                }
                break;

            case ACTION_POINTER_UP:
                if (pointerCount <= 1) {
                     onRotationEnds();
                }
                break;

            case ACTION_UP:
                break;

        }

        boolean retVal = _scaleGestureDetector.onTouchEvent(event);
        retVal = _gestureDetector.onTouchEvent(event) || retVal;
        return retVal || super.onTouchEvent(event);
    }

    private static RectF getMappedRect(float centerX, float centerY, float scale, float rotationAngleDegrees, Bitmap bitmap) {
        Matrix matrixCalc = new Matrix();
        RectF rectBitmapTransformed = new RectF();

        rectBitmapTransformed.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
        matrixCalc.preRotate(rotationAngleDegrees, bitmap.getWidth()/2 * scale, bitmap.getHeight()/2 * scale);
        matrixCalc.preScale(scale, scale);
        matrixCalc.postTranslate(centerX, centerY);
        matrixCalc.mapRect(rectBitmapTransformed);

        return rectBitmapTransformed;
    }

    private static void drawBitmapRotated(Canvas canvas, float centerX, float centerY, float scale, float rotationAngleDegrees, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.preRotate(rotationAngleDegrees, bitmap.getWidth()/2 * scale, bitmap.getHeight()/2 * scale);
        matrix.preScale(scale, scale);
        matrix.postTranslate(centerX - bitmap.getWidth()/2 * scale,
                centerY - bitmap.getHeight()/2 * scale);

        canvas.drawBitmap(bitmap, matrix, null);
    }

    public Bitmap getResultBitmap() {

        Rect rcResult = new Rect(0, 0,
                (int)(_rectVisible.width() / _scaleCurrent),
                (int)(_rectVisible.height() / _scaleCurrent));

        rcResult.offset((int)(-1 * _transitionX / _scaleCurrent), (int)(-1 * _transitionY / _scaleCurrent));

        Bitmap bmpReturn = Bitmap.createBitmap(_bitmapSource, rcResult.left, rcResult.top, rcResult.width(), rcResult.height());

        return bmpReturn;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Getters and setters
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void setScale(float scale) {
        if(_bitmapSource == null) {return;}

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

    public void setRotationAngle(float radAngle) {
        _rotationAngle = (float)(radAngle % (2 * Math.PI));
        if (_rotationAngle < 0) {
            _rotationAngle += 2 * Math.PI;
        }
        Log.i(TAG, "setRotationAngle(): " + _rotationAngle * 180 / Math.PI);
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
            _unscaledOffsetX -= distanceX / _scaleCurrent;
            _unscaledOffsetY -= distanceY / _scaleCurrent;
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

            //TODO try scaleGestureDetector.getScale
            setScale((float)Math.max( (double) (_scaleMinimum), _scaleCurrent * currentSpan / lastSpan));
            calcTransition();

            lastSpan = currentSpan;
            return true;
        }

    };

    private void onRotationStart() {
        _isScaled = true;

        if ( (_fingerOne != null) && (_fingerTwo != null)) {
            _lastFingerOne = _fingerOne;
            _lastFingerTwo = _fingerTwo;
        }

        ViewCompat.postInvalidateOnAnimation(ScalableImageView.this);
    }

    private void onRotate() {
        if ( (_fingerOne != null) && (_fingerTwo != null) &&
                (_lastFingerOne != null) && (_lastFingerTwo != null)) {

            _rotationAngle += Vector2D.
                    getSignedAngleBetween(new Vector2D(_lastFingerOne, _lastFingerTwo),
                            new Vector2D(_fingerOne, _fingerTwo));
            _rotationAngle %= 360;

            _lastFingerOne = _fingerOne;
            _lastFingerTwo = _fingerTwo;
        }
        ViewCompat.postInvalidateOnAnimation(ScalableImageView.this);
    }

    private void onRotationEnds() {
        _isScaled = false;
        ViewCompat.postInvalidateOnAnimation(ScalableImageView.this);
    }

    public static  class Vector2D {

        private float _x;
        private float _y;

        public Vector2D() {
            _x = 0;
            _y = 0;
        }

        public Vector2D(float x, float y) {
            _x = x;
            _y = y;
        }

        public Vector2D(Point point) {
            _x = point.x;
            _y = point.y;
        }

        public Vector2D(Point pointOne, Point pointTwo) {
            _x = pointOne.x - pointTwo.x;
            _y = pointOne.y - pointTwo.y;
        }

        public float getLength() {
            return (float)Math.sqrt(Math.pow(_x, 2) + Math.pow(_y, 2));
        }

        public static Vector2D getNormalized(Vector2D v) {
            float l = v.getLength();
            if (l == 0)
                return new Vector2D();
            else
                return new Vector2D(v._x / l, v._y / l);
        }

        public static float getSignedAngleBetween(Vector2D a, Vector2D b) {
            Vector2D na = getNormalized(a);
            Vector2D nb = getNormalized(b);

            double radAngle = Math.atan2(nb._y, nb._x) - Math.atan2(na._y, na._x);

            if ( (na._x * nb._x * na._y * nb._y < 0) ) {
                radAngle *= -1;
            }

            double gradAngle = radAngle * 180 / Math.PI;
            gradAngle %= 360;

            if (gradAngle < -180.f) gradAngle += 360.0f;
            if (gradAngle > 180.f) gradAngle -= 360.0f;

            return (float)gradAngle;

        }
    }

}

