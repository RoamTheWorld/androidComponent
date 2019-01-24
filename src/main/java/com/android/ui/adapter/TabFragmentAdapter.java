package com.android.ui.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * @author wangyang
 * @version 1.0.0
 * @description tab适配器
 * @create 2014-2-21 上午11:40:27
 */
public class TabFragmentAdapter implements OnCheckedChangeListener {
    protected Context mContext; // Fragment所属的Activity
    private List<Fragment> mFragments; // 一个tab页面对应一个Fragment
    private RadioGroup rgTabs; // 用于切换tab
    private int contentId; // Activity中所要被替换的区域的id
    private int currentTabIndex; // 当前Tab页面索引
    private OnTabChangeListener mListener;
    private int selectedColorId;
    private int normalColorId;
    private int index;// 显示第几个的索引
    private int leftInAnmi, leftOutAnmi, rightInAnmi, rightOutAnmi;

    public TabFragmentAdapter(Context context, List<Fragment> fragments, RadioGroup rgs, int contentId, int selectedColorId, int normalColorId, int leftInAnmi, int leftOutAnmi, int rightInAnmi,
                              int rightOutAnmi, int index) {
        this.mContext = context;
        this.contentId = contentId;
        this.rgTabs = rgs;
        this.mFragments = fragments;
        this.leftInAnmi = leftInAnmi;
        this.leftOutAnmi = leftOutAnmi;
        this.rightInAnmi = rightInAnmi;
        this.rightOutAnmi = rightOutAnmi;
        this.selectedColorId = selectedColorId;
        this.normalColorId = normalColorId;
        this.index = index;
        rgTabs.setOnCheckedChangeListener(this);
        if (fragments.size() <= 1)
            rgTabs.setVisibility(View.GONE);
        toFragment(index);
    }

    public TabFragmentAdapter(Context context, List<Fragment> fragments, RadioGroup rgs, int contentId) {
        this(context, fragments, rgs, contentId, 0, 0, 0, 0, 0, 0, 0);
    }

    public TabFragmentAdapter(Context context, List<Fragment> fragments, RadioGroup rgs, int contentId, int index) {
        this(context, fragments, rgs, contentId, 0, 0, 0, 0, 0, 0, index);
    }

    public TabFragmentAdapter(Context context, List<Fragment> fragments, RadioGroup rgs, int contentId, int selectedColorId, int normalColorId) {
        this(context, fragments, rgs, contentId, selectedColorId, normalColorId, 0, 0, 0, 0, 0);
    }

    public TabFragmentAdapter(Context context, List<Fragment> fragments, RadioGroup rgs, int contentId, int selectedColorId, int normalColorId, int index) {
        this(context, fragments, rgs, contentId, selectedColorId, normalColorId, 0, 0, 0, 0, index);
    }

    public TabFragmentAdapter(Context context, List<Fragment> fragments, RadioGroup rgs, int contentId, int leftInAnmi, int leftOutAnmi, int rightInAnmi, int rightOutAnmi) {
        this(context, fragments, rgs, contentId, 0, 0, leftInAnmi, leftOutAnmi, rightInAnmi, rightOutAnmi, 0);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        for (int i = 0; i < mFragments.size(); i++) {
            RadioButton radio = (RadioButton) rgTabs.getChildAt(i);
            Fragment fragment = mFragments.get(i);
            FragmentTransaction ft = obtainFragmentTransaction(i);

            if (rgTabs.getChildAt(i).getId() == checkedId) {
                if (selectedColorId > 0)
                    radio.setTextColor(mContext.getResources().getColor(selectedColorId));
                mFragments.get(currentTabIndex).onPause(); // 暂停当前tab
                if (fragment.isAdded()) {
                    fragment.onResume(); // 启动目标tab的onResume()
                } else {
                    ft.add(contentId, fragment);
                }
                showTab(i);
                ft.commitAllowingStateLoss();
            } else {
                fragment.onStop();
                if (normalColorId > 0)
                    radio.setTextColor(mContext.getResources().getColor(normalColorId));
            }
        }
    }

    /**
     * 打开指定fragment
     */
    public void toFragment(int idx) {
        if (idx >= rgTabs.getChildCount())
            return;
        RadioButton radio = (RadioButton) rgTabs.getChildAt(idx);
        radio.setChecked(true);
    }

    /**
     * 切换tab
     *
     * @param idx
     */
    public void showTab(int idx) {
        for (int i = 0; i < mFragments.size(); i++) {
            Fragment fragment = mFragments.get(i);
            FragmentTransaction ft = obtainFragmentTransaction(idx);
            if (idx == i) {
                ft.show(fragment);
            } else {
                ft.hide(fragment);
            }
            ft.commit();
        }
        currentTabIndex = idx; // 更新目标tab为当前tab
        if (mListener != null)
            mListener.onChanged(currentTabIndex);
    }

    /**
     * 获取一个带动画的FragmentTransaction
     *
     * @param index
     * @return
     */
    private FragmentTransaction obtainFragmentTransaction(int index) {
        FragmentTransaction ft = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
        // 设置切换动画
        if (index > currentTabIndex) {
            ft.setCustomAnimations(leftInAnmi, leftOutAnmi);
        } else {
            ft.setCustomAnimations(rightInAnmi, rightOutAnmi);
        }
        return ft;
    }

    public int getCurrentTabIndex() {
        return currentTabIndex;
    }

    public void setListener(OnTabChangeListener listener) {
        this.mListener = listener;
    }

    public interface OnTabChangeListener {
        public void onChanged(int index);
    }

    public void setSelectedColorId(int selectedColorId) {
        this.selectedColorId = selectedColorId;
    }

    public void setNormalColorId(int normalColorId) {
        this.normalColorId = normalColorId;
    }

    public void setLeftInAnmi(int leftInAnmi) {
        this.leftInAnmi = leftInAnmi;
    }

    public void setLeftOutAnmi(int leftOutAnmi) {
        this.leftOutAnmi = leftOutAnmi;
    }

    public void setRightInAnmi(int rightInAnmi) {
        this.rightInAnmi = rightInAnmi;
    }

    public void setRightOutAnmi(int rightOutAnmi) {
        this.rightOutAnmi = rightOutAnmi;
    }

    public void setSelectorId(int selectedColorId, int normalColorId) {
        this.selectedColorId = selectedColorId;
        this.normalColorId = normalColorId;
    }

    public void setRightAnmi(int rightInAnmi, int rightOutAnmi) {
        this.rightInAnmi = rightInAnmi;
        this.rightOutAnmi = rightOutAnmi;
    }

    public void setLeftAnmi(int leftInAnmi, int leftOutAnmi) {
        this.leftOutAnmi = leftOutAnmi;
        this.leftInAnmi = leftInAnmi;
    }

    public void setAnmi(int leftInAnmi, int leftOutAnmi, int rightInAnmi, int rightOutAnmi) {
        this.leftOutAnmi = leftOutAnmi;
        this.leftInAnmi = leftInAnmi;
        this.rightInAnmi = rightInAnmi;
        this.rightOutAnmi = rightOutAnmi;
    }

}
