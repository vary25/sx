package com.sxzq.oa;


import framework.utils.Log;
import imagelibrary.tools.universalimageloader.cache.memory.MemoryCacheAware;
import imagelibrary.tools.universalimageloader.cache.memory.impl.LruMemoryCache;
import imagelibrary.tools.universalimageloader.core.DisplayImageOptions;
import imagelibrary.tools.universalimageloader.core.ImageLoader;
import imagelibrary.tools.universalimageloader.core.ImageLoaderConfiguration;
import imagelibrary.tools.universalimageloader.core.assist.ImageLoadingListener;
import imagelibrary.tools.universalimageloader.core.assist.ImageScaleType;
import imagelibrary.tools.universalimageloader.core.assist.ImageSize;
import imagelibrary.tools.universalimageloader.core.assist.LoadedFrom;
import imagelibrary.tools.universalimageloader.core.assist.MemoryCacheUtil;
import imagelibrary.tools.universalimageloader.core.assist.QueueProcessingType;
import imagelibrary.tools.universalimageloader.core.display.BitmapDisplayer;
import imagelibrary.tools.universalimageloader.core.display.FadeInBitmapDisplayer;
import imagelibrary.tools.universalimageloader.core.process.BitmapProcessor;
import imagelibrary.tools.universalimageloader.utils.ImageSizeUtils;
import imagelibrary.tools.universalimageloader.utils.L;

import java.io.File;

import com.sxzq.oa.util.ImageUtil;
import com.sxzq.oa.util.ImageUtil.ScalingLogic;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

/**
 * 图片库类支持，对外提供、增加本地应用图标的异步加载
 * init、load、stop、clearMemCache、clearDiskCache、isMemCached、isDiskCached、option基础配置
 * @author Swei.Jiang 
 * @date 2013-8-8
 */
