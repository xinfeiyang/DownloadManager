package com.security.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import java.io.File;

/**
 * 关于APP文件安装、启动、设置、卸载等的工具类
 */
public class AppUtil {

    /**
     * 根据包名判断应用是否已经安装;
     * @param context:上下文
     * @param packageName:包名
     * @return :true代表应用已安装，false代表尚未安装;
     */
    public static boolean isAppInstalled(Context context,String packageName){
        PackageInfo packageInfo;
        try {
            PackageManager packageManager=context.getPackageManager();
            packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            packageInfo=null;
        }
        return packageInfo!=null?true:false;
    }


    /**
     * 启动App
     * @param context：上下文
     * @param packageName:包名;
     */
    public static void launchApp(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        //通过包名获取意图;
        Intent intent = pm.getLaunchIntentForPackage(packageName);
        context.startActivity(intent);
    }

    /**
     * 进入App的设置页面;
     * @param context:上下文
     * @param packageName:包名
     */
    public static void settingApp(Context context, String packageName) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.parse("package:" + packageName));
        context.startActivity(intent);
    }


    /**
     * 安装APP;
     * @param context:上下文；
     * @param file:安装的文件;
     */
    public static void installApp(Context context, File file) {
        /*<intent-filter>
            <action android:name="android.intent.action.VIEW" />
            <category android:name="android.intent.category.DEFAULT" />
            <data android:scheme="content" />
            <data android:scheme="file" />
            <data android:mimeType="application/vnd.android.package-archive" />
        </intent-filter>*/
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String type = "application/vnd.android.package-archive";
        Uri data = Uri.fromFile(file);
        intent.setDataAndType(data,type);
        context.startActivity(intent);
    }

    /**
     * 卸载应用(只能下载用户的应用，系统应用因无ROOT权限是卸载不了的)
     * @param context
     * @param packageName
     */
    public static void uninstallApp(Context context, String packageName){
       /* <intent-filter>
         <action android:name="android.intent.action.VIEW" />
         <action android:name="android.intent.action.DELETE" />
         <category android:name="android.intent.category.DEFAULT" />
         <data android:scheme="package" />
         </intent-filter>*/

        Intent intent=new Intent("android.intent.action.DELETE");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:"+packageName));
        context.startActivity(intent);
    }
}

