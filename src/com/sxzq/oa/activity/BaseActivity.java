package com.sxzq.oa.activity;

import com.sxzq.oa.Session;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class BaseActivity extends Activity {

	private TextView tvTitle;// 标题栏
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Session.initSession(this);
		initView();
		initData();
		super.onCreate(savedInstanceState);
	}
	
	protected void initView(){
		
	}
	
	protected void initData(){
		
	}
	
	protected void setTitle(String title){
		tvTitle = (TextView) findViewById(com.sxzq.oa.R.id.ivTitleName);
		if (tvTitle!=null) {
			tvTitle.setText(title);
		}
	}
	
}
