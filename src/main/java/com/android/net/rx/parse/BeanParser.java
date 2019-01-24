package com.android.net.rx.parse;

import com.android.net.parse.NetResponse;
import com.android.net.rx.exception.HttpException;
import com.android.net.rx.exception.ResultErrorException;

import rx.Observable;
import rx.functions.Func1;

/**
 * @author wangyang
 *         2017/9/23 14:13
 */
public class BeanParser <R> implements Func1<NetResponse<R>, Observable<R>> {

    @Override
    public Observable<R> call(NetResponse<R> netResponse) {
        if (null == netResponse) {
            HttpException exception = new HttpException("通信错误");
            exception.setType(HttpException.ExceptionType.SERVER_NULL);
            return Observable.error(exception);
        }
        if (netResponse.isSuccess()) {//如果返回信息中异常为true
            return Observable.just(netResponse.getBody());
        } else {
            //TODO 异常
            ResultErrorException executeException = new ResultErrorException(netResponse);
            return Observable.error(executeException);
        }
    }
}
