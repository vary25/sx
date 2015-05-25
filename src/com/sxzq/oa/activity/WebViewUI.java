package com.sxzq.oa.activity;

import java.util.Hashtable;

import com.sxzq.oa.R;
import com.sxzq.oa.ui.view.CustomWebViewClient;
import com.sxzq.oa.ui.view.TTWebView;
import com.sxzq.oa.ui.view.TTWebViewToolBar;

import framework.annotation.log.LogUtil;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;


/**
 * 查看在线wap的页面
 * @author lvxuejun
 *
 */
public class WebViewUI extends BaseActivity {

	private TTWebView mWebView;
	private ProgressBar mProgressBar;
	private ProgressBar mProgressBar1;
	String url ="";
	private TTWebViewToolBar toolBar;
	private boolean isClearHistory = true;
	private String imgPath= "";
	static final int msgShowSaveMsg = 201;
	static final int msgShowProgressDialog = 205;
	static final int msgHideProgressDialog = 206;
	private ViewGroup rootView,webview_container;
	@Override
	public void initView() {
		setContentView(R.layout.framework_webview);
//		findViewById(R.id.btn_exit).setOnClickListener(this);
		
		mProgressBar = (ProgressBar) findViewById(R.id.tt_view_probar);
		mProgressBar1 = (ProgressBar) findViewById(R.id.view_progress_bar);
		mWebView = (TTWebView) findViewById(R.id.tt_view_webview);
		mWebView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
//		android:fastScrollEnabled="true"
//			android:scrollbarStyle="insideOverlay"	
		webview_container = (ViewGroup) findViewById(R.id.framework_webview_container);
//		webview_container.addView(mWebView);
		rootView = (ViewGroup) findViewById(R.id.framework_webview_root);
//		btnFinish = findViewById(R.id.btn_exit);
//		btnFinish.setVisibility(View.VISIBLE);
//		btnFinish.setOnClickListener(this);
		toolBar = (TTWebViewToolBar) findViewById(R.id.tt_view_toolbar);
//		findViewById(R.id.view1).setVisibility(View.GONE);
		//loadingDialog = onCreateDialogByResId(R.string.loadingui, true);
		//loadingDialog.setIndeterminate(true);
	}

	public void initData() {
		String title = "";
		Bundle bundle = getIntent().getExtras();
		if (bundle==null) {
		}else{
			url = bundle.getString("url");
			if (bundle.containsKey("title")) {
				title = bundle.getString("title");
			}
			if (bundle.containsKey("show_toolbar") && bundle.getInt("show_toolbar", 1)==0) {
				toolBar.setVisibility(View.GONE);
			}
		}
		
		
		if (bundle.containsKey("title")) {
			title = bundle.getString("title");
			setTitle(title);
//			if (title !=null && !"".equalsIgnoreCase(title)) {
//				this.findViewById(R.id.home_title_img).setVisibility(View.GONE);
//				TextView tvTitle = (TextView) this.findViewById(R.id.view_title_txt);
//				tvTitle.setVisibility(View.VISIBLE);
//				tvTitle.setText(title);
//			}
			
		}
		
		
		if(url != null && !url.trim().equalsIgnoreCase("")){
			//loadingDialog.show();
			mProgressBar1.setVisibility(View.VISIBLE);
			mWebView.loadUrl(url);
			mWebView.setWebChromeClient(new CustomWebChromeClient());
			mWebView.setWebViewClient(new MCustomWebViewClient(this));
		}
		toolBar.initBar(mWebView,mHandler);
	}

	
	
