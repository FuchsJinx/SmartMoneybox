package com.smb.smartmoneybox.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.smb.smartmoneybox.utils.NotificationScheduler;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationScheduler.scheduleMonthlyNotification(context);
    }
}
