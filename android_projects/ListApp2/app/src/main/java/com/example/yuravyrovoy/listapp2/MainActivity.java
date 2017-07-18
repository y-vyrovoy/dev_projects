package com.example.yuravyrovoy.listapp2;

import android.app.ListActivity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity {

    private TextView textView;
    private List<String> listValues;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView)findViewById(R.id.mainText);

        listValues = new ArrayList<>();
        listValues.add("Kogda");
        listValues.add("Popal");
        listValues.add("Vpervye");
        listValues.add("Bering");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                                                            R.layout.list_row_layout,
                                                            R.id.textRow,
                                                            listValues);


        setListAdapter(adapter);

    }

    // when an item of the list is clicked

    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        super.onListItemClick(list, view, position, id);

        String selectedItem = (String) getListView().getItemAtPosition(position);
        //String selectedItem = (String) getListAdapter().getItem(position);

        textView.setText("You clicked " + selectedItem + " at position " + position);
    }


}
