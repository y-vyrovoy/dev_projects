package com.example.mycustomview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class ResultActivity extends AppCompatActivity {

    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        image = (ImageView) findViewById(R.id.viewImageResult);

        image.setImageBitmap(MyApp.getInstance().getBitmapLeft());
    }
}
