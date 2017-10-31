package cn.hbjx.alib.observer;

/**
 * Created by DengYiQian on 2017/6/6.
 */

public interface IObsObject<T> {

    void setObject(T var1);

    T getObject();

    void addObserverListener(Object var1, IObsListener var2);

    void removeObserverListener(Object var1, IObsListener var2);

    void addObserverListener(IObsListener var1);

    void removeObserverListener(IObsListener var1);

    void setObserverListener(IObsListener var1);

}
