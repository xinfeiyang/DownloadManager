package com.security.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 *有关屏幕密度的工具类;
 */
public class DensityUtil {

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dp2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dp(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	
	 /** 
	 * 将px值转换为sp值，保证文字大小不变 
	 */  
	public static int px2sp(Context context, float pxValue) {  
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
		return (int) (pxValue / fontScale + 0.5f);  
	}  
      
	/** 
	 * 将sp值转换为px值，保证文字大小不变 
	 */  
	public static int sp2px(Context context, float spValue) {  
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
		return (int) (spValue * fontScale + 0.5f);  
	} 

	/**
	 * 获取屏幕的宽度;
	 * @return:屏幕的宽度;
	 */
	public static int getScreenWidth(Context context) {
		WindowManager wm=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics=new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);
		return metrics.widthPixels;
	}
	
	/**
	 * 获取屏幕的高度;
	 * @return:屏幕的高度;
	 */
	public static int getScreenHeight(Context context) {
		WindowManager wm=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics=new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);
		return metrics.heightPixels;
	}
	
	/**
	 * 获得状态栏的高度
	 * @param context
	 * @return
	 */
	public static int getStatusHeight(Context context) {
	    int statusHeight = -1;
	    try {
	        Class<?> clazz = Class.forName("com.android.internal.R$dimen");
	        Object object = clazz.newInstance();
	        int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
	        statusHeight = context.getResources().getDimensionPixelSize(height);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return statusHeight;
	}
}
