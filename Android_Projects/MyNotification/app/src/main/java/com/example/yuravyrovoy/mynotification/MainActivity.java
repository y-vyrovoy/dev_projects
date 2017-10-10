package com.example.yuravyrovoy.mynotification;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {

    private static final int NOTIFICATION_ID = 834;

    private static final int [] arPatric = new int[]{R.drawable.patric_star_1,
                                                        R.drawable.patric_star_2,
                                                        R.drawable.patric_star_3};

    private static final int [] arKrabs = new int[]{R.drawable.mr_krabs_1,
                                                        R.drawable.mr_krabs_2};

    private EditText _editPatric = null;
    private EditText _editKrabs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _editPatric = (EditText)findViewById(R.id.editText1);
        _editKrabs = (EditText)findViewById(R.id.editText2);
    }

    public void onClick(View v) {
        createNotofication();
    }


    public void createNotofication(){

        int numPatric = Integer.parseInt(_editPatric.getText().toString());
        int numKrabs = Integer.parseInt(_editKrabs.getText().toString());

        //YoService.setNotificationIcons(this, numPatric, numKrabs);

        int icPatric;
        int icKrabs;

        if((numPatric >= 0) || (numPatric < arPatric.length)){
            icPatric = arPatric[numPatric];
        }
        else {
            icPatric = arPatric[0];
        }

        if((numKrabs >= 0) || (numKrabs < arKrabs.length)){
            icKrabs = arKrabs[numKrabs];
        }
        else {
            icKrabs = arKrabs[0];
        }

        Intent notificationIntent = new Intent(this, SecondActivity.class);

        NotificationUtils utilsNotification = new NotificationUtils(this);
        utilsNotification.createChannels();

        Notification.Builder builder
                = utilsNotification.getChannelNotification(NotificationUtils.ANDROID_CHANNEL_ID,
                                                getResources().getString(R.string.str_not_title),
                                                getResources().getString(R.string.str_notification),
                                                getResources().getString(R.string.str_ticker),
                                                notificationIntent,
                                                icPatric,
                                                icKrabs);

        utilsNotification.getManager().notify(NOTIFICATION_ID, builder.build());

    }

    public void onClick22(View v) {
        startService(new Intent(this, YoService.class));
    }

    public void onClick33(View v) {
        YoService.callSayHello(this);
    }

}
