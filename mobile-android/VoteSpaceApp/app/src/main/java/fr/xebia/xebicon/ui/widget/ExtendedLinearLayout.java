package fr.xebia.xebicon.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import fr.xebia.xebicon.R;

public class ExtendedLinearLayout extends LinearLayout {

        private Drawable mForegroundSelector;
        private Rect mRectPadding;
        private boolean mUseBackgroundPadding = false;

        public ExtendedLinearLayout(Context context) {
            super(context);
        }

        public ExtendedLinearLayout(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public ExtendedLinearLayout(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);

            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExtendedLinearLayout, defStyle, 0);

            final Drawable d = a.getDrawable(R.styleable.ExtendedLinearLayout_foreground);
            if (d != null) {
                setForeground(d);
            }

            a.recycle();

            if (this.getBackground() instanceof NinePatchDrawable) {
                final NinePatchDrawable npd = (NinePatchDrawable) this.getBackground();
                if (npd != null) {
                    mRectPadding = new Rect();
                    if (npd.getPadding(mRectPadding)) {
                        mUseBackgroundPadding = true;
                    }
                }
            }
        }

        @Override
        protected void drawableStateChanged() {
            super.drawableStateChanged();

            if (mForegroundSelector != null && mForegroundSelector.isStateful()) {
                mForegroundSelector.setState(getDrawableState());
            }
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            if (mForegroundSelector != null) {
                if (mUseBackgroundPadding) {
                    mForegroundSelector.setBounds(mRectPadding.left, mRectPadding.top, w - mRectPadding.right, h - mRectPadding.bottom);
                } else {
                    mForegroundSelector.setBounds(0, 0, w, h);
                }
            }
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);

            if (mForegroundSelector != null) {
                mForegroundSelector.draw(canvas);
            }
        }

        @Override
        protected boolean verifyDrawable(Drawable who) {
            return super.verifyDrawable(who) || (who == mForegroundSelector);
        }

        @Override
        public void jumpDrawablesToCurrentState() {
            super.jumpDrawablesToCurrentState();
            if (mForegroundSelector != null) mForegroundSelector.jumpToCurrentState();
        }

        public void setForeground(Drawable drawable) {
            if (mForegroundSelector != drawable) {
                if (mForegroundSelector != null) {
                    mForegroundSelector.setCallback(null);
                    unscheduleDrawable(mForegroundSelector);
                }

                mForegroundSelector = drawable;

                if (drawable != null) {
                    setWillNotDraw(false);
                    drawable.setCallback(this);
                    if (drawable.isStateful()) {
                        drawable.setState(getDrawableState());
                    }
                }  else {
                    setWillNotDraw(true);
                }
                requestLayout();
                invalidate();
            }
        }
}
