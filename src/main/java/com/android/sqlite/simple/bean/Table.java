package com.android.sqlite.simple.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 表结构的类
 *
 * @author wangyang
 * @2014-3-7 上午11:51:04
 */
public class Table {
	
	/**
	 * 主键集合
	 */
	private List<Coloumn> keyAttburite;
	
	/**
	 * 属性集合
	 */
	private List<Coloumn> attributes;
	
	public Table() {
		keyAttburite  = new ArrayList<Coloumn>();
		attributes  = new ArrayList<Coloumn>();
	}

	public List<Coloumn> getKeyAttburite() {
		if (keyAttburite == null)
			keyAttburite = new ArrayList<Coloumn>();
		return keyAttburite;
	}

	public void setKeyAttburite(List<Coloumn> keyAttburite) {
		this.keyAttburite = keyAttburite;
	}

	public List<Coloumn> getAttributes() {
		if (attributes == null)
			attributes = new ArrayList<Coloumn>();
		return attributes;
	}

	public void setAttributes(List<Coloumn> attributes) {
		this.attributes = attributes;
	}
	
	public void clear(){
		if(attributes!=null)
			attributes.clear();
		if(keyAttburite!=null)
			keyAttburite.clear();
	}
}
