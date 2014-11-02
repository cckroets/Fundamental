package com.ckroetsch.fundamental.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author curtiskroetsch
 */
public class TypefaceTextView extends TextView {

    public TypefaceTextView(Context context) {
        this(context, null);
    }

    public TypefaceTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TypefaceTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Action_Man.ttf");
            setTypeface(tf);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }
}
