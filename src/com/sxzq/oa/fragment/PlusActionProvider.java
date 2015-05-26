package com.sxzq.oa.fragment;

import com.sxzq.oa.R;
import com.sxzq.oa.Session;
import com.sxzq.oa.activity.MainActivity;
import com.sxzq.oa.servcie.XXService;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ActionProvider;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
import android.widget.Toast;


@SuppressLint("NewApi")
public class PlusActionProvider extends ActionProvider
{
	private Context context ;

	public PlusActionProvider(Context context) {
		super(context);
		this.context = context ;
	}

	@Override
	public View onCreateActionView() {
		return null;
	}
	
	@Override
	public void onPrepareSubMenu(SubMenu submenu)
	{
		submenu.clear();
		submenu.add(R.string.action_menu_groupchat).setIcon(R.drawable.action_group_chat).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				Toast.makeText(context, R.string.action_menu_groupchat, 5000).show();
				return true;
			}
		});
		
		submenu.add(R.string.action_menu_addfriend).setIcon(R.drawable.action_add_contacts).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				// 添加联系人
				XXService xxService = Session.getInstance().getService();
				if (xxService == null || !xxService.isAuthenticated()) {
					
				}else{
					new com.sxzq.oa.ui.view.AddRosterItemDialog(context,xxService).show();
				}
				
//				Toast.makeText(context, R.string.action_menu_addfriend, 5000).show();
				return true;
			}
		});
		
		submenu.add(R.string.action_menu_qr).setIcon(R.drawable.action_scan_qr_code).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				Toast.makeText(context, R.string.action_menu_qr, 5000).show();
				return true;
			}
		});
		
		submenu.add(R.string.action_menu_advice).setIcon(R.drawable.action_feedback).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				Toast.makeText(context, R.string.action_menu_advice, 5000).show();
				return true;
			}
		});
		
	}
	
	@Override
	public boolean hasSubMenu()
	{
		return true;
	}
	
}