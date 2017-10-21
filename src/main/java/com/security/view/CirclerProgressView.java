package com.security.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.security.activity.R;

/**
 * 自定义的圆形进度条;
 */
public class CirclerProgressView extends LinearLayout {

    private ImageView iv_icon;
    private TextView tv_note;

    private boolean progressEnabled=false;//默认设置不用progress;
    private long max=100;//设置圆形的最大值;
    private long progress;//设置当前的进度;
    private Paint paint;

    /**
     * 设置图标;
     * @param resId :图片资源的id;
     */
    public void setIcon(int resId) {
        iv_icon.setImageResource(resId);
    }

    /**
     * 设置文本
     * @param note:文本内容
     */
    public void setNote(String note) {
        tv_note.setText(note);
    }

    /**
     * 设置圆形进度条是否可用;
     * @param progressEnabled
     */
    public void setProgressEnabled(boolean progressEnabled) {
        this.progressEnabled = progressEnabled;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public CirclerProgressView(Context context) {
        this(context,null);
    }

    public CirclerProgressView(Context context,AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CirclerProgressView(Context context,AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View view = View.inflate(context, R.layout.view_circleprogressview,this);
        iv_icon = (ImageView) view.findViewById(R.id.iv_icon_cpv);
        tv_note = (TextView) findViewById(R.id.tv_note_cpv);

        //新建消除锯齿的Paint;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
    }

    /**
     *draw和onDraw的区别:
     * @param canvas
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);//绘制具体的内容
        if(progressEnabled){
            RectF oval = new RectF(iv_icon.getLeft(),iv_icon.getTop(),iv_icon.getRight(),iv_icon.getBottom());
            float startAngle = -90;
            float sweepAngle = progress * 360.f / max;
            boolean useCenter = false;// 是否保留两条边
            canvas.drawArc(oval,startAngle,sweepAngle,useCenter,paint);
        }

    }
}
