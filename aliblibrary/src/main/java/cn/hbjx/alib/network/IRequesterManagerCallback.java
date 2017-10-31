package cn.hbjx.alib.network;

/**
 * Created by DengYiQian on 2017/6/6.
 */

public interface IRequesterManagerCallback {

    void _networkFinished(ABaseAndroidRequester requester);

    void _networkStart(ABaseAndroidRequester requester);

}
