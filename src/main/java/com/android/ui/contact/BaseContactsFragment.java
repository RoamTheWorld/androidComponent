package com.android.ui.contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.master.R;
import com.android.ui.BaseFragment;
import com.android.ui.widget.SideBar;
import com.android.ui.widget.SideBar.OnTouchingLetterChangedListener;
import com.android.ui.widget.pulltorefresh.PullToRefreshBase.OnRefreshHeaderListener;
import com.android.ui.widget.pulltorefresh.PullToRefreshBase.PullModeEnum;
import com.android.ui.widget.pulltorefresh.PullToRefreshListView;
import com.android.utils.Constants;
import com.android.utils.StringUtil;

@SuppressLint("DefaultLocale")
public abstract class BaseContactsFragment<T extends BaseUser> extends BaseFragment implements OnItemClickListener, OnRefreshHeaderListener,
		OnTouchingLetterChangedListener {

	/**
	 * 智能下拉刷新ListView
	 */
	protected PullToRefreshListView refreshListView;
	protected ListView listView;
	protected ContactAdapter<T> adapter;
	protected List<T> allUsers, searchUsers;
	/**
	 * 是否显示搜索结果
	 */
	protected boolean isShowSearchResult;
	/**
	 * 右侧字母栏
	 */
	protected SideBar sideBar;
	/**
	 * 搜索文本框
	 */
	protected EditText etSearch;

	/**
	 * 添加联系人
	 */
	protected ImageButton btnComplete;
	/**联系人头部标题*/
	protected RelativeLayout layoutTitle;
	/**联系人头部搜索*/
	protected LinearLayout layoutSearch;

	@Override
	protected int getContentViewId() {
		return R.layout.contact_layout;
	}

	@Override
	protected void findViewById(View view) {
		layoutSearch = (LinearLayout) view.findViewById(R.id.search_layout);
		layoutTitle = (RelativeLayout) view.findViewById(R.id.layout_title);
		refreshListView = (PullToRefreshListView) view.findViewById(R.id.lv_contact);
		sideBar = (SideBar) view.findViewById(R.id.sb_contact);
		sideBar.setTextView((TextView) view.findViewById(R.id.sb_tv_hint));

		refreshListView.setCurrentMode(PullModeEnum.PULL_DOWN_TO_REFRESH);
		listView = refreshListView.getPullableView();
		listView.setSelector(R.color.translate);

	}

	@Override
	protected void setListeners() {
		sideBar.setOnTouchingLetterChangedListener(this);
		// 点击搜索框监听
		etSearch.addTextChangedListener(new SearchTextWatcher());
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// 判断当前显示的list,并获取点击Item对于的泛型
		T t;
		if (isShowSearchResult)
			t = searchUsers.get(position);
		else
			t = allUsers.get(position);

		// 跳转至详情页(需要引用项目在清单文件里对应Activity加上此action)
		Intent intent = new Intent(Constants.Action.CONTACT_DETAIL);
		intent.putExtra(Constants.CURRENT_USER_, t);
		startActivity(intent);
	}

	@Override
	public void onTouchingLetterChanged(String s) {
		// 获取点击字符串所对应的list位置,并设置
		if (adapter != null) {
			int position = adapter.getFirstPosition(s);
			if (position != -1)
				listView.setSelection(position);
		}
	}

	private class SearchTextWatcher implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// 如果联系人为空,则跳过
			if (allUsers == null || allUsers.size() == 0)
				return;

			// 获取当前输入文字,并作非空判断
			String content = etSearch.getText().toString();
			String spell = content.toUpperCase();

			// 为空,显示全部
			if (StringUtil.isEmpty(content)) {
				isShowSearchResult = false;
				adapter.setList(allUsers);
				adapter.notifyDataSetChanged();
			} else {
				// 否则,显示搜索结果
				if (searchUsers == null)
					searchUsers = new ArrayList<T>();
				searchUsers.clear();
				for (T t : allUsers) {
					if (t.getNickName().contains(content) || t.getSpellName().contains(spell))
						searchUsers.add(t);
				}
				isShowSearchResult = true;
				adapter.setList(searchUsers);
				adapter.notifyDataSetChanged();
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	}
	
	protected void notifySideBar(List<? extends BaseUser> users){
		List<String> list = new ArrayList<String>();
		for (BaseUser t : users) {
			String firstSpell = t.getFirstSpell();
			if(!list.contains(firstSpell.toUpperCase()))
				list.add(t.getFirstSpell().toUpperCase());
		}
		Collections.sort(list);
		sideBar.setList(list);
	}
	
	/**
	   * 设置收索的布局
	   * 
	   * @param layoutId 布局文件的Id
	   * @param editTextId 班编辑框的Id
	 */
	public void setSearchLayout(int layoutId, int editTextId) {
		if (layoutId < 0)
			layoutId = R.layout.layout_contact_search;
		View headSearch = LayoutInflater.from(getActivity()).inflate(layoutId, null);
		if (editTextId < 0)
			editTextId = R.id.et_contact_search;

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		headSearch.setLayoutParams(params);
		layoutSearch.addView(headSearch);
		etSearch = (EditText) headSearch.findViewById(editTextId);
	}
}
