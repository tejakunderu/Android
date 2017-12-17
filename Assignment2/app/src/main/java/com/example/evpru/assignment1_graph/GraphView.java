package com.example.evpru.assignment1_graph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.view.View;

import java.util.Random;

/**
 * Created by evpru on 1/18/2017.
 */

public class GraphView extends View {

    public static boolean BAR = false;
    public static boolean LINE = true;

    private Paint paint_x;
    private Paint paint_y;
    private Paint paint_z;
    private float[] values_x;
    private float[] values_y;
    private float[] values_z;
    private String[] horlabels;
    private String[] verlabels;
    private String title = "";
    private boolean type = false;
    private Random rand = new Random();

    public GraphView(Context context, float[] values_x,float[] values_y,float[] values_z, String title, String[] horlabels, String[] verlabels, boolean type) {
        super(context);
        if (values_x == null)
            values_x = new float[0];
        else
            this.values_x = values_x;
        if (values_y == null)
            values_y = new float[0];
        else
            this.values_y = values_y;
        if (values_z == null)
            values_z = new float[0];
        else
            this.values_z = values_z;
        if (title == null)
            title = "";
        else
            this.title = title;
        if (horlabels == null)
            this.horlabels = new String[0];
        else
            this.horlabels = horlabels;
        if (verlabels == null)
            this.verlabels = new String[0];
        else
            this.verlabels = verlabels;
        this.type = type;
        paint_x = new Paint();
    }

