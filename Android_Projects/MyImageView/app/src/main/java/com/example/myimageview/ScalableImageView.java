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
import android.util.AttributeSet;
import android.util.Log;
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
    private Bitmap _btimapToDraw;
    private Paint _paintBackground;
    private Paint _paintTransparent;


    public ScalableImageView(Context context) {
        super(context);
    }

    public ScalableImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ScalableImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ScalableImageView);

        Drawable drawableSrc = a.getDrawable(R.styleable.ScalableImageView_src);
        if(drawableSrc != null) {
            _btimapSource = ((BitmapDrawable) drawableSrc).getBitmap();
        }

        //Don't forget this
        a.recycle();

        _paintBackground = new Paint();
        _paintBackground.setStyle(Paint.Style.FILL);
        _paintBackground.setColor(Color.GRAY);

        _paintTransparent = new Paint();
        _paintTransparent.setStyle(Paint.Style.STROKE);
        _paintTransparent.setColor(Color.BLACK);
        _paintTransparent.setStrokeWidth(1);
    }

    public void setImageBitmap(Bitmap bitmap) {
        _btimapSource = bitmap;

        initScaleOffset();
        recalcVisibleBitmap();

        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i(TAG, "onMeasure()");

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int desiredWidth = _btimapSource.getWidth();
        int desiredHeight = _btimapSource.getHeight();

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
//            width = Math.min(desiredWidth, widthSize);
            width = widthSize;
        } else {
            //Be whatever you want
            width = desiredWidth;
        }
        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
//            height = Math.min(desiredHeight, heightSize);
            height = heightSize;
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }

    @Override
    public void onDraw(Canvas canvas) {

        canvas.drawRect(_rectVisible, _paintBackground);
        canvas.drawRect(_rectView, _paintTransparent);

        if(_btimapToDraw != null) {
            canvas.drawBitmap(_btimapToDraw,
                                (_rectVisible.width() - _btimapToDraw.getWidth())/2,
                                _rectVisible.top, null);
        }
    }

    @Override
    public void onSizeChanged (int w, int h, int oldw, int oldh) {
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
    private void recalcVisibleBitmap() {

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
            cutWidth = visibleWidth;
        } else {
            cutLeft = 0;
            cutWidth = scaledBitmapWidth;
        }

        // calculating top and height of source bitmap to cut
        int cutTop;
        int cutHeight;
        if(_offsetV >= 0) {
            cutTop = _offsetV;
            cutHeight = visibleHeight;
        } else {
            cutTop = 0;
            cutHeight = scaledBitmapHeight;
        }


        // scaling and cutting bitmap
        _btimapToDraw = Bitmap.createBitmap(
                            Bitmap.createScaledBitmap(_btimapSource,
                                                        scaledBitmapWidth, scaledBitmapHeight,
                                                        false),
                            cutLeft, cutTop, cutWidth, cutHeight);
    }

    public void setScale(float scale , boolean invalidate) {
        if(scale >= _scaleMinimum) {
            _scaleCurrent = scale;
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
        _offsetV = offsetV;
        recalcVisibleBitmap();

        if(invalidate == true) {
            invalidate();
        }
    }

    public int getOffsetV() {
        return _offsetV;
    }

    public void setOffsetH(int offsetH, boolean invalidate) {
        _offsetH = offsetH;
        recalcVisibleBitmap();

        if(invalidate == true) {
            invalidate();
        }
    }

    public int getOffsetH() {
        return _offsetH;
    }

}
