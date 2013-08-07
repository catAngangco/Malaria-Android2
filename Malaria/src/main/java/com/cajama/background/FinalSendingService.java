package com.cajama.background;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.FileObserver;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.cajama.malaria.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Jasper on 8/4/13.
 */
public class FinalSendingService extends Service {
    final String TAG = "FinalSendingService";
    SendFileAsyncTask asyncTask;
    File sentList, reportsDirectory;

    SendFileAsyncTask.OnAsyncResult onAsyncResult = new SendFileAsyncTask.OnAsyncResult() {
        @Override
        public void onResult(int resultCode, String message) {
            try {
                append_report(resultCode, message);
            } catch (Exception e) {
                Log.d(TAG, "error!");
                e.printStackTrace();
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        asyncTask = new SendFileAsyncTask(getString(R.string.server_send_address));
        asyncTask.setOnResultListener(onAsyncResult);

        reportsDirectory = new File(String.valueOf(Environment.getExternalStorageDirectory()) + "/Android/data/com.cajama.malaria/files/ZipFiles");
        FileObserver fileObserver = new FileObserver(reportsDirectory.getAbsolutePath()) {
            @Override
            public void onEvent(int i, String s) {

            }
        };

        sentList = new File(String.valueOf(Environment.getExternalStorageDirectory()) + "/Android/data/com.cajama.malaria/files/sent_log.txt");
        if (!sentList.exists()) {
            try {
                sentList.createNewFile();
            } catch (IOException e) {
                Log.d(TAG, "Failed to create sentList.txt!");
                e.printStackTrace();
            }
        }

        Log.d(TAG, "onCreate()");
        Toast.makeText(getApplicationContext(), "Sending reports...", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        if (reportsDirectory.exists()) {
            File[] reports = reportsDirectory.listFiles();
            asyncTask.execute(reports);
        }

        return START_FLAG_REDELIVERY;
    }

    public void append_report(int resultCode, String message) throws IOException {
        if (resultCode == 1) {
            FileWriter fileWriter = new FileWriter(sentList, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("\n"+message);
            bufferedWriter.close();
            Log.d(TAG, message + " added to sent list");
        }
        else {
            Log.d(TAG, message + " not added to sent list");
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        Toast.makeText(getApplicationContext(), "All reports sent!", Toast.LENGTH_LONG).show();
    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo activeWifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return activeNetInfo != null && (activeNetInfo.isConnectedOrConnecting() || activeWifiInfo.isConnectedOrConnecting());
    }
}
