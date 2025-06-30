package com.mcatech.chatnonet4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;

public class ActionReceiver extends BroadcastReceiver {
    public static final String ACTION_HIDE = "com.example.app.ACTION_HIDE";
    private static final int NOTIF_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_HIDE.equals(intent.getAction())) {
            NotificationManagerCompat.from(context).cancel(NOTIF_ID);
        }
    }
}