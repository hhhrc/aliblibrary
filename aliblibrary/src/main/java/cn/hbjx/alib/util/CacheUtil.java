package cn.hbjx.alib.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Date;

/**
 * Created by DengYiQian on 2017/6/6.
 */

public class CacheUtil {

    public static String cacheKey = "";
    public static final long CACHE_SECOND = 3600L;
    public static final String DATA_CACHE = "data_cache_" + CacheUtil.class.getName();
    public static final String INTERFACE_CACHE = "interface_cache_" + CacheUtil.class.getName();
    public static final String FOREVER_CACHE = "forever_cache_" + CacheUtil.class.getName();
    public static Context context = null;
    public static Gson g = new Gson();
    private static boolean isTimeCache = false;

    public CacheUtil() {
    }

    public static void isTimeCache(boolean b) {
        isTimeCache = b;
    }

    public static void saveInteger(String key, int b) {
        saveInteger(key, b, true);
    }

    public static void saveInteger(String key, int b, boolean mutable) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DATA_CACHE, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(mutable) {
            editor.putInt(key + cacheKey, b);
        } else {
            editor.putInt(key, b);
        }

        editor.commit();
    }

    public static int getInteger(String key) {
        return getInteger(key, true);
    }

    public static int getInteger(String key, boolean mutable) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DATA_CACHE, 0);
        return mutable?sharedPreferences.getInt(key + cacheKey, -1):sharedPreferences.getInt(key, -1);
    }

    public static void saveBoolean(String key, boolean b) {
        saveBoolean(key, b, true);
    }

    public static void saveBoolean(String key, boolean b, boolean mutable) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DATA_CACHE, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(mutable) {
            editor.putBoolean(key + cacheKey, b);
        } else {
            editor.putBoolean(key, b);
        }

        editor.commit();
    }

    public static boolean getBoolean(String key) {
        return getBoolean(key, true);
    }

    public static boolean getBoolean(String key, boolean mutable) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DATA_CACHE, 0);
        return mutable?sharedPreferences.getBoolean(key + cacheKey, false):sharedPreferences.getBoolean(key, false);
    }

    public static void saveObject(String key, Object object) {
        saveObject(key, object, DATA_CACHE, true);
    }

    public static void saveObject(String key, Object object, boolean mutable) {
        saveObject(key, object, DATA_CACHE, mutable);
    }

    public static void saveInterfaceObject(String key, Object object) {
        saveObject(key, object, INTERFACE_CACHE, true);
    }

    public static void saveInterfaceObject(String key, Object object, boolean mutable) {
        saveObject(key, object, INTERFACE_CACHE, mutable);
    }

    private static void saveObject(String key, Object object, String filename, boolean mutable) {
        CacheBody body = new CacheBody();
        body.d = new Date();
        body.obj = object;
        SharedPreferences sharedPreferences = context.getSharedPreferences(filename, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            ObjectOutputStream e = new ObjectOutputStream(baos);
            e.writeObject(body);
            String strList = new String(Base64.encode(baos.toByteArray(), 0));
            if(mutable) {
                editor.putString(key + cacheKey, strList);
            } else {
                editor.putString(key, strList);
            }

            editor.commit();
            e.close();
        } catch (IOException var18) {
            var18.printStackTrace();
        } finally {
            try {
                baos.close();
            } catch (IOException var17) {
                var17.printStackTrace();
            }

        }

    }

    public static Object getObject(String key) {
        return getObject(key, DATA_CACHE, true);
    }

    public static Object getObject(String key, boolean mutable) {
        return getObject(key, DATA_CACHE, mutable);
    }

    public static Object getInterfaceObject(String key) {
        return getObject(key, INTERFACE_CACHE, true);
    }

    public static Object getInterfaceObject(String key, boolean mutable) {
        return getObject(key, INTERFACE_CACHE, mutable);
    }

    private static Object getObject(String key, String filename, boolean mutable) {
        CacheBody body = null;
        Object result = null;
        Date d1 = new Date();
        Date d0 = null;
        SharedPreferences sharedPreferences = context.getSharedPreferences(filename, 0);
        String message;
        if(mutable) {
            message = sharedPreferences.getString(key + cacheKey, "");
        } else {
            message = sharedPreferences.getString(key, "");
        }

        if(message.equals("")) {
            return null;
        } else {
            byte[] buffer = Base64.decode(message.getBytes(), 0);
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);

            try {
                ObjectInputStream e;
                if(isTimeCache) {
                    e = new ObjectInputStream(bais);
                    body = (CacheBody)e.readObject();
                    e.close();
                    d0 = body.d;
                    int e1 = (int)((d1.getTime() - d0.getTime()) / 3600000L);
                    int minutes = (int)(((d1.getTime() - d0.getTime()) / 1000L - (long)(e1 * 3600)) / 60L);
                    int second = (int)((d1.getTime() - d0.getTime()) / 1000L - (long)(e1 * 3600) - (long)(minutes * 60));
                    if((long)Math.abs(second) <= 3600L) {
                        result = body.obj;
                    } else {
                        result = null;
                    }
                } else {
                    e = new ObjectInputStream(bais);
                    body = (CacheBody)e.readObject();
                    e.close();
                    result = body.obj;
                }

                Object e2 = result;
                return e2;
            } catch (StreamCorruptedException var27) {
                var27.printStackTrace();
            } catch (IOException var28) {
                var28.printStackTrace();
            } catch (ClassNotFoundException var29) {
                var29.printStackTrace();
            } finally {
                try {
                    bais.close();
                } catch (IOException var26) {
                    var26.printStackTrace();
                }

            }

            return null;
        }
    }

    public static void clearInterfaceCache() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(INTERFACE_CACHE, 0);
        sharedPreferences.edit().clear().commit();
    }

    public static void clearDataCache() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DATA_CACHE, 0);
        sharedPreferences.edit().clear().commit();
    }

    public static void clear(String name) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name + cacheKey, 0);
        sharedPreferences.edit().clear().commit();
    }

    public static void saveForeverObject(String key, Object object) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FOREVER_CACHE, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            ObjectOutputStream e = new ObjectOutputStream(baos);
            e.writeObject(object);
            String strList = new String(Base64.encode(baos.toByteArray(), 0));
            editor.putString(key + cacheKey, strList);
            editor.commit();
            e.close();
        } catch (IOException var15) {
            var15.printStackTrace();
        } finally {
            try {
                baos.close();
            } catch (IOException var14) {
                var14.printStackTrace();
            }

        }

    }

    public static Object getForeverObject(String key) {
        Object result = null;
        SharedPreferences sharedPreferences = context.getSharedPreferences(FOREVER_CACHE, 0);
        if(sharedPreferences == null) {
            return null;
        } else {
            String message = sharedPreferences.getString(key + cacheKey, "");
            if(message.equals("")) {
                return null;
            } else {
                byte[] buffer = Base64.decode(message.getBytes(), 0);
                ByteArrayInputStream bais = new ByteArrayInputStream(buffer);

                try {
                    ObjectInputStream e = new ObjectInputStream(bais);
                    result = e.readObject();
                    e.close();
                    Object var7 = result;
                    return var7;
                } catch (StreamCorruptedException var21) {
                    var21.printStackTrace();
                } catch (IOException var22) {
                    var22.printStackTrace();
                } catch (ClassNotFoundException var23) {
                    var23.printStackTrace();
                } finally {
                    try {
                        bais.close();
                    } catch (IOException var20) {
                        var20.printStackTrace();
                    }

                }

                return null;
            }
        }
    }

}
