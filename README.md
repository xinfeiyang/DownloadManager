# DownloadManager :下载管理器，根据当前的下载状态更新相应的UI；
#<font color="#FF1493">DownloadManager的使用</font>

##<font color="#FF1493">1、AppInfo实体类</font>

	/**
	 * AppInfo的详细信息;
	 */
	public class AppInfo {
	
	    private int id;
	    private String author;
	    private String date;
	    private String des;
	    private String downloadNum;
	    private String downloadUrl;
	    private String iconUrl;
	    private String name;
	    private String packageName;
	    private int size;
	    private double stars;
	    private String version;
	
	    private List<SafeBean> safe;
	    private List<String> screen;
	
	    public String getAuthor() {
	        return author;
	    }
	
	    public void setAuthor(String author) {
	        this.author = author;
	    }
	
	    public String getDate() {
	        return date;
	    }
	
	    public void setDate(String date) {
	        this.date = date;
	    }
	
	    public String getDes() {
	        return des;
	    }
	
	    public void setDes(String des) {
	        this.des = des;
	    }
	
	    public String getDownloadNum() {
	        return downloadNum;
	    }
	
	    public void setDownloadNum(String downloadNum) {
	        this.downloadNum = downloadNum;
	    }
	
	    public String getDownloadUrl() {
	        return downloadUrl;
	    }
	
	    public void setDownloadUrl(String downloadUrl) {
	        this.downloadUrl = downloadUrl;
	    }
	
	    public String getIconUrl() {
	        return iconUrl;
	    }
	
	    public void setIconUrl(String iconUrl) {
	        this.iconUrl = iconUrl;
	    }
	
	    public int getId() {
	        return id;
	    }
	
	    public void setId(int id) {
	        this.id = id;
	    }
	
	    public String getName() {
	        return name;
	    }
	
	    public void setName(String name) {
	        this.name = name;
	    }
	
	    public String getPackageName() {
	        return packageName;
	    }
	
	    public void setPackageName(String packageName) {
	        this.packageName = packageName;
	    }
	
	    public int getSize() {
	        return size;
	    }
	
	    public void setSize(int size) {
	        this.size = size;
	    }
	
	    public double getStars() {
	        return stars;
	    }
	
	    public void setStars(double stars) {
	        this.stars = stars;
	    }
	
	    public String getVersion() {
	        return version;
	    }
	
	    public void setVersion(String version) {
	        this.version = version;
	    }
	
	    public List<SafeBean> getSafe() {
	        return safe;
	    }
	
	    public void setSafe(List<SafeBean> safe) {
	        this.safe = safe;
	    }
	
	    public List<String> getScreen() {
	        return screen;
	    }
	
	    public void setScreen(List<String> screen) {
	        this.screen = screen;
	    }
	
	    public static class SafeBean {
	
	        private String safeDes;
	        private int safeDesColor;
	        private String safeDesUrl;
	        private String safeUrl;
	
	        public String getSafeDes() {
	            return safeDes;
	        }
	
	        public void setSafeDes(String safeDes) {
	            this.safeDes = safeDes;
	        }
	
	        public int getSafeDesColor() {
	            return safeDesColor;
	        }
	
	        public void setSafeDesColor(int safeDesColor) {
	            this.safeDesColor = safeDesColor;
	        }
	
	        public String getSafeDesUrl() {
	            return safeDesUrl;
	        }
	
	        public void setSafeDesUrl(String safeDesUrl) {
	            this.safeDesUrl = safeDesUrl;
	        }
	
	        public String getSafeUrl() {
	            return safeUrl;
	        }
	
	        public void setSafeUrl(String safeUrl) {
	            this.safeUrl = safeUrl;
	        }
	    }
	
	    @Override
	    public String toString() {
	        return "AppInfo{" +
	                "id=" + id +
	                ", author='" + author + '\'' +
	                ", date='" + date + '\'' +
	                ", des='" + des + '\'' +
	                ", downloadNum='" + downloadNum + '\'' +
	                ", downloadUrl='" + downloadUrl + '\'' +
	                ", iconUrl='" + iconUrl + '\'' +
	                ", name='" + name + '\'' +
	                ", packageName='" + packageName + '\'' +
	                ", size=" + size +
	                ", stars=" + stars +
	                ", version='" + version + '\'' +
	                ", safe=" + safe +
	                ", screen=" + screen +
	                '}';
	    }
	}


##<font color="#FF1493">2、DownloadManager的DownloadInfo下载信息</font>
	
	 /**
      *文件的下载信息;
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


##<font color="#FF1493">3、DownloadManager的具体实现</font>

	可以从3个角度分析下载：
	1、程序状态(编程记录)
	2、用户提示(UI展现)
	3、用户行为(触发操作)
	
	
	状态(编程记录)      用户提示(UI展现)        用户行为(触发操作)
	未下载          	     下载			        去下载
	下载中			     显示进度条		        暂停下载
	暂停下载		         继续下载		        断点继续下载
	等待下载		         等待中			        取消下载
	下载失败		         重试			        重试下载
	下载完成		         安装			        安装应用	
	已安装			     打开			        打开应用
------------------------------------------------------------------------------

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
	

##<font color="#FF1493">4、ListView中的Adapter的实现</font>

	/**
	 * 下载APP的适配器;
	 */
	public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppItemViewHolder> {
	
	    public static final String TAG="TAG";

		//集合中最多含有一个屏幕(五六个)左右的holder;
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
	    public class AppItemViewHolder extends RecyclerView.ViewHolder implements DownloadManager.DownloadObserver,View.OnClickListener {
	
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


##<font color="#FF1493">5、DownloadActivity的实现</font>

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



