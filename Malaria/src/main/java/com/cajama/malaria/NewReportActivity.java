package com.cajama.malaria;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.app.SherlockActivity;
import com.cajama.android.customviews.DateDisplayPicker;
import com.cajama.android.customviews.SquareImageView;
import com.actionbarsherlock.view.MenuItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Date;

public class NewReportActivity extends SherlockActivity implements LocationListener {
    ViewFlipper VF;
    GridView new_report_photos_layout;
    ImageAdapter images;
    private static final int CAMERA_REQUEST = 1888;
    private static final int PHOTO_REQUEST = 4214;
    private String imageFilePath;
    private String[] step_subtitles;

//    final Context context = this;

    private class myBitmap {
        String path;
        Bitmap image;
    }

    private class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private Vector<myBitmap> images = new Vector<myBitmap>();

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public void AddImage(myBitmap b) {
            images.add(b);
        }

        public void remove(int pos) {
            images.remove(pos);
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public myBitmap getItem(int arg0) {
            return images.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SquareImageView img;
            if(convertView ==null) {
                img = new SquareImageView(mContext);
            }
            else {
                img = (SquareImageView)convertView;
            }

            img.setImageBitmap(images.get(position).image);
            img.setScaleType(SquareImageView.ScaleType.CENTER_CROP);
            return img;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_report);

        Spinner spinner = (Spinner) findViewById(R.id.gender_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Spinner spinner2 = (Spinner) findViewById(R.id.species_spinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.species_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);


        VF = (ViewFlipper) findViewById(R.id.viewFlipper);
        getSupportActionBar().setSubtitle("Step 1 of " + VF.getChildCount());

        images = new ImageAdapter(this);
        new_report_photos_layout = (GridView) findViewById(R.id.new_report_photos_layout);
        new_report_photos_layout.setEmptyView(findViewById(R.id.empty_list_view));
        new_report_photos_layout.setAdapter(images);

        new_report_photos_layout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), FullscreenPhotoActivity.class);

                intent.putExtra("pos", position);
                intent.putExtra("path", images.getItem(position).path);

