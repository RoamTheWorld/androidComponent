package com.android.sqlite.example;

import com.android.sqlite.FinderLazyLoader;
import com.android.sqlite.ForeignLazyLoader;
import com.android.sqlite.annotation.Column;
import com.android.sqlite.annotation.Finder;
import com.android.sqlite.annotation.Foreign;
import com.android.sqlite.annotation.ID;
import com.android.sqlite.annotation.Table;

@Table
public class ChatUser extends BaseEntity{
	@ID
	private int id;
	
	@Column
	private String uid;
	
	@Column
	private String name;
	
	@Finder(column = "uid", targetColumn = "userId",many2many="Group_User",inverse=true)
	FinderLazyLoader<ChatGroup> group;
	
	@Foreign(column="baseId",foreign = "bId",isSuperClass=true)
	ForeignLazyLoader<BaseEntity> bId;
	
	public ChatUser(String bId, String nick, String sex, String uid, String name) {
		super(bId, nick, sex);
		this.uid = uid;
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

	public FinderLazyLoader<ChatGroup> getGroup() {
		return group;
	}

	public void setGroup(FinderLazyLoader<ChatGroup> group) {
		this.group = group;
	}

	@Override
	public String toString() {
		return "ChatUser [id=" + id + ", uid=" + uid + ", name=" + name + ", group=" + group + ", getbId()=" + getbId() + ", getNick()=" + getNick() + ", getSex()=" + getSex() + "]";
	}

	public ChatUser() {
		super();
	}
}
