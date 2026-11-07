package com.example.littlebylittle;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MindfulCalmService extends Service {

    private static final String TAG = "MindfulCalmService";
    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final int NOTIFICATION_ID = 2001;

    private final IBinder binder = new LocalBinder();
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private int elapsedSeconds = 0;
    private boolean isRunning = false;

    public class LocalBinder extends Binder {
        public MindfulCalmService getService() {
            return MindfulCalmService.this;
        }
    }

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                elapsedSeconds++;
                timerHandler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service onCreate() called - Initializing Mindful Calm Service");
        NotificationHelper.createNotificationChannels(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service onStartCommand() called with startId: " + startId);

        if (intent != null && ACTION_STOP.equals(intent.getAction())) {
            stopCalmSession();
            stopSelf();
            return START_NOT_STICKY;
        }

        startCalmSession();
        startForeground(NOTIFICATION_ID, buildForegroundNotification());

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Service onBind() called - Activity successfully bound to service");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "Service onUnbind() called");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service onDestroy() called - Releasing session resources");
        stopCalmSession();
    }

    public void startCalmSession() {
        if (!isRunning) {
            isRunning = true;
            timerHandler.post(timerRunnable);
        }
    }

    public void stopCalmSession() {
        isRunning = false;
        timerHandler.removeCallbacks(timerRunnable);
        stopForeground(true);

        // Lab 10: Custom Broadcast Event
        if (elapsedSeconds > 5) {
            Intent broadcastIntent = new Intent(SystemStatusReceiver.ACTION_GROWTH_DATA_UPDATED);
            broadcastIntent.setPackage(getPackageName());
            sendBroadcast(broadcastIntent);
        }
    }

    public int getElapsedSeconds() {
        return elapsedSeconds;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void resetTimer() {
        elapsedSeconds = 0;
    }

    private Notification buildForegroundNotification() {
        Intent stopIntent = new Intent(this, MindfulCalmService.class);
        stopIntent.setAction(ACTION_STOP);

        PendingIntent stopPendingIntent = PendingIntent.getService(
                this,
                0,
                stopIntent,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        ? PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                        : PendingIntent.FLAG_UPDATE_CURRENT
        );

        Intent openAppIntent = new Intent(this, MainActivity.class);
        PendingIntent openAppPendingIntent = PendingIntent.getActivity(
                this,
                1,
                openAppIntent,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        ? PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                        : PendingIntent.FLAG_UPDATE_CURRENT
        );

        return new NotificationCompat.Builder(this, NotificationHelper.CHANNEL_CALM_ID)
                .setSmallIcon(R.drawable.ic_avatar)
                .setContentTitle("Mindful Calm Session Active 🧘")
                .setContentText("Inhale gently, exhale slowly. Focus on the present moment.")
                .setContentIntent(openAppPendingIntent)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop Session", stopPendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }
}
