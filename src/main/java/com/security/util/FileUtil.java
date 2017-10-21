package com.security.util;

import android.content.Context;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 有关外部存储SD的工具类;
 */
public class FileUtil {

    /**
     * 判断SD卡是否可用;
     * @return :true代表SD卡可用,false代表SD卡不可用;
     */
    public static boolean isSDAvailable() {
        String state = Environment.getExternalStorageState();
        if (state != null && state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /*获取缓存路径，存储临时文件，可被一键清理和卸载清理*/
    /*
    * @param uniqueName :缓存的数据的类型的名称,如JSON代表缓存的是文本资源;
    * 可以看到，当SD卡存在或者SD卡不可被移除的时候，
    * 就调用getExternalCacheDir()方法来获取缓存路径，
    * 否则就调用getCacheDir()方法来获取缓存路径。
    * 前者获取到的就是/sdcard/Android/data/<application package>/cache这个路径，
    * 而后者获取到的是 /data/data/<application package>/cache 这个路径。*/
    public static File getDiskCacheDir(Context context,String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * 从缓存文件中读取保存的文本数据,若SD卡可用则从SD卡的缓存文件中读取相应的数据,
     * 若SD卡不可用,则从SharedPreference中读取;
     * @param context
     * @param fileName
     * @return
     */
    public static String readTextFromCacheFile(Context context,String fileName){
        String result="";
        if(isSDAvailable()){
            String key=MD5Util.DatEncryption(fileName);
            File file=new File(getDiskCacheDir(context,"JSON"),key);
            if(file.exists()){
                try {
                    FileInputStream is = new FileInputStream(file);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) != -1) {
                        stream.write(buffer, 0, length);
                    }
                    is.close();
                    stream.close();
                    result = stream.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else{
            result= (String) SharedPreferenceUtil.get(context,fileName,"");
        }
        return result;
    }

    /**
     * 緩存文本数据到缓存文件中;若SD卡可用,则将文本数据缓存进SD卡的缓存文件中,
     * 否则将其存放进SharedPreference中;
     * @param fileName:缓存的文件的名称;
     * @param value:缓存的文本数据;
     */
    public static void saveTextToCacheFile(Context context,String fileName,String value){
        if(isSDAvailable()){
            try {
                String key=MD5Util.DatEncryption(fileName);
                File file=new File(getDiskCacheDir(context,"JSON"),key);
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    //创建目录
                    parentFile.mkdirs();
                }
                if(!file.exists()){
                    file.createNewFile();
                }
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(value.getBytes());
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            SharedPreferenceUtil.put(context,fileName,value);
        }

    }

}



