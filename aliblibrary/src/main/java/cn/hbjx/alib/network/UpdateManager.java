package cn.hbjx.alib.network;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.hbjx.alib.util.Lg;

/**
 * Created by DengYiQian on 2017/6/6.
 */

public class UpdateManager {

    private Context mContext;
    private String apkUrl = "";
    private Dialog noticeDialog;
    private Dialog downloadDialog;
    private static final String savePath = "/sdcard/";
    private static final String saveFileName = "/sdcard/bankofchina.apk";
    private ProgressBar mProgress;
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private int progress;
    private Thread downLoadThread;
    private boolean interceptFlag = false;
    private UpdateManager.IUpdateManager listener;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 1:
                    UpdateManager.this.mProgress.setProgress(UpdateManager.this.progress);
                    break;
                case 2:
                    UpdateManager.this.installApk();
            }

        }
    };
    private Runnable mdownApkRunnable = new Runnable() {
        public void run() {
            try {
                URL e = new URL(UpdateManager.this.apkUrl);
                HttpURLConnection conn = (HttpURLConnection)e.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();
                File file = new File("/sdcard/");
                if(!file.exists()) {
                    file.mkdir();
                }

                String apkFile = "/sdcard/bankofchina.apk";
                File ApkFile = new File(apkFile);
                FileOutputStream fos = new FileOutputStream(ApkFile);
                int count = 0;
                byte[] buf = new byte[1024];

                do {
                    int numread = is.read(buf);
                    count += numread;
                    UpdateManager.this.progress = (int)((float)count / (float)length * 100.0F);
                    UpdateManager.this.mHandler.sendEmptyMessage(1);
                    if(numread <= 0) {
                        UpdateManager.this.mHandler.sendEmptyMessage(2);
                        break;
                    }

                    fos.write(buf, 0, numread);
                } while(!UpdateManager.this.interceptFlag);

                fos.close();
                is.close();
            } catch (MalformedURLException var12) {
                var12.printStackTrace();
            } catch (IOException var13) {
                var13.printStackTrace();
            }

        }
    };

    public UpdateManager(Context context) {
        this.mContext = context;
    }

    public void startUpdateInfo(String url, String date, String msg, boolean flag, UpdateManager.IUpdateManager listener) {
        this.apkUrl = url;
        this.listener = listener;
        this.showNoticeDialog(date, msg, flag);
    }

    @SuppressLint({"NewApi"})
    private void showNoticeDialog(String date, String msg, final boolean flag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setTitle("发现新版本 " + date + "");
        builder.setMessage(msg);
        builder.setPositiveButton("升级", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                UpdateManager.this.showDownloadDialog();
            }
        });
        if(!flag) {
            builder.setNegativeButton("忽略", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Lg.println(Boolean.valueOf(flag));
                    dialog.dismiss();
                }
            });
        }

        builder.setCancelable(!flag);
        this.noticeDialog = builder.create();
        this.noticeDialog.show();
    }

    private void showDownloadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setTitle("下载");
        this.mProgress = new ProgressBar(this.mContext, (AttributeSet)null, 16842872);
        this.mProgress.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        builder.setView(this.mProgress);
        builder.setCancelable(false);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                UpdateManager.this.interceptFlag = true;
            }
        });
        this.downloadDialog = builder.create();
        this.downloadDialog.show();
        this.downloadApk();
    }

    private void downloadApk() {
        this.downLoadThread = new Thread(this.mdownApkRunnable);
        this.downLoadThread.start();
    }

    private void installApk() {
        File apkfile = new File("/sdcard/bankofchina.apk");
        if(apkfile.exists()) {
            Intent i = new Intent("android.intent.action.VIEW");
            i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
            this.mContext.startActivity(i);
        }
    }

    public interface IUpdateManager {
        void updateListener();
    }

}
