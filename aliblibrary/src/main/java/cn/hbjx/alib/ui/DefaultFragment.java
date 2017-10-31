package cn.hbjx.alib.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import org.simple.eventbus.EventBus;

import cn.hbjx.alib.network.ARequesterFragment;

/**
 * Created by DengYiQian on 2017/6/6.
 */

public class DefaultFragment extends ARequesterFragment {

    public Gson g = new Gson();

    public DefaultFragment() {
    }

    public DefaultActivity getAct() {
        return (DefaultActivity)this.getActivity();
    }

    private void printLog() {
        Log.v("fragment", this + " -> " + Thread.currentThread().getStackTrace()[3].getMethodName());
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.printLog();
        EventBus.getDefault().register(this);
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.printLog();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.printLog();
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.printLog();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.printLog();
    }

    public void onResume() {
        super.onResume();
        this.printLog();
    }

    public void onDestroy() {
        this.printLog();
        super.onDestroy();
    }

    public void onDestroyView() {
        this.printLog();
        super.onDestroyView();
    }

    public void onStart() {
        this.printLog();
        super.onStart();
    }

    public void onStop() {
        this.printLog();
        super.onStop();
    }

    public void onDetach() {
        super.onDetach();
        this.printLog();
        EventBus.getDefault().register(this);
    }

}
