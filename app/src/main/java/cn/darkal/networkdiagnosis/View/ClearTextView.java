package cn.darkal.networkdiagnosis.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import cn.darkal.networkdiagnosis.R;

/**
 * Created by xuzhou on 2016/8/16.
 */
public class ClearTextView extends EditText {
    private Context mContext;
    private Bitmap mClearButton;
    private Paint mPaint;

    private boolean mClearStatus;

    //按钮显示方式
    private ClearButtonMode mClearButtonMode;
    //初始化输入框右内边距
    private int mInitPaddingRight;
    //按钮的左右内边距，默认为3dp
    private int mButtonPadding = dp2px(3);


    public ClearTextView(Context context) {
        super(context);
        init(context, null);
    }


    public ClearTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ClearTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 初始化
     */
    private void init(Context context, AttributeSet attributeSet) {
        this.mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.EditTextField);

        switch (typedArray.getInteger(R.styleable.EditTextField_clearButtonMode, 0)) {
            case 1:
                mClearButtonMode = ClearButtonMode.ALWAYS;
                break;
            case 2:
                mClearButtonMode = ClearButtonMode.WHILEEDITING;
                break;
            case 3:
                mClearButtonMode = ClearButtonMode.UNLESSEDITING;
                break;
            default:
                mClearButtonMode = ClearButtonMode.NEVER;
                break;
        }

        int clearButton = typedArray.getResourceId(R.styleable.EditTextField_clearButtonDrawable, R.drawable.clear_button);
        typedArray.recycle();

        //按钮的图片
        mClearButton = ((BitmapDrawable) getDrawableCompat(clearButton)).getBitmap();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mInitPaddingRight = getPaddingRight();
    }

    /**
     * 按钮状态管理
     *
     * @param canvas onDraw的Canvas
     */
    private void buttonManager(Canvas canvas) {
        switch (mClearButtonMode) {
            case ALWAYS:
                drawBitmap(canvas, getRect(true));
                break;
            case WHILEEDITING:
                drawBitmap(canvas, getRect(hasFocus() && getText().length() > 0));
                break;
            case UNLESSEDITING:
                break;
            default:
                drawBitmap(canvas, getRect(false));
                break;
        }
    }

    /**
     * 设置输入框的内边距
     *
     * @param isShow 是否显示按钮
     */
    private void setPadding(boolean isShow) {
        int paddingRight = mInitPaddingRight + (isShow ? mClearButton.getWidth() + mButtonPadding + mButtonPadding : 0);

        setPadding(getPaddingLeft(), getPaddingTop(), paddingRight, getPaddingBottom());
    }

    /**
     * 取得显示按钮与不显示按钮时的Rect
     *
     * @param isShow 是否显示按钮
     */
    private Rect getRect(boolean isShow) {
        int left, top, right, bottom;

        right = isShow ? getMeasuredWidth() + getScrollX() - mButtonPadding - mButtonPadding : 0;
        left = isShow ? right - mClearButton.getWidth() : 0;
        top = isShow ? (getMeasuredHeight() - mClearButton.getHeight()) / 2 : 0;
        bottom = isShow ? top + mClearButton.getHeight() : 0;


        //更新输入框内边距
        setPadding(isShow);


        return new Rect(left, top, right, bottom);
    }

    /**
     * 绘制按钮图片
     *
     * @param canvas onDraw的Canvas
     * @param rect   图片位置
     */
    private void drawBitmap(Canvas canvas, Rect rect) {
        if (rect != null) {
            canvas.drawBitmap(mClearButton, null, rect, mPaint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();

        buttonManager(canvas);

        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                //判断是否点击到按钮所在的区域
                if (event.getX() - (getMeasuredWidth() - getPaddingRight()) >= 0) {
                    setError(null);
                    this.setText("");
                }
                break;
            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * 获取Drawable
     *
     * @param resourseId 资源ID
     */
    private Drawable getDrawableCompat(int resourseId) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            return getResources().getDrawable(resourseId, mContext.getTheme());
        } else {
            return getResources().getDrawable(resourseId);
        }
    }

    /**
     * 设置按钮左右内边距
     *
     * @param buttonPadding 单位为dp
     */
    public void setButtonPadding(int buttonPadding) {
        this.mButtonPadding = dp2px(buttonPadding);
    }

    /**
     * 设置按钮显示方式
     *
     * @param clearButtonMode 显示方式
     */
    public void setClearButtonMode(ClearButtonMode clearButtonMode) {
        this.mClearButtonMode = clearButtonMode;
    }

    public boolean isShowing() {
        return mClearStatus;
    }

    public int dp2px(float dipValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 按钮显示方式
     * NEVER   不显示清空按钮
     * ALWAYS  始终显示清空按钮
     * WHILEEDITING   输入框内容不为空且有获得焦点
     * UNLESSEDITING  输入框内容不为空且没有获得焦点
     */
    public enum ClearButtonMode {
        NEVER, ALWAYS, WHILEEDITING, UNLESSEDITING
    }
}
