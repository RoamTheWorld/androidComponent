package com.android.utils.version;

/**
 * 版本更新
 * 
 * @description
 * @author wangyang
 * @create 2014-11-20 下午6:21:32
 * @version 1.0.0
 */
public class Version {
	public static int ANDROID = 0;
	public static int IOS = 1;
	private String id;
	private int version;
	private String versionName;
	private String appUrl;
	private String updateInfo;
	private long createTime;
	private int osType;
	private int isMustUpdate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getAppUrl() {
		return appUrl;
	}

	public void setAppUrl(String appUrl) {
		this.appUrl = appUrl;
	}

	public String getUpdateInfo() {
		return updateInfo;
	}

	public void setUpdateInfo(String updateInfo) {
		this.updateInfo = updateInfo;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int getOsType() {
		return osType;
	}

	public void setOsType(int osType) {
		this.osType = osType;
	}

	public boolean isMustUpdate() {
		return isMustUpdate == 1 ? true : false;
	}

	public void setIsMustUpdate(int isMustUpdate) {
		this.isMustUpdate = isMustUpdate;
	}

}
