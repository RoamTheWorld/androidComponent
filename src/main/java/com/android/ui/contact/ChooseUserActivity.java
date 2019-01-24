package com.android.ui.contact;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.view.View;

import com.android.utils.Constants;

public class ChooseUserActivity<T extends BaseUser> extends BaseContactActivity<T> {
	protected T t;
	protected List<T> list;
	protected int selectMoed;
	public static final int SINGLE_CHOICE = 1;
	public static final int MULTI_CHOICE = 2;
	public static final int REQUEST_CODE_CHOICE_USER = 1;
	private boolean isUserCache;
	private Intent intent;
	private boolean isChoose;

	@Override
	public void startActivity(Class clazz, boolean isfinsh) {

	}

	@SuppressWarnings("unchecked")
	@Override
	protected void init() {
		selectMoed = getIntent().getIntExtra(Constants.SELECT_MODE, MULTI_CHOICE);
		isUserCache = getIntent().getBooleanExtra(Constants.IS_USE_CACHE, false);
		if (selectMoed == MULTI_CHOICE)
			list = (List<T>) getIntent().getSerializableExtra(Constants.CHOICE_USER);
		else
			t = (T) getIntent().getSerializableExtra(Constants.CHOICE_USER);

		if (isUserCache)
			allUsers = (List<T>) getIntent().getSerializableExtra(Constants.ALL_USER);
		intent = new Intent();
	}

	/**
	 * 执行完选择人员后,返回结果,结束当前页
	 * 
	 * @author wangyang 2014-11-13 上午9:48:33
	 * @param view
	 */
	public void doComplete(View view) {
		ChooseUserAdapter<T> chooseUserAdapter = (ChooseUserAdapter<T>) adapter;
		if (chooseUserAdapter.getSelectUser() == null || chooseUserAdapter.getSelectUser().size() <= 0) {
			finish();
			return;
		}

		isChoose = true;
		if (selectMoed == SINGLE_CHOICE) {
			t = chooseUserAdapter.getSelectUser().get(0);
			intent.putExtra(Constants.CHOICE_USER, t);
		} else {
			list = chooseUserAdapter.getSelectUser();
			intent.putExtra(Constants.CHOICE_USER, (ArrayList<T>) list);
		}
		finish();
	}

	@Override
	public void finish() {
		if (isUserCache)
			intent.putExtra(Constants.ALL_USER, (ArrayList<T>) allUsers);
		if (isChoose)
			setResult(RESULT_OK, intent);
		else
			setResult(RESULT_CANCELED, intent);
		super.finish();
	}

	protected void setAllUsers(List<T> userlist) {
		allUsers = userlist;
		if (list != null) {
			for (T t : list) {
				replaceUser(t);
			}
		}
		if (t != null && allUsers.contains(t)) {
			replaceUser(t);
		}
	}

	private void replaceUser(T t) {
		int index = -1;
		for (int i = 0; i < allUsers.size(); i++) {
			if(allUsers.get(i).getUid().equals(t.getUid()) && (allUsers.get(i).getNickName().equals(t.getNickName())|| allUsers.get(i).getName().equals(t.getName()))){
				index = i;
				break;
			}
		}
		if (index >= 0){
			T temp = allUsers.remove(index);
			t.setIcon(temp.getIcon());
			t.setSelected(true);
			allUsers.add(t);
		}
	}

	public boolean isUserCache() {
		return isUserCache;
	}
}
