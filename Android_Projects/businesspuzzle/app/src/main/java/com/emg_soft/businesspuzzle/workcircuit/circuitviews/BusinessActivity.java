package com.emg_soft.businesspuzzle.workcircuit.circuitviews;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.emg_soft.businesspuzzle.CircuitActivity;
import com.emg_soft.businesspuzzle.MainActivity;
import com.emg_soft.businesspuzzle.R;
import com.emg_soft.businesspuzzle.workcircuit.circuitdata.CircuitManager;

public class BusinessActivity extends AppCompatActivity {

    private ImageView logo;
    private ImageView imageMainScreen;
    private ConstraintLayout layoutRoot;
    private Button btnSWRequirements;
    private Button btnEffectiveManagement;
    private Button btnSatisfiedCustomer;
    private Button btnSuccessfulProjects;
    private Button btnTeamWork;
    private Button btnVerification;
    private Button btnDevProcess;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);

        logo = (ImageView)findViewById(R.id.imageLogo);
        imageMainScreen = (ImageView)findViewById(R.id.img_main_screen);
        layoutRoot = (ConstraintLayout)findViewById(R.id.layoutRoot);

        layoutRoot.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
                                       int oldTop, int oldRight, int oldBottom) {
                layoutButtons();
            }
        });

        createButtons();
    }

    private void createButtons() {

        btnSWRequirements = addButton("SW Requirements", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    startCircuitActivity("software_requirements");
                                                }
                                            });

        btnEffectiveManagement = addButton("Effective management", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    startCircuitActivity("effective_management");
                                                }
                                            });

        btnSatisfiedCustomer = addButton("Satisfied", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    startCircuitActivity("satisfied_customer");
                                                }
                                            });

        btnSuccessfulProjects = addButton("Successfull projects", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    startCircuitActivity("successful_projects");
                                                }
                                            });

        btnTeamWork = addButton("Teamwork", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    startCircuitActivity("teamwork");
                                                }
                                            });

        btnVerification = addButton("SW Verification", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    startCircuitActivity("sw_verification");
                                                }
                                            });

        btnDevProcess = addButton("Dev Process", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    startCircuitActivity("dev_process");
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

        setCoord(btnSWRequirements, 110, 265, 1020, 1140);
        setCoord(btnEffectiveManagement, 225, 360, 560, 680);
        setCoord(btnSatisfiedCustomer, 400, 985, 1475, 1650);
        setCoord(btnSuccessfulProjects, 825, 1000, 960, 1080);
        setCoord(btnTeamWork, 500, 670, 820, 940);
        setCoord(btnVerification, 480, 620, 400, 520);
        setCoord(btnDevProcess, 370, 520, 1125, 1250);

    }

    private void setCoord(TextView button, int l, int r, int t, int b){

        int imageTop = imageMainScreen.getTop();

        int imageWidth = imageMainScreen.getWidth();
        int imageHeight= imageMainScreen.getHeight();

        float xScale = ((float)imageWidth)/1080;
        float yScale = ((float)imageHeight)/1900;

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
        startActivity(new Intent(BusinessActivity.this, MainActivity.class));
    }
}
