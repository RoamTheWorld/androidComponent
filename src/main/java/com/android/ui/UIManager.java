package com.android.ui;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.android.ui.widget.Toast;
import com.android.utils.FileUtil;
import com.android.utils.Log;
import com.android.utils.SoftMap;

/**
 * 界面切换管理类 其功能 类似 ActivityManage 使用前设置参数
 * UIManager.getInstance().setTopContainer(LinearLayout);
 * UIManager.getInstance().setContext(Context); 界面切换直接
 * UIManager.changeView(BaseView.class .class); 返回上一次界面
 * UIManager.getInstance().back(); 具体请查看 api
 * 
 * @author wangyang
 * @2014-2-25 上午10:27:27
 */
public class UIManager {

	private static boolean ISCLEARCACHE = false;// 是否清除数据

	private static LinkedList<Class<? extends BaseView>> HISTORY = new LinkedList<Class<? extends BaseView>>();// 缓存历史记录信息

	private static Map<String, BaseView> VIEWS; // 缓存所有的View

	private static UIManager mInstance = new UIManager();

	private LinearLayout mTopContainer;// 需要填充的帧布局

	private BaseView mCurrentView;// 正在显示的界面

	private Context mContext;// 传递上下文

	private static Set<String> mNoHistory = null;// 不需要缓存历史记录的界面

	private static int mCurrentViewState; // 当前显示界面的id

	/**
	 * 如果内存不足使用软引用
	 */
	static {
		if (FileUtil.hasAcailMemory())
			VIEWS = new HashMap<String, BaseView>();
		else {
			// 使用自定义软引用集合
			VIEWS = new SoftMap<String, BaseView>();
		}
	}

	private UIManager() {
		mNoHistory = new HashSet<String>();
	}

	public static UIManager getInstance() {
		return mInstance;
	}

	public static void mCurrentViewResoume() {
		BaseView baseView = VIEWS.get(HISTORY.getFirst().getSimpleName());
		baseView.onResume();
	}

	/**
	 * 界面 切换 类似 startActivity();
	 * 
	 * @author xzj
	 * @2014-2-25 上午9:56:51
	 * @param targetViewClazz
	 *            需要跳转到的界面class
	 * @param bundle
	 *            需要传递的参数 bundle
	 * @return
	 */
	public static boolean changeView(Class<? extends BaseView> targetViewClazz,
			Bundle bundle) {
		return UIManager.getInstance().change(targetViewClazz, bundle);
	}

	/**
	 * 获取当前显示的界面的id
	 * 
	 * @author xzj
	 * @2014-2-25 上午9:51:14
	 * @return
	 */
	public static int getmCurrentViewState() {
		return mCurrentViewState;
	}

	/**
	 * 单个设置 不需要添加到 缓存记录的view名字
	 * 
	 * @author xzj
	 * @2014-2-25 上午9:55:34
	 * @param viewName
	 */
	public static void setNoHistoryView(String viewName) {
		mNoHistory.add("viewName");
	}

	/**
	 * 
	 * @author wangyang
	 * @2013-11-14 上午10:42:26
	 * @param targetViewClazz
	 * @param bundle
	 * @return 界面的切换,根据View的Class与传递的Bundle来切换界面View
	 */
	public boolean change(Class<? extends BaseView> targetViewClazz,
			Bundle bundle) {
		// 判断如果当前界面重复点击则不切换界面
		if (mCurrentView != null && mCurrentView.getClass() == targetViewClazz)
			return false;

		// 判断缓存集合里是否有该view对象,有的话直接取之,否则创建
		String key = targetViewClazz.getSimpleName();
		BaseView targetView = null;
		if (VIEWS.containsKey(key)) {
			targetView = VIEWS.get(key);
			targetView.setBundle(bundle);
		} else {
			try {
				Constructor<? extends BaseView> constructor = targetViewClazz
						.getConstructor(Context.class, Bundle.class);
				if (bundle == null)
					bundle = new Bundle();
				targetView = constructor.newInstance(getContext(), bundle);
				VIEWS.put(key, targetView);
			} catch (Exception e) {
				Log.w(" init " + key + " error ");
			}
		}

		addHistory(targetView);

		// 清空上层帧布局的View组件
		mTopContainer.removeAllViews();

		// 调用当前界面的onPanse方法,并切换当前界面为目标界面并调用onResume方法
		if (mCurrentView != null)
			mCurrentView.onStop();
		mCurrentView = targetView;
		// mCurrentView.hideListViews();
		mCurrentView.onStart();
		// HiptvActivity.mCurrentViewState = mCurrentView.getId();

		// 获取中间布局对象,并添加至中间布局对象
		View view = mCurrentView.getView();
		mTopContainer.addView(view);
		return true;
	}

