package com.sxzq.oa.adapter;

import java.util.Date;
import java.util.List;

import com.sxzq.oa.ImageDisplayer;
import com.sxzq.oa.R;
import com.sxzq.oa.bean.AppModel;

import framework.utils.DateUtils;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class AppAdapter extends BaseListAdapter<AppModel> {

	public AppAdapter(Context context, List<AppModel> mList) {
		super(context, mList);
		// TODO Auto-generated constructor stub
	}
	

	@Override
	protected int getLayoutResId() {
		return R.layout.app_listview_item;
	}

	@Override
	protected void setData(BaseViewHolder holder, AppModel t) {
		holder.tvTitle.setText(t.getAppName());
		ImageDisplayer.load(holder.ivIcon,t.getIconUrl(),R.drawable.ic_launcher);
		
	}

	@Override
	protected BaseViewHolder getViewHolder(View convertView) {
		BaseViewHolder holder = new BaseViewHolder();
		holder.tvTitle = (TextView) convertView
				.findViewById(R.id.tv_title);
		holder.ivIcon =  (ImageView) convertView
				.findViewById(R.id.icon);
		
		return holder;
	}
	
}
