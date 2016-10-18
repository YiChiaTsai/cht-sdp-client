package com.javatechig.listapps;

import android.content.Context;
import android.app.ActivityManager;
import android.app.ActivityManager.*;
import android.widget.Toast;

import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by ChengYuDa on 2016/10/11.
 */

public class sudoactivity {
    Context myContext;
    public sudoactivity(Context context){
        this.myContext = myContext;
    }
    public void Doit(){
        ActivityManager activityManager = (ActivityManager) myContext.getSystemService( ACTIVITY_SERVICE );
        List<RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        for(int i = 0; i < procInfos.size(); i++)
        {
            System.out.println(procInfos.get(i).processName);
            if(procInfos.get(i).processName.equals("com.google.android.youtube"))
            {
                System.out.println("Youtuber is running");
                Toast.makeText(myContext.getApplicationContext(), "Youtuber is running", Toast.LENGTH_LONG).show();
            }
        }
    }
}
