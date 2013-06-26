package com.cajama.malaria;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.widget.ImageView;

public class FullscreenPhotoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_photo);
        // get intent data
        Intent i = getIntent();

        // Selected image id
        int position = i.getExtras().getInt("id");

        LayoutInflater inflater = getLayoutInflater();
        Bitmap bitmap = (Bitmap) i.getParcelableExtra("image");
        ImageView image = (ImageView) findViewById(R.id.fullscreen_imageView);
        image.setImageBitmap(bitmap);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fullscreen_photo, menu);
        return true;
    }
    
}
