package com.cajama.malaria.entryLogs;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import com.cajama.malaria.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

public class QueueLogActivity extends ListActivity {
    private ArrayList<HashMap> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queuelog);

        File queueLog = new File (getExternalFilesDir("queueLog"), "queueLog.txt");
        ArrayList<String> logs;
        ReadTextFile rtf = new ReadTextFile(queueLog);

        try{
            logs=rtf.readText();
            ArrayList<HashMap> logSet = new ArrayList<HashMap>();
            logSet=getLogSet(logs, logSet);

            ListView lview = (ListView) findViewById(android.R.id.list);
            entryAdapter adapter = new entryAdapter(this, logSet);
            lview.setAdapter(adapter);
        }
        catch (FileNotFoundException e){
            Log.w("ExternalStorage", "Error readingQueue " + queueLog, e);
        }
    }

    private ArrayList<HashMap> getLogSet(ArrayList<String> logs, ArrayList<HashMap> logSet){
        for(int i=0;i<logs.size();i=i+3){
            HashMap map = new HashMap();
            map.put("date", logs.get(i));
            map.put("time", logs.get(i+1));
            map.put("name", logs.get(i+2));
            logSet.add(map);
        }
        return logSet;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.queue_log, menu);
        return true;
    }

    public void goHome(View view) {
        finish();
    }
}
