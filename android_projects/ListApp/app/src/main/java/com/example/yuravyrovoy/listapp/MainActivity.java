package com.example.yuravyrovoy.listapp;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getListView().addHeaderView(((View)getLayoutInflater().inflate(R.layout.my_header, null)));

        setListAdapter(new MyAdapter());
    }

    private class MyAdapter extends BaseAdapter {

        DataSource dataSource;

        public MyAdapter(){
            dataSource = new DataSource();
        }

        @Override
        public int getCount() {
            return dataSource.size();
        }

        @Override
        public String getItem(int position) {
            return dataSource.getItem(position).getsHeader();
        }

        @Override
        public long getItemId(int position) {
            return dataSource.getItem(position).getsHeader().hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.listview_item_row, container, false);
            }

            ((TextView) convertView.findViewById(R.id.fieldTitle)).setText(dataSource.getItem(position).getsHeader());
            ((TextView) convertView.findViewById(R.id.fieldSubTitle)).setText(dataSource.getItem(position).getsText());
            return convertView;
        }
    }
}
