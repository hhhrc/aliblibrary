package cn.hbjx.alib.crash;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import cn.hbjx.alib.util.AUtil;

/**
 * Created by DengYiQian on 2017/6/6.
 */

@SuppressLint({"NewApi"})
public final class CustomActivityOnCrash {

    private static final String TAG = "CustomActivityOnCrash";
    private static final String EXTRA_RESTART_ACTIVITY_CLASS = "cat.ereza.customactivityoncrash.EXTRA_RESTART_ACTIVITY_CLASS";
    private static final String EXTRA_SHOW_ERROR_DETAILS = "cat.ereza.customactivityoncrash.EXTRA_SHOW_ERROR_DETAILS";
    private static final String EXTRA_STACK_TRACE = "cat.ereza.customactivityoncrash.EXTRA_STACK_TRACE";
    private static final String EXTRA_IMAGE_DRAWABLE_ID = "cat.ereza.customactivityoncrash.EXTRA_IMAGE_DRAWABLE_ID";
    private static final String EXTRA_EVENT_LISTENER = "cat.ereza.customactivityoncrash.EXTRA_EVENT_LISTENER";
    private static final String INTENT_ACTION_ERROR_ACTIVITY = "cat.ereza.customactivityoncrash.ERROR";
    private static final String INTENT_ACTION_RESTART_ACTIVITY = "cat.ereza.customactivityoncrash.RESTART";
    private static final String CAOC_HANDLER_PACKAGE_NAME = "cat.ereza.customactivityoncrash";
    private static final String DEFAULT_HANDLER_PACKAGE_NAME = "com.android.internal.os";
    private static final int MAX_STACK_TRACE_SIZE = 131071;
    private static final int TIMESTAMP_DIFFERENCE_TO_AVOID_RESTART_LOOPS_IN_MILLIS = 2000;
    private static final String SHARED_PREFERENCES_FILE = "custom_activity_on_crash";
    private static final String SHARED_PREFERENCES_FIELD_TIMESTAMP = "last_crash_timestamp";
    private static Application application;
    private static WeakReference<Activity> lastActivityCreated = new WeakReference((Object)null);
    private static boolean isInBackground = false;
    private static boolean launchErrorActivityWhenInBackground = true;
    private static boolean showErrorDetails = true;
    private static boolean enableAppRestart = true;
    public static int defaultErrorActivityDrawableId = -1;
    private static Class<? extends Activity> errorActivityClass = null;
    private static Class<? extends Activity> restartActivityClass = null;
    private static CustomActivityOnCrash.EventListener eventListener = null;

    public CustomActivityOnCrash() {
    }

