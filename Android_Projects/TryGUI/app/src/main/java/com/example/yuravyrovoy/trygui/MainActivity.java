package com.example.yuravyrovoy.trygui;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity
        extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    private ActionBar actionBar;

    // Title navigation Spinner data
    private ArrayList<SpinnerNavItem> navSpinner;
    private TitleSpinnerAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        // Get a support ActionBar corresponding to this toolbar
        actionBar = getSupportActionBar();

        // Setup ActionBar
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setIcon(R.drawable.logo_my_pat);
        actionBar.setDisplayShowTitleEnabled(false);



        // Spinner title navigation data
        navSpinner = new ArrayList<>();
        navSpinner.add(new SpinnerNavItem("Info", R.drawable.ic_info_outline));
        navSpinner.add(new SpinnerNavItem("Share", R.drawable.ic_share));
        navSpinner.add(new SpinnerNavItem("Settings", R.drawable.ic_settings));


        // title drop down adapter
        adapter = new TitleSpinnerAdapter(getApplicationContext(), navSpinner);


        Spinner spinner = (Spinner)findViewById(R.id.spinnerTitle);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        showTestMessage("item #" + Integer.toString(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        showTestMessage("onNothingSelected");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate our menu from the resources by using the menu inflater.
        getMenuInflater().inflate(R.menu.menu_title, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemShare:
                showTestMessage("Share");
                return true;

            case R.id.itemClose:
                showTestMessage("Close");
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void showTestMessage(String sMessage){
        Toast.makeText(this, sMessage, Toast.LENGTH_SHORT).show();
    }


}
