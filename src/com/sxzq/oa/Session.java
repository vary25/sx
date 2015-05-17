package com.sxzq.oa;

import framework.storage.dir.DirectoryContextExample;
import framework.storage.dir.DirectoryManager;
import framework.utils.CommonUtil;
import framework.utils.UIUtil;
import android.content.Context;

public class Session {
	private Context mContext;
	

	private Session(Context context) {
		this.mContext = context;
	}

	private boolean inited = false;
	private static Session __instance = null;

	public static synchronized Session getInstance() {
		return __instance;
	}

	public synchronized static void initSession(Context context) {
		if (__instance == null) {
			__instance = new Session(context);
		}
		__instance.init();
	}

	private void init() {
		if (inited) {
			return;
		}
		//初始化图片加载资源
		ImageDisplayer.initImageDisplayer(mContext);
		UIUtil.initUIUtil(mContext);
		
		DirectoryManager.init(new DirectoryContextExample(CommonUtil.getRootPath(mContext)));
		DirectoryManager.getInstance().createAll();
		
		inited=true;
	}

	public Context getContext() {
		return mContext;
	}
}
