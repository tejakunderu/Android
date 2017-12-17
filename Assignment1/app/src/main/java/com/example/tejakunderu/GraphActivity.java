package com.example.tejakunderu;

/**
 * All the code in this file was written by me for the purpose of the assignment
 */

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.Random;

public class GraphActivity extends AppCompatActivity {

    //Number of data points in the graph
    private static final int NUM_VALUES = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        Intent intent = getIntent();

        values = new float[NUM_VALUES];
        createLabels();
        String title = intent.getStringExtra(MainActivity.NAME);
        boolean type = true;

        rl = (RelativeLayout)findViewById(R.id.graph_layout);
        graphView = new GraphView(GraphActivity.this, values, title, horlabels, verlabels, type);
        rl.addView(graphView);
    }

    /**
     * Executes on click of the RUN button
     * Stops existing AsyncTask first in case the RUN button is clicked multiple times
     * Initializes new Random Values and starts a new AsyncTask
     */
    public void runGraph(View view) {
        stop = true;
        initialiseNewValues();
        stop = false;
        new realTimeGraph().execute();
    }

    /**
     * Executes on click of the STOP button
     * variable stop is set to true
     * AsyncTask then moves to the onPostExecute method
     */
    public void stopGraph(View view) {
        stop = true;
    }

    /**
     * Adding additional functionality to the back button to stop the current Thread
     */
    @Override
    public void onBackPressed() {
        stop = true;
        super.onBackPressed();
    }

    /**
     * Updates values in the background
     * Refreshes the graph in the update method
     * Initializes all values to 0 and clears the view on post execute
     */
    public class realTimeGraph extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            while(!stop) {
                updateGraphValues();
                temp = GraphActivity.values;
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            rl.removeView(graphView);
            graphView.setValues(temp);
            rl.addView(graphView);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            rl.removeView(graphView);
            initialiseValues();
            graphView.setValues(values);
            rl.addView(graphView);
        }

        private float[] temp;
    }

    /**
     * Initializes all values to zero
     */
    private void initialiseValues() {
        for(int i = 0; i < values.length; i++)
            values[i] = 0;
    }

    /**
     * Initializes the array with new Random values
     */
    private void initialiseNewValues() {
        Random rand = new Random();
        for(int i = 0; i < values.length; i++)
            values[i] = rand.nextFloat() * 2000;
    }

    /**
     * Values in the array are cycled around when the method is called
     */
    private void updateGraphValues() {
        int length = values.length;
        float temp1 = values[0];
        for(int i = 0; i < length - 1; i++) {
            float temp2 = values[i + 1];
            values[i] = temp2;
        }
        values[length - 1] = temp1;
    }

    /**
     * Creates horizontal and vertical labels on the graph
     */
    private void createLabels() {
        horlabels = new String[9];
        horlabels[0] = "" + 2700;
        horlabels[1] = "" + 2750;
        horlabels[2] = "" + 2800;
        horlabels[3] = "" + 2850;
        horlabels[4] = "" + 2900;
        horlabels[5] = "" + 2950;
        horlabels[6] = "" + 3000;
        horlabels[7] = "" + 3050;
        horlabels[8] = "" + 3100;


        verlabels = new String[4];
        verlabels[0] = "" + 500;
        verlabels[1] = "" + 1000;
        verlabels[2] = "" + 1500;
        verlabels[3] = "" + 2000;
    }

    private static float[] values;
    private static boolean stop;

    private GraphView graphView;
    private RelativeLayout rl;
    private String[] horlabels;
    private String[] verlabels;
}