public class ImageDisplayer {
	/**
	 * 初始化配置 
	 * @param context
	 * @param threadPoolSize 线程池中线程数量
	 * @param memCache 内存策略
	 * @param memCacheSize 内存分配大小
	 * @param threadPriority 加载图片线程优先级
	 */
	public static void initImageDisplayer(Context context , int threadPoolSize, MemoryCacheAware<String, Bitmap> memCache, int memCacheSize ,int threadPriority){
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
		.threadPoolSize(threadPoolSize)
		.threadPriority(threadPriority)
		.memoryCache(memCache)
		.memoryCacheSize(memCacheSize)
//		.denyCacheImageMultipleSizesInMemory()
		.tasksProcessingOrder(QueueProcessingType.FIFO)
		.defaultDisplayImageOptions(getDefaultOption(context)).enableLogging() // Not
		.build();
		ImageLoader.getInstance().init(config);
	}
	
	
	public static void initImageDisplayer(Context context , int threadPoolSize, MemoryCacheAware<String, Bitmap> memCache,int threadPriority){
	    int memoryCacheSize;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR)
        {
            int memClass = ((ActivityManager) context.getSystemService (Context.ACTIVITY_SERVICE)).getMemoryClass ();
            memoryCacheSize = (memClass / 4) * 1024 * 1024; // 1/6 of app memory limit
        } else
        {
            memoryCacheSize = 4 * 1024 * 1024;
        }
        initImageDisplayer(context, threadPoolSize, memCache , memoryCacheSize , threadPriority);
	}
	
	public static void initImageDisplayer(Context context , int threadPoolSize, MemoryCacheAware<String, Bitmap> memCache){
		initImageDisplayer(context, threadPoolSize, memCache ,Thread.NORM_PRIORITY - 1 );
	}
	
	public static void initImageDisplayer(Context context , int threadPoolSize){
		   int memoryCacheSize;
	        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR)
	        {
	            int memClass = ((ActivityManager) context.getSystemService (Context.ACTIVITY_SERVICE)).getMemoryClass ();
	            memoryCacheSize = (memClass / 3) * 1024 * 1024; // 1/6 of app memory limit
	        } else
	        {
	            memoryCacheSize = 6 * 1024 * 1024;
	        }
		initImageDisplayer(context, threadPoolSize, new LruMemoryCache (memoryCacheSize));
	}
	
	public static void initImageDisplayer(Context context){
		initImageDisplayer(context, 5);
	}
	
	public static void initSingleQueueDisplayer(Context context){
		initImageDisplayer(context, 1);
	}
    /**
     * 停止当前所有正在加载的任务
     */
    public static void stop(){
       try{ 
    	   ImageLoader.getInstance ().stop ();
       }catch(Throwable e){
           e.printStackTrace();
       }
    }
    
    public static ImageLoader getImageLoader(){
    	return ImageLoader.getInstance();
    }
    
    /**
     * 清除内存缓存
     */
    public static void clearImageMemCache(){
        ImageLoader imgLoader = ImageLoader.getInstance ();
        imgLoader.clearMemoryCache ();
    }
    /**
     * 清除本地缓存
     */
    public static void clearImageDiskCache(){
    	 ImageLoader imgLoader = ImageLoader.getInstance ();
    	 imgLoader.clearDiscCache();
    }
    /**
     * 是否内存中存在
     * @param imageView
     * @param uri
     * @return
     */
    public static boolean isMemCached(ImageView imageView, String uri){
    	 String memKey = MemoryCacheUtil.generateKey (uri, getImageSizeScaleTo (imageView));
         return ImageLoader.getInstance ().getMemoryCache ().get (memKey) != null;
    }
    /**
     * 是否本地文件中有
     * @param uri 
     * @return
     */
    public static boolean isDiskCached(String uri){
    	 File file = ImageLoader.getInstance ().getDiscCache ().get (uri);
         return file.exists ();
    }
    /**
     * 异步装载图片
     * @param imageView 控件
     * @param uri 加载的uri，支持包名 "package://" + item.getPackageName()
     * @param loadingId 加载的默认图标,-1为使用默认option配置
     * @param listener 监听器
     * @param isAllow 是否允许加载
     */
	public static void load(ImageView imageView, String uri ,  int loadingId  ,ImageLoadingListener listener,boolean isWIFI){
		if(isWIFI){
			if(loadingId == -1){
				ImageLoader.getInstance().displayImage(uri, imageView,  listener);
			}else{
				DisplayImageOptions op = getDefaultOption(loadingId, loadingId, loadingId);
				ImageLoader.getInstance().displayImage(uri, imageView,op , listener);
			}
		}
		else{
				if(loadingId != -1){
					imageView.setImageResource(loadingId);
				}
		}
	}
	
	public static void load(ImageView imageView, String uri ,  DisplayImageOptions options  ,ImageLoadingListener listener){
			ImageLoader.getInstance().displayImage(uri, imageView, options, listener);
	}
	
	public static void loadRound(ImageView imageView, String uri ,  int loaddingView , ImageLoadingListener listener){
		DisplayImageOptions options  = getRoundOption(imageView, 9, loaddingView);
		load( imageView,  uri ,   options  , listener);
	}
	public static void loadTailorRound(ImageView imageView ,String uri , int loaddingView, ImageLoadingListener listener){
		DisplayImageOptions options  = getTailorRoundOption(imageView, loaddingView);
		load(imageView, uri,options  , listener);
	}
	public static void loadTailorRound(ImageView imageView ,String uri ,ImageLoadingListener listener , int defaultIconId){
		DisplayImageOptions options  = getTailorRoundOption(imageView, defaultIconId);
		load(imageView, uri,options  , listener);
	}
	
	public static void load(ImageView imageView, String uri ,  int loadingId  ,ImageLoadingListener listener){
		load(imageView, uri, loadingId, listener, true);
	}
	
	public static void load(ImageView imageView, String uri ,ImageLoadingListener listener){
		load(imageView, uri, -1, listener, true);
	}
	
	
	public static void load(ImageView imageView, String uri , int loaddingId){
		load(imageView, uri, loaddingId, null, true);
	}
	
	public static void load(ImageView imageView, String uri){
		load(imageView, uri, -1, null, true);
	}
	
	public static Bitmap loadByMemcache(ImageView imageView, String uri){
		String memKey = MemoryCacheUtil.generateKey (uri, getImageSizeScaleTo (imageView));
		return ImageLoader.getInstance().getMemoryCache().get(memKey);
	}
	
	public static void logMemCache(){
		MemoryCacheAware<String, Bitmap> mem = ImageLoader.getInstance().getMemoryCache();
		for(String key :mem.keys()){
			Log.i("test", "key:" + key);
		}
		Log.i("test", "*************************************************");
	}
	
	
	public static void putMemcache(ImageView imageView ,String uri , Bitmap bitmap){
		String memKey = MemoryCacheUtil.generateKey (uri, getImageSizeScaleTo (imageView));
		ImageLoader.getInstance().getMemoryCache().put(memKey, bitmap);
	}
	
	
	/**
	 * 每次加载图片的配置
	 * @param displayer 主线程展示接口
	 * @param config 图片解码格式 ，如RGB_565
	 * @param scaleType  是否在加载到本地策略
	 * @param stubView 加载中图标
	 * @param emptyView url为空图标
	 * @param errorView 出现错误图标
	 * @return
	 */
	public static DisplayImageOptions getDefaultOption(BitmapDisplayer displayer, Bitmap.Config config,
			ImageScaleType scaleType, int stubView, int emptyView, int errorView) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory().cacheOnDisc().displayer(displayer)
				.showStubImage(stubView).showImageForEmptyUri(emptyView)
				.showImageOnFail(errorView).bitmapConfig(config)
				.imageScaleType(scaleType).build();
		return options;
	}
	
	public static DisplayImageOptions getDefaultOption(BitmapProcessor postDisplayer , BitmapDisplayer uiDisplayer, Bitmap.Config config,ImageScaleType scaleType, int stubView, int emptyView, int errorView){
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.cacheInMemory().cacheOnDisc().displayer(uiDisplayer).preProcessor(postDisplayer)
		.showStubImage(stubView).showImageForEmptyUri(emptyView)
		.showImageOnFail(errorView).bitmapConfig(config)
		.imageScaleType(scaleType).build();
		return options;
	}
	
	public static DisplayImageOptions getRoundOption(ImageView imageView , int round , int loadingView){
		return getDefaultOption(new ImageDisplayer.RoundBitmapProcessorImpl(imageView,round), new ImageDisplayer.MyBitmapDisplayer(), Config.RGB_565, ImageScaleType.IN_SAMPLE_POWER_OF_2, loadingView, loadingView, loadingView);
	}
	
	public static DisplayImageOptions getTailorRoundOption(ImageView imageView , int loadingView){
		return getDefaultOption(new ImageDisplayer.TailorRoundBitmapProcessorImpl(), new ImageDisplayer.MyBitmapDisplayer(), Config.RGB_565, ImageScaleType.IN_SAMPLE_POWER_OF_2, loadingView, loadingView, loadingView);
	}
	
	public static class TailorRoundBitmapProcessorImpl implements BitmapProcessor{
		@Override
		public Bitmap process(Bitmap bitmap) {
			return ImageUtil.toRoundBitmap(bitmap);
		}
	}
	
	public static class RoundBitmapProcessorImpl implements BitmapProcessor{

		@Override
		public Bitmap process(Bitmap bitmap) {
			return roundCorners(bitmap, imageView , roundPixels);
		}
		
		private final int roundPixels;
		private ImageView imageView;

		public RoundBitmapProcessorImpl(ImageView imageView,int roundPixels) {
			this.roundPixels = roundPixels;
			this.imageView = imageView;
		}

		public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) { 
			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888); 
			Canvas canvas = new Canvas(output); 
			final int color = 0xff424242; 
			final Paint paint = new Paint(); 
			final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()); 
			final RectF rectF = new RectF(rect); 
			final float roundPx = pixels; 
			paint.setAntiAlias(true); 
			canvas.drawARGB(0, 0, 0, 0); 
			paint.setColor(color); 
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint); 
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN)); 
			canvas.drawBitmap(bitmap, rect, rect, paint); 
			return output; 
			} 
		
		/**
		 * Process incoming {@linkplain Bitmap} to make rounded corners according to target {@link ImageView}.<br />
		 * This method <b>doesn't display</b> result bitmap in {@link ImageView}
		 * 
		 * @param bitmap Incoming Bitmap to process
		 * @param imageView Target {@link ImageView} to display bitmap in
		 * @param roundPixels
		 * @return Result bitmap with rounded corners
		 */
		public static Bitmap roundCorners(Bitmap bitmap, ImageView imageView, int roundPixels) {
			Bitmap roundBitmap;

			int bw = bitmap.getWidth();
			int bh = bitmap.getHeight();
			int vw = imageView.getWidth();
			int vh = imageView.getHeight();
			if (vw <= 0) vw = bw;
			if (vh <= 0) vh = bh;

			int width, height;
			Rect srcRect;
			Rect destRect;
			switch (imageView.getScaleType()) {
				case CENTER_INSIDE:
					float vRation = (float) vw / vh;
					float bRation = (float) bw / bh;
					int destWidth;
					int destHeight;
					if (vRation > bRation) {
						destHeight = Math.min(vh, bh);
						destWidth = (int) (bw / ((float) bh / destHeight));
					} else {
						destWidth = Math.min(vw, bw);
						destHeight = (int) (bh / ((float) bw / destWidth));
					}
					int x = (vw - destWidth) / 2;
					int y = (vh - destHeight) / 2;
					srcRect = new Rect(0, 0, bw, bh);
					destRect = new Rect(x, y, x + destWidth, y + destHeight);
					width = vw;
					height = vh;
					break;
				case FIT_CENTER:
				case FIT_START:
				case FIT_END:
				default:
					vRation = (float) vw / vh;
					bRation = (float) bw / bh;
					if (vRation > bRation) {
						width = (int) (bw / ((float) bh / vh));
						height = vh;
					} else {
						width = vw;
						height = (int) (bh / ((float) bw / vw));
					}
					srcRect = new Rect(0, 0, bw, bh);
					destRect = new Rect(0, 0, width, height);
					break;
				case CENTER_CROP:
					vRation = (float) vw / vh;
					bRation = (float) bw / bh;
					int srcWidth;
					int srcHeight;
					if (vRation > bRation) {
						srcWidth = bw;
						srcHeight = (int) (vh * ((float) bw / vw));
						x = 0;
						y = (bh - srcHeight) / 2;
					} else {
						srcWidth = (int) (vw * ((float) bh / vh));
						srcHeight = bh;
						x = (bw - srcWidth) / 2;
						y = 0;
					}
					width = Math.min(vw, bw);
					height = Math.min(vh, bh);
					srcRect = new Rect(x, y, x + srcWidth, y + srcHeight);
					destRect = new Rect(0, 0, width, height);
					break;
				case FIT_XY:
					width = vw;
					height = vh;
					srcRect = new Rect(0, 0, bw, bh);
					destRect = new Rect(0, 0, width, height);
					break;
				case CENTER:
				case MATRIX:
					width = Math.min(vw, bw);
					height = Math.min(vh, bh);
					x = (bw - width) / 2;
					y = (bh - height) / 2;
					srcRect = new Rect(x, y, x + width, y + height);
					destRect = new Rect(0, 0, width, height);
					break;
			}

			try {
				roundBitmap = getRoundedCornerBitmap(bitmap, roundPixels, srcRect, destRect, width, height);
			} catch (OutOfMemoryError e) {
				L.e(e, "Can't create bitmap with rounded corners. Not enough memory.");
				roundBitmap = bitmap;
			}

			return roundBitmap;
		}

		private static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int roundPixels, Rect srcRect, Rect destRect, int width, int height) {
			Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			final Paint paint = new Paint();
			final RectF destRectF = new RectF(destRect);

			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(0xFF000000);
			
			canvas.drawRoundRect(destRectF, roundPixels, roundPixels, paint);

			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			canvas.drawBitmap(bitmap, srcRect, destRectF, paint);

			return output;
		}
		
		
	}
	
	
	public static class BlurBitmapProcessorImpl implements BitmapProcessor{
		private Context mContext;
		private int desWidth,desHeight;
		
		public BlurBitmapProcessorImpl( Context mContext ,int desWidth, int desHeight) {
			super();
			this.mContext = mContext;
			this.desWidth = desWidth;
			this.desHeight = desHeight;
		}

		@Override
		public Bitmap process(Bitmap bitmap) {
			Bitmap bitmap2 = null;
			if(bitmap != null){
				bitmap = ImageUtil.createScaledBitmap(bitmap, desWidth, desHeight, ScalingLogic.FIT);
				bitmap2 = ImageUtil.fastblur(mContext,bitmap,12);
				if(bitmap != null && !bitmap.isRecycled()){
					bitmap.recycle();
					bitmap = null;
				}
			}
			return bitmap2;
		}
	}
	
	public static void cancel(ImageView imageView){
		ImageLoader.getInstance().cancelDisplayTask(imageView);
	}
	
	public static DisplayImageOptions getDefaultOption(BitmapDisplayer displayer,
			ImageScaleType scaleType, int stubView, int emptyView, int errorView) {
		return getDefaultOption(displayer, Bitmap.Config.RGB_565, scaleType , stubView , emptyView , errorView);
	}
	
	public static DisplayImageOptions getDefaultOption(BitmapDisplayer displayer, int stubView, int emptyView, int errorView){
		return  getDefaultOption(displayer, Bitmap.Config.RGB_565, ImageScaleType.IN_SAMPLE_POWER_OF_2 , stubView , emptyView , errorView);
	}
	public static DisplayImageOptions getDefaultOption(int stubView, int emptyView, int errorView){
		return  getDefaultOption(new MyBitmapDisplayer(), Bitmap.Config.RGB_565, ImageScaleType.IN_SAMPLE_POWER_OF_2 , stubView , emptyView , errorView);
	}
	//注：图片加载失败还是加载中图标;TODO 无图
	public static DisplayImageOptions getDefaultOption(Context mContext){
		//getApplicationContext().getResources().getIdentifier(name,"drawable", getApplicationContext().getPackageName())
		return  getDefaultOption(new MyBitmapDisplayer(), Bitmap.Config.RGB_565, ImageScaleType.IN_SAMPLE_POWER_OF_2 , mContext.getResources().getIdentifier("sinasdk_mgp_icon_default1","drawable", mContext.getPackageName()),mContext.getResources().getIdentifier("sinasdk_mgp_icon_default1","drawable", mContext.getPackageName()), mContext.getResources().getIdentifier("sinasdk_mgp_icon_default1","drawable", mContext.getPackageName()));
	}
	
    
    /**
     * 加载出现动画，实现如果只有联网下载图片的时候加载cos速率算法动画
     * 内存缓存和本地缓存不出现动画效果
     * @author Swei.Jiang 
     * @date 2013-8-8
     */
    public static class MyBitmapDisplayer implements BitmapDisplayer{

        @Override
        public Bitmap display(Bitmap bitmap,ImageView imageView,LoadedFrom loadedFrom)
        {
            if(loadedFrom == LoadedFrom.NETWORK){
                FadeInBitmapDisplayer.animate (imageView, new AccelerateDecelerateInterpolator() , 500);
            }
            imageView.setImageBitmap (bitmap);
            return bitmap;
        }
    }
    
    public static class MyBitmapDownloadDisplayer implements BitmapDisplayer{

		@Override
		public Bitmap display(Bitmap bitmap, ImageView imageView, LoadedFrom loadedFrom) {
			return bitmap;
		}
    }
    
    
    /**
     * 获取下载地址的url-image 对象
     * @param imageView
     * @return
     */
    public static ImageSize getImageSizeScaleTo(ImageView imageView)
    {
        return     ImageSizeUtils.defineTargetSizeForView(imageView, 0, 0);
    }
}
