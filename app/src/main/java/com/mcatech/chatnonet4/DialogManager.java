package com.mcatech.chatnonet4;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DialogManager {

    public interface DialogCallback {
        void onPositiveButtonPressed();
        void onNegativeButtonPressed();
    }

    @SuppressLint("MissingInflatedId")
    public static void popUp(Context context, String title, String message, String positiveButtonText, String negativeButtonText, final DialogCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.activity_dialog_manager, null);
        TextView titleTextView = dialogView.findViewById(R.id.title);
        TextView messageTextView = dialogView.findViewById(R.id.message);
        Button positiveButton = dialogView.findViewById(R.id.positiveButton);
        Button negativeButton = dialogView.findViewById(R.id.negativeButton);

        titleTextView.setText(title);
        messageTextView.setText(message);
        positiveButton.setText(positiveButtonText);
        if (negativeButtonText != null) {
            negativeButton.setText(negativeButtonText);
        } else {
            negativeButton.setVisibility(View.GONE);
        }

        AlertDialog dialog = builder.setView(dialogView).create();

        // Prevent the dialog from being dismissed by clicking outside or pressing the back button
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (callback != null) {
                    callback.onPositiveButtonPressed();
                }
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (callback != null) {
                    callback.onNegativeButtonPressed();
                }
            }
        });
    }

    // Overloaded method without negative button
    public static void popUp(Context context, String title, String message, String positiveButtonText, final DialogCallback callback) {
        popUp(context, title, message, positiveButtonText, null, callback);
    }
}
