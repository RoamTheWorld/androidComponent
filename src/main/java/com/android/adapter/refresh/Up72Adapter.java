package com.android.adapter.refresh;

import java.lang.reflect.Constructor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.android.adapter.refresh.ListDataHolder.LoadMoreMode;
import com.android.adapter.refresh.LoadMoreData.OnDataChangeListener;
import com.android.adapter.refresh.LoadMoreData.OnLoadDataFinishListener;
import com.android.exception.Up72Exception;
import com.android.master.R;
import com.android.ui.widget.LoadingDialog;
import com.android.ui.widget.Toast;
import com.android.utils.Constants;

/**
 * 自动加载的Adapter
 * 
 * @author wangyang 2014-7-29 下午2:19:49
 * @param <T>
 */
public class Up72Adapter<T> extends BaseAdapter implements OnDataChangeListener, OnLoadDataFinishListener, OnScrollListener {

	/**
	 * 上下文
	 */
	protected Context mContext;
	/**
	 * 数据池
	 */
	protected ListDataHolder<T> mHolder;
	/**
	 * BaseAdapterView的构造函数
	 */
	protected Constructor<?> mConstructor;
	/**
	 * Inflater
	 */
	private LayoutInflater mInflater;
	/**
	 * 当前Listview
	 */
	private ListView listView;
	/**
	 * 当前position
	 */
	private int curPosition;

	private boolean isLoadImage = true;
	private boolean isShowHeaderOrHeader = false;

	public final int DEFAULT = 0;
	public final int LOADING = 1;
	public final int ERROR = 2;
	public final int NOMORE = 3;

	/**
	 * 当前状态
	 */
	private int state = LOADING;

	/**
	 * @param mHolder
	 *            数据池
	 * @param context
	 *            上下文
	 * @param view
	 *            BaseAdapterView的class
	 * @param ListView
	 */
	public Up72Adapter(ListDataHolder<T> mHolder, Context context, Class<?> view, ListView listView, boolean isShowHeaderOrHeader) {
		this.mContext = context;
		this.mHolder = mHolder;
		if(mHolder.getMode() == LoadMoreMode.Header && listView==null)
			throw new Up72Exception("listview can not be null");
		
		this.listView = listView;
		this.isShowHeaderOrHeader = isShowHeaderOrHeader;

		// 设置监听
		mHolder.moreData.setDataChangeListener(this);
		mHolder.addOnLoadDataFinishListener(this);
		mHolder.setContext(mContext);

		if (this.listView != null)
			this.listView.setOnScrollListener(this);

		// 初始化Inflater
		mInflater = LayoutInflater.from(mContext);

		generateConstrutor(view);
	}

