package com.android.sqlite.simple.example;

import java.io.Serializable;

import android.graphics.Bitmap;

import com.android.sqlite.simple.annotation.Coloumn;
import com.android.sqlite.simple.annotation.ID;
import com.android.sqlite.simple.annotation.TableName;

/**
 * @description 聊天消息
 * 
 * @author wangyang
 * @create 2014-2-18 下午02:00:32
 * @version 1.0.0
 */
@TableName(DataBase.Table.CHAT_MESSAGE)
public class ChatMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** =======================聊天类型type======================= */
	/**
	 * 私聊
	 * */
	public static final int PRIVATE_CHAT = 0;
	/**
	 * 群聊
	 * */
	public static final int GROUP_CHAT = 1;

	/** =====================消息类型content_type========================== */
	public static final int TEXT = 0;
	/**
	 * content_type，表情
	 * */
	public static final int EXPRESSION = 1;
	/**
	 * content_type，图片
	 * */
	public static final int IMAGE = 2;
	/**
	 * content_type，音频
	 * */
	public static final int AUDIO = 3;
	/**
	 * content_type，视频
	 * */
	public static final int VIDEO = 4;
	/** =======================消息状态========================= */
	/**
	 * receipt,默认无任何状态
	 * */
	public static final int DEFAULT = 0;
	/**
	 * receipt,正在发送
	 * */
	public static final int SENDING = 1;
	/**
	 * receipt,发送成功
	 * */
	public static final int SUCCESS = 2;
	/**
	 * receipt,发送失败
	 * */
	public static final int FAILURE = -1;

	/** ========================================================== */
	/**
	 * 消息ID，区分每一条消息
	 */
	@ID
	@Coloumn
	private String messageId;
	/**
	 * 消息所属：PRIVATE_CHAT.个人 GROUP_CHAT.群组
	 */
	@Coloumn
	private Integer chat_type;
	/**
	 * 消息类型：文本、图片、音频
	 */
	@Coloumn
	private Integer content_type;
	/**
	 * 消息状态:DEFAULT,SENDING,SUCCESS,FAILURE
	 */
	@Coloumn
	private Integer receipt;
	/**
	 * 消息内容 文本为内容 表情为表情图片名,如ex30000 图片为图片网络地址,包括压缩图与原图,以"|"分隔。 音频为音频下载地址
	 * 视频为视频下载地址
	 * 
	 */
	@Coloumn
	private String content;
	/**
	 * 消息时间,以服务器时间为准
	 */
	@Coloumn
	private Long time;
	/**
	 * 消息发送人ID
	 */
	@Coloumn
	private String fromId;
	/**
	 * 消息接收人ID
	 */
	@Coloumn
	private String toId;
	/**
	 * 视频缩略图缓存
	 */
	private Bitmap videoBitmap;

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public int getChat_type() {
		return chat_type;
	}

	public void setChat_type(int chat_type) {
		this.chat_type = chat_type;
	}

	public int getContent_type() {
		return content_type;
	}

	public void setContent_type(int content_type) {
		this.content_type = content_type;
	}

	public int getReceipt() {
		return receipt;
	}

	public void setReceipt(int receipt) {
		this.receipt = receipt;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getTime() {
		return time == null ? 0 : time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getFromId() {
		return fromId;
	}

	public void setFromId(String fromId) {
		this.fromId = fromId;
	}

	public String getToId() {
		return toId;
	}

	public void setToId(String toId) {
		this.toId = toId;
	}

	public void setVideoBitmap(Bitmap videoBitmap) {
		this.videoBitmap = videoBitmap;
	}

	public Bitmap getVideoBitmap() {
		return videoBitmap;
	}
}
