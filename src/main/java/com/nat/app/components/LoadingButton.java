package com.nat.app.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;


import com.nat.app.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SuppressWarnings({"unused", "UnusedReturnValue", "RedundantSuppression"})
public class LoadingButton extends DrawableTextView {

    private int curStatus = STATUS.IDE;

    interface STATUS {
        int IDE = 0;
        int SHRINKING = 1;
        int LOADING = 2;
        int END_DRAWABLE_SHOWING = 3;
        int RESTORING = 4;
    }


    //Arr
    private boolean enableShrink;
    private boolean enableRestore;
    private boolean disableClickOnLoading;

    private Drawable[] mDrawablesSaved;
    private int mDrawablePaddingSaved;
    private CharSequence mTextSaved;
    private boolean mEnableTextInCenterSaved;
    private final int[] mRootViewSizeSaved = new int[]{0, 0};

    private ValueAnimator mShrinkAnimator;
    private int mShrinkDuration;
    private int mShrinkShape;

    @IntDef({ShrinkShape.DEFAULT, ShrinkShape.OVAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ShrinkShape {
        int DEFAULT = 0;
        int OVAL = 1;
    }


    private CircleIndicatorDrawable mLoadingDrawable;
    private OnStatusChangedListener mListener;
    private EndDrawable mEndDrawable;
    private int mLoadingSize;
    private int mLoadingPosition;

    private boolean isSizeChanging;
    private boolean nextReverse;
    private boolean isFail;


    public LoadingButton(Context context) {
        super(context);
        init(context, null);
    }

    public LoadingButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LoadingButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        //getConfig
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LoadingButton);

        disableClickOnLoading = array.getBoolean(R.styleable.LoadingButton_disableClickOnLoading, true);

        enableShrink = array.getBoolean(R.styleable.LoadingButton_enableShrink, true);
        enableRestore = array.getBoolean(R.styleable.LoadingButton_enableRestore, false);
        mShrinkDuration = array.getInt(R.styleable.LoadingButton_shrinkDuration, 450);
        mShrinkShape = array.getInt(R.styleable.LoadingButton_shrinkShape, ShrinkShape.DEFAULT);

        int loadingDrawableSize = array.getDimensionPixelSize(R.styleable.LoadingButton_loadingEndDrawableSize, (int) (enableShrink ? getTextSize() * 2 : getTextSize()));
        int loadingDrawableColor = array.getColor(R.styleable.LoadingButton_loadingDrawableColor, getTextColors().getDefaultColor());
        int loadingDrawablePosition = array.getInt(R.styleable.LoadingButton_loadingDrawablePosition, POSITION.START);
        int endSuccessDrawable = array.getResourceId(R.styleable.LoadingButton_endSuccessDrawable, -1);
        int endFailDrawableResId = array.getResourceId(R.styleable.LoadingButton_endFailDrawable, -1);
        int endDrawableAppearTime = array.getInt(R.styleable.LoadingButton_endDrawableAppearTime, EndDrawable.DEFAULT_APPEAR_DURATION);
        int endDrawableDuration = array.getInt(R.styleable.LoadingButton_endDrawableDuration, 900);

        array.recycle();

        //initLoadingDrawable
        mLoadingDrawable = new CircleIndicatorDrawable(context);
        mLoadingDrawable.setColorSchemeColors(loadingDrawableColor);
        mLoadingDrawable.setStrokeWidth(loadingDrawableSize * 0.14f);

        mLoadingSize = loadingDrawableSize;
        mLoadingPosition = loadingDrawablePosition;
        setDrawable(mLoadingPosition, mLoadingDrawable, loadingDrawableSize, loadingDrawableSize);
        setEnableCenterDrawables(true);

        //initLoadingDrawable
        if (endSuccessDrawable != -1 || endFailDrawableResId != -1) {
            mEndDrawable = new EndDrawable(endSuccessDrawable, endFailDrawableResId);
            mEndDrawable.mAppearAnimator.setDuration(endDrawableAppearTime);
            mEndDrawable.setKeepDuration(endDrawableDuration);
        }

        //initShrinkAnimator
        setUpShrinkAnimator();

        //initShrinkShape
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mShrinkShape > 0) {
                setClipToOutline(true);
                setOutlineProvider(new ShrinkShapeOutlineProvider());
            }

        }


        //Start|End -> true  Top|Bottom ->false
        setEnableTextInCenter(mLoadingPosition % 2 == 0);


        if (isInEditMode()) {
            mLoadingDrawable.setStartEndTrim(0, 0.8f);
        }

    }


    private void setUpShrinkAnimator() {
        mShrinkAnimator = ValueAnimator.ofFloat(0, 1f);
        mShrinkAnimator.setDuration(mShrinkDuration);
        mShrinkAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // y = kx + b
                // b = getRootViewSize()
                // k = getRootViewSize() - getLoadingSize
                getLayoutParams().width = (int) ((getShrinkSize() - mRootViewSizeSaved[0]) * (float) animation.getAnimatedValue() + mRootViewSizeSaved[0]);
                getLayoutParams().height = (int) ((getShrinkSize() - mRootViewSizeSaved[1]) * (float) animation.getAnimatedValue() + mRootViewSizeSaved[1]);
                requestLayout();
            }
        });


        mShrinkAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (!nextReverse) {
                    //begin shrink
                    curStatus = STATUS.SHRINKING;
                    isSizeChanging = true;
                    if (mListener != null) {
                        mListener.onShrinking();
                    }

                    LoadingButton.super.setText("", BufferType.NORMAL);
                    setCompoundDrawablePadding(0);
                    setCompoundDrawablesRelative(mLoadingDrawable, null, null, null);
                    setEnableTextInCenter(false);

                } else {
                    //begin restore
                    stopLoading();
                    curStatus = STATUS.RESTORING;
                    if (mListener != null) {
                        mListener.onRestoring();
                    }

                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!nextReverse) {
                    //shrink over
                    curStatus = STATUS.LOADING;
                    startLoading();
                    nextReverse = true;

                } else {
                    //restore over
                    isSizeChanging = false;
                    nextReverse = false;
                    toIde();
                    if (mListener != null) {
                        mListener.onRestored();
                    }
                }
            }

        });

    }

    private void beginShrink(boolean isReverse, boolean lastFrame) {
        if (mShrinkAnimator.isRunning()) {
            mShrinkAnimator.end();
        }
        this.nextReverse = isReverse;
        if (!isReverse) {
            mShrinkAnimator.start();

        } else {
            mShrinkAnimator.reverse();

        }
        if (lastFrame) {
            mShrinkAnimator.end();
        }

    }

    private void saveStatus() {
        mTextSaved = getText();
        mDrawablesSaved = copyDrawables(true);
        mDrawablePaddingSaved = getCompoundDrawablePadding();
        mEnableTextInCenterSaved = isEnableTextInCenter();
    }

    private void restoreStatus() {
        setText(mTextSaved);
        setCompoundDrawablePadding(mDrawablePaddingSaved);
        setCompoundDrawablesRelative(mDrawablesSaved[POSITION.START], mDrawablesSaved[POSITION.TOP], mDrawablesSaved[POSITION.END], mDrawablesSaved[POSITION.BOTTOM]);
        setEnableTextInCenter(mEnableTextInCenterSaved);
        getLayoutParams().width = mRootViewSizeSaved[0];
        getLayoutParams().height = mRootViewSizeSaved[1];
        requestLayout();

        addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                measureTextHeight();
                measureTextWidth();
                removeOnLayoutChangeListener(this);
            }
        });

    }

    private void toIde() {
        curStatus = STATUS.IDE;
        restoreStatus();
        isFail = false;

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //disable click
        if (event.getAction() == MotionEvent.ACTION_DOWN
                && disableClickOnLoading && curStatus != STATUS.IDE)
            return true;
        return super.onTouchEvent(event);
    }

    private void startLoading() {
        curStatus = STATUS.LOADING;

        if (!mLoadingDrawable.isRunning()) {
            mLoadingDrawable.start();
        }

        if (mListener != null) {
            mListener.onLoadingStart();
        }
    }

    private void stopLoading() {
        if (mLoadingDrawable.isRunning()) {
            mLoadingDrawable.stop();
            if (mListener != null) {
                mListener.onLoadingStop();
            }
        }

    }

    private void cancelAllRunning(boolean withRestoreAnim) {

        switch (curStatus) {
            case STATUS.SHRINKING:
                beginShrink(true, !withRestoreAnim);
                break;
            case STATUS.LOADING: {
                stopLoading();
                if (enableShrink) {
                    beginShrink(true, !withRestoreAnim);
                } else {
                    toIde();
                }
                break;
            }
            case STATUS.END_DRAWABLE_SHOWING:
                if (mEndDrawable != null) {
                    mEndDrawable.cancel(withRestoreAnim);
                } else {
                    beginShrink(true, !withRestoreAnim);
                }
                break;
            case STATUS.RESTORING:
                if (!withRestoreAnim)
                    mShrinkAnimator.end();
                else {
                    nextReverse = true;
                    mShrinkAnimator.reverse();
                }
                break;
        }

    }


    public void start() {
        //cancel last loading
        cancelAllRunning(false);

        saveStatus();
        if (enableShrink) {
            beginShrink(false, false);
        } else {
            if (TextUtils.isEmpty(getText())) {
                setCompoundDrawablePadding(0);
            }
            startLoading();
        }
    }

    public void complete(boolean isSuccess) {
        stopLoading();
        if (mEndDrawable != null) {
            if (mShrinkAnimator.isRunning())
                mShrinkAnimator.end();

            mEndDrawable.show(isSuccess);
        } else {
            //No EndDrawable,enableShrink
            this.isFail = !isSuccess;
            if (enableShrink) {
                if (enableRestore)
                    beginShrink(true, curStatus != STATUS.LOADING);

                else {
                    if (mListener != null) {
                        mListener.onCompleted(isSuccess);
                    }
                }
            } else {
                //No EndDrawable,disableShrink
                if (mListener != null) {
                    mListener.onCompleted(isSuccess);
                }

                if (enableRestore)
                    toIde();
            }


        }
    }


    public void cancel() {
        cancel(true);
    }

    public void cancel(boolean withRestoreAnim) {
        if (curStatus != STATUS.IDE) {
            cancelAllRunning(withRestoreAnim);

            if (mListener != null)
                mListener.onCanceled();
        }
    }

    public LoadingButton setDisableClickOnLoading(boolean disable) {
        this.disableClickOnLoading = disable;
        return this;
    }

    public LoadingButton setEnableShrink(boolean enable) {
        this.enableShrink = enable;
        return this;
    }


    public LoadingButton setEnableRestore(boolean enable) {
        this.enableRestore = enable;
        return this;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LoadingButton setShrinkShape(@ShrinkShape int shrinkShape) {
        this.mShrinkShape = shrinkShape;
        if (!(getOutlineProvider() instanceof ShrinkShapeOutlineProvider)) {
            setOutlineProvider(new ShrinkShapeOutlineProvider());
            setClipToOutline(true);
        } else
            invalidateOutline();

        return this;
    }

    public int getShrinkShape() {
        return mShrinkShape;
    }

    public int getShrinkSize() {
        return Math.max(Math.min(mRootViewSizeSaved[0], mRootViewSizeSaved[1]), getLoadingEndDrawableSize());
    }

    public ValueAnimator getShrinkAnimator() {
        return mShrinkAnimator;
    }

    public LoadingButton setShrinkDuration(long milliseconds) {
        mShrinkAnimator.setDuration(milliseconds);
        return this;
    }

    public int getShrinkDuration() {
        return mShrinkDuration;
    }

    public CircleIndicatorDrawable getLoadingDrawable() {
        return mLoadingDrawable;
    }


    public LoadingButton setLoadingPosition(@POSITION int position) {
        boolean enableTextInCenter = position % 2 == 0;
        setEnableTextInCenter(enableTextInCenter);
        mEnableTextInCenterSaved = enableTextInCenter;
        setDrawable(mLoadingPosition, null, 0, 0);
        mLoadingPosition = position;
        setDrawable(position, getLoadingDrawable(), getLoadingEndDrawableSize(), getLoadingEndDrawableSize());
        return this;
    }

    public LoadingButton setLoadingEndDrawableSize(@Px int px) {
        mLoadingSize = px;
        setDrawable(mLoadingPosition, mLoadingDrawable, px, px);
        return this;
    }


    public int getLoadingEndDrawableSize() {
        return mLoadingSize;
    }


    public LoadingButton setSuccessDrawable(@DrawableRes int drawableRes) {
        if (mEndDrawable == null)
            mEndDrawable = new EndDrawable(drawableRes, -1);
        else {
            mEndDrawable.setSuccessDrawable(drawableRes);
        }
        return this;
    }

    public LoadingButton setSuccessDrawable(Drawable drawable) {
        if (mEndDrawable == null)
            mEndDrawable = new EndDrawable(drawable, null);
        else {
            mEndDrawable.setSuccessDrawable(drawable);
        }
        return this;
    }

    public LoadingButton setFailDrawable(@DrawableRes int drawableRes) {
        if (mEndDrawable == null)
            mEndDrawable = new EndDrawable(-1, drawableRes);
        else {
            mEndDrawable.setFailDrawable(drawableRes);
        }
        return this;
    }

    public LoadingButton setFailDrawable(Drawable drawable) {
        if (mEndDrawable == null)
            mEndDrawable = new EndDrawable(null, drawable);
        else {
            mEndDrawable.setFailDrawable(drawable);
        }
        return this;
    }

    public LoadingButton setEndDrawableKeepDuration(long milliseconds) {
        if (mEndDrawable != null)
            mEndDrawable.setKeepDuration(milliseconds);
        return this;
    }

    public long getEndDrawableDuration() {
        if (mEndDrawable != null)
            return mEndDrawable.mKeepDuration;
        return EndDrawable.DEFAULT_APPEAR_DURATION;
    }


    public LoadingButton setEndDrawableAppearDuration(long milliseconds) {
        if (mEndDrawable != null)
            mEndDrawable.getAppearAnimator().setDuration(milliseconds);
        return this;
    }

    @Nullable
    public ObjectAnimator getEndDrawableAnimator() {
        if (mEndDrawable != null) {
            return mEndDrawable.getAppearAnimator();
        }
        return null;
    }


    @Override
    public void setCompoundDrawablePadding(int pad) {
        super.setCompoundDrawablePadding(pad);
        if (curStatus == STATUS.IDE)
            mDrawablePaddingSaved = pad;
    }


    @Override
    public void setText(CharSequence text, BufferType type) {
        if (TextUtils.isEmpty(text) && curStatus != STATUS.IDE) {
            setCompoundDrawablePadding(0);
        }

        if (enableShrink && isSizeChanging) {
            return;
        }
        super.setText(text, type);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (curStatus == STATUS.IDE) {
            mRootViewSizeSaved[0] = getWidth();
            mRootViewSizeSaved[1] = getHeight();
        }
    }

    @Override
    protected void onFirstLayout(int left, int top, int right, int bottom) {
        super.onFirstLayout(left, top, right, bottom);
        saveStatus();


    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mEndDrawable != null)
            mEndDrawable.draw(canvas);
        super.onDraw(canvas);
    }


    @SuppressWarnings("SameParameterValue")
    public class EndDrawable {
        private static final int DEFAULT_APPEAR_DURATION = 300;
        private Bitmap mSuccessBitmap;
        private Bitmap mFailBitmap;
        private Paint mPaint;
        private final Rect mBounds = new Rect();
        private Path mCirclePath;
        private ObjectAnimator mAppearAnimator;
        private long mKeepDuration;
        private float animValue;
        int[] offsetTemp = new int[]{0, 0};
        private boolean isShowing;
        private Runnable mRunnable;

        private EndDrawable(@Nullable Drawable successDrawable, @Nullable Drawable failDrawable) {
            setSuccessDrawable(successDrawable);
            setFailDrawable(failDrawable);
            init();
        }

        private EndDrawable(@DrawableRes int successResId, @DrawableRes int failResId) {
            setSuccessDrawable(successResId);
            setFailDrawable(failResId);
            init();
        }

        private void init() {
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            mCirclePath = new Path();
            mAppearAnimator = ObjectAnimator.ofFloat(this, "animValue", 1.0f);
            mRunnable = new Runnable() {
                @Override
                public void run() {

                    if (enableShrink) {
                        if (enableRestore) {
                            setAnimValue(0);
                            beginShrink(true, !nextReverse);
                        }
                    } else {
                        if (enableRestore) {
                            setAnimValue(0);
                            toIde();
                        }
                    }

                    isShowing = false;
                }
            };
            mAppearAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    curStatus = STATUS.END_DRAWABLE_SHOWING;
                    if (mListener != null) {
                        mListener.onEndDrawableAppear(!isFail, mEndDrawable);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (isShowing) {
                        postDelayed(mRunnable, mKeepDuration);
                    }
                    if (mListener != null) {
                        mListener.onCompleted(!isFail);
                    }
                }
            });
        }


        private void show(boolean isSuccess) {

            //end showing endDrawable
            if (isShowing) {
                cancel(false);
            }

            LoadingButton.this.isFail = !isSuccess;
            mAppearAnimator.start();
            isShowing = true;

        }

        private void cancel(boolean withAnim) {
            isShowing = false;

            getHandler().removeCallbacks(mRunnable);
            if (mAppearAnimator.isRunning()) {
                mAppearAnimator.end();
            }

            if (enableShrink)
                beginShrink(true, !(withAnim && nextReverse));
            else {
                toIde();
            }
            setAnimValue(0);
        }

        private void hide() {
            if (isShowing) {
                cancel(false);
            }
            mAppearAnimator.reverse();
            isShowing = true;
        }

        private int[] calcOffset(Canvas canvas, Rect bounds, @POSITION int pos) {
            final int[] offset = offsetTemp;
            offset[0] = canvas.getWidth() / 2;
            offset[1] = canvas.getHeight() / 2;

            switch (pos) {
                case POSITION.START: {
                    offset[0] -= (int) getTextWidth() / 2 + bounds.width() + getCompoundDrawablePadding();
                    if (enableShrink && nextReverse) {
                        offset[0] += bounds.width() / 2;
                    } else if (!isEnableTextInCenter()) {
                        offset[0] += (bounds.width() + getCompoundDrawablePadding()) / 2;
                    }

                    offset[1] -= bounds.height() / 2;
                    break;
                }
                case POSITION.TOP: {
                    offset[0] -= bounds.width() / 2;
                    offset[1] -= (int) getTextHeight() / 2 + bounds.height() + getCompoundDrawablePadding();
                    if (enableShrink && nextReverse) {
                        offset[1] += bounds.height() / 2;
                    } else if (!isEnableTextInCenter()) {
                        offset[1] += (bounds.height() + getCompoundDrawablePadding()) / 2;
                    }
                    break;
                }
                case POSITION.END: {
                    offset[0] += (int) getTextWidth() / 2 + getCompoundDrawablePadding();
                    if (enableShrink && nextReverse) {
                        offset[0] -= bounds.width() / 2;
                    } else if (!isEnableTextInCenter()) {
                        offset[0] -= (bounds.width() + getCompoundDrawablePadding()) / 2;
                    }
                    offset[1] -= bounds.height() / 2;
                    break;
                }
                case POSITION.BOTTOM: {
                    offset[0] -= bounds.width() / 2;
                    offset[1] += (int) getTextHeight() / 2 + getCompoundDrawablePadding();
                    if (enableShrink && nextReverse) {
                        offset[1] -= bounds.height() / 2;
                    } else if (!isEnableTextInCenter()) {
                        offset[1] -= (bounds.height() + getCompoundDrawablePadding()) / 2;
                    }
                    break;
                }
            }
            return offset;
        }

        private void draw(Canvas canvas) {
            if (getAnimValue() > 0 && mLoadingDrawable != null) {
                final Bitmap targetBitMap = isFail ? mFailBitmap : mSuccessBitmap;
                if (targetBitMap != null) {
                    final Rect bounds = mLoadingDrawable.getBounds();
                    mBounds.right = bounds.width();
                    mBounds.bottom = bounds.height();

                    final int[] offsets = calcOffset(canvas, mBounds, mLoadingPosition);
                    canvas.save();
                    canvas.translate(offsets[0], offsets[1]);
                    mCirclePath.reset();
                    mCirclePath.addCircle(mBounds.centerX(), mBounds.centerY(),
                            ((getLoadingEndDrawableSize() >> 1) * 1.5f) * animValue, Path.Direction.CW);
                    canvas.clipPath(mCirclePath);
                    canvas.drawBitmap(targetBitMap, null, mBounds, mPaint);
                    canvas.restore();
                }
            }
        }

        private void setAnimValue(float animValue) {
            this.animValue = animValue;
            invalidate();
        }

        public float getAnimValue() {
            return animValue;
        }

        public ObjectAnimator getAppearAnimator() {
            return mAppearAnimator;
        }

        public void setKeepDuration(long keepDuration) {
            this.mKeepDuration = keepDuration;
        }


        public void setSuccessDrawable(Drawable drawable) {
            mSuccessBitmap = getBitmap(drawable);
        }

        public void setSuccessDrawable(@DrawableRes int id) {
            if (id != -1) {
                Drawable drawable = ContextCompat.getDrawable(getContext(), id);
                mSuccessBitmap = getBitmap(drawable);
            }
        }

        public void setFailDrawable(@DrawableRes int id) {
            if (id != -1) {
                Drawable failDrawable = ContextCompat.getDrawable(getContext(), id);
                mFailBitmap = getBitmap(failDrawable);
            }
        }

        public void setFailDrawable(Drawable drawable) {
            mSuccessBitmap = getBitmap(drawable);
        }

    }

    @Nullable
    private Bitmap getBitmap(Drawable drawable) {
        if (drawable != null) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
        return null;
    }


    @Override
    protected void onDetachedFromWindow() {
        //release
        mShrinkAnimator.cancel();
        mLoadingDrawable.stop();
        if (mEndDrawable != null)
            mEndDrawable.mAppearAnimator.cancel();

        super.onDetachedFromWindow();

    }


    public class ShrinkShapeOutlineProvider extends RadiusViewOutlineProvider {
        @Override
        public void getOutline(View view, Outline outline) {
            if (enableShrink && mShrinkShape == ShrinkShape.OVAL
                    && (curStatus == STATUS.LOADING || curStatus == STATUS.END_DRAWABLE_SHOWING)) {
                outline.setOval(0, 0, getShrinkSize(), getShrinkSize());
            } else {
                super.getOutline(view, outline);
            }

        }
    }


    public static class OnStatusChangedListener {

        public void onShrinking() {

        }

        public void onLoadingStart() {

        }

        public void onLoadingStop() {

        }

        public void onEndDrawableAppear(boolean isSuccess, EndDrawable endDrawable) {

        }

        public void onRestoring() {

        }

        public void onRestored() {

        }

        public void onCompleted(boolean isSuccess) {

        }

        public void onCanceled() {

        }
    }

    public LoadingButton setOnStatusChangedListener(OnStatusChangedListener listener) {
        mListener = listener;
        return this;
    }
}