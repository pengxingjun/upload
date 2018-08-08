package com.elfinder.impl;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.elfinder.localfs.LocalFsVolume;
import com.elfinder.localfs.MysqlFsVolume;
import com.elfinder.services.FsService;
import com.elfinder.services.FsServiceFactory;
import com.upload.helper.SystemValue;
import com.web.entities.UploadFile;
import com.web.util.UploadFileCacheUtil;

/**
 * A StaticFsServiceFactory always returns one FsService, despite of whatever it
 * is requested
 * 
 * @author bluejoe
 * 
 */
public class StaticFsServiceFactory implements FsServiceFactory {
	FsService _fsService;

	@Override
	public FsService getFileService(HttpServletRequest request, ServletContext servletContext) {
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		DefaultFsService fsService = new DefaultFsService();

        File file = null;
        if(Boolean.parseBoolean(SystemValue.getProperties("isUseDatabase"))){
			//从数据库读取文件
			UploadFile rootFile = UploadFileCacheUtil.getUserRoot(userId);
            MysqlFsVolume fsVolume = new MysqlFsVolume();
			fsVolume.setName(rootFile.getName());
            fsVolume.setRootDir(rootFile);
            fsService.addVolume("File_" + userId, fsVolume);
		}else{
            LocalFsVolume fsVolume = new LocalFsVolume();
			fsVolume.setName("文件");
            file =  new File(SystemValue.RESOURCE_PATH + File.separator + userId);
            if(!file.exists()){
                file.mkdirs();
            }
            fsVolume.setRootDir(file);
            fsService.addVolume("File_" + userId, fsVolume);
		}
		fsService.setSecurityChecker(_fsService.getSecurityChecker());
		fsService.setServiceConfig(_fsService.getServiceConfig());
		return fsService;
	}

	public FsService getFsService() {
		return _fsService;
	}

	public void setFsService(FsService fsService) {
		_fsService = fsService;
	}
}
