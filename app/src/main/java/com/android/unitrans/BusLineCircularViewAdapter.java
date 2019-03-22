package com.android.unitrans;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.android.unitrans.CustomViews.MediumButton;

import java.util.ArrayList;
import java.util.List;

public class BusLineCircularViewAdapter extends RecyclerView.Adapter<BusLineCircularViewAdapter.MyViewHolder> {

    Context context;
    int width, height;
    List<String> busLines;
    public BusLineCircularViewAdapter(Context context, List<String> busLines) {
        this.context = context;
        this.busLines = new ArrayList<>(busLines);
        width = context.getResources().getDisplayMetrics().widthPixels;
        height = context.getResources().getDisplayMetrics().heightPixels;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        FrameLayout layout;
        MediumButton button;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.adapter_layout);

            button = itemView.findViewById(R.id.circular_btn);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) button.getLayoutParams();
            int diameter = ((height > width) ? height : width);
            int size = (int)((diameter / 2) * 0.13);
            layoutParams.width = size;
            layoutParams.height = size;
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter_bus_line_circular_view, viewGroup, false);

        return new BusLineCircularViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        int index = myViewHolder.getAdapterPosition();

        String busName = busLines.get(index);
        myViewHolder.button.setText(busName);

        if (busName.equals("C")) {
            myViewHolder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.tungsten));
        } else {
            myViewHolder.layout.setBackgroundColor(Color.TRANSPARENT);
        }

        Drawable drawable = ContextCompat.getDrawable(context, getDrawableID(index));
        myViewHolder.button.setBackground(drawable);

    }

    @Override
    public int getItemCount() {
        return busLines.size();
    }

    private int getDrawableID(int index) {
        int id = 0;

        switch(index) {
            case 0: id = R.drawable.circular_a; break;
            case 1: id = R.drawable.circular_b; break;
            case 2: id = R.drawable.circular_c; break;
            case 3: id = R.drawable.circular_d; break;
            case 4: id = R.drawable.circular_e; break;
            case 5: id = R.drawable.circular_f; break;
            case 6: id = R.drawable.circular_g; break;
            case 7: id = R.drawable.circular_j; break;
            case 8: id = R.drawable.circular_k; break;
            case 9: id = R.drawable.circular_l; break;
            case 10: id = R.drawable.circular_m; break;
            case 11: id = R.drawable.circular_o; break;
            case 12: id = R.drawable.circular_p; break;
            case 13: id = R.drawable.circular_q; break;
            case 14: id = R.drawable.circular_s; break;
            case 15: id = R.drawable.circular_t; break;
            case 16: id = R.drawable.circular_v; break;
            case 17: id = R.drawable.circular_w; break;
            case 18: id = R.drawable.circular_x; break;
            case 19: id = R.drawable.circular_z; break;
        }

        return id;
    }
}
