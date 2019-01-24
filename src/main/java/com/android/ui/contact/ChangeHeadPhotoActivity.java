package com.android.ui.contact;

import com.android.utils.Constants;

/**
 * 
 * 
 * 描述 :选择图片
 * 
 * @author wangyang
 * @version 1.0.0
 * @create 2013-8-2
 */
public class ChangeHeadPhotoActivity extends TakePhotoActivity {

	@Override
	protected String getImageFileDir() {
		return Constants.FILE_ROOT_DIRECTORY + "/headImage";
	}

}
