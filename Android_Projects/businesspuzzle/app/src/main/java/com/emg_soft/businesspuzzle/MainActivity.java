package com.emg_soft.businesspuzzle;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.emg_soft.businesspuzzle.workcircuit.circuitdata.CircuitContainer;
import com.emg_soft.businesspuzzle.workcircuit.circuitdata.CircuitManager;
import com.emg_soft.businesspuzzle.workcircuit.circuitdata.CircuitTrack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static com.emg_soft.businesspuzzle.workcircuit.circuitdata.CircuitContainer.TAG_ROOT;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS_ANSWER = 2017;

    private ListView listViewFilesCircuits;
    private TextView textViewResults;

    private TextView textFileCircuit;
    private Button btnReadXML;

    private boolean waitForPermissions;
    private boolean permissionsGranted;

    private CircuitContainer circuit = null;

    private List<String> lstCircuits = new ArrayList<>();
    private String fileNameCircuit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewFilesCircuits = (ListView)findViewById(R.id.viewFilesCircuits);

        listViewFilesCircuits.setEnabled(false);

        requestPermissionsAndResponse();

        CircuitManager.initFromResources(getResources(), this.getPackageName());
        fillFileLists();

        final ArrayAdapter<String> adapterCircuits =
                new ArrayAdapter<>(getBaseContext(),
                                    android.R.layout.simple_list_item_single_choice,
                                    lstCircuits);

        listViewFilesCircuits.setAdapter(adapterCircuits);


        listViewFilesCircuits.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            onLoadXML(i);
            }
        });

    }

    private void fillFileLists(){

        for(int i = 0; i < CircuitManager.ContainersCount(); i++){
            lstCircuits.add(CircuitManager.getContainer(i).getName());
        }

    }

    public void onLoadXML(int i){

        startActivity(new Intent(this, CircuitActivity.class).putExtra("circuit_number", i));
    }

    private void requestPermissionsAndResponse(){
        waitForPermissions = true;
        permissionsGranted = false;

        // waits while permissions will be granted or user will refuse
        new AsyncTask<Void, Void, String>(){

            @Override
            protected String doInBackground(Void... params) {

                while ( waitForPermissions == true){}
                return null;
            }

            @Override
            protected void onPostExecute(String result){
                listViewFilesCircuits.setEnabled( permissionsGranted == true );
            }

        }.execute();

        requestAppPermissions();
    }

    private void requestAppPermissions(){

        // If SDK version is lower than 23 (Marshmallow) no need to request permissions in runtime
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            ActivityCompat.
                    requestPermissions(this,
                                        new String [] {Manifest.permission.READ_EXTERNAL_STORAGE},
                                        REQUEST_PERMISSIONS_ANSWER);
        }
        else
        {

            // simple check - do we have permission or not
            if( (ContextCompat.
                        checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                                                            PackageManager.PERMISSION_GRANTED)  ) {
                permissionsGranted = true;
            }
            else {
                permissionsGranted = false;
            }

            waitForPermissions = false;
        }
    }

    @Override
    public void onRequestPermissionsResult (int requestCode,
                                            String[] permissions,
                                            int[] grantResults){

        if (requestCode == REQUEST_PERMISSIONS_ANSWER) {

            permissionsGranted =
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                                                                PackageManager.PERMISSION_GRANTED;

            waitForPermissions = false;
        }

    }



}
