package cn.hbjx.alib.ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by DengYiQian on 2017/6/6.
 */

public abstract class BaseComponent {

    protected Activity activity;
    protected View view;
    private ViewGroup root;
    private View _view;
    private int offset = -1;
    private boolean isDisplay = false;

    public BaseComponent(Activity activity, int resId) {
        this.activity = activity;
        this.view = LayoutInflater.from(activity).inflate(this.onCreate(), (ViewGroup)null);
        this._view = this.view;
        this.root = (ViewGroup)activity.findViewById(resId);
        this.root.addView(this.view);
        this.initComp();
        this.initListener();
    }

    public BaseComponent(Activity activity, View v) {
        if(v instanceof ViewGroup) {
            this.activity = activity;
            this.view = LayoutInflater.from(activity).inflate(this.onCreate(), (ViewGroup)null);
            this._view = this.view;
            this.root = (ViewGroup)v;
            this.root.addView(this.view);
            this.initComp();
            this.initListener();
            this.initData();
        }

    }

    public BaseComponent(Activity activity) {
        this.activity = activity;
        this.view = LayoutInflater.from(activity).inflate(this.onCreate(), (ViewGroup)null);
        this._view = this.view;
        this._view.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        this.initComp();
        this.initListener();
        this.initData();
    }

    public abstract int onCreate();

    public abstract void initComp();

    public abstract void initListener();

    public abstract void initData();

    public View findViewById(int id) {
        return this.view.findViewById(id);
    }

    public ViewGroup getRoot() {
        return this.root;
    }

    public int getOffset() {
        return this.offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public boolean isDisplay() {
        return this.isDisplay;
    }

    public void setDisplay(boolean isDisplay) {
        this.isDisplay = isDisplay;
    }

    public View getView() {
        return this._view;
    }

}
