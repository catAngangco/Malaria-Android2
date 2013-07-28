package com.cajama.malaria.newreport;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by GMGA on 7/28/13.
 */
public class MakeTextFile {
    private File file;
    private ArrayList<String> contentArray;

    public MakeTextFile(File file, ArrayList<String> contentArray){
        this.file = file;
        this.contentArray = contentArray;
    }

    private String combineData(){
        String content="";
        for(String data : contentArray){
            content+= data + "\n";
            Log.v("WRITE","CONTENT: " + data);
        }
        return content;
    }

    public void writeTextFile(){
        Log.v("write","text");
        try {
            OutputStream os = new FileOutputStream(file);
            String metaData = "";

            os.write(combineData().getBytes());
            os.close();
        } catch (IOException e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            Log.w("ExternalStorage", "Error writing " + file, e);
        }
    }
}
