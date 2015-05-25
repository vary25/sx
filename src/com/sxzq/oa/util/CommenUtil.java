package com.sxzq.oa.util;

import com.sxzq.oa.activity.WebViewUI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

public class CommenUtil {

	public static void openLink(Context context, String link){
		if (!TextUtils.isEmpty(null)) {
			if (link.toLowerCase().startsWith("http:")) {
				viewUrl(context, link, "", true);
			}else{
				Intent intent = new Intent("android.intent.action.VIEW");
				intent.setAction(link);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}
		}
	}
	
	public static void viewUrl(Context context, String url,String title,boolean isShowBar) {
		Intent webIntent = new Intent(context,WebViewUI.class);
		Bundle bundle = new Bundle();
		bundle.putString("url", url);
		bundle.putString("title", title);
		if (isShowBar) {
			bundle.putInt("show_toolbar", 1);
		}else{
			bundle.putInt("show_toolbar", 0);
		}
		
		webIntent.putExtras(bundle);
		webIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | 
				Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS); 
		context.startActivity(webIntent);
	}
	
	/**
	 * 获取url连接的扩展名
	 * @param url
	 * @return
	 */
	public static String getMiniTypeFromUrl(String url){
		String end = url.substring(url.lastIndexOf(".") + 1,
				url.length()).toLowerCase();
		if(end.indexOf("?") > -1){
			end = end.substring(0,end.indexOf("?"));
		}
		if(end.indexOf("/") > -1){
			end = end.substring(0, end.length() - 1);
		}
		
		String type = "";
		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			type = "audio/*";
		} else if (end.equals("3gp") || end.equals("mp4")) {
			type = "video/*";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			type = "image/*";
		} else if (end.equals("apk")) {
			/* android.permission.INSTALL_PACKAGES */
			type = "application/vnd.android.package-archive";
		} 
		//加入对ms office及pdf,rar,zip的判断
		else if (end.equals("doc")){
			type = "application/msword";
		}else if (end.equals("docx")){
			type = "application/msword";
		}else if (end.equals("xls")){
			type = "application/msexcel";
		}else if (end.equals("xlsx")){
			type = "application/msexcel";
		}else if (end.equals("ppt")){
//			type = "application/msppt";
			type = "application/vnd.ms-powerpoint";
		}else if (end.equals("pptx")){
			type = "application/vnd.ms-powerpoint";
		}else if (end.equals("pdf")){
			type = "application/pdf";
		}else if (end.equals("rar")){
			type = "application/rar";
		}else if (end.equals("zip")){
			type = "application/zip";
		}
		//判断是否链接
		else if (end.equals("html")
				||end.equals("htm")
				||end.equals("shtml")
				||end.equals("asp")
				||end.equals("aspx")
				||end.equals("jsp")
				||end.equals("php")
				||end.equals("perl")
				||end.equals("cgi")
				||end.equals("xml")
				||end.equals("com")
				||end.equals("cn")
				||end.equals("mobi")
				||end.equals("tel")
				||end.equals("asia")
				||end.equals("net")
				||end.equals("org")
				||end.equals("name")
				||end.equals("me")
				||end.equals("info")
				||end.equals("cc")
				||end.equals("hk")
				||end.equals("biz")
				||end.equals("tv")
				||end.equals("公司")
				||end.equals("网络")
				||end.equals("中国")
				){
			type = "link";
		}
		/* 如果无法直接打开，就跳出软件列表给用户选择 */
		else {
			type = "/*";
		}
		
		return type;
	}
}
