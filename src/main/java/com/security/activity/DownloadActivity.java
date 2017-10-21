package com.security.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.security.adapter.AppAdapter;
import com.security.bean.AppInfo;
import com.security.util.DownloadManager;
import com.security.util.FileUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;

public class DownloadActivity extends AppCompatActivity {

    private String url=DownloadManager.BASE_URL+"app/applist1";
    private static final String TAG="TAG";
    private RecyclerView recyclerView;
    private ProgressBar pb_progress;
    private String subUrl;
    private List<AppInfo> datas=new ArrayList<>();
    private AppAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        subUrl = url.substring(url.lastIndexOf("/")+1);
        initView();
        initData();
    }

    /**
     * 初始化View;
     */
    private void initView() {
        pb_progress = (ProgressBar) findViewById(R.id.pb_progress);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager manager=new LinearLayoutManager(DownloadActivity.this);
        recyclerView.setLayoutManager(manager);
        adapter = new AppAdapter(DownloadActivity.this,datas);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 初始化数据;
     */
    private void initData() {
        String json=FileUtil.readTextFromCacheFile(DownloadActivity.this,subUrl);
        if(!TextUtils.isEmpty(json)){//若有缓存存在;
            pb_progress.setVisibility(View.GONE);
            processData(json);
        }else{//无缓存,直接从网络读取；
            getDataFromNet();
        }
    }

    /**
     * 从网络上获取数据;
     */
    private void getDataFromNet() {
        OkHttpUtils.get().url(url).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                pb_progress.setVisibility(View.GONE);
                Toast.makeText(DownloadActivity.this,"网络请求失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                FileUtil.saveTextToCacheFile(DownloadActivity.this,subUrl,response);
                processData(response);
            }
        });
    }

    /**
     * 解析数据,此刻是在主线程中;
     * @param response
     */
    private void processData(String response) {
        Gson gson=new Gson();
        List<AppInfo> infos=gson.fromJson(response,new TypeToken<List<AppInfo>>(){}.getType());
        datas.addAll(infos);
        adapter.notifyDataSetChanged();
    }

    /**
     *当生命周期执行到onPause方法时，移除相应的监听;
     */
    @Override
    protected void onPause() {
        super.onPause();
        if(adapter!=null){
            List<AppAdapter.AppItemViewHolder> holders = adapter.getHolders();
            for(AppAdapter.AppItemViewHolder holder:holders){
                DownloadManager.getInstance().deleteObserver(holder);
            }
        }
    }

    /**
     * 当生命周期执行到onResumse方法时，增加相应的监听;
     */
    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null){
            List<AppAdapter.AppItemViewHolder> holders = adapter.getHolders();
            for(AppAdapter.AppItemViewHolder holder:holders){
                DownloadManager.getInstance().addObserver(holder);
            }
            //刷新页面;
            adapter.notifyDataSetChanged();
        }
    }
}
