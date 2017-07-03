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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;

/**
 * Created by User on 4/15/2017.
 */

public class ActivityThree extends AppCompatActivity {

    private TextView rssiTextView;
    private TextView transfer_rate_View;
    private TextView server_View;

    private Button sim_Button;
    private Button startRssi;

    private EditText sim_in;

    private ProgressBar pb;

    private Handler mThreadHandler;
    private HandlerThread mThread;

    //首先取得Wi-Fi服務控制Manager
    private WifiManager mWifiManager;
    private List<ScanResult> scanResult;
    private List<WifiConfiguration> wifiConfigurations;
    private List<WifiConfiguration> configs;
    private WifiInfo info;

    static String busyornot = "";

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

        // [START subscribe_topics]
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        // [END subscribe_topics]
        System.out.println("topic?");


        rssiTextView = (TextView)findViewById(R.id.rssiShow);
        transfer_rate_View = (TextView)findViewById(R.id.rateShow);
        server_View = (TextView)findViewById(R.id.serverbusy);



        transfer_rate_View.setText("上下載速度: "+Math.round(MyFirebaseMessagingService.transfer_rate_bit)+"Mb/s");

        startRssi = (Button)findViewById(R.id.buttonRssi);
        sim_Button = (Button)findViewById(R.id.simulatebutton);
        sim_in = (EditText) findViewById(R.id.sim_in);

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

                        if (MyFirebaseMessagingService.transfer_rate_bit>MyFirebaseMessagingService.transfer_rate_threshold){
                            busyornot = "Good";
                        }
                        else if (mWifiManager.getConnectionInfo().getRssi()>MyFirebaseMessagingService.signal_rate_threshold){
                            busyornot = "Busy";
                        }
                        else{
                            busyornot = "Question";
                        }

                        server_View.setText("伺服器狀況: "+busyornot);

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

        sim_Button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                transfer_rate_View.setText("上下載速度: "+sim_in.getText().toString()+"Mb/s");
                MyFirebaseMessagingService.transfer_rate_bit = Double.parseDouble(sim_in.getText().toString());
            }
        });
    }
}
