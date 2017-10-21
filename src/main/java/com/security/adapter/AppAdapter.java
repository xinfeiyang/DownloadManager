package com.security.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.security.activity.DetailActivity;
import com.security.activity.R;
import com.security.bean.AppInfo;
import com.security.util.DownloadManager;
import com.security.util.UIUtil;
import com.security.view.CirclerProgressView;

import java.util.LinkedList;
import java.util.List;

/**
 * 下载APP的适配器;
 */
public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppItemViewHolder> {

    public static final String TAG="TAG";
    private List<AppItemViewHolder> holders = new LinkedList<>();

    private Context context;
    private List<AppInfo> datas;

    public List<AppItemViewHolder> getHolders() {
        return holders;
    }

    public AppAdapter(Context context, List<AppInfo> datas) {
        this.context = context;
        this.datas=datas;
    }

    @Override
    public AppItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_recyclerview, null);
        AppItemViewHolder holder = new AppItemViewHolder(view);
        //将新创建的Holder加入到观察中中;
        DownloadManager.getInstance().addObserver(holder);
        holders.add(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(AppItemViewHolder holder, int position) {
        AppInfo info = datas.get(position);
        holder.setData(info);
    }

    @Override
    public int getItemCount() {
        if(datas!=null&&datas.size()>0){
            return datas.size();
        }
        return 0;
    }

    /**
     * AppItem的ViewHolder;
     */
    public class AppItemViewHolder extends RecyclerView.ViewHolder implements DownloadManager.DownloadObserver, View.OnClickListener {

        private AppInfo info;
        private View itemView;
        private ImageView iv_icon;
        private TextView tv_title;
        private RatingBar rb_star;
        private TextView tv_size;
        private TextView tv_des;
        private CirclerProgressView circlerProgressView;

        public AppItemViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.iv_icon = (ImageView) itemView.findViewById(R.id.item_iv_icon);
            this.tv_title = (TextView) itemView.findViewById(R.id.item_tv_title);
            this.rb_star = (RatingBar) itemView.findViewById(R.id.item_rb_stars);
            this.tv_size = (TextView) itemView.findViewById(R.id.item_tv_size);
            this.tv_des = (TextView) itemView.findViewById(R.id.item_tv_des);
            this.circlerProgressView = (CirclerProgressView) itemView.findViewById(R.id.item_circleprogressview);
            this.circlerProgressView.setOnClickListener(this);
        }

        @Override
        public void onDownLoadInfoChange(final DownloadManager.DownloadInfo downloadInfo) {

            if(!TextUtils.isEmpty(info.getPackageName())&&!TextUtils.isEmpty(downloadInfo.packageName)){
                //过滤DownloadInfo;
                if(!info.getPackageName().equals(downloadInfo.packageName)){
                    return;
                }
            }

            UIUtil.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    refreshUIOfCircleProgreeView(downloadInfo);
                }
            });
        }

        public void setData(final AppInfo info) {
            this.info=info;
            tv_title.setText(info.getName());
            tv_size.setText(Formatter.formatFileSize(context, info.getSize()));
            tv_des.setText(info.getDes());
            rb_star.setRating((float) info.getStars());
            Glide.with(context).load(DownloadManager.BASE_URL+ info.getIconUrl())
                    .placeholder(R.drawable.ic_default)
                    .error(R.drawable.ic_default)
                    .into(iv_icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!TextUtils.isEmpty(info.getPackageName())){
                        Intent intent=new Intent(UIUtil.getContext(),DetailActivity.class);
                        intent.putExtra("packageName",info.getPackageName());
                        context.startActivity(intent);
                    }
                }
            });

            // 清除复用convertView之后的progress效果;
            circlerProgressView.setProgressEnabled(false);
            circlerProgressView.setProgress(0);

            //根据下载安装状态,更新CircleProgressView的样式;
            DownloadManager.DownloadInfo downloadInfo=DownloadManager.getInstance().getDownloadInfo(info);
            refreshUIOfCircleProgreeView(downloadInfo);
        }

        /**
         * 根据DoanloadManager返回的DownloadInfo信息,刷新CircleProgressView;
         * @param downloadInfo:下载信息;
         */
        public void refreshUIOfCircleProgreeView(DownloadManager.DownloadInfo downloadInfo) {
            if(!info.getPackageName().equals(downloadInfo.packageName)){
                return ;
            }
            circlerProgressView.setProgressEnabled(false);
            switch (downloadInfo.state){
                /**
                 状态(编程记录)  	  |  给用户的提示(ui展现)
                 ---------------- |----------------------
                 未下载			  |下载
                 下载中			  |显示进度条
                 暂停下载	      |继续下载
                 等待下载		 |等待中...
                 下载失败 		 |重试
                 下载完成 		 |安装
                 已安装 			 |打开
                 */
                case DownloadManager.STATE_UNDOWNLOAD://未下载;
                    circlerProgressView.setNote("下载");
                    circlerProgressView.setIcon(R.drawable.ic_download);
                    break;

                case DownloadManager.STATE_DOWNLOADING://下载中;
                    circlerProgressView.setProgressEnabled(true);
                    circlerProgressView.setMax(downloadInfo.max);
                    circlerProgressView.setProgress(downloadInfo.progress);
                    int progress= (int) (downloadInfo.progress*100.f/downloadInfo.max+0.5f);
                    circlerProgressView.setNote(progress+"%");
                    circlerProgressView.setIcon(R.drawable.ic_pause);
                    break;

                case DownloadManager.STATE_WAITINGDOWNLOAD://等待下载;
                    circlerProgressView.setNote("等待中");
                    circlerProgressView.setIcon(R.drawable.ic_cancel);
                    break;

                case DownloadManager.STATE_PAUSEDOWNLOAD://暂停下载;
                    circlerProgressView.setNote("继续下载");
                    circlerProgressView.setIcon(R.drawable.ic_resume);
                    break;

                case DownloadManager.STATE_DOWNLOADFAILED://下载失败;
                    circlerProgressView.setNote("重试");
                    circlerProgressView.setIcon(R.drawable.ic_redownload);
                    break;

                case DownloadManager.STATE_DOWNLOADED://下载完成,尚未安装;
                    circlerProgressView.setProgressEnabled(false);
                    circlerProgressView.setNote("安装");
                    circlerProgressView.setIcon(R.drawable.ic_install);
                    break;

                case DownloadManager.STATE_INSTALLED://已安装;
                    circlerProgressView.setNote("打开");
                    circlerProgressView.setIcon(R.drawable.ic_install);
                    break;

                default:
                    break;
            }
        }

        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.item_circleprogressview){
                DownloadManager.DownloadInfo downloadInfo=DownloadManager.getInstance().getDownloadInfo(info);
                if(!downloadInfo.packageName.equals(info.getPackageName())){
                    return ;
                }

                switch (downloadInfo.state){
                    /**
                     状态(编程记录)  	  | 用户的行为
                     ---------------- |----------------------
                     未下载			  |下载
                     下载中			  |暂停下载
                     暂停下载	      |断点继续下载
                     等待下载		 |取消下载
                     下载失败 		 |重试下载
                     下载完成 		 |安装应用
                     已安装 			 |打开应用
                     */

                    case DownloadManager.STATE_UNDOWNLOAD://未下载;
                        DownloadManager.getInstance().download(downloadInfo);
                        break;

                    case DownloadManager.STATE_DOWNLOADING://下载中;
                        DownloadManager.getInstance().pauseDownload(downloadInfo);
                        break;

                    case DownloadManager.STATE_WAITINGDOWNLOAD://等待下载;
                        DownloadManager.getInstance().cancelDownload(downloadInfo);
                        break;

                    case DownloadManager.STATE_PAUSEDOWNLOAD://暂停下载;
                        DownloadManager.getInstance().download(downloadInfo);
                        break;

                    case DownloadManager.STATE_DOWNLOADFAILED://下载失败;
                        DownloadManager.getInstance().download(downloadInfo);
                        break;

                    case DownloadManager.STATE_DOWNLOADED://下载完成,尚未安装;
                        DownloadManager.getInstance().installApp(downloadInfo);
                        break;

                    case DownloadManager.STATE_INSTALLED://已安装;
                        DownloadManager.getInstance().launchApp(downloadInfo);
                        break;
                }
            }
        }
    }

}
