//package com.javatechig.listapps;
//
//import android.app.Activity;
//import android.app.ActivityManager;
//import android.app.ActivityManager.*;
//import android.app.AlertDialog;
//import android.app.Service;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Handler;
//import android.os.IBinder;
//import android.support.annotation.Nullable;
//import android.util.Log;
//import android.view.WindowManager;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.loopj.android.http.*;
//
//import java.util.Date;
//import java.util.List;
//
//import cz.msebera.android.httpclient.Header;
//
///**
// * Created by ChengYuDa on 2016/10/10.
// */
//
//public class DialogService extends Service {
//
//
//    // System parameter
//    private String HOST = "192.168.180.128";
//
//    private Handler handler = new Handler();
//    private Context mycontext;
//
//
//    private int x;
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
////    public void onCreate(){
////        this.getApplication().registerActivityLifecycleCallbacks(new Detector());
////    }
//
//    public void onStart(Intent intent, int startId) {
//        this.mycontext = this;
//
//        handler.postDelayed(backgroundservice, 1000);
//
//        showDialog();
//
//        super.onStart(intent, startId);
//    }
//
//    @Override
//    public void onDestroy() {
//        handler.removeCallbacks(backgroundservice);
//        super.onDestroy();
//    }
//
//    private Runnable backgroundservice = new Runnable() {//背景服務  目前沒用到
//        public void run() {
//            Log.i("time:", new Date().toString());
//
////            sudoactivity sudo = new sudoactivity(context);
//
//            ActivityManager activityManager = (ActivityManager) mycontext.getSystemService( ACTIVITY_SERVICE );
//            List<RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
//            for(int i = 0; i < procInfos.size(); i++)
//            {
//                System.out.println(procInfos.get(i).processName);
//                if(procInfos.get(i).processName.equals("com.google.android.youtube"))
//                {
//                    System.out.println("Youtuber is running");
//                    Toast.makeText(mycontext.getApplicationContext(), "Youtuber is running", Toast.LENGTH_LONG).show();
//                }
//            }
//            handler.postDelayed(this, 10000);
//
//
//        }
//    };
//
//
//    //For dialog
//    private void showDialog(){
//        AlertDialog.Builder builder = new AlertDialog.Builder(DialogService.this);
//        builder.setTitle("有更優惠的時段唷");
//        builder.setMessage("是否確定要在此時使用軟體?");
//        builder.setPositiveButton("我就是要使用",new DialogInterface.OnClickListener(){
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                RequestParams params = new RequestParams();
//                params.put("DATA", "Yes,"+AllAppsActivity.getCurrentTime());
//                passToServer(params);
//            }
//        });
//        builder.setNegativeButton("算了，我下次再用", new DialogInterface.OnClickListener(){
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                RequestParams params = new RequestParams();
//                params.put("DATA", "No,"+AllAppsActivity.getCurrentTime());
//                passToServer(params);
//            }
//        });
//        AlertDialog alert = builder.create();
//        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//設定提示框為系統提示框
//        alert.show();
//    }
//
//    //傳至Server
//    public void passToServer(RequestParams params){
//        AsyncHttpClient client = new AsyncHttpClient();
//        client.get("http://" + HOST + ":8080/MobileRestServer/rest/hello/CHT-SDP", params, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int i, Header[] headers, byte[] bytes) {
//                CharSequence cs = new String(bytes);
//                Toast toast = Toast.makeText(getApplicationContext(), cs, Toast.LENGTH_SHORT);    //toast 會閃現    用textView來接
//                toast.show();
//            }
//
//            @Override
//            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
//                Log.e("InvokeWS", Integer.toString(i));
//                if (bytes != null) {
//                    CharSequence cs = new String(bytes);
//                    Toast toast = Toast.makeText(getApplicationContext(), cs, Toast.LENGTH_SHORT);
//                    toast.show();
//                }
//            }
//        });
//    }
//}
