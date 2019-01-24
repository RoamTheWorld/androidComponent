package com.android.ui.contact;

import java.io.Serializable;

import android.widget.CheckBox;

import com.android.utils.ChineseUtil;
import com.android.utils.StringUtil;

/**
 * 电话簿实体
 * 
 * @Copyright 版权所有 (c) 2002 - 2003
 * @author wangyang
 * @version 1.0.0
 * @create 2014-6-30
 */
@SuppressWarnings("serial")
public abstract class BaseUser implements Serializable, Comparable<BaseUser> {
	/**
	 * 性别女
	 */
	public static final int MAN = 1;
	/**
	 * 性别男
	 */
	public static final int WOMAN = 2;

	protected String spellName;

	private boolean selected;

	private transient CheckBox checkBox;
	
	protected abstract String getUid();
	
	protected abstract String getName();

	protected abstract String getPhone();

	protected abstract String getIcon();
	
	protected abstract void setIcon(String icon);

	protected abstract String getNickName();

	protected abstract Integer getSex();
	
	public boolean isLogin(){
		return !StringUtil.isEmpty(getUid())&&!StringUtil.isEmpty(getName())&&!StringUtil.isEmpty(getNickName());
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public CheckBox getCheckBox() {
		return checkBox;
	}

	public void setCheckBox(CheckBox checkBox) {
		this.checkBox = checkBox;
	};

	public String getSpellName() {
		if (StringUtil.isEmpty(spellName))
			spellName = ChineseUtil.converterToSpell(getNickName());
		return spellName;
	}

	public String getFirstSpell() {
		return ChineseUtil.converterToSpellFirst(getSpellName());
	}

	@Override
	public int compareTo(BaseUser another) {
		return getSpellName().compareTo(another.getSpellName());
	}

}
