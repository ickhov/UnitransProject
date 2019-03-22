package com.android.unitrans;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.android.unitrans.CustomViews.MediumTextView;
import com.android.unitrans.CustomViews.RegularTextView;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;

public class BusScheduleFragment extends Fragment {

    public static final String KEY = "BusLine";
    private OnFragmentInteractionListener mListener;
    private int width, height;
    private TableLayout stop, table;
    private MediumTextView tableTitle;
    private String line;

    public BusScheduleFragment() {
        // Required empty public constructor
    }

    public static BusScheduleFragment newInstance(String line) {
        BusScheduleFragment fragment = new BusScheduleFragment();
        Bundle args = new Bundle();
        args.putString(KEY, line);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            line = getArguments().getString(KEY);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bus_schedule, container, false);

        width = view.getContext().getResources().getDisplayMetrics().widthPixels;
        height = view.getContext().getResources().getDisplayMetrics().heightPixels;

        setTableTitle(view);
        setTable(view);

        return view;
    }

    private void setTableTitle(View view) {
        tableTitle = view.findViewById(R.id.bus_schedule_title);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) tableTitle.getLayoutParams();
        layoutParams.height = (int)(height * 0.1);
        tableTitle.setTextSize(width / 40);
        tableTitle.setText("C Line Route Schedule");
    }

    private void setTable(View view) {
        stop = view.findViewById(R.id.bus_schedule_stop);
        table = view.findViewById(R.id.bus_schedule_table);
        table.setStretchAllColumns(true);

        InputStreamReader is;

        try {
            is = new InputStreamReader(view.getContext().getAssets().open(line.toLowerCase() + "_line.csv"));

            CSVReader csvReader = new CSVReader(is);
            String[] line;

            // throw away the header
            csvReader.readNext();

            while ((line = csvReader.readNext()) != null) {
                TableRow row = new TableRow(view.getContext());

                MediumTextView stopName = new MediumTextView(view.getContext());
                stopName.setText(line[0]);
                stopName.setTextSize(width / 80);
                stopName.setTextColor(Color.BLACK);
                stopName.setGravity(Gravity.START);
                stopName.setPadding(3, 3,3,3);
                row.addView(stopName, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, (int)(height * 0.05)));

                stop.addView(row);

                TableRow row2 = new TableRow(view.getContext());
                boolean isMorning = true;

                for (int i = 1; i < line.length; i++) {

                    String timeString = line[i];

                    if (timeString.contains("12")) {
                        isMorning = false;
                    }

                    if (isMorning) {
                        timeString = timeString + "\nAM";
                    } else {
                        timeString = timeString + "\nPM";
                    }

                    RegularTextView time = new RegularTextView(view.getContext());
                    time.setText(timeString);
                    time.setTextSize(width / 80);
                    time.setTextColor(Color.BLACK);
                    time.setGravity(Gravity.CENTER);
                    time.setPadding(3, 3, 3, 3);
                    row2.addView(time, new TableRow.LayoutParams((int)(width * 0.13), (int)(height * 0.05)));
                }

                table.addView(row2);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

    }
}
