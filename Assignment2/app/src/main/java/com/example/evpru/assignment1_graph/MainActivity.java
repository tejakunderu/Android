package com.example.evpru.assignment1_graph;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements Runnable{
    GraphView graphView;
    RelativeLayout mainRelativeLayout;
    Thread myThread;
    InputMethodManager inputManager;
    EditText patientIDET;
    EditText ageET;
    EditText patientNameET;
    RadioGroup genderRB;
    Button runButton, stopButton, uploadDBButton, downloadDBButton;
    String tableName;

    private UploadToServer uploadToServer;
    public ConnectivityManager cManager;
    public NetworkInfo nInfo;

//    static boolean tableCreated = false;
    static String DB_PATH = "/data/data/com.example.evpru.assignment1_graph/databases/Group11.db";

    static AccelerometerReaderDbHelper acceaderDbHelper;
//    SQLiteDatabase db;

    int rightandleftpadding = 128;
    Object thread = new Object();
    boolean isPaused = true;
//    Random rand = new Random(10);
//    int numberOfSamples = 10;
    float[] x_values = new float[10];
    float[] y_values = new float[10];
    float[] z_values = new float[10];
    static boolean service_running = false;

    String[] verlabels = {"200", "150", "100", "50", ""};
    String[] horlabels = {"", "50", "55", "60", "65", "70", "75", "80", "85", "90"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenwidth = size.x;

        int widthofLayout = screenwidth - rightandleftpadding;

        cManager = (ConnectivityManager)getSystemService(this.CONNECTIVITY_SERVICE);
        nInfo = cManager.getActiveNetworkInfo();

        patientIDET = (EditText) findViewById(R.id.editText2);
        ageET = (EditText) findViewById(R.id.editText3);
        patientNameET = (EditText) findViewById(R.id.editText4);
        genderRB = (RadioGroup) findViewById(R.id.genderRB);
        mainRelativeLayout = (RelativeLayout)findViewById(R.id.mainRelativeLayout);
        runButton = (Button) findViewById(R.id.runButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        uploadDBButton = (Button) findViewById(R.id.uploadDBButton);
        downloadDBButton = (Button) findViewById(R.id.downloadDBButton);

        patientIDET.setWidth(2*widthofLayout/7);
        ageET.setWidth(widthofLayout/7);
        patientNameET.setWidth(2*widthofLayout/7);

        graphView = new GraphView(MainActivity.this, x_values,y_values,z_values, "", horlabels, verlabels, true);
        mainRelativeLayout.addView(graphView);

        uploadToServer = UploadToServer.getInstance("Group11.db", MainActivity.this);

        myThread = new Thread(this);

//        testAccelerometer();
    }

//
//    public void testAccelerometer()
//    {
//        acceaderDbHelper = new AccelerometerReaderDbHelper(getBaseContext(),"Alex_123_23_Male");
//        db = acceaderDbHelper.getWritableDatabase();
//
//        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
//        if(null != mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER))
//        {
//            accSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        }
//        mSensorManager.registerListener(this,accSensor,1000000);
//    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putBoolean("isPaused",isPaused);
//        outState.putSerializable("Values",values);
//    }

//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        isPaused = savedInstanceState.getBoolean("isPaused");
//        values = (float[])savedInstanceState.getSerializable("Values");
//        if(!isPaused)
//        {
//            myThread.start();
//        }
//    }

    public void runGraph(View v)
    {
        inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

        String patientName = patientNameET.getText().toString();
        String age = ageET.getText().toString();
        String patientID = patientIDET.getText().toString();

        int genderID = genderRB.getCheckedRadioButtonId();

        if(service_running)
        {
            Toast.makeText(this,"Service already running!!!",Toast.LENGTH_LONG).show();
            return;
        }

        if(patientName.length() == 0 || age.length() == 0 ||
                patientID.length() == 0 || genderID == -1)
        {
            Toast.makeText(this,"Enter all the values!",Toast.LENGTH_LONG).show();
            return;
        }

        patientIDET.clearFocus();
        ageET.clearFocus();
        patientNameET.clearFocus();
        String gender = ((RadioButton) findViewById(genderID)).getText().toString();
        if("M".equals(gender))
            gender = "Male";
        else
            gender = "Female";

        tableName = patientName + "_" + patientID + "_" + age + "_" + gender;

        acceaderDbHelper = AccelerometerReaderDbHelper.getInstance(MainActivity.this);
        acceaderDbHelper.createDatabase();
        acceaderDbHelper.createTable(tableName);

        Intent intent = new Intent(this,AccelerometerService.class);
        intent.putExtra("tableName",tableName);
        startService(intent);

        if(myThread.isAlive())
        {
            synchronized (thread)
            {
//                populateNewValues(false);
                isPaused = false;
                thread.notifyAll();
            }
        }
        else
        {
//            populateNewValues(false);
            isPaused = false;
            myThread.start();
        }
    }

    public void populateNewValues(boolean isZeroes)
    {
        if(isZeroes)
        {
            for (int i = 0; i < 10; i++)
            {
                x_values[i] = 0;
                y_values[i] = 0;
                z_values[i] = 0;
            }
            return;
        }
        Cursor cur = null;
        try
        {
            cur = acceaderDbHelper.getLast10data(tableName);
        }
        catch (Exception e)
        {
            Log.d("MyAPP1",e.toString());
        }
        if(null != cur)
        {
            updateValues(cur);
        }
    }

    public void updateValues(Cursor cur)
    {
        int count = cur.getCount();

        if(count < 10)
        {
//            Log.d("MyAPP1","if Count = " + count);
            for(int i = 0; i < 10-count ; i++)
            {
                x_values[i] = 0;
                y_values[i] = 0;
                z_values[i] = 0;
            }
            if(null != cur)
            {
                if(cur.moveToFirst())
                {
                    int i = 9;
                    do
                    {
                        x_values[i] = cur.getFloat(cur.getColumnIndex("x_values"));
                        y_values[i] = cur.getFloat(cur.getColumnIndex("y_values"));
                        z_values[i] = cur.getFloat(cur.getColumnIndex("z_values"));
//                        Log.d("MyAPPV","values["+i+"]: " + x_values[i]+" : " + y_values[i]+" : " + z_values[i]);
                        i--;
                    }while(cur.moveToNext());
                }
            }
        }
        else
        {
            if(null != cur)
            {
                if(cur.moveToFirst())
                {
                    int i = 9;
                    do
                    {
                        float f = cur.getFloat(cur.getColumnIndex("x_values"));

                        x_values[i] = cur.getFloat(cur.getColumnIndex("x_values"));
                        y_values[i] = cur.getFloat(cur.getColumnIndex("y_values"));
                        z_values[i] = cur.getFloat(cur.getColumnIndex("z_values"));
//                        Log.d("MyAPPV","values["+i+"]: " + x_values[i]+" : " + y_values[i]+" : " + z_values[i]);
                        i--;
                        if(i < 0)
                            break;
                    }while(cur.moveToNext());
                }
            }
        }
    }

    public void stopGraph(View v) throws InterruptedException
    {
        stopGraphV();
    }

    public void stopGraphV()
    {
        Intent intent = new Intent(this,AccelerometerService.class);
        stopService(intent);
        if(!isPaused)
        {
            isPaused = true;
        }
    }

    public void uploadDB(View v)
    {
        if(!isPaused)
        {
            Toast.makeText(MainActivity.this,"Please stop the graph and try again!",Toast.LENGTH_SHORT).show();
            return;
        }
        if (null != nInfo && nInfo.isConnected())
        {
            uploadToServer.upload();
        }
        else
            Toast.makeText(this,"Please connect to Internet and try again!",Toast.LENGTH_LONG).show();
    }

    public void downloadDB(View v)
    {
        if(!isPaused)
        {
            Toast.makeText(MainActivity.this,"Please stop the graph and try again!",Toast.LENGTH_SHORT).show();
            return;
        }

        InputMethodManager inputManager1 = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager1.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

        String patientName = patientNameET.getText().toString();
        String age = ageET.getText().toString();
        String patientID = patientIDET.getText().toString();

        int genderID = genderRB.getCheckedRadioButtonId();

        if(patientName.length() == 0 || age.length() == 0 ||
                patientID.length() == 0 || genderID == -1)
        {
            Toast.makeText(this,"Enter all the values!",Toast.LENGTH_LONG).show();
            return;
        }

        if (null != nInfo && nInfo.isConnected())
        {
            if(uploadToServer.isUploadInProgress)
                Toast.makeText(this,"Upload in progress!!! Try again in sometime.",Toast.LENGTH_LONG).show();
            else
            {
                Toast.makeText(this,"Please wait, Download in progress!",Toast.LENGTH_LONG).show();
                uploadToServer.download("Group11.db");
                try
                {
                    Thread.sleep(3000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                plotDownloadedDB();
            }
        }
        else
            Toast.makeText(this,"Please connect to Internet and try again!",Toast.LENGTH_LONG).show();
    }

    public void plotDownloadedDB()
    {
        stopGraphV();
        SQLiteDatabase sqlitedb = null;
        Cursor cur = null;

        try{
            sqlitedb = SQLiteDatabase.openDatabase("/data/data/com.example.evpru.assignment1_graph/databases/download/Group11.db", null,SQLiteDatabase.OPEN_READONLY);
            cur = sqlitedb.rawQuery("SELECT * FROM " + tableName + " ORDER BY " + AccelerometerDBContract.AccelerometerEntry.COLUMN_NAME_TIMESTAMP +" DESC limit 10",null);
        }catch (final SQLiteException e){
            final String message = e.getMessage();
            runOnUiThread(new Runnable()
            {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this,"Table with the entered details not found!",Toast.LENGTH_LONG).show();
                }
            });
        }

        if(null != cur)
        {
            updateValues(cur);
            for(int i = 0; i<10;i++)
            {
                Log.d("MyAPPD","values["+i+"]: " + x_values[i]+" : " + y_values[i]+" : " + z_values[i]);
            }
            runOnUiThread(new Runnable()
            {
                @Override
                public void run() {
                    graphView.setValues(x_values,y_values,z_values);
                    graphView.invalidate();
                }
            });
        }
        uploadToServer.setDownloaded(false);
    }

    public static AccelerometerReaderDbHelper getAcceaderDbHelper()
    {
        return acceaderDbHelper;
    }

    public static void setAcceaderDbHelper(AccelerometerReaderDbHelper acceaderDbHelper)
    {
        MainActivity.acceaderDbHelper = acceaderDbHelper;
    }

    @Override
    public void run() {
        while(true)
        {
            if (!isPaused)
            {
                try
                {
                    Thread.sleep(500);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                populateNewValues(false);
                graphView.setValues(x_values, y_values, z_values);
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        graphView.invalidate();
                    }
                });
            }
            else
            {
                synchronized (thread)
                {

                    populateNewValues(true);
                    graphView.setValues(x_values, y_values, z_values);
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            graphView.invalidate();
                        }
                    });
                    try
                    {
                        thread.wait();
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
