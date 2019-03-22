package com.android.unitrans;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.android.unitrans.CustomViews.MediumButton;
import com.android.unitrans.CustomViews.MediumTextView;
import com.android.unitrans.CustomViews.RegularButton;
import com.android.unitrans.CustomViews.RegularTextView;
import com.android.unitrans.MapPath.MapPath;
import com.android.unitrans.MapPath.MapPathViewModel;
import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.tooltip.Tooltip;
import com.db.chart.view.BarChartView;
import com.db.chart.view.HorizontalBarChartView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import static com.android.unitrans.MapCoordinates.*;

public class BusFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    public static final String MY_PREFS_NAME = "Stop";
    private OnFragmentInteractionListener mListener;
    private int width, height, busMarkerCounter, doubleDeckerMarkerCounter;
    private ScrollView scrollView;
    private MediumTextView  announcementTitle, predictedTimeTitle, predictedTimeTextMin;
    private RegularTextView announcementText1, announcementText2, predictedTimeText;
    private Spinner predictedTimeSpinner;
    private MapView mapView;
    private GoogleMap mGoogleMap;
    private boolean isScrollEnabled;
    private SimpleTwoFingerDoubleTapDetector multiTouchListener;
    private Marker lastOpenned;
    private GeoApiContext geoApiContext;
    private MapPathViewModel mapPathViewModel;
    private TableLayout table, busLegend;
    private MediumTextView tableTitle;
    private RegularButton tableBtn;
    private List<LatLng> path;
    private Marker busMarker, doubleDeckerMarker;
    private Handler handler, doubleDeckerHandler;
    private LatLng silo, laRueOrchardNB, andersonRussel, andersonSunset,
            eightSycamore, wakeForest8thStreet, wakeForestOxford, sycamoreWakeForest,
            laRueOrchardSB, hutchisonBioletti;
    private String startLocation, targetLocation;
    private List<LatLng> markerPoints;
    private Handler timeHandler;
    private double lateTime;
    private MediumTextView barChartTitle;
    private RegularTextView barChartDay;
    private HorizontalBarChartView barChart;

    public BusFragment() {
        // Required empty public constructor
    }

    public static BusFragment newInstance() {
        BusFragment fragment = new BusFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bus, container, false);

        mapPathViewModel = ViewModelProviders.of(getActivity()).get(MapPathViewModel.class);
        path = new ArrayList();

        width = view.getContext().getResources().getDisplayMetrics().widthPixels;
        height = view.getContext().getResources().getDisplayMetrics().heightPixels;

        isScrollEnabled = true;
        lastOpenned = null;

        scrollView = view.findViewById(R.id.fragment_scrollview);

        multiTouchListener = new SimpleTwoFingerDoubleTapDetector() {
            @Override
            public void onTwoFingerDoubleTap() {
                // Do what you want here, I used a Toast for demonstration
                Toast.makeText(getContext(), "Two Finger Double Tap", Toast.LENGTH_LONG).show();
                //isScrollEnabled = false;
            }
        };

        setAnnouncementTitle(view);
        setAnnouncementText1(view);
        setAnnouncementText2(view);
        setPredictedTimeTitle(view);
        setPredictedTimeSpinner(view);
        setPredictedTimeText(view);
        setPredictedTimeTextMin(view);
        setMapView(view, savedInstanceState);
        setBusLegend(view);
        setTableTitle(view);
        setTable(view);
        setTableBtn(view);
        setBarChartTitle(view);
        setBarChartDay(view);
        setBarChart(view);

        return view;
    }

    private void setBarChartTitle(View view) {
        barChartTitle = view.findViewById(R.id.barChartTitle);
        barChartTitle.setTextSize(width / 50);
    }

    private void setBarChartDay(View view) {
        barChartDay = view.findViewById(R.id.barChartDay);

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) barChartDay.getLayoutParams();
        layoutParams.width = width / 2;

        barChartDay.setPadding(0, (int)(height * 0.01), 0, (int)(height * 0.01));

        barChartDay.setTextSize(width / 50);
    }

    private void setBarChart(View view) {
        barChart = view.findViewById(R.id.barChart);
        barChart.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Quicksand-Regular.ttf"));

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) barChart.getLayoutParams();
        layoutParams.height = (int)(height * 0.3);

        int counter = 0;

        InputStreamReader is;

        try {
            is = new InputStreamReader(view.getContext().getAssets().open("bus_time.csv"));

            CSVReader csvReader = new CSVReader(is);
            // throw away the header
            csvReader.readNext();

            List<String[]> items = csvReader.readAll();

            int n = 10;

            boolean isMorning = true;

            BarSet dataset = new BarSet();

            int color1 = ContextCompat.getColor(getContext(), R.color.C);
            int color2 = ContextCompat.getColor(getContext(), R.color.A);

            for (int i = 0; i < items.size() && counter < n; i++) {
                String timeString = items.get(i)[0];

                if (timeString.contains("12")) {
                    isMorning = false;
                }

                if (isMorning) {
                    timeString = timeString + "\nAM";
                } else {
                    timeString = timeString + "\nPM";
                }

                if (afterCurrentTime(timeString)) {
                    Bar bar = new Bar(timeString, (Integer.parseInt(items.get(i)[2]) + Integer.parseInt(items.get(i)[3])) * 5);

                    if (counter == 1) {
                        bar.setColor(color2);
                    } else {
                        bar.setColor(color1);
                    }

                    counter++;
                    dataset.addBar(bar);
                }
            }

            barChart.setRoundCorners(20);
            barChart.setLabelsFormat(new DecimalFormat("0"));
            barChart.addData(dataset);
            barChart.show();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void setAnnouncementTitle(View view) {
        announcementTitle = view.findViewById(R.id.announcement_title);
        announcementTitle.setTextSize(width / 50);
    }

    private void setAnnouncementText1(View view) {
        announcementText1 = view.findViewById(R.id.announcement_text1);
        announcementText1.setText("1. This bus will no longer be making stops.");
        announcementText1.setTextSize(width / 70);
    }

    private void setAnnouncementText2(View view) {
        announcementText2 = view.findViewById(R.id.announcement_text2);
        announcementText2.setText("2. We will be sending out an extra bus soon.");
        announcementText2.setTextSize(width / 70);
    }

    private void setPredictedTimeTitle(View view) {
        predictedTimeTitle = view.findViewById(R.id.predicted_time_title);
        predictedTimeTitle.setTextSize(width / 50);
    }

    private void setPredictedTimeSpinner(View view) {
        predictedTimeSpinner = view.findViewById(R.id.predicted_time_spinner);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) predictedTimeSpinner.getLayoutParams();
        layoutParams.width = (int)(width / 1.3);

        List<String> busStops = new ArrayList<>();
        busStops.add("Silo & Center Island NB");
        busStops.add("Hutchison & Bioletti NB");
        busStops.add("La Rue & Orchard NB");
        busStops.add("Anderson & Russell");
        busStops.add("Anderson & Sunset");
        busStops.add("8th & Sycamore");
        busStops.add("Wake Forest & 8th Street");
        busStops.add("Wake Forest & Oxford");
        busStops.add("Sycamore & Wake Forest");
        busStops.add("La Rue & Orchard SB");
        busStops.add("Hutchison & Bioletti SB");
        busStops.add("Silo & Center Island SB");

        MySpinnerAdapter adapter = new MySpinnerAdapter(getContext(),
                R.layout.support_simple_spinner_dropdown_item, busStops);

        predictedTimeSpinner.setAdapter(adapter);
        predictedTimeSpinner.setDropDownVerticalOffset(height / 15);

        SharedPreferences prefs = getContext().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        String stopName = prefs.getString("stop", null);

        if (stopName != null) {
            int spinnerPosition = adapter.getPosition(stopName);
            predictedTimeSpinner.setSelection(spinnerPosition);
            targetLocation = stopName;
        }

        timeHandler = new Handler();
        startLocation = "Silo & Center Island NB";

        predictedTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timeHandler.removeCallbacks(timeSimulation);
                targetLocation = parent.getItemAtPosition(position).toString();
                timeHandler.post(timeSimulation);
                SharedPreferences.Editor editor = getContext().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
                editor.putString("stop", targetLocation);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    Runnable timeSimulation = new Runnable() {
        @Override
        public void run() {
            int[] time = getRandomMinutes(startLocation, targetLocation);
            predictedTimeTextMin.setText(time[0] + ", " + time[1] + ", " + time[2] + " minutes");

            if (lateTime <= 0) {
                mListener.onTimeChanged("On-Time");
            } else {
                mListener.onTimeChanged("Late");
            }

            timeHandler.postDelayed(this, 1000);
        }
    };

    private void setPredictedTimeText(View view) {
        predictedTimeText = view.findViewById(R.id.predicted_time_text);
        predictedTimeText.setTextSize(width / 70);
    }

    private void setPredictedTimeTextMin(View view) {
        predictedTimeTextMin = view.findViewById(R.id.predicted_time_text_min);
        predictedTimeTextMin.setTextSize(width / 60);
        targetLocation = "Silo & Center Island SB";
        timeHandler.post(timeSimulation);
    }

    private void setMapView(View view, Bundle savedInstanceState) {
        mapView = view.findViewById(R.id.mapView);
        CardView.LayoutParams layoutParams = (CardView.LayoutParams) mapView.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height / 2;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    private void setBusLegend(View view) {
        busLegend = view.findViewById(R.id.bus_legend);
        busLegend.setShrinkAllColumns(true);

        TableRow row = new TableRow(view.getContext());

        ImageView bus1 = new ImageView(view.getContext());
        bus1.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.bus_green));
        row.addView(bus1, new TableRow.LayoutParams((int)(width * 0.35), (int)(height * 0.05)));

        RegularTextView busName1 = new RegularTextView(view.getContext());
        busName1.setText("< 50% Full");
        busName1.setTextSize(width / 80);
        busName1.setTextColor(Color.BLACK);
        busName1.setGravity(Gravity.START);
        busName1.setPadding(3, 3,3,3);
        row.addView(busName1, new TableRow.LayoutParams((int)(width * 0.35), (int)(height * 0.05)));

        ImageView bus2 = new ImageView(view.getContext());
        bus2.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.bus_yellow));
        row.addView(bus2, new TableRow.LayoutParams((int)(width * 0.35), (int)(height * 0.05)));

        RegularTextView busName2 = new RegularTextView(view.getContext());
        busName2.setText("< 90% Full");
        busName2.setTextSize(width / 80);
        busName2.setTextColor(Color.BLACK);
        busName2.setGravity(Gravity.START);
        busName2.setPadding(3, 3,3,3);
        row.addView(busName2, new TableRow.LayoutParams((int)(width * 0.35), (int)(height * 0.05)));

        ImageView bus3 = new ImageView(view.getContext());
        bus3.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.bus_red));
        row.addView(bus3, new TableRow.LayoutParams((int)(width * 0.35), (int)(height * 0.05)));

        RegularTextView busName3 = new RegularTextView(view.getContext());
        busName3.setText("> 90% Full");
        busName3.setTextSize(width / 80);
        busName3.setTextColor(Color.BLACK);
        busName3.setGravity(Gravity.START);
        busName3.setPadding(3, 3,3,3);
        row.addView(busName3, new TableRow.LayoutParams((int)(width * 0.35), (int)(height * 0.05)));

        busLegend.addView(row);
    }

    private void setTableTitle(View view) {
        tableTitle = view.findViewById(R.id.schedule_title);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) tableTitle.getLayoutParams();
        layoutParams.height = height / 15;
        tableTitle.setTextSize(width / 50);
    }

    private void setTable(View view) {
        table = view.findViewById(R.id.schedule_table);
        table.setShrinkAllColumns(true);

        InputStreamReader is;

        try {
            is = new InputStreamReader(view.getContext().getAssets().open("c_line.csv"));

            CSVReader csvReader = new CSVReader(is);
            String[] line;

            // throw away the header
            csvReader.readNext();

            List<String[]> items = csvReader.readAll();

            int totalItems = 0;

            for (String[] item: items) {
                TableRow row = new TableRow(view.getContext());
                int counter = 0;

                MediumTextView stop = new MediumTextView(view.getContext());
                stop.setText(item[0]);
                stop.setTextSize(width / 80);
                stop.setTextColor(Color.BLACK);
                stop.setGravity(Gravity.START);
                stop.setPadding(3, 3,3,3);
                row.addView(stop, new TableRow.LayoutParams((int)(width * 0.35), (int)(height * 0.05)));

                boolean isMorning = true;

                for (int i = 1; i < item.length && counter < 5; i++) {

                    String timeString = item[i];

                    if (timeString.contains("12")) {
                        isMorning = false;
                    }

                    if (isMorning) {
                        timeString = timeString + "\nAM";
                    } else {
                        timeString = timeString + "\nPM";
                    }

                    if (afterCurrentTime(timeString)) {
                        RegularTextView time = new RegularTextView(view.getContext());
                        time.setText(timeString);
                        time.setTextSize(width / 80);
                        time.setTextColor(Color.BLACK);
                        time.setGravity(Gravity.CENTER);
                        time.setPadding(3, 3, 3, 3);
                        counter++;
                        totalItems++;

                        if (afterActualCurrentTime(timeString))
                            time.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.A));

                        row.addView(time, new TableRow.LayoutParams((int)(width * 0.13), (int)(height * 0.05)));
                    }
                }

                while (counter < 5) {
                    RegularTextView time = new RegularTextView(view.getContext());
                    time.setPadding(3, 3, 3, 3);
                    counter++;
                    row.addView(time, 1, new TableRow.LayoutParams((int)(width * 0.13), (int)(height * 0.05)));
                }

                table.addView(row);
            }

            if(totalItems == 0) {
                timeHandler.removeCallbacks(timeSimulation);
                predictedTimeTextMin.setText("No Longer in Service");
                table.removeAllViews();

                for (int a = 0; a < items.size(); a++) {
                    String[] item = items.get(a);
                    TableRow row = new TableRow(view.getContext());
                    int counter = 0;

                    MediumTextView stop = new MediumTextView(view.getContext());
                    stop.setText(item[0]);
                    stop.setTextSize(width / 80);
                    stop.setTextColor(Color.BLACK);
                    stop.setGravity(Gravity.START);
                    stop.setPadding(3, 3,3,3);
                    row.addView(stop, new TableRow.LayoutParams((int)(width * 0.35), (int)(height * 0.05)));

                    boolean isMorning = true;

                    for (int i = 1; i < item.length && counter < 5; i++) {

                        String timeString = item[i];

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
                        counter++;
                        totalItems++;
                        row.addView(time, new TableRow.LayoutParams((int)(width * 0.13), (int)(height * 0.05)));
                    }

                    table.addView(row);
                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private boolean afterCurrentTime(String time) {

        try {
            Date currentDate = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentDate);
            cal.add(Calendar.MINUTE, -30);
            Date date1 = cal.getTime();

            String[] timeFormat = time.split(":");
            int hour = Integer.parseInt(timeFormat[0]);
            String[] minuteAMPM = timeFormat[1].split("\n");
            int min = Integer.parseInt(minuteAMPM[0]);

            if (minuteAMPM[1].equals("PM")) {
                if (hour != 12) {
                    hour += 12;
                }
            }

            Calendar compareCal = Calendar.getInstance();
            compareCal.setTime(currentDate);
            compareCal.set(Calendar.HOUR_OF_DAY, hour);
            compareCal.set(Calendar.MINUTE, min);
            Date date2 = compareCal.getTime();

            return date2.after(date1);
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private boolean afterActualCurrentTime(String time) {

        try {
            Date currentDate = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentDate);
            Date date1 = cal.getTime();

            String[] timeFormat = time.split(":");
            int hour = Integer.parseInt(timeFormat[0]);
            String[] minuteAMPM = timeFormat[1].split("\n");
            int min = Integer.parseInt(minuteAMPM[0]);

            if (minuteAMPM[1].equals("PM")) {
                if (hour != 12) {
                    hour += 12;
                }
            }

            Calendar compareCal = Calendar.getInstance();
            compareCal.setTime(currentDate);
            compareCal.set(Calendar.HOUR_OF_DAY, hour);
            compareCal.set(Calendar.MINUTE, min);
            Date date2 = compareCal.getTime();

            return date2.after(date1);
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private void setTableBtn(View view) {
        tableBtn = view.findViewById(R.id.schedule_view_all_btn);
        tableBtn.setTextSize(width / 70);
        tableBtn.setOnClickListener(this);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setAllGesturesEnabled(false);

        silo = new LatLng(SILO[0], SILO[1]);
        laRueOrchardNB = new LatLng(LARUE_ORCHARDNB[0], LARUE_ORCHARDNB[1]);
        andersonRussel = new LatLng(ANDERSON_RUSSELL[0], ANDERSON_RUSSELL[1]);
        andersonSunset = new LatLng(ANDERSON_SUNSET[0], ANDERSON_SUNSET[1]);
        eightSycamore = new LatLng(EIGHTTH_SYCAMORE[0], EIGHTTH_SYCAMORE[1]);
        wakeForest8thStreet = new LatLng(WAKEFOREST_8THSTREET[0], WAKEFOREST_8THSTREET[1]);
        wakeForestOxford = new LatLng(WAKEFOREST_OXFORD[0], WAKEFOREST_OXFORD[1]);
        sycamoreWakeForest = new LatLng(SYCAMORE_WAKEFOREST[0], SYCAMORE_WAKEFOREST[1]);
        laRueOrchardSB = new LatLng(LARUE_ORCHARDSB[0], LARUE_ORCHARDSB[1]);
        hutchisonBioletti = new LatLng(HUTCHISON_BIOLETTI[0], HUTCHISON_BIOLETTI[1]);

        markerPoints = new ArrayList<>();
        markerPoints.add(silo);
        markerPoints.add(hutchisonBioletti);
        markerPoints.add(laRueOrchardNB);
        markerPoints.add(andersonRussel);
        markerPoints.add(andersonSunset);
        markerPoints.add(eightSycamore);
        markerPoints.add(wakeForest8thStreet);
        markerPoints.add(wakeForestOxford);
        markerPoints.add(sycamoreWakeForest);
        markerPoints.add(laRueOrchardSB);
        markerPoints.add(hutchisonBioletti);
        markerPoints.add(silo);

        Marker[] markers = {mGoogleMap.addMarker(new MarkerOptions().position(silo).title("Silo").zIndex(0)),
                mGoogleMap.addMarker(new MarkerOptions().position(laRueOrchardNB).title("La Rue & Orchard (Outbound)").zIndex(0)),
                mGoogleMap.addMarker(new MarkerOptions().position(andersonRussel).title("Anderson & Russell").zIndex(0)),
                mGoogleMap.addMarker(new MarkerOptions().position(andersonSunset).title("Anderson & Sunset").zIndex(0)),
                mGoogleMap.addMarker(new MarkerOptions().position(eightSycamore).title("8th & Sycamore").zIndex(0)),
                mGoogleMap.addMarker(new MarkerOptions().position(wakeForest8thStreet).title("Wake Forest & 8th Street").zIndex(0)),
                mGoogleMap.addMarker(new MarkerOptions().position(wakeForestOxford).title("Wake Forest & Oxford").zIndex(0)),
                mGoogleMap.addMarker(new MarkerOptions().position(sycamoreWakeForest).title("Sycamore & Wake Forest").zIndex(0)),
                mGoogleMap.addMarker(new MarkerOptions().position(laRueOrchardSB).title("La Rue & Orchard (Inbound)").zIndex(0)),
                mGoogleMap.addMarker(new MarkerOptions().position(hutchisonBioletti).title("Hutchison & Bioletti").zIndex(0))};

        MarkerOptions m = new MarkerOptions().position(new LatLng(wakeForestOxford.latitude, wakeForestOxford.longitude + 0.0006));
        m.icon(BitmapDescriptorFactory.fromBitmap(getBitmap(R.drawable.round_account_circle_24)));
        mGoogleMap.addMarker(m);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        final LatLngBounds bounds = builder.build();

        mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
            }
        });

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                // Check if there is an open info window
                if (lastOpenned != null) {
                    // Close the info window
                    lastOpenned.hideInfoWindow();

                    // Is the marker the same marker that was already open
                    if (lastOpenned.equals(marker)) {
                        // Nullify the lastOpenned object
                        lastOpenned = null;
                        // Return so that the info window isn't openned again
                        return true;
                    }
                }

                // Open the info window for the marker
                marker.showInfoWindow();
                // Re-assign the last openned such that we can close it later
                lastOpenned = marker;

                // Event was handled by our code do not launch default behaviour.
                return true;
            }
        });


        //*********************** Draw Polyline *************************//
        geoApiContext = new GeoApiContext.Builder()
                .apiKey(getContext().getResources().getString(R.string.google_maps_key))
                .build();

        new MapAsyncTask(markerPoints).execute(getContext());
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.schedule_view_all_btn:
                mListener.onViewAllClicked("C");
                break;
        }
    }

    private class MapAsyncTask extends AsyncTask<Context, Void, Void> {
        private List<LatLng> markerPoints;
        List<MapPath> storedPaths;

        MapAsyncTask(List<LatLng> markers) {
            markerPoints = new ArrayList<>(markers);
        }

        @Override
        protected Void doInBackground(Context... contexts) {

            storedPaths = mapPathViewModel.getMapPath("C");

            if (storedPaths.size() == 0) {
                for (int a = 0; a < markerPoints.size() - 1; a++) {
                    String origin = markerPoints.get(a).latitude + "," + markerPoints.get(a).longitude;
                    String dest = markerPoints.get(a + 1).latitude + "," + markerPoints.get(a + 1).longitude;
                    DirectionsApiRequest req = DirectionsApi.getDirections(geoApiContext, origin, dest);
                    try {
                        DirectionsResult res = req.await();

                        //Loop through legs and steps to get encoded polylines of each step
                        if (res.routes != null && res.routes.length > 0) {
                            DirectionsRoute route = res.routes[0];

                            if (route.legs != null) {
                                for (int i = 0; i < route.legs.length; i++) {
                                    DirectionsLeg leg = route.legs[i];
                                    if (leg.steps != null) {
                                        for (int j = 0; j < leg.steps.length; j++) {
                                            DirectionsStep step = leg.steps[j];
                                            if (step.steps != null && step.steps.length > 0) {
                                                for (int k = 0; k < step.steps.length; k++) {
                                                    DirectionsStep step1 = step.steps[k];
                                                    EncodedPolyline points1 = step1.polyline;
                                                    if (points1 != null) {
                                                        //Decode polyline and add points to list of route coordinates
                                                        List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                                        for (com.google.maps.model.LatLng coord1 : coords1) {
                                                            path.add(new LatLng(coord1.lat, coord1.lng));
                                                            mapPathViewModel.insert(new MapPath("C", coord1.lat, coord1.lng));
                                                        }
                                                    }
                                                }
                                            } else {
                                                EncodedPolyline points = step.polyline;
                                                if (points != null) {
                                                    //Decode polyline and add points to list of route coordinates
                                                    List<com.google.maps.model.LatLng> coords = points.decodePath();
                                                    for (com.google.maps.model.LatLng coord : coords) {
                                                        path.add(new LatLng(coord.lat, coord.lng));
                                                        mapPathViewModel.insert(new MapPath("C", coord.lat, coord.lng));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception ex) {

                    }
                }
            } else {
                for (int i = 0; i < storedPaths.size(); i++) {
                    path.add(new LatLng(storedPaths.get(i).getLatitude(), storedPaths.get(i).getLongitude()));
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (path.size() > 0) {
                PolylineOptions opts = new PolylineOptions().addAll(path)
                        .color(ContextCompat.getColor(getContext(), R.color.C)).width(7);
                mGoogleMap.addPolyline(opts);

                updateMarkerPosition();
            }
            super.onPostExecute(aVoid);
        }
    }

    private void updateMarkerPosition() {
        busMarkerCounter = 0;

        LatLng newLatLng = path.get(busMarkerCounter);
        busMarkerCounter++;
        doubleDeckerMarkerCounter++;

        MarkerOptions marker = new MarkerOptions().position(newLatLng);
        marker.icon(BitmapDescriptorFactory.fromBitmap(getBitmap(R.drawable.bus_green)));
        marker.zIndex(1);
        busMarker = mGoogleMap.addMarker(marker);
        doubleDeckerMarker = mGoogleMap.addMarker(marker.icon(BitmapDescriptorFactory.fromBitmap(getBitmap(R.drawable.double_decker_green))));

        handler = new Handler();
        doubleDeckerHandler = new Handler();

        handler.post(busSimulation);
        doubleDeckerHandler.postDelayed(busSimulationDoubleDecker, 70000);
    }

    private Bitmap getBitmap(int drawableRes) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private int[] getRandomMinutes(String startLocation, String endLocation) {
        int[] minutes = new int[3];
        int startIndex = 0;
        int endIndex = 0;

        switch(startLocation) {
            case "Silo & Center Island NB":
                startIndex = 0;
                break;
            case "Hutchison & Bioletti NB":
                startIndex = 1;
                break;
            case "La Rue & Orchard NB":
                startIndex = 2;
                break;
            case "Anderson & Russell":
                startIndex = 3;
                break;
            case "Anderson & Sunset":
                startIndex = 4;
                break;
            case "8th & Sycamore":
                startIndex = 5;
                break;
            case "Wake Forest & 8th Street":
                startIndex = 6;
                break;
            case "Wake Forest & Oxford":
                startIndex = 7;
                break;
            case "Sycamore & Wake Forest":
                startIndex = 8;
                break;
            case "La Rue & Orchard SB":
                startIndex = 9;
                break;
            case "Hutchison & Bioletti SB":
                startIndex = 10;
                break;
            case "Silo & Center Island SB":
                startIndex = 11;
                break;
        }

        switch(endLocation) {
            case "Silo & Center Island NB":
                endIndex = 0;
                break;
            case "Hutchison & Bioletti NB":
                endIndex = 1;
                break;
            case "La Rue & Orchard NB":
                endIndex = 2;
                break;
            case "Anderson & Russell":
                endIndex = 3;
                break;
            case "Anderson & Sunset":
                endIndex = 4;
                break;
            case "8th & Sycamore":
                endIndex = 5;
                break;
            case "Wake Forest & 8th Street":
                endIndex = 6;
                break;
            case "Wake Forest & Oxford":
                endIndex = 7;
                break;
            case "Sycamore & Wake Forest":
                endIndex = 8;
                break;
            case "La Rue & Orchard SB":
                endIndex = 9;
                break;
            case "Hutchison & Bioletti SB":
                endIndex = 10;
                break;
            case "Silo & Center Island SB":
                endIndex = 11;
                break;
        }

        int[] time = {2, 2, 1, 1, 2, 1, 1, 4, 4, 2};

        for (int i = startIndex; i < endIndex; i++) {
            minutes[0] += time[startIndex];
        }

        int randomNum = ThreadLocalRandom.current().nextInt(1, 4);

        int random = ThreadLocalRandom.current().nextInt(1, 10);

        if (random < 5)
            lateTime += randomNum / 50.0;
        else
            lateTime -= randomNum / 70.0;

        minutes[0] = (int)(minutes[0] + lateTime);
        minutes[1] = minutes[0] + 30;
        minutes[2] = minutes[1] + 30;

        return minutes;
    }

    public interface OnFragmentInteractionListener {
        void onTimeChanged(String message);
        void onViewAllClicked(String line);
    }

    Runnable busSimulation = new Runnable() {
        @Override
        public void run() {
            int size = path.size();
            if (busMarkerCounter == size) {
                busMarkerCounter = 0;
                lateTime = 0;
            }

            if (busMarkerCounter >= 0 && busMarkerCounter < (size * 0.2)) {
                busMarker.setIcon(BitmapDescriptorFactory.fromBitmap(getBitmap(R.drawable.bus_green)));
            } else if (busMarkerCounter < (size * 0.55)) {
                busMarker.setIcon(BitmapDescriptorFactory.fromBitmap(getBitmap(R.drawable.bus_yellow)));
            } else if (busMarkerCounter < size) {
                busMarker.setIcon(BitmapDescriptorFactory.fromBitmap(getBitmap(R.drawable.bus_red)));
            }

            if (busMarkerCounter >= 0 && busMarkerCounter < (size * 0.05)) {
                startLocation = "Silo & Center Island NB";
            } else if (busMarkerCounter < (size * 0.3)) {
                startLocation = "Hutchison & Bioletti NB";
            } else if (busMarkerCounter < (size * 0.35)) {
                startLocation = "La Rue & Orchard NB";
            } else if (busMarkerCounter < (size * 0.4)) {
                startLocation = "Anderson & Russell";
            } else if (busMarkerCounter < (size * 0.45)) {
                startLocation = "Anderson & Sunset";
            } else if (busMarkerCounter < (size * 0.53)) {
                startLocation = "8th & Sycamore";
            } else if (busMarkerCounter < (size * 0.58)) {
                startLocation = "Wake Forest & 8th Street";
            } else if (busMarkerCounter < (size * 0.63)) {
                startLocation = "Wake Forest & Oxford";
            } else if (busMarkerCounter < (size * 0.68)) {
                startLocation = "Sycamore & Wake Forest";
            } else if (busMarkerCounter < (size * 0.8)) {
                startLocation = "La Rue & Orchard SB";
            } else if (busMarkerCounter < (size * 0.9)) {
                startLocation = "Hutchison & Bioletti SB";
            } else if (busMarkerCounter < size) {
                startLocation = "Silo & Center Island SB";
            }

            busMarker.setPosition(path.get(busMarkerCounter));

            busMarkerCounter++;
            handler.postDelayed(this, 1000);
        }
    };

    Runnable busSimulationDoubleDecker = new Runnable() {
        @Override
        public void run() {
            int size = path.size();
            if (doubleDeckerMarkerCounter == size) {
                doubleDeckerMarkerCounter = 0;
            }

            if (doubleDeckerMarkerCounter >= 0 && doubleDeckerMarkerCounter < (size * 0.2)) {
                doubleDeckerMarker.setIcon(BitmapDescriptorFactory.fromBitmap(getBitmap(R.drawable.double_decker_green)));
            } else if (doubleDeckerMarkerCounter < (size * 0.55)) {
                doubleDeckerMarker.setIcon(BitmapDescriptorFactory.fromBitmap(getBitmap(R.drawable.double_decker_yellow)));
            } else if (doubleDeckerMarkerCounter < size) {
                doubleDeckerMarker.setIcon(BitmapDescriptorFactory.fromBitmap(getBitmap(R.drawable.double_decker_red)));
            }

            doubleDeckerMarker.setPosition(path.get(doubleDeckerMarkerCounter));
            doubleDeckerMarkerCounter++;
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
