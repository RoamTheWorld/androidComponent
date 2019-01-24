package com.android.sqlite.simple.example;

/**
 * @description 数据库定义常量类。此类在应用项目中创建，定义表名及数据库创建语句。
 * 
 * @author wangyang
 * @create 2014-2-21 上午11:40:27
 * @version 1.0.0
 */
public class DataBase {
	/**
	 * 数据库版本
	 */
	public static final int VERSION = 1;
	/**
	 * 数据库名称
	 */
	public static final String DBNAME = "component.db";

	/** =================================== 数据库表========================== */
	public interface Table {
		/**
		 * 聊天消息
		 */
		public static final String CHAT_MESSAGE = "chat_message";
	}

	/** ===================================创建数据表sql========================= */
	public static final String[] mSql_ddl = { "CREATE TABLE "
			+ DataBase.Table.CHAT_MESSAGE
			+ "(messageId TEXT PRIMARY KEY,chat_type INTEGER,"
			+ "content_type INTEGER,receipt INTEGER,content TEXT,time LONG,"
			+ "fromId TEXT,toId TEXT);" };
}
