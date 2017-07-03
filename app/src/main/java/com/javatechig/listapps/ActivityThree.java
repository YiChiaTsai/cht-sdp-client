package com.javatechig.listapps;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by User on 4/15/2017.
 */

public class ActivityThree extends AppCompatActivity {

    private TextView rssiTextView;
    private Button startRssi;
    private ProgressBar pb;

    private Handler mThreadHandler;
    private HandlerThread mThread;

    //首先取得Wi-Fi服務控制Manager
    private WifiManager mWifiManager;
    private List<ScanResult> scanResult;
    private List<WifiConfiguration> wifiConfigurations;
    private List<WifiConfiguration> configs;
    private WifiInfo info;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);

        TextView title = (TextView) findViewById(R.id.activityTitle3);
        title.setText("");

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.ic_arrow:
                        Intent intent0 = new Intent(ActivityThree.this, MainActivity.class);
                        startActivity(intent0);
                        break;

                    case R.id.ic_android:
                        Intent intent1 = new Intent(ActivityThree.this, ActivityOne.class);
                        startActivity(intent1);
                        break;

                    case R.id.ic_books:
                        Intent intent2 = new Intent(ActivityThree.this, ActivityTwo.class);
                        startActivity(intent2);
                        break;

                    case R.id.ic_center_focus:

                        break;

                    case R.id.ic_backup:
                        Intent intent4 = new Intent(ActivityThree.this, ActivityFour.class);
                        startActivity(intent4);
                        break;
                }


                return false;
            }
        });


        rssiTextView = (TextView)findViewById(R.id.rssiShow);
        startRssi = (Button)findViewById(R.id.buttonRssi);
        pb = (ProgressBar)findViewById(R.id.progressBarRssi);
        pb.setProgress(0);

        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        mWifiManager.setWifiEnabled(true);
        mWifiManager.startScan();

        mThread = new HandlerThread("getRssi");
        mThread.start();

        mThreadHandler=new Handler(){
            int i = 0;
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case 1:
                        rssiTextView.setText(Integer.toString(mWifiManager.getConnectionInfo().getRssi()));
                        pb.setProgress(mWifiManager.getConnectionInfo().getRssi()+100);
                        break;
                }
                super.handleMessage(msg);
            }
        };

        startRssi.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        while(true){
                            try{
                                Message msg = new Message();
                                msg.what = 1;
                                mThreadHandler.sendMessage(msg);
                                Thread.sleep(10);
                            }
                            catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });
    }
}
