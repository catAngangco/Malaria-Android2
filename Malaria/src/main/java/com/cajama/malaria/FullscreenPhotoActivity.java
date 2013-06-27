package com.cajama.malaria;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;

public class FullscreenPhotoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_photo);
        Intent intent = getIntent();

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();

        Bitmap bmp = BitmapFactory.decodeFile(intent.getStringExtra("path"), bmpFactoryOptions);

        ImageView image = (ImageView) findViewById(R.id.fullscreen_imageView);
        image.setImageBitmap(bmp);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fullscreen_photo, menu);
        return true;
    }
    
}
