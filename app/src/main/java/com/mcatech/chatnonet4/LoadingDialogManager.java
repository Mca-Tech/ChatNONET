package com.mcatech.chatnonet4;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import java.util.Objects;

public class LoadingDialogManager {

    private Dialog dialog;

    public LoadingDialogManager(Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        // Set custom background drawable with rounded corners
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.main_foreground);

        // Set dialog size and layout parameters
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(layoutParams);

        // Create and set up the ProgressBar
        ProgressBar progressBar = new ProgressBar(context);
        RelativeLayout layout = new RelativeLayout(context);
        LayoutParams params = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(progressBar, params);
        dialog.setContentView(layout);
    }

    public void showLoadingDialog(boolean show) {
        if (show) {
            dialog.show();
        } else {
            dialog.dismiss();
        }
    }
}

