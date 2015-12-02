

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * CropImageView
 */
public class CropImageView extends ImageView {

    private int mHalfTouchSpace;

    private final int COLOR_RECT_CROP = Color.parseColor("#000000");
    private final int COLOR_LINE_CROP = Color.parseColor("#87ffffff");
    private final int COLOR_SHADOW = Color.parseColor("#87000000");
    private final int COLOR_DOT = Color.parseColor("#ffffff");

    private Rect cropAreaRect;
    private Point drawablePoint;

    private Drawable mCropDrawable;

    private int mCropViewHeight;
    private int mCropViewWidth;

    private Rect mCropRect;
    private Rect mImgRect;

    private Paint mCropPaint;
    private Paint mShadowPaint;
    private Paint mDotPaint;
    private Paint mLinePaint;

    private Point mDownPoint, mMovePoint;
    private Point mOldPoint, mNewPoint;

    private enum Loc {
        LOC_LEFT_TOP, LOC_LEFT_BOTTOM, LOC_RIGHT_TOP, LOC_RIGHT_BOTTOM,
        LOC_LEFT, LOC_TOP, LOC_RIGHT, LOC_BOTTOM, LOC_CENTER, LOC_OUTAREA
    }

    private Loc loc;

    private CropAreaRectWatcher watcher;

    public CropImageView(Context context) {
        super(context);
        initData();
    }

