package cn.hbjx.alib.observer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by DengYiQian on 2017/6/6.
 */

public interface IObsArray<T> {

    List<T> getList();

    void setList(ArrayList<T> var1);

    void add(T var1);

    void remove(T var1);

    void remove(int var1);

    void add(int var1, T var2);

    void add(Collection<T> var1);

    void add(int var1, Collection<T> var2);

    void addObserverListener(IObsListener var1);

    void setObserverListener(IObsListener var1);

    void removeObserverListener(IObsListener var1);

}
