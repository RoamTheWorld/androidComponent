package com.android.net.rx.model;

import java.io.Serializable;

/**
 * @author lxw
 *
 */
public class Request<T> implements Serializable{

	/**
	 */
	public Request(String method){
		this(method, null);
	}

	public Request(T params){
		this(null, params);
	}
	
	/**
	 * @param params
	 */
	public Request(String method, T params){
		this.params = params;
		this.method=method;
	}
	
	private T params;
	
	private String method;
	
	public T getParams() {
		return params;
	}

	public void setParams(T params) {
		this.params = params;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
	
	
}
