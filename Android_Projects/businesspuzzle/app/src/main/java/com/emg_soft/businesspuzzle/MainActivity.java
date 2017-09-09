package com.emg_soft.businesspuzzle;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.emg_soft.businesspuzzle.workcircuit.circuitdata.CircuitContainer;
import com.emg_soft.businesspuzzle.workcircuit.circuitdata.CircuitTrack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        textViewResults = (TextView)findViewById(R.id.textViewResults);
        textFileCircuit = (TextView)findViewById(R.id.textFileCircuit);
        btnReadXML = (Button) findViewById(R.id.btnLoadXML);

        btnReadXML.setEnabled(false);

        textViewResults.setMovementMethod(new ScrollingMovementMethod());

        requestPermissionsAndResponse();

        fillFileLists();

        final ArrayAdapter<String> adapterCircuits = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_single_choice, lstCircuits);
        listViewFilesCircuits.setAdapter(adapterCircuits);

        listViewFilesCircuits.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            fileNameCircuit = adapterCircuits.getItem(i);
            textFileCircuit.setText(fileNameCircuit);
            }
        });

    }

    private void fillFileLists(){

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        File[] subFiles = dir.listFiles();

        if (subFiles != null)
        {
            for (File file : subFiles)
            {
                if(file.getName().indexOf("circuit") >= 0){
                    lstCircuits.add(file.getName());
                }

            }
        }
    }

    public void onBtnLoadXML(View view){

        StringBuilder textFile = new StringBuilder();


        try {

            File file = new File(Environment.
                                    getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                                    fileNameCircuit);

            BufferedReader reader = new BufferedReader(new FileReader(file));

            circuit = CircuitContainer.createCurcuitFromXML(new FileInputStream(file));

            if(circuit == null) return;

            StringBuilder textData = new StringBuilder();


            reader.close();


            int inputsNumber = circuit.getLstInputs().size();
            String resultDscription = new String();

            for (int iOption = 0; iOption < Math.pow(2, inputsNumber) ; iOption++){

                Map<String, Boolean> mapInputs = new HashMap<>();

                int mask = 1;
                for(int iInput = 0; iInput < inputsNumber; iInput++){

                    boolean bInputValue = (iOption & mask) == mask;
                    mapInputs.put(circuit.getLstInputs().get(iInput).getName(), bInputValue);
                    mask = mask << 1;

                    resultDscription += circuit.getLstInputs().get(iInput).getName() +
                                        " = " + Boolean.toString( bInputValue) + "; ";
                }

                circuit.setInputs(mapInputs);
                boolean result = circuit.getLstResults().get(0).getValue();
                resultDscription += "result = " + Boolean.toString( result) + "\n";
            }

            resultDscription += "\n\nTracks:";

            List<CircuitTrack> lstTracks = circuit.getLstTracks();
            for(CircuitTrack track : lstTracks){

                resultDscription += "\n" +
                                    track.getItemStart().getName() +
                                    " -> " +
                                    track.getItemEnd().getName();

            }

            textViewResults.setText(resultDscription);


        }catch (IOException ex){
            textFile.append(ex.getMessage());
        }
        catch (Exception ex){
            textFile.append(ex.getMessage());
        }


   }

    public void onShowCircuit(View view){

        if(circuit == null){
            return;
        }

        CircuitContainer.removeAllCircuits();
        CircuitContainer.addCircuit(circuit);

        startActivity(new Intent(this, CircuitActivity.class));
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
                btnReadXML.setEnabled( permissionsGranted == true );
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
