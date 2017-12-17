package com.example.evpru.assignment1_graph;

import android.provider.BaseColumns;

/**
 * Created by evpru on 2/26/2017.
 */

public class AccelerometerDBContract
{
    public static final String DB_NAME = "Group11";
    private AccelerometerDBContract(){}

    public static class AccelerometerEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "default_table";
        public static final String COLUMN_NAME_TIMESTAMP = "time_stamp";
        public static final String COLUMN_NAME_XVALUES = "x_values";
        public static final String COLUMN_NAME_YVALUES = "y_values";
        public static final String COLUMN_NAME_ZVALUES = "z_values";
    }
}
