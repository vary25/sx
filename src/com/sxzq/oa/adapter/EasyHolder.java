package com.sxzq.oa.adapter;

import android.view.View;
import framework.annotation.ViewInjectable;

public abstract class EasyHolder<T> implements ViewInjectable{
	private View convertView;
	
	@Override
	public View findView(int id) {
		return convertView.findViewById(id);
	}

	@Override
	public View findView(String resId) {
		return null;
	}
	
	public void setConvertView(View convertView) {
		this.convertView = convertView;
	}

	/**
	 * View 加载数据
	 * 
	 * @param data
	 * @param position
	 */
	public abstract void loadData(T data, int position);

}
