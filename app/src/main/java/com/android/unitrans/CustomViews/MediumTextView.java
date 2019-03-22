package com.android.unitrans.CustomViews;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class MediumTextView extends AppCompatTextView {

    Typeface quicksand;

    public MediumTextView(Context context) {
        super(context);
        init();
    }

    public MediumTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MediumTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        quicksand = Typeface.createFromAsset(getContext().getAssets(), "Quicksand-Medium.ttf");
        setTypeface(quicksand);
    }
}
