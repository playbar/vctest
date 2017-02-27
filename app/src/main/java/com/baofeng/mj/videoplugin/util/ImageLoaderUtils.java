package com.baofeng.mj.videoplugin.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.baofeng.mj.videoplugin.R;
import com.baofeng.mj.videoplugin.download.FileStorageDirUtil;
import com.bfmj.viewcore.view.GLImageView;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by qiguolong on 2016/7/19.
 * 图片加载工具类
 */
public class ImageLoaderUtils {
    private static ImageLoaderUtils instance;//单例
    private ImageLoader imageLoader;
    private String fileDir;
    public ExecutorService threadPool = Executors.newFixedThreadPool(3);
    public int defaultid=0;
    private static Context mContext;

    private DisplayImageOptions.Builder optionsBuilder;
    private DisplayImageOptions imageOptions;

    public static ImageLoaderUtils getInstance(Context context) {
        mContext = context;
        if (instance == null) {
            instance = new ImageLoaderUtils();
        }
        return instance;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    /**
     * 初始化 大部分copy
     */
    private ImageLoaderUtils() {
        // 用于指导每一个Imageloader根据网络图片的状态（空白、下载错误、正在下载）显示对应的图片，是否将缓存加载到磁盘上，下载完后对图片进行怎么样的处理。
        optionsBuilder = new DisplayImageOptions.Builder();
        // optionsBuilder.showImageForEmptyUri(R.drawable.local_image_default);//
        // 地址为空显示默认缩略图
        // optionsBuilder.showImageOnFail(R.drawable.local_image_default);// 图片加载失败显示默认缩略图
        // optionsBuilder.showImageOnLoading(R.drawable.local_image_default);//
        // 正在加载中
        optionsBuilder.delayBeforeLoading(0);
        optionsBuilder.cacheInMemory(true); // 内存存储
        optionsBuilder.cacheOnDisc(true);
        optionsBuilder.imageScaleType(ImageScaleType.IN_SAMPLE_INT);
        optionsBuilder.bitmapConfig(Bitmap.Config.RGB_565);
        // optionsBuilder.displayer(new RoundedBitmapDisplayer(5));//
        // 设置显示风格这里是圆角矩形
        // optionsBuilder.

        DisplayImageOptions options = optionsBuilder.build();

        // 针对图片缓存的全局配置，主要有线程类、缓存大小、磁盘大小、图片下载与解析、日志方面的配置。
        ImageLoaderConfiguration.Builder configBuilder = new ImageLoaderConfiguration.Builder(
                mContext);//一定要是获取整个应用程序的上下文，不然会内存泄露
        configBuilder.threadPriority(Thread.NORM_PRIORITY - 2); // 线程池内加载的数量
        configBuilder.denyCacheImageMultipleSizesInMemory();
        //新增设置缓存位置
        fileDir = FileStorageDirUtil
                .getSavePath();
        File file = new File(fileDir);
        if (!file.isDirectory())
            file.mkdirs();
        configBuilder.discCache(new UnlimitedDiscCache(file));
        configBuilder.tasksProcessingOrder(QueueProcessingType.FIFO);

        configBuilder.defaultDisplayImageOptions(options);// 设置图片规则
        ImageLoaderConfiguration config = configBuilder.build();

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);

    }

    /**
     * 缓存目录
     * @return
     */
    public String getFileDir() {
        return fileDir;
    }

    /**
     * 缓存文件路径
     * @return
     */
    public String getFilePath(String url) {
        return fileDir + File.separator + String
                .valueOf(url.hashCode());
    }

    /**
     * 直接获取loader的缓存图 建议异步调用
     *
     * @param url
     * @return
     */
    public static Bitmap getImgCach(String url) {
        try {
            return BitmapFactory.decodeFile(getInstance(mContext).getFileDir() + File.separator + String
                    .valueOf(url.hashCode()));
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 设置默认图
     * @param id
     */
    public void configDeault(int id) {
        getInstance(mContext).defaultid = id;

    }

    /**
     *  加载图片后自动set至image
     * @param context
     * @param url
     * @param glImageView
     */
    public  void loadImage(final Context context, final String url, final GLImageView
            glImageView) {
       threadPool.execute(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = getInstance(mContext).getImageLoader().loadImageSync(url);
                MJGLUtils.exeGLQueueEvent(context, new Runnable() {
                    @Override
                    public void run() {
                        if (bitmap != null) {
                            glImageView.setImage(bitmap);
                        } else {
                            if (defaultid<1)
                                 glImageView.setImage(defaultid);
                        }
                    }
                });
            }
        });
    }

    /**
     * 当加载完图片后 回调
     *
     * @param context
     * @param url
     * @param loadComplete
     */
    public  void loadImage(final Context context, final String url, final LoadComplete
            loadComplete) {
       threadPool.execute(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = getInstance(mContext).getImageLoader().loadImageSync(url);
                MJGLUtils.exeGLQueueEvent(context, new Runnable() {
                    @Override
                    public void run() {
                        loadComplete.loadImage(bitmap);
                    }
                });
            }
        });
    }

    /**
     * load完回调
     */
    public interface LoadComplete {
        public void loadImage(Bitmap bitmap);
    }
    public  static  void  destroy(){

        if (instance!=null) {
            instance.threadPool.shutdownNow();
            instance.getImageLoader().destroy();
        }
        instance=null;

    }

    public DisplayImageOptions getImageOptions(){
        if(imageOptions == null){
            optionsBuilder.showImageForEmptyUri(R.mipmap.list_pic);// 地址为空显示默认缩略图
            optionsBuilder.showImageOnFail(R.mipmap.list_pic);// 图片加载失败显示默认缩略图
            optionsBuilder.showImageOnLoading(R.mipmap.list_pic);// 正在加载中
//            optionsBuilder.decodingOptions(MinifyImageUtil.getInstance().ImageOption());
            // optionsBuilder.displayer(new RoundedBitmapDisplayer(5));//设置显示风格这里是圆角矩形
            imageOptions = optionsBuilder.build();
        }
        return imageOptions;
    }
}
