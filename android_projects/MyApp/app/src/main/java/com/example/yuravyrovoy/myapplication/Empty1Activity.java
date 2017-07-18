package com.example.yuravyrovoy.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class Empty1Activity extends AppCompatActivity {

    private static int nCounter = 0;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty1);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_STATE);

        id = nCounter++;

        String sState = ( (message != null) && (message.isEmpty() == false) ) ?
                        message + " + E1." + Integer.toString(id) :
                        "E1." + Integer.toString(id);

        TextView txtMessage = (TextView)findViewById(R.id.textEmpty1Message);
        txtMessage.setText(sState);
    }

    /** Called when the user taps the Show Empty 2 button */
    public void ShowEmpty2(View view){
        Intent intent = new Intent(this, Empty2Activity.class);
        String sMessage = "E1." + Integer.toString(id);
        intent.putExtra(MainActivity.EXTRA_STATE, sMessage);
        startActivity(intent);
    }


    /** Called when the user taps the Show Empty 1 button */
    public void ShowEmpty1(View view){
        CheckBox checkBox = (CheckBox)findViewById(R.id.checkBox1);
        boolean bNewDoc = checkBox.isChecked();

        Intent intent = new Intent(this, Empty1Activity.class);
        String sMessage = "E1." + Integer.toString(id);
        intent.putExtra(MainActivity.EXTRA_STATE, sMessage);

        if(bNewDoc == true){
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        }


        startActivity(intent);
    }

    private static int nRep = 0;

    @Override
    protected void onNewIntent (Intent intent){
        String sMessage = intent.getStringExtra(MainActivity.EXTRA_STATE);

        TextView txtState = (TextView)findViewById(R.id.textEmpty1Message);

        String sNewMessage = "Re[" + Integer.toString(nRep++) + "]: " + sMessage;

        txtState.setText(sNewMessage);
    }

}
