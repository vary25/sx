package com.sxzq.oa.activity;

import com.sxzq.oa.Session;

import android.app.Activity;
import android.os.Bundle;

public class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Session.initSession(this);
		super.onCreate(savedInstanceState);
	}
}
