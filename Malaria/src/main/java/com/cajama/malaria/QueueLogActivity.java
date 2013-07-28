package com.cajama.malaria;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class QueueLogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queuelog);
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
    
}
