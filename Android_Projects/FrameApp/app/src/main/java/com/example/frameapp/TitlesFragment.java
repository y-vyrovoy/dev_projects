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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_title, container, false);
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
    }

    @Override
    public void onResume (){
        super.onResume();
        showArticle(mCurrentIndex);
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
        showArticle(position);
    }

    public void showArticle(int index){
        mCurrentIndex = index;
        mCallback.OnClickAction(mCurrentIndex);



/*
        ArticleFragment fragmentArticle = (ArticleFragment)
                                    getFragmentManager().findFragmentById(R.id.fragment_articles);



        boolean bDualPane = false;
        if ( (fragmentArticle != null) && (fragmentArticle.isVisible() == true) ){
            bDualPane = true;
        }


        if(bDualPane == true){
            mCallback.OnClickAction(mCurrentIndex);
        }
        else{
            Intent intent = new Intent();
            intent.setClass(getActivity(), ArticleActivity.class);
            intent.putExtra(ArticleFragment.ARG_INDEX, mCurrentIndex);
            startActivity(intent);
        }
*/
    }

}
