package com.android.unitrans.CustomViews;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

public class MediumEditText extends AppCompatEditText {

    Typeface quicksand;

    public MediumEditText(Context context) {
        super(context);
        init();
    }

    public MediumEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MediumEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        quicksand = Typeface.createFromAsset(getContext().getAssets(), "Quicksand-Medium.ttf");
        setTypeface(quicksand);
    }
}
