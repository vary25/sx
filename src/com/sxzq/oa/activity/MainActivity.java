package com.sxzq.oa.activity;

import java.lang.reflect.Method;
import java.util.ArrayList;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.sxzq.oa.R;
import com.sxzq.oa.fragment.ConversationFragment;
import com.sxzq.oa.fragment.ContactsFragment;
import com.sxzq.oa.fragment.DiscoverFragment;
import com.sxzq.oa.fragment.MeFragment;


public class MainActivity extends BaseFragmentActivity1 implements OnCheckedChangeListener {
	//ViewPager�ؼ�
		private ViewPager main_viewPager ;
		//RadioGroup�ؼ�
		private RadioGroup main_tab_RadioGroup ;
		//RadioButton�ؼ�
		private RadioButton radio_chats , radio_contacts , radio_discover , radio_me ;
		//����ΪFragment�Ķ�̬����
		private ArrayList<Fragment> fragmentList ;
		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main);
			//�����ʼ����������ȡ����ĸ��ؼ���Ӧ��ID
			InitView();
			//ViewPager��ʼ������
			InitViewPager();

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
		}
		
		protected void setButtonHeight(RadioButton rb,int icWidth, int icHeight){
			Drawable[] drawables = rb.getCompoundDrawables();
			drawables[1].setBounds(new Rect(0, 0, icWidth, icHeight));
			rb.setCompoundDrawables(drawables[0],drawables[1],drawables[2],drawables[3]);
		
		}
		
		public void InitViewPager()
		{
			main_viewPager = (ViewPager) findViewById(R.id.main_ViewPager);
			
			fragmentList = new ArrayList<Fragment>() ;
			
			Fragment chatsFragment = new ConversationFragment() ;
			Fragment contactsFragment = new ContactsFragment();
			Fragment discoverFragment = new DiscoverFragment();
			Fragment meFragment = new MeFragment();
			
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
}
