package com.android.unitrans.CustomViews;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

public class MediumButton extends AppCompatButton {

    Typeface quicksand;

    public MediumButton(Context context) {
        super(context);
        init();
    }

    public MediumButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MediumButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        quicksand = Typeface.createFromAsset(getContext().getAssets(), "Quicksand-Medium.ttf");
        setTypeface(quicksand);
    }
}
