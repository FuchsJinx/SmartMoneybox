package com.smb.smartmoneybox.utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.smb.smartmoneybox.R;
import com.smb.smartmoneybox.ui.activities.MainActivity;

import java.util.Calendar;

public class NotificationScheduler {

    private static final String CHANNEL_ID = "savings_reminder_channel";
    private static final String PREFS_NAME = "SmartMoneyBoxPrefs";
    private static final String KEY_LAST_NOTIFICATION = "last_notification";

    public static void scheduleMonthlyNotification(Context context) {
        // Создаем канал уведомлений (для Android 8.0+)
        createNotificationChannel(context);

        // Проверяем, не отправляли ли уведомление в этом месяце
        if (shouldSendNotification(context)) {
            showNotification(context);
            markNotificationSent(context);
        }

        // Планируем следующую проверку
        scheduleNextCheck(context);
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Напоминания о накоплениях",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Уведомления о рекомендуемых пополнениях целей");

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private static boolean shouldSendNotification(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long lastNotification = prefs.getLong(KEY_LAST_NOTIFICATION, 0);

        Calendar lastCal = Calendar.getInstance();
        lastCal.setTimeInMillis(lastNotification);

        Calendar now = Calendar.getInstance();

        // Проверяем, был ли отправлено уведомление в этом месяце
        return lastCal.get(Calendar.MONTH) != now.get(Calendar.MONTH) ||
                lastCal.get(Calendar.YEAR) != now.get(Calendar.YEAR);
    }

    private static void showNotification(Context context) {
        // Получаем сохраненный доход
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        double monthlyIncome = prefs.getFloat("monthly_income", 0f);

        if (monthlyIncome <= 0) return;

        // Создаем интент для открытия приложения
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Создаем уведомление
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_savings)
                .setContentTitle("Время пополнять цели!")
                .setContentText("Рекомендуемая сумма для накоплений: " + formatCurrency(monthlyIncome * 0.3))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("На основе вашего дохода рекомендуем отложить " +
                                formatCurrency(monthlyIncome * 0.3) + " на ваши финансовые цели."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        manager.notify(1, notification);
    }

    private static String formatCurrency(double amount) {
        return String.format("%.2f ₽", amount);
    }

    private static void markNotificationSent(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(KEY_LAST_NOTIFICATION, System.currentTimeMillis());
        editor.apply();
    }

    @SuppressLint("ScheduleExactAlarm")
    private static void scheduleNextCheck(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Планируем проверку на первое число следующего месяца
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 11); // 11:00 утра
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        } else {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        }
    }

    public static void testNotification(Context context) {
        createNotificationChannel(context);

        // Простое уведомление без проверок
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_savings) // Убедитесь, что иконка существует!
                .setContentTitle("Тестовое уведомление")
                .setContentText("Проверка работы уведомлений")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();

        manager.notify(12345, notification); // Уникальный ID
        Log.d("NotificationTest", "Уведомление отправлено");
    }
}