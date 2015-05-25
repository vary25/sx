package com.sxzq.oa.fragment;

import java.util.ArrayList;
import java.util.List;

import com.sxzq.oa.R;
import com.sxzq.oa.adapter.AppAdapter;
import com.sxzq.oa.bean.AppModel;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class DiscoverFragment extends Fragment
{
	@Override
	public View onCreateView(LayoutInflater inflater , ViewGroup container , Bundle savedInstanceState)
	{
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.discover_fragment,container,false) ;
		
		return v ;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		ListView lv = (ListView) view.findViewById(R.id.lvApp);
		List<AppModel> list = new ArrayList<AppModel>();
		AppModel app1 = new AppModel();
		app1.setAppName("签到");
		list.add(app1);
		
		AppAdapter adapter = new AppAdapter(getActivity(), list);
		
		lv.setAdapter(adapter);
		super.onViewCreated(view, savedInstanceState);
	}
	
	
}