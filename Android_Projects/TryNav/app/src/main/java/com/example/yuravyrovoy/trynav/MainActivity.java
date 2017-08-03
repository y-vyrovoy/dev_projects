package com.example.yuravyrovoy.trynav;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends BaseNavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Handle action bar item clicks here
        // The action bar will automatically handle clicks on the Home/Up button,
        // so long as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        switch (id){

            case R.id.action_share:
                showMessage("bar Share");
                break;

            case R.id.action_close:
                showMessage("bar Close");
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // TODO Handle navigation view item clicks here.

        int id = item.getItemId();

        if (id == R.id.itemInfo) {
            showMessage("nav Info");
        } else if (id == R.id.intemShare) {
            showMessage("nav Share");
        } else if (id == R.id.intemSettings) {
            showMessage("nav Settings");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showMessage(String sMessage){
        //Snackbar.make(findViewById(android.R.id.content), sMessage, Snackbar.LENGTH_LONG).setAction("Action", null).show();
        Toast.makeText(this, "MainActivity: " + sMessage, Toast.LENGTH_LONG ).show();
    }
}
