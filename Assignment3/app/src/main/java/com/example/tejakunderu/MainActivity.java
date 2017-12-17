package com.example.tejakunderu;

import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.*;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static SQLiteDatabase db;
    private Button recordButton;
    private EditText activityType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activityType = (EditText)findViewById(R.id.activity_type);
        recordButton = (Button)findViewById(R.id.record_button);

        try {
            db = SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory() + "/MCDatabase.db", null);

            try {
                String createDatabaseString = "CREATE TABLE IF NOT EXISTS "
                        + "AccelerometerData"
                        + "(ID INTEGER PRIMARY KEY autoincrement, ";
                for(int i = 0; i < 50; i++)
                    createDatabaseString += "AccelX" + i + " FLOAT, "
                            + "AccelY" + i + " FLOAT, "
                            + "AccelZ" + i + " FLOAT, ";
                createDatabaseString += "Label TEXT);";

                db.execSQL(createDatabaseString);
            } catch (SQLException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } catch(SQLException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void startRecording(View view) {
        Intent recordData = new Intent(this, AccelerometerClass.class);

        String activityName = activityType.getText().toString();
        Bundle b = new Bundle();
        b.putString("activity", activityName);

        recordData.putExtras(b);
        startService(recordData);
    }
}
