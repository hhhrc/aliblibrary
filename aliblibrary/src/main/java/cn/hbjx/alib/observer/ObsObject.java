package cn.hbjx.alib.observer;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by DengYiQian on 2017/6/6.
 */

public class ObsObject<T> implements IObsObject<T> {

    private T obj;
    private IdentityHashMap<Object, IObsListener<T>> listeners = new IdentityHashMap();
    private ArrayList<IObsListener<T>> _listeners = new ArrayList();
    private IObsListener<T> singoListener = null;

    public ObsObject() {
    }

    public void setObject(T o) {
        this.obj = o;
        Iterator var2 = this.listeners.entrySet().iterator();

        while(var2.hasNext()) {
            Map.Entry listener = (Map.Entry)var2.next();
            ((IObsListener)listener.getValue()).update(this.obj);
        }

        var2 = this._listeners.iterator();

        while(var2.hasNext()) {
            IObsListener listener1 = (IObsListener)var2.next();
            listener1.update(this.obj);
        }

        if(this.singoListener != null) {
            this.singoListener.update(this.obj);
        }

    }

    public T getObject() {
        return this.obj;
    }

    public void addObserverListener(Object obs, IObsListener listener) {
        this.listeners.put(obs, listener);
    }

    public void removeObserverListener(Object obs, IObsListener listener) {
        this.listeners.remove(obs);
    }

    public void addObserverListener(IObsListener listener) {
        this._listeners.add(listener);
    }

    public void removeObserverListener(IObsListener listener) {
        this._listeners.remove(listener);
    }

    public void setObserverListener(IObsListener listener) {
        this.singoListener = listener;
    }

}
