package com.android.sqlite.example;

import com.android.sqlite.ForeignLazyLoader;
import com.android.sqlite.annotation.Column;
import com.android.sqlite.annotation.Foreign;
import com.android.sqlite.annotation.ID;
import com.android.sqlite.annotation.Table;

@Table
public class User extends BaseEntity{
	
	@ID
	@Column
	protected int id;
	
	@Foreign(column="baseId",foreign = "bId",isSuperClass=true)
	public ForeignLazyLoader<BaseEntity> bId;
	//public BaseEntity bId;
	//public List<BaseEntity> bId;
	
	@Column
	protected String name;
	
	@Column
	protected String password;
	
	@Foreign(foreign = "dId")
	//public Department departId;
	public ForeignLazyLoader<Department> departId;
	//public List<Department> departId;
	
	
	
	public User(){}
	
	public User(int id, String name, String password) {
		super();
		this.id = id;
		this.name = name;
		this.password = password;
	}
	
	public User(String bId, String nick, String sex, int id, String name, String password) {
		super(bId, nick, sex);
		this.id = id;
		this.name = name;
		this.password = password;
	}
	
	public User(String bId, String nick, String sex,  String name, String password) {
		super(bId, nick, sex);
		this.name = name;
		this.password = password;
	}
	
	public User(int id,String bId, String nick, String sex,  String name, String password) {
		super(id,bId, nick, sex);
		this.name = name;
		this.password = password;
	}
	
	public User(String nick, String sex,  String name, String password) {
		super( nick, sex);
		this.name = name;
		this.password = password;
	}
	
	public User(int id) {
		super();
		this.id = id;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", bId=" + bId + ", name=" + name + ", password=" + password + ", departId=" + departId + ", getbId()=" + getbId() + ", getNick()=" + getNick() + ", getSex()=" + getSex() + "]";
	}

//	@Override
//	public String toString() {
//		return "User [id=" + id + ", name=" + name + ", password=" + password + ", departId=" + departId + "]";
//	}

//	@Override
//	public String toString() {
//		return "User [id=" + id + ", bId=" + bId + ", name=" + name + ", password=" + password + ", departId=" + departId + "]";
//	}
	
	

}
