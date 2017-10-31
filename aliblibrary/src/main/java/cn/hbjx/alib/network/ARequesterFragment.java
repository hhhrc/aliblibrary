package cn.hbjx.alib.network;

import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by DengYiQian on 2017/6/6.
 */

public class ARequesterFragment extends Fragment implements IRequesterManagerCallback{

    protected ArrayList<ABaseAndroidRequester> _tasks = new ArrayList();

    public ARequesterFragment() {
    }

    public void _networkFinished(ABaseAndroidRequester requester) {
        this._tasks.remove(requester);
    }

    public void _networkStart(ABaseAndroidRequester requester) {
        this._tasks.add(requester);
    }

    public void onStop() {
        super.onStop();
        Iterator var1 = this._tasks.iterator();

        while(var1.hasNext()) {
            ABaseAndroidRequester task = (ABaseAndroidRequester)var1.next();
            task.cancel();
        }

        this._tasks.clear();
    }

}
