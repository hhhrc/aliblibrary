package cn.hbjx.alib.observer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by DengYiQian on 2017/6/6.
 */

public class ObsArray <T> implements IObsArray<T>{

    private ArrayList<T> list = new ArrayList();
    private ArrayList<IObsListener> listeners = new ArrayList();

    public ObsArray() {
    }

    public List<T> getList() {
        return this.list;
    }

    public void setList(ArrayList<T> array) {
        this.list = array;
        this.update();
    }

    public void add(T t) {
        this.list.add(t);
        this.update();
    }

    public void remove(T t) {
        this.list.remove(t);
        this.update();
    }

    public void remove(int t) {
        this.list.remove(t);
        this.update();
    }

    public void add(int index, T t) {
        this.list.add(index, t);
        this.update();
    }

    public void add(Collection<T> t) {
        this.list.addAll(t);
        this.update();
    }

    public void add(int index, Collection<T> t) {
        this.list.addAll(index, t);
        this.update();
    }

    public void addObserverListener(IObsListener listener) {
        this.listeners.add(listener);
    }

    public void setObserverListener(IObsListener listener) {
        this.listeners.clear();
        if(listener != null) {
            this.listeners.add(listener);
        }

    }

    public void removeObserverListener(IObsListener listener) {
        this.listeners.remove(listener);
    }

    public void removeObserverListeners() {
        this.listeners.clear();
    }

    public void update() {
        if(this.listeners != null && this.listeners.size() != 0) {
            Iterator var1 = this.listeners.iterator();

            while(var1.hasNext()) {
                IObsListener listener = (IObsListener)var1.next();
                listener.update(this.list);
            }

        }
    }

}
