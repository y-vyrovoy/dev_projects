package com.example.frameapp;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ArticleFragment extends Fragment {

    private int mIndex;

    public ArticleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_article, container, false);

    }


    @Override
    public void onResume (){
        super.onResume();
        setText(mIndex);
    }

    public void setText(int index){
        mIndex = index;
        TextView textView = getActivity().findViewById(R.id.text_article);
        if(index >= 0) {
            textView.setText(MainActivity.mListTexts.get(index));
        }
        else{
            textView.setText("--- NO TEXT ---");
        }
    }
}
