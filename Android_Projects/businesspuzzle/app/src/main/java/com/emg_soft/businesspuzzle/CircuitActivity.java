package com.emg_soft.businesspuzzle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.emg_soft.businesspuzzle.workcircuit.circuitdata.CircuitContainer;
import com.emg_soft.businesspuzzle.workcircuit.circuitdata.CircuitManager;
import com.emg_soft.businesspuzzle.workcircuit.circuitviews.CircuitViewItemResult;

import org.w3c.dom.Text;

import java.util.List;

public class CircuitActivity extends AppCompatActivity {

    CircuitContainer circuit;
    private static final String TAG = CircuitActivity.class.getSimpleName();

    private TextView textViewResult;
    private TextView textHeader;
    private ConstraintLayout layoutRoot;
    private CircuitLayout layout;

    private List<CircuitViewItemResult>  lstResultViews = null;

    private int deviceWidth;
    private int deviceHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circuit);

        Intent intent = getIntent();
        int iCircuitNumber = intent.getIntExtra("circuit_number", -1);

        if(iCircuitNumber < 0){
            Toast.makeText(this, "Can not find circuit", Toast.LENGTH_SHORT).show();
        }

        layoutRoot = (ConstraintLayout)findViewById(R.id.layoutRoot);
        textHeader = (TextView)findViewById(R.id.textHeader);

        final Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        Point deviceDisplay = new Point();
        display.getSize(deviceDisplay);

        deviceWidth = deviceDisplay.x;
        deviceHeight = deviceDisplay.y;

        setOwnBackground();

        circuit = CircuitManager.getContainer(iCircuitNumber);

        textHeader.setText(circuit.getHeader());

        layout = (CircuitLayout)findViewById(R.id.layoutCircuit);
        layout.createLayoutFromCircuit(circuit, this);
        layout.recalcResult();


    }

    private void setOwnBackground(){

        Bitmap bitmapSource = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        //Bitmap bitmapScaled = Bitmap.createScaledBitmap(bitmapSource, deviceWidth, deviceHeight, true);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmapSource);
        bitmapDrawable.setGravity(Gravity.TOP | Gravity.RIGHT);
        layoutRoot.setBackgroundColor(Color.BLACK);


    }

}
