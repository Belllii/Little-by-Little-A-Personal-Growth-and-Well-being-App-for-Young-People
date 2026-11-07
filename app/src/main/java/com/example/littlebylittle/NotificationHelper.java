package com.example.littlebylittle;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHelper {

    public static final String CHANNEL_CALM_ID = "channel_mindful_calm";
    public static final String CHANNEL_REMINDERS_ID = "channel_daily_reminders";

    public static void createNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager == null) return;

            // Channel 1: Mindful Calm Session (Foreground Service)
            NotificationChannel calmChannel = new NotificationChannel(
                    CHANNEL_CALM_ID,
                    "Mindful Calm Sessions",
                    NotificationManager.IMPORTANCE_LOW
            );
            calmChannel.setDescription("Shows active calm breathing and focus sessions");
            manager.createNotificationChannel(calmChannel);

            // Channel 2: Daily Check-in Reminders
            NotificationChannel reminderChannel = new NotificationChannel(
                    CHANNEL_REMINDERS_ID,
                    "Daily Check-in Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            reminderChannel.setDescription("Gentle reminders to check in with your emotions");
            manager.createNotificationChannel(reminderChannel);
        }
    }

    public static void showDailyReminderNotification(Context context) {
        createNotificationChannels(context);

        Intent intent = new Intent(context, CheckInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        ? PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                        : PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_REMINDERS_ID)
                .setSmallIcon(R.drawable.ic_avatar)
                .setContentTitle("Little by Little 🌱")
                .setContentText("Take a deep breath. How are you feeling right now?")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Take a deep breath and give yourself a minute. A small daily check-in helps you reflect and grow."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(1001, builder.build());
        }
    }
}
