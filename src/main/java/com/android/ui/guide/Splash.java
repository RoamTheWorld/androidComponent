package com.android.ui.guide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.LayoutInflater;
import android.view.View;

import com.android.master.R;
import com.android.net.rx.BaseSubscriber;
import com.android.net.rx.SchedulersCompat;
import com.android.net.rx.life.RxLifecycle;
import com.android.net.rx.life.RxLifecycleManager;
import com.android.utils.Constants;

import java.util.concurrent.TimeUnit;

import rx.Observable;

/**
 * 引导页 只在第一次安装的时候显示
 * Copyright: 版权所有 (c) 2002 - 2003
 *
 * @author wangyang
 * @version 1.0.0
 * @create 2014-11-14
 */
public class Splash {
    private Context mContext;
    private LayoutInflater mInflater;
    private CircleFlowIndicator circleFlowIndicator;
    private ViewFlow viewFlow;
    private ImageAdapter imageAdapter;
    /**
     * 引导页结束要跳转的下一页
     */
    private String action;
    /**
     * 要显示的图片资源集合
     */
    private Object[] imagesRes;
    private SharedPreferences mSp;
    private Editor mEditor;

    private RxLifecycleManager rxLifecycleManager;

    public Splash(Context context) {
        super();
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        mSp = context.getSharedPreferences(Constants.FILE_CACHE_NAME, Context.MODE_PRIVATE);
        mEditor = mSp.edit();
        Constants.IS_FIRST_INSTALL = mSp.getBoolean("isFirst", true);
        rxLifecycleManager = new RxLifecycleManager();

    }

    public View getLayout() {
        View view = mInflater.inflate(R.layout.layout_viewflow_activity, null);
        viewFlow = (ViewFlow) view.findViewById(R.id.viewflow);
        circleFlowIndicator = (CircleFlowIndicator) view.findViewById(R.id.viewflowindic);

        if (Constants.IS_FIRST_INSTALL) {
            viewFlow.setVisibility(View.VISIBLE);
            circleFlowIndicator.setVisibility(View.VISIBLE);
            imageAdapter = new ImageAdapter(mContext, imagesRes);
            viewFlow.setAdapter(imageAdapter, 0);
            viewFlow.setFlowIndicator(circleFlowIndicator);

            mEditor.putBoolean("isFirst", false);
            mEditor.commit();
        } else {
            viewFlow.setVisibility(View.GONE);
            circleFlowIndicator.setVisibility(View.GONE);
            mContext.startActivity(new Intent(action));
            ((Activity) mContext).finish();
        }

        setListener();
        return view;
    }

    private void setListener() {
        viewFlow.setOnViewSwitchListener(new ViewFlow.ViewSwitchListener() {

            @Override
            public void onSwitched(View view, int position) {
                if (position == imagesRes.length - 1) {
                    Observable.timer(2000, TimeUnit.MILLISECONDS)
                            .compose(rxLifecycleManager.<Long>bindUntilEvent(RxLifecycle.Event.BG_TASK_DESTROY))
                            .compose(SchedulersCompat.<Long>applyIoSchedulers())
                            .subscribe(new BaseSubscriber<Long>() {
                                @Override
                                public void onNext(Long aLong) {
                                    mContext.startActivity(new Intent(action));
                                    ((Activity) mContext).finish();
                                }
                            });
                } else {
                    rxLifecycleManager.onDestroyEvent(RxLifecycle.Event.BG_TASK_DESTROY);
                }
            }
        });
    }

    /**
     * 设置显示的图片资源
     *
     * @param imagesRes
     */
    public void setImages(Object[] imagesResIds) {
        this.imagesRes = imagesResIds;
    }

    /**
     * 设置要跳转下一个的action
     */
    public void setAction(String action) {
        this.action = action;
    }

}
