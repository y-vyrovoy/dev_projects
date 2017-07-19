package com.example.frameapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TitlesFragment.IOnListItemClickListener {

    public static List<String> mListHeaders;
    public static List<String> mListTexts;



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

    public MainActivity(){
        InitLists();
    }

    private boolean mDoublePane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View v = findViewById(R.id.fragment_articles);
        mDoublePane = (v != null) && (v.getVisibility() == View.VISIBLE);
    }

    @Override
    public void OnClickAction(int index) {
        showData(index);
    }

    private void showData(int index){

        ArticleFragment fragmentArticle = (ArticleFragment)getFragmentManager().
                                                            findFragmentById(R.id.fragment_articles);

        if( (fragmentArticle != null) && (mDoublePane == true) ){
            fragmentArticle.setText(index);
        }
        else if(index >= 0){
            Intent intent = new Intent();
            intent.setClass(this, ArticleActivity.class);
            intent.putExtra(ArticleActivity.ARG_INDEX, index);
            startActivity(intent);
        }


    }
}
