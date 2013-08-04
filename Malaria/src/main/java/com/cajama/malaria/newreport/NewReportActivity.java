package com.cajama.malaria.newreport;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.cajama.android.customviews.DateDisplayPicker;
import com.cajama.malaria.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewReportActivity extends SherlockActivity{
    ViewFlipper VF;
    GridView new_report_photos_layout;
    ImageAdapter images;
    private static final int CAMERA_REQUEST = 1888;
    private static final int PHOTO_REQUEST = 4214;
    private String imageFilePath;
    private String[] step_subtitles;
    ArrayList<String> entryList = new ArrayList<String>();
    ArrayList<String> accountList = new ArrayList<String>();

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

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
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

            bmpFactoryOptions.inSampleSize = calculateInSampleSize(bmpFactoryOptions, 100, 100);
            bmpFactoryOptions.inJustDecodeBounds = false;

            Bitmap bmp = BitmapFactory.decodeFile(imageFilePath, bmpFactoryOptions);

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

    private void generateSummary() {
        String fname, mname, lname, birthday, gender, diagnosisHuman, diagnosisNotes, photoCount, dateCreated, timeCreated, latitude,longitude;

        //date & time
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        //date
        dateCreated = today.format("%m/%d/%Y");
        TextView textViewA = (TextView) findViewById(R.id.textViewA);
        textViewA.setText(dateCreated);
        entryList.add(dateCreated);
        //time
        timeCreated = today.format("%H:%M:%S");
        TextView textViewB = (TextView) findViewById(R.id.textViewB);
        textViewB.setText(timeCreated);
        entryList.add(timeCreated);

        //latitude & longitude
        GetLocation getLoc = new GetLocation(this);
        //latitude
        TextView textViewLat = (TextView) findViewById(R.id.textViewLat);
        latitude = getLoc.getLatitude();
        textViewLat.setText(latitude);
        entryList.add(latitude);
        //longitude
        TextView textViewLong = (TextView) findViewById(R.id.textViewLong);
        longitude = getLoc.getLongitude();
        textViewLong.setText(longitude);
        entryList.add(longitude);

        //first name
        EditText editText1 = (EditText)findViewById(R.id.given_name_textfield);
        TextView textView1 = (TextView)findViewById(R.id.textView1);
        fname = editText1.getText().toString();
        textView1.setText(fname);
        entryList.add(fname);

        //middle name
        EditText editText2=(EditText)findViewById(R.id.middle_name_textfield);
        TextView textView2 =(TextView)findViewById(R.id.textView2);
        mname=editText2.getText().toString();
        textView2.setText(mname);
        entryList.add(mname);

        //last name
        EditText editText3=(EditText)findViewById(R.id.last_name_textfield);
        TextView textView3 =(TextView)findViewById(R.id.textView3);
        lname=editText3.getText().toString();
        textView3.setText(lname);
        entryList.add(lname);

        //birthday
        DateDisplayPicker dateDP=(DateDisplayPicker)findViewById(R.id.clientEditCreate_BirthDateDayPicker);
        TextView textView4=(TextView)findViewById(R.id.textView4);
        birthday=dateDP.getText().toString();
        textView4.setText(birthday);
        entryList.add(birthday);

        //sex
        Spinner spinner1=(Spinner)findViewById(R.id.gender_spinner);
        TextView textView5 =(TextView)findViewById(R.id.textView5);
        gender=spinner1.getSelectedItem().toString();
        textView5.setText(gender);
        entryList.add(gender);

        //number of images
        GridView gridView1 = (GridView)findViewById(R.id.new_report_photos_layout);
        TextView textView6 = (TextView)findViewById(R.id.textView6);
        photoCount = Integer.toString(gridView1.getCount());
        textView6.setText(photoCount);

        //malaria species
        Spinner spinner2=(Spinner)findViewById(R.id.species_spinner);
        TextView textView7=(TextView) findViewById(R.id.textView7);
        diagnosisHuman=spinner2.getSelectedItem().toString();
        textView7.setText(diagnosisHuman);
        entryList.add(diagnosisHuman);

        //diagnostic notes
        EditText editText4=(EditText)findViewById(R.id.diagnostic_notes);
        TextView textView8 =(TextView)findViewById(R.id.textView8);
        diagnosisNotes =editText4.getText().toString();
        textView8.setText(diagnosisNotes);
        entryList.add(diagnosisNotes);
    }

    private String getAccountData(){
        String USERNAME, PASSWORD;
        EditText editText1=(EditText )findViewById(R.id.username);
        EditText editText2=(EditText )findViewById(R.id.password);
        USERNAME           =editText1.getText().toString();
        PASSWORD           =editText2.getText().toString();
        Log.v("write","USERNAME: " + USERNAME + " PASSWORD: " + PASSWORD);
        accountList.add(USERNAME);
        accountList.add(PASSWORD);
        Log.v("write","stuff: " + accountList.get(0) + accountList.get(1));

        return USERNAME;
    }

    private void submitFinishedReport() {

        ArrayList<String> imageList = new ArrayList<String>();
        for (int i=1; i < images.getCount()+1;i++ ) imageList.add(i,images.getItem(i-1).path);

        String USERNAME = getAccountData();

        AssembleData assembleData = new AssembleData(getApplicationContext(),entryList,imageList,accountList,USERNAME);
        assembleData.start();

        finish();
    }
}
