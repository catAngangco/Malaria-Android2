package com.cajama.malaria;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class FullscreenPhotoActivity extends Activity {
    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_photo);
        Intent intent = getIntent();

        pos = intent.getIntExtra("pos", -1);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_photo:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder
                        .setTitle(R.string.warning)
                        .setMessage(R.string.photo_delete_warning)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("pos", pos);
                                setResult(RESULT_OK, resultIntent);
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

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
}
