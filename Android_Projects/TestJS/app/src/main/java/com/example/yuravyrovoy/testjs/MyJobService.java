package com.example.yuravyrovoy.testjs;

import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public class MyJobService extends JobService {

    private static final String TAG = MyJobService.class.getSimpleName();
    private static final String SERVICE_THREAD_NAME = "Thread [" + TAG + "]";

    public static final String MSG_CANCEL_RESCHEDULING = "cancel_rescheduling [" + TAG + "]";
    public static final String PRM_RESTART = "restart [" + TAG + "]";

    private static final int CMD_CANCEL_RESCHEDULING = 1;

    private JobParameters mParameters;


    public MyJobService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "Periodic Service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent.getBooleanExtra(MSG_CANCEL_RESCHEDULING, false) == true) {

            //Message msgWait = Message.obtain(null, CMD_CANCEL_RESCHEDULING, 0, 0);
            ///mServiceHandler.sendMessage(msgWait);

             Log.i(TAG, "rescheduling canceled by user");
        }

        return START_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        mParameters = jobParameters;

        LogSaver logSaver = new LogSaver();
        new Thread(logSaver).run();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }



    // nested classed definition

    private class LogSaver implements Runnable {

        @Override
        public void run(){


            try {

                String sMessage = "\r\n" + android.text.format.DateFormat.format("dd.MM.yyyy kk:mm:ss", Calendar.getInstance());

                File myFile = new File(getExternalFilesDir(null), "JobScheduler.log");

                BufferedWriter bw = new BufferedWriter(new FileWriter(myFile, true));
                bw.append(sMessage);
                bw.close();

                broadCastToMediaScanner(MyJobService.this, myFile);

                Toast.makeText(MyJobService.this, "Log Complete", Toast.LENGTH_SHORT).show();

            } catch (FileNotFoundException e) {
                sendNotification("FileNotFoundException");
                e.printStackTrace();

            } catch (IOException e) {
                sendNotification("IOException");
                e.printStackTrace();
            }


            Log.i(TAG, "JobService is always on service!");
            jobFinished(mParameters, true);
        }

    }

    public static void broadCastToMediaScanner(Context context, File file) {

        Uri contentUri = Uri.fromFile(file);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    private void sendNotification(String sMessage){

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.ic_dialog_alert)
                        .setContentTitle("JobScheduler")
                        .setContentText(sMessage);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mBuilder.setContentIntent(null);

        mNotificationManager.notify(11, mBuilder.build());
    }

}
