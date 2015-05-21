package com.sxzq.oa.activity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.sxzq.oa.R;
import com.sxzq.oa.XXBroadcastReceiver;
import com.sxzq.oa.XXBroadcastReceiver.EventHandler;
import com.sxzq.oa.db.RosterProvider;
import com.sxzq.oa.db.RosterProvider.RosterConstants;
import com.sxzq.oa.fragment.ContactsFragment;
import com.sxzq.oa.fragment.ConversationFragment;
import com.sxzq.oa.fragment.DiscoverFragment;
import com.sxzq.oa.fragment.MeFragment;
import com.sxzq.oa.servcie.IConnectionStatusCallback;
import com.sxzq.oa.servcie.XXService;
import com.sxzq.oa.util.L;
import com.sxzq.oa.util.NetUtil;
import com.sxzq.oa.util.PreferenceConstants;
import com.sxzq.oa.util.PreferenceUtils;
import com.sxzq.oa.util.T;
import com.sxzq.oa.util.XMPPHelper;


public class MainActivity extends BaseFragmentActivity1 implements OnCheckedChangeListener, EventHandler,IConnectionStatusCallback{
	private static final String[] GROUPS_QUERY = new String[] {
		RosterConstants._ID, RosterConstants.GROUP, };
	private XXService mXxService;
	//ViewPager�ؼ�
	private ViewPager main_viewPager ;
	//RadioGroup�ؼ�
	private RadioGroup main_tab_RadioGroup ;
	//RadioButton�ؼ�
	private RadioButton radio_chats , radio_contacts , radio_discover , radio_me ;
	//����ΪFragment�Ķ�̬����
	private ArrayList<Fragment> fragmentList ;
	
	private TextView mTitleNameView;
	private ImageView mTitleStatusView;
	private ProgressBar mTitleProgressBar;
	private View mNetErrorView;
	
	Fragment chatsFragment ;
	ContactsFragment contactsFragment ;
	Fragment discoverFragment;
	Fragment meFragment;
	
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
	private Handler mainHandler = new Handler();
	private ContentObserver mRosterObserver = new RosterObserver();
	
	private class RosterObserver extends ContentObserver {
		public RosterObserver() {
			super(mainHandler);
		}

		public void onChange(boolean selfChange) {
			L.d(MainActivity.class, "RosterObserver.onChange: " + selfChange);
			mainHandler.postDelayed(new Runnable() {
				public void run() {
					contactsFragment.refresh();
				}
			}, 100);
		}
	}
	
	ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mXxService = ((XXService.XXBinder) service).getService();
			mXxService.registerConnectionStatusCallback(MainActivity.this);
			// 开始连接xmpp服务器
			if (!mXxService.isAuthenticated()) {
				String usr = PreferenceUtils.getPrefString(MainActivity.this,
						PreferenceConstants.ACCOUNT, "");
				String password = PreferenceUtils.getPrefString(
						MainActivity.this, PreferenceConstants.PASSWORD, "");
				mXxService.Login(usr, password);
				// mTitleNameView.setText(R.string.login_prompt_msg);
				// setStatusImage(false);
				// mTitleProgressBar.setVisibility(View.VISIBLE);
			} else {
				mTitleNameView.setText(XMPPHelper
						.splitJidAndServer(PreferenceUtils.getPrefString(
								MainActivity.this, PreferenceConstants.ACCOUNT,
								"")));
				setStatusImage(true);
				mTitleProgressBar.setVisibility(View.GONE);
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mXxService.unRegisterConnectionStatusCallback();
			mXxService = null;
		}

	};
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		startService(new Intent(this, XXService.class));

		setContentView(R.layout.activity_main);
		//�����ʼ����������ȡ����ĸ��ؼ���Ӧ��ID
		InitView();
		//ViewPager��ʼ������
		InitViewPager();

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		bindXMPPService();
		getContentResolver().registerContentObserver(
				RosterProvider.CONTENT_URI, true, mRosterObserver);
		setStatusImage(isConnected());
		// if (!isConnected())
		// mTitleNameView.setText(R.string.login_prompt_no);
