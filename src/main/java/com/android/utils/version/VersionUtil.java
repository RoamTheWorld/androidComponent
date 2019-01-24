package com.android.utils.version;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RemoteViews;

import com.android.BaseApplication;
import com.android.master.R;
import com.android.net.rx.BaseSubscriber;
import com.android.net.rx.RxHttpClient;
import com.android.net.rx.SchedulersCompat;
import com.android.net.rx.exception.ResultErrorException;
import com.android.net.rx.life.RxLifecycleManager;
import com.android.ui.widget.Toast;
import com.android.utils.Constants;
import com.android.utils.DownLoadByMutilThread;
import com.android.utils.DownLoadByMutilThread.DownLoadListener;
import com.android.utils.FileUtil;
import com.android.utils.PackageUtil;
import com.android.utils.StringUtil;
import com.android.utils.security.SecurityType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * 描述 :多线程下载类 , 支持断点续传
 *
 * @author wangyang
 * @version 1.0.0
 * @create 2013-7-26
 */
public class VersionUtil implements DownLoadListener {

    public static String osType = "android";
    public static String osTypeKey = "osType";

    private Context context;
    private Notification updateNotification;
    private NotificationManager updateNotificationManager;
    private PendingIntent updatePendingIntent;
    private RemoteViews contentView;

    private DownLoadListener listener;
    private OnCancelButtonListener cancelListener;
    private OnCheckVersionFinishListener finishListener;

    protected RxLifecycleManager rxLifecycleManager;

    protected <T> Observable.Transformer<T, T> bindToLifecycle() {
        return rxLifecycleManager.bindToLifecycle();
    }

    /**
     * 最新版本名称
     */
    private String version;

    private DownLoadByMutilThread downLoad;

    private static VersionUtil versionUtil;

    private VersionUtil(Context context) {
        this.context = context;
        this.listener = this;
         rxLifecycleManager = new RxLifecycleManager();
    }

    public static VersionUtil getInstance(Context context) {
        if (versionUtil == null) {
            synchronized (VersionUtil.class) {
                if (versionUtil == null)
                    versionUtil = new VersionUtil(context);
                else
                    versionUtil.context = context;
            }
        } else
            versionUtil.context = context;
        return versionUtil;
    }

    public void checkUpdate(String url, SecurityType securityType, OnCheckVersionFinishListener checkVersionFinishListener, OnCancelButtonListener listener) {
        this.cancelListener = listener;
        this.finishListener = checkVersionFinishListener;
        Map<String, Object> header = new HashMap<>();
        header.put(osTypeKey, osType);
        RxHttpClient.instance.withSecurityType(securityType).post(url, null, VersionResponse.class, header)
                .compose(this.<Version>bindToLifecycle())
                .compose(SchedulersCompat.<Version>applyIoSchedulers())
                .subscribe(new BaseSubscriber<Version>() {
                    @Override
                    public void onNext(final Version version) {
                        if (version == null) {
                            commonHandleException(new ResultErrorException("check version fail!"));
                            return;
                        }
                        if (version.getVersion() <= PackageUtil.getVersionCode(context)) {
                            Toast.show(context, "当前已经是最新版本");
                            if (finishListener != null)
                                finishListener.onCheckVersionFinish(version);
                            return;
                        }
                        showDownLoadDialog(version);
                    }

                    @Override
                    public void commonHandleException(ResultErrorException resultErrorException) {
                        if (finishListener != null)
                            finishListener.onCheckVersionFinish(null);
                        Toast.show(context, "获取最新版本失败,请稍后再试!");
                    }
                });
    }

