package com.ijse.ijsestm;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class Pop extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewimage);

        DisplayMetrics dis=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dis);

        int width=dis.widthPixels;
        int heigh=dis.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(heigh*.6));

        ImageView imageView=(ImageView) findViewById(R.id.imageView);

        byte[] decodedString = Base64.decode(MainActivity.imagePath, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0,decodedString.length);
        imageView.setImageBitmap(decodedByte);

    }
}
