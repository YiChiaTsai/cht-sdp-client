package com.javatechig.listapps;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;
import java.text.DecimalFormat;
import java.math.RoundingMode;

/**
 * Created by User on 4/15/2017.
 */

public class ActivityTwo extends AppCompatActivity {

    private TextView speedtest_inprogress;
    private ProgressBar speedtest_progressbar;
    private TextView speedtest_download;
    private TextView speedtest_upload;
    private ProgressBar speedtest_download_bar;
    private ProgressBar speedtest_upload_bar;

    private double transfer_rate_bit;
    private float speedtest_progressbar_percent;
    private float speedtest_progressbar_download;
    private float speedtest_progressbar_upload;
    private double final_download_rate;
    private double final_upload_rate;
    private Handler mUI_Handler;
    private Handler mThreadHandler;
    private HandlerThread mThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        speedtest_inprogress = (TextView)findViewById(R.id.speedtest_inprogress);
        speedtest_progressbar = (ProgressBar)findViewById(R.id.speedtest_progressbar);
        speedtest_download = (TextView)findViewById(R.id.speedtest_upload);
        speedtest_upload = (TextView)findViewById(R.id.speedtest_download);
        speedtest_download_bar = (ProgressBar)findViewById(R.id.speedtest_download_bar);
        speedtest_upload_bar = (ProgressBar)findViewById(R.id.speedtest_upload_bar);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.ic_arrow:
                        Intent intent0 = new Intent(ActivityTwo.this, MainActivity.class);
                        startActivity(intent0);
                        break;

                    case R.id.ic_android:
                        Intent intent1 = new Intent(ActivityTwo.this, ActivityOne.class);
                        startActivity(intent1);
                        break;

                    case R.id.ic_books:

                        break;

                    case R.id.ic_center_focus:
                        Intent intent3 = new Intent(ActivityTwo.this, ActivityThree.class);
                        startActivity(intent3);
                        break;

                    case R.id.ic_backup:
                        Intent intent4 = new Intent(ActivityTwo.this, ActivityFour.class);
                        startActivity(intent4);
                        break;
                }


                return false;
            }
        });

        final SpeedTestSocket speedTestSocket = new SpeedTestSocket();

        // add a listener to wait for speedtest completion and progress
        speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {
            int i = 0;
            @Override
            public void onCompletion(SpeedTestReport report) {
                if(i == 0){
                    final_download_rate = report.getTransferRateBit().doubleValue()/(1024*1024);
                    speedTestSocket.startUpload("http://spthc1.taiwanmobile.com/speedtest/upload.php", 1000000);
                } else {
                    final_upload_rate = report.getTransferRateBit().doubleValue()/(1024*1024);;
                }
                i++;

                // called when download/upload is complete
                System.out.println("[COMPLETED] rate in octet/s : " + report.getTransferRateOctet());
                System.out.println("[COMPLETED] rate in bit/s   : " + report.getTransferRateBit());
            }

            @Override
            public void onError(SpeedTestError speedTestError, String errorMessage) {
                // called when a download/upload error occur
                System.out.println("[ERROR]  " + errorMessage);
            }

            @Override
            public void onProgress(float percent, SpeedTestReport report) {
                // called to notify download/upload progress
                System.out.println("[PROGRESS] progress : " + percent + "%");
                System.out.println("[PROGRESS] rate in octet/s : " + report.getTransferRateOctet());
                System.out.println("[PROGRESS] rate in bit/s   : " + report.getTransferRateBit());
                MyFirebaseMessagingService.transfer_rate_bit = report.getTransferRateBit().doubleValue()/(1024*1024); // bit to Mb
                transfer_rate_bit = report.getTransferRateBit().doubleValue()/(1024*1024);
                speedtest_progressbar_percent = percent;

                if(i == 0){
                    speedtest_progressbar_download = percent;
                } else {
                    speedtest_progressbar_upload = percent;
                }
            }

            @Override
            public void onInterruption() {
                // triggered when forceStopTask is called
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                speedTestSocket.startDownload("http://spthc1.taiwanmobile.com/speedtest/random2000x2000.jpg");
            }
        }).start();

        mUI_Handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                switch (msg.what)
                {
                    case 1:
                        DecimalFormat df = new DecimalFormat("#.##");
                        df.setRoundingMode(RoundingMode.CEILING);
                        speedtest_inprogress.setText(df.format(transfer_rate_bit));
                        speedtest_progressbar.setProgress((int)speedtest_progressbar_percent);

                        speedtest_download.setText(df.format(final_download_rate));
//                        speedtest_download_bar.setProgress((int)speedtest_progressbar_download);
                        speedtest_upload.setText(df.format(final_upload_rate));
//                        speedtest_download_bar.setProgress((int)speedtest_progressbar_upload);
                        break;
                }
            }
        };

        mThread = new HandlerThread("getBit");
        mThread.start();

        mThreadHandler=new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(1000);
                    while (true){
                        Message msg = new Message();
                        msg.what = 1;
                        mUI_Handler.sendMessage(msg);
                        Thread.sleep(200);
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

}
