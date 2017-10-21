package com.security.util;

import android.util.Log;

/**
 * 日志工具类；
 */
public class LogUtil {

    public static final int VERBOSE=1;
    public static final int DEBUG=2;
    public static final int INFO=3;
    public static final int WARN=4;
    public static final int ERROR=5;
    public static final int NOTHING=6;

    //默认的控制级别;
    public static int level =INFO;

    //自定义的TAG；
    public static String TAG="LOG";
    
    public static void v(String msg){
        if(level<=VERBOSE){
            Log.i(TAG,msg);
        }
    }

    public static void d(String msg){
        if(level<=DEBUG){
            Log.d(TAG,msg);
        }
    }

    public static void i(String msg){
        if(level<=INFO){
            Log.i(TAG,msg);
        }
    }

    public static void w(String msg){
        if(level<=WARN){
            Log.w(TAG,msg);
        }
    }

    public static void e(String msg){
        if(level<=ERROR){
            Log.e(TAG,msg);
        }
    }

}
