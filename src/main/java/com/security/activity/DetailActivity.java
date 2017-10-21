package com.security.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.security.bean.AppInfo;
import com.security.util.DensityUtil;
import com.security.util.DownloadManager;
import com.security.util.FileUtil;
import com.security.util.UIUtil;
import com.security.view.ProgressButton;
import com.security.view.RatioLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener,DownloadManager.DownloadObserver{

    private ImageView appDetailInfoIvIcon;
    private TextView appDetailInfoTvName;
    private RatingBar appDetailInfoRbStar;
    private TextView appDetailInfoTvDownloadnum;
    private TextView appDetailInfoTvVersion;
    private TextView appDetailInfoTvTime;
    private TextView appDetailInfoTvSize;

    private LinearLayout appDetailPicIvContainer;

    private TextView appDetailDesTvDes;
    private TextView appDetailDesTvAuthor;
    private ImageView appDetailDesIvArrow;

    private Button appDetailDownloadBtnFavo;
    private Button appDetailDownloadBtnShare;
    private ProgressButton progressButton;
    private String packageName;

    private ScrollView scrollView;

    private int desHeight=0;

    /**
     * 当前是否展开
     */
    private boolean isOpen = true;

    private AppInfo infoDetail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        DownloadManager.getInstance().addObserver(this);
        scrollView= (ScrollView) findViewById(R.id.scrollView);

        initInfoViews();
        //initSafeViews();
        initPicViews();
        initDesViews();
        initBottomViews();

        packageName = getIntent().getStringExtra("packageName");
        initData();
    }


    /**
     * 初始化数据;
     */
    private void initData() {
        String json= FileUtil.readTextFromCacheFile(UIUtil.getContext(),packageName);
        if(!TextUtils.isEmpty(json)){
            processData(json);
        }else{
            getDataFromNet();
        }
    }

    /**
     * 从网络获取数据;
     */
    private void getDataFromNet() {
        String request=DownloadManager.BASE_URL+"app/"+packageName+"/"+packageName;
        OkHttpUtils.get().url(request).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(UIUtil.getContext(),"网络请求失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                if(!TextUtils.isEmpty(response)){
                    FileUtil.saveTextToCacheFile(UIUtil.getContext(),packageName,response);
                    processData(response);
                }else{
                    Toast.makeText(UIUtil.getContext(),"返回值为空",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * 解析数据;
     */
    private void processData(String json) {
        Gson gson=new Gson();
        infoDetail = gson.fromJson(json, AppInfo.class);
        if(infoDetail !=null){
            setDataForViews(infoDetail);
        }
    }


    /**
     * 为View赋值;
     */
    private void setDataForViews(AppInfo info) {
        setDataForInfo(info);
        setDataForPic(info);
        setDataForDes(info);

        setDataForBottom(info);
    }

    /**
     * 为底部Bottom赋值;
     * @param appInfo:用户信息;
     */
    private void setDataForBottom(AppInfo appInfo) {
        DownloadManager.DownloadInfo info = DownloadManager.getInstance().getDownloadInfo(appInfo);
        refreshUIOfProgressButton(info);
    }

    private void initInfoViews() {
        appDetailInfoIvIcon = (ImageView) findViewById(R.id.app_detail_info_iv_icon);
        appDetailInfoTvName = (TextView) findViewById(R.id.app_detail_info_tv_name);
        appDetailInfoRbStar = (RatingBar) findViewById(R.id.app_detail_info_rb_star);
        appDetailInfoTvDownloadnum = (TextView) findViewById(R.id.app_detail_info_tv_downloadnum);
        appDetailInfoTvVersion = (TextView) findViewById(R.id.app_detail_info_tv_version);
        appDetailInfoTvTime = (TextView) findViewById(R.id.app_detail_info_tv_time);
        appDetailInfoTvSize = (TextView) findViewById(R.id.app_detail_info_tv_size);
    }



    /**
     * 为信息页设值;
     */
    private void setDataForInfo(AppInfo info) {
        //为Info设值;
        appDetailInfoTvName.setText(info.getName());
        appDetailInfoRbStar.setRating((float)info.getStars());
        appDetailInfoTvDownloadnum.setText("下载量:"+info.getDownloadNum());
        appDetailInfoTvVersion.setText("版本:"+info.getVersion());
        appDetailInfoTvTime.setText("更新时间:"+info.getDate());
        appDetailInfoTvSize.setText("文件大小:"+Formatter.formatFileSize(UIUtil.getContext(),info.getSize()));
        Glide.with(UIUtil.getContext()).load(DownloadManager.BASE_URL+info.getIconUrl())
                .placeholder(R.drawable.ic_default)
                .error(R.drawable.ic_default)
                .into(appDetailInfoIvIcon);

    }

    private void initPicViews() {
        appDetailPicIvContainer = (LinearLayout) findViewById(R.id.app_detail_pic_iv_container);
    }

    /**
     * 为图片页设图;
     */
    private void setDataForPic(AppInfo info) {
        int margin=DensityUtil.dp2px(UIUtil.getContext(),5);
        int screenWidth = DensityUtil.getScreenWidth(UIUtil.getContext());
        List<String> pics = info.getScreen();
        for(int i=0;i< pics.size();i++){
            ImageView imageView=new ImageView(UIUtil.getContext());
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(screenWidth/3,LinearLayout.LayoutParams.WRAP_CONTENT);
            if(i!=0){
                params.leftMargin=margin;
            }

            imageView.setLayoutParams(params);

            RatioLayout ratioLayout=new RatioLayout(UIUtil.getContext());
            ratioLayout.setRatio(150/250);
            ratioLayout.setRelative(RatioLayout.RELATIVE_WIDTH);

            Glide.with(this).load(DownloadManager.BASE_URL+pics.get(i))
                    .placeholder(R.drawable.ic_default)
                    .error(R.drawable.ic_default)
                    .into(imageView);
            ratioLayout.addView(imageView);
            appDetailPicIvContainer.addView(ratioLayout);
        }
    }


    private void initDesViews() {
        appDetailDesTvDes = (TextView)findViewById( R.id.app_detail_des_tv_des );
        appDetailDesTvAuthor = (TextView)findViewById( R.id.app_detail_des_tv_author );
        appDetailDesIvArrow = (ImageView)findViewById( R.id.app_detail_des_iv_arrow );
    }

    /**
     * 为具体描述页赋值;
     */
    private void setDataForDes(AppInfo info) {
        appDetailDesTvAuthor.setText(info.getAuthor());
        appDetailDesTvDes.setText(info.getDes());
        appDetailDesTvDes.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                desHeight=appDetailDesTvDes.getMeasuredHeight();
                toggle(false);
                appDetailDesTvDes.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        appDetailDesIvArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(true);
            }
        });
    }


    private void toggle(boolean isAnimation){
        int height=getHeightOfTextView(5,infoDetail.getDes());
        int start=0;
        int end=0;
        if(isOpen){//折叠;
            start=desHeight;
            end=height;
            if(isAnimation){
                doAnimation(start, end);
            }else{
                appDetailDesTvDes.setHeight(end);
            }
        }else{//关闭;
            start=height;
            end=desHeight;
            if(isAnimation){
                if(isAnimation){
                    doAnimation(start, end);
                }else{
                    appDetailDesTvDes.setHeight(end);
                }
            }
        }
        if(isAnimation){
            if (isOpen) {
                ObjectAnimator.ofFloat(appDetailDesIvArrow, "rotation", 180, 0).start();
            } else {
                ObjectAnimator.ofFloat(appDetailDesIvArrow, "rotation", 0, 180).start();
            }
        }

        isOpen=!isOpen;
    }

    private void doAnimation(int start, int end) {
        ObjectAnimator animator = ObjectAnimator.ofInt(appDetailDesTvDes, "height", start, end);
        animator.start();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ViewParent parent = appDetailDesTvDes.getParent();
                while (true) {
                    parent = parent.getParent();
                    if (parent == null) {// 已经没有父亲
                        break;
                    }
                    if (parent instanceof ScrollView) {// 已经找到
                        ((ScrollView) parent).fullScroll(View.FOCUS_DOWN);
                        break;
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * 获取行高;
     * @param lines:行高;
     * @param des:文本描述;
     * @return
     */
    private int getHeightOfTextView(int lines, String des) {
        TextView tv=new TextView(UIUtil.getContext());
        tv.setLines(lines);
        tv.setText(des);
        tv.measure(0,0);
        int height=tv.getMeasuredHeight();
        return height;
    }

    private void initBottomViews() {
        appDetailDownloadBtnFavo = (Button)findViewById( R.id.app_detail_download_btn_favo );
        appDetailDownloadBtnShare = (Button)findViewById( R.id.app_detail_download_btn_share );
        progressButton = (ProgressButton)findViewById( R.id.app_detail_download_btn_download );

        appDetailDownloadBtnFavo.setOnClickListener(this);
        appDetailDownloadBtnShare.setOnClickListener(this);
        progressButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if ( v == appDetailDownloadBtnFavo ) {
            Toast.makeText(UIUtil.getContext(),"收藏", Toast.LENGTH_SHORT).show();
        } else if ( v == appDetailDownloadBtnShare ) {
            Toast.makeText(UIUtil.getContext(),"分享", Toast.LENGTH_SHORT).show();
        } else if ( v == progressButton ) {
            Toast.makeText(UIUtil.getContext(),"下载", Toast.LENGTH_SHORT).show();
            DownloadManager.DownloadInfo info=DownloadManager.getInstance().getDownloadInfo(infoDetail);
            /**
             状态(编程记录)     | 用户行为(触发操作)
             ----------------| -----------------
             未下载			| 去下载
             下载中			| 暂停下载
             暂停下载			| 断点继续下载
             等待下载			| 取消下载
             下载失败 			| 重试下载
             下载完成 			| 安装应用
             已安装 			| 打开应用
             */
            switch(info.state){
                case DownloadManager.STATE_UNDOWNLOAD:// 未下载
                    DownloadManager.getInstance().download(info);
                    break;
                case DownloadManager.STATE_DOWNLOADING:// 下载中
                    DownloadManager.getInstance().pauseDownload(info);
                    break;
                case DownloadManager.STATE_PAUSEDOWNLOAD:// 暂停下载
                    DownloadManager.getInstance().download(info);
                    break;
                case DownloadManager.STATE_WAITINGDOWNLOAD:// 等待下载
                    DownloadManager.getInstance().cancelDownload(info);
                    break;
                case DownloadManager.STATE_DOWNLOADFAILED:// 下载失败
                    DownloadManager.getInstance().download(info);
                    break;
                case DownloadManager.STATE_DOWNLOADED:// 下载完成
                    DownloadManager.getInstance().installApp(info);
                    break;
                case DownloadManager.STATE_INSTALLED:// 已安装
                    DownloadManager.getInstance().launchApp(info);
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * 更新ProgressButton的状态；
     * @param info
     */
    private void refreshUIOfProgressButton(DownloadManager.DownloadInfo info) {
        progressButton.setProgessEnabled(false);
        switch (info.state) {
            /**
             状态(编程记录)  	|  给用户的提示(ui展现)
             ----------------|----------------------
             未下载			|下载
             下载中			|显示进度条
             暂停下载			|继续下载
             等待下载			|等待中...
             下载失败 			|重试
             下载完成 			|安装
             已安装 			|打开
             */
            case DownloadManager.STATE_UNDOWNLOAD:// 未下载
                progressButton.setText("下载");
                break;
            case DownloadManager.STATE_DOWNLOADING:// 下载中
                progressButton.setProgessEnabled(true);
                progressButton.setMax(info.max);
                progressButton.setProgress(info.progress);
                int progress = (int) (info.progress * 100.f / info.max + .5f);
                progressButton.setText(progress + "%");
                break;
            case DownloadManager.STATE_PAUSEDOWNLOAD:// 暂停下载
                progressButton.setText("继续下载");
                progressButton.setProgessEnabled(true);
                break;
            case DownloadManager.STATE_WAITINGDOWNLOAD:// 等待下载
                progressButton.setText("等待中...");
                break;
            case DownloadManager.STATE_DOWNLOADFAILED:// 下载失败
                progressButton.setText("重试");
                break;
            case DownloadManager.STATE_DOWNLOADED:// 下载完成
                progressButton.setText("安装");
                break;
            case DownloadManager.STATE_INSTALLED:// 已安装
                progressButton.setText("打开");
                break;

            default:
                break;
        }
    }


    @Override
    public void onDownLoadInfoChange(final DownloadManager.DownloadInfo info) {
        //过滤DownloadInfo;
        if(!info.packageName.equals(packageName)){
            return;
        }
        UIUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                refreshUIOfProgressButton(info);
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        DownloadManager.getInstance().deleteObserver(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(infoDetail!=null){
            DownloadManager.getInstance().addObserver(this);
            //更新UI;
            DownloadManager.DownloadInfo info=DownloadManager.getInstance().getDownloadInfo(infoDetail);
            refreshUIOfProgressButton(info);
        }
    }
}

