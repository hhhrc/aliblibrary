package cn.hbjx.alib.ui;

import android.app.AlertDialog;
import android.app.Application;
import android.os.StrictMode;

import cn.hbjx.alib.crash.CustomActivityOnCrash;
import cn.hbjx.alib.crash.DefaultErrorActivity;
import cn.hbjx.alib.util.CacheUtil;
import cn.hbjx.alib.util.Lg;

/**
 * Created by DengYiQian on 2017/6/6.
 */

public class DefaultApplication extends Application {

    private AlertDialog.Builder conflictBuilder;

    public DefaultApplication() {
    }

    public void onCreate() {
        if(Lg.DEBUG) {
            Lg.println("应用创建");
        }

        super.onCreate();
        CacheUtil.context = this.getApplicationContext();
        StrictMode.ThreadPolicy policy = (new android.os.StrictMode.ThreadPolicy.Builder()).permitAll().build();
        StrictMode.setThreadPolicy(policy);
        if(Lg.DEBUG) {
            CustomActivityOnCrash.defaultErrorActivityDrawableId = 2130903040;
            CustomActivityOnCrash.install(this);
            CustomActivityOnCrash.setErrorActivityClass(DefaultErrorActivity.class);
        }

    }

    public void onTerminate() {
        if(Lg.DEBUG) {
            Lg.println("应用关闭");
        }

        super.onTerminate();
    }

}
