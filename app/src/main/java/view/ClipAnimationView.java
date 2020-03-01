package view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


import androidx.annotation.Nullable;

/**
 * 自定义裁剪框缩放View
 * @author 李豪
 */
public class ClipAnimationView extends View {

    /**
     * 动画宽高
     */
    private float animWidth;
    private float animHeight;
    /**
     * 中点坐标
     */
    private float corrX;
    private float corrY;

    private float[] drawCorr = new float[]{0,0,0,0};

    private int duration = 500;

    private Paint mPaint;
    private Path mPath;
    private RectF innerRect;
    private RectF outRect;

    /**
     * 缩放方向
     */
    private boolean zoomOut = true;

    public ClipAnimationView(Context context) {
        super(context);
    }

    public ClipAnimationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public ClipAnimationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ClipAnimationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initPaint(){
        mPaint = new Paint();
        //填充方式
        mPaint.setStyle(Paint.Style.FILL);
        //阴影
        mPaint.setColor(Color.parseColor("#AA1C1C1C"));
        mPath = new Path();
        //内部框
        innerRect = new RectF(0,0,0,0);
        //外部框
        outRect = new RectF(0,0,0,0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //每次绘制前，都需要先调用 Path.reset()方法，清除原来的路径，这是一个坑，不加的话看不到效果
        mPath.reset();
        innerRect.set(drawCorr[0],drawCorr[1],drawCorr[2],drawCorr[3]);
        outRect.set(0,0,getWidth(),getHeight());
        //两个Path方向相反
        mPath.addRect(outRect, Path.Direction.CW);
        mPath.addRect(innerRect, Path.Direction.CCW);
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * 开始放大动画
     * @param left 左边距
     * @param top 上边距
     * @param right 右边距
     * @param bottom 下边距
     */
    public void startAnim(int left, int top, int right, int bottom){
        if(right <= left || bottom <= top){
            return;
        }
        animWidth = right - left;
        animHeight = bottom - top;
        corrX = (float) (right + left) / 2;
        corrY = (float) (bottom + top) / 2;
        ValueAnimator valueAnimator;
        if(zoomOut){
            valueAnimator = ValueAnimator.ofFloat(0f,getAnimTarget(animWidth,animHeight));
        }else {
            valueAnimator = ValueAnimator.ofFloat(getAnimTarget(animWidth,animHeight),0f);
        }
        valueAnimator.setDuration(duration);
        valueAnimator.addUpdateListener(updateListener);
        valueAnimator.start();
    }

    ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            getDrawCorr((float)animation.getAnimatedValue());
            invalidate();
        }
    };

    /**
     * 获取动画结束值
     * @param animWidth 最终阴影宽
     * @param animHeight 最终阴影高
     * @return 阴影宽和阴影高的斜边长
     */
    private float getAnimTarget(float animWidth, float animHeight){
        return (float) Math.sqrt(animWidth * animWidth + animHeight * animHeight) / 2;
    }

    /**
     * 获取下一个要绘制的坐标
     * @param animTarget 插值器返回的值
     */
    private void getDrawCorr(float animTarget){
        float x = animWidth/animHeight;
        float drawHeight = (float) Math.sqrt((animTarget * animTarget) /
                (1 + (animWidth * animWidth)/(animHeight * animHeight)));
        float drawWidth = drawHeight * x;

        drawCorr[0] = corrX - drawWidth;
        drawCorr[1] = corrY - drawHeight;
        drawCorr[2] = corrX + drawWidth;
        drawCorr[3] = corrY + drawHeight;
    }

    public void setDuration(int duration){
        this.duration = duration;
    }

    /**
     * 设置缩放动画方向
     * @param zoomOut 缩放方向
     */
    public void setZoomOut(boolean zoomOut){
        this.zoomOut = zoomOut;
    }


}
