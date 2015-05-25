package com.sxzq.oa.ui.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.view.MotionEvent;

/**
 * 重写的webview
 * @author lvxuejun
 *
 */
public class TTWebView extends WebView{

	Context context;
	
	public TTWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		WebSettings webSettings = this.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(true);
		webSettings.setBuiltInZoomControls(false);
		webSettings.setPluginsEnabled(true);
		
		//this.setInitialScale(100);
		this.setVerticalScrollBarEnabled(true); 
		this.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
		this.context = context;
	}
	Handler handler = null;
	int msgdoubleClick;
	long lastTouchTime = 0;

	float lastUpX = 0;
	float lastUpY = 0;
	
	float lastDownX = 0;
	float lastDownY = 0;
	final int DIF = 12;
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		
		
		
		if(MotionEvent.ACTION_UP == ev.getAction()){  
			float difX = Math.abs(ev.getX() - lastUpX);
			float difY = Math.abs(ev.getY() - lastUpY);
			float difDownUPX =  Math.abs(ev.getX() - lastDownX);
			float difDownUPY = Math.abs(ev.getY() - lastDownY);
			if (System.currentTimeMillis()< lastTouchTime + 400 && difX <DIF && difY<DIF &&
					difDownUPX<DIF && difDownUPY<DIF) {
				lastTouchTime = 0;
				onDoubleClick();

			}else{
				lastTouchTime = System.currentTimeMillis();
				
			}
			lastUpX = ev.getX();
			lastUpY = ev.getY();
		}else if (MotionEvent.ACTION_DOWN == ev.getAction()) {
			lastDownX = ev.getX();
			lastDownY = ev.getY();
		}
		
		return super.onTouchEvent(ev);
	}
	

	public void setDoubleClick(Handler handler,int what){
		this.handler = handler;
		msgdoubleClick = what;
	}
	
	private void onDoubleClick(){
		if (this.handler!=null) {
			handler.sendEmptyMessage(msgdoubleClick);
		}
	}	
}