	@Override
	protected void onDestroy() {
		try {
			if (mWebView!=null) {
				mWebView.loadDataWithBaseURL("", "", "text/html", "utf-8", "");
				mWebView.clearCache(false);
				mWebView.destroyDrawingCache();
				mWebView.destroy();
				webview_container.removeAllViews();
				rootView.removeAllViews();
				System.gc();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}



	public class CustomWebChromeClient extends WebChromeClient{
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
			if (newProgress > 0) {
				mProgressBar.setVisibility(View.VISIBLE);
			}
			Message m = Message.obtain();
			m.what = newProgress;
			mHandler.sendMessage(m);
			if (newProgress >= 100) {
				mProgressBar.setVisibility(View.GONE);
				mProgressBar.setProgress(0);
				toolBar.setStatus();
			}
		}

		 @Override
		    public void onConsoleMessage(String message, int lineNumber, String sourceID) {
		        super.onConsoleMessage(message, lineNumber, sourceID);
		    }
		
		@Override
		public void onReceivedTouchIconUrl(WebView view, String url,
				boolean precomposed) {
			super.onReceivedTouchIconUrl(view, url, precomposed);
		}
		
	}

	private class MCustomWebViewClient extends CustomWebViewClient{

		public MCustomWebViewClient(Activity context) {
			super(context);
		}



		@Override
		public void onLoadResource(WebView view, String url) {
			super.onLoadResource(view, url);
		}



		@Override
		public void doUpdateVisitedHistory(WebView view, String url,
				boolean isReload) {
			super.doUpdateVisitedHistory(view, url, isReload);
		}



		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			LogUtil.d(this.getClass(), "Loading Url: " + url);
			/*boolean isLocal = (url.startsWith("content://") || url.startsWith("file://"))?true:false;

			// 本地文件处理
			if(isLocal){
				// 文件实际路径
				String filePath = url.substring(url.indexOf(Constants.TT_STORE), url.length());
				if(filePath.charAt(filePath.length()-1) == '/')
					filePath = filePath.substring(0, filePath.length() - 1);
				LogUtil.d(this.getClass(), "filePath:" + filePath);

				String urlPath = "";
				if (filePath.contains(msg.getFolderName())) {
					urlPath = filePath.substring(filePath.indexOf(msg.getFolderName())+msg.getFolderName().length());
				}else{
					urlPath = filePath.substring(filePath.lastIndexOf("/")+1);
				}
				LogManager.logUserView(WebViewUI.this,urlPath , msg.getMailId(),msg.getTtId());

				Intent localIntent = ActivityUtil.getIntentForShowFile(getApplicationContext(), filePath);
				if(localIntent != null && !localIntent.getType().equalsIgnoreCase("text/html")){
					startActivity(localIntent);
				}else{
					view.loadUrl(url);
				}
			}
			// 远程文件处理
			else{
				String fileType = StringUtil.getMiniTypeFromUrl(url);

				//在线拨打
				if (url.toLowerCase().startsWith("tel:")) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					startActivity(intent);
				}else{
					if(fileType.equals("link")){
						view.loadUrl(url);
					}else if (fileType.equals("audio/*")||fileType.equals("video/*")
							|| fileType.equals("apk")
							|| fileType.equals("application/vnd.android.package-archive")
							|| fileType.equals("application/msword")
							|| fileType.equals("application/msexcel")
							|| fileType.equals("application/vnd.ms-powerpoint")
							|| fileType.equals("application/pdf")
							|| fileType.equals("application/rar")
							|| fileType.equals("application/zip")) {
						Uri uri = Uri.parse(url); 
						Intent intent = new Intent(Intent.ACTION_VIEW, uri);
						startActivity(intent);
					}else if (fileType.equals("image/*")) {
						Intent imgIntent = new Intent();
						imgIntent.setClass(getApplicationContext(), ImageViewUI.class);
						Bundle bundle = new Bundle();
						imgPath = Constants.TT_DOWNLOAD_ROOT + StringUtil.toMD5(url) + url.substring(url.lastIndexOf("."));
						bundle.putString("imgUrl", url);
						bundle.putString("imgPath", imgPath);
						imgIntent.putExtras(bundle);
						WebViewUI.this.startActivity(imgIntent);
						//new viewPictureTask().execute(url);
					}else{
						view.loadUrl(url);
					}
				}
			}
			*/
			if(url.toLowerCase().startsWith("voiceofchina://recommend")){
//				try {
//					String query = url.substring(url.indexOf("?")+1);
//					//TODO 自荐
//					String decodeQuery =  new String(Base64Util.decode(query), "utf-8");
//					Hashtable<String, String> hashQuery = StringUtil.getUrlParam(decodeQuery, true);
//					String obj = hashQuery.get("obj");
//					LogManager.d("obj:" + obj);
//					UserRecommend uRecommend = new UserRecommend(WebViewUI.this);
//					uRecommend.getUserRecommend(Constants.userId, obj,getString(R.string.self_recommend));
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				
				return true;
			}else if(url.toLowerCase().contains("closepage")){
				finish();
				return true;
			}else{
				return super.shouldOverrideUrlLoading(mWebView, url);
			}
		}

		
		
		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler,
				SslError error) {
			handler.proceed();			
			//super.onReceivedSslError(view, handler, error);
		}



		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			mHandler.sendEmptyMessage(msgHideProgressDialog);
			LogUtil.d(this.getClass(), "onReceivedError errorCode = " + errorCode);
			view.loadData("", "","utf-8");
			toolBar.setStatus();
			Toast.makeText(WebViewUI.this, "页面加载失败，请检查网络", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			try {
				mHandler.sendEmptyMessage(msgShowProgressDialog);
			} catch (Exception e) {
			}
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			mHandler.sendEmptyMessage(msgHideProgressDialog);
			if (isClearHistory) {
				view.clearHistory();
				isClearHistory = false;
			}
			toolBar.setStatus();
			super.onPageFinished(view, url);
			
		}

	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if(mWebView.canGoBack()){
				mWebView.goBack();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case TTWebViewToolBar.Msg.home:
				isClearHistory = true;
				mWebView.loadUrl(url);
				
				break;
			case TTWebViewToolBar.Msg.show:
				toolBar.showBar();
				break;
			case TTWebViewToolBar.Msg.hide:
				toolBar.hiddenBar();
				break;
			case msgShowSaveMsg:
				if (!"".equalsIgnoreCase(imgPath)) {
					Toast.makeText(WebViewUI.this, "图片已保存至:" + imgPath, Toast.LENGTH_LONG).show();
				}
				
				break;
			case msgShowProgressDialog:
				try {
//					if (loadingDialog!=null) {
//						loadingDialog.show();
//					}
					mProgressBar1.setVisibility(View.VISIBLE);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case msgHideProgressDialog:
				try {
//					if (loadingDialog!=null) {
//						loadingDialog.dismiss();
//					}
					mProgressBar1.setVisibility(View.GONE);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			default:
				mProgressBar.setProgress(msg.what);
				break;
			}

			super.handleMessage(msg);
		}
	};

//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.btn_exit:
//			finish();
//			break;
//		}
//	}
}
