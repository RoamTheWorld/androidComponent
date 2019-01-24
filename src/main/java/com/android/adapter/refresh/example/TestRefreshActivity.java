package com.android.adapter.refresh.example;

import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.android.adapter.refresh.ListDataHolder;
import com.android.adapter.refresh.ListDataHolder.LoadMoreMode;
import com.android.adapter.refresh.SqlLiteRequest;
import com.android.adapter.refresh.Up72Adapter;
import com.android.master.R;
import com.android.net.rx.BaseSubscriber;
import com.android.net.rx.SchedulersCompat;
import com.android.sqlite.BaseDAO;
import com.android.sqlite.sql.Selector;
import com.android.ui.BaseActivity;
import com.android.ui.widget.Toast;
import com.android.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;

public class TestRefreshActivity extends BaseActivity {

    private ListView listView;

    private Up72Adapter<News> adapter;

    private ListDataHolder<News> dataHolder;

    private Button btnDb, btnNet;

    @Override
    protected int getContentViewId() {
        return R.layout.example_activity_refresh_test;
    }

    @Override
    public void startActivity(Class clazz, boolean isfinsh) {

    }

    @Override
    protected void findViewById() {
        btnDb = (Button) findViewById(R.id.btn_db);
        btnNet = (Button) findViewById(R.id.btn_net);
        listView = (ListView) findViewById(R.id.listview);
    }

    @Override
    protected void init() {
        Constants.RESULT_STATE_SUCCESS = "0";
        Constants.DB_NAME = "test.db";
    }

    @Override
    protected void setListeners() {
        btnNet.setOnClickListener(this);
        btnDb.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Toast.show(this,"当前控件==="+v);
        if (v == btnNet)
            initNet();
        else
            initDb();
    }

    private void initDb() {
        dataHolder = ListDataHolder.fromListDataHolder(News.class, new SqlLiteRequest());
        dataHolder.setMode(LoadMoreMode.Header);

        Observable.just(Constants.DB_NAME).flatMap(new Func1<String, Observable<String>>() {
            @Override
            public Observable<String> call(String dbName) {
                addNews(dbName);
                return Observable.just(dbName);
            }
        }).compose(SchedulersCompat.<String>applyIoSchedulers())
                .subscribe(new BaseSubscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        dataHolder = ListDataHolder.fromListDataHolder(News.class, new SqlLiteRequest());
                        dataHolder.setMode(LoadMoreMode.Footer);
                        adapter = new Up72Adapter<>(dataHolder, TestRefreshActivity.this, ItemView.class, listView);
                        listView.setAdapter(adapter);
                        dataHolder.refresh();
                    }
                });

    }

    private void addNews(final String dbName) {
        final BaseDAO<News> dao = new BaseDAO<News>(TestRefreshActivity.this, dbName) {};
        List<News> list = dao.list(Selector.from(News.class));
        if (list == null || list.size() == 0) {
           try{
               dao.setRecursion(false);
               dao.setGenerateRecursion(false);
               dao.beginTransaction();
               List<News> users = new ArrayList<>();
               for (int i = 1; i <= 120; i++) {
                   News news = new News("title" + i, "关羽" + i, "icon" + i, i % 2 == 0);
                   System.out.println("--------------------------" + news);
                   users.add(news);
               }
               dao.saveOrUpdate(users);
               dao.setTransactionSuccessful();
           }catch (Exception e){

           }finally {
               dao.endTransaction();
           }
        }
    }

    private void initNet() {
        dataHolder = ListDataHolder.fromListDataHolder(News.class);
        dataHolder.setMode(LoadMoreMode.Footer);
        dataHolder.put("url", "http://192.168.1.102:8080/TestServer/userAction.do");
        dataHolder.put("method", "testNews");
        dataHolder.put(Constants.REFRESH_NET_RESPONSE_KEY, MyResponse.class);
        adapter = new Up72Adapter<>(dataHolder, this, ItemView.class, listView);
        listView.setAdapter(adapter);
        dataHolder.refresh();
    }
}
