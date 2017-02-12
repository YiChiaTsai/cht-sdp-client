package com.javatechig.listapps;

import java.util.ArrayList;
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


public class AllAppsActivity extends ListActivity {
	private String HOST = "192.168.11.50";

	private PackageManager packageManager = null;
	private List<ApplicationInfo> applist = null;
	private ApplicationAdapter listadaptor = null;

	private Handler mHandler = new Handler();
	private long mStartTotalRX = 0;
	private long mStartTotalTX = 0;
//	private long mStartCertainAppRX = 0;
//	private long mStartCertainAppTX = 0;

	private JSONObject jsonObj = new JSONObject();
	private JSONArray jsonArr = new JSONArray();
	private JSONArray jsonArrApp = new JSONArray();
	private int jsonArrId = 0;

	private JSONObject jsonObjSummary = new JSONObject();
	private JSONArray jsonArrSummary = new JSONArray();
	private int jsonArrIdSummary = 0;

	private int useOrNot = 0; //default is 0, true is 1, false is 2.

	private Intent intent;
	private Button testButton;

	String trafficDataInfo = "";
	String infoSentToServer = "";
	String flowSentToServer = "";

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

	final private double thresholdOfDay = 150*1024;
	final private double thresholdOfMorning = 150*1024;
	final private double thresholdOfAfternoon = 150*1024;
	final private double thresholdOfEvening = 150*1024;
	final private double thresholdOfMidnight = 150*1024;
	private int[] arrayUid = new int[]{10118, 10134, 10110, 10138, 10174, 10175};
	private String[] arrayApp = new String[]{"Facebook","LINE","YouTube", "VoiceTube", "ClashofClans", "Knowledge"};
	private double[] dataUsageOfApp = new double[6];
//	private double[][] dataUsageOfMorning = new double[365][12]; //0-rx0, 1-tx0, 2-rx1, 3-tx1, ...
//	private double[][] dataUsageOfAfternoon = new double[365][12];
//	private double[][] dataUsageOfEvening = new double[365][12];
//	private double[][] dataUsageOfMidnight = new double[365][12];
//	private double[][] thresholdOfTime = new double[365][4]; //Exceed 150MB, set it 1. Otherwise, set it 0.

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		packageManager = getPackageManager();

		new LoadApplications().execute();

		mStartTotalRX = TrafficStats.getTotalRxBytes();
		mStartTotalRX = TrafficStats.getTotalTxBytes();

//		for(int i=0; i<6; i++){
//			dataUsageOfApp[0][i*2] = TrafficStats.getUidRxBytes(arrayUid[i]);
//			dataUsageOfApp[0][i*2] = TrafficStats.getUidTxBytes(arrayUid[i]);
//
//			dataUsageOfMorning[0][i*2] = TrafficStats.getUidRxBytes(arrayUid[i]);
//			dataUsageOfMorning[0][i*2+1] = TrafficStats.getUidTxBytes(arrayUid[i]);
//
//			dataUsageOfAfternoon[0][i*2] = TrafficStats.getUidRxBytes(arrayUid[i]);
//			dataUsageOfAfternoon[0][i*2+1] = TrafficStats.getUidTxBytes(arrayUid[i]);
//
//			dataUsageOfEvening[0][i*2] = TrafficStats.getUidRxBytes(arrayUid[i]);
//			dataUsageOfEvening[0][i*2+1] = TrafficStats.getUidTxBytes(arrayUid[i]);
//
//			dataUsageOfMidnight[0][i*2] = TrafficStats.getUidRxBytes(arrayUid[i]);
//			dataUsageOfMidnight[0][i*2+1] = TrafficStats.getUidTxBytes(arrayUid[i]);
//		}

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


		// [START subscribe_topics]
		FirebaseMessaging.getInstance().subscribeToTopic("news");
		// [END subscribe_topics]

