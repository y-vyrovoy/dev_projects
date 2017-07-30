package com.example.yuravyrovoy.trygui;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        // Inflate a menu to be displayed in the toolbar
        myToolbar.inflateMenu(R.menu.menu_title);

        // Set an OnMenuItemClickListener to handle menu item clicks
        myToolbar.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        TextView textView = (TextView)findViewById(R.id.textView);
                        textView.setText(item.getTitle());

                        return true;
                    }
                });

        ActionBar ab = getSupportActionBar();

        Log.i("tag", Boolean.toString(ab.isShowing()));
    }
}
