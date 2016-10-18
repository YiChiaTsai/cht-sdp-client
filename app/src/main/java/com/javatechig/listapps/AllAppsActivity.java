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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

public class AllAppsActivity extends ListActivity {
	private PackageManager packageManager = null;
	private List<ApplicationInfo> applist = null;
	private ApplicationAdapter listadaptor = null;

    private Handler mHandler = new Handler();
    private long mStartTotalRX = 0;
    private long mStartTotalTX = 0;
    private long mStartCertainAppRX = 0;
    private long mStartCertainAppTX = 0;
    private String dataUsage = null;

    private Intent intent;
    private Button testButton;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		packageManager = getPackageManager();

		new LoadApplications().execute();

        mStartTotalRX = TrafficStats.getTotalRxBytes();
        mStartTotalRX = TrafficStats.getTotalTxBytes();
        mStartCertainAppRX = TrafficStats.getUidRxBytes(10066); //youtube
        mStartCertainAppTX = TrafficStats.getUidTxBytes(10066); //youtube
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

        intent = new Intent(AllAppsActivity.this,DialogService.class);


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
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.ask_certainappuid_title));
        builder.setMessage("10066");

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
            long rxBytes = (TrafficStats.getTotalRxBytes()- mStartTotalRX)/1048576;
            long txBytes = (TrafficStats.getTotalTxBytes()- mStartTotalTX)/1048576;

            long CertainApprxBytes = (TrafficStats.getUidRxBytes(10066)- mStartCertainAppRX)/1048576;
            long CertainApptxBytes = (TrafficStats.getUidTxBytes(10066)- mStartCertainAppTX)/1048576;

            System.out.println("Total: " + rxBytes + "MB" + " " + txBytes + "MB");
            System.out.println("CertainApp: " + CertainApprxBytes + "MB" + " " + CertainApptxBytes + "MB");

            dataUsage = "Total: " + rxBytes + "MB" + " " + txBytes + "MB" + "\n" + "CertainApp: " + CertainApprxBytes + "MB" + " " + CertainApptxBytes + "MB";

            mHandler.postDelayed(mRunnable, 1000);
        }
    };

    private Button.OnClickListener startClickListener = new Button.OnClickListener() {
        public void onClick(View arg0) {
            startService(intent);
        }
    };

	public void onDestroy(){
        stopService(intent);
	}

}