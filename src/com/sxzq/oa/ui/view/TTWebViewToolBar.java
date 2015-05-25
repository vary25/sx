package com.sxzq.oa.ui.view;

import java.util.logging.LogManager;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sxzq.oa.R;

public class TTWebViewToolBar extends LinearLayout implements OnClickListener{

	private Context context;
	private WebView webview = null;
	private ImageView imgBack,imgForward,imgHome,imgRefresh,imgHidden,imgShow,imgFullScreen;
	private LinearLayout llControl,llShow;
//	private boolean isShowBar,isWithFullScreen = false,isFullScreen=false;
	private Animation animation;
//	private String homeUrl = "";
	private Handler handler;
	private int showTimes = 3000,keepTimes=0;
	
	
	public static class Msg{
		public static final int home = 111;
		public static final int show = 112;
		public static final int hide = 113;
		public static final int fullscreen=114;
		public static final int restorescreen=115;
	}
	
	
	public void initBar(WebView wv,Handler handler){
		webview = wv;
//		homeUrl = webview.getUrl();
		this.handler = handler;
	}
	
	public void initBar(WebView wv,Handler handler,boolean isWithFullScrean,boolean isFullScreen){
		webview = wv;
//		homeUrl = webview.getUrl();
		this.handler = handler;
//		this.isWithFullScreen = isWithFullScrean;
//		this.isFullScreen = isFullScreen;
		if (isWithFullScrean) {
			imgFullScreen.setVisibility(View.VISIBLE);
		}
	}
	
	public TTWebViewToolBar(Context context, AttributeSet attrs) {
		super(context, attrs);

		try {
			this.context = context;
//			isShowBar = false;
//			TypedArray typeArr = context.obtainStyledAttributes(attrs,R.styleable.TTWebViewToolBar);    
//			int webviewId = typeArr.getResourceId(R.styleable.TTWebViewToolBar_webViewId, 0);
//
//			webview  =  (WebView) findViewById(webviewId);
//			
//			typeArr.recycle();
			llControl = new LinearLayout(context,attrs);
			llControl.setBackgroundColor(0X85000000);
//			llControl.setBackgroundResource(R.drawable.btn_browser_bg);
			llShow = new LinearLayout(context,attrs);
			llShow.setBackgroundResource(R.drawable.btn_browser_bg);
			
			imgHome = getView(R.drawable.btn_browser_home);
			imgBack = getView(R.drawable.btn_browser_back);
			imgForward = getView(R.drawable.btn_browser_forward);
			imgRefresh = getView(R.drawable.btn_browser_refresh);
			
//			imgFullScreen = getView(R.drawable.btn_browser_fullscreen);
//			imgFullScreen.setVisibility(View.GONE);
			
			imgHidden = getView(R.drawable.btn_browser_draw);
			
			imgShow = getView(R.drawable.btn_browser_draw_collapse,llShow,0);
			//imgShow.setLayoutParams(params);
			llControl.setVisibility(View.GONE);
			imgShow.setVisibility(View.VISIBLE);
			
			this.addView(llControl,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
			this.addView(llShow,new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.FILL_PARENT));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private ImageView getView(int resId){
		return getView(resId,llControl,1);
	}
	
	private ImageView getView(int resId,LinearLayout parent,float weight){
		
		ImageView img = new ImageView(context, null, R.style.WebViewToolBarImageStyle);
		img.setImageResource(resId);
		img.setOnClickListener(this);
		if (weight>0) {
			LayoutParams imgparams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,weight);
			parent.addView(img,imgparams);
		}else{
			parent.addView(img);
		}
			
		
		return img;
	}
	
	public TTWebViewToolBar(Context context,WebView toContrlWebView, AttributeSet attrs) {
		super(context, attrs);				
	}
	
	public void setStatus(){
		imgBack.setEnabled(false);
		imgForward.setEnabled(false);
		if (webview!=null) {
			if (webview.canGoBack()) {
				imgBack.setEnabled(true);
			}else{
				imgBack.setEnabled(false);
			}
			
			if (webview.canGoForward()) {
				imgForward.setEnabled(true);
			}else{
				imgForward.setEnabled(false);
			}
		}
	}

	@Override
	public void onClick(View v) {
		keepTimes = 0;
		if (v.equals(imgHome)) {
			handler.sendEmptyMessage(Msg.home);
			//webview.loadUrl(homeUrl);
		}else if(v.equals(imgBack)){
			if (webview.canGoBack()) {
				webview.goBack();
			}
			
		}else if (v.equals(imgForward)) {
			if (webview.canGoForward()) {
				webview.goForward();
			}
		}else if (v.equals(imgRefresh)) {
			webview.reload();
		}else if (v.equals(imgShow)) {
			showBar();
			//new TimerThread().start();
		}else if(v.equals(imgHidden)){
			keepTimes = showTimes;
			hiddenBar();
//		}else if (v.equals(imgFullScreen)) {
//			if (isFullScreen) {
//				handler.sendEmptyMessage(Msg.restorescreen);
//				isFullScreen = false;
//			}else{
//				handler.sendEmptyMessage(Msg.fullscreen);
//				isFullScreen = true;
//			}			
		}
		
		//setStatus();
	}

	/**
	 * 
	 */
	public void showBar() {
		animation = AnimationUtils.makeInAnimation(context, false);
		animation.setDuration(500);
		llControl.setAnimation(animation);
		animation.start();
		llControl.setVisibility(View.VISIBLE);
		imgShow.setVisibility(View.GONE);
	}

	/**
	 * 
	 */
	public void hiddenBar() {
		llControl.setVisibility(View.GONE);
		animation = AnimationUtils.makeOutAnimation(context, true);
		animation.setDuration(500);
		llControl.setAnimation(animation);
		animation.start();
		imgShow.setVisibility(View.VISIBLE);
	}
	
	public class TimerThread extends Thread{
		public TimerThread(){
			keepTimes = 0;
		}

		@Override
		public void run() {
			try {
				while (true) {
					if (keepTimes>=showTimes) {
						handler.sendEmptyMessage(Msg.hide);
						break;
					}else{
						keepTimes += 100;
						sleep(100);
					}
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			super.run();
		}
		
		
	}
	
}
