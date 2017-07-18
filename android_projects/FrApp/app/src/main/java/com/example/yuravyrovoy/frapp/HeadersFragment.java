package com.example.yuravyrovoy.frapp;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HeadersFragment extends ListFragment {
    OnHeadlineSelectedListener mCallback;

    // Container Activity must implement this interface
    public interface OnHeadlineSelectedListener {
        public void onArticleSelected(int position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mCallback = (OnHeadlineSelectedListener)context;
    }

    int mCurCheckPosition = 0;

    public HeadersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_headers, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Populate list with our static array of titles.
        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_activated_1, MainActivity.ds.getHeaderList()));

        if(savedInstanceState != null){
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }

        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        showDetails(mCurCheckPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //showDetails(position);
        mCallback.onArticleSelected(position);

    }

    void showDetails(int position){
        mCurCheckPosition = position;

        View frameDetails = (View)getActivity().findViewById(R.id.fragment_viewer);
        boolean bDualPane = (frameDetails != null) && (frameDetails.getVisibility() == View.VISIBLE);

        if(bDualPane == true){

            TextFragment textFragment = new TextFragment();

            Bundle args = new Bundle();
            args.putInt(textFragment.ARG_POSITION, position);
            textFragment.setArguments(args);

            FragmentTransaction ft = getFragmentManager().beginTransaction();

            ft.replace(R.id.fragment_viewer, textFragment);

            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();

        }
        else{
            Log.i("dualPane", "one");
        }
    }

}
