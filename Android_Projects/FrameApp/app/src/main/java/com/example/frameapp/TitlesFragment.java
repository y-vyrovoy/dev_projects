package com.example.frameapp;


import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class TitlesFragment extends ListFragment {

    public interface IOnListItemClickListener{
        public void OnClickAction(int index);
    }

    public static String ARG_INDEX = "com.example.frameapp.TitlesFragment.ARG_INDEX";
    private IOnListItemClickListener mCallback;
    private int mCurrentIndex;

    public TitlesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Populate list with our static array of titles.
        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_activated_1, MainActivity.mListHeaders));

        if(savedInstanceState != null){
            mCurrentIndex = savedInstanceState.getInt(ARG_INDEX);
            getListView().setItemChecked(mCurrentIndex, true);
        }
        else{
            mCurrentIndex = 0;
        }

        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mCallback.OnClickAction(mCurrentIndex);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_INDEX, mCurrentIndex);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mCallback = (IOnListItemClickListener)context;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mCurrentIndex = position;
        mCallback.OnClickAction(mCurrentIndex);
    }


}
