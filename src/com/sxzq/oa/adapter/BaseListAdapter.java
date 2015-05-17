package com.sxzq.oa.adapter;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.sxzq.oa.Session;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
/**
 * 支持增删改查、排序、分页
 * @author Swei.Jiang 
 * @date 2013-8-26
 * @param <T>
 */
public abstract class BaseListAdapter<T> extends BaseAdapter implements OnScrollListener {

	protected String TAG = this.getClass().getSimpleName();
	protected List<T> mList;
	public List<T> getList() {
		return mList;
	}

	protected Context mContext;
	protected LayoutInflater mInflater;
	private boolean mNotifyOnChange = true;

	private final Object mLock = new Object();

	public BaseListAdapter(Context context, List<T> mList) {
		this.mList = mList;
		mContext = context;
		mInflater = LayoutInflater.from(context);
	}

	public BaseListAdapter(Context context) {
		mList = new ArrayList<T>();
		mContext = context;
		mInflater = LayoutInflater.from(context);
	}

	public void add(T object) {
		synchronized (mLock) {
			mList.add(object);
		}
		if (mNotifyOnChange)
			notifyDataSetChanged();
	}

	public void addAll(Collection<? extends T> collection) {
		synchronized (mLock) {
			mList.addAll(collection);
		}
		if (mNotifyOnChange)
			notifyDataSetChanged();
	}

	public void addAll(T... items) {
		synchronized (mLock) {
			Collections.addAll(mList, items);
		}
		if (mNotifyOnChange)
			notifyDataSetChanged();
	}

	public void insert(T object, int index) {
		synchronized (mLock) {
			mList.add(index, object);
		}
		if (mNotifyOnChange)
			notifyDataSetChanged();
	}

	public void remove(T object) {
		synchronized (mLock) {
			mList.remove(object);
		}
		if (mNotifyOnChange)
			notifyDataSetChanged();
	}

	public void clear() {
		synchronized (mLock) {
			mList.clear();
		}
		if (mNotifyOnChange)
			notifyDataSetChanged();
	}

	public void sort(Comparator<? super T> comparator) {
		synchronized (mLock) {
			Collections.sort(mList, comparator);
		}
		if (mNotifyOnChange)
			notifyDataSetChanged();
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		mNotifyOnChange = true;
	}

	public void setNotifyOnChange(boolean notifyOnChange) {
		mNotifyOnChange = notifyOnChange;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public T getItem(int position) {
		return mList.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView,
			ViewGroup parent){
		BaseViewHolder holder = null;
		if(convertView == null){
			convertView = mInflater.inflate(getLayoutResId(), null);
			holder = getViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (BaseViewHolder) convertView.getTag();
		}
		
		setData(holder,getItem(position));
		
		return convertView;
	};
	
	abstract protected int getLayoutResId();
	
	abstract protected void setData(BaseViewHolder holder,T t);
	
	abstract protected BaseViewHolder getViewHolder(View convertView);

	protected boolean scrolling = false;

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (onScrollLoadMoreListener == null) {
			return;
		}
		scrolling = false;
		Log.d("Scroll", "firstVisibleItem:" + firstVisibleItem
				+ "totalItemCount" + totalItemCount + "visibleItemCount"
				+ visibleItemCount);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (onScrollLoadMoreListener == null) {
			return;
		}
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			scrolling = true;
			if (mList.size() > 2
					&& view.getLastVisiblePosition() >= (mList.size() - 2)) {
				if (totalCount != -1 && view.getCount() > 2
						&& totalCount <= mList.size()) {
					// Toast.makeText(mInflater.getContext(),mInflater.getContext().getResources().getString(R.string.mzw_list_no_more),Toast.LENGTH_SHORT).show();
					return;
				}
				onScrollLoadMoreListener.loadMore(mList);
				if (onScrollLoadMoreListener.isLoading()) {
					// view.setSelection(view.getCount());
					view.invalidate();
				}
			}
		}
	}

	private int totalCount = -1;

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public OnScrollLoadMoreListener<T> onScrollLoadMoreListener;

	public void setOnScrollLoadMoreListener(
			OnScrollLoadMoreListener<T> onloadMoreListener) {
		this.onScrollLoadMoreListener = onloadMoreListener;
	}

	public static interface OnScrollLoadMoreListener<T> {

		void loadMore(List<T> mList);

		boolean isLoading();
	}
	
	public int getLayout(String name) {
		
		
		return Session.getInstance().getContext().getResources().getIdentifier(name,
				"layout", Session.getInstance().getContext().getPackageName());
	}
	public int getId(String name) {
		return Session.getInstance().getContext().getResources().getIdentifier(name,
				"id", Session.getInstance().getContext().getPackageName());
	}
	
	public int getDrawable(String name) {
		return Session.getInstance().getContext().getResources().getIdentifier(name,
				"drawable", Session.getInstance().getContext().getPackageName());
	}
	public int getString(String name) { 
		return Session.getInstance().getContext().getResources().getIdentifier(name,
				"string", Session.getInstance().getContext().getPackageName());
	}
	
	
}
