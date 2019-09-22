package com.ijse.ijsestm;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.annotation.Nullable;

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
    }
}
