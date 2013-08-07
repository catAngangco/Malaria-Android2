package com.cajama.background;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by Jasper on 7/26/13.
 */
public class SendingService extends Service {
    String TAG = "SendingService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public boolean isConnectionAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo activeWifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return activeNetInfo != null && (activeNetInfo.isConnectedOrConnecting() || activeWifiInfo.isConnectedOrConnecting());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // start sending compressed data to server (need server API and files)

        /*while (true) {
            if (!isConnectionAvailable()) {
                try {
                    Log.d(TAG, "sleep for 3 secs");
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else {
                Log.d(TAG, "internet available!");

                *//*File file = new File(Environment.getExternalStorageDirectory()+"/download/", String.valueOf(R.string.apk_filename));

                if (file.exists())
                    Toast.makeText(getApplicationContext(), "Updated apk file exists!", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(), "Updated apk does not exist!", Toast.LENGTH_LONG).show();*//*


            }
        }*/

        String url = "http://befreicafsan.pythonanywhere.com/api/apk/";
        //File file = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.cajama.malaria/files/ZipFiles/");


        new AsyncPostStringTask(url).execute("rawrrawr");


        /*File file1 = new File(Environment.getExternalStorageDirectory()+"/download/", "Justice+League+-EFD.apk");

        if (file1.exists()) {
            Toast.makeText(getApplicationContext(), "Merong file!", Toast.LENGTH_LONG).show();
        }
        else Toast.makeText(getApplicationContext(), "Error: Walang file!", Toast.LENGTH_LONG).show();*/


        // for posting files
        //new AsyncHttpPostTask(url).execute(file1);
        // for posting strings
        //new AsyncPostStringTask(url).execute("Asdadasd");
        //new AsyncHttpPostTask(url).execute(file);

