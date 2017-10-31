package cn.hbjx.alib.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Locale;

/**
 * Created by DengYiQian on 2017/6/6.
 */

public class UnCaughtException implements Thread.UncaughtExceptionHandler {

    private Context context;
    private static Context context1;

    public UnCaughtException(Context ctx) {
        this.context = ctx;
        context1 = ctx;
    }

    private StatFs getStatFs() {
        File path = Environment.getDataDirectory();
        return new StatFs(path.getPath());
    }

    private long getAvailableInternalMemorySize(StatFs stat) {
        long blockSize = (long)stat.getBlockSize();
        long availableBlocks = (long)stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    private long getTotalInternalMemorySize(StatFs stat) {
        long blockSize = (long)stat.getBlockSize();
        long totalBlocks = (long)stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    private void addInformation(StringBuilder message) {
        message.append("Locale: ").append(Locale.getDefault()).append('\n');

        try {
            PackageManager stat = this.context.getPackageManager();
            PackageInfo pi = stat.getPackageInfo(this.context.getPackageName(), 0);
            message.append("Version: ").append(pi.versionName).append('\n');
            message.append("Package: ").append(pi.packageName).append('\n');
        } catch (Exception var4) {
            Log.e("CustomExceptionHandler", "Error", var4);
            message.append("Could not get Version information for ").append(this.context.getPackageName());
        }

        message.append("Phone Model: ").append(Build.MODEL).append('\n');
        message.append("Android Version: ").append(Build.VERSION.RELEASE).append('\n');
        message.append("Board: ").append(Build.BOARD).append('\n');
        message.append("Brand: ").append(Build.BRAND).append('\n');
        message.append("Device: ").append(Build.DEVICE).append('\n');
        message.append("Host: ").append(Build.HOST).append('\n');
        message.append("ID: ").append(Build.ID).append('\n');
        message.append("Model: ").append(Build.MODEL).append('\n');
        message.append("Product: ").append(Build.PRODUCT).append('\n');
        message.append("Type: ").append(Build.TYPE).append('\n');
        StatFs stat1 = this.getStatFs();
        message.append("Total Internal memory: ").append(this.getTotalInternalMemorySize(stat1)).append('\n');
        message.append("Available Internal memory: ").append(this.getAvailableInternalMemorySize(stat1)).append('\n');
    }

    public void uncaughtException(Thread t, Throwable e) {
        try {
            StringBuilder ignore = new StringBuilder();
            Date curDate = new Date();
            ignore.append("Error Report collected on : ").append(curDate.toString()).append('\n').append('\n');
            ignore.append("Informations :").append('\n');
            this.addInformation(ignore);
            ignore.append('\n').append('\n');
            ignore.append("Stack:\n");
            StringWriter result = new StringWriter();
            PrintWriter printWriter = new PrintWriter(result);
            e.printStackTrace(printWriter);
            ignore.append(result.toString());
            printWriter.close();
            ignore.append('\n');
            ignore.append("**** End of current Report ***");
            Log.e(UnCaughtException.class.getName(), "Error while sendErrorMail" + ignore);
            this.sendErrorMail(ignore);
        } catch (Throwable var7) {
            Log.e(UnCaughtException.class.getName(), "Error while sending error e-mail", var7);
        }

    }

    public void sendErrorMail(final StringBuilder errorContent) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        (new Thread() {
            public void run() {
                Looper.prepare();
                builder.setTitle("Sorry...!");
                builder.create();
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                });
                builder.setPositiveButton("Report", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent sendIntent = new Intent("android.intent.action.SEND");
                        String subject = "Your App crashed! Fix it!";
                        StringBuilder body = new StringBuilder("Yoddle");
                        body.append('\n').append('\n');
                        body.append(errorContent).append('\n').append('\n');
                        sendIntent.setType("message/rfc822");
                        sendIntent.putExtra("android.intent.extra.EMAIL", new String[]{"coderzheaven@gmail.com"});
                        sendIntent.putExtra("android.intent.extra.TEXT", body.toString());
                        sendIntent.putExtra("android.intent.extra.SUBJECT", subject);
                        sendIntent.setType("message/rfc822");
                        UnCaughtException.context1.startActivity(sendIntent);
                        System.exit(0);
                    }
                });
                builder.setMessage("Oops,Your application has crashed");
                builder.show();
                Looper.loop();
            }
        }).start();
    }

}
