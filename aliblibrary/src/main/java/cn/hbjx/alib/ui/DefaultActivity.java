package cn.hbjx.alib.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;

import com.google.gson.Gson;

import org.simple.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.hbjx.alib.util.AUtil;
import cn.hbjx.alib.util.Lg;

/**
 * Created by DengYiQian on 2017/6/6.
 */

public class DefaultActivity extends MediaActivity{

    public float SCREEN_WIDTH;
    public float SCREEN_HEIGHT;
    public Gson g = new Gson();
    public static final String IMAGE_PATH;
    private long _firstTime = 0L;
    private LoadingDialog progress;
    private boolean isActive = true;
    private boolean isDefaultLoadingDialog = true;
    protected BroadcastReceiver mShowProgressReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("msg");
            if(msg != null && !msg.trim().equals("")) {
                DefaultActivity.this.showProgress(msg);
            } else {
                DefaultActivity.this.showProgress();
            }

        }
    };
    protected BroadcastReceiver mHideProgressReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            DefaultActivity.this.hideProgress();
        }
    };
    protected BroadcastReceiver mCloseAllActivityReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            DefaultActivity.this.finish();
        }
    };
    private boolean progressShow;

    public DefaultActivity() {
    }

    public DefaultActivity getActivity() {
        return this;
    }

    public void setDefaultLoadingDialog(boolean b) {
        this.isDefaultLoadingDialog = b;
    }

    public void startActivity(Class cls) {
        this.startActivity(new Intent(this, cls));
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.SCREEN_WIDTH = (float)metrics.widthPixels;
        this.SCREEN_HEIGHT = (float)metrics.heightPixels;
        this.progress = new LoadingDialog(this);
        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(AUtil.SHOW_PROGRESS);
        this.registerReceiver(this.mShowProgressReceiver, filter1);
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(AUtil.HIDE_PROGRESS);
        this.registerReceiver(this.mHideProgressReceiver, filter2);
        IntentFilter filter3 = new IntentFilter();
        filter3.addAction(AUtil.ACTIVITYS_CLOSE);
        this.registerReceiver(this.mCloseAllActivityReceiver, filter3);
        EventBus.getDefault().register(this);
    }

    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        this.unregisterReceiver(this.mShowProgressReceiver);
        this.unregisterReceiver(this.mHideProgressReceiver);
        this.unregisterReceiver(this.mCloseAllActivityReceiver);
    }

    public void showProgress(String msg) {
        if(!this.isFinishing() && this.isActive && this.isDefaultLoadingDialog) {
            this.progress.show();
        }

    }

    public void showProgress() {
        if(!this.isFinishing() && this.isActive && this.isDefaultLoadingDialog) {
            this.progress.show();
        }

    }

    public void hideProgress() {
        this.progress.hide();
    }

    protected void onPause() {
        super.onPause();
        this.isActive = false;
    }

    protected void onResume() {
        super.onResume();
        this.isActive = true;
    }

    public void finish() {
        if(this.progress != null) {
            this.progress.hide();
            this.progress.cancel();
        }

        super.finish();
    }

    protected void onStart() {
        super.onStart();
        this.init();
    }

    protected void init() {
        this.initComp();
        this.initListener();
        this.initData();
    }

    protected void initComp() {
    }

    protected void initListener() {
    }

    protected void initData() {
    }

    public void onStop() {
        super.onStop();
    }

    public String createImageThumbnail(String srcPath, String fileName, Context ct) {
        if(srcPath == null) {
            return null;
        } else {
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            newOpts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(srcPath, newOpts);
            newOpts.inJustDecodeBounds = false;
            float w = (float)newOpts.outWidth;
            float h = (float)newOpts.outHeight;
            float whbe = w / h;
            short hh = 960;
            short ww = 640;
            int be = 1;
            if(w > h && w > (float)ww) {
                be = newOpts.outWidth / ww;
            } else if(w < h && h > (float)hh) {
                be = newOpts.outHeight / hh;
            }

            if(be <= 0) {
                be = 1;
            }

            newOpts.inSampleSize = be;
            if(w > (float)ww) {
                h = (float)((int)((float)ww / whbe));
                w = (float)ww;
            }

            Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
            if(bitmap != null) {
                bitmap = Bitmap.createScaledBitmap(bitmap, (int)w, (int)h, true);
                return bitmap != null?this.saveBitmap(bitmap, fileName):null;
            } else {
                return null;
            }
        }
    }

    public String saveBitmap(Bitmap bitmap, String bitName) {
        if(bitmap != null) {
            File cybercareMain = new File(IMAGE_PATH);
            if(!cybercareMain.exists()) {
                cybercareMain.mkdirs();
            }

            File f = new File(IMAGE_PATH + bitName);

            try {
                f.createNewFile();
            } catch (IOException var10) {
                var10.printStackTrace();
            }

            FileOutputStream fOut = null;

            try {
                fOut = new FileOutputStream(f);
            } catch (FileNotFoundException var9) {
                var9.printStackTrace();
            }

            if(fOut != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fOut);
            }

            try {
                fOut.flush();
            } catch (IOException var8) {
                var8.printStackTrace();
            }

            try {
                fOut.close();
            } catch (IOException var7) {
                var7.printStackTrace();
            }

            return f.getAbsolutePath();
        } else {
            return bitName;
        }
    }

    public DefaultActivity getAct() {
        return this;
    }

    public void exitApp() {
        this.printLog();
        long secondTime = System.currentTimeMillis();
        if(secondTime - this._firstTime > 2000L) {
            this.toast("再按一次退出程序...");
            this._firstTime = secondTime;
        } else {
            this.finish();
        }
    }

    private void printLog() {
        Lg.println(Thread.currentThread().getStackTrace()[3].getMethodName());
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    static {
        IMAGE_PATH = Environment.getExternalStorageDirectory().toString() + File.separator + "childword" + File.separator + "Images" + File.separator;
    }

}
