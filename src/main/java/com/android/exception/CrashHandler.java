package com.android.exception;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.android.utils.Constants;
import com.android.utils.DateUtil;
import com.android.utils.FileUtil;
import com.android.utils.StringUtil;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理,写入日志文件,上传服务器等操作
 * 
 * @author wangyang 2014-11-19 下午6:39:19
 */
public class CrashHandler implements UncaughtExceptionHandler {

	public static final String TAG = "CrashHandler";

	/**
	 * 系统默认的UncaughtException处理类
	 */
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	/**
	 * CrashHandler实例
	 */
	private static CrashHandler INSTANCE;
	/**
	 * 程序的Context对象
	 */
	private Context mContext;
	/**
	 * 是否需要重启
	 */
	private boolean isRestart;
	/**
	 * 用来存储设备信息和异常信息
	 */
	private Map<String, String> infos = new HashMap<>();
	/**
	 * 日志文件本地保存路径,服务器上传路径
	 */
	private String saveDir, uploadUrl;
	/**
	 * 当前错误线程
	 */
	private Thread thread;
	/**
	 * 当前错误
	 */
	private Throwable throwable;

	/** 保证只有一个CrashHandler实例 */
	private CrashHandler(Context context, String saveDir, String uploadUrl) {
		mContext = context;

		this.saveDir = saveDir;
		this.uploadUrl = uploadUrl;

		if (StringUtil.isEmpty(saveDir))
			this.saveDir = Constants.FILE_ROOT_DIRECTORY + "/error";

	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance(Context context, String saveDir, String uploadUrl) {
		if (INSTANCE == null) {
			synchronized (CrashHandler.class) {
				if (INSTANCE == null)
					INSTANCE = new CrashHandler(context, saveDir, uploadUrl);
			}
		}
		return INSTANCE;
	}

	public static CrashHandler getInstance(Context context, String saveDir) {
		return getInstance(context, saveDir, null);
	}

	public static CrashHandler getInstance(Context context) {
		return getInstance(context, null, null);
	}

	public void init() {
		init(false);
	}
	
	public void init(boolean isRestart) {
		// 获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
		
		this.isRestart = isRestart;
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		ex.printStackTrace();
		this.thread = thread;
		this.throwable = ex;
		final String savePath = handleException(ex);
		// 如果本地日志文件保存成功,并且上传服务器路径有效就发送服务器日志文件,否则直接重启应用
		if (!StringUtil.isEmpty(savePath)) {
			if (!StringUtil.isEmpty(uploadUrl)) {
//				RxHttpClient.instance.postFile(uploadUrl,new File(savePath),String.class)
//						.subscribeOn(Schedulers.io())
//						.subscribe(new BaseSubscriber<String>() {
//							@Override
//							public void onNext(String result) {
//								restartApp();
//							}
//
//							@Override
//							public void commonHandleException(ResultErrorException resultErrorException) {
//								restartApp();
//							}
//						});
			} else {
				//restartApp();
			}
		} else {
			deafultUncaughtException(thread, ex);
		}
	}

	private void deafultUncaughtException(Thread thread, Throwable ex) {
		// 本地日志保存失败,并且默认Handler不为空,则使用默认处理
		if (mDefaultHandler != null)
			mDefaultHandler.uncaughtException(thread, ex);
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private String handleException(Throwable ex) {
		if (ex == null) {
			return null;
		}
		ex.printStackTrace();
		// 收集设备参数信息
		collectDeviceInfo(mContext);

		// 保存日志文件
		return saveCrashInfo2File(ex);
	}

	/**
	 * 收集设备参数信息
	 * 
	 * @param ctx
	 */
	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null" : pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "an error occured when collect package info", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				Log.d(TAG, field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				Log.e(TAG, "an error occured when collect crash info", e);
			}
		}
	}

	/**
	 * 保存错误信息到文件中
	 * 
	 * @param ex
	 * @return 返回文件名称,便于将文件传送到服务器
	 */
	private String saveCrashInfo2File(Throwable ex) {

		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		try {
			String time = DateUtil.getDateTime("yyyy-MM-dd-HH-mm-ss");
			String fileName = "crash-" + time + ".log";
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				String path = FileUtil.getFileDir(mContext, saveDir).getAbsolutePath();
				fileName = path + "/" + fileName;
				FileOutputStream fos = new FileOutputStream(fileName);
				fos.write(sb.toString().getBytes());
				fos.close();
			}
			return fileName;
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing file...", e);
		}
		return null;
	}

	/**
	 * 重启App
	 * 
	 * @author wangyang 2014-11-19 下午6:42:04
	 */
	@SuppressLint("InlinedApi")
	private void restartApp() {
		if(!isRestart)
			return;
		
		SharedPreferences sp = mContext.getSharedPreferences(Constants.FILE_CACHE_NAME, Context.MODE_PRIVATE);
		int count = sp.getInt("restartCount", 0);
		long lastReStartTime = sp.getLong("lastReStartTime", System.currentTimeMillis());
		boolean isReStart = true;
		if (count <= 2 ) {
			sp.edit().putInt("restartCount", ++count).commit();
		} else if(count>2 && (System.currentTimeMillis() - lastReStartTime) > 10000){
			isReStart = false;
			sp.edit().putInt("restartCount", 0).commit();
		}else{
			isReStart = false;
			sp.edit().putInt("restartCount", 0).commit();
		}
		
		
		
		sp.edit().putLong("lastReStartTime", System.currentTimeMillis()).commit();

		if (!isReStart) {
			deafultUncaughtException(thread, throwable);
			return;
		}

		new Thread() {
			public void run() {
				Looper.prepare();
				com.android.ui.widget.Toast.show(mContext, "程序错误", Toast.LENGTH_LONG);
				Looper.loop();
			};
		}.start();
		// 启动app的入口界面,并清空任务栈

		Intent i = new Intent();
		i.setAction(mContext.getPackageName() + Constants.Action.HOME_PAGE);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(i);

		// 延迟3秒,确保Toast正常显示
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		android.os.Process.killProcess(android.os.Process.myPid());
	}
}
