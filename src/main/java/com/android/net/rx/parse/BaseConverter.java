package com.android.net.rx.parse;

import okhttp3.Response;
import rx.Observable;

/**
 * @author wangyang
 *         2017/9/1 15:56
 */
public abstract class BaseConverter<T> implements Converter<Response, Observable<T>> {

//    @Override
//    public NetResponse convert2NetResult(Response response) throws IOException, HttpException {
//        String result = response.body().string();
//        Log.i("response:" + result);
//
//        if (!result.contains(Constants.RESULT_KEY_STATE)) {
//            HttpException exception = new HttpException("通信错误");
//            exception.setType(HttpException.ExceptionType.SERVER_NULL);
//            throw exception;
//        }
//
//        JSONObject obj = JSON.parseObject(result);
//
//        NetResponse netResult = new NetResponse(obj.getString(Constants.RESULT_KEY_STATE), obj.getString(Constants.RESULT_KEY_ERROR_MESSAGE), obj.getString(Constants.RESULT_KEY_BODY));
//        if (!netResult.isSuccess()) {
//            ResultErrorException executeException = new ResultErrorException(result);
//            throw executeException;
//        }
//        return netResult;
//    }
}
