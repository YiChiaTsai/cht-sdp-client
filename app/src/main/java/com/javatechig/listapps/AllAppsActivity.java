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


public class AllAppsActivity extends ListActivity {
	private String HOST = "192.168.11.50";

	private PackageManager packageManager = null;
	private List<ApplicationInfo> applist = null;
	private ApplicationAdapter listadaptor = null;

	private Handler mHandler = new Handler();
	private long mStartTotalRX = 0;
	private long mStartTotalTX = 0;
	private long mStartCertainAppRX = 0;
	private long mStartCertainAppTX = 0;
	private String dataUsage = null;

	private JSONObject jsonObj = new JSONObject();
	private int jsonObjId = 0;

	private int useOrNot = 0; //default is 0, true is 1, false is 2.

	private Intent intent;
	private Button testButton;

	String trafficDataInfo = "";
	String infoSentToServer = "";

	private int[] arrayUid = new int[]{10136, 10137, 10066, 10139, 10140, 10141};
	private String[] arrayApp = new String[]{"Facebook","LINE","YouTube", "VoiceTube", "ClashofClans", "Knowledge"};
	private double[][] dataUsageOfApp = new double[365][12];
	private double[][] dataUsageOfMorning = new double[365][12]; //0-rx0, 1-tx0, 2-rx1, 3-tx1, ...
	private double[][] dataUsageOfAfternoon = new double[365][12];
	private double[][] dataUsageOfEvening = new double[365][12];
	private double[][] dataUsageOfMidnight = new double[365][12];
	private double[][] thresholdOfTime = new double[365][4]; //Exceed 150MB, set it 1. Otherwise, set it 0.

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		packageManager = getPackageManager();

		new LoadApplications().execute();

		mStartTotalRX = TrafficStats.getTotalRxBytes();
		mStartTotalRX = TrafficStats.getTotalTxBytes();

		for(int i=0; i<6; i++){
			dataUsageOfApp[0][i*2] = TrafficStats.getUidRxBytes(arrayUid[i]);
			dataUsageOfApp[0][i*2] = TrafficStats.getUidTxBytes(arrayUid[i]);

			dataUsageOfMorning[0][i*2] = TrafficStats.getUidRxBytes(arrayUid[i]);
			dataUsageOfMorning[0][i*2+1] = TrafficStats.getUidTxBytes(arrayUid[i]);

			dataUsageOfAfternoon[0][i*2] = TrafficStats.getUidRxBytes(arrayUid[i]);
			dataUsageOfAfternoon[0][i*2+1] = TrafficStats.getUidTxBytes(arrayUid[i]);

			dataUsageOfEvening[0][i*2] = TrafficStats.getUidRxBytes(arrayUid[i]);
			dataUsageOfEvening[0][i*2+1] = TrafficStats.getUidTxBytes(arrayUid[i]);

			dataUsageOfMidnight[0][i*2] = TrafficStats.getUidRxBytes(arrayUid[i]);
			dataUsageOfMidnight[0][i*2+1] = TrafficStats.getUidTxBytes(arrayUid[i]);
		}

