package com.sxzq.oa.adapter;

import java.util.Date;
import java.util.List;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.sxzq.oa.ImageDisplayer;
import com.sxzq.oa.R;
import com.sxzq.oa.bean.Conversation;
import framework.utils.DateUtils;

public class ConversationAdapter extends BaseListAdapter<Conversation> {

	public ConversationAdapter(Context context, List<Conversation> mList) {
		super(context, mList);
	}

	@Override
	protected int getLayoutResId() {
		return R.layout.conversation_listview_item;
	}
	
	@Override
	protected BaseViewHolder getViewHolder(View convertView) {
		BaseViewHolder holder = new BaseViewHolder();
		holder.tvTitle = (TextView) convertView
				.findViewById(R.id.recent_list_item_name);
		holder.tvTime = (TextView) convertView
				.findViewById(R.id.recent_list_item_time);
		holder.tvContent = (TextView) convertView
				.findViewById(R.id.recent_list_item_msg);
		holder.tvMark = (TextView) convertView.findViewById(R.id.unreadmsg);
		holder.ivBtn =  convertView
				.findViewById(R.id.recent_del_btn);
		holder.ivIcon =  (ImageView) convertView
				.findViewById(R.id.icon);
		
		return holder;
	}

	@Override
	protected void setData(BaseViewHolder holder, Conversation t) {
		holder.tvTitle.setText(t.getcName());
		holder.tvContent.setText(t.getTitle());
		holder.tvTime.setText(DateUtils.getFormatTime(new Date(t.getCreateTime()), "MM:dd hh:mm"));

		if (t.getUnreadCount()>0) {
			holder.tvMark.setVisibility(View.VISIBLE);
		}else{
			holder.tvMark.setVisibility(View.GONE);
		}
		
		ImageDisplayer.load(holder.ivIcon,t.getIcon(),R.drawable.ic_launcher);
		
		holder.tvMark.bringToFront();
		holder.ivBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});
		
	}
	
	void removeChatHistoryDialog(final String id, final String userName) {
		
	}

}
