package com.emg_soft.businesspuzzle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.emg_soft.businesspuzzle.workcircuit.circuitdata.CircuitContainer;
import com.emg_soft.businesspuzzle.workcircuit.circuitviews.CircuitViewItemResult;

import java.util.List;

public class CircuitActivity extends AppCompatActivity {

    CircuitContainer circuit;
    private static final String TAG = CircuitActivity.class.getSimpleName();

    private TextView textViewResult;
    private CircuitLayout layout;

    private List<CircuitViewItemResult>  lstResultViews = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circuit);


        final int circuitItem = CircuitContainer.getCircuitsCount();

        if(circuitItem > 0) {
            circuit = CircuitContainer.getCircuit(circuitItem - 1);
        }
        else{
            circuit = null;
            return;
        }

        layout = (CircuitLayout)findViewById(R.id.layoutCircuit);

        layout.createLayoutFromCircuit(circuit, this);

        layout.recalcResult();

    }

}
