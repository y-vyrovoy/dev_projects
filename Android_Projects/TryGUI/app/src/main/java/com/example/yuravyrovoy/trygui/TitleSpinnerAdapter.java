package com.example.yuravyrovoy.trygui;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Yura Vyrovoy on 7/31/2017.
 */

public class TitleSpinnerAdapter extends BaseAdapter {

    private ArrayList<SpinnerNavItem> spinnerNavItem;
    private Context context;

    public TitleSpinnerAdapter(Context context,
                                  ArrayList<SpinnerNavItem> spinnerNavItem) {
        this.spinnerNavItem = spinnerNavItem;
        this.context = context;
    }

    @Override
    public int getCount() {
        return spinnerNavItem.size();
    }

    @Override
    public Object getItem(int position) {
        return spinnerNavItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {


        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_header_title_navigation, null);

        }
/*
        ImageView imgIcon = convertView.findViewById(R.id.imgIcon);
        TextView txtTitle = convertView.findViewById(R.id.txtTitle);

        imgIcon.setImageResource(spinnerNavItem.get(position).getIcon());
        imgIcon.setVisibility(View.GONE);
        txtTitle.setText(spinnerNavItem.get(position).getTitle());
*/
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup viewGroup) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_item_title_navigation, null);
        }

        ImageView imgIcon = convertView.findViewById(R.id.imgIcon);
        TextView txtTitle = convertView.findViewById(R.id.txtTitle);

        //imgIcon.setImageResource(R.drawable.logo_my_pat);
        imgIcon.setImageResource(spinnerNavItem.get(position).getIcon());
        txtTitle.setText(spinnerNavItem.get(position).getTitle());
        return convertView;

    }

}
