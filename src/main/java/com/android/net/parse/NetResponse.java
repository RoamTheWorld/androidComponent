package com.android.net.parse;

import com.android.utils.Constants;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @description JSON返回对象
 * 
 * @author wangyang
 * @create 2014-2-17 下午03:16:09
 * @version 1.0.0
 */
public class NetResponse<T> implements Serializable {
	/**
	 * 状态码
	 */
	@SerializedName(Constants.RESULT_KEY_STATE)
	private String state;

	/**
	 * 错误信息
	 */
	@SerializedName(Constants.RESULT_KEY_ERROR_MESSAGE)
	private String errorMsg;

	/**
	 * 消息体
	 */
	@SerializedName(Constants.RESULT_KEY_BODY)
	private T body;

	public T getBody() {
		return body;
	}

	public void setBody(T body) {
		this.body = body;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public boolean isSuccess() {
		return Constants.RESULT_STATE_SUCCESS.equals(state);
	}
}
