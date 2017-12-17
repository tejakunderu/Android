package com.example.evpru.assignment1_graph;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by evpru on 3/6/2017.
 */

public class AccelerometerService extends Service implements SensorEventListener
{
    AccelerometerReaderDbHelper acceaderDbHelper;
    SQLiteDatabase db;

    SensorManager mSensorManager;
    Sensor accSensor;

    Long currentTimeMillis;
    Long lastTimeMillis;
    String tableName;

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d("MyAPP1", "onCreate: Accelerometer");
//        Toast.makeText(AccelerometerService.this,"AccelerometerService onCreate",Toast.LENGTH_LONG).show();
        currentTimeMillis = System.currentTimeMillis();
        lastTimeMillis = System.currentTimeMillis();

        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        if(null != mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER))
        {
            accSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        mSensorManager.registerListener(this,accSensor,1000000);

        acceaderDbHelper = MainActivity.getAcceaderDbHelper();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        tableName = intent.getStringExtra("tableName");
//        Toast.makeText(AccelerometerService.this,"AccelerometerService onStartCommand: " +tableName ,Toast.LENGTH_LONG).show();
        MainActivity.service_running = true;
        Log.d("MyAPP1", "onStartCommand: Accelerometer");

        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        mSensorManager.unregisterListener(this,accSensor);
        MainActivity.service_running = false;
        Log.d("MyAPP1", "onDestroy: Accelerometer");
//        Toast.makeText(AccelerometerService.this,"AccelerometerService onDestroy",Toast.LENGTH_LONG).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        currentTimeMillis = System.currentTimeMillis();

        if(currentTimeMillis - lastTimeMillis < 1000)
        {
            return;
        }
        lastTimeMillis = currentTimeMillis;
        String ts = currentTimeMillis.toString();

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        ContentValues values = new ContentValues();
        values.put(AccelerometerDBContract.AccelerometerEntry.COLUMN_NAME_TIMESTAMP, ts);
        values.put(AccelerometerDBContract.AccelerometerEntry.COLUMN_NAME_XVALUES, x);
        values.put(AccelerometerDBContract.AccelerometerEntry.COLUMN_NAME_YVALUES, y);
        values.put(AccelerometerDBContract.AccelerometerEntry.COLUMN_NAME_ZVALUES, z);
        long newRowId = 0;
        try{
            acceaderDbHelper.addEntry(tableName,ts,x,y,z);
            Log.d("MyAPPN","Sensor value added!");
        }
        catch (Exception e)
        {
            Log.d("MyAPP1",e.getMessage());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }
}
