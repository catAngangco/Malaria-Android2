package com.cajama.malaria.entryLogs;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by GMGA on 7/28/13.
 */
public class ReadTextFile{
    private File file;
    public ReadTextFile(File file){
        this.file = file;
    }

    public ArrayList<String> readText() throws FileNotFoundException{
        ArrayList<String> logs = new ArrayList<String>();
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line;
            Log.v("logs","enter");

            while((line = br.readLine())!= null){
                Log.v("logs",line);
                logs.add(line);
            }

            br.close();
            fis.close();
        } catch (IOException e){
            Log.w("ExternalStorage", "Error reading " + file, e);
        }
        return logs;
    }
}
