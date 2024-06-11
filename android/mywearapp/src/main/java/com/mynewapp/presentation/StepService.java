package com.mynewapp.presentation;

import static android.os.Build.VERSION_CODES.R;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.mynewapp.R;

public class StepService extends Service {
    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    public static final String ACTION_STOP_SERVICE = "com.goxlabs.amplifywatch.action.STOP_SERVICE";

    private static final long INTERVAL = 1 * 30 * 1000; // 5 minutes in milliseconds

    @Override
    public void onCreate() {
        super.onCreate();
        // Schedule the repeating alarm
        scheduleRepeatingAlarm();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_STOP_SERVICE.equals(action)) {
                stopService();
                return START_NOT_STICKY;
            }
        }

        createNotificationChannel();
        Intent notificationIntent = new Intent(this, StepService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), com.mynewapp.R.drawable.splash_icon);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Amplify Watch Service")
                .setContentText("The service process noise metrics")
                .setSmallIcon(com.mynewapp.R.drawable.splash_icon)
                .setLargeIcon(bm)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(700, notification);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Here we do not use a binder to interact with the foreground service.
        return null;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void scheduleRepeatingAlarm() {
        Intent intent = new Intent(this, SensorReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(getApplicationContext().ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + INTERVAL, pendingIntent);
    }
    private void stopService() {
        stopForeground(true); // Stop service from running in the foreground
        stopSelf(); // Stop the service
    }

}