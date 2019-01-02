package com.example.archer.amadeus;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AmadeusLogger {
    public static File logFile;

    public static void appendLog(String text, Context context) {

        String LOG_FILE_PATH = context.getString(R.string.pref_log_file_name);
        logFile = new File(context.getFilesDir(), LOG_FILE_PATH);

        if (!logFile.exists()) {

            try {
                logFile.createNewFile();

            } catch(IOException e) {

            }
        }

        try {

            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();

        } catch(IOException e) {

        }
    }
}
