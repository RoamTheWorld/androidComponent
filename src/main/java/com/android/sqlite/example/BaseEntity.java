package com.android.sqlite.example;

import com.android.sqlite.annotation.Column;
import com.android.sqlite.annotation.ID;
import com.android.sqlite.annotation.Table;

@Table
public class BaseEntity {
	
	@ID()
	private int id;
	
	@Column
	private String bId;
	
	@Column
	private String nick;
	
	@Column
	private String sex;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getbId() {
		return bId;
	}

	public void setbId(String bId) {
		this.bId = bId;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}
	
	public BaseEntity(){}
	
	public BaseEntity(String bId, String nick, String sex) {
		super();
		this.bId = bId;
		this.nick = nick;
		this.sex = sex;
	}

	public BaseEntity(String nick, String sex) {
		super();
		this.nick = nick;
		this.sex = sex;
	}
	
	public BaseEntity(int id, String bId, String nick, String sex) {
		super();
		this.id = id;
		this.bId = bId;
		this.nick = nick;
		this.sex = sex;
	}

	@Override
	public String toString() {
		return "BaseEntity [bId=" + bId + ", nick=" + nick + ", sex=" + sex + "]";
	}
}
