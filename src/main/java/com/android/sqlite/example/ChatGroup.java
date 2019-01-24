package com.android.sqlite.example;

import com.android.sqlite.FinderLazyLoader;
import com.android.sqlite.annotation.Column;
import com.android.sqlite.annotation.Finder;
import com.android.sqlite.annotation.ID;
import com.android.sqlite.annotation.Table;

@Table
public class ChatGroup {
	@ID
	private int id;
	
	@Column
	private String uid;
	
	@Column
	private String name;
	
	@Finder(column = "uid", targetColumn = "groupId",many2many="Group_User",inverse=false)
	FinderLazyLoader<ChatUser> users;
	
	public ChatGroup(String uid, String name) {
		super();
		this.uid = uid;
		this.name = name;
	}

	public ChatGroup(String name) {
		super();
		this.name = name;
	}

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

	public FinderLazyLoader<ChatUser> getUsers() {
		return users;
	}

	public void setUsers(FinderLazyLoader<ChatUser> users) {
		this.users = users;
	}

	public ChatGroup() {
		super();
	}
}