//		mRosterAdapter.requery();
		XXBroadcastReceiver.mListeners.add(this);
		if (NetUtil.getNetworkState(this) == NetUtil.NETWORN_NONE)
			mNetErrorView.setVisibility(View.VISIBLE);
		else
			mNetErrorView.setVisibility(View.GONE);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getContentResolver().unregisterContentObserver(mRosterObserver);
		unbindXMPPService();
		XXBroadcastReceiver.mListeners.remove(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}
	
	private boolean isConnected() {
		return mXxService != null && mXxService.isAuthenticated();
	}
	
	private void unbindXMPPService() {
		try {
			unbindService(mServiceConnection);
			L.i(LoginActivity.class, "[SERVICE] Unbind");
		} catch (IllegalArgumentException e) {
			L.e(LoginActivity.class, "Service wasn't bound!");
		}
	}

	
	
	private void bindXMPPService() {
		L.i(LoginActivity.class, "[SERVICE] Unbind");
		bindService(new Intent(MainActivity.this, XXService.class),
				mServiceConnection, Context.BIND_AUTO_CREATE
						+ Context.BIND_DEBUG_UNBIND);
	}

	public void InitView()
	{
		main_tab_RadioGroup = (RadioGroup) findViewById(R.id.main_tab_RadioGroup) ;

		radio_chats = (RadioButton) findViewById(R.id.radio_chats) ;
		radio_contacts = (RadioButton) findViewById(R.id.radio_contacts) ;
		radio_discover = (RadioButton) findViewById(R.id.radio_discover) ;
		radio_me = (RadioButton) findViewById(R.id.radio_me) ;

		int barHeight = getResources().getDimensionPixelOffset(R.dimen.bottom_bar_height);
		int icHeight = barHeight *3/5;
		int icWidth = icHeight * 8 / 7;

		setButtonHeight(radio_chats, icWidth, icHeight);
		setButtonHeight(radio_contacts, icWidth, icHeight);
		setButtonHeight(radio_discover, icWidth, icHeight);
		setButtonHeight(radio_me, icWidth, icHeight);

		main_tab_RadioGroup.setOnCheckedChangeListener(this);
		
		mNetErrorView = findViewById(R.id.net_status_bar_top);
		mTitleNameView = (TextView) findViewById(R.id.ivTitleName);
		mTitleProgressBar = (ProgressBar) findViewById(R.id.ivTitleProgress);
		mTitleStatusView = (ImageView) findViewById(R.id.ivTitleStatus);
		mTitleNameView.setText(XMPPHelper.splitJidAndServer(PreferenceUtils
				.getPrefString(this, PreferenceConstants.ACCOUNT, "")));
//		mTitleNameView.setOnClickListener(this);
	}

	protected void setButtonHeight(RadioButton rb,int icWidth, int icHeight){
		Drawable[] drawables = rb.getCompoundDrawables();
		drawables[1].setBounds(new Rect(0, 0, icWidth, icHeight));
		rb.setCompoundDrawables(drawables[0],drawables[1],drawables[2],drawables[3]);

	}

	public void InitViewPager(){
		main_viewPager = (ViewPager) findViewById(R.id.main_ViewPager);

		fragmentList = new ArrayList<Fragment>() ;

		chatsFragment = new ConversationFragment() ;
		contactsFragment = new ContactsFragment();
		discoverFragment = new DiscoverFragment();
		meFragment = new MeFragment();

		//����Fragment����������
		fragmentList.add(chatsFragment);
		fragmentList.add(contactsFragment);
		fragmentList.add(discoverFragment);
		fragmentList.add(meFragment);

		//����ViewPager��������
		main_viewPager.setAdapter(new MyAdapter(getSupportFragmentManager() , fragmentList));
		//��ǰΪ��һ��ҳ��
		main_viewPager.setCurrentItem(0);
		//ViewPager��ҳ��ı������
		main_viewPager.setOnPageChangeListener(new MyListner());
	}

	public class MyAdapter extends FragmentPagerAdapter
	{
		ArrayList<Fragment> list ;
		public MyAdapter(FragmentManager fm , ArrayList<Fragment> list)
		{
			super(fm);
			this.list = list ;
		}
		@Override
		public Fragment getItem(int arg0) {
			return list.get(arg0);
		}
		@Override
		public int getCount() {
			return list.size();
		}
	}

	public class MyListner implements OnPageChangeListener
	{

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {
			//��ȡ��ǰҳ�����ڸı��ӦRadioButton��״̬
			int current = main_viewPager.getCurrentItem() ;
			switch(current)
			{
			case 0:
				main_tab_RadioGroup.check(R.id.radio_chats);
				break;
			case 1:
				main_tab_RadioGroup.check(R.id.radio_contacts);
				break;
			case 2:
				main_tab_RadioGroup.check(R.id.radio_discover);
				break;
			case 3:
				main_tab_RadioGroup.check(R.id.radio_me);
				break;
			}
		}

	}

	@Override
	public void onCheckedChanged(RadioGroup radioGroup, int CheckedId) 
	{
		//��ȡ��ǰ��ѡ�е�RadioButton��ID�����ڸı�ViewPager�ĵ�ǰҳ
		int current=0;
		switch(CheckedId)
		{
		case R.id.radio_chats:
			current = 0 ;
			break ;
		case R.id.radio_contacts:
			current = 1 ;
			break;
		case R.id.radio_discover:
			current = 2 ;
			break;
		case R.id.radio_me:
			current = 3 ;
			break ;
		}
		if(main_viewPager.getCurrentItem() != current)
		{
			main_viewPager.setCurrentItem(current);
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu) ;
		return true;
	}

	@Override
	public boolean onMenuOpened(int featureId , Menu menu)
	{
		if(featureId == Window.FEATURE_ACTION_BAR  && menu != null)
		{
			if(menu.getClass().getSimpleName().equals("MenuBuilder"))
			{
				try {
					Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return super.onMenuOpened(featureId, menu) ;
	}

	public List<String> getRosterGroups() {
		// we want all, online and offline
		List<String> list = new ArrayList<String>();
		Cursor cursor = getContentResolver().query(RosterProvider.GROUPS_URI,
				GROUPS_QUERY, null, null, RosterConstants.GROUP);
		int idx = cursor.getColumnIndex(RosterConstants.GROUP);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			list.add(cursor.getString(idx));
			cursor.moveToNext();
		}
		cursor.close();
		return list;
	}

	@Override
	public void connectionStatusChanged(int connectedState, String reason) {
		switch (connectedState) {
		case XXService.CONNECTED:
			mTitleNameView.setText(XMPPHelper.splitJidAndServer(PreferenceUtils
					.getPrefString(MainActivity.this,
							PreferenceConstants.ACCOUNT, "")));
			mTitleProgressBar.setVisibility(View.GONE);
			// mTitleStatusView.setVisibility(View.GONE);
			setStatusImage(true);
			break;
		case XXService.CONNECTING:
			mTitleNameView.setText(R.string.login_prompt_msg);
			mTitleProgressBar.setVisibility(View.VISIBLE);
			mTitleStatusView.setVisibility(View.GONE);
			break;
		case XXService.DISCONNECTED:
			mTitleNameView.setText(R.string.login_prompt_no);
			mTitleProgressBar.setVisibility(View.GONE);
			mTitleStatusView.setVisibility(View.GONE);
			T.showLong(this, reason);
			break;

		default:
			break;
		}
		
	}
	
	private void setStatusImage(boolean isConnected) {
		if (!isConnected) {
			mTitleStatusView.setVisibility(View.GONE);
			return;
		}
		String statusMode = PreferenceUtils.getPrefString(this,
				PreferenceConstants.STATUS_MODE, PreferenceConstants.AVAILABLE);
		int statusId = mStatusMap.get(statusMode);
		if (statusId == -1) {
			mTitleStatusView.setVisibility(View.GONE);
		} else {
			mTitleStatusView.setVisibility(View.VISIBLE);
			mTitleStatusView.setImageResource(statusId);
		}
	}

	@Override
	public void onNetChange() {
		if (NetUtil.getNetworkState(this) == NetUtil.NETWORN_NONE) {
			T.showShort(this, R.string.net_error_tip);
			mNetErrorView.setVisibility(View.VISIBLE);
		} else {
			mNetErrorView.setVisibility(View.GONE);
		}
		
	}
}
