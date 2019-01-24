package com.android.sqlite.example;

import com.android.sqlite.FinderLazyLoader;
import com.android.sqlite.annotation.Column;
import com.android.sqlite.annotation.Finder;
import com.android.sqlite.annotation.ID;
import com.android.sqlite.annotation.Table;

@Table(name="Department")
public class Group {
	
	@ID
	@Column
	protected int id;
	
	@Column
	protected String gid;
	
	@Column
	protected String name;
	
	@Finder(column = "gid", targetColumn = "uid")
	protected FinderLazyLoader<UserA> users;


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public FinderLazyLoader<UserA> getUsers() {
		return users;
	}

	public void setUsers(FinderLazyLoader<UserA> users) {
		this.users = users;
	}

	public Group(String gid, String name) {
		super();
		this.gid = gid;
		this.name = name;
	}

	public Group() {
		super();
	}

	@Override
	public String toString() {
		return "Group [id=" + id + ", gid=" + gid + ", name=" + name + ", users=" + users + "]";
	}
	
}
