package com.example.tejakunderu;

/*
 * All the code in this file was written by me for the purpose of the assignment
 */

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static final String NAME = "com.example.tejakunderu.NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etPatientId = (EditText)findViewById(R.id.patient_id_edit_text);
        etPatientAge = (EditText)findViewById(R.id.patient_age_edit_text);
        etPatientName = (EditText)findViewById(R.id.patient_name_edit_text);
        rbGenderMale = (RadioButton) findViewById(R.id.patient_gender_button_male);
        rbGenderFemale = (RadioButton) findViewById(R.id.patient_gender_button_female);
        tvErrorMessage = (TextView)findViewById(R.id.error_message_text);
    }

    /**
     * Executes on click of the next page button
     */
    public void nextPageAction(View view) {
        new checkEntries().execute();
    }

    /**
     * Checks for validity of the entries and creates an intent if valid
     */
    public class checkEntries extends AsyncTask<Void, Void, Void> {
        String id = etPatientId.getText().toString();
        String age = etPatientAge.getText().toString();
        String name = etPatientName.getText().toString();
        boolean error;
        int num = 0;

        @Override
        protected void onPreExecute() {
            error = !(rbGenderMale.isChecked() || rbGenderFemale.isChecked());
        }

        @Override
        protected Void doInBackground(Void... params) {
            if(id.equals("") || name.equals("") || age.equals(""))
                error = true;

            try{
                num = Integer.parseInt(age);
                if(num < 0 || num > 150)
                    error = true;
            } catch(NumberFormatException e) {
                error = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
           if(!error) {
               tvErrorMessage.setVisibility(View.INVISIBLE);
               Intent nextPage = new Intent(MainActivity.this, GraphActivity.class);
               nextPage.putExtra(NAME, name);
               startActivity(nextPage);
           } else tvErrorMessage.setVisibility(View.VISIBLE);
        }
    }

    private EditText etPatientId;
    private EditText etPatientAge;
    private EditText etPatientName;
    private TextView tvErrorMessage;
    private RadioButton rbGenderMale;
    private RadioButton rbGenderFemale;

}
