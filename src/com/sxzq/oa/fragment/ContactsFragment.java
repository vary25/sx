package com.sxzq.oa.fragment;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sxzq.oa.R;
import com.sxzq.oa.Session;
import com.sxzq.oa.activity.ChatActivity;
import com.sxzq.oa.activity.MainActivity;
import com.sxzq.oa.adapter.RosterAdapter;
import com.sxzq.oa.servcie.XXService;
import com.sxzq.oa.ui.iphonetreeview.IphoneTreeView;
import com.sxzq.oa.ui.pulltorefresh.PullToRefreshBase;
import com.sxzq.oa.ui.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.sxzq.oa.ui.pulltorefresh.PullToRefreshScrollView;
import com.sxzq.oa.util.L;
import com.sxzq.oa.util.PreferenceConstants;
import com.sxzq.oa.util.PreferenceUtils;
import com.sxzq.oa.util.T;
import com.way.ui.quickaction.ActionItem;
import com.way.ui.quickaction.QuickAction;
import com.way.ui.quickaction.QuickAction.OnActionItemClickListener;
import com.way.ui.view.AddRosterItemDialog;
import com.way.ui.view.GroupNameView;

public class ContactsFragment extends Fragment
{
	public static HashMap<String, Integer> mStatusMap;
	static {
		mStatusMap = new HashMap<String, Integer>();
		mStatusMap.put(PreferenceConstants.OFFLINE, -1);
		mStatusMap.put(PreferenceConstants.DND, R.drawable.status_shield);
		mStatusMap.put(PreferenceConstants.XA, R.drawable.status_invisible);
		mStatusMap.put(PreferenceConstants.AWAY, R.drawable.status_leave);
		mStatusMap.put(PreferenceConstants.AVAILABLE, R.drawable.status_online);
		mStatusMap.put(PreferenceConstants.CHAT, R.drawable.status_qme);
	}
	private XXService mXxService;
	private PullToRefreshScrollView mPullRefreshScrollView;
	private IphoneTreeView mIphoneTreeView;
	private RosterAdapter mRosterAdapter;
	private int mLongPressGroupId, mLongPressChildId;
	
