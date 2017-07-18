package com.example.yuravyrovoy.frapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class TextFragment extends Fragment {

    int mCurrentIndex;
    public static String ARG_POSITION = "com.example.yuravyrovoy.frapp.ARG_POSITION";

    public TextFragment() {
        //mCurrentIndex = -1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_headers, container, false);
    }

    public void SetText (int position){
        TextView textView = (TextView)getActivity().findViewById(R.id.details_text);
        if(textView != null) {
            textView.setText(MainActivity.ds.getItemText(position));
        }
    }

}
