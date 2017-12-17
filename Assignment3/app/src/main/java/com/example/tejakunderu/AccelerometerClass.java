package com.example.tejakunderu;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by tejak on 4/2/2017.
 */

public class AccelerometerClass extends Service implements SensorEventListener {

    private SensorManager accelerometerManage;
    private Sensor accelerometerSensor;
    private String activity;
    Bundle b;

    int column = 0;
    int row = 0;
    Long currentTimeMillis;
    Long lastTimeMillis;

    String sensorReading = getSensorReadingString();

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor currentSensor = event.sensor;

        currentTimeMillis = System.currentTimeMillis();

        if(currentTimeMillis - lastTimeMillis < 100)
            return;
        lastTimeMillis = currentTimeMillis;

        if(currentSensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            sensorReading += event.values[0] + ", "
                    + event.values[1] + ", "
                    + event.values[2] + ", ";

            column++;

            if(column > 49) {
                sensorReading += "'" + activity + "');";
                try {
                    MainActivity.db.execSQL(sensorReading);
                    System.out.println(sensorReading);
                } catch (SQLException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }

                sensorReading = getSensorReadingString();
                row++;
                column = 0;
            }

            if(row > 19) {
                accelerometerManage.unregisterListener(this);
                this.stopSelf();
            }
        }
    }

    private String getSensorReadingString() {
        String sensorReading = "INSERT INTO AccelerometerData(";

        for(int i = 0; i < 50; i++)
            sensorReading += "AccelX" + i + ", "
                    + "AccelY" + i + ", "
                    + "AccelZ" + i + ", ";

        sensorReading += "Label) VALUES(";

        return sensorReading;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service started", Toast.LENGTH_LONG).show();

        currentTimeMillis = System.currentTimeMillis();
        lastTimeMillis = System.currentTimeMillis();

        accelerometerManage = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = accelerometerManage.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelerometerManage.registerListener(this, accelerometerSensor, 1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        b = intent.getExtras();
        activity = b.getString("activity");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Terminated", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