	public void generateConstrutor(Class<?> view) {
		// 初始化构造函数
		try {
			mConstructor = view.getDeclaredConstructor(Context.class);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public Up72Adapter(ListDataHolder<T> mHolder, Context context, Class<?> view, ListView listView) {
		this(mHolder, context, view, listView,false);
	}
	
	public Up72Adapter(ListDataHolder<T> mHolder, Context context, Class<?> view) {
		this(mHolder, context, view, null,false);
	}
	
	public Up72Adapter(ListDataHolder<T> mHolder, Context context, Class<?> view, boolean isShowHeaderOrHeader) {
		this(mHolder, context, view, null,isShowHeaderOrHeader);
	}

	@Override
	public int getCount() {
		if (mHolder.getDatasCount() > 0 && isShowHeaderOrHeader) {
			return mHolder.getDatasCount() + 1;
		}
		return mHolder.getDatasCount();
	}

	@Override
	public Object getItem(int position) {
		if (mHolder.getMode() == LoadMoreMode.Header && isShowHeaderOrHeader)
			position = position - 1;
		return position >= mHolder.getDatasCount() || position < 0 ? null : mHolder.getDatas().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		curPosition = position;
		// 如果是header或者footer,返回对应的view
		if(isShowHeaderOrHeader){
			if (position == getCount() - 1 && mHolder.getMode() == LoadMoreMode.Footer && position != 0)
				return generateHeaderOrFooterView(convertView);
			if (position == 0 && mHolder.getMode() == LoadMoreMode.Header)
				return generateHeaderOrFooterView(convertView);
		}
		// 初始化BaseAdapterView
		BaseAdapterView<T> view = null;
		if (convertView != null && convertView.getTag() instanceof BaseAdapterView) {
			view = (BaseAdapterView<T>) convertView.getTag();
		} else {
			view = generateBaseAdapterView(view);
		}
		// 获取当前position的泛型对象
		if (mHolder.getMode() == LoadMoreMode.Header && isShowHeaderOrHeader)
			curPosition = curPosition - 1;
		T t = mHolder.obtainItem(curPosition);

		// 请求前记录firstVisiblePosition
		if (mHolder.getMode() == LoadMoreMode.Header && position <= Constants.AUTO_LOAD_MORE_AT_POSITION )
			curPosition = listView.getFirstVisiblePosition() - 1;
		// 生成控件,绑定数据
		view.setHolder(mHolder);
		view.bindData(t, position, isLoadImage);

		// 设置convertView及tag
		convertView = view.getView();
		convertView.setTag(view);
		return convertView;
	}

	@SuppressWarnings("unchecked")
	public BaseAdapterView<T> generateBaseAdapterView(BaseAdapterView<T> view) {
		try {
			view = (BaseAdapterView<T>) mConstructor.newInstance(mContext);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return view;
	}

	private View generateHeaderOrFooterView(View convertView) {
		switch (state) {
		case LOADING:
			return getHeaderOrFooterView(convertView, R.layout.layout_item_loading);
		case ERROR:
			return getHeaderOrFooterView(convertView, R.layout.layout_item_loading_error);
		case NOMORE:
			return getHeaderOrFooterView(convertView, R.layout.layout_item_loading_finish);
		}
		return convertView;
	}

	/**
	 * 生成LoadingView
	 * 
	 * @author wangyang 2014-7-29 下午2:34:49
	 * @param convertView
	 * @return
	 */
	private View getHeaderOrFooterView(View convertView, int resId) {
		if (convertView != null) {
			convertView.destroyDrawingCache();
		}
		View view = mInflater.inflate(resId, null);
		return view;
	}

	/**
	 * 数据有变化后的回调方法,重新notifyDataSetChanged
	 * 
	 * @author wangyang 2014-7-29 下午2:36:09
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.up72.adapter.refresh.ListDataHolder.OnDataChangeListener#onDataChange
	 * (int)
	 */
	@Override
	public void onDataChange() {
		notifyDataSetChanged();
	}

	@Override
	public void notifyDataSetChanged() {
		if (mHolder.getLastResultSize() < mHolder.getPageSize() || mHolder.getLastResultSize() <= 0) {
			state = NOMORE;
		}
		if (state != ERROR) {
			super.notifyDataSetChanged();
			// 如果为上拉刷新
			if (mHolder.getMode() == LoadMoreMode.Header && mHolder.getDatasCount() > 0) {
				// 设置到上次显示的位置
				curPosition += mHolder.getLastResultSize();
				listView.setSelection(curPosition + 1);
			}
		}
	}

	/**
	 * 错误回调
	 * 
	 * @author wangyang 2014-7-29 下午2:36:45
	 * @param error
	 */
	@Override
	public void onError(String error) {
		Toast.show(mContext, error);
		state = ERROR;
		notifyDataSetChanged();
	}

	@Override
	public <E> void OnLoadDataFinish(ListDataHolder<E> dataHolder) {
		LoadingDialog.dismissLoadingDialog();
	}

	@Override
	public void setDefaultState() {
		this.state = LOADING;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE) {
			isLoadImage = true;
			super.notifyDataSetChanged();
		} else
			isLoadImage = false;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

	}
}
