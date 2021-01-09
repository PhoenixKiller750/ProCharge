package com.jayden.batteryalarm.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.jayden.batteryalarm.model.IntruderData;
import com.jayden.batteryalarm.R;
import com.jayden.batteryalarm.model.IntruderData;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

public class FullScreenImageDialog extends Dialog {
    private Context context ;
    private Activity activity ;
    private IntruderData intruderData;

    public FullScreenImageDialog(Context context, int dialogCustomTheme, Activity activity, IntruderData intruderData) {
        super(context , dialogCustomTheme);
        this.context = context;
        this.activity =  activity;
        this.intruderData = intruderData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_full_screen_image);
    }

    @Override
    protected void onStart() {
        super.onStart();

        ImageView imgDisplay =  findViewById(R.id.imgDisplay);
        Button btnClose =  findViewById(R.id.btnClose);
        Button btnShare = findViewById(R.id.btnShare);

        final Uri uri = Uri.fromFile(new File(intruderData.getIntruder_path()));
        Picasso.get().load(uri)
                .resize(96, 96)
                .centerCrop()
                .memoryPolicy(MemoryPolicy.NO_CACHE).
                into(imgDisplay);

        btnClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                context.startActivity(Intent.createChooser(intent, "Share Image"));
            }
        });
    }
}
