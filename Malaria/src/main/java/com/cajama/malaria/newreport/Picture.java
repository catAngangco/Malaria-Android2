package com.cajama.malaria.newreport;


import android.app.Activity;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

import com.cajama.malaria.R;

public class Picture extends Activity {
    private SurfaceView preview=null;
    private SurfaceHolder previewHolder=null;
    private Camera camera=null;
    private boolean inPreview=false;
    private boolean cameraConfigured=false;
    private String path;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        preview=(SurfaceView)findViewById(R.id.preview);
        previewHolder=preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        Intent intent = getIntent();
        path = ((Uri) intent.getParcelableExtra(android.provider.MediaStore.EXTRA_OUTPUT)).getPath();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            Camera.CameraInfo info=new Camera.CameraInfo();

            for (int i=0; i < Camera.getNumberOfCameras(); i++) {
                Camera.getCameraInfo(i, info);

                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    camera=Camera.open();
                }
            }
        }

        if (camera == null) {
            camera=Camera.open();
        }

        setCameraDisplayOrientation(this, 0, camera);

        Camera.Parameters parameters=camera.getParameters();
        Camera.Size pictureSize= getBiggestPictureSize(parameters);

        parameters.setPictureSize(pictureSize.width,
                pictureSize.height);
        parameters.setJpegQuality(100);
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        camera.setParameters(parameters);

        startPreview();
    }

    @Override
    public void onPause() {
        if (inPreview) {
            camera.stopPreview();
        }

        camera.release();
        camera=null;
        inPreview=false;

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.picture, menu);

        return(super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.camera) {
            if (inPreview) {
            	inPreview=false;
                camera.autoFocus(new Camera.AutoFocusCallback(){
                    @Override
                    public void onAutoFocus(boolean arg0, Camera arg1) {
                        camera.takePicture(null, null, photoCallback);
                    }
                });
            }
        }

        return(super.onOptionsItemSelected(item));
    }

    private Camera.Size getBestPreviewSize(int width, int height,
                                           Camera.Parameters parameters) {
        Camera.Size result=null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result=size;
                }
                else {
                    int resultArea=result.width * result.height;
                    int newArea=size.width * size.height;

                    if (newArea > resultArea) {
                        result=size;
                    }
                }
            }
        }

        return(result);
    }

    private Camera.Size getBiggestPictureSize(Camera.Parameters parameters) {
        Camera.Size result=null;

        for (Camera.Size size : parameters.getSupportedPictureSizes()) {
            if (result == null) {
                result=size;
            }
            else {
                int resultArea=result.width * result.height;
                int newArea=size.width * size.height;

                if (newArea > resultArea) {
                    result=size;
                }
            }
        }

        return(result);
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        Camera.Parameters parameters=camera.getParameters();
        parameters.setRotation(result);
        camera.setParameters(parameters);
        camera.setDisplayOrientation(result);
    }

    private void initPreview(int width, int height) {
        if (camera != null && previewHolder.getSurface() != null) {
            try {
                camera.setPreviewDisplay(previewHolder);
                setCameraDisplayOrientation(this, 0, camera);
            }
            catch (Throwable t) {
                Log.e("PreviewDemo-surfaceCallback",
                        "Exception in setPreviewDisplay()", t);
                Toast.makeText(Picture.this, t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }


            Camera.Parameters parameters=camera.getParameters();
            Camera.Size size=getBestPreviewSize(width, height, parameters);
            Camera.Size pictureSize= getBiggestPictureSize(parameters);

            if (size != null && pictureSize != null) {
                parameters.setPreviewSize(size.width, size.height);
                parameters.setPictureSize(pictureSize.width,
                        pictureSize.height);
                parameters.setJpegQuality(100);
                parameters.setPictureFormat(ImageFormat.JPEG);
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                camera.setParameters(parameters);
                cameraConfigured=true;
            }

        }
    }

    private void startPreview() {
        if (cameraConfigured && camera != null) {
            setCameraDisplayOrientation(this, 0, camera);
            camera.startPreview();
            inPreview=true;
        }
    }

    SurfaceHolder.Callback surfaceCallback=new SurfaceHolder.Callback() {
        public void surfaceCreated(SurfaceHolder holder) {
            // no-op -- wait until surfaceChanged()
        }

        public void surfaceChanged(SurfaceHolder holder, int format,
                                   int width, int height) {
            initPreview(width, height);
            startPreview();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // no-op
        }
    };

    Camera.PictureCallback photoCallback=new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            new SavePhotoTask().execute(data);
            //camera.startPreview();
            //inPreview=true;

            Intent resultIntent = new Intent(getApplicationContext(), NewReportActivity.class);
            File imageFile = new File(path);
            Uri fileUri = Uri.fromFile(imageFile);
            resultIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, fileUri);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    };

    class SavePhotoTask extends AsyncTask<byte[], String, String> {
        @Override
        protected String doInBackground(byte[]... jpeg) {
            File photo= new File(path);

            if (photo.exists()) {
                photo.delete();
            }

            try {
                FileOutputStream fos=new FileOutputStream(photo.getPath());

                fos.write(jpeg[0]);
                fos.close();
            }
            catch (java.io.IOException e) {
                Log.e("PictureDemo", "Exception in photoCallback", e);
            }

            return(null);
        }
    }
}