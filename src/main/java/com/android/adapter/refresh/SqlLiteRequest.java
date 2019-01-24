package com.android.adapter.refresh;

import com.android.adapter.refresh.LoadMoreData.RequestType;
import com.android.net.rx.BaseSubscriber;
import com.android.net.rx.SchedulersCompat;
import com.android.net.rx.exception.ResultErrorException;
import com.android.sqlite.BaseDAO;
import com.android.sqlite.sql.Selector;
import com.android.utils.Constants;
import com.android.utils.StringUtil;

import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.functions.Func1;

/**
 * @author wangyang 2014-7-29 下午3:04:22
 */
public class SqlLiteRequest extends BaseRequestStack {

    /**
     * 数据库操作类
     */
    protected BaseDAO<? extends Object> dao;

    public SqlLiteRequest() {
        super();
    }

    @Override
    public <T> void requestData(final Class<T> clazz, final Map<String, Object> params, final LoadMoreData<T> loadMoreData) {
        Observable.just(clazz).compose(rxLifecycleManager.<Class<T>>bindToLifecycle())
                .flatMap(new Func1<Class<T>, Observable<List<T>>>() {
                    @Override
                    public Observable<List<T>> call(Class<T> tClass) {
                        // 初始化dao
                        if (dao == null)
                            dao = BaseDAO.getInstance(clazz, context, Constants.DB_NAME);

                        // 初始化Selector
                        Selector selector = (Selector) params.get(Constants.SELECTOR);
                        if (selector == null)
                            selector = Selector.from(clazz);

                        // 添加OrderBy条件
                        if (params.containsKey(Constants.ORDER_COLUMN))
                            selector = selector.orderBy(params.get(Constants.ORDER_COLUMN).toString(), StringUtil.isEmpty(params.get(Constants.DESC)) ? false : Boolean.parseBoolean(params.get(Constants.DESC).toString()));

                        // 计算offset和pageSize
                        int index = Integer.parseInt(params.get(Constants.PAGE_INDEX).toString());
                        int pageSize = Integer.parseInt(params.get(Constants.PAGE_SIZE).toString());
                        index = (index - 1) * pageSize;

                        // 添加selector
                        selector = selector.limit(pageSize).offset(index);
                        return Observable.just((List<T>)dao.list(selector));
                    }
                }).compose(SchedulersCompat.<List<T>>applyIoSchedulers())
                .subscribe(new BaseSubscriber<List<T>>() {
                    @Override
                    public void onNext(List<T> list) {
                        loadMoreData.handleRequestSuccess(list);
                    }

                    @Override
                    public void commonHandleException(ResultErrorException result) {
                        loadMoreData.handleReuqestError(Constants.ERROR);
                    }
                });
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.SQL_LITE;
    }
}
