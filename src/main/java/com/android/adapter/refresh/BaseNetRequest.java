package com.android.adapter.refresh;

import com.android.utils.security.SecurityType;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseNetRequest extends BaseRequestStack {

	protected Map<String, String> headers;

	protected SecurityType security;

	public Object removeHeader(String key) {
		if (key == null || headers == null)
			return null;
		return headers.remove(key);
	}

	public void removeAllHeader() {
		if (headers == null || headers.size() <= 0)
			return;
		headers.clear();
	}
	
	public void addAllHeader(Map<String, String> map) {
		if (map == null || map.size() <= 0)
			return;
		if (headers == null)
			headers = new HashMap<>();
		headers.putAll(map);
	}

	public void addHeader(String key, String value) {
		if (headers == null)
			headers = new HashMap<>();
		if (value != null && key != null)
			headers.put(key, value);
	}

	public void addSecurityHeader(String key, String desKey, Object value) {
		if (desKey == null || desKey.length() < 24)
			return;
		try {
			addHeader(key, security.getSecurityString(desKey, value));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setSecurity(SecurityType security) {
		this.security = security;
	}

}
