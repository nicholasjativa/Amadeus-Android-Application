package com.example.archer.amadeus;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AmadeusLogger {
    public static File logFile;
    private static SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

    public static void appendLog(String text, Context context) {

        String LOG_FILE_PATH = context.getString(R.string.pref_log_file_name);
        logFile = new File(context.getFilesDir(), LOG_FILE_PATH);
        String timestamp = formatter.format(new Date());

        if (!logFile.exists()) {

            try {
                logFile.createNewFile();

            } catch(IOException e) {

            }
        }

        try {

            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(timestamp);
            buf.newLine();
            buf.append(text);
            buf.newLine();
            buf.close();

        } catch(IOException e) {

        }
    }

    public static void clearLog(Context context) {

        String LOG_FILE_PATH = context.getString(R.string.pref_log_file_name);
        logFile = new File(context.getFilesDir(), LOG_FILE_PATH);

        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, false));
            buf.write("");
            buf.close();
        } catch(IOException e) {

        }
    }
}
