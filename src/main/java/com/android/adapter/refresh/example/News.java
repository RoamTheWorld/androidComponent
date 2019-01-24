package com.android.adapter.refresh.example;

import com.android.sqlite.annotation.Column;
import com.android.sqlite.annotation.ID;
import com.android.sqlite.annotation.Table;

import java.io.Serializable;

@Table
public class News implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ID(autoIncrement=true)
	private int id;
	
	@Column
	private String title;
	
	@Column(name="content")
	private String summary;
	
	@Column(name="url")
	private String url;
	
	@Column
	private boolean hot;
	
	public News() {
		super();
	}
	
	public News(String title, String summary, String icon, boolean hot) {
		super();
		this.title = title;
		this.summary = summary;
		this.url = icon;
		this.hot = hot;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getIcon() {
		return url;
	}

	public void setIcon(String icon) {
		this.url = icon;
	}

	public boolean isHot() {
		return hot;
	}

	public void setHot(boolean hot) {
		this.hot = hot;
	}

	@Override
	public String toString() {
		return "News [id=" + id + ", title=" + title + ", summary=" + summary + ", url=" + url + ", hot=" + hot + "]";
	}
	
}
