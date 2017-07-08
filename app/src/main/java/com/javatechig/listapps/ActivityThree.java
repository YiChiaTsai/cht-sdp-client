package com.javatechig.listapps;

import android.content.Context;
import android.content.Intent;
import android.net.TrafficStats;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import java.util.UUID;

import android.content.Context;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

/**
 * Created by User on 4/15/2017.
 */

public class ActivityThree extends AppCompatActivity {

    private String HOST = "192.168.43.176";
    private Handler mHandler = new Handler();
    String congestionSentToServer = "";
    private int CongestionCount = 0;
    private JSONObject jsonObjCongestion = new JSONObject();
    private JSONArray jsonArrCongestion = new JSONArray();

    private TelephonyManager telephonyManager;
    private GsmCellLocation cellLocation;
    private int cid;
    private int lac;

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

    private static String deviceId = "5341231";
    public static String getdeviceId(){
        return deviceId;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);

        mHandler.postDelayed(mRunnable, 1000);

        try {
            deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        cellLocation = (GsmCellLocation) telephonyManager.getCellLocation();

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

    private final Runnable mRunnable = new Runnable() {
        public void run() {

            System.out.println(getCurrentTime());

            if (Integer.parseInt(getSec()) % 10 == 0) { //getMin().toString().equals("00") && getSec().toString().equals("00") getMin().toString().equals("00") && getMin().toString().equals("00") && Integer.parseInt(getSec()) % 10 == 0   getSec().toString().equals("00")     getMin().toString().equals("00") && getSec().toString().equals("00")
                try {
                    // Here we convert Java Object to JSON
                    CongestionCount++;
                    JSONObject congestionObj = new JSONObject();
                    congestionObj.put("serial_id", CongestionCount);
                    congestionObj.put("mac_id", deviceId);
                    congestionObj.put("date", getDate());
                    congestionObj.put("clock", getClock());
                    congestionObj.put("congestion", busyornot);
                    congestionObj.put("cell_id", cellLocation.getCid());
                    congestionObj.put("lac", cellLocation.getLac());

                    jsonArrCongestion.put(congestionObj);
                    jsonObjCongestion.put("congestiondatabases", jsonArrCongestion);

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }

            if (Integer.parseInt(getSec()) % 10 == 0) {
                    RequestParams paramsCongestion = new RequestParams();
                    congestionSentToServer = jsonObjCongestion.toString();
//                    congestionSentToServer = calculateCongestion();
                    System.out.println(congestionSentToServer);

                    // 送Congestion的通道,  要送的東西放在congestionSentToServer , 格式幫忙弄成json , 第一格放MAC ID ,  第二格當下時間 第三四格擁塞與否
                    paramsCongestion.put("congestion", congestionSentToServer);
                    passToServer(paramsCongestion, "CHT-congestion");
            }

            mHandler.postDelayed(mRunnable, 1000);
        }
    };

//    private String calculateCongestion() {
//        String serial_id = "";
//        String mac_id = "";
//        String enodeb_id = "";
//        String date = "";
//        String clock = "";
//        String congestion = "";
//        String cell_id = "";
//        String lac = "";
//
//        try {
//            JSONArray congestiondatabases = jsonObjCongestion.getJSONArray("congestiondatabases");
//
//            for (int i = 0; i < congestiondatabases.length(); i++) {
//                JSONObject c = congestiondatabases.getJSONObject(i);
//
//                serial_id = c.getString("macid");
//                mac_id = c.getString("id");
//                enodeb_id = c.getString("enodeb_id");
//                date = c.getString("date");
//                clock = c.getString("clock");
//                congestion = c.getString("congestion");
//                cell_id = c.getString("cell_id");
//                lac = c.getString("lac");
//            }
//
//        } catch (JSONException ex) {
//            ex.printStackTrace();
//        }
//
//        return jsonObjCongestion.toString();
//    }

    public static String getCurrentTime() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        return formattedDate;
    }

    public static String getSec() {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("ss");
        String formattedDate = df.format(c.getTime());

        return formattedDate;
    }

    public static String getDate() {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());

        return formattedDate;
    }

    public static String getClock() {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        return formattedDate;
    }

    //傳至Server
    public void passToServer(RequestParams params, String tube){ //tube: CHT-flow 送流量   tube:CHT-feature 送feature
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://" + HOST + ":8080/CHTServer/hello/"+tube;
        System.out.println(url);
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                CharSequence cs = new String(bytes);
                Toast toast = Toast.makeText(getApplicationContext(), cs, Toast.LENGTH_SHORT);    //toast 會閃現    用textView來接
                toast.show();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.e("InvokeWS", Integer.toString(i));
                if (bytes != null) {
                    CharSequence cs = new String(bytes);
                    Toast toast = Toast.makeText(getApplicationContext(), cs, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }
}
