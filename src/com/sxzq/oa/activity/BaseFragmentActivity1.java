package com.sxzq.oa.activity;

import com.sxzq.oa.Session;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import framework.annotation.ViewInjectable;
import framework.annotation.parser.AnnotationViewParser;


public abstract class BaseFragmentActivity1 extends FragmentActivity implements ViewInjectable {
	

	protected void startActivity(Class<?> cls){
		startActivity(new Intent(this, cls));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Session.initSession(this);
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public void setContentView(int contentView) {
		super.setContentView(contentView);
		new AnnotationViewParser().parse(this);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public View findView(int id) {
		return findViewById(id);
	}

	@Override
	public View findView(String resId) {
		return findViewById(getId(resId));
	}

	public int getLayout(String name) {
		return getApplicationContext().getResources().getIdentifier(name,
				"layout", getApplicationContext().getPackageName());
	}

	public int getId(String name) {
		return getApplicationContext().getResources().getIdentifier(name, "id",
				getApplicationContext().getPackageName());
	}

	public int getDrawable(String name) {
		return getApplicationContext().getResources().getIdentifier(name,
				"drawable", getApplicationContext().getPackageName());
	}

	public int getString(String name) {
		return getApplicationContext().getResources().getIdentifier(name,
				"string", getApplicationContext().getPackageName());
	}

	public int getDimen(String name) {
		return getApplicationContext().getResources().getIdentifier(name,
				"dimen", getApplicationContext().getPackageName());
	}

	public int getColor(String name) {
		return getApplicationContext().getResources().getIdentifier(name,
				"color", getApplicationContext().getPackageName());
	}

}
