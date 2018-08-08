package com.upload.helper;

import com.web.entities.UploadFile;
import com.upload.util.PropertiesUtil;

public class SystemValue {
	
	public static final int YES = 1;
	public static final int NO = 0;
	
	//测试模式
	public static boolean DEBUG_MODE;
	//资源路径
	public static String RESOURCE_PATH;
	//文件夹名称后缀
	public static String THUMB;
	//目录类型
	public static final int DIR_TYPE = UploadFile.DirType.VIRTUAL_PATH;
	//文件服务器地址
	//public static final String FILE_SERVER_ADDRESS = "https://xqn.njlime.com/upload/";

	public static String getProperties(String name){
		return PropertiesUtil.readValue("/property.properties",name);
	}
}