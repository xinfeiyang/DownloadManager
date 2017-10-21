package com.security.activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.security.util.DownloadManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    private ProgressBar pb;

    public static int START_DOWNLOAD=0;//开始下载;
    public static int PAUSE_DOWNLOAD=1;//继续下载;

    private int currentState=START_DOWNLOAD;

    private boolean flag=true;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            pb.setMax(msg.arg2);
            pb.setProgress(msg.arg1);
        }
    };
    private Button btn_pause_resume;
    private TextView tv_des;
    private ImageView iv_arrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_des = (TextView) findViewById(R.id.tv_des);
        iv_arrow = (ImageView) findViewById(R.id.iv_arrow);

        tv_des.setText("2005-2014 你的校园一直在这儿。 中国最大的实名制SNS网络平台，大学生必备网络社交应用。 -------我们好像在哪儿见过------- \uF06E\t早春发芽，我在人人通过姓名，学校，找到了从小到大的同学，并加入了校园新圈子 \uF06E\t花开半夏，在新鲜事里和好友分享彼此的生活点滴，我们渺小如星辰，却真实存在着 \uF06E\t花花世界，这里的人貌似不疯不成活，蛇精病短视频、激萌语音照片，芝麻烂谷飚日志 \uF06E\t一叶知秋，喜欢上了每天看人人话题、看世界，公共主页、我知道这个世界有多大我们就得担负多大。 \uF06E\t漫天雪花：不知什么时候，习惯上了回顾过去，三千前，五年前，我们无知无畏，无所不能，每个样子，都好像在哪儿见过");

        int height=tv_des.getLineHeight();
        Log.i("TAG", "lineheight: "+height);

        iv_arrow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(flag){
                    flag=false;
                    tv_des.setEllipsize(null);//展开
                    tv_des.setSingleLine(false);
                    ObjectAnimator.ofFloat(iv_arrow,"rotation",0,180).start();
                }else{
                    flag=true;
                    tv_des.setEllipsize(TextUtils.TruncateAt.END);
                    tv_des.setLines(4);
                    ObjectAnimator.ofFloat(iv_arrow,"rotation",180,0).start();
                }
            }
        });


        Button btn_enter= (Button) findViewById(R.id.btn_enter);
        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,DownloadActivity.class);
                startActivity(intent);
            }
        });



        Button btn_download= (Button)findViewById(R.id.btn);
        btn_pause_resume = (Button) findViewById(R.id.btn_pause_resume);
        pb = (ProgressBar) findViewById(R.id.pb);
        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download();
            }
        });

        btn_pause_resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentState==START_DOWNLOAD){//开始下载
                    currentState=PAUSE_DOWNLOAD;//设置状态为暂停;
                    btn_pause_resume.setText("继续下载");
                }else if(currentState==PAUSE_DOWNLOAD){//暂停下载;
                    currentState=START_DOWNLOAD;
                    btn_pause_resume.setText("暂停下载");
                    download();
                }
            }
        });

    }

    private File getSaveFile(Context context) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator +"download");
    }

    /**
     * 点击下载
     */
    private void download() {
        File file=getSaveFile(MainActivity.this);
        if(!file.exists()){
           file.mkdirs();
        }
        final File saveFile=new File(file,"renren.apk");
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                try {
                    int progress = 0;
                    long range=0;//已下载的文件的大小;
                    if(saveFile.exists()){
                        range=saveFile.length();
                    }
                    progress= (int)range;
                    String request= DownloadManager.BASE_URL+"app/com.renren.mobile.android/com.renren.mobile.android.apk";
                    URL url=new URL(request);
                    conn= (HttpURLConnection) url.openConnection();
                    int length=getContentLength(request);
                    Log.i("TAG", "contentLength: "+length);
                    conn.setRequestProperty("Range","bytes="+range+"-"+length);
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    if(conn.getResponseCode()==200||conn.getResponseCode()==206){
                        InputStream in=conn.getInputStream();
                        FileOutputStream output=new FileOutputStream(saveFile,true);
                        byte[] buffer=new byte[1024];
                        int len=-1;
                        while((len=in.read(buffer))!=-1){
                            if(currentState==PAUSE_DOWNLOAD){
                                break;
                            }
                            output.write(buffer,0,len);
                            progress+=len;
                            Message msg=Message.obtain();
                            msg.arg1=progress;
                            msg.arg2= (int) length;
                            handler.sendMessageDelayed(msg,1000);
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally{
                    if(conn!=null){
                        conn.disconnect();
                        conn=null;
                    }
                }
            }
        }).start();
    }

    private int getContentLength(String request){
        int length=0;
        HttpURLConnection conn=null;
        try {
            URL url = new URL(request);
            conn= (HttpURLConnection) url.openConnection();
            length=conn.getContentLength();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(conn!=null){
                conn.disconnect();
                conn=null;
            }
            return length;
        }
    }

}
