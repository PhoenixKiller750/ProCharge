package com.jayden.batteryalarm.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jayden.batteryalarm.R;
import com.jayden.batteryalarm.model.AlarmData;
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LogHistoryAdapter extends RecyclerView.Adapter<LogHistoryAdapter.LogViewHolder> {
    private Context context ;
    private List<AlarmData> alarmDataList ;

    public LogHistoryAdapter(Context context, List<AlarmData> alarmDataArrayList) {
        this.context = context;
        this.alarmDataList = alarmDataArrayList;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // Inflate the layout view you have created for the list rows here
        View view = layoutInflater.inflate(R.layout.data_log_histoty_list, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {

        AlarmData alarmData = alarmDataList.get(position);
        Log.e("hf" , alarmData.toString());

        holder.total_progress.setProgress(alarmData.getUnplugg_percentage());
        holder.txt_per.setText(alarmData.getUnplugg_percentage() + " %");
        Log.e("TYPE ", alarmData.getAlarm_type() + " ");
        if (alarmData.getAlarm_type().equalsIgnoreCase("TIME")) {
            //Connected Time
            holder.txt_set_time.setText(alarmData.getSelected_hour() + " h " + alarmData.getSelected_minute() + " min");

            //Disconnected Time
            SimpleDateFormat format = new SimpleDateFormat(" MMM dd yyyy HH:mm:ss", Locale.getDefault());
            try {
                Date date1 = format.parse(alarmData.getCurrent_date_time());
                Date date2 = format.parse(alarmData.getUnplagg_date_time());
                assert date2 != null;
                assert date1 != null;
                long millis = date2.getTime() - date1.getTime();

                long minutes = (millis / (1000 * 60)) % 60;
                long hours = millis / (1000 * 60 * 60);

                long difference_hours = alarmData.getSelected_hour() - hours;
                long difference_min = alarmData.getSelected_minute() - minutes ;

                holder.txt_discharge_time.setText(difference_hours + " h "+ difference_hours + " min");

                if (difference_hours > 0 && difference_min > 0 ){
                    alarmData.setSelected_minute((int) difference_min);
                    alarmData.setSelected_hour((int) difference_hours);
                }
                Log.e("RECEIVER " , "hours  : " + hours + " minutes : " + minutes );
                Log.e("RECEIVER " , "difference_hours  : " + difference_hours + " difference_min : " + difference_min );

            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("CATCh" , e.getMessage() + " ");
            }
        }
        else {
            // Connected Persentage
            holder.txt_set_time.setText(alarmData.getCurrent_percentage() + " % ");
            //Disconnected Purcentage
            holder.txt_discharge_time.setText(alarmData.getUnplugg_percentage() + " % ");
        }

        String date_demo = alarmData.getCurrent_date_time();
        date_demo = date_demo.substring(0,12);
        StringBuilder sb = new StringBuilder(date_demo);
        sb.insert(7, ',');
        holder.txt_date.setText(sb.toString());
    }

    @Override
    public int getItemCount() {
        return alarmDataList.size();
    }

    public class LogViewHolder extends RecyclerView.ViewHolder{
        private ProgressBar total_progress;
        private TextView txt_per , txt_date , txt_set_time , txt_discharge_time;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            total_progress = itemView.findViewById(R.id.total_progress);
            txt_per = itemView.findViewById(R.id.txt_per);
            txt_date = itemView.findViewById(R.id.txt_date);
            txt_set_time = itemView.findViewById(R.id.txt_set_time);
            txt_discharge_time = itemView.findViewById(R.id.txt_discharge_time);
        }
    }
}
