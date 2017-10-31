package cn.hbjx.alib.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URLEncoder;

import cn.hbjx.alib.encryption.Des;
import cn.hbjx.alib.encryption.RSA;
import cn.hbjx.alib.util.AUtil;
import cn.hbjx.alib.util.Lg;

/**
 * Created by DengYiQian on 2017/6/6.
 */

public class ABaseAndroidRequester extends AsyncTask<Void, Void, Object> {

    private boolean isSppend=true;
    protected Http http;
    protected Gson g = new Gson();
    protected IRequester callback;
    protected IRequesterManagerCallback requesterManagerCallback;

    public ABaseAndroidRequester() {}

    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected Object doInBackground(Void... params) {
        String type = "get";
        String url = null;
        Http.DataType dataType = Http.DataType.TYPE1_FORM;
        Annotation[] annotations = this.getClass().getAnnotations();
        Annotation[] fields = annotations;
        int var7 = annotations.length;

        int var8;
        for(var8 = 0; var8 < var7; ++var8) {
            Annotation an = fields[var8];
            if(an instanceof ARequestType) {
                type = ((ARequestType)an).type().trim();
                url = ((ARequestType)an).url();
                dataType = ((ARequestType)an).dataType();
            }
        }

        this.http = new Http(url, type);
        this.http.setDataType(dataType);
        Field[] var19 = this.getClass().getDeclaredFields();
        Field[] var20 = var19;
        var8 = var19.length;

        for(int var21 = 0; var21 < var8; ++var21) {
            Field field = var20[var21];
            ARequestParam annotation = (ARequestParam)field.getAnnotation(ARequestParam.class);
            if(null != annotation) {
                Lg.println(annotation);

                try {
                    Object e = field.get(this);
                    if(e.toString().toLowerCase().indexOf("file://") != -1) {
                        String[] var22 = e.toString().split(";");
                        String[] var14 = var22;
                        int var15 = var22.length;

                        for(int var16 = 0; var16 < var15; ++var16) {
                            String s = var14[var16];
                            if(!s.equals("")) {
                                this.http.addFile(s.replaceAll("file://", ""), field.getName());
                            }
                        }
                    } else {
                        String value;
                        if(annotation.rsa()) {
                            Lg.println(field.getName() + " -> " + e.toString());
                            value = RSA.en(URLEncoder.encode(e.toString(), "UTF-8"));
                        } else {
                            value = e.toString();
                        }
                        http.isSppend(isSppend);
                        http.addParam(field.getName(), value);
                    }
                } catch (Exception var18) {
                    var18.printStackTrace();
                }
            }
        }

        Lg.println(this.http.toString());
        return this.syncObj();
    }

    protected void onPostExecute(Object o) {
        if(this.callback != null) {
            this.callback.callback(o);
        }

        if(this.requesterManagerCallback != null) {
            this.requesterManagerCallback._networkFinished(this);
            if(this.requesterManagerCallback instanceof Context) {
                AUtil.progress((Context)this.requesterManagerCallback, false);
            }
        }

    }

    public void async(IRequester callback) {
        this.callback = callback;
        this.execute(new Void[0]);
    }

    public void async(IRequesterManagerCallback cxt, IRequester callback) {
        this.requesterManagerCallback = cxt;
        if(this.requesterManagerCallback != null) {
            this.requesterManagerCallback._networkStart(this);
            if(this.requesterManagerCallback instanceof Context) {
                AUtil.progress((Context)this.requesterManagerCallback, true);
            }
        }

        this.callback = callback;
        this.execute(new Void[0]);
    }

    public void async(IRequesterManagerCallback cxt, IRequester callback, boolean isSppend) {
        this.requesterManagerCallback = cxt;
        if(this.requesterManagerCallback != null) {
            this.requesterManagerCallback._networkStart(this);
            if(this.requesterManagerCallback instanceof Context) {
                AUtil.progress((Context)this.requesterManagerCallback, true);
            }
        }

        this.callback = callback;
        this.isSppend = isSppend;
        this.execute(new Void[0]);
    }

    protected Object syncObj() {
        String result = this.syncString();
        if(result == null) {
            return result;
        } else {
            Class[] var2 = this.getClass().getClasses();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Class aClass = var2[var4];
                ARequestResult ann = (ARequestResult)aClass.getAnnotation(ARequestResult.class);
                if(ann != null) {
                    Lg.println(ann);
                    if(ann.des()) {
                        try {
                            result = Des.decryptDES(result, Des.key);
                        } catch (Exception var14) {
                            var14.printStackTrace();
                        }
                    }
                    Object o = null;

                    try {
                        if(result.startsWith("[")) {
                            if(aClass.getFields().length != 1) {
                                throw new Exception("Response 书写错误 json 返回为一个数组 ,Response 必须包含一个List集合用于封装数据,且只能有一个list集合对象");
                            }

                            Field[] e = aClass.getFields();
                            int var9 = e.length;

                            for(int var10 = 0; var10 < var9; ++var10) {
                                Field field = e[var10];
                                result = "{\"" + field.getName() + "\":" + result + "}";
                            }
                            o = this.g.fromJson(result, aClass);
                            return o;
                        }
                        o = this.g.fromJson(result, aClass);
                    } catch (Exception var15) {
                        var15.printStackTrace();
                    }
                    return o;
                }
            }
            return null;
        }
    }

    protected String syncString() {
        String execute = null;

        try {
            execute = this.http.execute();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        Lg.println(execute);
        return execute;
    }

    @SuppressLint({"NewApi"})
    protected void onCancelled(Object o) {
        super.onCancelled(o);
        if(this.http != null) {
            (new Thread(new Runnable() {
                public void run() {
                    ABaseAndroidRequester.this.http.cancel();
                }
            })).start();
            if(this.requesterManagerCallback != null && this.requesterManagerCallback instanceof Context) {
                AUtil.progress((Context)this.requesterManagerCallback, false);
            }
        }

    }

    @SuppressLint({"NewApi"})
    public void cancel() {
        this.cancel(true);
    }

}
