package com.android.ui.contact;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ChooseUserAdapter<T extends BaseUser> extends ContactAdapter<T> {

	protected int selectMode;
	protected List<T> choiceUsers;

	public ChooseUserAdapter(Context context, List<T> users, int selectMode) {
		this(context, users, null, null, selectMode);
	}

	public ChooseUserAdapter(Context context, List<T> users, List<T> choiceUsers, int selectMode) {
		this(context, users, choiceUsers, null, selectMode);
	}

	public ChooseUserAdapter(Context context, List<T> users, T choiceUser, int selectMode) {
		this(context, users, null, choiceUser, selectMode);
	}

	private ChooseUserAdapter(Context context, List<T> users, List<T> choiceUsers, T choiceUser, int selectMode) {
		super(context, users);
		this.selectMode = selectMode;

		/**
		 * 初始化传入已选择用户
		 */
		this.choiceUsers = new ArrayList<T>();

		if (choiceUsers != null)
			this.choiceUsers.addAll(choiceUsers);

		if (choiceUser != null)
			this.choiceUsers.add(choiceUser);

		if (this.choiceUsers.size() <= 0) {
			for (T t : users) {
				if (t.isSelected())
					this.choiceUsers.add(t);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = super.getView(position, convertView, parent);
		ContactViewHolder holder = (ContactViewHolder) convertView.getTag();
		setChoiceCheckBox(holder, position);
		return convertView;
	}

	protected void setChoiceCheckBox(ContactViewHolder holder, int position) {
		holder.ivCall.setVisibility(View.GONE);
		holder.cbCheck.setVisibility(View.VISIBLE);
		holder.cbCheck.setOnCheckedChangeListener(new CheckBoxChange(users.get(position)));
		if (selectMode == ChooseUserActivity.SINGLE_CHOICE)
			users.get(position).setCheckBox(holder.cbCheck);
		if (users.get(position).isSelected() && choiceUsers.contains(users.get(position)))
			holder.cbCheck.setChecked(true);
		else
			holder.cbCheck.setChecked(false);
	}

	private class CheckBoxChange implements OnCheckedChangeListener {

		private T t;

		public CheckBoxChange(T t) {
			this.t = t;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (t == null)
				return;
			if (isChecked) {
				if (selectMode == ChooseUserActivity.SINGLE_CHOICE && choiceUsers.size() > 0) {
					if (!choiceUsers.get(0).equals(t)) {
						T temp = choiceUsers.remove(0);
						if (temp.getCheckBox() != null)
							temp.getCheckBox().setChecked(false);
					}
				}

				if (!choiceUsers.contains(t)) {
					t.setSelected(true);
					choiceUsers.add(t);
				}
			} else {
				t.setSelected(false);
				choiceUsers.remove(t);
			}
		}
	}

	public List<T> getSelectUser() {
		return choiceUsers;
	}
}
