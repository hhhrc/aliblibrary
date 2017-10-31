package cn.hbjx.alib.util;

import android.util.Log;

import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.Formatter;

/**
 * Created by DengYiQian on 2017/6/6.
 */

public class Lg {

    public static boolean DEBUG = true;
    public static boolean FORMAT_JSON = false;
    public static String tag = "lg";
    public static Gson g = new Gson();
    private static final ThreadLocal<Lg.ReusableFormatter> thread_local_formatter = new ThreadLocal() {
        protected Lg.ReusableFormatter initialValue() {
            return new Lg.ReusableFormatter();
        }
    };

    public Lg() {
    }

    public static void setDebug(boolean b) {
        if(b) {
            DEBUG = true;
            FORMAT_JSON = true;
        } else {
            DEBUG = false;
            FORMAT_JSON = false;
        }

    }

    public static void setFormatJson(boolean b) {
        if(b) {
            FORMAT_JSON = true;
        } else {
            FORMAT_JSON = false;
        }

    }

    public static void printJson(String o) {
        if(DEBUG) {
            System.err.println(StringTookit.JSONStringFormat(o));
        }

    }

    public static void printJson(Object o) {
        if(DEBUG) {
            System.err.println(StringTookit.JSONStringFormat(g.toJson(o)));
        }

    }

    public static void println(Object obj, boolean showMemory) {
        if(DEBUG && showMemory) {
            DecimalFormat df = new DecimalFormat("#.##");
            Log.d(tag, "\n★★★★★★★★★★★★★★★★★★★★★★★★★★");
            Log.d(tag, "★[Class:" + Thread.currentThread().getStackTrace()[3].getClassName() + "  Method:" + Thread.currentThread().getStackTrace()[3].getMethodName() + "  Line:" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "]★  \n★[Memory:" + df.format((double)(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024.0D / 1024.0D) + " M / " + df.format((double)Runtime.getRuntime().maxMemory() / 1024.0D / 1024.0D) + " M]★");
            if(obj != null) {
                Log.d(tag, obj.toString());
            }

            Log.d(tag, "★★★★★★★★★★★★★★★★★★★★★★★★★★\n");
        } else if(DEBUG && !showMemory) {
            Log.d(tag, obj.toString());
        }

    }

    public static void println(Object obj) {
        if(DEBUG && null!=obj) {
            Log.d(tag, obj.toString());
        }

    }

    public static void println(String tag, Object obj) {
        if(DEBUG) {
            Log.d(tag, obj + "");
        }

    }

    public static void println(String msg, Object... args) {
        if(DEBUG) {
            Log.d(tag, format(msg, args));
        }

    }

    public static String format(String msg, Object... args) {
        Lg.ReusableFormatter formatter = (Lg.ReusableFormatter)thread_local_formatter.get();
        return formatter.format(msg, args);
    }

    private static class ReusableFormatter {
        private Formatter formatter;
        private StringBuilder builder = new StringBuilder();

        public ReusableFormatter() {
            this.formatter = new Formatter(this.builder);
        }

        public String format(String msg, Object... args) {
            this.formatter.format(msg, args);
            String s = this.builder.toString();
            this.builder.setLength(0);
            return s;
        }
    }

}
