package com.android.ui;

import android.content.Intent;

import com.android.net.rx.mvp.BasePresenter;
import com.android.net.rx.mvp.RxPresenter;
import com.android.net.rx.mvp.ViewWithPresenter;
import com.android.ui.widget.LoadingDialog;

/**
 * @author wangyang
 *         2017/9/5 00:17
 */
public abstract class MvpActivity<P extends BasePresenter> extends BaseActivity implements ViewWithPresenter<P> {

    private boolean isOnPause = false;

    private P presenter;

    @Override
    public void onDestroy() {
        stopPresenter();
        destroyBgTask();
        super.onDestroy();
    }

    @Override
    public void startActivity(Class clazz,boolean isfinsh) {
        if (isfinsh){
            startActivity(new Intent(this,clazz));
            finish();
        }else {
            startActivity(new Intent(this,clazz));
        }


    }

    @Override
    public void onStart() {
        startPresenter();
        super.onStart();
    }

    @Override
    public final P getPresenter() {
        if (presenter == null) {
            presenter = initPresenter();
        }
        return presenter;
    }

    public void stopPresenter() {
        if (getPresenter() != null) {
            getPresenter().unSubscribe();
        }
    }

    public void startPresenter() {
        if (getPresenter() != null) {
            getPresenter().subscribe();
        }
    }

    /**
     * 显示loading框之前恢复presenter工作
     */
    public void showLoadingDialog() {
        showLoadingDialog("正在加载...");
    }

    public void showLoadingDialog(String message) {
        startPresenter();
        LoadingDialog.showLoadingDialog(this, message);
    }

    public void closeLoadingDialog() {
        stopPresenter();
        LoadingDialog.dismissLoadingDialog();
    }

    public void destroyBgTask() {
        if (getPresenter() != null && getPresenter() instanceof RxPresenter) {
            ((RxPresenter) getPresenter()).destroyBgTask();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isOnPause) {
            isOnPause = false;
            if (getPresenter() == null)
                return;
            getPresenter().subscribe();
            startBgTask();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isOnPause = true;
        destroyBgTask();
    }

    protected void startBgTask() {
    }
}
