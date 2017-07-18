package com.example.frameapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TitlesFragment.IOnListItemClickListener {

    public static List<String> mListHeaders;
    public static List<String> mListTexts;

    int nLastChoice;

    private void InitLists(){

        int NItems = 10;

        mListHeaders = new ArrayList<>();
        for(int i = 0; i < NItems; i++){
            mListHeaders.add("Header " + Integer.toString(i));
        }

        mListTexts = new ArrayList<>();
        for(int i = 0; i < NItems; i++){
            String sText = "Article  " + Integer.toString(i);
            for(int j = 0; j < 3; j++) {
                sText += sText;
            }
            mListTexts.add(sText);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitLists();
    }

    @Override
    public void OnClickAction(int index) {
        showData(index);
    }

    private void showData(int index){

        ArticleFragment fragmentArticle = (ArticleFragment)getSupportFragmentManager().
                                            findFragmentById(R.id.fragment_articles);

        if(fragmentArticle != null){
            fragmentArticle.setText(index);
        }
    }
}
