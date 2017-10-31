package cn.hbjx.alib.network;

import com.google.gson.Gson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import cn.hbjx.alib.util.Lg;

/**
 * Created by DengYiQian on 2017/6/6.
 */

public class ABaseRequester {

    protected Http http;
    protected Gson g = new Gson();

    public ABaseRequester() {
    }

    public Object syncObj() {
        String result = this.syncString();
        Class[] var2;
        int var3;
        int var4;
        Class aClass;
        ARequestResult ann;
        Object var12;
        if(result.startsWith("[")) {
            var2 = this.getClass().getClasses();
            var3 = var2.length;

            for(var4 = 0; var4 < var3; ++var4) {
                aClass = var2[var4];
                ann = (ARequestResult)aClass.getAnnotation(ARequestResult.class);
                if(ann != null) {
                    if(aClass.getFields().length != 1) {
                        try {
                            throw new Exception("Response 书写错误 json 返回为一个数组 ,Response 必须包含一个List集合用于封装数据,且只能有一个list集合对象");
                        } catch (Exception var11) {
                            var11.printStackTrace();
                            return null;
                        }
                    }

                    Field[] o = aClass.getFields();
                    int var8 = o.length;

                    for(int var9 = 0; var9 < var8; ++var9) {
                        Field field = o[var9];
                        result = "{\"" + field.getName() + "\":" + result + "}";
                    }

                    var12 = this.g.fromJson(result, aClass);
                    return var12;
                }
            }
        } else {
            var2 = this.getClass().getClasses();
            var3 = var2.length;

            for(var4 = 0; var4 < var3; ++var4) {
                aClass = var2[var4];
                ann = (ARequestResult)aClass.getAnnotation(ARequestResult.class);
                if(ann != null) {
                    var12 = this.g.fromJson(result, aClass);
                    return var12;
                }
            }
        }

        return null;
    }

    public String syncString() {
        String type = "get";
        String url = null;
        Http.DataType dataType = Http.DataType.TYPE1_FORM;
        Annotation[] annotations = this.getClass().getAnnotations();
        Annotation[] fields = annotations;
        int res = annotations.length;

        int var7;
        for(var7 = 0; var7 < res; ++var7) {
            Annotation an = fields[var7];
            if(an instanceof ARequestType) {
                type = ((ARequestType)an).type().trim();
                url = ((ARequestType)an).url();
                dataType = ((ARequestType)an).dataType();
            }
        }

        this.http = new Http(url, type);
        this.http.setDataType(dataType);
        Field[] var13 = this.getClass().getDeclaredFields();
        Field[] var14 = var13;
        var7 = var13.length;

        for(int var16 = 0; var16 < var7; ++var16) {
            Field field = var14[var16];
            ARequestParam annotation = (ARequestParam)field.getAnnotation(ARequestParam.class);
            if(null != annotation) {
                try {
                    Object e = field.get(this);
                    this.http.addParam(field.getName(), e.toString());
                } catch (IllegalAccessException var12) {
                    var12.printStackTrace();
                }
            }
        }

        if(Lg.DEBUG) {
            Lg.println(this.http.toString());
        }

        String var15 = this.http.execute();
        return var15;
    }

    public void cancel() {
        this.http.cancel();
    }

}
