package com.android.sqlite.example;

import com.android.sqlite.ForeignLazyLoader;
import com.android.sqlite.annotation.Column;
import com.android.sqlite.annotation.Foreign;

public class UserA extends BaseUser{
	
	public UserA(String uid, String name, String password) {
		super(uid, name, password);
	}
	
	
	@Column
	protected String password;
	
	@Column
	protected String sex;
	
	@Column
	protected String icon;

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	@Foreign(foreign = "gid",column="groupId")
	protected ForeignLazyLoader<Group>  group;
	
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public void setGroup(ForeignLazyLoader<Group> group) {
		super.setGroup(group);
		this.group = group;
	}

	public UserA() {
	}

	public UserA(String uid, String name, String password, String sex, String icon) {
		super(uid, name, password);
		this.sex = sex;
		this.icon = icon;
		this.uid = uid;
		this.name = name;
		this.password = password;
	}

	public UserA(int i) {
		super(i);
	}

	@Override
	public String toString() {
		return "UserA [password=" + password + ", sex=" + sex + ", icon=" + icon + ", group=" + group + ", id=" + id + ", uid=" + uid + ", name=" + name + "]";
	}

	
	
	
	
}
