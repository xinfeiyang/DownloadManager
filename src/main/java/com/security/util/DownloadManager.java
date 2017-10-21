package com.security.util;

import android.content.Context;
import android.os.Environment;

import com.security.bean.AppInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自定义的下载管理器:默认是下载apk文件的,如有需求,请改文件后缀名;
 */
public class DownloadManager {

    //下载过程中的七种状态;
    public static final int STATE_UNDOWNLOAD = 0; // 未下载
    public static final int STATE_DOWNLOADING = 1; // 下载中
    public static final int STATE_PAUSEDOWNLOAD = 2; // 暂停下载
    public static final int STATE_WAITINGDOWNLOAD = 3;// 等待下载
    public static final int STATE_DOWNLOADFAILED = 4;// 下载失败
    public static final int STATE_DOWNLOADED = 5;// 下载完成
    public static final int STATE_INSTALLED = 6;//已安装;

    private static DownloadManager instance;
    public static final String BASE_URL="http://5efm7b.natappfree.cc/";

    /**
     * 固定长度的线程池,设定核心线程数和最大线程数均为3个,也就是说同一时刻只能有3个任务同时进行;
     */
    private ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)Executors.newFixedThreadPool(3);

    /**
     * 保存下载信息的集合，键为packageName,值为DownloadInfo;
     */
    private Map<String, DownloadInfo> maps = new HashMap<>();

    /**
     * 私有化构造方法;
     */
    private DownloadManager() {

    }

    /**
     * 单例模式;
     * @return :下载任务管理器;
     */
    public static DownloadManager getInstance() {
        if (instance == null) {
            synchronized (DownloadManager.class) {
                if (instance == null) {
                    instance = new DownloadManager();
                }
            }
        }
        return instance;
    }

    /**
     * 点击进行下载;
     * @param info:文件的下载信息;
     */
    public void download(DownloadInfo info){
        //将下载文件的信息保存进入maps;
        maps.put(info.packageName,info);

        /***********当前状态为未下载;***********/
        info.state=STATE_UNDOWNLOAD;
        /**********通知内容观察者状态改变;*****/
        notifyObservers(info);

        //我们默认线程池的核心线程为3,当下载的任务数大于3时,其他的任务就会进入等待状态；
        //若用户点击了下载按钮,则我们可以提前设定状态为等待中；
        // 若当前的下载任务小于3，则会进行下载任务，状态会相应改变；
        //若当前的下载任务大于3,则多余任务会进入等待队列;
        /***********当前状态为等待下载;***********/
        info.state=STATE_WAITINGDOWNLOAD;
        /**********通知内容观察者状态改变;*****/
        notifyObservers(info);

        DownloadTask task=new DownloadTask(info);
        info.task=task;//为downloadInfo的下载任务赋值,方便取消任务时候进行删除;
        threadPoolExecutor.execute(task);
    }

    /**
     * 暂停下载,设定状态为暂停下载;
     * @param info :用户下载信息；
     */
    public void pauseDownload(DownloadInfo info){
        /**********当前状态为暂停下载**********/
        info.state=STATE_PAUSEDOWNLOAD;
        /**********通知内容观察者状态改变;*****/
        notifyObservers(info);
    }

    /**
     * 取消下载,设定状态为未下载;
     * @param info:用户下载信息；
     */
    public void cancelDownload(DownloadInfo info){
        Runnable task=info.task;
        threadPoolExecutor.remove(task);

        /**********当前状态为未下载**********/
        info.state=STATE_UNDOWNLOAD;
        /**********通知内容观察者状态改变;*****/
        notifyObservers(info);
    }

    /**
     * 返回用户下载信息,方便用户更新UI;
     * @param appInfo:文件下载信息;
     * @return :DownloadInfo
     */
    public DownloadInfo getDownloadInfo(AppInfo appInfo){

        //已安装；
        if(AppUtil.isAppInstalled(UIUtil.getContext(),appInfo.getPackageName())){
            DownloadInfo info=generateDownloadInfo(appInfo);
            info.state=STATE_INSTALLED;
            return info;
        }


        //下载完成
        DownloadInfo info=generateDownloadInfo(appInfo);
        File file=new File(info.savePath);
        if(file.exists()&&file.length()==appInfo.getSize()){//文件存在,并且文件的大小等于需要下载的文件大小;
            info.state=STATE_DOWNLOADED;//下载完成;
            return info;
        }

        /**
         下载中
         暂停下载
         等待下载
         下载失败
         */
        DownloadInfo downloadInfo=maps.get(appInfo.getPackageName());
        if(downloadInfo!=null){
            return downloadInfo;
        }

        //当期状态为未下载;
        DownloadInfo undownloadInfo=generateDownloadInfo(appInfo);
        undownloadInfo.state=STATE_UNDOWNLOAD;//当前状态为未下载;
        return undownloadInfo;
    }

    /**
     * 安装应用;
     * @param downloadInfo
     */
    public void installApp(DownloadInfo downloadInfo) {
        File file=new File(downloadInfo.savePath);
        AppUtil.installApp(UIUtil.getContext(),file);
    }

    /**
     * 启动应用;
     * @param downloadInfo
     */
    public void launchApp(DownloadInfo downloadInfo) {
        AppUtil.launchApp(UIUtil.getContext(),downloadInfo.packageName);
    }


    /**
     * 子线程执行：
     * 具体下载任务;
     */
    private class DownloadTask implements Runnable{

        private DownloadInfo info;

        public DownloadTask(DownloadInfo info) {
            this.info=info;
        }

        @Override
        public void run() {
            HttpURLConnection conn = null;
            try {
                /*############### 当前状态: 下载中 ###############*/
                info.state = STATE_DOWNLOADING;
                notifyObservers(info);
				/*#######################################*/

                boolean isPause=false;//默认为不暂停；
                int range=0;//文件的初始下载进度;
                File file=new File(info.savePath);
                //获取文件已下载的大小;
                if(file.exists()){//文件存在;
                    range= (int)file.length();
                }else{//文件不存在;
                    File parentFile = file.getParentFile();
                    if (!parentFile.exists()) {
                        //创建目录
                        parentFile.mkdirs();
                    }
                    if(!file.exists()){
                        file.createNewFile();
                    }
                    range=0;
                }

                info.progress=range;

                URL url= new URL(DownloadManager.BASE_URL+info.downloadUrl);
                conn= (HttpURLConnection) url.openConnection();
                //利用断点续传;
                conn.setRequestProperty("Range","bytes="+range+"-"+info.max);
                conn.setRequestMethod("GET");//Get请求；
                conn.setConnectTimeout(5000);//连接超时时间;
                //读取文件数据成功的返回码为200,读取间隔数据成功的返回码为206
                if(conn.getResponseCode()==200||conn.getResponseCode()==206){
                    InputStream in=conn.getInputStream();
                    //可以以追加的形式输入;
                    FileOutputStream output=new FileOutputStream(file,true);
                    byte[] buffer=new byte[1024];
                    int len=-1;
                    while((len=in.read(buffer))!=-1){
                        //如果当前状态为暂停下载,则退出循环;
                        if(info.state==STATE_PAUSEDOWNLOAD){
                            isPause=true;
                            break;
                        }

                        output.write(buffer,0,len);
                        info.progress+=len;

                        /*************当前状态为:下载进行中***********/
                        info.state=DownloadManager.STATE_DOWNLOADING;
                        /****************通知内容观察者状态改变**************/
                        notifyObservers(info);
                    }

                    //主动暂停
                    if(isPause){
                        /*************当前状态为:暂停下载***********/
                        info.state=DownloadManager.STATE_PAUSEDOWNLOAD;
                        /****************通知内容观察者状态改变**************/
                        notifyObservers(info);
                    }else{//下载完成
                        /*************当前状态为:下载完成***********/
                        info.state=DownloadManager.STATE_DOWNLOADED;
                        /****************通知内容观察者状态改变**************/
                        notifyObservers(info);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

                /*************当前状态为:下载失败***********/
                info.state=DownloadManager.STATE_DOWNLOADFAILED;
                /****************通知内容观察者状态改变**************/
                notifyObservers(info);

            }finally{
                if(conn!=null){
                    conn.disconnect();//断开连接;
                    conn=null;
                }
            }
        }

    }

    /**
     * 根据下载地址生成downloadInfo
     * @param appInfo:文件信息;
     * @return :DownloadInfo,注意：生成的downloadInfo只包含下载地址和文件的保存地址两部分信息;
     */
    public DownloadInfo generateDownloadInfo(AppInfo appInfo) {
        DownloadInfo info = new DownloadInfo();
        info.downloadUrl=appInfo.getDownloadUrl();
        info.packageName=appInfo.getPackageName();
        File saveFile=new File(getSaveFile(UIUtil.getContext()),appInfo.getPackageName()+".apk");
        info.savePath=saveFile.getAbsolutePath();
        info.max=appInfo.getSize();
        info.progress=0;
        return info;
    }

    /**
     * 根据url返回相应的packageName;
     * @param url :文件的下载地址;
     * @return :根据文件的下载地址,返回apk的包名;
     */
    private String getPackageName(String url) {
        return url.substring(url.lastIndexOf("/")+1,url.lastIndexOf("."));
    }


    /*
    *用于保存下载的文件;
    * 可以看到，当SD卡存在或者SD卡不可被移除的时候，
    * 就调用getExternalCacheDir()方法来获取缓存路径，
    * 否则就调用getCacheDir()方法来获取缓存路径。
    * 前者获取到的就是/sdcard/Android/data/<application package>/cache这个路径，
    * 而后者获取到的是 /data/data/<application package>/cache 这个路径*/
    private File getSaveFile(Context context) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + "Download");
    }


    /**
     * 文件的下载信息;
     */
    public class DownloadInfo {

        //文件的包名;
        public String packageName;

        /******下载的文件的保存地址*****/
        public String savePath;

        /******文件的下载地址*****/
        public String downloadUrl;

        /*****默认下载状态为未下载*****/
        public int state = DownloadManager.STATE_UNDOWNLOAD;

        /******文件总大小*****/
        public long max;

        /******已下载的文件的大小*****/
        public long progress;

        /******当前文件的下载任务;*****/
        public Runnable task;

        @Override
        public String toString() {
            return "DownloadInfo{" +
                    "packageName='" + packageName + '\'' +
                    ", savePath='" + savePath + '\'' +
                    ", downloadUrl='" + downloadUrl + '\'' +
                    ", state=" + state +
                    ", max=" + max +
                    ", progress=" + progress +
                    ", task=" + task +
                    '}';
        }
    }


    /*********************自定义内容观察者开始************************/
    public interface  DownloadObserver{
        void onDownLoadInfoChange(DownloadInfo info);
    }

    List<DownloadObserver> downLoadObservers = new LinkedList<DownloadObserver>();

    /**添加观察者*/
    public void addObserver(DownloadObserver observer) {
        if (observer == null) {
            throw new NullPointerException("observer == null");
        }
        synchronized (this) {
            if (!downLoadObservers.contains(observer))
                downLoadObservers.add(observer);
        }
    }

    /**删除观察者*/
    public synchronized void deleteObserver(DownloadObserver observer) {
        downLoadObservers.remove(observer);
    }

    /**通知观察者数据改变*/
    public void notifyObservers(DownloadInfo info) {
        for (DownloadObserver observer : downLoadObservers) {
            observer.onDownLoadInfoChange(info);
        }
    }

    /*********************自定义内容观察者结束************************/

}

