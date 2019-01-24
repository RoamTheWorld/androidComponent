/**   
 * @Title: LoadPageError.java 
 * @Package com.up72.kit 
 * @author imhzwen@gmail.com   
 * @date 2014-7-8 上午10:57:03 
 * @version V1.0
 * @encoding UTF-8   
 */
package com.android.exception;

/**
 * @ClassName: LoadPageError
 * @date 2014-7-8 上午10:57:03
 * 
 */
@SuppressWarnings("serial")
public class AdapterException extends Exception {
	public enum ErrorState {
		NetError, ServerError, ParseError;
		private String errorMessage;

		public String getErrorMessage() {
			return errorMessage;
		}

		public void setErrorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
		}
	}

	public ErrorState errorState;

	/**
	 * <p>
	 * Title:
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 */
	public AdapterException(ErrorState errorState) {
		super();
		this.errorState = errorState;
	}
}
