package com.security.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * 进度Button;
 */
public class ProgressButton extends Button {

    //是否允许进度;
    private boolean progessEnabled = true;
    private long max = 100;//进度最大值;
    private long progress = 75;//当前进度;

    /**
     * 是否允许进度;
     */
    public void setProgessEnabled(boolean progessEnabled) {
        this.progessEnabled = progessEnabled;
    }

    /**
     * 进度最大值;
     */
    public void setMax(long max) {
        this.max = max;
    }

    /**
     * 当前进度;
     */
    public void setProgress(long progress) {
        this.progress = progress;
    }

    public ProgressButton(Context context) {
        super(context);
    }

    public ProgressButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (progessEnabled) {
            Drawable drawable = new ColorDrawable(Color.BLUE);
            int left = 0;
            int top = 0;
            int right = (int) (progress * 1.0f / max * getMeasuredWidth() + 0.5f);
            int bottom = getBottom();
            drawable.setBounds(left, top, right, bottom);
            drawable.draw(canvas);
        }

        super.onDraw(canvas);//绘制文本和背景;
    }
}