    public void setValues(float[] newValues_x,float[] newValues_y, float[] newValues_z)
    {
        this.values_x = newValues_x;
        this.values_y = newValues_y;
        this.values_z = newValues_z;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float border = 20;
        float horstart = border * 2;
        float height = getHeight();
        float width = getWidth() - 1;
        float max_x = getMax(values_x);
        float max_y = getMax(values_y);
        float max_z = getMax(values_z);
        float min_x = getMin(values_x);
        float min_y = getMin(values_y);
        float min_z = getMin(values_z);
        float diff_x = max_x - min_x;
        float diff_y = max_y - min_y;
        float diff_z = max_z - min_z;
        float graphheight = height - (2 * border);
        float graphwidth = width - (2 * border);


        paint_x.setTextSize(50);
        paint_x.setTextAlign(Paint.Align.LEFT);
        int vers = verlabels.length - 1;
        for (int i = 0; i < verlabels.length; i++) {
            paint_x.setColor(Color.DKGRAY);
            float y = ((graphheight / vers) * i) + border;
            canvas.drawLine(horstart, y, width, y, paint_x);
            paint_x.setColor(Color.WHITE);
            canvas.drawText(verlabels[i], 0, y, paint_x);
        }
        int hors = horlabels.length - 1;
        for (int i = 0; i < horlabels.length; i++) {
            paint_x.setColor(Color.DKGRAY);
            float x = ((graphwidth / hors) * i) + horstart;
            canvas.drawLine(x, height - border, x, border, paint_x);
            paint_x.setTextAlign(Align.CENTER);
            if (i==horlabels.length-1)
                paint_x.setTextAlign(Align.RIGHT);
            if (i==0)
                paint_x.setTextAlign(Align.LEFT);
            paint_x.setColor(Color.WHITE);
            canvas.drawText(horlabels[i], x, height - 4, paint_x);
        }

        paint_x.setTextAlign(Align.CENTER);
        canvas.drawText(title, (graphwidth / 2) + horstart, border - 4, paint_x);

        this.paint_y = paint_x;
        this.paint_z = paint_x;
        if (max_x != min_x) {
            paint_x.setColor(Color.LTGRAY);
            if (type == BAR) {
                float datalength = values_x.length;
                float colwidth = (width - (2 * border)) / datalength;
                for (int i = 0; i < values_x.length; i++) {
                    float val = values_x[i] - min_x;
                    float rat = val / diff_x;
                    float h = graphheight * rat;
                    canvas.drawRect((i * colwidth) + horstart, (border - h) + graphheight, ((i * colwidth) + horstart) + (colwidth - 1), height - (border - 1), paint_x);
                }
            } else {
                float datalength = values_x.length;
                float colwidth = (width - (2 * border)) / datalength;
                float halfcol = colwidth / 2;
                float lasth = 0;
                for (int i = 0; i < values_x.length; i++) {
                    float val = values_x[i] - min_x;
                    float rat = val / diff_x;
                    float h = graphheight * rat;
                    if (i >= 0)
                        paint_x.setColor(Color.GREEN);
                    paint_x.setStrokeWidth(2.0f);
                    canvas.drawLine(((i - 1) * colwidth) + (horstart + 1) + halfcol, (border - lasth) + graphheight, (i * colwidth) + (horstart + 1) + halfcol, (border - h) + graphheight, paint_x);
                    // paint_x.setColor(Color.RED);
                    //canvas.drawLine(((i - 1) * colwidth) + (horstart + 1) + halfcol +50, (border - lasth) + graphheight + 50, (i * colwidth) + (horstart + 1) + halfcol + 50, (border - h) + graphheight + 50, paint_x);


                    lasth = h;
                }
            }
        }
        if (max_y != min_y) {
            paint_y.setColor(Color.LTGRAY);
            if (type == BAR) {
                float datalength = values_y.length;
                float colwidth = (width - (2 * border)) / datalength;
                for (int i = 0; i < values_y.length; i++) {
                    float val = values_y[i] - min_y;
                    float rat = val / diff_y;
                    float h = graphheight * rat;
                    canvas.drawRect((i * colwidth) + horstart, (border - h) + graphheight, ((i * colwidth) + horstart) + (colwidth - 1), height - (border - 1), paint_y);
                }
            } else {
                float datalength = values_y.length;
                float colwidth = (width - (2 * border)) / datalength;
                float halfcol = colwidth / 2;
                float lasth = 0;
                for (int i = 0; i < values_y.length; i++) {
                    float val = values_y[i] - min_y;
                    float rat = val / diff_y;
                    float h = graphheight * rat;
                    if (i >= 0)
                        paint_x.setColor(Color.RED);
                    paint_x.setStrokeWidth(2.0f);
                    canvas.drawLine(((i - 1) * colwidth) + (horstart + 1) + halfcol, (border - lasth) + graphheight, (i * colwidth) + (horstart + 1) + halfcol, (border - h) + graphheight, paint_x);
                    // paint_x.setColor(Color.RED);
                    //canvas.drawLine(((i - 1) * colwidth) + (horstart + 1) + halfcol +50, (border - lasth) + graphheight + 50, (i * colwidth) + (horstart + 1) + halfcol + 50, (border - h) + graphheight + 50, paint_x);


                    lasth = h;
                }
            }
        }
        if (max_z != min_z) {
            paint_x.setColor(Color.LTGRAY);
            if (type == BAR) {
                float datalength = values_z.length;
                float colwidth = (width - (2 * border)) / datalength;
                for (int i = 0; i < values_z.length; i++) {
                    float val = values_z[i] - min_z;
                    float rat = val / diff_z;
                    float h = graphheight * rat;
                    canvas.drawRect((i * colwidth) + horstart, (border - h) + graphheight, ((i * colwidth) + horstart) + (colwidth - 1), height - (border - 1), paint_x);
                }
            } else {
                float datalength = values_z.length;
                float colwidth = (width - (2 * border)) / datalength;
                float halfcol = colwidth / 2;
                float lasth = 0;
                for (int i = 0; i < values_z.length; i++) {
                    float val = values_z[i] - min_z;
                    float rat = val / diff_z;
                    float h = graphheight * rat;
                    if (i >= 0)
                        paint_x.setColor(Color.BLUE);
                    paint_x.setStrokeWidth(2.0f);
                    canvas.drawLine(((i - 1) * colwidth) + (horstart + 1) + halfcol, (border - lasth) + graphheight, (i * colwidth) + (horstart + 1) + halfcol, (border - h) + graphheight, paint_x);
                    // paint_x.setColor(Color.RED);
                    //canvas.drawLine(((i - 1) * colwidth) + (horstart + 1) + halfcol +50, (border - lasth) + graphheight + 50, (i * colwidth) + (horstart + 1) + halfcol + 50, (border - h) + graphheight + 50, paint_x);


                    lasth = h;
                }
            }
        }

    }

    private float getMax(float[] values) {
        float largest = Integer.MIN_VALUE;
        for (int i = 0; i < values.length; i++)
            if (values[i] > largest)
                largest = values[i];

        //largest = 3000;
        return largest;
    }

    private float getMin(float[] values) {
        float smallest = Integer.MAX_VALUE;
        for (int i = 0; i < values.length; i++)
            if (values[i] < smallest)
                smallest = values[i];

        //smallest = 0;
        return smallest;
    }
}