package com.cajama.malaria.entryLogs;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import com.cajama.malaria.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class SentLogActivity extends Activity {
    final String TAG = "SentLogActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentlog);

        File sentLog = new File(String.valueOf(getExternalFilesDir(null)) + "/sent_log.txt");
        Log.d(TAG, sentLog.getPath());
        if (!sentLog.exists()) {
            Log.d(TAG, "no sentlog file");
            try {
                sentLog.createNewFile();
            } catch (IOException e) {
                Log.d(TAG, "error in creating sentLog");
                e.printStackTrace();
            }
        }

        ArrayList<String> logs;
        ReadTextFile rtf = new ReadTextFile(sentLog);

        try {
            logs = rtf.readText();
            ArrayList<HashMap> logSet = new ArrayList<HashMap>();
            logSet = getLogSet(logs, logSet);

            ListView lview = (ListView) findViewById(android.R.id.list);
            entryAdapter adapter = new entryAdapter(this, logSet);
            lview.setAdapter(adapter);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sent_log, menu);
        return true;
    }

    public void goHome(View view) {
        finish();
    }

    @Override
    public void onStop(){
        Log.v("stop", "STOP");
        super.onStop();
    }

    public void onDestroy() {
        super.onDestroy();
        Log.v("stop","onDESTROY");
        finish();
        Log.v("stop","finish");
    }

    public ArrayList<HashMap> getLogSet(ArrayList<String> logs, ArrayList<HashMap> logSet) {
        for(int i=0;i<logs.size();i=i+3){
            HashMap map = new HashMap();
            map.put("date", format(logs.get(i), "/"));
            map.put("time", format(logs.get(i+1), ":"));
            map.put("name", logs.get(i+2));
            logSet.add(map);
        }
        return logSet;
    }
    
    public String format(String str, String item) { // inserts / and : in date and time
    	return str.substring(0, 2) + item + str.substring(2, 4) + item + str.substring(4, str.length());
    }
}
