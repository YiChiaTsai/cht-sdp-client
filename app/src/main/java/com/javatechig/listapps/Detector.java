package com.javatechig.listapps;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;


/**
 * Created by ChengYuDa on 2016/10/11.
 */

public class Detector implements Application.ActivityLifecycleCallbacks {

    private int run = 0;
    private int resume = 0;
    private int create = 0;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        System.out.println("Some App create now "+(++create));
    }

    @Override
    public void onActivityStarted(Activity activity) {
        System.out.println("Some App running now "+(++run));
        Log.e("","onActivityStarted:" + activity.getLocalClassName());
    }

    @Override
    public void onActivityResumed(Activity activity) {
        System.out.println("Some App resuming now "+(++resume));
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
