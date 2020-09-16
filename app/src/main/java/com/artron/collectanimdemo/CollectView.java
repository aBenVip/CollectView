package com.artron.collectanimdemo;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.Nullable;

public class CollectView extends View {

    private Paint mPaint;
    private TextPaint mTextPaint;
    private Drawable mNormalDrawable;
    private Drawable mSelectDrawable;

    private int mTextBegin = 0;
    private float mScale = 1f;
    private int mRadius = 0;
    private String mSelectText = "收藏成功";
    private String mNormalText = "取消成功";
    private int mPadding = 0;

    private boolean isAnimatorFinish = true;
    private String mText;
    private int shadowRadius;
    private int textWidth;
    private int mTextSize;
    private int mColor = Color.BLACK;

    public CollectView(Context context) {
        this(context, null);
    }

    public CollectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CollectView);
        mNormalDrawable = a.getDrawable(R.styleable.CollectView_normal_drawable);
        mSelectDrawable = a.getDrawable(R.styleable.CollectView_select_drawable);
        mSelectText = a.hasValue(R.styleable.CollectView_select_text) ? a.getString(R.styleable.CollectView_select_text) : mSelectText;
        mNormalText = a.hasValue(R.styleable.CollectView_normal_text) ? a.getString(R.styleable.CollectView_normal_text) : mNormalText;
        mPadding = a.getDimensionPixelSize(R.styleable.CollectView_padding, mPadding);
        mTextSize = a.getDimensionPixelSize(R.styleable.CollectView_text_size, (int) DensityUtil.sp2px(getContext(), 12));
        mColor = a.getDimensionPixelSize(R.styleable.CollectView_text_color, mColor);
        shadowRadius = a.getDimensionPixelSize(R.styleable.CollectView_shadow_radius, DensityUtil.dp2px(getContext(), 20));
        a.recycle();
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(false);
        mTextPaint = new TextPaint();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(false);
        mTextPaint.setColor(mColor);
        mText = isSelected() ? mNormalText : mSelectText;

    }

    @Override
    public void setSelected(boolean selected) {
        if (!isAnimatorFinish) {
            return;
        }
        super.setSelected(selected);
        startAnim();
    }

    public void startAnim() {

        mText = isSelected() ? mSelectText : mNormalText;
        textWidth = (int) mTextPaint.measureText(mText);
        isAnimatorFinish = false;
        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, textWidth + mPadding * 2 + shadowRadius);
        valueAnimator.setInterpolator(new OvershootInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mTextBegin = (int) valueAnimator.getAnimatedValue();
                requestLayout();
                invalidate();
            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stopAnim();
                        invalidate();
                        requestLayout();
                    }
                }, 1500);

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        ValueAnimator scaleAnimator = ValueAnimator.ofFloat(0.1f, 1.1f, 0.9f, 1f);

        scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mScale = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        scaleAnimator.setInterpolator(new AccelerateInterpolator());
        animatorSet.playTogether(valueAnimator, scaleAnimator);
        animatorSet.setDuration(250);
        animatorSet.start();
    }


    public void stopAnim() {

        isAnimatorFinish = false;
        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(textWidth + mPadding * 2 + shadowRadius, 0);
        valueAnimator.setInterpolator(new OvershootInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mTextBegin = (int) valueAnimator.getAnimatedValue();
                invalidate();
                requestLayout();
            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isAnimatorFinish = true;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animatorSet.playTogether(valueAnimator);
        animatorSet.setDuration(250);
        animatorSet.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRadius = getHeight() / 2 - shadowRadius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float textWidth = mTextPaint.measureText(mText);
        mPaint.setColor(Color.parseColor("#EEEEEE"));
        canvas.drawRect(mRadius + shadowRadius, mRadius - (fontMetrics.bottom - fontMetrics.top) / 2 + shadowRadius - mPadding, mRadius * 2 + mTextBegin - (fontMetrics.bottom - fontMetrics.top) / 2, mRadius + (fontMetrics.bottom - fontMetrics.top) / 2 + shadowRadius + mPadding, mPaint);

        mPaint.setColor(Color.WHITE);
        mPaint.setShadowLayer(shadowRadius, 0, 0, Color.parseColor("#EEEEEE"));
        canvas.drawCircle(mRadius + shadowRadius, mRadius + shadowRadius, mRadius, mPaint);
        mPaint.clearShadowLayer();
        Drawable mDrawable = isSelected() ? mSelectDrawable : mNormalDrawable;
        if (mDrawable != null) {
            canvas.save();
            canvas.translate(mRadius + shadowRadius - mDrawable.getIntrinsicWidth() * mScale / 2f, mRadius + shadowRadius - mDrawable.getIntrinsicHeight() * mScale / 2f);
            mDrawable.setBounds(0, 0, (int) (mDrawable.getMinimumWidth() * mScale), (int) (mDrawable.getMinimumHeight() * mScale));
            mDrawable.draw(canvas);
            canvas.restore();
        }
        canvas.save();

        canvas.clipRect(mRadius * 2 + shadowRadius, shadowRadius, mRadius * 2 + mTextBegin, mRadius * 2 + shadowRadius * 2);
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        float baseline = mRadius + distance;

        mPaint.setColor(Color.parseColor("#EEEEEE"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(mRadius * 2 - textWidth + mTextBegin + shadowRadius, mRadius - (fontMetrics.bottom - fontMetrics.top) / 2 + shadowRadius - mPadding, mRadius * 2 + mTextBegin, mRadius + (fontMetrics.bottom - fontMetrics.top) / 2 + shadowRadius + mPadding, (fontMetrics.bottom - fontMetrics.top) / 2, (fontMetrics.bottom - fontMetrics.top) / 2, mPaint);
        } else {
            canvas.drawRect(mRadius * 2 - textWidth + mTextBegin + shadowRadius, mRadius - (fontMetrics.bottom - fontMetrics.top) / 2 + shadowRadius - mPadding, mRadius * 2 + mTextBegin, mRadius + (fontMetrics.bottom - fontMetrics.top) / 2 + shadowRadius + mPadding, mPaint);
        }
        canvas.drawText(mText, mRadius * 2 - textWidth - mPadding + mTextBegin, baseline + shadowRadius, mTextPaint);

        canvas.restore();


    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec) + mTextBegin;
        setMeasuredDimension(width, getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }


    public static int getDefaultSize(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        //从这里我们看出，对于AT_MOST和EXACTLY在View当中的处理是完全相同的。所以在我们自定义View时要对这两种模式做出处理。
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }
}
