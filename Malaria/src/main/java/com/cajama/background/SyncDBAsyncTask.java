package com.cajama.background;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import com.cajama.malaria.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by Jasper on 8/8/13.
 */
public class SyncDBAsyncTask extends AsyncTask<String, Void, String> {
    String url;
    String TAG = "SyncDBAsyncTask";
    OnAsyncResult onAsyncResult;
    
    public SyncDBAsyncTask(String url) {
    	this.url = url;
    }

    public void setOnResultListener(OnAsyncResult onAsyncResult) {
        if (onAsyncResult != null) this.onAsyncResult = onAsyncResult;
    }

    @Override
    protected String doInBackground(String... strings) {
    	HttpPost post = null;
        HttpClient client = null;
        MultipartEntity mp = null;
        try {
            client = new DefaultHttpClient();
            post = new HttpPost(url);

            mp = new MultipartEntity();
            ContentBody stringBody = new StringBody(strings[0]);
            mp.addPart("message", stringBody);
            post.setEntity(mp);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, String.valueOf(post.getRequestLine()));

        try {
        	HttpResponse response = client.execute(post);
        	Log.d(TAG, "response: "+ response.getStatusLine());

            InputStream is = response.getEntity().getContent();//getResponse.getEntity().getContent();

            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();

            String line;
            try {

                br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (sb.toString().equals("ok")) {
            	onAsyncResult.onResult(1, sb.toString());
            }
            else onAsyncResult.onResult(0, "fail");

            Log.d(TAG, sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "error!");
            onAsyncResult.onResult(0, "failed");
        }

        // TODO Auto-generated method stub
        return null;
    }

    public interface OnAsyncResult {
        public abstract void onResult(int resultCode, String message);
    }
}