    public CropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    public CropImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
    }

    private void initData() {
        mHalfTouchSpace = getPxfromDp(12);
        loc = Loc.LOC_OUTAREA;
        mCropRect = new Rect();
        mImgRect = new Rect();
        mShadowPaint = new Paint();
        mShadowPaint.setStyle(Paint.Style.FILL);
        mShadowPaint.setColor(COLOR_SHADOW);
        mCropPaint = new Paint();
        mCropPaint.setStyle(Paint.Style.STROKE);
        mCropPaint.setStrokeWidth(2);
        mCropPaint.setColor(COLOR_RECT_CROP);
        mDotPaint = new Paint();
        mDotPaint.setAntiAlias(true);
        mDotPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mDotPaint.setColor(COLOR_DOT);
        mLinePaint = new Paint();
        mLinePaint.setColor(COLOR_LINE_CROP);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setStrokeWidth(2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mCropViewWidth = getMeasuredWidth();
        mCropViewHeight = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null == getDrawable()) {
            return;
        } else if (mCropDrawable == null) {
            mCropDrawable = getDrawable();
            updateMImgRect();
        } else {
            if (!mCropDrawable.equals(getDrawable())) {
                updateMImgRect();
                mCropDrawable = getDrawable();
            }
        }

        drawCropRect(canvas);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownPoint = new Point((int) event.getX(), (int) event.getY());
                mOldPoint = new Point(mDownPoint);
                loc = getLocType(mDownPoint);
                Log.d("LOC_TYPE",loc.toString());
                if (loc == Loc.LOC_OUTAREA) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mMovePoint = new Point((int) event.getX(), (int) event.getY());
                mNewPoint = new Point((int) event.getX(), (int) event.getY());
                int dx = mNewPoint.x - mOldPoint.x;
                int dy = mNewPoint.y - mOldPoint.y;
                switch (loc) {
                    case LOC_LEFT_TOP:
                        mCropRect.left += dx;
                        mCropRect.top += dy;
                        if (mCropRect.left < mImgRect.left) {
                            mCropRect.left = mImgRect.left;
                        }
                        if (mCropRect.top < mImgRect.top) {
                            mCropRect.top = mImgRect.top;
                        }
                        if (mCropRect.right - mCropRect.left < mHalfTouchSpace * 2) {
                            mCropRect.left = mCropRect.right - mHalfTouchSpace * 2;
                        }
                        if (mCropRect.bottom - mCropRect.top < mHalfTouchSpace * 2) {
                            mCropRect.top = mCropRect.bottom - mHalfTouchSpace * 2;
                        }
                        break;
                    case LOC_TOP:
                        mCropRect.top += dy;
                        if (mCropRect.top < mImgRect.top) {
                            mCropRect.top = mImgRect.top;
                        }
                        if (mCropRect.bottom - mCropRect.top < mHalfTouchSpace * 2) {
                            mCropRect.top = mCropRect.bottom - mHalfTouchSpace * 2;
                        }
                        break;
                    case LOC_RIGHT_TOP:
                        mCropRect.right += dx;
                        mCropRect.top += dy;
                        if (mCropRect.right > mImgRect.right) {
                            mCropRect.right = mImgRect.right;
                        }
                        if (mCropRect.top < mImgRect.top) {
                            mCropRect.top = mImgRect.top;
                        }
                        if (mCropRect.right - mCropRect.left < mHalfTouchSpace * 2) {
                            mCropRect.right = mCropRect.left + mHalfTouchSpace * 2;
                        }
                        if (mCropRect.bottom - mCropRect.top < mHalfTouchSpace * 2) {
                            mCropRect.top = mCropRect.bottom - mHalfTouchSpace * 2;
                        }
                        break;
                    case LOC_LEFT:
                        mCropRect.left += dx;
                        if (mCropRect.left < mImgRect.left) {
                            mCropRect.left = mImgRect.left;
                        }
                        if (mCropRect.right - mCropRect.left < mHalfTouchSpace * 2) {
                            mCropRect.left = mCropRect.right - mHalfTouchSpace * 2;
                        }
                        break;
                    case LOC_CENTER:
                        mCropRect.left += dx;
                        mCropRect.right += dx;
                        mCropRect.top += dy;
                        mCropRect.bottom += dy;
                        if (mCropRect.left < mImgRect.left) {
                            mCropRect.right = mImgRect.left + mCropRect.right - mCropRect.left;
                            mCropRect.left = mImgRect.left;
                        }
                        if (mCropRect.top < mImgRect.top) {
                            mCropRect.bottom = mImgRect.top + mCropRect.bottom - mCropRect.top;
                            mCropRect.top = mImgRect.top;
                        }
                        if (mCropRect.right > mImgRect.right) {
                            mCropRect.left = mImgRect.right - (mCropRect.right - mCropRect.left);
                            mCropRect.right = mImgRect.right;
                        }
                        if (mCropRect.bottom > mImgRect.bottom) {
                            mCropRect.top = mImgRect.bottom - (mCropRect.bottom - mCropRect.top);
                            mCropRect.bottom = mImgRect.bottom;
                        }
                        break;
                    case LOC_RIGHT:
                        mCropRect.right += dx;
                        if (mCropRect.right > mImgRect.right) {
                            mCropRect.right = mImgRect.right;
                        }
                        if (mCropRect.right - mCropRect.left < mHalfTouchSpace * 2) {
                            mCropRect.right = mCropRect.left + mHalfTouchSpace * 2;
                        }
                        break;
                    case LOC_LEFT_BOTTOM:
                        mCropRect.left += dx;
                        mCropRect.bottom += dy;
                        if (mCropRect.left < mImgRect.left) {
                            mCropRect.left = mImgRect.left;
                        }
                        if (mCropRect.bottom > mImgRect.bottom) {
                            mCropRect.bottom = mImgRect.bottom;
                        }
                        if (mCropRect.right - mCropRect.left < mHalfTouchSpace * 2) {
                            mCropRect.left = mCropRect.right - mHalfTouchSpace * 2;
                        }
                        if (mCropRect.bottom - mCropRect.top < mHalfTouchSpace * 2) {
                            mCropRect.bottom = mCropRect.top + mHalfTouchSpace * 2;
                        }
                        break;
                    case LOC_BOTTOM:
                        mCropRect.bottom += dy;
                        if (mCropRect.bottom > mImgRect.bottom) {
                            mCropRect.bottom = mImgRect.bottom;
                        }
                        if (mCropRect.bottom - mCropRect.top < mHalfTouchSpace * 2) {
                            mCropRect.bottom = mCropRect.top + mHalfTouchSpace * 2;
                        }
                        break;
                    case LOC_RIGHT_BOTTOM:
                        mCropRect.right += dx;
                        mCropRect.bottom += dy;
                        if (mCropRect.right > mImgRect.right) {
                            mCropRect.right = mImgRect.right;
                        }
                        if (mCropRect.bottom > mImgRect.bottom) {
                            mCropRect.bottom = mImgRect.bottom;
                        }
                        if (mCropRect.right - mCropRect.left < mHalfTouchSpace * 2) {
                            mCropRect.right = mCropRect.left + mHalfTouchSpace * 2;
                        }
                        if (mCropRect.bottom - mCropRect.top < mHalfTouchSpace * 2) {
                            mCropRect.bottom = mCropRect.top + mHalfTouchSpace * 2;
                        }
                        break;
                    default:
                        break;

                }
                watcher.onMove(getCropAreaRectF());
                invalidate();
                mOldPoint = new Point(mNewPoint);
                break;
            case MotionEvent.ACTION_UP:
                watcher.onUp(getCropAreaRectF());
                break;
            default:
                break;
        }

        return true;
    }

    private Loc getLocType(Point mDownPoint) {
        if (mDownPoint.x > mCropRect.left - mHalfTouchSpace * 2 && mDownPoint.x < mCropRect.left + mHalfTouchSpace * 2
                && mDownPoint.y > mCropRect.top - mHalfTouchSpace * 2 && mDownPoint.y < mCropRect.top + mHalfTouchSpace * 2) {
            return Loc.LOC_LEFT_TOP;
        } else if (mDownPoint.x > mCropRect.left + mHalfTouchSpace * 2 && mDownPoint.x < mCropRect.right - mHalfTouchSpace * 2
                && mDownPoint.y > mCropRect.top - mHalfTouchSpace * 2 && mDownPoint.y < mCropRect.top + mHalfTouchSpace * 2) {
            return Loc.LOC_TOP;
        } else if (mDownPoint.x > mCropRect.right - mHalfTouchSpace * 2 && mDownPoint.x < mCropRect.right + mHalfTouchSpace * 2
                && mDownPoint.y > mCropRect.top - mHalfTouchSpace * 2 && mDownPoint.y < mCropRect.top + mHalfTouchSpace * 2) {
            return Loc.LOC_RIGHT_TOP;
        } else if (mDownPoint.x > mCropRect.left - mHalfTouchSpace * 2 && mDownPoint.x < mCropRect.left + mHalfTouchSpace * 2
                && mDownPoint.y > mCropRect.top + mHalfTouchSpace * 2 && mDownPoint.y < mCropRect.bottom - mHalfTouchSpace * 2) {
            return Loc.LOC_LEFT;
        } else if (mDownPoint.x > mCropRect.left + mHalfTouchSpace * 2 && mDownPoint.x < mCropRect.right - mHalfTouchSpace * 2
                && mDownPoint.y > mCropRect.top + mHalfTouchSpace * 2 && mDownPoint.y < mCropRect.bottom - mHalfTouchSpace * 2) {
            return Loc.LOC_CENTER;
        } else if (mDownPoint.x > mCropRect.right - mHalfTouchSpace * 2 && mDownPoint.x < mCropRect.right + mHalfTouchSpace * 2
                && mDownPoint.y > mCropRect.top + mHalfTouchSpace * 2 && mDownPoint.y < mCropRect.bottom - mHalfTouchSpace * 2) {
            return Loc.LOC_RIGHT;
        } else if (mDownPoint.x > mCropRect.left - mHalfTouchSpace * 2 && mDownPoint.x < mCropRect.left + mHalfTouchSpace * 2
                && mDownPoint.y > mCropRect.bottom - mHalfTouchSpace * 2 && mDownPoint.y < mCropRect.bottom + mHalfTouchSpace * 2) {
            return Loc.LOC_LEFT_BOTTOM;
        } else if (mDownPoint.x > mCropRect.left + mHalfTouchSpace * 2 && mDownPoint.x < mCropRect.right - mHalfTouchSpace * 2
                && mDownPoint.y > mCropRect.bottom - mHalfTouchSpace * 2 && mDownPoint.y < mCropRect.bottom + mHalfTouchSpace * 2) {
            return Loc.LOC_BOTTOM;
        } else if (mDownPoint.x > mCropRect.right - mHalfTouchSpace * 2 && mDownPoint.x < mCropRect.right + mHalfTouchSpace * 2
                && mDownPoint.y > mCropRect.bottom - mHalfTouchSpace * 2 && mDownPoint.y < mCropRect.bottom + mHalfTouchSpace * 2) {
            return Loc.LOC_RIGHT_BOTTOM;
        }
        return Loc.LOC_OUTAREA;
    }

    private void drawCropRect(Canvas canvas) {
        canvas.drawLine(mCropRect.left, mCropRect.top + (mCropRect.bottom - mCropRect.top) / 3,
                mCropRect.right, mCropRect.top + (mCropRect.bottom - mCropRect.top) / 3, mLinePaint);
        canvas.drawLine(mCropRect.left, mCropRect.top + (mCropRect.bottom - mCropRect.top) * 2 / 3,
                mCropRect.right, mCropRect.top + (mCropRect.bottom - mCropRect.top) * 2 / 3, mLinePaint);
        canvas.drawLine(mCropRect.left + (mCropRect.right - mCropRect.left) / 3, mCropRect.top,
                mCropRect.left + (mCropRect.right - mCropRect.left) / 3, mCropRect.bottom, mLinePaint);
        canvas.drawLine(mCropRect.left + (mCropRect.right - mCropRect.left) * 2 / 3, mCropRect.top,
                mCropRect.left + (mCropRect.right - mCropRect.left) * 2 / 3, mCropRect.bottom, mLinePaint);
//        canvas.drawRect(mImgRect.left, mImgRect.top, mImgRect.right, mCropRect.top, mShadowPaint);
//        canvas.drawRect(mImgRect.left, mCropRect.top, mCropRect.left, mCropRect.bottom, mShadowPaint);
//        canvas.drawRect(mCropRect.right, mCropRect.top, mImgRect.right, mCropRect.bottom, mShadowPaint);
//        canvas.drawRect(mImgRect.left, mCropRect.bottom, mImgRect.right, mImgRect.bottom, mShadowPaint);
        canvas.drawRect(0, 0, mCropViewWidth, mCropRect.top, mShadowPaint);
        canvas.drawRect(0, mCropRect.top, mCropRect.left, mCropRect.bottom, mShadowPaint);
        canvas.drawRect(mCropRect.right, mCropRect.top, mCropViewWidth, mCropRect.bottom, mShadowPaint);
        canvas.drawRect(0, mCropRect.bottom, mCropViewWidth, mCropViewHeight, mShadowPaint);

        canvas.drawRect(mCropRect, mCropPaint);
        canvas.drawCircle(mCropRect.left, mCropRect.top, mHalfTouchSpace, mDotPaint);
        canvas.drawCircle(mCropRect.right, mCropRect.top, mHalfTouchSpace, mDotPaint);
        canvas.drawCircle(mCropRect.left, mCropRect.bottom, mHalfTouchSpace, mDotPaint);
        canvas.drawCircle(mCropRect.right, mCropRect.bottom, mHalfTouchSpace, mDotPaint);

    }

    private void updateMImgRect() {
        Log.i("updateMImgRect", "updateMImgRect");
        Matrix m = getImageMatrix();
        float[] values = new float[10];
        m.getValues(values);
        // Image在绘制过程中的变换矩阵，从中获得x和y方向的缩放系数
        float sx = values[0];
        float sy = values[4];
        // 计算Image在屏幕上实际绘制的宽高
        float showImgWidth = (getDrawable().getBounds().width() * sx);
        float showImgHeight = (getDrawable().getBounds().height() * sy);
        Log.w("IMG", showImgWidth + "  " + showImgHeight);
        RectF tempRectF = new RectF((mCropViewWidth - showImgWidth) * 1.00f / 2,
                (mCropViewHeight - showImgHeight) * 1.00f / 2,
                (mCropViewWidth - showImgWidth) * 1.00f / 2 + showImgWidth,
                (mCropViewHeight - showImgHeight) * 1.00f / 2 + showImgHeight);
        tempRectF.round(mImgRect);
        setmCropRect(cropAreaRect);
    }

    public RectF getCropAreaRectF() {
        RectF cropAreaRectF = new RectF();
        cropAreaRectF.left = (mCropRect.left - mImgRect.left) * 1.0f / mImgRect.width();
        cropAreaRectF.top = (mCropRect.top - mImgRect.top) * 1.0f / mImgRect.height();
        cropAreaRectF.right = (mCropRect.right - mImgRect.left) * 1.0f / mImgRect.width();
        cropAreaRectF.bottom = (mCropRect.bottom - mImgRect.top) * 1.0f / mImgRect.height();
        return cropAreaRectF;
    }

    private void setmCropRect(RectF cropAreaRectF) {

        mCropRect.left = (int) (mImgRect.left + mImgRect.width() * cropAreaRectF.left);
        mCropRect.top = (int) (mImgRect.top + mImgRect.height() * cropAreaRectF.top);
        mCropRect.right = (int) (mImgRect.left + mImgRect.width() * cropAreaRectF.right);
        mCropRect.bottom = (int) (mImgRect.top + mImgRect.height() * cropAreaRectF.bottom);
        Log.d("mCropRect2", mCropRect.toString());
    }

    public void setCropAreaRect(Rect cropAreaRec, Point drawablePoint) {
        this.cropAreaRect = cropAreaRec;
        this.drawablePoint = drawablePoint;
    }

    private void setmCropRect(Rect cropAreaRect) {
        RectF cropAreaRectF = new RectF();
        Log.d("set-cropAreaRect", cropAreaRect.toString());
        Log.d("set-mImgRect", mImgRect.toString());
        Log.d("set-getBounds", getDrawable().getBounds().toString());
        cropAreaRectF.left = (cropAreaRect.left * 1.0f / drawablePoint.x);
        cropAreaRectF.top = (cropAreaRect.top * 1.0f / drawablePoint.y);
        cropAreaRectF.right = (cropAreaRect.right * 1.0f / drawablePoint.x);
        cropAreaRectF.bottom = (cropAreaRect.bottom * 1.0f / drawablePoint.y);
        Log.d("cropAreaRectF", cropAreaRectF.toString());
        setmCropRect(cropAreaRectF);
    }

    public void reset() {
        mCropRect = new Rect(mImgRect);
        invalidate();
    }

    private int getPxfromDp(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public void setCropAreaRectWatcher(CropAreaRectWatcher watcher){
        this.watcher = watcher;
    }

    public interface CropAreaRectWatcher {
        void onMove(RectF CropAreaRectF);
        void onUp(RectF CropAreaRectF);
    }

}
