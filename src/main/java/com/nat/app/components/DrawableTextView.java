package com.nat.app.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.Dimension;
import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.appcompat.widget.AppCompatTextView;

import com.nat.app.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

public class DrawableTextView extends AppCompatTextView {

    @IntDef({POSITION.START, POSITION.TOP, POSITION.END, POSITION.BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface POSITION {
        int START = 0;
        int TOP = 1;
        int END = 2;
        int BOTTOM = 3;
    }

    private Drawable[] mDrawables = new Drawable[]{null, null, null, null};
    private Rect[] mDrawablesBounds = new Rect[4];
    private float mTextWidth;
    private float mTextHeight;
    private boolean firstLayout;
    private int canvasTransX = 0, canvasTransY = 0;

    private boolean isCenterHorizontal;
    private boolean isCenterVertical;
    private boolean enableCenterDrawables;
    private boolean enableTextInCenter;
    private int radius;


    public DrawableTextView(Context context) {
        super(context);
        init(context, null);
    }

    public DrawableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DrawableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        Drawable[] drawables = getCompoundDrawablesRelative();
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DrawableTextView);
        enableCenterDrawables = array.getBoolean(R.styleable.DrawableTextView_enableCenterDrawables, false);
        enableTextInCenter = array.getBoolean(R.styleable.DrawableTextView_enableTextInCenter, false);

        radius = array.getDimensionPixelSize(R.styleable.DrawableTextView_radius, 0);

        if (radius > 0) {
            setClipToOutline(true);
            setOutlineProvider(new RadiusViewOutlineProvider());
        }

        if (drawables[POSITION.START] != null) {
            Rect startBounds = drawables[POSITION.START].getBounds();
            startBounds.right = array.getDimensionPixelSize(R.styleable.DrawableTextView_drawableStartWidth, drawables[POSITION.START].getIntrinsicWidth());
            startBounds.bottom = array.getDimensionPixelSize(R.styleable.DrawableTextView_drawableStartHeight, drawables[POSITION.START].getIntrinsicHeight());
        }

        if (drawables[POSITION.TOP] != null) {
            Rect topBounds = drawables[POSITION.TOP].getBounds();
            topBounds.right = array.getDimensionPixelSize(R.styleable.DrawableTextView_drawableTopWidth, drawables[POSITION.TOP].getIntrinsicWidth());
            topBounds.bottom = array.getDimensionPixelSize(R.styleable.DrawableTextView_drawableTopHeight, drawables[POSITION.TOP].getIntrinsicHeight());
        }

        if (drawables[POSITION.END] != null) {
            Rect endBounds = drawables[POSITION.END].getBounds();
            endBounds.right = array.getDimensionPixelSize(R.styleable.DrawableTextView_drawableEndWidth, drawables[POSITION.END].getIntrinsicWidth());
            endBounds.bottom = array.getDimensionPixelSize(R.styleable.DrawableTextView_drawableEndHeight, drawables[POSITION.END].getIntrinsicHeight());
        }

        if (drawables[POSITION.BOTTOM] != null) {
            Rect bottomBounds = drawables[POSITION.BOTTOM].getBounds();
            bottomBounds.right = array.getDimensionPixelSize(R.styleable.DrawableTextView_drawableBottomWidth, drawables[POSITION.BOTTOM].getIntrinsicWidth());
            bottomBounds.bottom = array.getDimensionPixelSize(R.styleable.DrawableTextView_drawableBottomHeight, drawables[POSITION.BOTTOM].getIntrinsicHeight());
        }
        array.recycle();
        setCompoundDrawables(drawables[POSITION.START], drawables[POSITION.TOP], drawables[POSITION.END], drawables[POSITION.BOTTOM]);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (enableCenterDrawables) {
            final int absoluteGravity = Gravity.getAbsoluteGravity(getGravity(), getLayoutDirection());
            isCenterHorizontal = (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.CENTER_HORIZONTAL;
            isCenterVertical = (absoluteGravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.CENTER_VERTICAL;
        }

        if (!firstLayout) {
            onFirstLayout(left, top, right, bottom);
            firstLayout = true;
        }
    }


    protected void onFirstLayout(int left, int top, int right, int bottom) {
        measureTextWidth();
        measureTextHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (enableCenterDrawables && (isCenterHorizontal | isCenterVertical)) {

            //有文字就才位移画布
            boolean textNoEmpty = !TextUtils.isEmpty(getText());
            //画布的偏移量
            int transX = 0, transY = 0;


            if (mDrawables[POSITION.START] != null && isCenterHorizontal) {
                Rect bounds = mDrawablesBounds[POSITION.START];
                int offset = (int) calcOffset(POSITION.START);
                mDrawables[POSITION.START].setBounds(bounds.left + offset, bounds.top,
                        bounds.right + offset, bounds.bottom);

                if (enableTextInCenter && textNoEmpty)
                    transX -= (mDrawablesBounds[POSITION.START].width() + getCompoundDrawablePadding()) >> 1;


            }

            if (mDrawables[POSITION.TOP] != null && isCenterVertical) {
                Rect bounds = mDrawablesBounds[POSITION.TOP];
                int offset = (int) calcOffset(POSITION.TOP);
                mDrawables[POSITION.TOP].setBounds(bounds.left, bounds.top + offset,
                        bounds.right, bounds.bottom + offset);

                if (enableTextInCenter && textNoEmpty)
                    transY -= (mDrawablesBounds[POSITION.TOP].height() + getCompoundDrawablePadding()) >> 1;


            }

            if (mDrawables[POSITION.END] != null && isCenterHorizontal) {
                Rect bounds = mDrawablesBounds[POSITION.END];
                int offset = -(int) calcOffset(POSITION.END);
                mDrawables[POSITION.END].setBounds(bounds.left + offset, bounds.top,
                        bounds.right + offset, bounds.bottom);

                if (enableTextInCenter && textNoEmpty)
                    transX += (mDrawablesBounds[POSITION.END].width() + getCompoundDrawablePadding()) >> 1;
            }

            if (mDrawables[POSITION.BOTTOM] != null && isCenterVertical) {
                Rect bounds = mDrawablesBounds[POSITION.BOTTOM];
                int offset = -(int) calcOffset(POSITION.BOTTOM);
                mDrawables[POSITION.BOTTOM].setBounds(bounds.left, bounds.top + offset,
                        bounds.right, bounds.bottom + offset);

                if (enableTextInCenter && textNoEmpty)
                    transY += (mDrawablesBounds[POSITION.BOTTOM].height() + getCompoundDrawablePadding()) >> 1;
            }


            if (enableTextInCenter && textNoEmpty) {
                canvas.translate(transX, transY);
                this.canvasTransX = transX;
                this.canvasTransY = transY;
            }
        }
        super.onDraw(canvas);
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        canvas.translate(-canvasTransX, -canvasTransY);
        super.onDrawForeground(canvas);
    }

    private float calcOffset(@POSITION int position) {

        switch (position) {
            case POSITION.START:
            case POSITION.END:
                return (getWidth() - (getCompoundPaddingStart() + getCompoundPaddingEnd() + getTextWidth())) / 2;

            case POSITION.TOP:
            case POSITION.BOTTOM:
                return (getHeight() - (getCompoundPaddingTop() + getCompoundPaddingBottom() + getTextHeight())) / 2;

            default:
                return 0;

        }
    }

    protected int getCanvasTransX() {
        return canvasTransX;
    }

    protected int getCanvasTransY() {
        return canvasTransY;
    }

    protected void measureTextWidth() {
        final Rect textBounds = new Rect();
        getLineBounds(0, textBounds);
        String text = "";
        if (getText() != null && getText().length() > 0) {
            text = getText().toString();
        } else if (getHint() != null && getHint().length() > 0) {
            text = getHint().toString();
        }
        final float width = getPaint().measureText(text);
        final float maxWidth = textBounds.width();
        mTextWidth = width <= maxWidth || maxWidth == 0 ? width : maxWidth;
    }

    protected void measureTextHeight() {
        if ((getText() != null && getText().length() > 0)
                || (getHint() != null && getHint().length() > 0))
            mTextHeight = getLineHeight() * getLineCount();
        else
            mTextHeight = 0;
    }

    protected float getTextWidth() {
        return mTextWidth;
    }

    public float getTextHeight() {
        return mTextHeight;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        measureTextWidth();
        measureTextHeight();
    }

    public void setDrawable(@POSITION int position, @Nullable Drawable drawable, @Px int width, @Px int height) {
        mDrawables[position] = drawable;
        if (drawable != null) {
            Rect bounds = new Rect();
            if (width == -1 && height == -1) {
                if (drawable.getBounds().width() > 0 && drawable.getBounds().height() > 0) {
                    //如果Bounds宽高大于0,则保持默认
                    final Rect origin = drawable.getBounds();
                    bounds.set(origin.left, origin.top, origin.right, origin.bottom);
                } else {
                    //否则取Drawable的内部大小
                    bounds.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                }
            } else {
                bounds.right = width;
                bounds.bottom = height;
            }
            mDrawables[position].setBounds(bounds.left, bounds.top, bounds.right, bounds.bottom);
            mDrawablesBounds[position] = bounds;
        }
        super.setCompoundDrawables(mDrawables[POSITION.START], mDrawables[POSITION.TOP], mDrawables[POSITION.END], mDrawables[POSITION.BOTTOM]);
    }


    @Override
    public void setCompoundDrawables(@Nullable Drawable left, @Nullable Drawable top, @Nullable Drawable right, @Nullable Drawable bottom) {
        super.setCompoundDrawables(left, top, right, bottom);
        storeDrawables(left, top, right, bottom);
    }

    @Override
    public void setCompoundDrawablesRelative(@Nullable Drawable start, @Nullable Drawable top, @Nullable Drawable end, @Nullable Drawable bottom) {
        super.setCompoundDrawablesRelative(start, top, end, bottom);
        storeDrawables(start, top, end, bottom);
    }


    private void storeDrawables(@Nullable Drawable start, @Nullable Drawable top, @Nullable Drawable end, @Nullable Drawable bottom) {
        if (mDrawables != null) {
            if (start != null && start != mDrawables[POSITION.START]) {
                mDrawablesBounds[POSITION.START] = start.copyBounds();
            }
            mDrawables[POSITION.START] = start;


            if (top != null && top != mDrawables[POSITION.TOP]) {
                mDrawablesBounds[POSITION.TOP] = top.copyBounds();
            }
            mDrawables[POSITION.TOP] = top;
            if (end != null && end != mDrawables[POSITION.END]) {
                mDrawablesBounds[POSITION.END] = end.copyBounds();
            }
            mDrawables[POSITION.END] = end;

            if (bottom != null && bottom != mDrawables[POSITION.BOTTOM]) {
                mDrawablesBounds[POSITION.BOTTOM] = bottom.copyBounds();
            }
            mDrawables[POSITION.BOTTOM] = bottom;
        }

    }


    protected Drawable[] copyDrawables(boolean clearOffset) {
        Drawable[] drawables = Arrays.copyOf(getDrawables(), 4);
        //clear offset
        if (clearOffset)
            clearOffset(drawables);

        return drawables;
    }

    private void clearOffset(Drawable... drawables) {
        for (Drawable drawable : drawables) {
            if (drawable != null) {
                Rect bounds = drawable.getBounds();
                bounds.offset(-bounds.left, -bounds.top);
            }
        }
    }

    protected int dp2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public DrawableTextView setDrawableStart(Drawable drawableStart,
                                             @Dimension(unit = Dimension.DP) int width,
                                             @Dimension(unit = Dimension.DP) int height) {
        setDrawable(POSITION.START, drawableStart, dp2px(width), dp2px(height));
        return this;
    }

    public DrawableTextView setDrawableStart(Drawable drawableStart) {
        setDrawableStart(drawableStart, -1, -1);
        return this;
    }

    public DrawableTextView setDrawableTop(Drawable drawableTop,
                                           @Dimension(unit = Dimension.DP) int width,
                                           @Dimension(unit = Dimension.DP) int height) {
        setDrawable(POSITION.TOP, drawableTop, dp2px(width), dp2px(height));
        return this;
    }

    public DrawableTextView setDrawableTop(Drawable drawableTop) {
        setDrawableTop(drawableTop, -1, -1);
        return this;
    }

    public DrawableTextView setDrawableEnd(Drawable drawableEnd,
                                           @Dimension(unit = Dimension.DP) int width,
                                           @Dimension(unit = Dimension.DP) int height) {
        setDrawable(POSITION.END, drawableEnd, dp2px(width), dp2px(height));
        return this;
    }

    public DrawableTextView setDrawableEnd(Drawable drawableEnd) {
        setDrawableEnd(drawableEnd, -1, -1);
        return this;
    }


    public DrawableTextView setDrawableBottom(Drawable drawableBottom,
                                              @Dimension(unit = Dimension.DP) int width,
                                              @Dimension(unit = Dimension.DP) int height) {
        setDrawable(POSITION.BOTTOM, drawableBottom, dp2px(width), dp2px(height));
        return this;
    }

    public DrawableTextView setDrawableBottom(Drawable drawableBottom) {
        setDrawableBottom(drawableBottom, -1, -1);
        return this;
    }

    public DrawableTextView setEnableCenterDrawables(boolean enable) {
        if (enableCenterDrawables) {
            //清除之前的位移
            clearOffset(mDrawables);
        }
        this.enableCenterDrawables = enable;
        return this;
    }

    public DrawableTextView setEnableTextInCenter(boolean enable) {
        this.enableTextInCenter = enable;
        return this;
    }

    public boolean isEnableTextInCenter() {
        return enableTextInCenter;
    }

    public boolean isEnableCenterDrawables() {
        return enableCenterDrawables;
    }

    public Drawable[] getDrawables() {
        return mDrawables;
    }


    public DrawableTextView setRadius(@Px int px) {
        this.radius = px;
        if (!(getOutlineProvider() instanceof RadiusViewOutlineProvider)) {
            setOutlineProvider(new RadiusViewOutlineProvider());
            setClipToOutline(true);
        } else
            invalidateOutline();
        return this;
    }

    public DrawableTextView setRadiusDP(@Dimension(unit = Dimension.DP) int dp) {
        return setRadius(dp2px(dp));
    }

    public int getRadius() {
        return radius;
    }


    public class RadiusViewOutlineProvider extends ViewOutlineProvider {
        @Override
        public void getOutline(View view, Outline outline) {
            outline.setRoundRect(0, 0, getWidth(), getHeight(), radius);
        }
    }
}