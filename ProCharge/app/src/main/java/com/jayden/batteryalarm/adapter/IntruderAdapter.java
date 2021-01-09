package com.jayden.batteryalarm.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jayden.batteryalarm.R;
import com.jayden.batteryalarm.dialog.FullScreenImageDialog;
import com.jayden.batteryalarm.model.IntruderData;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import jp.wasabeef.picasso.transformations.BlurTransformation;

public class IntruderAdapter extends BaseAdapter {
    private Context context;
    private Activity activity;
    List<IntruderData> intruderDataList;
    LayoutInflater inflater ;

    private Picasso mPicasso;

    public IntruderAdapter(Context context, Activity activity, List<IntruderData> intruderDataList) {
        this.context = context;
        this.activity = activity;
        this.intruderDataList = intruderDataList;
        inflater = (LayoutInflater)activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (context != null) {
            Picasso.Builder builder = new Picasso.Builder(context);
            mPicasso = builder.build();
        }

    }

    @Override
    public int getCount() {
        return intruderDataList.size();
    }

    @Override
    public IntruderData getItem(int i) {
        return intruderDataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View vi = view;
        final ViewHolder holder;
        if(view == null){
            vi = inflater.inflate(R.layout.intruder_log_layout,   null);
            holder = new ViewHolder();
            holder.img_intruder_photo = vi.findViewById(R.id.img_photo);
            holder.txt_date = vi.findViewById(R.id.txt_date);
            holder.txt_time = vi.findViewById(R.id.txt_time);
            holder.rel_view = vi.findViewById(R.id.rel_password);

            vi.setTag( holder );
        }
        else{
            holder =(ViewHolder)vi.getTag();
        }

        final IntruderData intruderData = intruderDataList.get(i);

        Uri uri = Uri.fromFile(new File(intruderData.getIntruder_path()));
        Picasso.get().load(uri)
                .transform(new BlurTransformation(context, 25, 1))
                .resize(96, 96).centerCrop().memoryPolicy(MemoryPolicy.NO_CACHE).
                into(holder.img_intruder_photo);

        holder.txt_date.setText(intruderData.getIntruder_date());
        holder.txt_time.setText(intruderData.getIntruder_time());

        holder.rel_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FullScreenImageDialog fullScreenImageDialog = new FullScreenImageDialog(context , R.style.AppTheme , activity , intruderData);
                fullScreenImageDialog.show();
            }
        });


        return vi;
    }

    public static class ViewHolder{
        public TextView  txt_date,txt_time;
        public ImageView img_intruder_photo;
        RelativeLayout rel_view;
    }
}
