package com.android.adapter.refresh;

import com.android.net.parse.NetResponse;
import com.android.net.rx.BaseSubscriber;
import com.android.net.rx.RxHttpClient;
import com.android.net.rx.SchedulersCompat;
import com.android.utils.Constants;

import java.util.List;
import java.util.Map;


/**
 * @author wangyang
 *         2017/9/19 11:20
 */
public class RxOkRequest extends BaseNetRequest{

    @Override
    public <T> void requestData(final Class<T> clazz, Map<String, Object> params, final LoadMoreData<T> loadMoreData) {
        String url = params.get(Constants.URL).toString();
        RxHttpClient.instance.post(url,params, (Class<? extends NetResponse<List<T>>>) params.get(Constants.REFRESH_NET_RESPONSE_KEY),headers)
                .compose(rxLifecycleManager.<List<T>>bindToLifecycle())
                .compose(SchedulersCompat.<List<T>>applyIoSchedulers())
                .subscribe(new BaseSubscriber<List<T>>() {
                    @Override
                    public void onNext(List<T> list) {
                        loadMoreData.handleRequestSuccess(list);
                    }
                });
    }

    @Override
    public LoadMoreData.RequestType getRequestType() {
        return LoadMoreData.RequestType.NET;
    }
}
