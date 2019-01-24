package com.android.utils;

import com.android.master.BuildConfig;

/**
 * 
 * 常用全局变量类 
 * 
 * @author wangyang
 * @version 1.0.0
 * @create 2014-2-20
 */
public class Constants {
	/***************************************************** 缓存模块 ***************************************************/
	/**
	 * SharedPreferences缓存名
	 */
	public static String FILE_CACHE_NAME = "config";

	/**
	 * 文件缓存名
	 */
	public static String FILE_CACHE_DIRECTORY = "cache";
	/**
	 * 项目缓存根目录保存名
	 */
	public static String FILE_ROOT_DIRECTORY = "rootDir";
	/**
	 * image缓存目录
	 */
	public static String FILE_IMAGE_DIRECTORY = "image";
	/**
	 * audio缓存目录
	 */
	public static String FILE_AUDIO_DIRECTORY = "audio";
	/**
	 * video缓存目录
	 */
	public static String FILE_VIDEO_DIRECTORY = "video";
	/**
	 * webview缓存目录
	 */
	public static String FILE_WEBVIEW = "webview";
	
	/**
	 * 应用名称的key
	 */
	public static final String APP_NAME = "appName";

	/**
	 * 应用名称的key
	 */
	public static String DB_NAME = "dbName";

	/**
	 * 数据库版本Key
	 */
	public static int DB_VERSION = 1;

	/***************************************************************************************************************/

	/***************************************************** 用户/联系人 ****************************************************/
	/**
	 * 当前用户名
	 */
	public static String CURRENT_USER_ID = "userId";

	/**
	 * 当前用户登录名
	 */
	public static String CURRENT_USER_LOGIN_NAME = "loginname";

	/**
	 * 当前用户的UID 用于XMPP聊天
	 */
	public static String CURRENT_USER_UID = "useruid";

	/**
	 * 当前用户密码
	 */
	public static String CURRENT_USER_PASSWORD = "password";
	/**
	 * 当前用户(也用于详情界面)
	 */
	public static String CURRENT_USER_ = "user";
	/**
	 * 自动登陆
	 */
	public static final String AUTO_LOGIN = "autoLogin";

	public static String IS_NOTIFICATION = "isNotification";
	/**
	 * 是否登录
	 */
	public static boolean IS_LOGIN = false;
	/**
	 * 是否是第一次安装
	 */
	public static boolean IS_FIRST_INSTALL = true;
	/**
	 * 是否选择联系人key
	 */
	public static String SELECT_MODE = "selectMode";
	/**
	 * 是否使用缓存的的key
	 */
	public static String IS_USE_CACHE = "isUseCache";
	/**
	 * 选择联系人的key
	 */
	public static String CHOICE_USER = "choiceUsers";
	/**
	 * 所有联系人的key
	 */
	public static String ALL_USER = "allUsers";
	/**
	 * 
	 */
	public static String IS_OTHER_ACTIVITY = "isOtherActivity";
	/**
	 * 
	 */
	public static String OS_TYPE = "osType";
	
	public static String ENCRYPT_KEY = "3b38e11ffd65698aedeb5ffc";
	
	public static String HEADER_ENCRYPT_KEY = "encryptValue";
	
	/***************************************************** webview模块 ***************************************************/
	
	public static final String WEBVIEW_URL = "url";
	
	public static final String JS_OBJECT_ANDROID = "android";
	
	public static final String JS_OBJECT_NAME = "name";
	
	/*********************************************************************************************************************/

	/*********************************************************************************************************************/

	/***************************************************** 网络模块 ***************************************************/
	public static String CONNECTED = "网络已连接";

	public static String NO_CONNECTED = "网络连接断开";

	public final static String RESULT_KEY_STATE = BuildConfig.RESULT_KEY_STATE;

	public final static String RESULT_KEY_ERROR_MESSAGE = BuildConfig.RESULT_KEY_ERROR_MESSAGE;

	public final static String RESULT_KEY_BODY = BuildConfig.RESULT_KEY_BODY;

	public static String RESULT_STATE_SUCCESS = BuildConfig.RESULT_SUCCESS;

	public static String RESULT_STATE_FAILURE = BuildConfig.RESULT_FAIL;

	public static final String HTTP_SERVER_IP = BuildConfig.SERVER_IP;

	public static final int NET_TIME_OUT = 30 * 1000;
	/***************************************************************************************************************/

	/***************************************************** 多线程下载 ***************************************************/

	public static final int TIME_OUT = 10 * 1000;

	public static final int THREAD_COUNT = 3;

	public static final int THREAD_BUFFER_SIZE = 1024;

	public static final int REFRESH_PROGRESS_DELAY = 1000;

	public static final String IS_DOWN = "isDonw";

	/***************************************************************************************************************/

	/****************************** volley 一次性缓存文件大小设置 ************************************************/
	public static final int VOLLEYCACHESIZE = 5 * 1024 * 1024;

	/***************************************************** 智能加载List ***************************************************/
	public static String URL = "url";

	public static String PAGE_INDEX = "pageNumber";

	public static String PAGE_SIZE = "pageSize";

	public static int PAGEINDEX = 1;

	public static int PAGESIZE = 15;

	public static int AUTO_LOAD_MORE_AT_POSITION = 1;

	public static String SELECTOR = "selector";
	
	public static String ORDER_COLUMN = "orderColumn";

	public static String DESC = "desc";

	public static String ERROR = "加载失败";

	public final static String REFRESH_NET_RESPONSE_KEY = "response";

	/***************************************************************************************************************/

	/***************************************************** CrashHandler ***************************************************/

	public static String UP_LOAD_LOG_NAME = "file";

	/***************************************************************************************************************/

	/***************************************************** NetWorkImageView ***************************************************/

	public static String SPLIT_REGULAR= ",";



	/***************************************************************************************************************/
	public class Action {
		/**
		 * 网络状态改变
		 */
		public static final String NET_WORK_CHANGE = "com.up72.androidComponent.netWorkChange";

		/**
		 * 登陆界面
		 */
		public static final String LOGIN = "com.up72.login";

		/**
		 * 联系人详情界面
		 */
		public static final String CONTACT_DETAIL = "com.up72.contactDetail";

		/**
		 * 首页
		 */
		public static final String HOME_PAGE = ".home";
		
		/**
		 * 基础广播接收者注册的action
		 */
		public static final String BASE_RECEIVER_ACTION = "com.up72.wikiword";
		
		/**发送广播的第一个参数,用于携带what*/
		public static final String BROADCAST_FIRST_DATA = "what";
		/**发送广播的第二个参数,用于声明是否携带了其他数据,以及额外参数的类型*/
		public static final String BROADCAST_SECODE_DATA = "hasOtherData";
		/**发送广播的第三个参数，用来携带额外数据*/
		public static final String BROADCAST_THIRD_DATA = "extraData";
	}

	public class What {
		public static final int NET_WORK_CHANGED = 0;
		
		
		public static final int DEFAULT_VALUE = -1;
	}

	/**
	 * 设置消息状态回执
	 * 
	 * 0.发送中 正在发送消息 1.已发送 、消息服务器尚未断开，并且消息已经发送出去 2.已送达 :目标客户端收到消息、
	 * 3.已阅读:用户查看信息以后进行状态返回、 4.失败、由于网络原因,发送失败 5.已删除 界面删除了消息,本地数据库依然缓存
	 * 
	 * */
	public static final String SENDMESSAGE = "对方已接收";
	public static final String READMESSAGE = "对方已阅读";
}
