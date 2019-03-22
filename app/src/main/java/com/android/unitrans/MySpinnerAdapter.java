package com.android.unitrans;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MySpinnerAdapter extends ArrayAdapter<String> {
    // Initialise custom font, for example:
    Typeface quicksand;

    public MySpinnerAdapter(Context context, int resource, List<String> items) {
        super(context, resource, items);
        quicksand = Typeface.createFromAsset(context.getAssets(), "Quicksand-Regular.ttf");
    }

    // Affects default (closed) state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setTypeface(quicksand);
        view.setGravity(Gravity.CENTER);
        return view;
    }

    // Affects opened state of the spinner
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getDropDownView(position, convertView, parent);
        view.setTypeface(quicksand);
        view.setGravity(Gravity.CENTER);
        return view;
    }
}


