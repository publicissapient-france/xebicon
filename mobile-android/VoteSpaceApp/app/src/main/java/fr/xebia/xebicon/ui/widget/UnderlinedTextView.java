package fr.xebia.xebicon.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class UnderlinedTextView extends TextView {

    private final Paint mPaint = new Paint();
    private int mUnderlineHeight;

    public UnderlinedTextView(Context context) {
        super(context);
    }

    public UnderlinedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UnderlinedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setUnderlineHeight(int underlineHeight){
        if(underlineHeight < 0){
            mUnderlineHeight = 0;
        }
        if(underlineHeight != mUnderlineHeight){
            mUnderlineHeight = underlineHeight;
            setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom() + mUnderlineHeight);
        }
    }

    public void setUnderlineColor(int underlineColor){
        if(mPaint.getColor() != underlineColor){
            mPaint.setColor(underlineColor);
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, getHeight() - mUnderlineHeight, getWidth(), getHeight(), mPaint);
    }
}
