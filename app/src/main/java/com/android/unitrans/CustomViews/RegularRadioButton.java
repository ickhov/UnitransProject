package com.android.unitrans.CustomViews;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;

public class RegularRadioButton extends AppCompatRadioButton {

    Typeface quicksand;

    public RegularRadioButton(Context context) {
        super(context);
        init();
    }

    public RegularRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RegularRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        quicksand = Typeface.createFromAsset(getContext().getAssets(), "Quicksand-Regular.ttf");
        setTypeface(quicksand);
    }
}
