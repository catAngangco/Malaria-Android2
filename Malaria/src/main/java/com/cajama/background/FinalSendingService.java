package com.cajama.background;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Environment;
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
    File[] reports;
    Intent upload;

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
    	//if (asyncTask.getStatus() == Status.FINISHED || asyncTask.getStatus() == Status.PENDING) {
        asyncTask = new SendFileAsyncTask("http://10.40.93.115/api/send/");//getString(R.string.server_send_address));
        asyncTask.setOnResultListener(onAsyncResult);
    	//}
    	
    	reportsDirectory = new File(String.valueOf(Environment.getExternalStorageDirectory()) + "/Android/data/com.cajama.malaria/files/ZipFiles");
        if (!reportsDirectory.exists()) reportsDirectory.mkdir();
        reports = reportsDirectory.listFiles();

        sentList = new File(String.valueOf(Environment.getExternalStorageDirectory()) + "/Android/data/com.cajama.malaria/files/sent_log.txt");
        if (!sentList.exists()) {
            try {
                sentList.createNewFile();
                Log.d(TAG, "Created sentlist file");
            } catch (IOException e) {
                Log.d(TAG, "Failed to create sentList.txt!");
                e.printStackTrace();
            }
        }

        Log.d(TAG, "onCreate()");
        //Toast.makeText(getApplicationContext(), "Sending reports...", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isConnected()) {
            Log.d(TAG, "may internet!");
            //Toast.makeText(getApplicationContext(), "Sending reports...", Toast.LENGTH_LONG).show();
            if (reportsDirectory.exists()) {
                reports = reportsDirectory.listFiles();
                //for (File report : reports) {
                	Log.d(TAG, String.valueOf(reports.length));
                    if(asyncTask.getStatus() == AsyncTask.Status.PENDING) { 
                        asyncTask.execute(reports);
                    }
                    else if(asyncTask.getStatus() == AsyncTask.Status.RUNNING){
                        //reports = reportsDirectory.listFiles();
                        Log.d(TAG, "task alreading running: " + reports.length + " on queue");
                    	//asyncTask.execute(reports);
                    }
                    else if(asyncTask.getStatus() == AsyncTask.Status.FINISHED){
                    	//asyncTask.execute(report);
                    	//reports = reportsDirectory.listFiles();
                    	//stopSelf();
                    	//asyncTask.execute(reports);
                    	stopSelf();
                    }
                    
                //}
            }
            Toast.makeText(getApplicationContext(), "test!", Toast.LENGTH_SHORT);
            return START_FLAG_REDELIVERY;
        }
        else {
            Toast.makeText(getApplicationContext(), "No internet connection!", Toast.LENGTH_LONG).show();
            Log.d(TAG, "no internet connection");
            return START_NOT_STICKY;
        }
    }

    public void append_report(int resultCode, String message) throws IOException {
        if (resultCode == 1) {
            FileWriter fileWriter = new FileWriter(sentList, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            String[] split = message.split("_");
            bufferedWriter.write(split[0]+"\n");
            bufferedWriter.write(split[1]+"\n");
            bufferedWriter.write(split[2].substring(0, split[2].length()-4)+"\n");
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
        Toast.makeText(getApplicationContext(), "No more reports to send!", Toast.LENGTH_LONG).show();
        Intent intent1 = new Intent(getApplicationContext(), FinalSendingService.class);
    	startService(intent1);
    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        //NetworkInfo activeNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo activeWifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return activeWifiInfo != null && activeWifiInfo.isConnectedOrConnecting();
    }
}
