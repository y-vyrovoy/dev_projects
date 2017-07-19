package com.example.frameapp;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class ArticleActivity extends AppCompatActivity {

    public static String ARG_INDEX = "com.example.frameapp.ArticleActivity.ARG_INDEX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        if (getResources().getConfiguration().orientation
                                                        == Configuration.ORIENTATION_LANDSCAPE) {

            // If the screen is now in landscape mode, we can show the
            // dialog in-line with the list so we don't need this activity.
            finish();
            return;
        }
        if(savedInstanceState != null){
            int index = savedInstanceState.getInt(ARG_INDEX);
            ArticleFragment articleFragment = (ArticleFragment)getFragmentManager().findFragmentById(R.id.fragment_in_act_articles);

            if(articleFragment != null){
                articleFragment.setText(index);
            }
            else{
                Log.d("ArticleActivity:","Can't find fragment");
            }
        }
    }
}
