package com.security.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.security.activity.R;

/**
 * 自定义的宽高按比例设定的组件,用于按照宽高比例设定子控件的宽高;
 */
public class RatioLayout extends FrameLayout {

    //自定义的相对于宽(相对于高)的常量;
    public static final int RELATIVE_WIDTH = 0;
    public static final int RELATIVE_HEIGHT = 1;

    //从自定义属性中获取的值;
    private float ratio;
    private int relative;

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    public void setRelative(int relative) {
        this.relative = relative;
    }

    public RatioLayout(Context context) {
        this(context, null);
    }

    public RatioLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatioLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RatioLayout);
        ratio = array.getFloat(R.styleable.RatioLayout_ratio, 0);
        relative = array.getInt(R.styleable.RatioLayout_relative, 0);
        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY && ratio != 0 && relative == RELATIVE_WIDTH) {
            int width = MeasureSpec.getSize(widthMeasureSpec);

            //获取孩子的宽度;
            int childWidth = width - getPaddingLeft() - getPaddingRight();
            //获取孩子的高度;
            int childHeight = (int) (childWidth / ratio + 0.5f);

            int height = childHeight + getPaddingTop() + getPaddingBottom();

            // 主动测绘孩子.固定孩子的大小
            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);
            measureChildren(childWidthMeasureSpec, childHeightMeasureSpec);

            //测绘父组件;
            setMeasuredDimension(width, height);
        } else if (heightMode == MeasureSpec.EXACTLY && ratio != 0 && relative == RELATIVE_HEIGHT) {
            int height = MeasureSpec.getSize(heightMeasureSpec);

            //获取孩子的高度;
            int childHeight = height - getPaddingTop() - getPaddingBottom();
            //获取孩子的宽度;
            int childWidth = (int) (childHeight * ratio + 0.5f);

            int width = childWidth + getPaddingLeft() + getPaddingRight();

            // 主动测绘孩子.固定孩子的大小
            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);
            measureChildren(childWidthMeasureSpec, childHeightMeasureSpec);

            //测绘父组件;
            setMeasuredDimension(width, height);

        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
