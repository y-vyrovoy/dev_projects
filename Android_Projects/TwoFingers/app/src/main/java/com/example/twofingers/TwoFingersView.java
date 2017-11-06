package com.example.twofingers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
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
 * Created by Yura Vyrovoy on 10/27/2017.
 */

public class TwoFingersView extends View {
    private static final String TAG = TwoFingersView.class.getSimpleName();

    private Context _context;
    private float _width;
    private float _height;

    private Paint _paintTextTitle;
    private Paint _paintTextTech;
    private Paint _paintBounds;
    private Paint _paintCenterLine;

    private Rect _textBounds = new Rect();
    private String _text;

    private boolean _isScaled;

    private Point _bitmapCenter;

    private Point _fingerOne;
    private Point _fingerTwo;

    private Point _lastFingerOne;
    private Point _lastFingerTwo;

    private Bitmap _bitmapArrow;

    private float _rotationAngle;
    private RectF _rectBitmapRotated = new RectF();
    private RectF _rectBitmap = new RectF();

    private float _scale;

    private ScaleGestureDetector _scaleGestureDetector;
    private GestureDetectorCompat _gestureDetector;

    public TwoFingersView(Context context) {
        super(context);
        init(context);
    }

    public TwoFingersView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TwoFingersView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public TwoFingersView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        _context = context;

        _scaleGestureDetector = new ScaleGestureDetector(context, _scaleGestureListener);
        _gestureDetector = new GestureDetectorCompat(context, _gestureListener);

        _scale = 1;
        _rotationAngle = 0;

        _text = context.getResources().getString(R.string.app_name);

        int colorDarkGrey = Color.argb(100, 30, 30, 30);
        int colorLightGrey = Color.argb(100, 220, 220, 220);

        _paintTextTitle = new Paint();
        _paintTextTitle.setColor(colorDarkGrey);
        _paintTextTitle.setTextSize(60);
        _paintTextTitle.setTextAlign(Paint.Align.CENTER);

        _paintTextTech = new Paint();
        _paintTextTech.setTextSize(50);
        _paintTextTech.setColor(colorDarkGrey);

        _paintBounds = new Paint();
        _paintBounds.setStrokeWidth(1);
        _paintBounds.setColor(colorLightGrey);

        _paintCenterLine = new Paint();
        _paintCenterLine.setStrokeWidth(1);
        _paintCenterLine.setColor(Color.RED);

        _bitmapArrow = BitmapFactory.decodeResource(getResources(), R.drawable.ic_arrow);

        _bitmapCenter = new Point(0, 0);
    }

    @Override
    public void onDraw(Canvas canvas) {

        canvas.drawRect(_textBounds, _paintBounds);

        canvas.drawText(_text,
                _textBounds.centerX(),
                (_height - _textBounds.height()) / 2,
                _paintTextTitle);

        RectF rectBitmapBound = getMappedRect(_bitmapCenter.x, _bitmapCenter.y, _scale, _rotationAngle, _bitmapArrow);
        rectBitmapBound.offset(-1 * _bitmapArrow.getWidth()/2 * _scale,
                                -1 * _bitmapArrow.getHeight()/2 * _scale);

        canvas.drawRect(rectBitmapBound, _paintBounds);

        drawBitmapRotated(canvas,
                            _bitmapCenter.x, _bitmapCenter.y,
                            _scale, _rotationAngle, _bitmapArrow);

        canvas.drawText("scale: " + _scale, 10, 50, _paintTextTech);
        canvas.drawText("angle: " + _rotationAngle, 10, 100, _paintTextTech);

        canvas.drawLine(_bitmapCenter.x, 0, _bitmapCenter.x, _height, _paintCenterLine);
        canvas.drawLine(0, _bitmapCenter.y, _width, _bitmapCenter.y, _paintCenterLine);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.i(TAG, "onSizeChanged()");

        _width = w;
        _height = h;

        _paintTextTitle.getTextBounds(_text, 0, _text.length(), _textBounds);
        _textBounds.offset((int) (_width - _textBounds.width()) / 2,
                (int) (_height - _textBounds.height()) / 2);

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        int points = event.getPointerCount();

        switch (action & MotionEvent.ACTION_MASK) {
            case ACTION_DOWN:
                Log.i(TAG, "onTouchEvent(): ACTION_DOWN");
                break;

            case ACTION_POINTER_DOWN:
                if (points >= 2) {
                    _fingerOne = new Point((int) event.getX(0), (int) event.getY(0));
                    _fingerTwo = new Point((int) event.getX(1), (int) event.getY(1));
                    onRotationStart();
                }
                break;

            case ACTION_MOVE:
                if (_isScaled && (points >= 2)) {
                    _fingerOne = new Point((int) event.getX(0), (int) event.getY(0));
                    _fingerTwo = new Point((int) event.getX(1), (int) event.getY(1));
                    onRotate();
                }
                break;

            case ACTION_POINTER_UP:
                if (points < 2) {
                    onRotationEnds();
                }

                break;

            case ACTION_UP:
                Log.i(TAG, "onTouchEvent(): ACTION_UP");
                break;

        }

        boolean retVal = _scaleGestureDetector.onTouchEvent(event);
        retVal = _gestureDetector.onTouchEvent(event) || retVal;
        return retVal || super.onTouchEvent(event);
    }

    public void setScale(float scale) {
        _scale = scale;
    }

    public float getScale() {
        return _scale;
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


    private final ScaleGestureDetector.OnScaleGestureListener _scaleGestureListener
            = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        private double _lastSpan;


        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            float lastSpanX = scaleGestureDetector.getCurrentSpanX();
            float lastSpanY = scaleGestureDetector.getCurrentSpanY();
            _lastSpan = (float) Math.sqrt(Math.pow(lastSpanX, 2) + Math.pow(lastSpanY, 2));

            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            float spanX = scaleGestureDetector.getCurrentSpanX();
            float spanY = scaleGestureDetector.getCurrentSpanY();
            double currentSpan = Math.sqrt(Math.pow(spanX, 2) + Math.pow(spanY, 2));

            setScale(getScale() * (float) (currentSpan / _lastSpan));

            _lastSpan = currentSpan;
            return true;
        }
    };

    private final GestureDetector.SimpleOnGestureListener _gestureListener
            = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            _bitmapCenter.offset( (int)(-1 * distanceX), (int)(-1 * distanceY) );
            ViewCompat.postInvalidateOnAnimation(TwoFingersView.this);
            return true;
        }
    };

    private void onRotationStart() {
        _isScaled = true;

        if ( (_fingerOne != null) && (_fingerTwo != null)) {
            _lastFingerOne = _fingerOne;
            _lastFingerTwo = _fingerTwo;
        }

        ViewCompat.postInvalidateOnAnimation(TwoFingersView.this);
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
        ViewCompat.postInvalidateOnAnimation(TwoFingersView.this);
    }

    private void onRotationEnds() {
        _isScaled = false;
        ViewCompat.postInvalidateOnAnimation(TwoFingersView.this);
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