                startActivityForResult(intent, PHOTO_REQUEST);
            }
        });

        Resources res = getResources();
        step_subtitles = new String[]{
                res.getString(R.string.patient_details),
                res.getString(R.string.slide_photos),
                res.getString(R.string.diagnosis),
                res.getString(R.string.summary),
                res.getString(R.string.submit)
        };
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        int displayedchild = VF.getDisplayedChild();
        getSupportActionBar().setSubtitle(String.format("Step %d of %d - %s", displayedchild + 1, VF.getChildCount(), step_subtitles[displayedchild]));

        switch(displayedchild) {
            case 0: menu.findItem(R.id.action_prev).setTitle(R.string.cancel);
                    menu.findItem(R.id.action_photo).setVisible(false);
                    menu.findItem(R.id.action_next).setTitle(R.string.next);
                    break;
            case 1: menu.findItem(R.id.action_prev).setTitle(R.string.back);
                    menu.findItem(R.id.action_photo).setVisible(true);
                    menu.findItem(R.id.action_next).setTitle(R.string.next);
                    break;
            case 2: menu.findItem(R.id.action_prev).setTitle(R.string.back);
                    menu.findItem(R.id.action_photo).setVisible(false);
                    menu.findItem(R.id.action_next).setTitle(R.string.next);
                    break;
            case 3: menu.findItem(R.id.action_prev).setTitle(R.string.back);
                    menu.findItem(R.id.action_photo).setVisible(false);
                    menu.findItem(R.id.action_next).setTitle(R.string.next);
                    break;
            case 4: menu.findItem(R.id.action_prev).setTitle(R.string.back);
                    menu.findItem(R.id.action_photo).setVisible(false);
                    menu.findItem(R.id.action_next).setTitle(R.string.submit);
                    break;
            default: break;
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.new_report, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_prev:

                if (VF.getDisplayedChild() == 0) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder
                            .setTitle(R.string.warning)
                            .setMessage(R.string.new_report_cancel_warning)
                            .setCancelable(false)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    for (int c=0; c<images.getCount(); c++) {
                                        File file = new File(images.getItem(c).path);
                                        file.delete();
                                    }

                                    finish();
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();

                    alertDialog.show();
                } else {
                    VF.showPrevious();
                }
                invalidateOptionsMenu();
                return true;
            case R.id.action_next:
                //invalidateOptionsMenu();
                if(VF.getDisplayedChild() == 2){
                    generateSummary();
                    VF.showNext();
                }
                else if(VF.getDisplayedChild() != VF.getChildCount()-1) {
                    VF.showNext();
                }
                else if(VF.getDisplayedChild() == 4){
                    submitFinishedReport();
                }
                invalidateOptionsMenu();
                return true;
            case R.id.action_photo:
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                imageFilePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES) +  "/" + timeStamp + "_slide.jpg";

                File imageFile = new File(imageFilePath);
                Uri fileUri = Uri.fromFile(imageFile);
                cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
            bmpFactoryOptions.inJustDecodeBounds = true;

            Bitmap bmp = BitmapFactory.decodeFile(imageFilePath, bmpFactoryOptions);

            bmpFactoryOptions.inSampleSize = calculateInSampleSize(bmpFactoryOptions, 100, 100);
            bmpFactoryOptions.inJustDecodeBounds = false;

            bmp = BitmapFactory.decodeFile(imageFilePath, bmpFactoryOptions);

            myBitmap bmpp = new myBitmap();
            bmpp.image = bmp;
            bmpp.path = imageFilePath;

            images.AddImage(bmpp);
            images.notifyDataSetChanged();
        } else if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK) {
            int pos = data.getIntExtra("pos", -1);

            if (pos != -1 ){
                File file = new File(images.getItem(pos).path);
                file.delete();

                images.remove(pos);
                images.notifyDataSetChanged();
            }
        }
    }


    @Override
    public void onBackPressed() {
        invalidateOptionsMenu();
        if (VF.getDisplayedChild() == 0) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder
                    .setTitle(R.string.warning)
                    .setMessage(R.string.new_report_cancel_warning)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            for (int c=0; c<images.getCount(); c++) {
                                File file = new File(images.getItem(c).path);
                                file.delete();
                            }

                            finish();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();

            alertDialog.show();
        } else {
            VF.showPrevious();
        }
    }

    String fname, mname, lname, birthday, gender, diagnosisHuman, diagnosisNotes, photoCount, dateCreated, timeCreated, latitude,longitude;
    String USERNAME, PASSWORD;
    TextView textViewLat, textViewLong;

    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        latitude = String.valueOf(lat);
        longitude = String.valueOf(lng);
        textViewLat.setText(latitude);
        textViewLong.setText(longitude);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    private void generateSummary() {

        EditText editText1=(EditText )findViewById(R.id.given_name_textfield);
        EditText editText2=(EditText )findViewById(R.id.middle_name_textfield);
        EditText editText3=(EditText )findViewById(R.id.last_name_textfield);
        DateDisplayPicker dateDisplayPicker=(DateDisplayPicker )findViewById(R.id.clientEditCreate_BirthDateDayPicker);
        Spinner spinner1=(Spinner )findViewById(R.id.gender_spinner);
        GridView gridView1 = (GridView) findViewById(R.id.new_report_photos_layout);
        Spinner spinner2=(Spinner )findViewById(R.id.species_spinner);
        EditText editText4=(EditText )findViewById(R.id.diagnostic_notes);
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        fname           =editText1.getText().toString();
        mname           =editText2.getText().toString();
        lname           =editText3.getText().toString();
        birthday        =dateDisplayPicker.getText().toString();
        gender          =spinner1.getSelectedItem().toString();
        photoCount      =Integer.toString(gridView1.getCount());
        diagnosisHuman  =spinner2.getSelectedItem().toString();
        diagnosisNotes  =editText4.getText().toString();
        dateCreated     = today.format("%m/%d/%Y");
        timeCreated     = today.format("%H:%M:%S");

        TextView textView1 = (TextView) findViewById(R.id.textView1);
        TextView textView2 = (TextView) findViewById(R.id.textView2);
        TextView textView3 = (TextView) findViewById(R.id.textView3);
        TextView textView4 = (TextView) findViewById(R.id.textView4);
        TextView textView5 = (TextView) findViewById(R.id.textView5);
        TextView textView6 = (TextView) findViewById(R.id.textView6);
        TextView textView7 = (TextView) findViewById(R.id.textView7);
        TextView textView8 = (TextView) findViewById(R.id.textView8);
        TextView textViewA = (TextView) findViewById(R.id.textViewA);
        TextView textViewB = (TextView) findViewById(R.id.textViewB);
        textViewLat        = (TextView) findViewById(R.id.textViewLat);
        textViewLong       = (TextView) findViewById(R.id.textViewLong);

        textView1.setText(fname);
        textView2.setText(mname);
        textView3.setText(lname);
        textView4.setText(birthday);
        textView5.setText(gender);
        textView6.setText(photoCount);
        textView7.setText(diagnosisHuman);
        textView8.setText(diagnosisNotes);
        textViewA.setText(dateCreated);
        textViewB.setText(timeCreated);
        onLocationChanged(location);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    private String combineData(String metaData){
        EditText editText1=(EditText )findViewById(R.id.username);
        EditText editText2=(EditText )findViewById(R.id.password);
        USERNAME           =editText1.getText().toString();
        PASSWORD           =editText2.getText().toString();

        metaData = dateCreated + "\n"
                + timeCreated + "\n"
                + latitude + "\n"
                + longitude + "\n"
                + fname + "\n"
                + mname + "\n"
                + lname + "\n"
                + birthday + "\n"
                + gender + "\n"
                + diagnosisHuman + "\n"
                + diagnosisNotes;
        return metaData;
    }
    private void makeTextFile(){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File entryFile = new File (getExternalFilesDir(null), "textData.txt");

        try {
            OutputStream os = new FileOutputStream(entryFile);
            String metaData = new String();

            os.write(combineData(metaData).getBytes());
            os.close();
        } catch (IOException e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            Log.w("ExternalStorage", "Error writing " + entryFile, e);
        }
    }
    private void submitFinishedReport() {

        makeTextFile();

        ArrayList fileList = new ArrayList();
        try{
            fileList.add(0,getExternalFilesDir(null).getPath() + "/textData.txt");
        }
        catch (Exception e){
            Log.v("Error","arrayList error");
        }

        File sdCardEnv = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imgDir = new File(sdCardEnv, "path");
        for (int i=1; i < images.getCount()+1;i++ ) {
            fileList.add(i,images.getItem(i-1).path);
        }

        String[] entryData = new String[fileList.size()];
        for(int i=0;i<fileList.size();i++){
            entryData[i] = fileList.get(i).toString();
        }

        File zipFile = new File (getExternalFilesDir(null), "entryData.zip");

        Compress firstZip = new Compress(entryData,zipFile.getPath());
        firstZip.zip();

        Log.v("sd", "main" + zipFile.getPath());

        onDestroy();
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.v("stop", "onPAUSE");
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
        Log.v("stop", "finish");
    }


}
