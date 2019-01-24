package com.android.sqlite.example;

import com.android.sqlite.FinderLazyLoader;
import com.android.sqlite.annotation.Column;
import com.android.sqlite.annotation.Finder;
import com.android.sqlite.annotation.ID;
import com.android.sqlite.annotation.Table;

@Table
public class Department {
	
	@ID()
	private int id;
	
	@Column
	private String department;
	
	@Column
	private String dId;
	
	//建议使用延迟加载
	@Finder(column = "dId", targetColumn = "departId")
	public FinderLazyLoader<User> users;
	//public List<User> users;
	//public User users;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public Department(String department) {
		super();
		this.department = department;
	}
	
	public Department(String department, String dId) {
		super();
		this.department = department;
		this.dId = dId;
	}

	public Department(int id, String department) {
		super();
		this.id = id;
		this.department = department;
	}

	public Department(){
	}

	@Override
	public String toString() {
		return "Department [id=" + id + ", department=" + department + ", users=" + users + "]";
	}

	public String getdId() {
		return dId;
	}

	public void setdId(String dId) {
		this.dId = dId;
	}
	
}
