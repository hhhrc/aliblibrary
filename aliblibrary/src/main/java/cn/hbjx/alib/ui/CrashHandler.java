package cn.hbjx.alib.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;

import cn.hbjx.alib.util.Lg;

/**
 * Created by DengYiQian on 2017/6/6.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    public static CrashHandler instance = new CrashHandler();
    private Context cxt;
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return instance;
    }

    public void init(Context cxt) {
        this.cxt = cxt;
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void uncaughtException(Thread thread, Throwable ex) {
        Lg.println("*************uncaughtException");
        StringWriter result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        StackTraceElement[] trace = ex.getStackTrace();
        StackTraceElement[] trace2 = new StackTraceElement[trace.length + 3];
        System.arraycopy(trace, 0, trace2, 0, trace.length);
        trace2[trace.length + 0] = new StackTraceElement("Android", "MODEL", Build.MODEL, -1);
        trace2[trace.length + 1] = new StackTraceElement("Android", "VERSION", Build.VERSION.RELEASE, -1);
        trace2[trace.length + 2] = new StackTraceElement("Android", "FINGERPRINT", Build.FINGERPRINT, -1);
        ex.setStackTrace(trace2);
        ex.printStackTrace(printWriter);
        final String stacktrace = result.toString();
        printWriter.close();
        Lg.println(stacktrace);
        (new Thread() {
            public void run() {
                Looper.prepare();
                Lg.println(stacktrace);
                Toast.makeText(CrashHandler.this.cxt.getApplicationContext(), "很抱歉,程序出现异常,即将退出.", 0).show();
                CrashHandler.this.showDialog(CrashHandler.this.cxt, stacktrace);
                Looper.loop();
            }
        }).start();
    }

    private void showDialog(Context context, final String msg) {
        final AlertDialog.Builder buder = new AlertDialog.Builder(context);
        buder.setTitle("崩溃异常");
        buder.setMessage(msg);
        buder.setPositiveButton("邮件反馈", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                CrashHandler.this.sendMailByIntent(buder.getContext(), msg, "fyygw@126.com");
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });
        buder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });
        AlertDialog dialog = buder.create();
        dialog.getWindow().setType(2003);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        Log.i("PLog", "2");
    }

    public void sendMailByIntent(Context context, String msg, String email) {
        Intent data = new Intent("android.intent.action.SENDTO");
        data.setFlags(268435456);
        data.setData(Uri.parse("mailto:" + email));
        data.putExtra("android.intent.extra.SUBJECT", "[意见反馈]");
        data.putExtra("android.intent.extra.TEXT", msg);
        this.cxt.startActivity(data);
    }

}
