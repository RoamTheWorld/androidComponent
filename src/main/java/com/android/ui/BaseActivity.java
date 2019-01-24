package com.android.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.BaseApplication;
import com.android.master.R;
import com.android.utils.ButtonClickLock;
import com.android.utils.Constants;
import com.android.utils.PublicUtils;
import com.android.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author wangyang
 * @version 1.0.0
 * @description Activity基类, 可在引用项目中继承该类并添加相应功能。
 * @create 2014-2-14 下午03:25:49
 */
public abstract class BaseActivity extends FragmentActivity implements OnClickListener {

    protected final String CLICK_LOCK = this.getClass().getSimpleName();

    /**
     * Activity结束时是否滑动退出
     */
    private boolean isRightOut = true;

    protected SharedPreferences mSp;

    private LinearLayout linearLayout;
    private FrameLayout frameLeft, frameRight;
    private TextView tvLeft, tvContent, tvRight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSp = getSharedPreferences(Constants.FILE_CACHE_NAME, Context.MODE_PRIVATE);
        StatusBarUtil.setStatusBarColor(this, R.color.white);
        setContentView();


        findViewById();
        init();
        setListeners();
        registerReceivers();
        if (getApplication() instanceof BaseApplication)
            ((BaseApplication) getApplication()).getActivities().add(this);
    }

    protected void put(String key, boolean value) {
        Editor editor = mSp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    protected void put(String key, float value) {
        Editor editor = mSp.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    protected void put(String key, String value) {
        Editor editor = mSp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    protected void put(String key, int value) {
        Editor editor = mSp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    protected void put(String key, long value) {
        Editor editor = mSp.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    protected void put(String key, Set<String> value) {
        Editor editor = mSp.edit();
        editor.putStringSet(key, value);
        editor.commit();
    }

    /**
     * 设置布局文件
     */
    protected void setContentView() {
        setContentView(getContentViewId());
        // getLayoutInflater().inflate(getContentViewId(), mFMContent);
    }

    /**
     * 设置布局文件Id
     */
    protected abstract int getContentViewId();

    public void startActivity(Class clazz, boolean isFinish) {
    }

    /**
     * 查找控件
     */
    protected abstract void findViewById();

    /**
     * 设置事件监听
     */
    protected void setListeners() {
    }

    /**
     * 初始化Activity数据
     */
    protected abstract void init();

    /**
     * 注册广播接收者
     */
    protected void registerReceivers() {
    }

    ;

    /**
     * 取消广播接收
     */
    protected void unRegisterReceivers() {
    }



    /**
     * 是否滑动退出Activity
     *
     * @param isRightOut
     */
    public void setRightOut(boolean isRightOut) {
        this.isRightOut = isRightOut;
    }

    @Override
    protected void onDestroy() {
        unRegisterReceivers();
        super.onDestroy();
        if (getApplication() instanceof BaseApplication)
            ((BaseApplication) getApplication()).getActivities().remove(this);
        ButtonClickLock.removeLock(CLICK_LOCK);
    }

    @Override
    public void finish() {
        super.finish();
        if (isRightOut) {
            overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
        }
    }

    @Override
    public void onClick(View v) {


        if (!isCanClick())
            return;

    }

    public void doBack(View view) {
        finish();
    }

    /**
     * 初始化标题栏
     *
     * @param imgLeft  左侧图片ID
     * @param imgRight 右侧图片ID
     * @param left     左侧文字
     * @param content  中间文字
     * @param right    右侧文字
     * @author 李应锋
     * @create 2015-3-2
     */
    protected void initBaseTitle(int imgLeft, int imgRight, String left, String content, String right) {
        this.initBaseTitle(Color.GRAY, imgLeft, imgRight, left, content, right, Color.BLACK, Color.BLACK, Color.BLACK);
    }

    /**
     * 初始化标题栏
     *
     * @param bgColor  标题栏背景色
     * @param imgLeft  左侧图片ID
     * @param imgRight 右侧图片ID
     * @param left     左侧文字
     * @param content  中间文字
     * @param right    右侧文字
     * @author 李应锋
     * @create 2015-3-2
     */

    protected void initBaseTitle(int bgColor, int imgLeft, int imgRight, String left, String content, String right) {
        this.initBaseTitle(bgColor, imgLeft, imgRight, left, content, right, Color.BLACK, Color.BLACK, Color.BLACK);
    }

    /**
     * 初始化标题栏
     *
     * @param bgColor      标题栏背景色
     * @param imgLeft      左侧图片ID
     * @param left         左侧文字
     * @param content      中间文字
     * @param right        右侧文字
     * @param leftColor    左侧文字颜色
     * @param contentColor 中间文字颜色
     * @param rightColor   右侧文字颜色
     * @author 李应锋
     * @create 2015-3-2
     */
    protected void initBaseTitle(int bgColor, int imgLeft, int imgRight, String left, String content, String right, int leftColor, int contentColor, int rightColor) {
        if (linearLayout == null)
            linearLayout = (LinearLayout) findViewById(R.id.title_linear);
              linearLayout.setBackgroundColor(bgColor);
        if (frameLeft == null)
            frameLeft = (FrameLayout) findViewById(R.id.title_frame_left);
        if (imgLeft > 0 || left != null) {
            frameLeft.setVisibility(View.VISIBLE);
            frameLeft.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickTitleLeft(v);
                }
            });
        } else
            frameLeft.setVisibility(View.INVISIBLE);

        if (frameRight == null)
            frameRight = (FrameLayout) findViewById(R.id.title_frame_right);
        if (imgRight > 0 || right != null) {
            frameRight.setVisibility(View.VISIBLE);
            frameRight.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickTitleRight(v);
                }
            });
        } else
            frameRight.setVisibility(View.INVISIBLE);

        if (tvLeft == null)
            tvLeft = (TextView) findViewById(R.id.title_tv_left);
        if (imgLeft > 0)
            tvLeft.setBackgroundResource(imgLeft);
        if (left != null)
            tvLeft.setText(left);
        tvLeft.setTextColor(leftColor);
        if (tvContent == null)
            tvContent = (TextView) findViewById(R.id.title_tv_content);
        if (content != null)
            tvContent.setText(content);
        tvContent.setTextColor(contentColor);
        if (tvRight == null)
            tvRight = (TextView) findViewById(R.id.title_tv_right);
        if (imgRight > 0)
            tvRight.setBackgroundResource(imgRight);
        if (right != null)
            tvRight.setText(right);
        tvRight.setTextColor(rightColor);
    }

    /**
     * 标题栏左侧点击事件
     */
    protected void onClickTitleLeft(View v) {
        finish();
    }

    /**
     * 标题栏右侧点击事件
     */
    protected void onClickTitleRight(View v) {
    }

    public boolean isCanClick() {
        return ButtonClickLock.isCanClick(CLICK_LOCK);
    }


    /**
     * 绑定按钮的点击事件
     */
    public void setOnClickListeners(OnClickListener listeners, View... views) {
        if (views != null) {
            for (View view : views) {
                if (listeners != null) {
                    view.setOnClickListener(listeners);
                }
            }
        }
    }

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    public boolean isHadPermission(String... permissions) {
        boolean result = true;
        if (PublicUtils.isEmpty(permissions))
            return result;

        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissionList = new ArrayList<>();
            for (String permission : permissions) {
                int hasWriteSdcardPermission = ContextCompat.checkSelfPermission(this, permission);
                if (hasWriteSdcardPermission != PackageManager.PERMISSION_GRANTED)
                    permissionList.add(permission);
            }

            String[] stringArray = permissionList.toArray(new String[0]);
            if (permissionList.size() > 0) {
                requestPermissions(stringArray, REQUEST_CODE_ASK_PERMISSIONS);
                result = false;
            }
        }
        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                //可以遍历每个权限设置情况
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //这里写你需要相关权限的操作
                    for (String permission : permissions)
                        onRequestPermissionSuccess(permission);
                } else {
                    Toast.makeText(this, "权限没有开启,请在设置-->权限管理中开启", Toast.LENGTH_SHORT).show();
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void onRequestPermissionSuccess(String permission) {}
}
