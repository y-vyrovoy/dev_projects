package com.example.yuravyrovoy.frapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements HeadersFragment.OnHeadlineSelectedListener {

    public static  DataSource ds;

    public void onArticleSelected(int position){
        showData(position);
    }

    protected void showData(int position){

        TextFragment textFragment = (TextFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_viewer);
        textFragment.SetText(position);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ds = new DataSource();
    }
}
