package com.AflamTvMaroc.moviesdarija.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.TextView;

import com.AflamTvMaroc.moviesdarija.R;
import com.AflamTvMaroc.moviesdarija.interfaces.OnAdStatusChangeListener;

public class LoadingDialog {
    private static final String TAG = "LoadingDialog";
    private Dialog dialog;
    private SeekBar seekBar;
    private TextView progressTextView;
    boolean drag = false;


    public LoadingDialog(Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        seekBar = dialog.findViewById(R.id.seekBar);
        progressTextView = dialog.findViewById(R.id.progressTextView);
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }
    public Boolean isShowing(){
        return dialog.isShowing();
    }

    public void setProgress(int progress1) {

        drag = false;
        seekBar.setProgress(progress1);
        progressTextView.setText(progress1 + "%");
        drag = true;
        dialog.show();

        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (drag) {
                    return true;
                }
                return false;
            }
        });

    }

    public void setMax(int max) {
        seekBar.setMax(max);
    }
}
