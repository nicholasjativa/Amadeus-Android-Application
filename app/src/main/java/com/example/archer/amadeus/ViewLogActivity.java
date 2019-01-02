package com.example.archer.amadeus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ViewLogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_log);

        String LOG_FILE_PATH = getString(R.string.pref_log_file_name);
        FileInputStream stream;
        String line;
        String entireFile = "";

        try {

            stream = openFileInput(LOG_FILE_PATH);
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));


            line = br.readLine();
            while (line != null) {

                entireFile = entireFile + (line + "\n");
                line = br.readLine();
            }

        } catch(IOException e) {
            e.printStackTrace();
        }

        TextView tvLog = (TextView) findViewById(R.id.textview_log);
        tvLog.setText(entireFile);

    }
}
