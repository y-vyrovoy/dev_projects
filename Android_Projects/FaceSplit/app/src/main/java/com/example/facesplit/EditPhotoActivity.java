package com.example.facesplit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class EditPhotoActivity extends AppCompatActivity {

    private TextView textView;
    private SplitImageView imageEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_photo);

        imageEdit = (SplitImageView) findViewById(R.id.imageEdit);

        if(MyApp.getBitmapToEdit() != null) {
            imageEdit.setSourceBitmap(MyApp.getBitmapToEdit());
        }

        textView = (TextView)findViewById(R.id.textView);
        textView.setVisibility(View.INVISIBLE);
    }

    public void onBtnEdit(View v) {
        textView.setVisibility(View.VISIBLE);
    }
}
