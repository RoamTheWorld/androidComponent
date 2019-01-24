package com.android.ui;

import android.content.Context;
import android.content.Intent;

import com.android.net.rx.mvp.BasePresenter;
import com.android.net.rx.mvp.RxPresenter;
import com.android.net.rx.mvp.ViewWithPresenter;
import com.android.ui.widget.LoadingDialog;

/**
 * @author wangyang
 *         2017/9/5 00:22
 */
public abstract class MvpFragment<P extends BasePresenter> extends BaseFragment implements ViewWithPresenter<P> {

    private boolean isOnPause = false;

    private P presenter;

    @Override
    public void onDestroy() {
        stopPresenter();
        destroyBgTask();//super里会把普通任务取消，这里取消一下后台任务就行。
        super.onDestroy();
    }

    @Override
    public void onStart() {
        startPresenter();
        super.onStart();
    }



    @Override
    public final P getPresenter() {
        if (presenter == null) {
            Context context=getActivity();
            presenter = initPresenter();
        }
        return presenter;
    }

    public void stopPresenter() {
        if (getPresenter() != null) {
            getPresenter().unSubscribe();
        }
    }

    @Override
    public void startActivity(Class clazz, boolean isfinsh) {
        if (isfinsh){
            startActivity(new Intent(getActivity(),clazz));
            getActivity().finish();
        }else {
            startActivity(new Intent(getActivity(),clazz));
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
    public void showLoadingDialog(String message) {
        startPresenter();
        LoadingDialog.showLoadingDialog(getActivity(), message);
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
            startBgTask(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isOnPause = true;
        destroyBgTask();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            destroyBgTask();
        } else {
            if (getPresenter() != null) {
                getPresenter().subscribe();
                startBgTask(false);
            }
        }
    }

    protected void startBgTask(boolean isTrggleOnResume) {
    }
}
