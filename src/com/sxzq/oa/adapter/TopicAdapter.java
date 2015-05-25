package com.sxzq.oa.adapter;

import java.util.List;
import com.sxzq.oa.ImageDisplayer;
import com.sxzq.oa.R;
import com.sxzq.oa.bean.TopicModel;
import com.sxzq.oa.util.CommenUtil;
import com.sxzq.oa.util.TimeUtil;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class TopicAdapter extends BaseListAdapter<TopicModel> {

	public TopicAdapter(Context context, List<TopicModel> mList) {
		super(context, mList);
	}
	

	@Override
	protected int getLayoutResId() {
		return R.layout.topic_listview_item;
	}

	@Override
	protected void setData(BaseViewHolder holder, TopicModel t) {
		TopicViewHodler tHolder = (TopicViewHodler)holder;
		holder.tvTime.setText(TimeUtil.getTime(t.getCreatTime()));
		ImageDisplayer.load(holder.ivIcon,t.getIconUrl(),R.drawable.ic_launcher);
		holder.ivIcon.setOnClickListener(new OnTopicClick(t.getLinkUrl()));
		if (TextUtils.isEmpty(t.getT1())) {
			tHolder.tvTopic1.setVisibility(View.GONE);
		}else{
			tHolder.tvTopic1.setVisibility(View.VISIBLE);
			tHolder.tvTopic1.setOnClickListener(new OnTopicClick(t.getLink1()));
		}
		
		if (TextUtils.isEmpty(t.getT2())) {
			tHolder.tvTopic2.setVisibility(View.GONE);
		}else{
			tHolder.tvTopic2.setVisibility(View.VISIBLE);
			tHolder.tvTopic2.setOnClickListener(new OnTopicClick(t.getLink2()));
		}
		
		if (TextUtils.isEmpty(t.getT3())) {
			tHolder.tvTopic3.setVisibility(View.GONE);
		}else{
			tHolder.tvTopic3.setVisibility(View.VISIBLE);
			tHolder.tvTopic3.setOnClickListener(new OnTopicClick(t.getLink3()));
		}
	}

	class OnTopicClick implements OnClickListener{

		String url;
		
		public OnTopicClick(String url) {
			super();
			this.url = url;
		}



		@Override
		public void onClick(View v) {
			CommenUtil.openLink(mContext, url);
			
		}
		
	}
	
	@Override
	protected BaseViewHolder getViewHolder(View convertView) {
		TopicViewHodler holder = new TopicViewHodler();
		holder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);

		holder.ivIcon =  (ImageView) convertView
				.findViewById(R.id.iv_icon);
		
		holder.tvTopic1 = (TextView) convertView
				.findViewById(R.id.tvTopic1);
		holder.tvTopic2 = (TextView) convertView
				.findViewById(R.id.tvTopic2);
		holder.tvTopic3 = (TextView) convertView
				.findViewById(R.id.tvTopic3);
		
		return holder;
	}
	
	public static class TopicViewHodler extends BaseViewHolder{
		public TextView tvTopic1;
		public TextView tvTopic2;
		public TextView tvTopic3;
	}
}
