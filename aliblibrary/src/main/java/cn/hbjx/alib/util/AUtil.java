package cn.hbjx.alib.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by DengYiQian on 2017/6/6.
 */

public class AUtil {

    public static String SHOW_PROGRESS = "show_progress";
    public static String HIDE_PROGRESS = "hide_progress";
    public static String ACTIVITYS_CLOSE = "activitys_close";

    public AUtil() {
    }

    public static void closeAllActivitys(Context cxt) {
        Intent intent = new Intent();
        intent.setAction(ACTIVITYS_CLOSE);
        cxt.sendBroadcast(intent);
    }

    public static void progress(Context cxt, boolean f) {
        progress(cxt, f, "");
    }

    public static void progress(Context cxt, boolean f, String msg) {
        Intent intent;
        if (f) {
            intent = new Intent();
            intent.setAction(SHOW_PROGRESS);
            intent.putExtra("msg", msg);
            cxt.sendBroadcast(intent);
        } else {
            intent = new Intent();
            intent.setAction(HIDE_PROGRESS);
            cxt.sendBroadcast(intent);
        }

    }

    public static DisplayMetrics getDisplayMetrics(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    public static int dp2px(Context cxt, float dipValue) {
        float scale = cxt.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5F);
    }

    public static int px2dp(Context cxt, float pxValue) {
        float scale = cxt.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5F);
    }

    public static int px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5F);
    }

    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5F);
    }

    public static void closeSoftInput(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService("input_method");
        if (imm != null && imm.isActive()) {
            View focusView = activity.getCurrentFocus();
            if (focusView != null) {
                imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
            }
        }
    }

    public static void refresh(final SwipeRefreshLayout refresh, final boolean status) {
        refresh.post(new Runnable() {
            public void run() {
                if (status) {
                    refresh.setRefreshing(true);
                } else {
                    refresh.setRefreshing(false);
                }
            }
        });
    }


    public static void drawIcon(Drawable left, Drawable top, Drawable right, Drawable bottom, View tv) {
        if (left != null) {
            left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
        }

        if (right != null) {
            right.setBounds(0, 0, right.getMinimumWidth(), right.getMinimumHeight());
        }

        if (top != null) {
            top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());
        }

        if (bottom != null) {
            bottom.setBounds(0, 0, bottom.getMinimumWidth(), bottom.getMinimumHeight());
        }

        if (tv instanceof TextView) {
            ((TextView) tv).setCompoundDrawables(left, top, right, bottom);
        } else if (tv instanceof Button) {
            ((Button) tv).setCompoundDrawables(left, top, right, bottom);
        }

    }

    public static String getDateFromLong(String dateFormat, Long millSec) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = new Date(millSec.longValue());
        return sdf.format(date);
    }


    public static void uninstallAPK(Context cxt, String packageName) {
        Uri uri = Uri.parse("package:" + packageName);
        Intent intent = new Intent("android.intent.action.DELETE", uri);
        cxt.startActivity(intent);
    }

    public static void uninstallAPK(Context cxt) {
        Uri uri = Uri.parse("package:" + getPackageInfo(cxt).packageName);
        Intent intent = new Intent("android.intent.action.DELETE", uri);
        cxt.startActivity(intent);
    }

    public static PackageInfo getPackageInfo(Context c) {
        PackageManager packageManager = c.getPackageManager();
        PackageInfo packInfo = null;

        try {
            packInfo = packageManager.getPackageInfo(c.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException var4) {
            var4.printStackTrace();
        }

        return packInfo;
    }

    public static boolean hasSdcard() {
        String status = Environment.getExternalStorageState();
        return status.equals("mounted") || status.equals("/mnt/sdcard");
    }

    public static String getAppProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List infos = manager.getRunningAppProcesses();
        Iterator var4 = infos.iterator();

        ActivityManager.RunningAppProcessInfo info;
        do {
            if (!var4.hasNext()) {
                return "";
            }

            info = (ActivityManager.RunningAppProcessInfo) var4.next();
        } while (info.pid != pid);

        return info.processName;
    }

    public static Drawable getAppIcon(Context context, String packname) {
        try {
            PackageManager e = context.getPackageManager();
            ApplicationInfo info = e.getApplicationInfo(packname, 0);
            return info.loadIcon(e);
        } catch (PackageManager.NameNotFoundException var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public static String getAppVersion(Context context, String packname) {
        PackageManager pm = context.getPackageManager();

        try {
            PackageInfo e = pm.getPackageInfo(packname, 0);
            return e.versionName;
        } catch (PackageManager.NameNotFoundException var4) {
            var4.printStackTrace();
            return packname;
        }
    }

    public static String getAppName(Context context, String packname) {
        PackageManager pm = context.getPackageManager();

        try {
            ApplicationInfo e = pm.getApplicationInfo(packname, 0);
            return e.loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException var4) {
            var4.printStackTrace();
            return packname;
        }
    }

    private static String getCurrentActivityName(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String runningActivity = ((ActivityManager.RunningTaskInfo) activityManager.getRunningTasks(1).get(0)).topActivity.getClassName();
        return runningActivity;
    }

}
