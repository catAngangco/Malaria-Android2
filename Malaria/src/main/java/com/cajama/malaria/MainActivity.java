package com.cajama.malaria;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.cajama.malaria.entryLogs.QueueLogActivity;
import com.cajama.malaria.entryLogs.SentLogActivity;
import com.cajama.malaria.newreport.NewReportActivity;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void submitNewReport(View view) {
        Intent intent = new Intent(this, NewReportActivity.class);
        startActivity(intent);
    }

    public void viewQueueLog(View view) {
        Intent intent = new Intent(this, QueueLogActivity.class);
        startActivity(intent);
    }

    public void viewSentLog(View view) {
        Intent intent = new Intent(this, SentLogActivity.class);
        startActivity(intent);
    }

}