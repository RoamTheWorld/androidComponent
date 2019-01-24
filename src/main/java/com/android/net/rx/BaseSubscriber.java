package com.android.net.rx;

import com.android.net.rx.exception.HttpException;
import com.android.net.rx.exception.ResultErrorException;
import com.android.utils.Log;
import com.android.utils.StringUtil;

import rx.Subscriber;

/**
 * 错误通用处理
 * Created by xdy on 2016/4/26.
 */
public abstract class BaseSubscriber<T> extends Subscriber<T> {

    public final static String LOG_TAG = BaseSubscriber.class.getSimpleName();

    ResultErrorException resultErrorException;

    @Override
    public void onError(Throwable e) {

        /**
         * 拿到当前弹出错误框的activity
         */
//        BaseMobileActivity curActivity = (BaseMobileActivity) ActivityManager.getAppManager().currentActivity();
        /**
         * 输出错误日志
         */
        Log.i(LOG_TAG, e.getMessage());
        StackTraceElement[] elements = e.getStackTrace();
        for (StackTraceElement element : elements) {
            Log.i(LOG_TAG, element.toString());
        }
        if (e instanceof ResultErrorException) {
            Log.i(LOG_TAG, "发生网络错误...");
            resultErrorException = (ResultErrorException) e;
            if (resultErrorException.getType().equals(HttpException.ExceptionType.RESULT)) {
                if (!StringUtil.isEmpty(resultErrorException.getErrorCode())) {
                    if (ResultErrorException.ERROR_CODE_ROLE_INVALID.equals(
                            resultErrorException.getErrorCode())
                            || ResultErrorException.ERROR_CODE_SESSION_INVALID.equals(
                            resultErrorException.getErrorCode())
                            || ResultErrorException.ERROR_CODE_SESSION_TIMEOUT.equals(
                            resultErrorException.getErrorCode())) {
                        Log.i(LOG_TAG, "BII会话已超时...");
                        doOnTimeOut();
                        // TODO: 2017/9/1 添加弹框
//                        if (curActivity != null) {
//                            Intent intent =
//                                    new Intent(BroadcastConst.INTENT_ACTION_SERVICE_TIMEOUT);
//                            intent.putExtra(BaseMobileActivity.INTENT_MESSAGE, e.getMessage());
//                            curActivity.sendBroadcast(intent);
//                            return;
//                        }
                    }
                }
            }
        } else if (e instanceof HttpException) {
            // TODO: 2016/4/27  网络连接错误
            resultErrorException = new ResultErrorException((HttpException) e);
            resultErrorException.setErrorMessage(
                    resultErrorException.getType() == HttpException.ExceptionType.SERVER_NULL
                            ? "通信错误" : "网络异常，请检查您的网络状态");
        } else {
            // TODO: 2016/4/27 其他类型的错误
            resultErrorException = new ResultErrorException(e.getMessage());
            resultErrorException.setType(HttpException.ExceptionType.OTHER);
            resultErrorException.setErrorMessage(e.getMessage());
        }
        doOnException(resultErrorException);
    }

    private void doOnException(ResultErrorException resultErrorException) {
        commonHandleException(resultErrorException);
        handleException(resultErrorException);
    }

    public void doOnTimeOut() {

    }

    /**
     * 通用错误处理
     */
    public void commonHandleException(ResultErrorException resultErrorException) {
        //TODO 弹对话框显示错误信息
//        BaseMobileActivity curActivity =
//                (BaseMobileActivity) ActivityManager.getAppManager().currentActivity();
//        curActivity.showErrorDialog(resultErrorException.getErrorMessage());
    }

    public void handleException(ResultErrorException resultErrorException){}

    @Override
    public void onCompleted() {}
}

