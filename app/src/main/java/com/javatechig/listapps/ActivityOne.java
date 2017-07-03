package com.javatechig.listapps;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.view.View;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import android.app.AlertDialog;
import android.app.Application;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.health.UidHealthStats;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.loopj.android.http.*;

import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;

import cz.msebera.android.httpclient.Header;

import java.io.*;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.*;

import static com.google.android.gms.internal.zzs.TAG;

public class ActivityOne extends AppCompatActivity implements ListView.OnItemClickListener {

    private String HOST = "192.168.43.176";

    private boolean getService = false;        //是否已開啟定位服務
    private LocationManager mLocationManager;               //宣告定位管理控制
    private String sLongitude = "";
    private String sLatitude = "";
    private double latitude = 0;
    private double longitude = 0;
    private int LngLatCount = 0;
    private String locationSummary = "";

    private PackageManager packageManager = null;
    private List<ApplicationInfo> applist = null;
    private ApplicationAdapter listadaptor = null;

    private Handler mHandler = new Handler();
    private long mStartTotalRX = 0;
    private long mStartTotalTX = 0;

    private JSONObject jsonObj = new JSONObject();
    private JSONArray jsonArr = new JSONArray();
    private int jsonArrId = 0;

    private JSONObject jsonObjFeature = new JSONObject();
    private JSONArray jsonArrFeature = new JSONArray();
    private JSONArray jsonArrApp = new JSONArray();
    private int jsonArrIdFeature = 0;

    private JSONObject jsonObjRec = new JSONObject();
    private JSONArray jsonArrRec = new JSONArray();

    private int useOrNot = 0; //default is 0, true is 1, false is 2.

    private Intent intent;
    private Button testButton;

    String trafficDataInfo = "";
    String infoSentToServer = "";
    String flowSentToServer = "";
    String recSentToServer = "";

    private double dataUsageOfDay = 0;
    private double dataUsageOfMorning = 0;
    private double dataUsageOfAfternoon = 0;
    private double dataUsageOfEvening = 0;
    private double dataUsageOfMidnight = 0;
    private int limitOfDay = -1; //Exceed 150MB, set it as 1. Otherwise, set it as 0;
    private int limitOfMorning = -1;
    private int limitOfAfternoon = -1;
    private int limitOfEvening = -1;
    private int limitOfMidnight = -1;
    private double exceedOfDay = 0;
    private double exceedOfMorning = 0;
    private double exceedOfAfternoon = 0;
    private double exceedOfEvening = 0;
    private double exceedOfMidnight = 0;
    private String dataUsageSummary = "";
    private static String deviceId = "5341231";

    public static String getdeviceId(){
        return deviceId;
    }

    final private double thresholdOfDay = 150 * 1024;
    final private double thresholdOfMorning = 150 * 1024;
    final private double thresholdOfAfternoon = 150 * 1024;
    final private double thresholdOfEvening = 150 * 1024;
    final private double thresholdOfMidnight = 150 * 1024;
    private int[] arrayUid = new int[]{10118, 10134, 10110, 10138, 10174, 10175};
    private String[] arrayApp = new String[]{"Facebook", "LINE", "YouTube", "VoiceTube", "ClashofClans", "Knowledge"};
    private double[] dataUsageOfApp = new double[6];
    private double[] mStartAppRX = new double[6];
    private double[] mStartAppTX = new double[6];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.ic_arrow:
                        Intent intent0 = new Intent(ActivityOne.this, MainActivity.class);
                        intent0.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent0);
                        break;

                    case R.id.ic_android:

                        break;

                    case R.id.ic_books:
                        Intent intent2 = new Intent(ActivityOne.this, ActivityTwo.class);
                        startActivity(intent2);
                        break;

                    case R.id.ic_center_focus:
                        Intent intent3 = new Intent(ActivityOne.this, ActivityThree.class);
                        startActivity(intent3);
                        break;

                    case R.id.ic_backup:
                        Intent intent4 = new Intent(ActivityOne.this, ActivityFour.class);
                        startActivity(intent4);
                        break;
                }


                return false;
            }
        });

        //取得系統定位服務
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        packageManager = getPackageManager();
        ((ListView)findViewById(R.id.list)).setOnItemClickListener(this);
        new ActivityOne.LoadApplications().execute();

        try {
            deviceId = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
            System.out.println("deviceID:"+deviceId);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        mStartTotalRX = TrafficStats.getTotalRxBytes();
        mStartTotalRX = TrafficStats.getTotalTxBytes();

        for (int i = 0; i < 6; i++) {
            mStartAppRX[i] = TrafficStats.getUidRxBytes(arrayUid[i]);
            mStartAppTX[i] = TrafficStats.getUidTxBytes(arrayUid[i]);
        }

        if (mStartTotalRX == TrafficStats.UNSUPPORTED || mStartTotalRX == TrafficStats.UNSUPPORTED) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Uh Oh!");
            alert.setMessage("Your device does not support traffic stat monitoring.");
            alert.show();
        } else {
            mHandler.postDelayed(mRunnable, 1000);
        }

        testButton = (Button) findViewById(R.id.testme);
        testButton.setOnClickListener(startClickListener);

