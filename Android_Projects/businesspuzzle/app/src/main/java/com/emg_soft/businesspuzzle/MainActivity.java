package com.emg_soft.businesspuzzle;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.emg_soft.businesspuzzle.workcircuit.circuitdata.CircuitContainer;
import com.emg_soft.businesspuzzle.workcircuit.circuitdata.CircuitManager;
import com.emg_soft.businesspuzzle.workcircuit.circuitdata.CircuitTrack;
import com.emg_soft.businesspuzzle.workcircuit.circuitviews.BusinessActivity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
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

    private static String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_PERMISSIONS_ANSWER = 2017;

    private ImageView imageMainScreen;
    private ConstraintLayout layoutRoot;

    private Button btnAWS;
    private Button btnMainCPU;
    private Button btnApplication;
    private Button btnRTController;
    private Button btnIOT;
    private Button btnUI;

    private boolean waitForPermissions;
    private boolean permissionsGranted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageMainScreen = (ImageView)findViewById(R.id.img_main_screen);
        layoutRoot = (ConstraintLayout)findViewById(R.id.layoutRoot);


        enableControls(false);


        requestPermissionsAndResponse();

        CircuitManager.initFromResources(getResources(), this.getPackageName());

        layoutRoot.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
                                       int oldTop, int oldRight, int oldBottom) {
                layoutButtons();
            }
        });

        createButttons();
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
                enableControls( permissionsGranted == true );
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


    private void enableControls(boolean enable){
    }

    private void createButttons(){

        btnAWS = addButton("AWS", new View.OnClickListener(){
                                                    @Override
                                                    public void onClick(View v) {
                                                        startCircuitActivity("cloud_iot");
                                                    }
                                                });


        btnMainCPU =  addButton("Main CPU", new View.OnClickListener(){
                                                    @Override
                                                    public void onClick(View v) {
                                                        startCircuitActivity("main_board");
                                                    }
                                                });


        btnApplication =  addButton("Applicator", new View.OnClickListener(){
                                                    @Override
                                                    public void onClick(View v) {
                                                        startCircuitActivity("applicator");
                                                    }
                                                });

        btnRTController  =  addButton("RT Controller", new View.OnClickListener(){
                                                    @Override
                                                    public void onClick(View v) {
                                                        startCircuitActivity("rt_controller");
                                                    }
                                                });

        btnIOT =  addButton("IOT", new View.OnClickListener(){
                                                    @Override
                                                    public void onClick(View v) {
                                                        startCircuitActivity("iot");
                                                    }
                                                });

        btnUI =  addButton("Web Based", new View.OnClickListener(){
                                                    @Override
                                                    public void onClick(View v) {
                                                        startCircuitActivity("web_base_ui");
                                                    }
                                                });
    }

    private Button addButton(String name, View.OnClickListener listener){

        Button button = new Button(this);

        //button.setText(name);
        button.setOnClickListener(listener);
        button.setBackgroundColor(Color.TRANSPARENT);

        layoutRoot.addView(button);

        return button;

    }

    private void layoutButtons(){

        setCoord(btnAWS, 55, 310, 35, 230);
        setCoord(btnMainCPU, 195, 565, 645, 885);
        setCoord(btnApplication, 660, 845, 1020, 1135);
        setCoord(btnRTController, 655, 845, 715, 830);
        setCoord(btnIOT, 330, 470, 355, 450);
        setCoord(btnUI, 615, 755, 320, 450);

    }

    private void setCoord(TextView button, int l, int r, int t, int b){

        int imageTop = imageMainScreen.getTop();

        int imageWidth = imageMainScreen.getWidth();
        int imageHeight= imageMainScreen.getHeight();

        float xScale = ((float)imageWidth)/960;
        float yScale = ((float)imageHeight)/1440;

        button.layout((int)(xScale * l),
                        (int)(yScale * t + imageTop ),
                        (int)(xScale * r),
                        (int)(yScale * b + imageTop ));

    }

    public void startCircuitActivity(String circuitName){

        int nCircuit = CircuitManager.getIndexFromName(circuitName);
        if(nCircuit < 0)
        {
            return;
        }

        startActivity(new Intent(this, CircuitActivity.class).putExtra("circuit_number", nCircuit));

    }

    public void goThere(View view){
        startActivity(new Intent(MainActivity.this, BusinessActivity.class));
    }

}
