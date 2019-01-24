package com.android.net.rx.exception;

//TODO 需要改写
/**
 * 异常基类
 * Created by wangyang on 2016/3/11.
 */
public class HttpException extends Exception {

    private static final long serialVersionUID = 1L;

    private ExceptionType type;

    public ExceptionType getType() {
        return type;
    }

    public void setType(ExceptionType type) {
        this.type = type;
    }

    public HttpException(String detailMessage) {
        super(detailMessage);
    }

    public HttpException(Throwable cause) {
        super(cause);
    }

    public HttpException(){
        super();
    }
    public HttpException(HttpException be){
        super(be);
        this.type=be.getType();
    }

    public enum ExceptionType{
        NET_WORK,RESULT,OTHER, SERVER_NULL
    }

}
