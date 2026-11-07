package com.example.littlebylittle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.Toast;

public class SystemStatusReceiver extends BroadcastReceiver {

    private static final String TAG = "SystemStatusReceiver";

    public static final String ACTION_GROWTH_DATA_UPDATED = "com.example.littlebylittle.ACTION_GROWTH_DATA_UPDATED";

    public interface StatusListener {
        void onStatusChanged(String message);
    }

    private static StatusListener listener;

    public static void setStatusListener(StatusListener newListener) {
        listener = newListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;

        String action = intent.getAction();
        Log.d(TAG, "Broadcast received: " + action);

        if (Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(action)) {
            boolean isAirplaneModeOn = intent.getBooleanExtra("state", false);
            String msg = isAirplaneModeOn
                    ? "✈️ Airplane Mode enabled: Little by Little is running in offline-first mode with private local Room DB."
                    : "🌐 Network reconnected: Realtime sync with Firebase is active.";

            Log.i(TAG, msg);
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

            if (listener != null) {
                listener.onStatusChanged(msg);
            }

        } else if (Intent.ACTION_BATTERY_LOW.equals(action)) {
            String msg = "🔋 Battery is low! Little by Little has auto-saved all your reflections and switched to battery saver mode.";
            Log.w(TAG, msg);
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

            if (listener != null) {
                listener.onStatusChanged(msg);
            }

        } else if (Intent.ACTION_BATTERY_OKAY.equals(action)) {
            String msg = "⚡ Battery level restored.";
            Log.i(TAG, msg);

            if (listener != null) {
                listener.onStatusChanged(msg);
            }

        } else if (ACTION_GROWTH_DATA_UPDATED.equals(action)) {
            // Lab 10: Custom Broadcast Event Handling
            String msg = "🌱 Little by Little: Progress updated!";
            Log.i(TAG, msg);
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

            if (listener != null) {
                listener.onStatusChanged(msg);
            }
        }
    }
}
