package com.mynewapp.presentation;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.util.Log;

import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;


public class SensorReceiver extends BroadcastReceiver {
    static String TAG = SensorReceiver.class.getSimpleName();
    Context context;

    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private Sensor heartRateSensor;
    private SensorEventListener sensorEventListener;
    private Sensor offBodySensor;
    private PutDataMapRequest putDataMapRequest;


    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        putDataMapRequest = PutDataMapRequest.create("/heath_data/provider");
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        offBodySensor = sensorManager.getDefaultSensor(Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT);
        setOnetimeTimerAfter5Min(context);
        checkStepData();
        checkHeartRateData();


    }

    private void checkStepData() {
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {

                    float stepsSinceLastReboot = event.values[0];
                    putDataMapRequest.getDataMap().putString("steps", String.valueOf(stepsSinceLastReboot));
                    PutDataRequest request = putDataMapRequest.asPutDataRequest();
                    Wearable.getDataClient(context).putDataItem(request);


                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                Log.d(TAG, "Accuracy changed to: " + accuracy);
            }
        };
        sensorManager.unregisterListener(sensorEventListener);
        sensorManager.registerListener(sensorEventListener, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);

    }

    private void checkHeartRateData() {
        SensorEventListener listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                if (event == null) return;
                long latestHeartRate = (long) event.values[0];
                putDataMapRequest.getDataMap().putString("heartrate", String.valueOf(latestHeartRate));
                PutDataRequest request = putDataMapRequest.asPutDataRequest();
                Wearable.getDataClient(context).putDataItem(request);

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                Log.d("TAG", "Accuracy changed to: " + accuracy);
            }
        };

        sensorManager.unregisterListener(listener);
        boolean supportedAndEnabled = sensorManager.registerListener(listener,
                heartRateSensor, SensorManager.SENSOR_DELAY_UI);

    }

    private void checkOffBodyDetect()
    {

        SensorEventListener offBodyListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                float offbodyDataFloat = event.values[0];
                int offbodyData = Math.round(offbodyDataFloat);

            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                Log.d("TAG", "Accuracy changed to: " + accuracy);
            }
        };

        sensorManager.unregisterListener(offBodyListener);
        boolean supportedAndEnabled = sensorManager.registerListener(offBodyListener,
                offBodySensor, SensorManager.SENSOR_DELAY_UI);

    }
    public void setOnetimeTimerAfter5Min(Context context) {
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, SensorReceiver.class);
        ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager.AlarmClockInfo ac = new AlarmManager.AlarmClockInfo(System.currentTimeMillis() + 1 * 60 * 1000, pi);
        mgr.setAlarmClock(ac, pi);
        intentArray.add(pi);
    }




}
