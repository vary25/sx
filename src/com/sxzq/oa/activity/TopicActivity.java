package com.sxzq.oa.activity;

import java.util.ArrayList;
import java.util.List;

import com.sxzq.oa.R;
import com.sxzq.oa.adapter.TopicAdapter;
import com.sxzq.oa.bean.TopicModel;
import com.sxzq.oa.util.refreshlistview.PullToRefreshListView;

import android.os.Bundle;

public class TopicActivity extends BaseActivity {
	
	private PullToRefreshListView listView;
	private  TopicAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void initView() {
		setContentView(R.layout.topic_activity);
		listView = (PullToRefreshListView) findViewById(R.id.list);
		super.initView();
	}
	
	@Override
	protected void initData() {
		List<TopicModel> list = new ArrayList<TopicModel>()	;
		adapter = new TopicAdapter(this, list);
		
		listView.getAdapterView().setAdapter(adapter);
		super.initData();
	}
}