//        intent = new Intent(ActivityOne.this,DialogService.class);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        } else {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = true;

        switch (item.getItemId()) {
            case R.id.menu_settings_ask_time: {
                displayTimeDialog();
                break;
            }
            case R.id.menu_settings_ask_certainappuid: {
                displayUidDialog();
                break;
            }
            case R.id.menu_settings_ask_certainappdata: {
//				readFromFile();
                displayDataDialog();
                break;
            }
            case R.id.menu_settings_ask_map: {
                displayMapDialog();
                break;
            }
            default: {
                result = super.onOptionsItemSelected(item);

                break;
            }
        }

        return result;
    }

    public static String getCurrentTime() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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

    public static String getHr() {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("HH");
        String formattedDate = df.format(c.getTime());

        return formattedDate;
    }

    public static String getMin() {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("mm");
        String formattedDate = df.format(c.getTime());

        return formattedDate;
    }

    public static String getSec() {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("ss");
        String formattedDate = df.format(c.getTime());

        return formattedDate;
    }

    public static String getYear() {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy");
        String formattedDate = df.format(c.getTime());

        return formattedDate;
    }

    public static String getMonth() {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("MM");
        String formattedDate = df.format(c.getTime());

        return formattedDate;
    }

    public static String getDay() {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd");
        String formattedDate = df.format(c.getTime());

        return formattedDate;
    }

    private void displayTimeDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.ask_time_title));
        builder.setMessage(getCurrentTime());

        builder.setPositiveButton("Know More", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/YiChiaTsai/cht-sdp"));
                startActivity(browserIntent);
                dialog.cancel();
            }
        });
        builder.setNegativeButton("No Thanks!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void displayUidDialog() {
        String Uid = "";
        for (int i = 0; i < 6; i++) {
            Uid += arrayApp[i] + ": " + arrayUid[i] + "\n";
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.ask_certainappuid_title));
        builder.setMessage(Uid);

        builder.show();
    }

    private String calculateFeat() {
        dataUsageOfDay = 0;
        dataUsageOfMorning = 0;
        dataUsageOfAfternoon = 0;
        dataUsageOfEvening = 0;
        dataUsageOfMidnight = 0;
        limitOfDay = -1; //Exceed 150MB, set it as 1. Otherwise, set it as 0;
        limitOfMorning = -1;
        limitOfAfternoon = -1;
        limitOfEvening = -1;
        limitOfMidnight = -1;
        exceedOfDay = 0;
        exceedOfMorning = 0;
        exceedOfAfternoon = 0;
        exceedOfEvening = 0;
        exceedOfMidnight = 0;
        String macid = "";
        dataUsageSummary = "";

        try {
            JSONArray databases = jsonObj.getJSONArray("databases");
//			JSONArray appdatabases = jsonObj.getJSONArray("appdatabases");
            for (int i = 0; i < databases.length(); i++) {
                JSONObject c = databases.getJSONObject(i);

                macid = c.getString("macid");
                String id = c.getString("id");
                String date = c.getString("date");
                String clock = c.getString("clock");
                String hr = c.getString("hr");
                String datausageRx = c.getString("datausageRx");
                String datausageTx = c.getString("datausageTx");
                String datausageSum = c.getString("datausageSum");
                String datausageRxNow = c.getString("datausageRxNow");
                String datausageTxNow = c.getString("datausageTxNow");
                String datausageSumNow = c.getString("datausageSumNow");

                if (hr.toString().equals("07") || hr.toString().equals("08") || hr.toString().equals("09") || hr.toString().equals("10") || hr.toString().equals("11")) {
                    dataUsageOfMorning += Double.parseDouble(datausageSumNow);
                }
                if (hr.toString().equals("12") || hr.toString().equals("13") || hr.toString().equals("14") || hr.toString().equals("15") || hr.toString().equals("16")) {
                    dataUsageOfAfternoon += Double.parseDouble(datausageSumNow);
                }
                if (hr.toString().equals("17") || hr.toString().equals("18") || hr.toString().equals("19") || hr.toString().equals("20") || hr.toString().equals("21") || hr.toString().equals("22") || hr.toString().equals("23")) {
                    dataUsageOfEvening += Double.parseDouble(datausageSumNow);
                }
                if (hr.toString().equals("00") || hr.toString().equals("01") || hr.toString().equals("02") || hr.toString().equals("03") || hr.toString().equals("04") || hr.toString().equals("05") || hr.toString().equals("06")) {
                    dataUsageOfMidnight += Double.parseDouble(datausageSumNow);
                }

            }
            dataUsageOfDay = dataUsageOfMorning + dataUsageOfAfternoon + dataUsageOfEvening + dataUsageOfMidnight;

            if (dataUsageOfDay > thresholdOfDay) {
                limitOfDay = 1;
                exceedOfDay = dataUsageOfDay - thresholdOfDay;
            } else {
                limitOfDay = 0;
                exceedOfDay = dataUsageOfDay - thresholdOfDay;
            }
            if (dataUsageOfMorning > thresholdOfMorning) {
                limitOfMorning = 1;
                exceedOfMorning = dataUsageOfMorning - thresholdOfMorning;
            } else {
                limitOfMorning = 0;
                exceedOfMorning = dataUsageOfMorning - thresholdOfMorning;
            }
            if (dataUsageOfAfternoon > thresholdOfAfternoon) {
                limitOfAfternoon = 1;
                exceedOfAfternoon = dataUsageOfAfternoon - thresholdOfAfternoon;
            } else {
                limitOfAfternoon = 0;
                exceedOfAfternoon = dataUsageOfAfternoon - thresholdOfAfternoon;
            }
            if (dataUsageOfEvening > thresholdOfEvening) {
                limitOfEvening = 1;
                exceedOfEvening = dataUsageOfEvening - thresholdOfEvening;
            } else {
                limitOfEvening = 0;
                exceedOfEvening = dataUsageOfEvening - thresholdOfEvening;
            }
            if (dataUsageOfMidnight > thresholdOfMidnight) {
                limitOfMidnight = 1;
                exceedOfMidnight = dataUsageOfMidnight - thresholdOfMidnight;
            } else {
                limitOfMidnight = 0;
                exceedOfMidnight = dataUsageOfMidnight - thresholdOfMidnight;
            }

            dataUsageSummary += "MACID=" + macid + "\nDate=" + getDate()
                    + "\nDataUsageOfDay=" + dataUsageOfDay + "KB" + "\nDataUsageOfMorning=" + dataUsageOfMorning + "KB" + "\nDataUsageOfAfternoon=" + dataUsageOfAfternoon + "KB"
                    + "\nDataUsageOfEvening=" + dataUsageOfEvening + "KB" + "\nDataUsageOfMidnight=" + dataUsageOfMidnight + "KB"
                    + "\n\nLimitOfDay=" + limitOfDay + "\nLimitOfMorning=" + limitOfMorning + "\nLimitOfAfternoon=" + limitOfAfternoon
                    + "\nLimitOfEvening=" + limitOfEvening + "\nLimitOfMidnight=" + limitOfMidnight
                    + "\n\nExceedOfDay=" + exceedOfDay + "KB" + "\nExceedOfMorning=" + exceedOfMorning + "KB" + "\nExceedOfAfternoon=" + exceedOfAfternoon + "KB"
                    + "\nExceedOfEvening=" + exceedOfEvening + "KB" + "\nExceedOfMidnight=" + exceedOfMidnight + "KB"
                    + "\n\n" + arrayApp[0] + "-" + arrayUid[0] + "=" + dataUsageOfApp[0] + "KB" + " " + arrayApp[1] + "-" + arrayUid[1] + "=" + dataUsageOfApp[1] + "KB"
                    + " " + arrayApp[2] + "-" + arrayUid[2] + "=" + dataUsageOfApp[2] + "KB" + " " + arrayApp[3] + "-" + arrayUid[3] + "=" + dataUsageOfApp[3] + "KB"
                    + " " + arrayApp[4] + "-" + arrayUid[4] + "=" + dataUsageOfApp[4] + "KB" + " " + arrayApp[5] + "-" + arrayUid[5] + "=" + dataUsageOfApp[5] + "KB\n";
            System.out.println(dataUsageSummary);

            JSONObject featureObj = new JSONObject();
            featureObj.put("macid", deviceId);
            featureObj.put("id", jsonArrIdFeature);
            featureObj.put("date", getDate()); // Set the first name/pair
            featureObj.put("clock", getClock());
            featureObj.put("dataUsageOfDay", dataUsageOfDay);
            featureObj.put("dataUsageOfMorning", dataUsageOfMorning);
            featureObj.put("dataUsageOfAfternoon", dataUsageOfAfternoon);
            featureObj.put("dataUsageOfEvening", dataUsageOfEvening);
            featureObj.put("dataUsageOfMidnight", dataUsageOfMidnight);
            featureObj.put("exceedOfDay", exceedOfDay);
            featureObj.put("exceedOfMorning", exceedOfMorning);
            featureObj.put("exceedOfAfternoon", exceedOfAfternoon);
            featureObj.put("exceedOfEvening", exceedOfEvening);
            featureObj.put("exceedOfMidnight", exceedOfMidnight);
            featureObj.put("limitOfDay", limitOfDay);
            featureObj.put("limitOfMorning", limitOfMorning);
            featureObj.put("limitOfAfternoon", limitOfAfternoon);
            featureObj.put("limitOfEvening", limitOfEvening);
            featureObj.put("limitOfMidnight", limitOfMidnight);

            jsonArrFeature.put(0, featureObj);
            jsonObjFeature.put("featuredatabases", jsonArrFeature);

            jsonArrIdFeature++;
//            System.out.println(jsonObjFeature.toString());

        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        return jsonObjFeature.toString();
    }

    private void displayDataDialog() {

        String showMessage = calculateFeat();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.ask_appdata_title));
        builder.setMessage("Feature Summary:\n" + dataUsageSummary + "\n\nJsonFormat:\n" + showMessage);

        builder.show();
    }

    private void displayMapDialog() {

        String showMessage = calculateRec();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.ask_map_title));
        builder.setMessage("Location Summary:\n" + locationSummary + "\n\nJsonFormat:\n" + showMessage);

        builder.show();
    }

    private String calculateRec() {
        String macid = "";
        String id = "";
        String date = "";
        String clock = "";
        String longitude = "";
        String latitude = "";
        locationSummary = "";

        try {
            JSONArray recdatabases = jsonObjRec.getJSONArray("recdatabases");

            for (int i = 0; i < recdatabases.length(); i++) {
                JSONObject c = recdatabases.getJSONObject(i);

                macid = c.getString("macid");
                id = c.getString("id");
                date = c.getString("date");
                clock = c.getString("clock");
                longitude = c.getString("longitude");
                latitude = c.getString("latitude");

                locationSummary += "MACID=" + macid + "\nId=" + id + "\nDate=" + date + "\nClock=" + clock + "\nLongitude=" + longitude + "\nLatitude=" + latitude + "\n";
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        System.out.println(locationSummary);

        return jsonObjRec.toString();
    }

    private void locationServiceInitial() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LngLatCount++;

        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);    //使用GPS定位座標

        longitude = location.getLongitude();
        sLongitude = "Initial-Lng-" + LngLatCount + ": " + String.valueOf(longitude);
        latitude = location.getLatitude();
        sLatitude = "Initial-Lat-" + LngLatCount + ": " + String.valueOf(latitude);

        System.out.println("GreatLocation: " + sLongitude + ", " + sLatitude);
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            LngLatCount++;
            longitude = location.getLongitude();
            sLongitude = "Listener-Lng" + String.valueOf(longitude);
            latitude = location.getLatitude();
            sLatitude = "Listener-Lat" + String.valueOf(latitude);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };



    private Long getTotalBytesManual(int localUid) {

        File dir = new File("/proc/uid_stat/");
        String[] children = dir.list();
        if (!Arrays.asList(children).contains(String.valueOf(localUid))) {
            return 0L;
        }
        File uidFileDir = new File("/proc/uid_stat/" + String.valueOf(localUid));
        File uidActualFileReceived = new File(uidFileDir, "tcp_rcv");
        File uidActualFileSent = new File(uidFileDir, "tcp_snd");

        String textReceived = "0";
        String textSent = "0";

        try {
            BufferedReader brReceived = new BufferedReader(new FileReader(uidActualFileReceived));
            BufferedReader brSent = new BufferedReader(new FileReader(uidActualFileSent));
            String receivedLine;
            String sentLine;

            if ((receivedLine = brReceived.readLine()) != null) {
                textReceived = receivedLine;
            }
            if ((sentLine = brSent.readLine()) != null) {
                textSent = sentLine;
            }

        } catch (IOException e) {

        }
        return Long.valueOf(textReceived).longValue() + Long.valueOf(textReceived).longValue();

    }

    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
        for (ApplicationInfo info : list) {
            try {
                if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                    applist.add(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return applist;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ApplicationInfo app = applist.get(position);
        try {
            Intent intent = packageManager
                    .getLaunchIntentForPackage(app.packageName);

            if (null != intent) {
                showDialog();
                if (useOrNot == 1)
                    startActivity(intent);

            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(ActivityOne.this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(ActivityOne.this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private class LoadApplications extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress = null;

        @Override
        protected Void doInBackground(Void... params) {
            applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
            listadaptor = new ApplicationAdapter(ActivityOne.this,
                    R.layout.snippet_list_row, applist);

            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void result) {
            ((ListView)findViewById(R.id.list)).setAdapter(listadaptor);
            progress.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(ActivityOne.this, null,
                    "Loading application info...");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    private final Runnable mRunnable = new Runnable() {
        public void run() {

            int inthr1 = -1;
            int inthr2 = -1;
            String intervalhr = "";
            if (getHr().equals("00")) {
                inthr1 = 23;
                inthr2 = Integer.parseInt(getHr());
            } else {
                inthr1 = Integer.parseInt(getHr()) - 1;
                inthr2 = Integer.parseInt(getHr());
            }
            intervalhr = inthr1 + "~" + inthr2;

            System.out.println(getCurrentTime());

            long rxBytes = (TrafficStats.getTotalRxBytes() - mStartTotalRX) / 1024; //1048576 = 1024*1024 = 2^20
            long txBytes = (TrafficStats.getTotalTxBytes() - mStartTotalTX) / 1024;
            long sumBytes = rxBytes + txBytes;

            for (int i = 0; i < 6; i++) {
                dataUsageOfApp[i] = getTotalBytesManual(arrayUid[i]) / 1024;
            }

            if (Integer.parseInt(getSec()) % 10 == 0) { //Integer.parseInt(getSec()) % 10 == 0 getMin().toString().equals("00") && getSec().toString().equals("00") getMin().toString().equals("00") && getHr().toString().equals("23") && getMin().toString().equals("59") && getSec().toString().equals("00")
                try {
                    for (int i = 0; i < 6; i++) {
                        JSONObject appObj = new JSONObject();
                        appObj.put("appid", arrayUid[i]);
                        appObj.put("application", arrayApp[i]);
                        appObj.put("date", getDate()); // Set the first name/pair
                        appObj.put("clock", getClock());
                        appObj.put("datausageSum", dataUsageOfApp[i]);

                        jsonArrApp.put(i, appObj);
                        jsonObjFeature.put("appdatabases", jsonArrApp);
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }

            //Every hour (10 secs), we call the DATAUSAGE function and send it to Server
            if (Integer.parseInt(getSec()) % 10 == 0) { //getMin().toString().equals("00") && getSec().toString().equals("00") getMin().toString().equals("00") && getMin().toString().equals("00") && Integer.parseInt(getSec()) % 10 == 0   getSec().toString().equals("00")     getMin().toString().equals("00") && getSec().toString().equals("00")

                try {
//					JSONObject jsonLastRecord = new JSONObject(readFromFile());
//
//					writeToFile(jsonObj.toString());

                    // Here we convert Java Object to JSON
                    JSONObject pnObj = new JSONObject();
                    pnObj.put("macid", deviceId);
                    pnObj.put("id", jsonArrId);
                    pnObj.put("date", getDate()); // Set the first name/pair
                    pnObj.put("clock", getClock());
                    pnObj.put("hr", getHr());
                    pnObj.put("hrintervalhr", intervalhr);
                    pnObj.put("datausageRx", rxBytes);
                    pnObj.put("datausageTx", txBytes);
                    pnObj.put("datausageSum", sumBytes);

                    jsonArr.put(pnObj);
                    jsonObj.put("databases", jsonArr);

                    JSONArray databasesTmp = jsonObj.getJSONArray("databases");
                    if (jsonArrId - 1 >= 0) {
                        JSONObject cTmp = databasesTmp.getJSONObject(jsonArrId);
                        JSONObject cPrevious = databasesTmp.getJSONObject(jsonArrId - 1);
                        cTmp.put("datausageRxNow", rxBytes - Integer.parseInt(cPrevious.getString("datausageRx")));
                        cTmp.put("datausageTxNow", txBytes - Integer.parseInt(cPrevious.getString("datausageTx")));
                        cTmp.put("datausageSumNow", sumBytes - Integer.parseInt(cPrevious.getString("datausageSum")));
                    } else {
                        JSONObject cTmp = databasesTmp.getJSONObject(jsonArrId);
                        cTmp.put("datausageRxNow", 0);
                        cTmp.put("datausageTxNow", 0);
                        cTmp.put("datausageSumNow", 0);
                    }

                    jsonArrId++;
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }

            if (Integer.parseInt(getSec()) % 10 == 0) { //getMin().toString().equals("00") && getSec().toString().equals("00") getMin().toString().equals("00") && getMin().toString().equals("00") && Integer.parseInt(getSec()) % 10 == 0   getSec().toString().equals("00")     getMin().toString().equals("00") && getSec().toString().equals("00")
                try {
                    // Here we convert Java Object to JSON
                    JSONObject recObj = new JSONObject();
                    recObj.put("macid", deviceId);
                    recObj.put("id", LngLatCount);
                    recObj.put("date", getDate()); // Set the first name/pair
                    recObj.put("clock", getClock());
                    recObj.put("longitude", sLongitude);
                    recObj.put("latitude", sLatitude);

                    jsonArrRec.put(recObj);
                    jsonObjRec.put("recdatabases", jsonArrRec);

                    locationServiceInitial();
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }


            if (Integer.parseInt(getSec()) % 10 == 0) {
                try {
                    JSONArray databases = jsonObj.getJSONArray("databases");

                    for (int i = 0; i < databases.length(); i++) {
                        JSONObject c = databases.getJSONObject(i);

                        String macid = c.getString("macid");
                        String id = c.getString("id");
                        String date = c.getString("date");
                        String clock = c.getString("clock");
                        String hr = c.getString("hr");
                        String hrintervalhr = c.getString("hrintervalhr");
                        String datausageRx = c.getString("datausageRx");
                        String datausageTx = c.getString("datausageTx");
                        String datausageSum = c.getString("datausageSum");
                        String datausageRxNow = c.getString("datausageRxNow");
                        String datausageTxNow = c.getString("datausageTxNow");
                        String datausageSumNow = c.getString("datausageSumNow");

                        System.out.println("List: macid= " + macid + " id=" + id + " " + date + " " + clock + " intervalhr=" + intervalhr + " Rx=" + datausageRx + "KB Tx=" + datausageTx + "KB Sum=" + datausageSum + "KB RxNow=" + datausageRxNow + "KB TxNow=" + datausageTxNow + "KB SumNow=" + datausageSumNow);
                    }

//							if(getSec().toString().equals("00")) { //getMin().toString().equals("00") && getSec().toString().equals("00")
                    RequestParams paramsFlow = new RequestParams();
                    flowSentToServer = jsonObj.toString();
                    System.out.println(flowSentToServer);

                    // 送流量的通道,  要送的東西放在flowSentToServer , 格式幫忙弄成json , 第一格放MAC ID ,  第二格當下時間 第三格用量
                    paramsFlow.put("flow", flowSentToServer);
                    passToServer(paramsFlow, "CHT-flow");
//							}

//							if(getSec().toString().equals("00")) { //getHr().toString().equals("23") && getMin().toString().equals("59") && getSec().toString().equals("00")
                    RequestParams paramsFeat = new RequestParams();
                    infoSentToServer = calculateFeat();
                    System.out.println(infoSentToServer);

                    // 送feature的通道,  要送的東西放在infoSentToServer , 格式幫忙弄成json , 第一格放MAC ID , 第二格開始放18個feature
                    paramsFeat.put("feature", infoSentToServer);
                    passToServer(paramsFeat, "CHT-feature");
//							}

                    RequestParams paramsRec = new RequestParams();
                    recSentToServer = calculateRec();
                    System.out.println(recSentToServer);

                    // 送Location的通道,  要送的東西放在recSentToServer , 格式幫忙弄成json , 第一格放MAC ID ,  第二格當下時間 第三四格經緯度
                    paramsRec.put("rec", recSentToServer);
                    passToServer(paramsRec, "CHT-rec");

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }

            mHandler.postDelayed(mRunnable, 1000);
        }
    };

    private Button.OnClickListener startClickListener = new Button.OnClickListener() {
        public void onClick(View arg0) {
//			startService(intent);
            showDialog();
        }
    };

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    public void onDestroy(){
        super.onDestroy();
        stopService(intent);
    }

    private void writeToFile(String data) {


        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("trafficdata.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = openFileInput("trafficdata.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();

//				System.out.println("PPAP: " + ret);
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    //For dialog
    private int showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityOne.this);
        builder.setTitle("There are more discount available in next certain period!");
        builder.setMessage("Are you sure to use Skype APP right now or later?");
        builder.setPositiveButton("Yes, I wanna use it now!",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RequestParams params = new RequestParams();
                params.put("RULE", "Yes,"+getCurrentTime()); //"Yes,"+ getCurrentTime() ->
                passToServer(params,"CHT-RULE");
                useOrNot = 1;
            }
        });
        builder.setNegativeButton("No, I can use it later!", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RequestParams params = new RequestParams();
                params.put("RULE", "No,"+getCurrentTime());
                passToServer(params,"CHT-RULE");
                useOrNot = 2;
            }
        });
        builder.show();

        return useOrNot;
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