		System.out.println("topic?");

//        intent = new Intent(AllAppsActivity.this,DialogService.class);

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
			default: {
				result = super.onOptionsItemSelected(item);

				break;
			}
		}

		return result;
	}

	public static String getCurrentTime() {
		Calendar c = Calendar.getInstance();
		System.out.println("Current time => "+c.getTime());

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
		for(int i=0; i<6; i++){
			Uid += arrayApp[i] + ": " + arrayUid[i] + "\n";
		}

		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.ask_certainappuid_title));
		builder.setMessage(Uid);

		builder.show();
	}

	private String calculateFeat() {
		dataUsageOfDay = 0; dataUsageOfMorning = 0; dataUsageOfAfternoon = 0; dataUsageOfEvening = 0; dataUsageOfMidnight = 0;
		limitOfDay = -1; //Exceed 150MB, set it as 1. Otherwise, set it as 0;
		limitOfMorning = -1; limitOfAfternoon = -1; limitOfEvening = -1; limitOfMidnight = -1;
		exceedOfDay = 0; exceedOfMorning = 0; exceedOfAfternoon = 0; exceedOfEvening = 0; exceedOfMidnight = 0;
		dataUsageSummary= "";
		String showdate = "";

		try {
			JSONArray databases = jsonObj.getJSONArray("databases");
			JSONArray appdatabases = jsonObj.getJSONArray("appdatabases");
			for (int i = 0; i < databases.length(); i++) {
				JSONObject c = databases.getJSONObject(i);

				String id = c.getString("id");
				String date = c.getString("date");
				showdate = date;
				String clock = c.getString("clock");
				String hr = c.getString("hr");
				String datausageRx = c.getString("datausageRx");
				String datausageTx = c.getString("datausageTx");
				String datausageSum = c.getString("datausageSum");
				String datausageRxNow = c.getString("datausageRxNow");
				String datausageTxNow = c.getString("datausageTxNow");
				String datausageSumNow = c.getString("datausageSumNow");

				if (hr == "07" || hr == "08" || hr == "09" || hr == "10" || hr == "11") {
					dataUsageOfMorning += Double.parseDouble(datausageSumNow);
				}
				if (hr == "12" || hr == "13" || hr == "14" || hr == "15" || hr == "16") {
					dataUsageOfAfternoon += Double.parseDouble(datausageSumNow);
				}
				if (hr == "17" || hr == "18" || hr == "19" || hr == "20" || hr == "21" || hr == "22" || hr == "23") {
					dataUsageOfEvening += Double.parseDouble(datausageSumNow);
				}
				if (hr == "00" || hr == "01" || hr == "02" || hr == "03" || hr == "04" || hr == "05" || hr == "06") {
					dataUsageOfMidnight += Double.parseDouble(datausageSumNow);
				}

//				dataUsageSummary += "List: id=" + id + " " + date + " " + clock + " hr=" + hr + " Rx=" + datausageRx + "KB Tx=" + datausageTx + "KB Sum=" + datausageSum + "KB RxNow=" + datausageRxNow + "KB TxNow=" + datausageTxNow + "KB SumNow=" + datausageSumNow + "\n";

			}
			dataUsageOfDay = dataUsageOfMorning + dataUsageOfAfternoon + dataUsageOfEvening + dataUsageOfMidnight;

			if (dataUsageOfDay > thresholdOfDay ) {
				limitOfDay = 1;
				exceedOfDay = dataUsageOfDay-thresholdOfDay;
			}else {
				limitOfDay = 0;
				exceedOfDay = dataUsageOfDay-thresholdOfDay;
			}
			if (dataUsageOfMorning > thresholdOfMorning ) {
				limitOfMorning = 1;
				exceedOfMorning = dataUsageOfMorning-thresholdOfMorning;
			}else {
				limitOfMorning = 0;
				exceedOfMorning = dataUsageOfMorning-thresholdOfMorning;
			}
			if (dataUsageOfAfternoon > thresholdOfAfternoon ) {
				limitOfAfternoon = 1;
				exceedOfAfternoon = dataUsageOfAfternoon-thresholdOfAfternoon;
			}else {
				limitOfAfternoon = 0;
				exceedOfAfternoon = dataUsageOfAfternoon-thresholdOfAfternoon;
			}
			if (dataUsageOfEvening > thresholdOfEvening ) {
				limitOfEvening = 1;
				exceedOfEvening = dataUsageOfEvening-thresholdOfEvening;
			}else {
				limitOfEvening = 0;
				exceedOfEvening = dataUsageOfEvening-thresholdOfEvening;
			}
			if (dataUsageOfMidnight > thresholdOfMidnight ) {
				limitOfMidnight = 1;
				exceedOfMidnight = dataUsageOfMidnight-thresholdOfMidnight;
			}else {
				limitOfMidnight = 0;
				exceedOfMidnight = dataUsageOfMidnight-thresholdOfMidnight;
			}

//			JSONObject statisticsObj0 = new JSONObject();
//			statisticsObj0.put("date", getDate()); // Set the first name/pair
//			statisticsObj0.put("timeslot", "morning");
//			statisticsObj0.put("datausageSum", dataUsageOfMorning);
//			jsonArrSummary.put(statisticsObj0);
//			jsonObjSummary.put("databasesSummary", jsonArrSummary);
//
//			JSONObject statisticsObj1 = new JSONObject();
//			statisticsObj1.put("date", getDate()); // Set the first name/pair
//			statisticsObj1.put("timeslot", "afternoon");
//			statisticsObj1.put("datausageSum", dataUsageOfAfternoon);
//			jsonArrSummary.put(statisticsObj1);
//			jsonObjSummary.put("databasesSummary", jsonArrSummary);
//
//			JSONObject statisticsObj2 = new JSONObject();
//			statisticsObj2.put("date", getDate()); // Set the first name/pair
//			statisticsObj2.put("timeslot", "evening");
//			statisticsObj2.put("datausageSum", dataUsageOfEvening);
//			jsonArrSummary.put(statisticsObj2);
//			jsonObjSummary.put("databasesSummary", jsonArrSummary);
//
//			JSONObject statisticsObj3 = new JSONObject();
//			statisticsObj3.put("date", getDate()); // Set the first name/pair
//			statisticsObj3.put("timeslot", "midnight");
//			statisticsObj3.put("datausageSum", dataUsageOfMidnight);
//			jsonArrSummary.put(statisticsObj3);
//			jsonObjSummary.put("databasesSummary", jsonArrSummary);
//
//			JSONObject statisticsObj4 = new JSONObject();
//			statisticsObj4.put("date", getDate()); // Set the first name/pair
//			statisticsObj4.put("timeslot", "day");
//			statisticsObj4.put("datausageSum", dataUsageOfDay);
//			jsonArrSummary.put(statisticsObj4);
//			jsonObjSummary.put("databasesSummary", jsonArrSummary);


//			System.out.println("RichardJson: " + jsonObjSummary.toString());
		}catch(JSONException ex) {
			ex.printStackTrace();
		}

//		dataUsageSummary += "Json: " + jsonObjSummary.toString() + "\n";

		dataUsageSummary += "Feature Summary: Date=" + showdate
				+ " dataUsageOfDay=" + dataUsageOfDay + "KB" + " dataUsageOfMorning=" + dataUsageOfMorning + "KB" + " dataUsageOfAfternoon=" + dataUsageOfAfternoon + "KB"
				+ " dataUsageOfEvening=" + dataUsageOfEvening + "KB" + " dataUsageOfMidnight=" + dataUsageOfMidnight + "KB"
				+ "\n\nlimitOfDay=" + limitOfDay + " limitOfMorning=" + limitOfMorning + " limitOfAfternoon=" + limitOfAfternoon
				+ " limitOfEvening=" + limitOfEvening + " limitOfMidnight=" + limitOfMidnight
				+ "\n\nexceedOfDay=" + exceedOfDay + "KB" + " exceedOfMorning=" + exceedOfMorning + "KB" + " exceedOfAfternoon=" + exceedOfAfternoon + "KB"
				+ " exceedOfEvening=" + exceedOfEvening + "KB" + " exceedOfMidnight=" + exceedOfMidnight + "KB"
				+ "\n\n" + arrayApp[0] + "-" + arrayUid[0] + "=" + dataUsageOfApp[0] +"KB" + " " + arrayApp[1] + "-" + arrayUid[1] + "=" + dataUsageOfApp[1] +"KB"
				+ " " + arrayApp[2] + "-" + arrayUid[2] + "=" + dataUsageOfApp[2] +"KB" + " " + arrayApp[3] + "-" + arrayUid[3] + "=" + dataUsageOfApp[3] +"KB"
				+ " " + arrayApp[4] + "-" + arrayUid[4] + "=" + dataUsageOfApp[4] +"KB" + " " + arrayApp[5] + "-" + arrayUid[5] + "=" + dataUsageOfApp[5] +"KB";
		System.out.print(dataUsageSummary);

		return dataUsageSummary;
	}

	private void displayDataDialog() {

		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.ask_appdata_title));
		builder.setMessage(calculateFeat());

		builder.show();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		ApplicationInfo app = applist.get(position);
		try {
			Intent intent = packageManager
					.getLaunchIntentForPackage(app.packageName);

			if (null != intent) {
				showDialog();
				if(useOrNot == 1)
					startActivity(intent);

			}
		} catch (ActivityNotFoundException e) {
			Toast.makeText(AllAppsActivity.this, e.getMessage(),
					Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Toast.makeText(AllAppsActivity.this, e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
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

	private class LoadApplications extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress = null;

		@Override
		protected Void doInBackground(Void... params) {
			applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
			listadaptor = new ApplicationAdapter(AllAppsActivity.this,
					R.layout.snippet_list_row, applist);

			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Void result) {
			setListAdapter(listadaptor);
			progress.dismiss();
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			progress = ProgressDialog.show(AllAppsActivity.this, null,
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

//			System.out.println( getCurrentTime() );

			long rxBytes = (TrafficStats.getTotalRxBytes() - mStartTotalRX)/1024; //1048576 = 1024*1024 = 2^20
			long txBytes = (TrafficStats.getTotalTxBytes() - mStartTotalTX)/1024;
			long sumBytes = rxBytes + txBytes;

			for(int i=0; i<6; i++){
				dataUsageOfApp[i] = ( (TrafficStats.getUidRxBytes(arrayUid[i]) - 0) + (TrafficStats.getUidTxBytes(arrayUid[i]) - 0) )/1024;
			}

			if(getMin().toString().equals("00") && getSec().toString().equals("00")) { //getHr().toString().equals("23") && getMin().toString().equals("59") && getSec().toString().equals("00")
				try {
					for (int i = 0; i < 6; i++) {
						JSONObject appObj = new JSONObject();
						appObj.put("id", arrayUid[i]);
						appObj.put("application", arrayApp[i]);
						appObj.put("date", getDate()); // Set the first name/pair
						appObj.put("clock", getClock());
						appObj.put("datausageSum", dataUsageOfApp[i]);

						jsonArrApp.put(appObj);
						jsonObj.put("appdatabases", jsonArrApp);
					}
				}catch(JSONException ex) {
					ex.printStackTrace();
				}
			}

			//Every hour (10 secs), we call the DATAUSAGE function and send it to Server
			if(getMin().toString().equals("00") && getSec().toString().equals("00")) { //  Integer.parseInt(getSec()) % 10 == 0   getSec().toString().equals("00")     getMin().toString().equals("00") && getSec().toString().equals("00")

				try {
					// Here we convert Java Object to JSON
					JSONObject pnObj = new JSONObject();
					pnObj.put("id", jsonArrId);
					pnObj.put("date", getDate()); // Set the first name/pair
					pnObj.put("clock", getClock());
					pnObj.put("hr", getHr());
					pnObj.put("datausageRx", rxBytes);
					pnObj.put("datausageTx", txBytes);
					pnObj.put("datausageSum", sumBytes);

					jsonArr.put(pnObj);
					jsonObj.put("databases", jsonArr);


					JSONArray databasesTmp = jsonObj.getJSONArray("databases");
					if(jsonArrId-1 >= 0) {
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

					if(getMin().toString().equals("00") && getSec().toString().equals("00")) {
						try {
							JSONArray databases = jsonObj.getJSONArray("databases");

							for (int i = 0; i < databases.length(); i++) {
								JSONObject c = databases.getJSONObject(i);

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

								System.out.println("List: id=" + id + " " + date + " " + clock + " hr=" + hr + " Rx=" + datausageRx + "KB Tx=" + datausageTx + "KB Sum=" + datausageSum + "KB RxNow=" + datausageRxNow + "KB TxNow=" + datausageTxNow + "KB SumNow=" + datausageSumNow);

								if(getMin().toString().equals("00") && getSec().toString().equals("00")) {
									int inthr1 = -1;
									int inthr2 = -1;
									if(hr.equals("00")) {
										inthr1 = 23;
										inthr2 = Integer.parseInt(hr);
									}else {
										inthr1 = Integer.parseInt(hr)-1;
										inthr2 = Integer.parseInt(hr);
									}

									RequestParams paramsFlow = new RequestParams();
									flowSentToServer = "Hr: " + inthr1 + "~" + inthr2 + ", DataUsage= " + datausageSumNow;

									// 送流量的通道,  要送的東西放在flowToServer , 格式幫忙弄成json , 第一格放MAC ID ,  第二格當下時間 第三格用量
									paramsFlow.put("flow", flowSentToServer);
									passToServer(paramsFlow, "CHT-flow");
								}

								if(getHr().toString().equals("23") && getMin().toString().equals("59") && getSec().toString().equals("00")) {
									RequestParams paramsFeat = new RequestParams();
									infoSentToServer = calculateFeat();

									// 送feature的通道,  要送的東西放在infoSentToServer , 格式幫忙弄成json , 第一格放MAC ID , 第二格開始放15個feature
									paramsFeat.put("feature" , infoSentToServer);
									passToServer(paramsFeat,"CHT-feature");

									displayDataDialog();
								}

							}
							System.out.println("Json: " + jsonObj.toString());

						}catch(JSONException ex) {
							ex.printStackTrace();
						}
					}
				}
				catch(JSONException ex) {
					ex.printStackTrace();
				}
			}

//			for(int i=0; i<6; i++){
//				int time = Integer.parseInt(getOclock());
//				if(time>=0 && time < 700){
//					dataUsageOfMidnight[0][i*2] = TrafficStats.getUidRxBytes(10066) - dataUsageOfMidnight[0][i*2];
//				}
//				if(time>=700 && time < 1200){
//
//				}
//
//				dataUsageOfApp[0][i*2] = dataUsageOfMorning[0][i*2] + dataUsageOfAfternoon[0][i*2] + dataUsageOfEvening[0][i*2] + dataUsageOfMidnight[0][i*2];
//				dataUsageOfApp[0][i*2+1] = dataUsageOfMorning[0][i*2+1] + dataUsageOfAfternoon[0][i*2+1] + dataUsageOfEvening[0][i*2+1] + dataUsageOfMidnight[0][i*2+1];
//			}

//			System.out.println("Total: " + rxBytes + "Bytes" + " " + txBytes + "Bytes");
//			for(int i=0; i<6; i++) {
//				System.out.println(arrayApp[i] + "(" + arrayUid[i] + ")" + ": " + dataUsageOfApp[0][i*2] + "Bytes" + " " + dataUsageOfApp[0][i*2+1] + "Bytes");
//			}

//			System.out.println( Integer.parseInt(getOclock()) );

//			long CertainApprxBytes = (TrafficStats.getUidRxBytes(10066)- mStartCertainAppRX)/1048576;
//			long CertainApptxBytes = (TrafficStats.getUidTxBytes(10066)- mStartCertainAppTX)/1048576;
//
//			dataUsage = "Total: " + rxBytes + "MB" + " " + txBytes + "MB" + "\n" + "CertainApp: " + CertainApprxBytes + "MB" + " " + CertainApptxBytes + "MB";


			// (TEST) Every minutes, return the APP data usage to server.
//			if(getMin().toString().equals("00")) {
//				writeToFile(CertainApprxBytes + "MB" + "," + CertainApptxBytes + "MB");
//				trafficDataInfo = readFromFile();
//
//				infoSentToServer = "10066" + "," + "YouTube" + "," + getCurrentTime() + "," + trafficDataInfo;
//
//				RequestParams params = new RequestParams();
//				params.put("DATAUSAGE", infoSentToServer);
//				passToServer(params);
//
//			}

			// Every hour, return the APP data usage to server.
//			if(getClock().toString().equals("00:00")) {
//				showDialog();
//			}

			mHandler.postDelayed(mRunnable, 1000);
		}
	};

	private Button.OnClickListener startClickListener = new Button.OnClickListener() {
		public void onClick(View arg0) {
//			startService(intent);
			showDialog();
		}
	};

	public void onDestroy(){
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

				System.out.println("PPAP: " + ret);
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
		AlertDialog.Builder builder = new AlertDialog.Builder(AllAppsActivity.this);
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

//		AlertDialog alert = builder.create();
//		alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//設定提示框為系統提示框
//		alert.show();

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