    public static void install(Context context) {
        try {
            if(context == null) {
                Log.e("CustomActivityOnCrash", "Install failed: context is null!");
            } else {
                if(Build.VERSION.SDK_INT < 14) {
                    Log.w("CustomActivityOnCrash", "CustomActivityOnCrash will be installed, but may not be reliable in API lower than 14");
                }

                final Thread.UncaughtExceptionHandler t = Thread.getDefaultUncaughtExceptionHandler();
                if(t != null && t.getClass().getName().startsWith("cat.ereza.customactivityoncrash")) {
                    Log.e("CustomActivityOnCrash", "You have already installed CustomActivityOnCrash, doing nothing!");
                } else {
                    if(t != null && !t.getClass().getName().startsWith("com.android.internal.os")) {
                        Log.e("CustomActivityOnCrash", "IMPORTANT WARNING! You already have an UncaughtExceptionHandler, are you sure this is correct? If you use ACRA, Crashlytics or similar libraries, you must initialize them AFTER CustomActivityOnCrash! Installing anyway, but your original handler will not be called.");
                    }

                    application = (Application)context.getApplicationContext();
                    Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                        public void uncaughtException(Thread thread, Throwable throwable) {
                            Log.e("CustomActivityOnCrash", "App has crashed, executing CustomActivityOnCrash\'s UncaughtExceptionHandler", throwable);
                            if(CustomActivityOnCrash.hasCrashedInTheLastSeconds(CustomActivityOnCrash.application)) {
                                Log.e("CustomActivityOnCrash", "App already crashed in the last 2 seconds, not starting custom error activity because we could enter a restart loop. Are you sure that your app does not crash directly on init?", throwable);
                                if(t != null) {
                                    t.uncaughtException(thread, throwable);
                                    return;
                                }
                            } else {
                                CustomActivityOnCrash.setLastCrashTimestamp(CustomActivityOnCrash.application, (new Date()).getTime());
                                if(CustomActivityOnCrash.errorActivityClass == null) {
                                    CustomActivityOnCrash.errorActivityClass = CustomActivityOnCrash.guessErrorActivityClass(CustomActivityOnCrash.application);
                                }

                                if(CustomActivityOnCrash.isStackTraceLikelyConflictive(throwable, CustomActivityOnCrash.errorActivityClass)) {
                                    Log.e("CustomActivityOnCrash", "Your application class or your error activity have crashed, the custom activity will not be launched!");
                                    if(t != null) {
                                        t.uncaughtException(thread, throwable);
                                        return;
                                    }
                                } else if(CustomActivityOnCrash.launchErrorActivityWhenInBackground || !CustomActivityOnCrash.isInBackground) {
                                    Intent lastActivity = new Intent(CustomActivityOnCrash.application, CustomActivityOnCrash.errorActivityClass);
                                    StringWriter sw = new StringWriter();
                                    PrintWriter pw = new PrintWriter(sw);
                                    throwable.printStackTrace(pw);
                                    String stackTraceString = sw.toString();
                                    if(stackTraceString.length() > 131071) {
                                        String disclaimer = " [stack trace too large]";
                                        stackTraceString = stackTraceString.substring(0, 131071 - disclaimer.length()) + disclaimer;
                                    }

                                    if(CustomActivityOnCrash.enableAppRestart && CustomActivityOnCrash.restartActivityClass == null) {
                                        CustomActivityOnCrash.restartActivityClass = CustomActivityOnCrash.guessRestartActivityClass(CustomActivityOnCrash.application);
                                    } else if(!CustomActivityOnCrash.enableAppRestart) {
                                        CustomActivityOnCrash.restartActivityClass = null;
                                    }

                                    lastActivity.putExtra("cat.ereza.customactivityoncrash.EXTRA_STACK_TRACE", stackTraceString);
                                    lastActivity.putExtra("cat.ereza.customactivityoncrash.EXTRA_RESTART_ACTIVITY_CLASS", CustomActivityOnCrash.restartActivityClass);
                                    lastActivity.putExtra("cat.ereza.customactivityoncrash.EXTRA_SHOW_ERROR_DETAILS", CustomActivityOnCrash.showErrorDetails);
                                    lastActivity.putExtra("cat.ereza.customactivityoncrash.EXTRA_EVENT_LISTENER", CustomActivityOnCrash.eventListener);
                                    lastActivity.putExtra("cat.ereza.customactivityoncrash.EXTRA_IMAGE_DRAWABLE_ID", CustomActivityOnCrash.defaultErrorActivityDrawableId);
                                    lastActivity.setFlags(268468224);
                                    if(CustomActivityOnCrash.eventListener != null) {
                                        CustomActivityOnCrash.eventListener.onLaunchErrorActivity();
                                    }

                                    CustomActivityOnCrash.application.startActivity(lastActivity);
                                }
                            }

                            Activity lastActivity1 = (Activity)CustomActivityOnCrash.lastActivityCreated.get();
                            if(lastActivity1 != null) {
                                lastActivity1.finish();
                                CustomActivityOnCrash.lastActivityCreated.clear();
                            }

                            CustomActivityOnCrash.killCurrentProcess();
                        }
                    });
                    if(Build.VERSION.SDK_INT >= 14) {
                        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                            int currentlyStartedActivities = 0;

                            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                                if(activity.getClass() != CustomActivityOnCrash.errorActivityClass) {
                                    CustomActivityOnCrash.lastActivityCreated = new WeakReference(activity);
                                }

                            }

                            public void onActivityStarted(Activity activity) {
                                ++this.currentlyStartedActivities;
                                CustomActivityOnCrash.isInBackground = this.currentlyStartedActivities == 0;
                            }

                            public void onActivityResumed(Activity activity) {
                            }

                            public void onActivityPaused(Activity activity) {
                            }

                            public void onActivityStopped(Activity activity) {
                                --this.currentlyStartedActivities;
                                CustomActivityOnCrash.isInBackground = this.currentlyStartedActivities == 0;
                            }

                            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                            }

                            public void onActivityDestroyed(Activity activity) {
                            }
                        });
                    }

                    Log.i("CustomActivityOnCrash", "CustomActivityOnCrash has been installed.");
                }
            }
        } catch (Throwable var2) {
            Log.e("CustomActivityOnCrash", "An unknown error occurred while installing CustomActivityOnCrash, it may not have been properly initialized. Please report this as a bug if needed.", var2);
        }

    }

    public static boolean isShowErrorDetailsFromIntent(Intent intent) {
        return intent.getBooleanExtra("cat.ereza.customactivityoncrash.EXTRA_SHOW_ERROR_DETAILS", true);
    }

    public static int getDefaultErrorActivityDrawableIdFromIntent(Intent intent) {
        return defaultErrorActivityDrawableId;
    }

    public static String getStackTraceFromIntent(Intent intent) {
        return intent.getStringExtra("cat.ereza.customactivityoncrash.EXTRA_STACK_TRACE");
    }

    public static String getAllErrorDetailsFromIntent(Context context, Intent intent) {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String buildDateAsString = getBuildDateAsString(context, dateFormat);
        String versionName = getVersionName(context);
        String errorDetails = "";
        String appProcessName = AUtil.getAppProcessName(context);
        errorDetails = errorDetails + "App Name : " + AUtil.getAppName(context, appProcessName) + " \n";
        errorDetails = errorDetails + "Process Name : " + appProcessName + " \n";
        errorDetails = errorDetails + "Build version: " + versionName + " \n";
        errorDetails = errorDetails + "Build date: " + buildDateAsString + " \n";
        errorDetails = errorDetails + "Current date: " + dateFormat.format(currentDate) + " \n";
        errorDetails = errorDetails + "Device: " + getDeviceModelName() + " \n \n";
        errorDetails = errorDetails + "Stack trace:  \n";
        errorDetails = errorDetails + getStackTraceFromIntent(intent);
        return errorDetails;
    }

    public static Class<? extends Activity> getRestartActivityClassFromIntent(Intent intent) {
        Serializable serializedClass = intent.getSerializableExtra("cat.ereza.customactivityoncrash.EXTRA_RESTART_ACTIVITY_CLASS");
        return serializedClass != null && serializedClass instanceof Class?(Class)serializedClass:null;
    }

    public static CustomActivityOnCrash.EventListener getEventListenerFromIntent(Intent intent) {
        Serializable serializedClass = intent.getSerializableExtra("cat.ereza.customactivityoncrash.EXTRA_EVENT_LISTENER");
        return serializedClass != null && serializedClass instanceof CustomActivityOnCrash.EventListener?(CustomActivityOnCrash.EventListener)serializedClass:null;
    }

    /** @deprecated */
    @Deprecated
    public static void restartApplicationWithIntent(Activity activity, Intent intent) {
        restartApplicationWithIntent(activity, intent, (CustomActivityOnCrash.EventListener)null);
    }

    public static void restartApplicationWithIntent(Activity activity, Intent intent, CustomActivityOnCrash.EventListener eventListener) {
        intent.addFlags(268468224);
        if(eventListener != null) {
            eventListener.onRestartAppFromErrorActivity();
        }

        activity.finish();
        activity.startActivity(intent);
        killCurrentProcess();
    }

    /** @deprecated */
    @Deprecated
    public static void closeApplication(Activity activity) {
        closeApplication(activity, (CustomActivityOnCrash.EventListener)null);
    }

    public static void closeApplication(Activity activity, CustomActivityOnCrash.EventListener eventListener) {
        if(eventListener != null) {
            eventListener.onCloseAppFromErrorActivity();
        }

        activity.finish();
        killCurrentProcess();
    }

    public static boolean isLaunchErrorActivityWhenInBackground() {
        return launchErrorActivityWhenInBackground;
    }

    public static void setLaunchErrorActivityWhenInBackground(boolean launchErrorActivityWhenInBackground) {
        CustomActivityOnCrash.launchErrorActivityWhenInBackground = launchErrorActivityWhenInBackground;
    }

    public static boolean isShowErrorDetails() {
        return showErrorDetails;
    }

    public static void setShowErrorDetails(boolean showErrorDetails) {
        CustomActivityOnCrash.showErrorDetails = showErrorDetails;
    }

    public static int getDefaultErrorActivityDrawable() {
        return defaultErrorActivityDrawableId;
    }

    public static void setDefaultErrorActivityDrawable(int defaultErrorActivityDrawableId) {
        CustomActivityOnCrash.defaultErrorActivityDrawableId = defaultErrorActivityDrawableId;
    }

    public static boolean isEnableAppRestart() {
        return enableAppRestart;
    }

    public static void setEnableAppRestart(boolean enableAppRestart) {
        CustomActivityOnCrash.enableAppRestart = enableAppRestart;
    }

    public static Class<? extends Activity> getErrorActivityClass() {
        return errorActivityClass;
    }

    public static void setErrorActivityClass(Class<? extends Activity> errorActivityClass) {
        CustomActivityOnCrash.errorActivityClass = errorActivityClass;
    }

    public static Class<? extends Activity> getRestartActivityClass() {
        return restartActivityClass;
    }

    public static void setRestartActivityClass(Class<? extends Activity> restartActivityClass) {
        CustomActivityOnCrash.restartActivityClass = restartActivityClass;
    }

    public static CustomActivityOnCrash.EventListener getEventListener() {
        return eventListener;
    }

    public static void setEventListener(CustomActivityOnCrash.EventListener eventListener) {
        if(eventListener != null && eventListener.getClass().getEnclosingClass() != null && !Modifier.isStatic(eventListener.getClass().getModifiers())) {
            throw new IllegalArgumentException("The event listener cannot be an inner or anonymous class, because it will need to be serialized. Change it to a class of its own, or make it a static inner class.");
        } else {
            CustomActivityOnCrash.eventListener = eventListener;
        }
    }

    private static boolean isStackTraceLikelyConflictive(Throwable throwable, Class<? extends Activity> activityClass) {
        do {
            StackTraceElement[] stackTrace = throwable.getStackTrace();
            StackTraceElement[] var3 = stackTrace;
            int var4 = stackTrace.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                StackTraceElement element = var3[var5];
                if(element.getClassName().equals("android.app.ActivityThread") && element.getMethodName().equals("handleBindApplication") || element.getClassName().equals(activityClass.getName())) {
                    return true;
                }
            }
        } while((throwable = throwable.getCause()) != null);

        return false;
    }

    private static String getBuildDateAsString(Context context, DateFormat dateFormat) {
        String buildDate;
        try {
            ApplicationInfo e = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
            ZipFile zf = new ZipFile(e.sourceDir);
            ZipEntry ze = zf.getEntry("classes.dex");
            long time = ze.getTime();
            buildDate = dateFormat.format(new Date(time));
            zf.close();
        } catch (Exception var8) {
            buildDate = "Unknown";
        }

        return buildDate;
    }

    private static String getVersionName(Context context) {
        try {
            PackageInfo e = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return e.versionName;
        } catch (Exception var2) {
            return "Unknown";
        }
    }

    private static String getDeviceModelName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        return model.startsWith(manufacturer)?capitalize(model):capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String s) {
        if(s != null && s.length() != 0) {
            char first = s.charAt(0);
            return Character.isUpperCase(first)?s:Character.toUpperCase(first) + s.substring(1);
        } else {
            return "";
        }
    }

    private static Class<? extends Activity> guessRestartActivityClass(Context context) {
        Class resolvedActivityClass = getRestartActivityClassWithIntentFilter(context);
        if(resolvedActivityClass == null) {
            resolvedActivityClass = getLauncherActivity(context);
        }

        return resolvedActivityClass;
    }

    private static Class<? extends Activity> getRestartActivityClassWithIntentFilter(Context context) {
        Intent searchedIntent = (new Intent()).setAction("cat.ereza.customactivityoncrash.RESTART").setPackage(context.getPackageName());
        List resolveInfos = context.getPackageManager().queryIntentActivities(searchedIntent, 64);
        if(resolveInfos != null && resolveInfos.size() > 0) {
            ResolveInfo resolveInfo = (ResolveInfo)resolveInfos.get(0);

            try {
                return (Class<? extends Activity>) Class.forName(resolveInfo.activityInfo.name);
            } catch (ClassNotFoundException var5) {
                Log.e("CustomActivityOnCrash", "Failed when resolving the restart activity class via intent filter, stack trace follows!", var5);
            }
        }

        return null;
    }

    private static Class<? extends Activity> getLauncherActivity(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if(intent != null) {
            try {
                return (Class<? extends Activity>) Class.forName(intent.getComponent().getClassName());
            } catch (ClassNotFoundException var3) {
                Log.e("CustomActivityOnCrash", "Failed when resolving the restart activity class via getLaunchIntentForPackage, stack trace follows!", var3);
            }
        }

        return null;
    }

    private static Class<? extends Activity> guessErrorActivityClass(Context context) {
        Class resolvedActivityClass = getErrorActivityClassWithIntentFilter(context);
        if(resolvedActivityClass == null) {
            resolvedActivityClass = DefaultErrorActivity.class;
        }

        return resolvedActivityClass;
    }

    private static Class<? extends Activity> getErrorActivityClassWithIntentFilter(Context context) {
        Intent searchedIntent = (new Intent()).setAction("cat.ereza.customactivityoncrash.ERROR").setPackage(context.getPackageName());
        List resolveInfos = context.getPackageManager().queryIntentActivities(searchedIntent, 64);
        if(resolveInfos != null && resolveInfos.size() > 0) {
            ResolveInfo resolveInfo = (ResolveInfo)resolveInfos.get(0);

            try {
                return (Class<? extends Activity>) Class.forName(resolveInfo.activityInfo.name);
            } catch (ClassNotFoundException var5) {
                Log.e("CustomActivityOnCrash", "Failed when resolving the error activity class via intent filter, stack trace follows!", var5);
            }
        }

        return null;
    }

    private static void killCurrentProcess() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

    private static void setLastCrashTimestamp(Context context, long timestamp) {
        context.getSharedPreferences("custom_activity_on_crash", 0).edit().putLong("last_crash_timestamp", timestamp).commit();
    }

    private static long getLastCrashTimestamp(Context context) {
        return context.getSharedPreferences("custom_activity_on_crash", 0).getLong("last_crash_timestamp", -1L);
    }

    private static boolean hasCrashedInTheLastSeconds(Context context) {
        long lastTimestamp = getLastCrashTimestamp(context);
        long currentTimestamp = (new Date()).getTime();
        return lastTimestamp <= currentTimestamp && currentTimestamp - lastTimestamp < 2000L;
    }

    public interface EventListener extends Serializable {
        void onLaunchErrorActivity();

        void onRestartAppFromErrorActivity();

        void onCloseAppFromErrorActivity();
    }

}