	private MainActivity mainActivity;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		mainActivity = (MainActivity) getActivity();
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater , ViewGroup container , Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.contacts_fragment, null) ;
		initViews();
		registerListAdapter();
		return v;
	}
	
	private void registerListAdapter() {
		mRosterAdapter = new RosterAdapter(getActivity(), mIphoneTreeView,
				mPullRefreshScrollView);
		mIphoneTreeView.setAdapter(mRosterAdapter);
		mRosterAdapter.requery();
	}
	
	private void initViews() {
		mPullRefreshScrollView = (PullToRefreshScrollView) getActivity().findViewById(R.id.pull_refresh_scrollview);
		// mPullRefreshScrollView.setMode(Mode.DISABLED);
		// mPullRefreshScrollView.getLoadingLayoutProxy().setLastUpdatedLabel(
		// "最近更新：刚刚");
		mPullRefreshScrollView
				.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						new GetDataTask().execute();
					}
				});
		mIphoneTreeView = (IphoneTreeView) getActivity().findViewById(R.id.iphone_tree_view);
		mIphoneTreeView.setHeaderView(mainActivity.getLayoutInflater().inflate(
				R.layout.contact_buddy_list_group, mIphoneTreeView, false));
		mIphoneTreeView.setEmptyView(getActivity().findViewById(R.id.empty));
		mIphoneTreeView
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						int groupPos = (Integer) view.getTag(R.id.xxx01); // 参数值是在setTag时使用的对应资源id号
						int childPos = (Integer) view.getTag(R.id.xxx02);
						mLongPressGroupId = groupPos;
						mLongPressChildId = childPos;
						if (childPos == -1) {// 长按的是父项
							// 根据groupPos判断你长按的是哪个父项，做相应处理（弹框等）
							showGroupQuickActionBar(view
									.findViewById(R.id.group_name));
							// T.showShort(MainActivity.this,
							// "LongPress group position = " + groupPos);
						} else {
							// 根据groupPos及childPos判断你长按的是哪个父项下的哪个子项，然后做相应处理。
							// T.showShort(MainActivity.this,
							// "onClick child position = " + groupPos
							// + ":" + childPos);
							showChildQuickActionBar(view
									.findViewById(R.id.icon));

						}
						return false;
					}
				});
		mIphoneTreeView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				String userJid = mRosterAdapter.getChild(groupPosition,
						childPosition).getJid();
				String userName = mRosterAdapter.getChild(groupPosition,
						childPosition).getAlias();
				startChatActivity(userJid, userName);
				return false;
			}
		});
		
	}
	
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// if (mPullRefreshScrollView.getState() != State.REFRESHING)
			// mPullRefreshScrollView.setState(State.REFRESHING, true);
		}

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			if (!Session.getInstance().isConnected()) {// 如果没有连接重新连接
				String usr = PreferenceUtils.getPrefString(mainActivity,
						PreferenceConstants.ACCOUNT, "");
				String password = PreferenceUtils.getPrefString(
						mainActivity, PreferenceConstants.PASSWORD, "");
				mXxService.Login(usr, password);
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			// Do some stuff here
			// Call onRefreshComplete when the list has been refreshed.
			mRosterAdapter.requery();// 重新查询一下数据库
			mPullRefreshScrollView.onRefreshComplete();
			// mPullRefreshScrollView.getLoadingLayoutProxy().setLastUpdatedLabel(
			// "最近更新：刚刚");
			T.showShort(mainActivity, "刷新成功!");
			super.onPostExecute(result);
		}
	}
	
	private void startChatActivity(String userJid, String userName) {
		Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
		Uri userNameUri = Uri.parse(userJid);
		chatIntent.setData(userNameUri);
		chatIntent.putExtra(ChatActivity.INTENT_EXTRA_USERNAME, userName);
		startActivity(chatIntent);
	}
	
	private void showGroupQuickActionBar(View view) {
		QuickAction quickAction = new QuickAction(mainActivity, QuickAction.HORIZONTAL);
		quickAction
				.addActionItem(new ActionItem(0, getString(R.string.rename)));
		quickAction.addActionItem(new ActionItem(1,
				getString(R.string.add_friend)));
		quickAction
				.setOnActionItemClickListener(new OnActionItemClickListener() {

					@Override
					public void onItemClick(QuickAction source, int pos,
							int actionId) {
						// 如果没有连接直接返回
						if (!Session.getInstance().isConnected()) {
							T.showShort(getActivity(),
									R.string.conversation_net_error_label);
							return;
						}
						switch (actionId) {
						case 0:
							String groupName = mRosterAdapter.getGroup(
									mLongPressGroupId).getGroupName();
							if (TextUtils.isEmpty(groupName)) {// 系统默认分组不允许重命名
								T.showShort(getActivity(),
										R.string.roster_group_rename_failed);
								return;
							}
							renameRosterGroupDialog(mRosterAdapter.getGroup(
									mLongPressGroupId).getGroupName());
							break;
						case 1:

							new AddRosterItemDialog((MainActivity)getActivity(),
									mXxService).show();// 添加联系人
							break;
						default:
							break;
						}
					}
				});
		quickAction.show(view);
		quickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
	}

	private void showChildQuickActionBar(View view) {
		QuickAction quickAction = new QuickAction(getActivity(), QuickAction.HORIZONTAL);
		quickAction.addActionItem(new ActionItem(0, getString(R.string.add)));
		quickAction
				.addActionItem(new ActionItem(1, getString(R.string.rename)));
		quickAction.addActionItem(new ActionItem(2, getString(R.string.move)));
		quickAction
				.addActionItem(new ActionItem(3, getString(R.string.delete)));
		quickAction
				.setOnActionItemClickListener(new OnActionItemClickListener() {

					@Override
					public void onItemClick(QuickAction source, int pos,
							int actionId) {
						String userJid = mRosterAdapter.getChild(
								mLongPressGroupId, mLongPressChildId).getJid();
						String userName = mRosterAdapter.getChild(
								mLongPressGroupId, mLongPressChildId)
								.getAlias();
						if (!Session.getInstance().isConnected()) {
							T.showShort(getActivity(),
									R.string.conversation_net_error_label);
							return;
						}
						switch (actionId) {
						case 0:
							if (mXxService != null)
								mXxService
										.requestAuthorizationForRosterItem(userJid);
							break;
						case 1:
							renameRosterItemDialog(userJid, userName);
							break;
						case 2:
							moveRosterItemToGroupDialog(userJid);
							break;
						case 3:
							removeRosterItemDialog(userJid, userName);
							break;

						default:
							break;
						}
					}
				});
		quickAction.show(view);
	}
	
	public abstract class EditOk {
		abstract public void ok(String result);
	}
	
	void removeRosterItemDialog(final String JID, final String userName) {
		new AlertDialog.Builder(getActivity())
				.setTitle(R.string.deleteRosterItem_title)
				.setMessage(
						getString(R.string.deleteRosterItem_text, userName, JID))
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								mXxService.removeRosterItem(JID);
							}
						}).setNegativeButton(android.R.string.no, null)
				.create().show();
	}

	
	void renameRosterItemDialog(final String JID, final String userName) {
		editTextDialog(R.string.RenameEntry_title,
				getString(R.string.RenameEntry_summ, userName, JID), userName,
				new EditOk() {
					public void ok(String result) {
						if (mXxService != null)
							mXxService.renameRosterItem(JID, result);
					}
				});
	}

	
	
	private void editTextDialog(int titleId, CharSequence message, String text,
			final EditOk ok) {
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.edittext_dialog, null);

		TextView messageView = (TextView) layout.findViewById(R.id.text);
		messageView.setText(message);
		final EditText input = (EditText) layout.findViewById(R.id.editText);
		input.setTransformationMethod(android.text.method.SingleLineTransformationMethod
				.getInstance());
		input.setText(text);
		new AlertDialog.Builder(getActivity())
				.setTitle(titleId)
				.setView(layout)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								String newName = input.getText().toString();
								if (newName.length() != 0)
									ok.ok(newName);
							}
						}).setNegativeButton(android.R.string.cancel, null)
				.create().show();
	}

	
	void renameRosterGroupDialog(final String groupName) {
		editTextDialog(R.string.RenameGroup_title,
				getString(R.string.RenameGroup_summ, groupName), groupName,
				new EditOk() {
					public void ok(String result) {
						if (mXxService != null)
							mXxService.renameRosterGroup(groupName, result);
					}
				});
	}

	void moveRosterItemToGroupDialog(final String jabberID) {
		LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View group = inflater
				.inflate(R.layout.moverosterentrytogroupview, null);
		final GroupNameView gv = (GroupNameView) group
				.findViewById(R.id.moverosterentrytogroupview_gv);
		gv.setGroupList(mainActivity.getRosterGroups());
		new AlertDialog.Builder(getActivity())
				.setTitle(R.string.MoveRosterEntryToGroupDialog_title)
				.setView(group)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								L.d("new group: " + gv.getGroupName());
								if (Session.getInstance().isConnected())
									mXxService.moveRosterItemToGroup(jabberID,
											gv.getGroupName());
							}
						}).setNegativeButton(android.R.string.cancel, null)
				.create().show();
	}
	
	public void refresh(){
		mRosterAdapter.requery();
	}

}