package com.cajama.background;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import com.cajama.malaria.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by Jasper on 8/4/13.
 */
public class SendFileAsyncTask extends AsyncTask<File, Void, String> {
    OnAsyncResult onAsyncResult;
    private final String TAG = SendFileAsyncTask.class.getSimpleName();
    private String server;

    public SendFileAsyncTask(final String server) {
        this.server = server;
    }

    public void setOnResultListener(OnAsyncResult onAsyncResult) {
        if (onAsyncResult != null) this.onAsyncResult = onAsyncResult;
    }

    @Override
    protected String doInBackground(File... params) {
        for (File currentFile : params) {
            //File currentFile = params[0];
            Log.d(TAG, currentFile.getAbsolutePath());
            if (onAsyncResult != null) {
                Log.d(TAG, "doInBackground()");
                AndroidHttpClient http = AndroidHttpClient.newInstance("MyApp"+currentFile.getName());
                HttpPost method = new HttpPost(this.server);

                try {
                    MultipartEntity mp = new MultipartEntity();
                    ContentBody cbFile = new FileBody(currentFile, "text/plain");
                    ContentBody cbFilename = new StringBody(currentFile.getName());
                    ContentBody cbName = new StringBody("file");
                    mp.addPart("name", cbName);
                    mp.addPart("filename", cbFilename);
                    mp.addPart("file", cbFile);
                    method.setEntity(mp);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                //method.setEntity(new FileEntity(params[0], "text/plain"));

                Log.d(TAG, String.valueOf(method.getRequestLine()));

                try {
                    HttpResponse response = http.execute(method);

                    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    final StringBuilder out = new StringBuilder();
                    String line;
                    try {
                        while ((line = rd.readLine()) != null) {
                            out.append(line);
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    //wr.close();
                    try {
                        rd.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    //final String serverResponse = slurp(is);
                    Log.d(TAG, "serverResponse: " + out.toString());

                    if (out.toString().equals("Case recorded.")) {
                    	currentFile.delete();
                    	onAsyncResult.onResult(1, currentFile.getName());
                    }
                    else onAsyncResult.onResult(0, "failed");
                    method.getEntity().consumeContent();
                    http.getConnectionManager().shutdown();
                    http.close();
                    method.abort();

                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG, "done!");
    }

    public interface OnAsyncResult {
        public abstract void onResult(int resultCode, String message);
    }
}