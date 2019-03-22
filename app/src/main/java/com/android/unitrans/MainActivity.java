package com.android.unitrans;

import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import com.android.unitrans.CustomViews.CustomImageButton;
import com.android.unitrans.CustomViews.MediumTextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BusFragment.OnFragmentInteractionListener,
    BusScheduleFragment.OnFragmentInteractionListener, View.OnClickListener {

    int width, height;
    ConstraintLayout main;
    RecyclerView circularBusLineView;
    BusLineCircularViewAdapter adapter;
    List<String> busLines;
    MediumTextView headingTitleLetter, headingTitle, headingDesc, onTimeLate;
    BusScheduleFragment busScheduleFragment;
    CustomImageButton back, forward;
    int position = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        width = getResources().getDisplayMetrics().widthPixels;
        height = getResources().getDisplayMetrics().heightPixels;

        main = findViewById(R.id.main_activity);

        initializeBusLines();
        setScrollableMenu();
        setOnTimeLate();
        setHeadingTitleLetter();
        setHeadingTitle();
        setHeadingDesc();
        setBack();
        setForward();
    }

    private void initializeBusLines() {
        busLines = new ArrayList<>();

        busLines.add("A");
        busLines.add("B");
        busLines.add("C");
        busLines.add("D");
        busLines.add("E");
        busLines.add("F");
        busLines.add("G");
        busLines.add("J");
        busLines.add("K");
        busLines.add("L");
        busLines.add("M");
        busLines.add("O");
        busLines.add("P");
        busLines.add("Q");
        busLines.add("S");
        busLines.add("T");
        busLines.add("V");
        busLines.add("W");
        busLines.add("X");
        busLines.add("Z");
    }

    private void setScrollableMenu() {
        circularBusLineView = findViewById(R.id.circular_btn_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager
                (getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        circularBusLineView.setLayoutManager(layoutManager);

        adapter = new BusLineCircularViewAdapter(getApplicationContext(), busLines);
        circularBusLineView.setAdapter(adapter);
    }

    private void setOnTimeLate() {
        onTimeLate = findViewById(R.id.heading_on_time_late);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) onTimeLate.getLayoutParams();
        onTimeLate.setPadding(30, 20, 30, 20);
        onTimeLate.setTextSize(width / 50);
    }

    private void setHeadingTitleLetter() {
        headingTitleLetter = findViewById(R.id.heading_title_letter);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) headingTitleLetter.getLayoutParams();
        layoutParams.width = width / 4;
        layoutParams.height = width / 4;
        headingTitleLetter.setTextSize(width / 20);
    }

    private void setHeadingTitle() {
        headingTitle = findViewById(R.id.heading_title);
        headingTitle.setText("C Line");
        headingTitle.setTextSize(width / 35);
    }

    private void setHeadingDesc() {
        headingDesc = findViewById(R.id.heading_desc);
        headingDesc.setText("Sycamore / Wake Forest");
        headingDesc.setTextSize(width / 70);
    }

    private void setBack() {
        back = findViewById(R.id.back_btn);
        back.setOnClickListener(this);

    }

    private void setForward() {
        forward = findViewById(R.id.forward_btn);
        forward.setOnClickListener(this);
    }

    @Override
    public void onTimeChanged(String message) {
        onTimeLate.setText(message);
        if (message.equals("Late"))
            onTimeLate.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_corner_red_background_2));
        else
            onTimeLate.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_corner_green_background));
    }

    @Override
    public void onViewAllClicked(String line) {
        if (busScheduleFragment == null)
            busScheduleFragment = BusScheduleFragment.newInstance(line);

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top, R.anim.enter_from_top, R.anim.exit_to_bottom)
                .add(R.id.main_activity, busScheduleFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forward_btn:
                if (position < adapter.getItemCount()) {
                    position++;
                    circularBusLineView.smoothScrollToPosition(position);
                }
                break;
            case R.id.back_btn:
                if (position > 0) {
                    position--;
                    circularBusLineView.smoothScrollToPosition(position);
                }
                break;
        }
    }
}
