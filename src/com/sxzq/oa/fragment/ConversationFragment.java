package com.sxzq.oa.fragment;


import com.sxzq.oa.R;
import com.sxzq.oa.util.refreshlistview.PullToRefreshListView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ConversationFragment extends Fragment
{
	private PullToRefreshListView listView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		listView = (PullToRefreshListView) getActivity().findViewById(R.id.pullToRefreshListView1);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater , ViewGroup container , Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.conversation_fragment, container,false) ;
		return v ;
	}
}