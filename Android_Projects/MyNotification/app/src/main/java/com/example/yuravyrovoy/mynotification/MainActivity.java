package com.example.yuravyrovoy.mynotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v) {
        addNotificationAndUpdateSummaries();
    }


    private void addNotificationAndUpdateSummaries() {
        // [BEGIN create_notification]

        NotificationManager notificationManager = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);

        // Create a Notification and notify the system.
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_notification)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.sample_notification_content))
                .setAutoCancel(true);

        final Notification notification = builder.build();
        notificationManager.notify(222, notification);
        // [END create_notification]
    }
}
