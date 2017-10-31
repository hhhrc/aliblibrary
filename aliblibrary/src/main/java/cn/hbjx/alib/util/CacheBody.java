package cn.hbjx.alib.util;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by DengYiQian on 2017/6/6.
 */

public class CacheBody implements Serializable {
    private static final long serialVersionUID = 4653948707335338906L;
    public Date d;
    public Object obj;

    CacheBody() {
    }
}