    private void showDownLoadDialog(final Version version) {
        VersionDialog.showDialog(VersionUtil.this.context, version.getVersionName(), version.getUpdateInfo(), null, null, new OnClickListener() {
            @Override
            public void onClick(View v) {
                VersionDialog.dismiss();
                if (version.isMustUpdate()) {
                    if (cancelListener == null) {
                        BaseApplication.clearActivitys();
                    } else {
                        cancelListener.onCancel(version);
                    }
                } else {
                    context.startActivity(new Intent(context.getPackageName() + Constants.Action.HOME_PAGE));
                }
            }
        }, new OnClickListener() {
            @Override
            public void onClick(View v) {
                VersionDialog.dismiss();
                download(version.getAppUrl(), version.getVersionName());
            }
        }, false, true);
    }

    public void checkUpdate(String url) {
        checkUpdate(url, null, null, null);
    }

    public void checkUpdate(String url, OnCancelButtonListener listener) {
        checkUpdate(url, null, null, listener);
    }

    public void checkUpdate(String url, OnCheckVersionFinishListener listener) {
        checkUpdate(url, null, listener, null);
    }

    public void checkUpdate(String url, OnCheckVersionFinishListener listener, OnCancelButtonListener cancelLintener) {
        checkUpdate(url, null, listener, cancelLintener);
    }

    public void checkUpdate(String url, SecurityType securityType) {
        checkUpdate(url, securityType, null, null);
    }

    public void download(String address, String version, String dirName) {
        download(address, version, dirName, false);
    }

    /**
     * 下载安装文件
     */
    public void download(String address, String version, String dirName, boolean isSupportBreakPoint) {
        this.version = version;
        if (StringUtil.isEmpty(dirName))
            dirName = Constants.FILE_ROOT_DIRECTORY;
        File dirPath = FileUtil.getFileDir(context, dirName);
        downLoad = new DownLoadByMutilThread(listener, dirPath.getAbsolutePath() + "/" + version + ".apk", address);
        downLoad.startDownLoad(isSupportBreakPoint);
    }

    public void download(String address, String version) {
        download(address, version, context.getPackageName());
    }

    /**
     * 打开安装文件进行安装
     *
     * @param file
     */
    private void openFile(File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    @Override
    public void beforeDownLoad(int max) {
        Observable.just(max).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        updateNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        updatePendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, new Intent(), 0);
                        contentView = new RemoteViews(context.getPackageName(), R.layout.notification_app_download);
                        contentView.setTextViewText(R.id.notificationTitle, "Download: " + version);
                        contentView.setTextViewText(R.id.notificationPercent, "0% ");
                        contentView.setProgressBar(R.id.notificationProgress, 100, 0, false);

                        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher);

                        if (android.os.Build.VERSION.SDK_INT >= 16) {
                            updateNotification = notifyBuilder.build();
                            updateNotification.bigContentView = contentView;
                        }
                        updateNotification.contentView = contentView;
                        updateNotification.contentIntent = updatePendingIntent;
                        updateNotificationManager.notify(1, updateNotification);
                    }
                });
    }

    @Override
    public void onDownLoadProcess(int process, int max) {
        process = process * 100 / max;
        Observable.just(process).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Integer>() {
                    @Override
                    public void onNext(Integer process) {
                        contentView.setTextViewText(R.id.notificationPercent, process + "% ");
                        contentView.setProgressBar(R.id.notificationProgress, 100, process, false);
                        updateNotificationManager.notify(1, updateNotification);
                    }
                });
    }

    @Override
    public void finished(String path) {
        Observable.just(path).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<String>() {
                    @Override
                    public void onNext(String path) {
                        contentView.setTextViewText(R.id.notificationPercent, "100% ");
                        contentView.setProgressBar(R.id.notificationProgress, 100, 100, false);
                        updateNotificationManager.notify(1, updateNotification);
                        updateNotificationManager.cancel(1);
                        openFile(new File(path));
                    }
                });
    }

    @Override
    public void onError(Throwable error) {

    }

    public void setListener(DownLoadListener listener) {
        this.listener = listener;
    }

    public interface OnCancelButtonListener {
        void onCancel(Version version);
    }

    public interface OnCheckVersionFinishListener {
        void onCheckVersionFinish(Version version);
    }
}
