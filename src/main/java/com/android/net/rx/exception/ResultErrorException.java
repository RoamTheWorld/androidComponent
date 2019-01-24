package com.android.net.rx.exception;

import com.android.net.parse.NetResponse;

/**
 * BII网络异常类
 * Created by Me on 2016/4/22.
 */
public class ResultErrorException extends HttpException {

    public final static String ERROR_CODE_SESSION_INVALID = "validation.session_invalid";
    public final static String ERROR_CODE_SESSION_TIMEOUT = "validation.session_timeout";
    public final static String ERROR_CODE_ROLE_INVALID = "role.invalid_user";

    public ResultErrorException() {}

    public ResultErrorException(String detailMessage) {
        super(detailMessage);
    }

    public ResultErrorException(HttpException exception) {
        super(exception);
    }

    public ResultErrorException(NetResponse netResult) {
        super(netResult.getErrorMsg());
        setType(ExceptionType.RESULT);
        setErrorMessage(netResult.getErrorMsg());
        setErrorCode(netResult.getState() + "");
    }

    /*message 错误信息*/
    private String errorMessage;

    private String errorCode;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