		if (mStartTotalRX == TrafficStats.UNSUPPORTED || mStartTotalRX == TrafficStats.UNSUPPORTED || mStartCertainAppRX == TrafficStats.UNSUPPORTED || mStartCertainAppTX == TrafficStats.UNSUPPORTED) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Uh Oh!");
			alert.setMessage("Your device does not support traffic stat monitoring.");
			alert.show();
		} else {
			mHandler.postDelayed(mRunnable, 1000);
		}


		testButton = (Button) findViewById(R.id.testme);
		testButton.setOnClickListener(startClickListener);

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
				readFromFile();
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

	private void displayDataDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.ask_certainappdata_title));
		builder.setMessage(dataUsage);

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

			long rxBytes = (TrafficStats.getTotalRxBytes()- mStartTotalRX)/1048576; //1048576 = 1024*1024 = 2^20
			long txBytes = (TrafficStats.getTotalTxBytes()- mStartTotalTX)/1048576;
			long sumBytes = rxBytes + txBytes;

			//Every hour, we call the DATAUSAGE function and send it to Server
			if(Integer.parseInt(getSec()) % 10 == 0 ) { //  getSec().toString().equals("00")     getMin().toString().equals("00") && getSec().toString().equals("00")

				try {
					// Here we convert Java Object to JSON
					jsonObj.put("id", jsonObjId);
					jsonObj.put("date", getDate()); // Set the first name/pair
					jsonObj.put("clock", getClock());
					jsonObj.put("datausageRx", rxBytes);
					jsonObj.put("datausageTx", txBytes);
					jsonObj.put("datausageSum", sumBytes);

					jsonObjId++;

//            JSONObject jsonAdd = new JSONObject(); // we need another object to store the address
//            jsonAdd.put("address", "infinite space, 000");
//            jsonAdd.put("city", "Android city");
//            jsonAdd.put("state", "World");	// We add the object to the main object
//
//            jsonObj.put("address", jsonAdd);	// and finally we add the phone number
//            // In this case we need a json array to hold the java list
//
//            JSONArray jsonArr = new JSONArray();
//
//            for ( int i=0; i<3; i++) {
//                JSONObject pnObj = new JSONObject();
//                pnObj.put("num", i);
//                pnObj.put("type", i+10);
//                jsonArr.put(pnObj);
//            }
//
//            jsonObj.put("phoneNumber", jsonArr);

					System.out.println(jsonObj.toString());

					RequestParams params = new RequestParams();
					infoSentToServer = jsonObj.toString();
					params.put("DATAUSAGE", infoSentToServer);
					passToServerJSON(params,"CHT-ClockJSON");

				}
				catch(JSONException ex) {
					ex.printStackTrace();
				}
			}

			long CertainApprxBytes = (TrafficStats.getUidRxBytes(10066)- mStartCertainAppRX)/1048576;
			long CertainApptxBytes = (TrafficStats.getUidTxBytes(10066)- mStartCertainAppTX)/1048576;

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

			System.out.println("Total: " + rxBytes + "Bytes" + " " + txBytes + "Bytes");
//			for(int i=0; i<6; i++) {
//				System.out.println(arrayApp[i] + "(" + arrayUid[i] + ")" + ": " + dataUsageOfApp[0][i*2] + "Bytes" + " " + dataUsageOfApp[0][i*2+1] + "Bytes");
//			}

//			System.out.println( Integer.parseInt(getOclock()) );
			System.out.println( getCurrentTime() );

			dataUsage = "Total: " + rxBytes + "MB" + " " + txBytes + "MB" + "\n" + "CertainApp: " + CertainApprxBytes + "MB" + " " + CertainApptxBytes + "MB";


			// (TEST) Every minutes, return the APP data usage to server.
			if(getMin().toString().equals("00")) {
				writeToFile(CertainApprxBytes + "MB" + "," + CertainApptxBytes + "MB");
				trafficDataInfo = readFromFile();

				infoSentToServer = "10066" + "," + "YouTube" + "," + getCurrentTime() + "," + trafficDataInfo;

				RequestParams params = new RequestParams();
				params.put("DATAUSAGE", infoSentToServer);
				passToServerJSON(params,"CHT-ClockJSON");

			}

			// Every hour, return the APP data usage to server.
			if(getClock().toString().equals("00:00")) {
				showDialog();
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
		builder.setTitle("有更優惠的時段唷");
		builder.setMessage("是否確定要在此時使用軟體?");
		builder.setPositiveButton("我就是要使用",new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				RequestParams params = new RequestParams();
				params.put("RULE", "Yes,"+getCurrentTime()); //"Yes,"+ getCurrentTime() ->
				passToServer(params,"CHT-RULE");
				useOrNot = 1;
			}
		});
		builder.setNegativeButton("算了，我下次再用", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				RequestParams params = new RequestParams();
				params.put("RULE", "No,"+getCurrentTime());
				passToServer(params,"CHT-RULE");
				useOrNot = 2;
			}
		});
		AlertDialog alert = builder.create();
		alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//設定提示框為系統提示框
		alert.show();

		return useOrNot;
	}

	//傳至Server
	public void passToServer(RequestParams params,String category){
		AsyncHttpClient client = new AsyncHttpClient();
		client.get("http://" + HOST + ":8080/CHTServer/hello/"+category, params, new AsyncHttpResponseHandler() {
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

	public void passToServerJSON(RequestParams params,String category){
		AsyncHttpClient client = new AsyncHttpClient();
		client.post("http://" + HOST + ":8080/CHTServer/hello/"+category, params, new AsyncHttpResponseHandler() {
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