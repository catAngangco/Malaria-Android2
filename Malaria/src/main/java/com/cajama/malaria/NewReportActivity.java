package com.cajama.malaria;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.ViewFlipper;

import com.cajama.android.customviews.SquareImageView;

import java.util.Vector;

public class NewReportActivity extends Activity {
    ViewFlipper VF;
    GridView new_report_photos_layout;
    ImageAdapter images;
    private static final int CAMERA_REQUEST = 1888;

    private class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private Vector<Bitmap> images = new Vector<Bitmap>();

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public void AddImage(Bitmap b) {
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
        public Object getItem(int arg0) {
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

            img.setImageBitmap(images.get(position));
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

        VF = (ViewFlipper) findViewById(R.id.viewFlipper);

        images = new ImageAdapter(this);
        new_report_photos_layout = (GridView) findViewById(R.id.new_report_photos_layout);
        new_report_photos_layout.setAdapter(images);

        new_report_photos_layout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent i = new Intent(getApplicationContext(), FullscreenPhotoActivity.class);
                i.putExtra("image", (Bitmap) images.getItem(position));
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (VF.getDisplayedChild() == 0) {
            menu.findItem(R.id.action_prev).setIcon(R.drawable.navigation_cancel).setTitle(R.string.cancel);
        } else {
            menu.findItem(R.id.action_prev).setIcon(R.drawable.navigation_previous_item).setTitle(R.string.back);
        }
        if (VF.getDisplayedChild() == 1) {
            menu.findItem(R.id.action_photo).setVisible(true);
        } else {
            menu.findItem(R.id.action_photo).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_report, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_prev:
                invalidateOptionsMenu();
                if (VF.getDisplayedChild() == 0) {
                    finish();
                } else {
                    VF.showPrevious();
                }
                return true;
            case R.id.action_next:
                invalidateOptionsMenu();
                VF.showNext();
                return true;
            case R.id.action_photo:
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            images.AddImage(photo);
            images.notifyDataSetChanged();

        }
    }
}