	/**
	 * 
	 * @author wangyang
	 * @2013-12-4 下午3:08:35
	 * @param targetView
	 *            添加操作到历史记录,某些
	 */
	private void addHistory(BaseView targetView) {
		String name = targetView.getClass().getSimpleName();
		if (mNoHistory.contains(name))
			return;
		// 添加历史记录
		HISTORY.addFirst(targetView.getClass());
	}

	/**
	 * 
	 * @author wangyang
	 * @2013-11-18 下午12:03:07
	 * @return 根据历史记录,返回上一个界面
	 */
	public boolean back() {
		// 获取得到历史记录最前面的key,如果不为空,则获取到集合中的View
		Class<?> key = null;
		if (HISTORY.size() > 1) {
			key = HISTORY.getFirst();
			if (key == null)
				return false;
			BaseView targetView = VIEWS.get(key.getSimpleName());
			if (targetView == null) {
				// 提示用户：应用在低内存下运行
				Toast.show(mContext, "应用在低内存下运行");
				// 界面跳转：首页 changeView(BaseView.class, null);
				HISTORY.clear();
			} else {
				// 判断当前view与得到的view是否为同一个,如果是则移除历史记录(长度为1时候除外)
				if (targetView.getClass() == mCurrentView.getClass()) {
					if (HISTORY.size() == 1)
						return false;
					HISTORY.removeFirst();
					// 迭代调用此方法
					return back();
				} else {
					// 切换界面
					mTopContainer.removeAllViews();
					mCurrentView.onStop();
					mCurrentView = targetView;

					// 变更状态
					mCurrentViewState = mCurrentView.getId();

					// 启动界面
					mCurrentView.onResume();
					mTopContainer.addView(targetView.getView());
					return true;
				}
			}
			return false;
		} else {
			// 跳转至首页 change(LiveView.class,null);
			return true;
		}
	}

	/**
	 *   
	 * @author wangyang
	 * @2013-12-9 下午4:12:43
	 * @param viewName
	 *            根据名称清除VIEW
	 */
	public void remove(String viewName) {
		VIEWS.remove(viewName);
	}

	/**
	 * @author huboyang
	 * @2013-11-27 上午11:23:31
	 * @param 清除Map中所有的View
	 */
	public void resetAllView() {
		VIEWS.clear();
		HISTORY.clear();
	}

	/**
	 * 
	 * @author wangyang
	 * @2013-11-18 下午2:40:41
	 * @return 获取历史记录的上一个界面的Class
	 */
	public Class<? extends BaseView> getPreHistoryKey() {
		if (HISTORY != null && HISTORY.size() > 1)
			return HISTORY.get(1);
		return null;
	}

	/**
	 * 
	 * @author wangyang
	 * @2013-11-14 上午11:04:31
	 * @param mTopContainer
	 *            赋值帧布局
	 */
	public void setTopContainer(LinearLayout mTopContainer) {
		this.mTopContainer = mTopContainer;
	}

	/**
	 * @author wangyang
	 * @2013-11-14 上午10:37:35
	 * @return 获取Context
	 */
	private Context getContext() {
		return mContext;
	}

	// 设置Context
	public void setContext(Context mContext) {
		this.mContext = mContext;
	}

	/**
	 * 
	 * @author wangyang
	 * @2013-11-14 上午11:04:14
	 * @return 获取当前正在显示的View
	 */
	public BaseView getCurrentView() {
		return mCurrentView;
	}

	/**
	 * 当前是否设置清除缓存
	 * 
	 * @author xzj
	 * @2014-2-25 上午10:09:29
	 * @return
	 */
	public static boolean isISCLEARCACHE() {
		return ISCLEARCACHE;
	}

	/**
	 * 设置是否清除缓存
	 * 
	 * @author xzj
	 * @2014-2-25 上午10:09:10
	 * @param iSCLEARCACHE
	 */
	public static void setISCLEARCACHE(boolean iSCLEARCACHE) {
		ISCLEARCACHE = iSCLEARCACHE;
	}

	/**
	 * 获取不需要缓存的 类名集合
	 * 
	 * @author xzj
	 * @2014-2-25 上午10:08:37
	 * @return
	 */
	public Set<String> getmNoHistory() {
		return mNoHistory;
	}

	/**
	 * 集合设置 不需要添加到 缓存的 界面 类名
	 * 
	 * @author xzj
	 * @2014-2-25 上午10:06:38
	 * @param mNoHistory
	 */
	public void setmNoHistory(Set<String> mNoHistory) {
		UIManager.mNoHistory = mNoHistory;
	}

	/**
	 * 获取历史缓存记录集合
	 * 
	 * @author xzj
	 * @2014-2-25 上午10:08:10
	 * @return
	 */
	public static LinkedList<Class<? extends BaseView>> getHISTORY() {
		return HISTORY;
	}
}