        /*try {
            HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
            c.setRequestMethod("POST");
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        /*Toast.makeText(getApplicationContext(), "starting send", Toast.LENGTH_LONG).show();
        try {
            HttpClient httpclient = new DefaultHttpClient();

            HttpPost httppost = new HttpPost(url);

            InputStreamEntity reqEntity = new InputStreamEntity(
                    new FileInputStream(file), -1);
            reqEntity.setContentType("binary/octet-stream");
            reqEntity.setChunked(false); // Send in multiple parts if needed
            httppost.setEntity(reqEntity);
            HttpResponse response = httpclient.execute(httppost);
            //Do something with response...
            Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
            Log.d(TAG, response.toString());

        } catch (Exception e) {
            // show error
        }*/

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy().");
        /*NotificationHelper nh = new NotificationHelper(getApplicationContext());
        nh.createNotification(2);*/
    }

    public class AsyncPostStringTask extends AsyncTask<String, Void, String> {
        String url;

        public AsyncPostStringTask(String url) {
            this.url = url;
        }

        /*public String getPage(URL url) throws IOException {
            final URLConnection connection = url.openConnection();
            HttpPost httpRequest = null;

            try {
                *//**//*httpRequest = new HttpPost(url.toURI());
                httpRequest.addHeader("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundaryNUgWMoat5gpFpnnc");

                MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                mpEntity.addPart("name", new StringBody(file.getName().substring(file.getName().lastIndexOf(".")+1)));
                mpEntity.addPart("fileData", new FileBody(file));
                httpRequest.setEntity(mpEntity);*//**//*
                httpRequest = new HttpPost(url.toURI());

                MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, "----WebKitFormBoundaryNUgWMoat5gpFpnnc", Charset.forName("UTF-8"));
                FileBody fb = new FileBody(file, "application/octet-stream");
                mpEntity.addPart("file", fb);
                httpRequest.addHeader("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundaryNUgWMoat5gpFpnnc");
                httpRequest.setHeader("Content-Disposition", "form-data");

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);

            *//**//*HttpEntity entity = response.getEntity();
            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
            InputStream stream = bufHttpEntity.getContent();

            String ct = connection.getContentType();

            final BufferedReader reader;

            if (ct.indexOf("charset=") != -1) {
                ct = ct.substring(ct.indexOf("charset=") + 8);
                reader = new BufferedReader(new InputStreamReader(stream, ct));
            }else {
                reader = new BufferedReader(new InputStreamReader(stream));
            }

            final StringBuilder sb = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            stream.close();
            return sb.toString();*//**//*
            return "asdf";
        }*/

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

                HttpClient httpclient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();

//  HTTP parameters stores header etc.
                HttpParams params = new BasicHttpParams();
                params.setParameter("http.protocol.handle-redirects",false);

//  Create a local instance of cookie store
                CookieStore cookieStore = new BasicCookieStore();

//  Bind custom cookie store to the local context
                localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

//  connect and receive
                HttpPost httpPost = new HttpPost(url);
                httpPost.setParams(params);
                httpPost.setEntity(mp);
                response = httpclient.execute(httpPost, localContext);

//  obtain redirect target
                String redirectLocation = "";
                Header locationHeader = response.getFirstHeader("location");
                if (locationHeader != null) {
                    redirectLocation = locationHeader.getValue();
                    System.out.println("loaction: " + redirectLocation);
                    Log.d(TAG, "yun oh!");
                } else {
                    Log.d(TAG, "lolers!");
                    // The response is invalid and did not provide the new location for
                    // the resource.  Report an error or possibly handle the response
                    // like a 404 Not Found error.
                }


                HttpClient httpclient2 = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(redirectLocation);
                HttpResponse getResponse = httpclient2.execute(httpGet);

                InputStream is = getResponse.getEntity().getContent();

                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "pdf.pdf");
                file.createNewFile();

                FileOutputStream fos = new FileOutputStream(file);

                byte[] buffer = new byte[1024];
                int len1 = 0;
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);
                }
                fos.close();
                is.close();
                Log.d(TAG, "rawrwarwr");

                /*BufferedReader getReader = new BufferedReader(new InputStreamReader(getResponse.getEntity().getContent()));
                final StringBuilder getResultBuilder = new StringBuilder();
                String getResult;
                try {
                    while ((getResult = getReader.readLine()) != null) {
                        getResultBuilder.append(getResult);
                    }
                } catch (Exception e) {
                    Log.d(TAG, "error in reading get result");
                    e.printStackTrace();
                }

                try {
                    getReader.close();
                } catch (Exception e) {
                    Log.e(TAG, "error in closing getReader");
                    e.printStackTrace();
                }*/



                /*if (response.getStatusLine().getStatusCode() == 200) {

                    // no auto-redirecting at client side, need manual send the request.
                    HttpGet request2 = new HttpGet(url);
                    HttpResponse response2 = client.execute(request2);

                    BufferedReader rd = new BufferedReader(new InputStreamReader(response2.getEntity().getContent()));
                    final StringBuilder out = new StringBuilder();
                    String line;
                    try {
                        while ((line = rd.readLine()) != null) {
                            out.append(line);
                        }
                    }
                    catch (Exception e) {

                    }
                    //wr.close();
                    try {
                        rd.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    //final String serverResponse = slurp(is);
                    Log.d(TAG, "serverResponse2: " + out.toString());
                }*/

                /*BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                final StringBuilder out = new StringBuilder();
                String line;
                try {
                    while ((line = rd.readLine()) != null) {
                        out.append(line);
                    }
                }
                catch (Exception e) {

                }
                //wr.close();
                try {
                    rd.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //final String serverResponse = slurp(is);
                Log.d(TAG, "serverResponse: " + out.toString());*/




            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // TODO Auto-generated method stub
            return null;
        }
    }

    public class AsyncHttpPostTask extends AsyncTask<File, Void, String> {
        private final String TAG = AsyncHttpPostTask.class.getSimpleName();
        private String server;


        public AsyncHttpPostTask(final String server) {
            this.server = server;
        }

        @Override
        protected String doInBackground(File... params) {
            Log.d(TAG, "doInBackground");
            HttpClient http = AndroidHttpClient.newInstance("MyApp");
            HttpPost method = new HttpPost(this.server);

            try {
                MultipartEntity mp = new MultipartEntity();
                ContentBody cbFile = new FileBody(params[0], "text/plain");
                ContentBody cbFilename = new StringBody(params[0].getName());
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




            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // TODO Auto-generated method stub
            return null;
        }

    }
}
