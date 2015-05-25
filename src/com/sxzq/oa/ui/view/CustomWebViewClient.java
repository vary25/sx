package com.sxzq.oa.ui.view;

import com.sxzq.oa.util.CommenUtil;

import framework.annotation.log.LogUtil;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * webview 的client，用于与html页面交互，处理在线拨打，本地展示等功能。
 * @author lvxuejun
 *
 */
public class CustomWebViewClient extends WebViewClient {
	
	Activity context;
	
	public CustomWebViewClient(Activity context) {
		super();
		this.context = context;
	}


	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		boolean isLocal = (url.startsWith("content://") || url.startsWith("file://")) ? true : false;
		// 本地文件处理
		if (isLocal) {
			// 文件实际路径

//			Intent localIntent = ActivityUtil.getIntentForShowFile(
//					context, filePath);
//			if (localIntent != null
//					&& !localIntent.getType().equalsIgnoreCase("text/html")) {
//				context.startActivity(localIntent);
//			} else {
//				view.loadUrl(url);
//			}
		}
		// 远程文件处理
		else {
			try {
				String fileType = CommenUtil.getMiniTypeFromUrl(url);
				String imgPath = "";
				if(url.toLowerCase().startsWith("http://")){
					if (fileType.equals("audio/*")||fileType.equals("video/*")
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
						context.startActivity(intent);
					}else if (fileType.equals("image/*")) {
						try {
//							imgPath = StringUtil._RootStorePath
//									+ StringUtil.toMD5(url)
//									+ url.substring(url.lastIndexOf("."));
//							//To do 打开图片查看页面
//							Engine.viewImage(context, url, imgPath);
						} catch (Exception e) {
							e.printStackTrace();
						}
					/*}else if(url.toLowerCase().contains("/getfile?")){
						try {
							imgPath = StringUtil._RootStorePath+ StringUtil.toMD5(url)+ ".jpg";
							//To do 打开图片查看页面
							Engine.viewImage(context, url, imgPath);
						} catch (Exception e) {
							e.printStackTrace();
						} */
						
					}else if(fileType.equals("link")){
						view.loadUrl(url);
					}else{
						view.loadUrl(url);
					}
				}else{
					Uri uri = Uri.parse(url); 
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					context.startActivity(intent);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	@Override
	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl) {
		String msg = "内容加载失败，请检查网络";
//		if (context instanceof UI) {
//			((UI)context).showMsg(msg);
//		}else if (context instanceof BaseFragmentActivity) {
//			((BaseFragmentActivity)context).showMsg(msg);
//		}else{
//			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
//		}
		view.loadDataWithBaseURL("", msg, "text/html", "utf-8", "");
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		LogUtil.d("url:" + url);
		super.onPageStarted(view, url, favicon);
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		super.onPageFinished(view, url);
	}
	
}
