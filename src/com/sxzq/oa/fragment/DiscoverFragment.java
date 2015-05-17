package com.sxzq.oa.fragment;

import com.sxzq.oa.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DiscoverFragment extends Fragment
{
	@Override
	public View onCreateView(LayoutInflater inflater , ViewGroup container , Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.discover_fragment, null) ;
		return v ;
	}
}