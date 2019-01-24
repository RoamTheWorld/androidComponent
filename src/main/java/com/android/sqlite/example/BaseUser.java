package com.android.sqlite.example;

import com.android.sqlite.ForeignLazyLoader;
import com.android.sqlite.annotation.Column;
import com.android.sqlite.annotation.Foreign;
import com.android.sqlite.annotation.ID;

public class BaseUser {
	
	@ID
	@Column
	protected int id;
	
	@Column
	protected String uid;
	
	@Column
	protected String name;
	
	@Column
	protected String password;
	
	@Foreign(foreign = "gid",column="groupId")
	protected ForeignLazyLoader<Group>  group;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public ForeignLazyLoader<Group> getGroup() {
		return group;
	}

	public void setGroup(ForeignLazyLoader<Group> group) {
		this.group = group;
	}

	public BaseUser(String uid, String name, String password) {
		super();
		this.uid = uid;
		this.name = name;
		this.password = password;
	}

	public BaseUser(int id) {
		super();
		this.id = id;
	}

	@Override
	public String toString() {
		return "BaseUser [id=" + id + ", uid=" + uid + ", name=" + name + ", password=" + password + ", group=" + group + "]";
	}

	public BaseUser() {
		super();
	}
	
	
}
