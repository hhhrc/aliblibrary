package cn.hbjx.alib.network;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by DengYiQian on 2017/6/6.
 */

public class ARequesterActivity extends AppCompatActivity implements IRequesterManagerCallback{

    protected ArrayList<ABaseAndroidRequester> _arequester_tasks = new ArrayList();

    private final String TAG = ARequesterActivity.class.getSimpleName();

    public ARequesterActivity() {
    }

    public void _networkFinished(ABaseAndroidRequester requester) {
        this._arequester_tasks.remove(requester);
    }

    public void _networkStart(ABaseAndroidRequester requester) {
        this._arequester_tasks.add(requester);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"onCreate()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG,"onRestart()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG,"onPause()");
    }

    @Override
    public void onStop() {
        Log.i(TAG,"onStop()");
        Iterator var1 = this._arequester_tasks.iterator();

        while(var1.hasNext()) {
            ABaseAndroidRequester task = (ABaseAndroidRequester)var1.next();
            task.cancel();
        }

        this._arequester_tasks.clear();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy()");
    }
}
